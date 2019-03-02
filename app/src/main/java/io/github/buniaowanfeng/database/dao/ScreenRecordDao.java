package io.github.buniaowanfeng.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.data.ScreenRecord;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableScreenRecord;

/**
 * Created by caofeng on 16-7-9.
 */
public class ScreenRecordDao {

    /**
     *  save screenOn or screenOff
     * @param record
     * @return
     */
    public static boolean insert(ScreenRecord record){
        long row = -1;
        if (record == null)
            return false;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableScreenRecord.DAY,record.day);
        values.put(TableScreenRecord.TYPE,record.type);
        values.put(TableScreenRecord.TIME,record.startTime);
        row = db.insert(TableScreenRecord.TABLE_NAME,null,values);
//        db.close();
        return  row != -1;
    }

    public static int getWakeTimes(int day) {
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        int numberOfWake = 0 ;
        String selection = TableScreenRecord.TYPE + " =? and " +
                            TableScreenRecord.DAY + " =? ";
        String[] selectionArgs = {ScreenRecord.SCREEN_ON, String.valueOf(day)};
        Cursor cursor = db.query(TableScreenRecord.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        numberOfWake = cursor.getCount();
//        db.close();
        return numberOfWake;
    }

    public static List<ScreenRecord> getScreen(int day){
        List<ScreenRecord> records = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String selection = TableScreenRecord.DAY + " =? ";
        String[] selectionArgs = {String.valueOf(day)};
        Cursor cursor = db.query(TableScreenRecord.TABLE_NAME,null, selection,selectionArgs, null,null,null);
        if (cursor.moveToFirst()){
            int typeId = cursor.getColumnIndex(TableScreenRecord.TYPE);
            int timeId = cursor.getColumnIndex(TableScreenRecord.TIME);
            do {
                ScreenRecord record = new ScreenRecord();
                record.type = cursor.getString(typeId);
                record.startTime = cursor.getLong(timeId);
                record.day = day;
                records.add(record);
            }while (cursor.moveToNext());
        }

        cursor.close();


//        db.close();
        return records;
    }
}
