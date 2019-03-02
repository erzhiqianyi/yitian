package io.github.buniaowanfeng.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableDailyInfo;
import io.github.buniaowanfeng.util.SharePreferenceUtil;
import io.github.buniaowanfeng.util.SpUtil;

/**
 * Created by caofeng on 16-7-14.
 */
public class DailyInfoDao {
    public static final int TYPE_FOURTEEN = 1;
    private static final String TAG = "dailyinfodao";

    /**
     * save daily usage information
     * @param dailyInfo
     * @return
     */
    public static boolean insert(DailyInfo dailyInfo){
        long row = -1;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableDailyInfo.DAY,dailyInfo.day);
        values.put(TableDailyInfo.USED_TIME,dailyInfo.usedTime);
        values.put(TableDailyInfo.NUMBER_OF_APPS,dailyInfo.numberOfApps);
        values.put(TableDailyInfo.NUMBER_OF_TIMES,dailyInfo.numberOfTimes);
        values.put(TableDailyInfo.NUMBER_OF_WAKE,dailyInfo.numberOfWake);
        row =  db.insert(TableDailyInfo.TABLE_NAME,null,values);
//        db.close();
        return row != -1;
    }

    /**
     * update daily usage information
     * @param dailyInfo
     * @return true if update success else false
     */
    public static boolean update(DailyInfo dailyInfo){
        long row = -1;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableDailyInfo.NUMBER_OF_WAKE, dailyInfo.numberOfWake);
        values.put(TableDailyInfo.NUMBER_OF_APPS, dailyInfo.numberOfApps);
        values.put(TableDailyInfo.NUMBER_OF_TIMES, dailyInfo.numberOfTimes);
        values.put(TableDailyInfo.USED_TIME, dailyInfo.usedTime);

        String whereClause = TableDailyInfo.DAY + " =? ";
        String[] whereArgs = {String.valueOf(dailyInfo.day)};
        row = db.update(TableDailyInfo.TABLE_NAME, values,whereClause,whereArgs);
//        db.close();
        return row != -1;
    }
    /**
     * check weather a day is in the table
     * @param day
     * @return if the is already in the table return false else return true
     */
    public static boolean isReported(int day){
        boolean result = false;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableDailyInfo.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableDailyInfo.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        result = cursor.getCount() > 0 ? true : false;
        cursor.close();
//        db.close();
        return result;
    }

    /**
     *  get  saved days
     * @return days if there are more than 14 day,return the last 14 day
     */
    public static List<Integer> getDays(int type){
        List<Integer> days = new ArrayList<>() ;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String order = TableDailyInfo.DAY ;
        Cursor cursor;
        if ( type == TYPE_FOURTEEN){
            cursor =  db.query(TableDailyInfo.TABLE_NAME,null,null,null,null,null,order, "14");
        }else {
             cursor = db.query(TableDailyInfo.TABLE_NAME,null,null,null,null,null,order, null);
        }
        if (cursor.moveToFirst()){
            int dayId = cursor.getColumnIndex(TableDailyInfo.DAY);
            do {
                days.add(cursor.getInt(dayId));
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return days;
    }

    public static DailyInfo getDailyUseInfo(int day){
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.day = day;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableDailyInfo.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableDailyInfo.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            int usedTimeId = cursor.getColumnIndex(TableDailyInfo.USED_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_TIMES);
            int numberOfAppsId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_APPS);
            int numberOfWakeId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_WAKE);
            do {
                dailyInfo.usedTime = cursor.getLong(usedTimeId);
                dailyInfo.numberOfTimes = cursor.getInt(numberOfTimeId);
                dailyInfo.numberOfApps = cursor.getInt(numberOfAppsId);
                dailyInfo.numberOfWake = cursor.getInt(numberOfWakeId);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        dailyInfo.dailyGoal = SpUtil.getInstance().getLong(SpUtil.KEY_DAILY_GOAL);
        return dailyInfo;
    }

    public static List<DailyInfo> getDailyInfo(){
        List<DailyInfo> infos = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String order = TableDailyInfo.DAY + " desc ";
        Cursor cursor = db.query(TableDailyInfo.TABLE_NAME,null,null,null,null,null,order);
        if (cursor.moveToFirst()){
            int dayId = cursor.getColumnIndex(TableDailyInfo.DAY);
            int usedTimeId = cursor.getColumnIndex(TableDailyInfo.USED_TIME);
            int numberOfTimeId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_TIMES);
            int numberOfAppsId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_APPS);
            int numberOfWakeId = cursor.getColumnIndex(TableDailyInfo.NUMBER_OF_WAKE);
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.day = cursor.getInt(dayId);
                dailyInfo.usedTime = cursor.getLong(usedTimeId);
                dailyInfo.numberOfTimes = cursor.getInt(numberOfTimeId);
                dailyInfo.numberOfApps = cursor.getInt(numberOfAppsId);
                dailyInfo.numberOfWake = cursor.getInt(numberOfWakeId);
                infos.add(dailyInfo);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return infos;
    }
}
