package io.github.buniaowanfeng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.buniaowanfeng.service.YiTianService;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * 接收解锁广播, 启动服务
 */
public class UserPresentReceiver extends BroadcastReceiver {
    private static final String TAG = "user_present_receiver";
    public UserPresentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Androids.isYiTianServiceRunning(context)){
            LogUtil.i(TAG,"服务正在运行");
        }else {
            LogUtil.i(TAG,"服务已经停止,重新启动服务");
            YiTianService.startService(context);
        }
    }
}
