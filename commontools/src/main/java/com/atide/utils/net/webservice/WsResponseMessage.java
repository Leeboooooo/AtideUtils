package com.atide.utils.net.webservice;

import java.io.Serializable;

/**
 * Created by Lee on 14-4-16.
 */
public class WsResponseMessage implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;

    public int mCode;
    public String mMsg;
    public String mData;
    public String mTag;
}
