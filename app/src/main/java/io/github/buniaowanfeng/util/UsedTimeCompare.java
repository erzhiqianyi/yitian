package io.github.buniaowanfeng.util;

import java.util.Comparator;

import io.github.buniaowanfeng.data.UsageAmount;

/**
 * Created by caofeng on 16-7-25.
 */
public class UsedTimeCompare implements Comparator<UsageAmount>{
    @Override
    public int compare(UsageAmount amount, UsageAmount t1) {
        return (int) (t1.usedTime - amount.usedTime);
    }
}
