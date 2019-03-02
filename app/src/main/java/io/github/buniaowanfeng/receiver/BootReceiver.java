package io.github.buniaowanfeng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.buniaowanfeng.service.YiTianService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        YiTianService.startService(context);
    }
}
