package com.atide.utils.net.fileservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Lee on 14-4-17.
 */
public class FilePoolManager
{
    private ReentrantLock mLock;
    private BlockingQueue<Runnable> mBlockingQueue;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private HashMap<String,FileRequestRun> mTaskQueue;

    private static FilePoolManager mManager;

    public static FilePoolManager getInstance()
    {
        if (mManager == null)
        {
            synchronized (FilePoolManager.class)
            {
                if (mManager == null)
                {
                    mManager = new FilePoolManager();
                    mManager.init();
                }
            }
        }
        return mManager;
    }

    private FilePoolManager()
    {
    }

    private void init()
    {
        mLock = new ReentrantLock();
        int threadPoolSize = Runtime.getRuntime().availableProcessors()*2+2;
        mLock = new ReentrantLock();
        mBlockingQueue = new LinkedBlockingQueue<Runnable>();
        mThreadPoolExecutor = new HttpThreadPoolExecutor(threadPoolSize,threadPoolSize*2,30, TimeUnit.SECONDS,mBlockingQueue);
        mTaskQueue = new HashMap<String, FileRequestRun>();
    }

    private void execute(FileDownRequest adpater)
    {
        FileRequestRun task = new FileRequestRun(adpater,revedHander);
        adpater.setId(task.getId());
        mThreadPoolExecutor.execute(task);
        mTaskQueue.put(adpater.getId(),task);
    }

    protected boolean sendRequest(FileDownRequest adpater)
    {
        execute(adpater);
        return true;
    }

    private Handler revedHander = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            FileResponseMessage responseMessage = (FileResponseMessage)bundle.getSerializable("responseMsg");
            FileDownRequest adpater = (FileDownRequest)bundle.getSerializable("adpater");
            if(adpater != null && responseMessage != null)
            {
                responseMessage.mTag = adpater.getFileId();
                if(responseMessage.mDownDone)
                {
                    adpater.onResponse(responseMessage);
                }
                else
                {
                    adpater.progress(responseMessage);
                }

            }

        }
    };

    public class HttpThreadPoolExecutor extends ThreadPoolExecutor
    {
        public HttpThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                      long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue)
        {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
        @Override
        public void beforeExecute(Thread t, Runnable r)
        {
            super.beforeExecute(t, r);
        }

        @Override
        public void afterExecute(Runnable r, Throwable t)
        {

            mLock.lock();

            FileRequestRun task = (FileRequestRun)r;
            String id = task.getId();
            mTaskQueue.remove(id);
            mLock.unlock();
            super.afterExecute(r, t);
        }
    }
}
