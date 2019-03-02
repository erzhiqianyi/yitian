package io.github.buniaowanfeng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.database.dao.AllAppInfoDao;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.ImageUtil;
import io.github.buniaowanfeng.util.LogUtil;

public class InstallReceiver extends BroadcastReceiver {
    public InstallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        LogUtil.d("test ", " installed");
        if (intent.getAction().equals(Intent.ACTION_INSTALL_PACKAGE)) {
            String packageName = intent.getDataString();
            AppInfo appInfo = new AppInfo();
            appInfo.appPackage = packageName;
            appInfo.appName = Androids.getAppName(packageName);
            AllAppInfoDao.insert(appInfo);
            AppMessage appMessage = new AppMessage();
            appMessage.packageName = packageName;
            appMessage.appIcon = Androids.getAppIcon(packageName);
            ImageUtil.saveIcon(appMessage);
        }
    }
}
