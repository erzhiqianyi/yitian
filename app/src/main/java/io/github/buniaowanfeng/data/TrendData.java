package io.github.buniaowanfeng.data;

/**
 * Created by caofeng on 16-7-28.
 * data for tend activity
 */
public class TrendData {
    public static final int TYPE_WHOLE = 1;
    public static final int TYPE_APP = 2;
    public String packageName;
    public String appName;
    public String countDay;
    public String usedTime;
    public String usedTimes;
    public String wakeTimes;
    public int type;
    public int day;
    public int progress;

    @Override
    public String toString() {
        return "TrendData{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", countDay='" + countDay + '\'' +
                ", usedTime='" + usedTime + '\'' +
                ", usedTimes='" + usedTimes + '\'' +
                ", wakeTimes='" + wakeTimes + '\'' +
                ", day=" + day +
                ", progress=" + progress +
                '}';
    }
}
