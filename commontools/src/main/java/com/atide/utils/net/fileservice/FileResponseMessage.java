package com.atide.utils.net.fileservice;

import java.io.Serializable;

/**
 * Created by Lee on 14-4-17.
 */
public class FileResponseMessage implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    public int mCode;
    public String mMsg;
    public String mData;
    public long mDownSize;
    public long mFileSize;
    public Boolean mDownDone;
    public String mTag;
}
