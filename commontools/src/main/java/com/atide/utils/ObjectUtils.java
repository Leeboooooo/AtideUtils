package com.atide.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package: com.atide.utils
 * @Description: Decompose the class attribute
 * @author: HJC
 * @date: 2016/3/3.
 */
public class ObjectUtils {
    /**
     * get property value by its name
     * */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * get property name, return array
     * */
    public static String[] getFiledName(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            System.out.println(fields[i].getType());
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * get property type、name、and value, take in map and return list
     * */
    public static List<Map<String, String>> getFiledsInfo(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> infoMap=null;
        for(int i=0;i<fields.length;i++){
            infoMap = new HashMap<String, String>();
            infoMap.put("type", fields[i].getType().toString());
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o).toString());
            list.add(infoMap);
        }
        return list;
    }

    /**
     * get properties on the object and return an array
     * */
    public static Object[] getFiledValues(Object o){
        String[] fieldNames=ObjectUtils.getFiledName(o);
        Object[] value=new Object[fieldNames.length];
        for(int i=0;i<fieldNames.length;i++){
            value[i]=ObjectUtils.getFieldValueByName(fieldNames[i], o);
        }
        return value;
    }

}
