package com.atide.utils.net.webservice;

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
 * Created by Lee on 14-4-16.
 */
public class WsPoolManager {
    private ReentrantLock mLock;
    private BlockingQueue<Runnable> mBlockingQueue;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private HashMap<String,WsRequestRun> mTaskQueue;

    private static WsPoolManager mManager;

    public static WsPoolManager getInstance()
    {
        if (mManager == null)
        {
            synchronized (WsPoolManager.class)
            {
                if (mManager == null)
                {
                    mManager = new WsPoolManager();
                    mManager.init();
                }
            }
        }
        return mManager;
    }

    private WsPoolManager()
    {
    }

    private void init()
    {
        mLock = new ReentrantLock();
        int threadPoolSize = Runtime.getRuntime().availableProcessors()*2+2;
        mLock = new ReentrantLock();
        mBlockingQueue = new LinkedBlockingQueue<Runnable>();
        mThreadPoolExecutor = new HttpThreadPoolExecutor(threadPoolSize,threadPoolSize*2,30, TimeUnit.SECONDS,mBlockingQueue);
        mTaskQueue = new HashMap<String, WsRequestRun>();
    }

    private void execute(WsRequest adpater,boolean flag)
    {
        WsRequestRun task = new WsRequestRun(adpater,revedHander,flag);
        adpater.setId(task.getId());
        mThreadPoolExecutor.execute(task);
        mTaskQueue.put(adpater.getId(),task);
    }

    protected boolean sendRequest(WsRequest adpater,boolean flag)
    {
        execute(adpater,flag);
        return true;
    }

    private Handler revedHander = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            WsResponseMessage responseMessage = (WsResponseMessage)bundle.getSerializable("responseMsg");
            WsRequest adpater = (WsRequest)bundle.getSerializable("adpater");
            responseMessage.mTag = adpater.getTag();
            if(adpater != null)
            {
                adpater.onResponse(responseMessage);
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

            WsRequestRun task = (WsRequestRun)r;
            String id = task.getId();
            mTaskQueue.remove(id);
            mLock.unlock();
            super.afterExecute(r, t);
        }
    }

}
