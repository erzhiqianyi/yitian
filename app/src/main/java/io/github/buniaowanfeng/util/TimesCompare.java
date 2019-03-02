package io.github.buniaowanfeng.util;

import java.util.Comparator;

import io.github.buniaowanfeng.data.Base;
import io.github.buniaowanfeng.data.UsageAmount;

/**
 * Created by caofeng on 16-7-26.
 */
public class TimesCompare implements Comparator<Base<String,UsageAmount>> {
    @Override
    public int compare(Base<String, UsageAmount> t1, Base<String, UsageAmount> t2) {
        return t2.body.numberOfTimes - t1.body.numberOfTimes;
    }
}
