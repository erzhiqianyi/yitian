package io.github.buniaowanfeng.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableAppUsage;
import io.github.buniaowanfeng.database.table.TableUsageAmount;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * Created by caofeng on 16-7-14.
 */
public class UsageAmountDao {

    private static final String TAG = "usage amount dao";

    /**
     * check weather an app is in the table
     * @param appInfo
     * @return the usage amount of this app if the app is in the table ,else return null
     */
    public static UsageAmount isIn(AppInfo appInfo){
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        UsageAmount amount = null;
        long usedTimes;
        int numberOfTimes;
        String selection = TableAppUsage.APP_NAME + " =? and " +
                TableAppUsage.DAY + " =? ";
        String[] selectionArgs = {appInfo.appName, String.valueOf(appInfo.day)};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            int numberOfTimesID = cursor.getColumnIndex(TableUsageAmount.NUMBER_OF_TIMES);
            do {
                amount = new UsageAmount();
                usedTimes = cursor.getLong(usedTimeId);
                numberOfTimes = cursor.getInt(numberOfTimesID);
                amount.usedTime = usedTimes;
                amount.numberOfTimes = numberOfTimes;
                amount.appName = appInfo.appName;
                amount.day = appInfo.day;
                amount.appPackage = appInfo.appPackage;
            }while (cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return amount;
    }

    /**
     * insert app amount information
     * @param amount
     * @return
     */
    public static boolean insert(UsageAmount amount){
        long row = -1;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();

        if (amount.appPackage.equals("buniaowanfeng") || amount.appPackage.equals("idle")) {
            return false;
        }
        LogUtil.d(TAG,amount.toString());
        values.put(TableUsageAmount.APP_NAME,amount.appName);
        values.put(TableUsageAmount.PACKAGE_NAME,amount.appPackage);
        values.put(TableUsageAmount.DAY,amount.day);
        values.put(TableUsageAmount.USED_TIME,amount.usedTime);
        values.put(TableUsageAmount.NUMBER_OF_TIMES,amount.numberOfTimes);

        row = db.insert(TableUsageAmount.TABLE_NAME,null,values);
//        db.close();
        return row != -1;
    }

    /**
     * update app amount information
     * @param amount
     * @return
     */
    public static boolean update(UsageAmount amount){
        long row = -1;

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableUsageAmount.USED_TIME,amount.usedTime);
        values.put(TableUsageAmount.NUMBER_OF_TIMES,amount.numberOfTimes);

        String whereClause = TableUsageAmount.PACKAGE_NAME + " =? and "
                + TableUsageAmount.DAY + " =? ";
        String[] whereArgs = {amount.appPackage, String.valueOf(amount.day)};
        row = db.update(TableUsageAmount.TABLE_NAME,values,whereClause,whereArgs);
//        db.close();
        return row != -1;
    }

    public static DailyInfo countDailyInfo(int day){
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.DAY + " =? and " +
                            TableUsageAmount.APP_NAME + " != ? ";
        String[] selectionArgs = {String.valueOf(day),AppInfo.APP_IDLE};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        DailyInfo dailyInfo = new DailyInfo();
        int numberOfApps = cursor.getCount();
        int numberOfTimes = 0 ;
        long usedTime = 0;
        int numberOfWake = ScreenRecordDao.getWakeTimes(day);
        if (cursor.moveToFirst()){
            int numberOfTimesId = cursor.getColumnIndex(TableUsageAmount.NUMBER_OF_TIMES);
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            do {
                numberOfTimes += cursor.getInt(numberOfTimesId);
                usedTime += cursor.getLong(usedTimeId);
            }while (cursor.moveToNext());
        }
        dailyInfo.day = day;
        dailyInfo.usedTime = usedTime;
        dailyInfo.numberOfApps = numberOfApps;
        dailyInfo.numberOfTimes = numberOfTimes;
        dailyInfo.numberOfWake = numberOfWake;
        cursor.close();
//        db.close();

        return dailyInfo;
    }

    public static long getLauncherUsedTime(int day){
        long usedTime = 0;

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.APP_NAME + " =? and " +
                            TableUsageAmount.DAY + " =? ";
        String[] selectionArgs = {AppInfo.APP_IDLE, String.valueOf(day)};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            do {
                usedTime = cursor.getLong(usedTimeId);
            }while (cursor.moveToNext());
        }
        LogUtil.d(TAG,"used time " + usedTime);
        cursor.close();
//        db.close();
        return usedTime;
    }

    public static List<UsageAmount> getUsage(int day) {
        List<UsageAmount> usages = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs
                ,null,null,null);
        if (cursor.moveToFirst()){
            int appNameId = cursor.getColumnIndex(TableUsageAmount.APP_NAME);
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableUsageAmount.NUMBER_OF_TIMES);
            int packageId = cursor.getColumnIndex(TableUsageAmount.PACKAGE_NAME);
            do {
                UsageAmount usageAmount = new UsageAmount();
                usageAmount.day = day;
                usageAmount.appName = cursor.getString(appNameId);
                usageAmount.usedTime = cursor.getLong(usedTimeId);
                usageAmount.numberOfTimes = cursor.getInt(numberOfTimeId);
                usageAmount.appPackage = cursor.getString(packageId);
                usageAmount.usedTimes = DateUtil.longToMessage(usageAmount.usedTime);
                usages.add(usageAmount);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return usages;
    }

    public  static List<UsageAmount> getSingleAppUsage(String packageName,String appName){
        List<UsageAmount> usages = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {String.valueOf(packageName)};
        String order = TableUsageAmount.DAY + " desc ";
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs
                ,null,null,order);
        if (cursor.moveToFirst()){
            int dayId = cursor.getColumnIndex(TableUsageAmount.DAY);
            int appNameId = cursor.getColumnIndex(TableUsageAmount.APP_NAME);
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableUsageAmount.NUMBER_OF_TIMES);
            do {
                UsageAmount usageAmount = new UsageAmount();
                usageAmount.day = cursor.getInt(dayId);
                usageAmount.appName = cursor.getString(appNameId);
                usageAmount.usedTime = cursor.getLong(usedTimeId);
                usageAmount.numberOfTimes = cursor.getInt(numberOfTimeId);
                usageAmount.appPackage = packageName;
                usageAmount.usedTimes = DateUtil.longToMessage(usageAmount.usedTime);
                usages.add(usageAmount);
            }while (cursor.moveToNext());
        }

//        cursor.close();
        db.close();
        if (usages.size() == 0){
            UsageAmount usageAmount = new UsageAmount();
            usageAmount.appName = appName;
            usageAmount.appPackage = packageName;
            usages.add(usageAmount);
        }
        return usages;
    }

    public static List<Integer> getDays(String packageName){
        List<Integer> days = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {String.valueOf(packageName)};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs
                ,null,null,null);
        if (cursor.moveToFirst()){
            int dayId = cursor.getColumnIndex(TableUsageAmount.DAY);
            do {
                days.add(cursor.getInt(dayId));
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return days;
    }

    public static UsageAmount getUsageAmount(int day,String packageName){
        UsageAmount amount = new UsageAmount();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableUsageAmount.DAY + " =? and "
                + TableUsageAmount.PACKAGE_NAME + " =? ";
        String[] selectionArgs = {String.valueOf(day),packageName};
        Cursor cursor = db.query(TableUsageAmount.TABLE_NAME,null,selection,selectionArgs
                ,null,null,null);
        if (cursor.moveToFirst()){
            int usedTimeId = cursor.getColumnIndex(TableUsageAmount.USED_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableUsageAmount.NUMBER_OF_TIMES);
            do {
                amount.usedTime = cursor.getLong(usedTimeId);
                amount.numberOfTimes = cursor.getInt(numberOfTimeId);
            }while (cursor.moveToNext());
        }
        amount.usedTimes = DateUtil.longToDayInfo(amount.usedTime);
        amount.average = DateUtil.longToDayInfo((long) (amount.usedTime * 1.0 / amount.numberOfTimes));

        cursor.close();
//        db.close();
        return amount;
    }
}

