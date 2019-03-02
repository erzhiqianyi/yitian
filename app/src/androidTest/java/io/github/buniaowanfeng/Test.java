package io.github.buniaowanfeng;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.dao.TodoDao;

/**
 * Created by caofeng on 16-9-20.
 */

public class Test extends AndroidTestCase {

    public void test_get_new(){
        assertEquals(8,TodoDao.getTodo(0).size());
    }

    public void test_get_unsynced(){
        assertEquals(8,TodoDao.getUnSyncedTodo().size());
    }


    public void test_get_all_todo(){
        assertEquals(340,TodoDao.getAllTodo().size());
    }

    public void test_get_todo_by_day(){
        assertEquals(10,TodoDao.getTodo(20160926).size());
    }
    public void test_update(){
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        String sql = " update todo set sync = 0 where sync = 2 ";
        db.execSQL(sql);
    }


}
