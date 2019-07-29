package com.woniu.core.utils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Anlycal<远>
 * @date 2019/6/20
 * @description ...
 */


public class DateUtil {
    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    public static final String yyMMdd = "yyyyMMdd";



    public static String formatTime(long millis,String type){
        return getStr(millis,type);
    }

    private static String getStr(long millis,String type){
        SimpleDateFormat format = new WeakReference<>(new SimpleDateFormat(type)).get();
        Date date = new WeakReference<>(new Date(millis)).get();
        return format.format(date);
    }

    /**
     * 返回文字描述的日期
     *
     * @param millis
     * @return
     */
    public static String timeFormatText(long millis) {
        if (millis < 0) {
            return "";
        }
        long diff = new WeakReference<>(new Date()).get().getTime() - new WeakReference<>(new Date(millis)).get().getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

}
