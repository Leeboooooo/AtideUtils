package com.atide.utils.net.fileservice;

import java.io.Serializable;


/**
 * Created by Lee on 14-4-17.
 */
public abstract class FileDownRequest implements Serializable {

    private static final long sSerVerUId = -4903107312403938616L;
    private String mHost;
    private String mUrl;
    private String mId;
    private String mServerPath;
    private String mLocalPath;
    private String mFileName;
    private String mFileId;
    private String mParams;
    private Boolean mIsDownload;

    public FileDownRequest(String host)
    {
        mHost = host;
//        mLocalPath = UserAccount.getInstance().getLocalDir("fileCach/");
        mIsDownload = false;
    }

    public final FileDownRequest setServerPath(String serverPath)
    {
        mServerPath = serverPath;
        return this;
    }
    public final FileDownRequest setParams(String params)
    {
        mParams = params;
        return this;
    }

    public final String getServerPath()
    {
        return mServerPath;
    }
    public final FileDownRequest setLocalPath(String localPath)
    {
        mLocalPath = localPath;
        return this;
    }

    public final FileDownRequest setIsDownLoad(Boolean isDownload)
    {
        mIsDownload = isDownload;
        return this;
    }

    public final FileDownRequest setFileName(String fileName)
    {
        mFileName = fileName;
        return this;
    }

    public final FileDownRequest setFileId(String fileId)
    {
        mFileId = fileId;
        return this;
    }

    public final FileDownRequest setHost(String host)
    {
        mHost = host;
        return this;
    }

    public final FileDownRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    protected final String getHost()
    {
        return mHost;
    }

    protected final String getUrl()
    {
        return mUrl;
    }

    public final void setId(String id)
    {
        mId = id;
    }

    protected final String getId()
    {
        return mId;
    }

    public final String getLocalPath()
    {
        return mLocalPath;
    }

    public final String getFileName()
    {
        return mFileName;
    }

    public final String getParams()
    {
        return mParams;
    }

    public final String getFileId()
    {
        return mFileId;
    }

    public final Boolean getmIsDownload()
    {
        return mIsDownload;
    }

    public void notifyRequest()
    {
        FilePoolManager.getInstance().sendRequest(this);
    }
    public abstract void onResponse(FileResponseMessage msg);
    public abstract void progress(FileResponseMessage msg);
}
