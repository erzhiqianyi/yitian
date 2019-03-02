package io.github.buniaowanfeng.data;

import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-14.
 * daily amount info
 */
public class DailyInfo {
    public int day;
    public int numberOfApps;
    public int numberOfTimes;
    public int numberOfWake;
    public long usedTime;
    public long dailyGoal;
    public boolean isReported;

    public DailyInfo() {
    }

    @Override
    public String toString() {
        return "DailyInfo{" +
                "day=" + day +
                ", numberOfApps=" + numberOfApps +
                ", numberOfTimes=" + numberOfTimes +
                ", numberOfWake=" + numberOfWake +
                ", usedTime=" + DateUtil.longToDayInfo(usedTime) +
                ", isReported=" + isReported +
                '}';
    }
}
