package com.atide.utils.net.httpservice;

import org.repackage.ksoap2.transport.ServiceConnection;

import java.io.IOException;

/**
 * Created by Lee on 14-4-16.
 */
public class HttpTransportSE extends org.repackage.ksoap2.transport.HttpTransportSE
{
    private int timeout = 30000; // 默认超时时间为30s

    public HttpTransportSE(String url) {
        super(url);
    }

    public HttpTransportSE(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    @Override
    protected ServiceConnection getServiceConnection() throws IOException {
        HtServerConnectionSE serviceConnection = new HtServerConnectionSE(url);
        serviceConnection.setConnectionTimeOut(timeout);
        return serviceConnection;
    }
}
