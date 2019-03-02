package io.github.buniaowanfeng.data;

import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-14.
 * information of a used app
 */
public class AppInfo {
    public static final String APP_IDLE = "idle";
    public static final int TYPE_IDLE = 0;
    public static final int TYPE_APP = 1;
    public static final int TYPE_MULTIPLE = 2;
    public static final int TYPE_HEAD =3 ;
    public static final int TYPE_SINGLE = 4;

    public static final int IDLE_BASE_HEIGHT = 100;
    public static final int APP_BASE_HEIGHT = 300;
    public static final long SECONDS_OF_A_DAY = 8640000;

    public String appName;
    public String appPackage;
    public long startTime;
    public long endTime;
    public long usedTime;
    public int day;
    public int type;
    public int height;
    public int tag;
    public AppInfo() {
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", appPackage='" + appPackage + '\'' +
                ", startTime=" + DateUtil.time(startTime)+
                ", endTime=" + DateUtil.time(endTime)+
                ", usedTime=" + DateUtil.longToString(usedTime)+
                ", day=" + day +
                ", type=" + type +
                '}';
    }
}
