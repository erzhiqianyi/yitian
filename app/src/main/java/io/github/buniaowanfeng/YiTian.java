package io.github.buniaowanfeng;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import io.github.buniaowanfeng.util.Androids;

/**
 * Created by caofeng on 16-6-24.
 */
public class YiTian extends Application {
    public static Context mContext;
    public static SharedPreferences mSp;
    public static String mLauncher ;
    public static String mLauncherPackage;
    public static String mIconPath;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mSp = getSharedPreferences(getString(R.string.app_usage),MODE_APPEND);
        mLauncherPackage =Androids.getLauncherName();
        mLauncher = Androids.getAppName(mLauncherPackage);
        mIconPath = getExternalFilesDir(null).getPath() + "icon";
    }
}
