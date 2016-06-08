package com.atide.utils.net.httpservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lee on 14-5-27.
 */
public class RequestJsonHttp
{
    private static String mBizCode = "ME001";
    private static String mOutputFormat = "json";
    private static String mUthenticate = "purviewapp";

    private JSONObject mRoot;
    private JSONObject mBizInfo;

     public static RequestJsonHttp build()
    {
        RequestJsonHttp rj = new RequestJsonHttp();
        return  rj;
    }

    private RequestJsonHttp()
    {
        mRoot = new JSONObject();
        mBizInfo = new JSONObject();
//        try {
//            mRoot.put("bizCode",mBizCode);
//            mRoot.put("outputFormat",mOutputFormat);
//            mRoot.put("authenticate",mUthenticate);
//            mRoot.put("bizInfo",mBizInfo );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    public void modifyParam(String key, Object code)
    {
        try {
            mRoot.put(key,code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addBizInfoArray(String key,JSONObject code)
    {
        JSONArray jArray = mBizInfo.optJSONArray(key);
        if(jArray == null)
        {
            jArray = new JSONArray();
            try {
                mBizInfo.put(key, jArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jArray.put(code);
    }

    @Override
    public String toString() {
        return mRoot.toString();
    }
}
