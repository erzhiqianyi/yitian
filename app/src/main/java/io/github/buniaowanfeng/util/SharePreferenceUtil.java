package io.github.buniaowanfeng.util;

import io.github.buniaowanfeng.YiTian;

/**
 * Created by caofeng on 16-7-9.
 */
public class SharePreferenceUtil {
    public static final String KEY_PRE_START_TIME = "pre_start_time";

    public static void putLong(String key ,long value){
        YiTian.mSp.edit().putLong(key,value).apply();
    }

    public static long getLong(String key) {
        return YiTian.mSp.getLong(key,0);
    }

}
