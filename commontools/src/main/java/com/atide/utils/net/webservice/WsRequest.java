package com.atide.utils.net.webservice;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by Lee on 14-4-16.
 */
public abstract class WsRequest implements Serializable
{
    private String mHost;
    private String mUrl;
    private String mNameSpace;
    private String mMethodName;
    private String mTokenHeader;
    private String mId;
    private String mTag;
    private boolean mflag=false;
    private LinkedHashMap<String,Object> mParams;
    private LinkedHashMap<String,Object> mHeads;
    private static final long sSerVerUId = -4903107312403938616L;
    private WsResponseMessage msg;

 public WsRequest()
    {
        mParams = new LinkedHashMap<String, Object>();
        mHeads = new LinkedHashMap<String, Object>();
        mTag = "null";
    }

    public final WsRequest setTag(String tag)
    {
        mTag = tag;
        return this;
    }

    public final String getTag()
    {
        return mTag;
    }

    public final WsRequest setHost(String host)
    {
        mHost = host;
        return this;
    }

    public final WsRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }
    public final WsRequest setFlag(boolean flag)
    {
        mflag = flag;
        return this;
    }


    public final WsRequest setNameSpace(String nameSpace)
    {
        mNameSpace = nameSpace;
        return this;
    }

    public final WsRequest setMethodName(String methodName)
    {
        mMethodName = methodName;
        return this;
    }
    public final WsRequest setTokenHeader(String tokenHeader)
    {
        mTokenHeader = tokenHeader;
        return this;
    }

    public final WsRequest addParam(String key, Object value)
    {
        mParams.put(key, value);
        return this;
    }
    public final WsRequest addHead(String key, Object value)
    {
        mHeads.put(key, value);
        return this;
    }


    protected final String getHost()
    {
        return mHost;
    }
    protected final boolean getFlag()
    {
        return mflag;
    }


    protected final String getUrl()
    {
        return mUrl;
    }

    public final String getNameSpace()
    {
        return mNameSpace;
    }

    public final String getMethodName()
    {
        return mMethodName;
    }
    public final String getTokenHeader()
    {
        return mTokenHeader;
    }
    public final LinkedHashMap<String, Object > getParams()
    {
        return mParams;
    }
    public final LinkedHashMap<String, Object > getHeads()
    {
        return mHeads;
    }
    public final void setId(String id)
    {
        mId = id;
    }

    protected final String getId()
    {
        return mId;
    }

    public final WsRequest notifyRequest()
    {
        WsPoolManager.getInstance().sendRequest(this,mflag);
        return this;
    }

    public abstract void onResponse(WsResponseMessage msg);

}
