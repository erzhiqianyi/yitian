package io.github.buniaowanfeng.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableAppUsage;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * Created by caofeng on 16-7-14.
 */
public class AppUsageDao {
    private static final String TAG = " app usage dao";

    /**
     * save used app information
     * @param appInfo
     * @return
     */
    public static boolean insert(AppInfo appInfo){
        long row = -1;

        LogUtil.d(TAG,appInfo.toString());

        if( appInfo == null) {
            return false;
        }

        if (appInfo.appPackage.equals("buniaowanfeng")){
            return false;
        }

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TableAppUsage.APP_NAME,appInfo.appName);
        values.put(TableAppUsage.START_TIME,appInfo.startTime);
        values.put(TableAppUsage.END_TIME,appInfo.endTime);
        values.put(TableAppUsage.USED_TIME,appInfo.usedTime);
        values.put(TableAppUsage.DAY,appInfo.day);
        values.put(TableAppUsage.PACKAGE_NAME, appInfo.appPackage);
        row = db.insert(TableAppUsage.TABLE_NAME, null,values);
//        db.close();
        return row != -1;
    }

    public static List<AppInfo> getAppUsage(int day){
        List<AppInfo> appInfos = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableAppUsage.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableAppUsage.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            int appNameId = cursor.getColumnIndex(TableAppUsage.APP_NAME);
            int startTimeId = cursor.getColumnIndex(TableAppUsage.START_TIME);
            int endTimeId = cursor.getColumnIndex(TableAppUsage.END_TIME);
            int usedTimeId = cursor.getColumnIndex(TableAppUsage.USED_TIME);
            int packageId = cursor.getColumnIndex(TableAppUsage.PACKAGE_NAME);
            do {
                AppInfo appInfo = new AppInfo();
                appInfo.appName = cursor.getString(appNameId);
                appInfo.startTime = cursor.getLong(startTimeId);
                appInfo.usedTime = cursor.getLong(usedTimeId);
                appInfo.appPackage = cursor.getString(packageId);
                appInfo.endTime = cursor.getLong(endTimeId);
                appInfo.day = day;
                appInfos.add(appInfo);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return appInfos;
    }

    public static List<AppInfo> getAppInfo(int day ,String packageName){
        List<AppInfo> appInfos = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableAppUsage.DAY + " =? and " +
                            TableAppUsage.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {String.valueOf(day),packageName};
        Cursor cursor1 = db.query(TableAppUsage.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor1.moveToFirst()){
            int appNameId = cursor1.getColumnIndex(TableAppUsage.APP_NAME);
            int startTimeId = cursor1.getColumnIndex(TableAppUsage.START_TIME);
            int endTimeId = cursor1.getColumnIndex(TableAppUsage.END_TIME);
            int usedTimeId = cursor1.getColumnIndex(TableAppUsage.USED_TIME);
            do {
                AppInfo appInfo = new AppInfo();
                appInfo.appName = cursor1.getString(appNameId);
                appInfo.startTime = cursor1.getLong(startTimeId);
                appInfo.usedTime = cursor1.getLong(usedTimeId);
                appInfo.endTime = cursor1.getLong(endTimeId);
                appInfo.appPackage = packageName;
                appInfo.day = day;
                appInfos.add(appInfo);
            }while (cursor1.moveToNext());
        }
        cursor1.close();
//        db.close();
        return appInfos;
    }

    public static long getStartTime(int day){
        long startTime = 0 ;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableAppUsage.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableAppUsage.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        int startTimeId = cursor.getColumnIndex(TableAppUsage.START_TIME);
        cursor.moveToFirst();
        startTime = cursor.getLong(startTimeId);
        cursor.close();
        return startTime;
    }

}
