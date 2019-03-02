package io.github.buniaowanfeng.data;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by caofeng on 16-7-22.
 */
public class AppMultiple {
    public String startTime;
    public String usedTime;
    public String appName;
    public String packageName;
    public int day;
    public int height;
    public List<Drawable> appIcon;
    public List<String> appPackage;
    @Override
    public String toString() {
        return "AppMultiple{" +
                " day " + day +
                "startTime='" + startTime + '\'' +
                ", usedTime='" + usedTime + '\'' +
                ", height=" + height +
                ", app name " + appName +
                '}';
    }
}
