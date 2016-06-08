package com.atide.utils.net.httpservice;

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
public class HtPoolManager {
    private ReentrantLock mLock;
    private BlockingQueue<Runnable> mBlockingQueue;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private HashMap<String, HtRequestRun> mTaskQueue;

    private static HtPoolManager mManager;

    public static HtPoolManager getInstance() {
        if (mManager == null) {
            synchronized (HtPoolManager.class) {
                if (mManager == null) {
                    mManager = new HtPoolManager();
                    mManager.init();
                }
            }
        }
        return mManager;
    }

    private HtPoolManager() {
    }

    private void init() {
        mLock = new ReentrantLock();
        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 2;
        mLock = new ReentrantLock();
        mBlockingQueue = new LinkedBlockingQueue<Runnable>();
        mThreadPoolExecutor = new HttpThreadPoolExecutor(threadPoolSize, threadPoolSize * 2, 30, TimeUnit.SECONDS, mBlockingQueue);
        mTaskQueue = new HashMap<String, HtRequestRun>();
    }

    private void execute(HttpRequest adpater, boolean flag) {

        //创建runnable任务让线程池去执行
        HtRequestRun task = new HtRequestRun(adpater, revedHander, flag);
        adpater.setId(task.getId());
        mThreadPoolExecutor.execute(task);
        mTaskQueue.put(adpater.getId(), task);
    }

    protected boolean sendRequest(HttpRequest adpater, boolean flag) {
        execute(adpater, flag);
        return true;
    }

    private Handler revedHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            HtResponseMessage responseMessage = (HtResponseMessage) bundle.getSerializable("responseMsg");
            HttpRequest adpater = (HttpRequest) bundle.getSerializable("adpater");
            responseMessage.mTag = adpater.getTag();
            if (adpater != null) {
                adpater.onResponse(responseMessage);
            }
        }
    };

    public class HttpThreadPoolExecutor extends ThreadPoolExecutor {
        public HttpThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                      long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        public void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
        }

        @Override
        public void afterExecute(Runnable r, Throwable t) {

            mLock.lock();

            HtRequestRun task = (HtRequestRun) r;
            String id = task.getId();
            mTaskQueue.remove(id);
            mLock.unlock();
            super.afterExecute(r, t);
        }
    }

}
