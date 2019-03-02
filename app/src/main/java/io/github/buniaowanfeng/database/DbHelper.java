package io.github.buniaowanfeng.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.database.table.TableAllAppInfo;
import io.github.buniaowanfeng.database.table.TableAppUsage;
import io.github.buniaowanfeng.database.table.TableDailyInfo;
import io.github.buniaowanfeng.database.table.TableScreenRecord;
import io.github.buniaowanfeng.database.table.TableTodo;
import io.github.buniaowanfeng.database.table.TableUsageAmount;

/**
 * Created by caofeng on 16-7-9.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static DbHelper sDbHelper;
    public static final String DATABASE_NAME = "yitian.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DbHelper getInstance(){
        if (sDbHelper == null){
            sDbHelper = new DbHelper(YiTian.mContext);
        }
        return sDbHelper;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableScreenRecord.CREATE_TABLE);
        db.execSQL(TableAppUsage.CREATE_TABLE);
        db.execSQL(TableUsageAmount.CREATE_TABLE);
        db.execSQL(TableDailyInfo.CREATE_TABLE);
        db.execSQL(TableAllAppInfo.CREATE_TABLE);
        db.execSQL(TableTodo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
