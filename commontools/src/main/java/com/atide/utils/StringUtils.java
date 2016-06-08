package com.atide.utils;

/**
 * @Package: com.atide.utils
 * @Description: String tools
 * @author: HJC
 * @date: 2016/3/18.
 */
public class StringUtils {

    /**
     * get string not null from object
     * @param source
     * @return
     */
    public static String getStringNotNull(Object source){
        String result = source.toString();
        if(null == result || result.length()<1 ){
            result = "";
        }
        return result;
    }

    /**
     * get string not null from string
     * @param str
     * @return
     */
    public static String getStringNotNull(String str){
        String result = "";
        if(null != result && result.length()>0 ){
            result = str;
        }
        return result;
    }
}
