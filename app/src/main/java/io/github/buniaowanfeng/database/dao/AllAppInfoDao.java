package io.github.buniaowanfeng.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.data.AppDailyHead;
import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableAllAppInfo;
import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-24.
 */
public class AllAppInfoDao {

    public static boolean insert(AppInfo appInfo){
        long row = -1;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(TableAllAppInfo.APP_NAME,appInfo.appName);
        values.put(TableAllAppInfo.PACKAGE_NAME,appInfo.appPackage);
        row = db.insert(TableAllAppInfo.TABLE_NAME,null,values);
//        db.close();
        return row != -1;
    }

    public static List<AppMessage> getAllApps() {
        List<AppMessage> allApps = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String order = TableAllAppInfo.PACKAGE_NAME;
        Cursor cursor = db.query(TableAllAppInfo.TABLE_NAME, null, null, null, null, null, order);
        if (cursor.moveToFirst()){
            int packgeId = cursor.getColumnIndex(TableAllAppInfo.PACKAGE_NAME);
            do {
                AppMessage message = new AppMessage();
                message.packageName = cursor.getString(packgeId);
                allApps.add(message);
            }while (cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return allApps;
    }

    public static boolean insert(List<AppMessage> needSave) {
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        db.beginTransaction();
        try {
            for (AppMessage appMessage : needSave) {
                ContentValues values = new ContentValues();
                values.put(TableAllAppInfo.APP_NAME, appMessage.appName);
                values.put(TableAllAppInfo.PACKAGE_NAME, appMessage.packageName);
                db.insert(TableAllAppInfo.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
//        db.close();
        return true;
    }

    public static void delete() {
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        db.delete(TableAllAppInfo.TABLE_NAME,null,null);
    }

    public static AppDailyHead  getGoal(String packageName){
        AppDailyHead head = new AppDailyHead();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableAllAppInfo.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {packageName};
        Cursor cursor = db.query(TableAllAppInfo.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            int useTimeId = cursor.getColumnIndex(TableAllAppInfo.GOAL_USE_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableAllAppInfo.GOAL_NUMBER_OF_TIME);
            int appNameId = cursor.getColumnIndex(TableAllAppInfo.APP_NAME);
            do {
                head.appName = cursor.getString(appNameId);
                head.packageName = packageName;
                head.usedTime = DateUtil.longToString(cursor.getLong(useTimeId));
                head.numberOfTimes = cursor.getInt(numberOfTimeId)+"次";

            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return head;
    }

    public static List<AppDailyHead> getAllAppName(){
        List<AppDailyHead> apps = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.query(TableAllAppInfo.TABLE_NAME,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            int appNameId = cursor.getColumnIndex(TableAllAppInfo.APP_NAME);
            int appPackageId = cursor.getColumnIndex(TableAllAppInfo.PACKAGE_NAME);
            do {
                AppDailyHead appDailyHead = new AppDailyHead();
                appDailyHead.appName = cursor.getString(appNameId);
                appDailyHead.packageName = cursor.getString(appPackageId);
                apps.add(appDailyHead);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return apps;
    }

    public static boolean isNewInstalled(AppInfo appInfo){
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String seletion = TableAllAppInfo.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {appInfo.appPackage};
        Cursor cursor = db.query(TableAllAppInfo.TABLE_NAME,null,seletion,selectionArgs,null,null,null);
        if (cursor.getCount() == 0){
            return true;
        }else {
            return false;
        }

    }
}
