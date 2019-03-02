package io.github.buniaowanfeng.data;

/**
 * Created by caofeng on 16-7-18.
 */
public class HeadMessage {
    public int dayInInt;
    public String day;
    public String goal;
    public String usedTime;
    public long usedTimeInLong;
    public String remain;
    public String numberOfWake;
    public String numberOfApps;
    public String numberOfTimes;
    public int progress;
    public boolean isOverUse;
    @Override
    public String toString() {
        return "HeadMessage{" +
                "day='" + day + '\'' +
                ", goal='" + goal + '\'' +
                ", remain='" + remain + '\'' +
                ", numberOfWake='" + numberOfWake + '\'' +
                ", numberOfApps='" + numberOfApps + '\'' +
                ", numberOfTimes='" + numberOfTimes + '\'' +
                ", progress=" + progress +
                ", isOverUse=" + isOverUse +
                '}';
    }
}
