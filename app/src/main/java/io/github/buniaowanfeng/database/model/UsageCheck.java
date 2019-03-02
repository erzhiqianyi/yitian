package io.github.buniaowanfeng.database.model;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.CheckResult;
import rx.Observable;

/**
 * Created by caofeng on 16-7-9.
 */
public class UsageCheck {
    private static final String TAG = "usage check";
    private static String prePackage = "";

    /**
     * 检查正在运行的应用，如果切换了应用，返回之前应用的使用情况
     * @return
     */
    public static Observable<CheckResult> checkAppUsage(){
        CheckResult result = new CheckResult();
        long time = System.currentTimeMillis();
        String curPackage = getCurrentUsedPackage(time);
        if (!prePackage.equals(curPackage)){
            result.curTime = time;
            result.prePackage = prePackage;
            result.curPackage = curPackage;
            result.isNull = false;
            prePackage = curPackage;
        }else {
            result.isNull = true;
        }
        return Observable.just(result);
    }

    /**
     * 获取正在使用的应用的包名
     * @param time 当前时间
     * @return
     */
    public static String getCurrentUsedPackage(long time){
        String currentUsedPackage = "buniaowanfeng";
        ActivityManager activityManager = (ActivityManager)
                YiTian.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) {
            /**
             * 5.0以上需要在设置中开启查询应用使用情况的权限
             */
            UsageStatsManager usageStatsManager = (UsageStatsManager)
                    YiTian.mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStatses = usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                            0,time);
            UsageStats  currentUsedStats = null;

            for (UsageStats usageStats : usageStatses){
//                LogUtil.e(TAG,usageStats.getPackageName());
                if (currentUsedStats == null || currentUsedStats.getLastTimeUsed() < usageStats.getLastTimeUsed()){
                    currentUsedStats = usageStats;
                }
            }
            currentUsedPackage = currentUsedStats != null ?
                    currentUsedStats.getPackageName():
                    currentUsedPackage;
        }else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos =
                    activityManager.getRunningTasks(1);
                    activityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
            ComponentName topActivity = runningTaskInfo.topActivity;
            if (topActivity != null){
                currentUsedPackage = topActivity.getPackageName();
            }
        }

        return currentUsedPackage;
    }
}
