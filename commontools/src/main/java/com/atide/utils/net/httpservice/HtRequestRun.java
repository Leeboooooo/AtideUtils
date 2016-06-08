package com.atide.utils.net.httpservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lee on 14-4-16.
 */
public class HtRequestRun implements Runnable {
    private static final String TAG = "HtRequestRun";
    private HttpRequest mRequestData;
    private Handler mRevedHander;
    private String mId;
    private boolean mStopRunning;
    private boolean dotNetFlag = false;

    public HtRequestRun(HttpRequest adpater, Handler revedHandler, boolean dotNetFlag) {
        mRequestData = adpater;
        mRevedHander = revedHandler;
        mId = UUID.randomUUID().toString();
        this.dotNetFlag = dotNetFlag;
    }


    @Override
    public void run() {
        if (mRequestData == null || mStopRunning) {
            return;
        }
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("params",mRequestData.getParams()));
        //对参数编码
        String param = URLEncodedUtils.format(params, "UTF-8");
        //baseUrl
//        String baseUrl = "http://172.16.3.135:8080/tcmsQuery/compPayBizService/login";
        String baseUrl=mRequestData.getHost()+mRequestData.getUrl()+mRequestData.getMethodName();
        //将URL与参数拼接
        HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(getMethod); //发起GET请求
            Object  kk = EntityUtils.toString(response.getEntity(), "utf-8");
            String responseData = kk.toString();
            sendMessage(200, "网络请求成功", responseData);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求错误io-"+e.getMessage());
            sendMessage(201, "网络请求失败", null);
        }  catch (NullPointerException e){
            e.printStackTrace();
            Log.e(TAG, "网络请求错误null-"+e.getMessage());
            sendMessage(200, "返回数据为空", null);
        }
    }

    private void sendMessage(int code, String successMsg, String data) {
        HtResponseMessage wsMsg = new HtResponseMessage();
        wsMsg.mCode = code;
        wsMsg.mMsg = successMsg;
        wsMsg.mData = data;

        Message hadnelMsg = mRevedHander.obtainMessage();
        Bundle bund = new Bundle();
        bund.putSerializable("adpater", mRequestData);
        bund.putSerializable("responseMsg", wsMsg);
        hadnelMsg.setData(bund);
        hadnelMsg.sendToTarget();
    }

    public String getId() {
        return mId;
    }
}
