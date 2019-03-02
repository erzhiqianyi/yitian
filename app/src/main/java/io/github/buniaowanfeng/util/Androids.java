package io.github.buniaowanfeng.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.List;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppInfo;

/**
 * Created by caofeng on 16-6-23.
 * 工具类
 */
public class Androids {
    private static final String TAG = "androids";

    /**
     * 判断服务是否在运行
     * @param context
     * @return
     */
    public static boolean isYiTianServiceRunning(Context context){
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos =
                manager.getRunningServices(300);
        for (ActivityManager.RunningServiceInfo info : serviceInfos){
            String className = info.service.getClassName();
            if (className.equals("io.github.buniaowanfeng.service.YiTianService"))
                return true;
        }
        return false;
    }


    /**
     * 获取桌面的包名
     * @return
     */
    public static String getLauncherName(){
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo resolveInfo = YiTian.mContext.getPackageManager()
                .resolveActivity(intent,0);
        if (resolveInfo == null){
            return "";
        }

        if (resolveInfo.activityInfo.packageName.equals("android")){
            return "";
        }else {
            return resolveInfo.activityInfo.packageName;
        }
    }

    public static String getAppName(String packageName){
        String appName = "";
        if (packageName.equals(AppInfo.APP_IDLE))
            return AppInfo.APP_IDLE;
        PackageManager manager = YiTian.mContext.getPackageManager();
        try {
            ApplicationInfo info = manager.getApplicationInfo(packageName,0);
            appName = (String) manager.getApplicationLabel(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appName.equals(YiTian.mLauncher)){
            appName = AppInfo.APP_IDLE;
        }

        return appName;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasPermissionForUsage(){
        PackageManager packageManager = YiTian.mContext.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    YiTian.mContext.getPackageName(),0);
            AppOpsManager appOpsManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) YiTian.mContext
                        .getSystemService(Context.APP_OPS_SERVICE);
            }
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,applicationInfo.packageName);
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取应用图标
     * @param appPackage
     * @return
     */
    public static Drawable getAppIcon(String appPackage){
        Drawable icon = null;
        PackageManager manager =YiTian.mContext.getPackageManager();
        try {
            ApplicationInfo info = manager.getApplicationInfo(appPackage,0);
            icon = manager.getApplicationIcon(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return icon;
    }
}
