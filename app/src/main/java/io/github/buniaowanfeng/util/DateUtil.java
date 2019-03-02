package io.github.buniaowanfeng.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;

/**
 * Created by caofeng on 16-7-9.
 */
public class DateUtil {
    /**
     * 将毫秒时间转换为字符串时间
     * @param millSeconds
     * @return　如果时间小于６０秒，返回秒，
     * 如果时间大于１分钟且小于６０分钟，返回分＋秒的格式，
     * 如果时间大于１小时，返回小时＋分钟＋秒的格式
     */
    public static String longToString(long millSeconds){
        StringBuilder builder = new StringBuilder();
        millSeconds = millSeconds < 0 ? -millSeconds : millSeconds;
        int seconds = (int) (millSeconds / 1000);
        int hours = 0;
        int minute = 0;
        int second = 0;
        if (seconds >= 3600){
            hours = (int) (seconds / 3600);
            builder.append(hours);
            builder.append("h");
        }

        seconds = seconds - 3600 * hours;
        if (seconds >= 60){
            minute = (int) (seconds / 60);
            builder.append(minute);
            builder.append("m");
        }

        second = seconds - 60 * minute;
        builder.append(second);
        builder.append("s");
        return builder.toString();
    }

    public static String longToMessage(long millSeconds){
        StringBuilder builder = new StringBuilder();
        millSeconds = millSeconds < 0 ? -millSeconds : millSeconds;
        int seconds = (int) (millSeconds / 1000);
        int hours = 0;
        int minute = 0;
        int second = 0;
        if (seconds >= 3600){
            hours = (int) (seconds / 3600);
            builder.append(hours);
            builder.append("小时");
        }

        seconds = seconds - 3600 * hours;
        if (seconds >= 60){
            minute = (int) (seconds / 60);
            builder.append(minute);
            builder.append("分");
        }

        if (builder.toString().length() > 0)
            return builder.toString();
        else {
            second = seconds - 60 * minute;
            builder.append(second);
            builder.append("秒");
            return builder.toString();
        }

    }
    /**
     * 将毫秒时间转换成　yyyyMMdd 格式的时间
     * @param time
     * @return
     */
    public static int day(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(time);
        return Integer.parseInt(format.format(date));
    }

    public static Date longToDate(long time){
        SimpleDateFormat format = new SimpleDateFormat("dd");
        Date date = new Date(time);
        return date;
    }
    /**
     * 将毫秒时间转换成　HH:mm:ss 格式的时间
     * @param time
     * @return
     */
    public static String time(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }

    public static String intToString(int dayInInt){
        int year = dayInInt / 10000;
        int month = (dayInInt - year * 10000) / 100;
        int day = (dayInInt - year * 10000 - month * 100);
        if (day(System.currentTimeMillis()) == dayInInt)
            return "今天";
        return month + "月" + day + "日";
    }
    public static String longToStringNoS(long millSeconds) {
        StringBuilder builder = new StringBuilder();
        millSeconds = millSeconds < 0 ? -millSeconds : millSeconds;
        int seconds = (int) (millSeconds / 1000);
        int hours = 0;
        int minute = 0;
        if (seconds >= 3600){
            hours = (int) (seconds / 3600);
            builder.append(hours);
            builder.append("h");
        }

        seconds = seconds - 3600 * hours;
        if (seconds >= 60){
            minute = (int) (seconds / 60);
            builder.append(minute);
            builder.append("m");
        }

        return builder.toString();
    }

    public static String longToDayInfo(long usedTime) {
        int seconds = (int) (usedTime / 1000);
        int year = 0;
        int day = 0 ;
        int hours = 0;
        int minute = 0 ;
        if ( seconds > 3600 ){
            hours = (int) (seconds / 3600);
            if (hours > 24  ){
                day = (int)(hours / 24);
                hours = hours - day * 24;
                if (day > 365){
                    year = (int)(day / 365);
                }
            }
        }

        seconds = seconds - 3600 * hours;
        if (seconds >= 60){
            minute = (int) (seconds / 60);
        }

        seconds  = seconds - 60 * minute;

        if (day > 0 ){
            return day + YiTian.mContext.getString(R.string.day)
                    + hours + YiTian.mContext.getString(R.string.hour);
        }else {
            if (hours > 0){
                return hours + YiTian.mContext.getString(R.string.hour) +
                        minute + YiTian.mContext.getString(R.string.minute);
            }else {
                if (minute > 0){
                    return  minute + YiTian.mContext.getString(R.string.minute)+
                            seconds + YiTian.mContext.getString(R.string.seconds);
                }else {
                    return  seconds + YiTian.mContext.getString(R.string.seconds);
                }
            }
        }
    }

    public static String intToDayInfo(int number) {
        if (number < 1000)
            return number +"";
        else if (number < 10 * 1000)
            return number / 1000 + YiTian.mContext.getString(R.string.thosand);
        else if ( number < 10 * 1000 * 100 )
            return number / 10000 + YiTian.mContext.getString(R.string.wan);
        else if ( number < 10 * 1000 * 1000)
            return number / 10 * 1000 * 100 + YiTian.mContext.getString(R.string.millision);
        else  if ( number < 10 * 1000 * 10000)
            return number / 10 * 1000 * 1000 + YiTian.mContext.getString(R.string.qianwan);
        else
            return number / 10 * 1000 * 10000 + YiTian.mContext.getString(R.string.yi);
    }

    public static String minuteToString(int time){
        int hour = time / 60;
        int minute = time - hour * 60 ;

        String result = hour > 0 ? hour+"h"+ (minute > 0 ? minute+"m":"") : (minute > 0 ? minute+"m":"");
        return result;
    }

    public static int getMonth(Date date){
        SimpleDateFormat format= new SimpleDateFormat("MM");
        return Integer.valueOf(format.format(date));
    }

    public static int getYear(Date date){
        SimpleDateFormat format= new SimpleDateFormat("yyyy");
        return Integer.valueOf(format.format(date));
    }
}
