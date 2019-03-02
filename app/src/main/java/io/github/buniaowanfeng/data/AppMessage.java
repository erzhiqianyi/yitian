package io.github.buniaowanfeng.data;

import android.graphics.drawable.Drawable;

/**
 * Created by caofeng on 16-7-18.
 */
public class AppMessage {
    public String startTime;
    public String endTime;
    public String usedTime;
    public String appName;
    public Drawable appIcon;
    public String packageName;
    public long startTimeInLong;
    public int height;
    public int type;
    public float progress;
    public float startSweap;

    public AppMessage() {
    }

    @Override
    public String toString() {
        return "AppMessage{" +
                "startTime='" + startTime + '\'' +
                ", usedTime='" + usedTime + '\'' +
                ", appName='" + appName + '\'' +
                ", height=" + height +
                ", type=" + type +
                '}';
    }
}
