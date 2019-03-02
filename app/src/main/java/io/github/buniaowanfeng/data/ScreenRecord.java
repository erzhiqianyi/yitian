package io.github.buniaowanfeng.data;

import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-9.
 * information of screen on or screen off
 *
 */
public class ScreenRecord  {

    public static final String SCREEN_ON = "on";
    public static final String SCREEN_OFF = "off";

    public long startTime;
    public int day;
    public String type;

    public ScreenRecord() {

    }

    public ScreenRecord(long startTime, int day, String type) {
        this.startTime = startTime;
        this.day = day;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ScreenRecord { " +
                "startTime= " + DateUtil.time(startTime)+
                ", day= " + day +
                ", type='" + type + '\'' +
                '}';

    }
}
