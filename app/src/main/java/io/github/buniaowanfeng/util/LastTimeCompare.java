package io.github.buniaowanfeng.util;

import java.util.Comparator;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.Soul;

/**
 * Created by caofeng on 16-7-22.
 */
public class LastTimeCompare implements Comparator<Soul<AppInfo>> {
    @Override
    public int compare(Soul soul, Soul t1) {
        return (int) (soul.startTimeInLong - t1.startTimeInLong);
    }
}
