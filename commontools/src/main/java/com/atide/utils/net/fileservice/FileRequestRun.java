package com.atide.utils.net.fileservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lee on 14-4-17.
 */
public class FileRequestRun implements Runnable {
    private static final int mRevBufferSize = 1024 * 2;
    private FileDownRequest mRequestData;
    private Handler mRevedHander;
    private String mId;
    private boolean mStopRunning;
    private DefaultHttpClient mHttpClient;
    private HttpContext mHttpContext = null;

    public FileRequestRun(FileDownRequest adapter, Handler revedHandler) {
        mRequestData = adapter;
        mRevedHander = revedHandler;
        mId = UUID.randomUUID().toString();
        this.mStopRunning = false;
        mHttpContext = new BasicHttpContext();
    }

    public void cancel() {
        this.mStopRunning = true;
        mHttpClient.getConnectionManager().shutdown();
    }

    @Override
    public void run() {
        if (mRequestData == null || mStopRunning) {
            return;
        }
        mHttpClient = getHttpClient();
        String urlString = mRequestData.getHost() + mRequestData.getUrl();
        String severPath = mRequestData.getServerPath();
        HttpPost httpPost = new HttpPost(urlString);

        if (mRequestData.getmIsDownload()) {
            List<NameValuePair> reqPar = new ArrayList<NameValuePair>();
            reqPar.add(new BasicNameValuePair("params", mRequestData.getParams()));
            //下载
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(reqPar, "UTF-8"));
                HttpResponse httpResponse = mHttpClient.execute(httpPost);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    BufferedHttpEntity bufHttpEntity = null;
                    try {
                        bufHttpEntity = new BufferedHttpEntity(entity);
                    } catch (OutOfMemoryError e) {
                        Log.e("FileRequestRun", "file down out of memory.");
                        return;
                    }
                    InputStream is = bufHttpEntity.getContent();
                    if (is != null && is.available() > 0) {
                        downloadFile(is, responseCode);
                    } else {
                        FileResponseMessage resMsg = new FileResponseMessage();
                        resMsg.mCode = -2;
                        resMsg.mData = "";
                        resMsg.mMsg = "文件不存在！";
                        resMsg.mDownDone = true;
                        resMsg.mDownSize = 0;
                        resMsg.mFileSize = 0;
                        sendMessage(resMsg);
                    }

                } else {
                    FileResponseMessage resMsg = new FileResponseMessage();
                    resMsg.mCode = -2;
                    resMsg.mData = "";
                    resMsg.mMsg = "网络异常，请检查网络";
                    resMsg.mDownDone = true;
                    resMsg.mDownSize = 0;
                    resMsg.mFileSize = 0;
                    sendMessage(resMsg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //上传
            HttpResponse httpResponse = null;
            try {
                JSONObject jResponse = new JSONObject(mRequestData.getParams());
                String filePath = "";
                if (mRequestData.getUrl().contains("Login")) {
                    filePath = decodeImage(mRequestData.getLocalPath(), "", 100);
                } else {
                    filePath = decodeImage(mRequestData.getLocalPath(), jResponse.optString("refid"), 800);
                }

                File file = new File(filePath);
                FileBody fileBody = new FileBody(file, "image/jpeg");
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("image", fileBody);
                entity.addPart("params", new StringBody(mRequestData.getParams()));
                httpPost.setEntity(entity);
                httpPost.addHeader("charset", HTTP.UTF_8);
                httpResponse = mHttpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int responseCode = 0;
            if ( null != httpResponse ){
                responseCode = httpResponse.getStatusLine().getStatusCode();
            }
            if (responseCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                FileResponseMessage resMsg = new FileResponseMessage();
                resMsg.mCode = 200;
                resMsg.mData = "";
                resMsg.mMsg = "";
                resMsg.mDownDone = true;
                resMsg.mDownSize = 0;
                resMsg.mFileSize = 0;
                sendMessage(resMsg);

            } else {
                FileResponseMessage resMsg = new FileResponseMessage();
                resMsg.mCode = -2;
                resMsg.mData = "";
                resMsg.mMsg = "网络异常，请检查网络";
                resMsg.mDownDone = true;
                resMsg.mDownSize = 0;
                resMsg.mFileSize = 0;
                sendMessage(resMsg);
            }


        }
    }

    private String decodeImage(String orgpath, String id, int resultSize) {
        String reslutPath = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(orgpath, options);
        options.inJustDecodeBounds = false;
        //计算缩放比

        int be = (int) ((options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / (float) resultSize);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;

        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(orgpath, options);
        reslutPath = "" + id + ".jpg";
        File file = new File(reslutPath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reslutPath;
    }

    private void downloadFile(InputStream is, int responseCode) {
        int fileSize = 0;
        try {
            fileSize = is.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        int readCount = 0;
        try {
            out = new FileOutputStream(mRequestData.getLocalPath(), false);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

            byte[] buf = new byte[4096];
            int bytesRead = 0;
            while (bytesRead >= 0 && !mStopRunning) {
                long now = System.currentTimeMillis();
                try {
                    bytesRead = bufferedInputStream.read(buf);
                    bufferedOutputStream.write(buf, 0, bytesRead);
                    readCount += bytesRead;

                    FileResponseMessage resMsg = new FileResponseMessage();
                    resMsg.mCode = 200;
                    resMsg.mData = "";
                    resMsg.mMsg = "正在下载";
                    resMsg.mDownDone = false;
                    resMsg.mDownSize = readCount;
                    resMsg.mFileSize = fileSize;
                    sendMessage(resMsg);
                    if (readCount >= fileSize) {
                        break;
                    }
                } catch (Exception e) {
                    FileResponseMessage resMsg = new FileResponseMessage();
                    resMsg.mCode = -2;
                    resMsg.mData = "";
                    resMsg.mMsg = "文件下载异常";
                    resMsg.mDownDone = true;
                    resMsg.mDownSize = 0;
                    resMsg.mFileSize = 0;
                    sendMessage(resMsg);
                }
            }
            try {
                is.close();
                bufferedInputStream.close();
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                FileResponseMessage resMsg = new FileResponseMessage();
                resMsg.mCode = -2;
                resMsg.mData = "";
                resMsg.mMsg = "文件下载异常";
                resMsg.mDownDone = true;
                resMsg.mDownSize = 0;
                resMsg.mFileSize = 0;
                sendMessage(resMsg);
            }
        } catch (FileNotFoundException e) {
            FileResponseMessage resMsg = new FileResponseMessage();
            resMsg.mCode = -2;
            resMsg.mData = "";
            resMsg.mMsg = "文件不存在";
            resMsg.mDownDone = true;
            resMsg.mDownSize = 0;
            resMsg.mFileSize = 0;
            sendMessage(resMsg);
        }

        FileResponseMessage resMsg = new FileResponseMessage();
        resMsg.mCode = 200;
        resMsg.mData = mRequestData.getLocalPath();
        resMsg.mMsg = "文件下载成功";
        resMsg.mDownDone = true;
        resMsg.mDownSize = 0;
        resMsg.mFileSize = 0;
        sendMessage(resMsg);
    }

    public String getId() {
        return mId;
    }

    private static DefaultHttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000); //此处的单位是毫秒
        HttpConnectionParams.setSocketBufferSize(httpParams, mRevBufferSize * 2);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    private void sendMessage(FileResponseMessage resMsg) {
        Message hadnelMsg = mRevedHander.obtainMessage();
        Bundle bund = new Bundle();
        bund.putSerializable("adpater", mRequestData);
        bund.putSerializable("responseMsg", resMsg);
        hadnelMsg.setData(bund);
        hadnelMsg.sendToTarget();
    }

    private String getFileFormat(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index <= 0) {
            return "";
        }
        String format = filePath.substring(index, filePath.length());
        return format;
    }
}
