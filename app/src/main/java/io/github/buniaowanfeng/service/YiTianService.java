package io.github.buniaowanfeng.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.presententer.CheckUsagePresenter;
import io.github.buniaowanfeng.ui.activity.MainActivity;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.ICheckUsagePresenter;
import io.github.buniaowanfeng.view.INotification;

public class YiTianService extends Service implements INotification{
    private static final String TAG = "YiTian";

    /**
     * 前台通知编号,不能为0
     */
    private static final int FOREGROUND_ID = 1;

    /**
     * 屏幕点亮广播接收器
     */
    private ScreenOnReceiver screenOnReceiver;

    /**
     * 屏幕关闭广播接收器
     */
    private ScreenOffReceiver screenOffReceiver;

    private ICheckUsagePresenter checkUsagePresenter;

    /**
     * 定时器，隔一秒中查询一下当前正在使用的应用
     */
    private Handler mHandler = new Handler();

    /**
     * 定时器，隔一秒中查询一下当前正在使用的应用
     */
    Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            checkUsagePresenter.check();
            mHandler.postDelayed(this,1000);
        }
    };

    public YiTianService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        LogUtil.d(TAG,"start command");
        checkUsagePresenter = new CheckUsagePresenter(this);
        createNotification();
        mHandler.postDelayed(checkRunnable,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 注册广播
     */
    public void registerReceiver(){
        screenOnReceiver = new ScreenOnReceiver();
        IntentFilter filterScreenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnReceiver,filterScreenOn);

        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filterScreenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver,filterScreenOff);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(screenOffReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void startService(Context context){
        context.startService(new Intent(context, YiTianService.class));
    }

    @Override
    public void updateNotification(DailyInfo dailyInfo) {
        LogUtil.d(TAG,dailyInfo.toString());
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(FOREGROUND_ID,buildNotification(dailyInfo));
    }

    private void createNotification(){
        startForeground(FOREGROUND_ID,buildNotification(new DailyInfo()));
    }

    private Notification buildNotification(DailyInfo dailyInfo){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setTicker("一天");
        builder.setSmallIcon(R.drawable.ic_stat_name);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.item_notification);

        String usedTime = DateUtil.longToStringNoS(dailyInfo.usedTime);
        String numberOfWake = dailyInfo.numberOfWake + "次";
        String numberOfApps = dailyInfo.numberOfApps + "个";
        String numberOfTimes = dailyInfo.numberOfTimes + "次";
        int progress = (int) (dailyInfo.usedTime * 1.0 / 86400000 * 100) ;

        LogUtil.d(TAG,dailyInfo.toString() + " 进度 " + progress);
        remoteViews.setProgressBar(R.id.pb_used_time,100,progress,false);
        remoteViews.setTextViewText(R.id.tv_used_time,usedTime);
        remoteViews.setTextViewText(R.id.tv_number_of_wake,numberOfWake);
        remoteViews.setTextViewText(R.id.tv_number_of_apps,numberOfApps);
        remoteViews.setTextViewText(R.id.tv_number_of_times,numberOfTimes);

        builder.setContent(remoteViews);
        return builder.build();
    }
    /**
     * 接收屏幕点亮广播
     */
    public class ScreenOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG,"屏幕点亮,开启查询");
            checkUsagePresenter.screenOn();
            mHandler.postDelayed(checkRunnable,1000);
        }
    }

    /**
     * 接受屏幕关闭广播
     */
    public class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG,"屏幕关闭,停止查询");
            mHandler.removeCallbacks(checkRunnable);
            checkUsagePresenter.screenOff();
        }
    }

}
