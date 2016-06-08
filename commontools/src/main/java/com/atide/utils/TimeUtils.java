package com.atide.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Package: com.atide.utils
 * @Description: use for getting date or time
 * @author: HJC
 * @date: 2016/3/4.
 */
public class TimeUtils {

    /**
     * get time like HH:mm:ss.sss
     * @return time
     */
    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.sss");
        return formatter.format(date).toString();
    }


    /**
     * get day like yyyy-MM-dd
     * @return
     */
    public static String getDay() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date).toString();
    }
}
