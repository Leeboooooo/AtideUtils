package com.atide.utils.net.webservice;

import org.repackage.ksoap2.transport.HttpTransportSE;
import org.repackage.ksoap2.transport.ServiceConnection;

import java.io.IOException;

/**
 * Created by Lee on 14-4-16.
 */
public class WsHttpTransportSE extends HttpTransportSE
{
    private int timeout = 30000; // 默认超时时间为30s

    public WsHttpTransportSE(String url) {
        super(url);
    }

    public WsHttpTransportSE(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    @Override
    protected ServiceConnection getServiceConnection() throws IOException {
        WsServerConnectionSE serviceConnection = new WsServerConnectionSE(url);
        serviceConnection.setConnectionTimeOut(timeout);
        return serviceConnection;
    }
}
