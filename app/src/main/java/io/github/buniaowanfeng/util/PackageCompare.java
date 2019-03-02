package io.github.buniaowanfeng.util;

import java.util.Comparator;

import io.github.buniaowanfeng.data.AppMessage;

/**
 * Created by caofeng on 16-7-24.
 */
public class PackageCompare implements Comparator<AppMessage> {
    @Override
    public int compare(AppMessage appMessage, AppMessage t1) {
        return appMessage.packageName.compareTo(t1.packageName);
    }
}
