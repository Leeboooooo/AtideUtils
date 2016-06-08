package com.atide.utils.net.webservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.repackage.ksoap2.SoapEnvelope;
import org.repackage.ksoap2.serialization.SoapObject;
import org.repackage.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Lee on 14-4-16.
 */
public class WsRequestRun implements Runnable {
    private static final String TAG = "WsRequestRun";
    private WsRequest mRequestData;
    private Handler mRevedHander;
    private String mId;
    private boolean mStopRunning;
    private boolean dotNetFlag = false;

    public WsRequestRun(WsRequest adpater, Handler revedHandler, boolean dotNetFlag) {
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
        SoapObject rpc = new SoapObject(mRequestData.getNameSpace(), mRequestData.getMethodName());

        LinkedHashMap<String, Object> map = mRequestData.getParams();
        if (map == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            rpc.addProperty(key, entry.getValue());
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        LinkedHashMap<String, Object> head = mRequestData.getHeads();
        if (head.size() > 0) {
            Element[] header = new Element[1];
            header[0] = new Element().createElement(mRequestData.getNameSpace(), mRequestData.getTokenHeader());
            try {
                for (Map.Entry<String, Object> entry : head.entrySet()) {
                    Element element = new Element().createElement(mRequestData.getNameSpace(), entry.getKey());
                    element.addChild(Node.TEXT, entry.getValue());
                    header[0].addChild(Node.ELEMENT, element);
                }
            } catch (Exception e) {
                Log.e(TAG, "Parse element occur error.");
            }
            envelope.headerOut = header;
        }
        envelope.bodyOut = rpc;
        envelope.dotNet = dotNetFlag;
        envelope.setOutputSoapObject(rpc);
        String url = mRequestData.getHost() + mRequestData.getUrl();
        int timeout = 15000;
        WsHttpTransportSE ht = new WsHttpTransportSE(url, timeout);

        ht.debug = true;
        try {
            ht.call(mRequestData.getNameSpace() + mRequestData.getMethodName(), envelope);
            Object kk = envelope.getResponse();
            String responseData = kk.toString();//(String) envelope.getResponse();
            sendMessage(200, "网络请求成功", responseData);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求错误io-" + e.getMessage());
            sendMessage(201, "网络请求失败", null);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求错误xml-" + e.getMessage());
            sendMessage(201, "网络请求失败", null);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求错误null-" + e.getMessage());
            sendMessage(200, "返回数据为空", null);
        }
    }

    private void sendMessage(int code, String successMsg, String data) {
        WsResponseMessage wsMsg = new WsResponseMessage();
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
