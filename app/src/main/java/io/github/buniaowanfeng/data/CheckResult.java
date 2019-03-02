package io.github.buniaowanfeng.data;

import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-9.
 */
public class CheckResult {
    /**
     * curPackage  当前应用的包名 io.github.buniaowanfeng
     *
     */
    public String curPackage;
    public String prePackage;
    public long curTime;
    public boolean isNull;

    public CheckResult() {

    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "curPackage='" + curPackage + '\'' +
                ", prePackage='" + prePackage + '\'' +
                ", curTime=" + DateUtil.time(curTime) +
                ", isNotNone=" + isNull +
                '}';
    }
}
