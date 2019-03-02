package io.github.buniaowanfeng.data;

import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-14.
 */
public class UsageAmount {
    public int day;
    public String appName;
    public long usedTime;
    public int numberOfTimes;
    public String appPackage;
    public String usedTimes;
    public int usedRate;
    public int numberOfTimeRate;
    public String average;
    public UsageAmount() {
    }

    @Override
    public String toString() {
        return "UsageAmount{" +
                "day=" + day +
                ", appName='" + appName + '\'' +
                ", usedTime=" + DateUtil.longToString(usedTime) +
                ", numberOfTimes=" + numberOfTimes +
                ", appPackage='" + appPackage + '\'' +
                '}';
    }
}
