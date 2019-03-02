package io.github.buniaowanfeng.data;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyHead {
    public String packageName;
    public String appName;
    public String usedTime;
    public String numberOfTimes;
    public int progress;

    @Override
    public String toString() {
        return "AppDailyHead{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", usedTime='" + usedTime + '\'' +
                ", numberOfTimes='" + numberOfTimes + '\'' +
                ", progress=" + progress +
                '}';
    }
}
