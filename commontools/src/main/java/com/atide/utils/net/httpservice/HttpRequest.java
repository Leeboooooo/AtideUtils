package com.atide.utils.net.httpservice;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by Lee on 14-4-16.
 */
public abstract class HttpRequest implements Serializable {
    private static final long sSerVerUId = -4903107312403938616L;
    private String mHost;
    private String mUrl;
    private String mMethodName;
    private String mId;
    private String mTag;
    private boolean mflag = false;
    private String mParams;
    private LinkedHashMap<String, Object> mHeads;
    private HtResponseMessage msg;

    public HttpRequest() {
        mParams = null;
        mHeads = new LinkedHashMap<String, Object>();
        mTag = "null";
    }

    public final HttpRequest setTag(String tag) {
        mTag = tag;
        return this;
    }

    public final String getTag() {
        return mTag;
    }

    public final HttpRequest setHost(String host) {
        mHost = host;
        return this;
    }

    public final HttpRequest setUrl(String url) {
        mUrl = url;
        return this;
    }

    public final HttpRequest setFlag(boolean flag) {
        mflag = flag;
        return this;
    }


    //设置方法名称
    public final HttpRequest setMethodName(String methodName) {
        mMethodName = methodName;
        return this;
    }


    public final HttpRequest addParam(String value) {
        mParams = value;
        return this;
    }

    public final HttpRequest addHead(String key, Object value) {
        mHeads.put(key, value);
        return this;
    }


    protected final String getHost() {
        return mHost;
    }

    protected final boolean getFlag() {
        return mflag;
    }


    protected final String getUrl() {
        return mUrl;
    }

    public final String getMethodName() {
        return mMethodName;
    }

    public final String getParams() {
        return mParams;
    }

    public final LinkedHashMap<String, Object> getHeads() {
        return mHeads;
    }

    public final void setId(String id) {
        mId = id;
    }

    protected final String getId() {
        return mId;
    }

    public final HttpRequest notifyRequest() {

        //把参数传递到HtPoolManager中去
        HtPoolManager.getInstance().sendRequest(this, mflag);
        return this;
    }

    public abstract void onResponse(HtResponseMessage msg);

}
