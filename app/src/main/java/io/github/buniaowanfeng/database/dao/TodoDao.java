package io.github.buniaowanfeng.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.database.DbHelper;
import io.github.buniaowanfeng.database.table.TableTodo;
import io.github.buniaowanfeng.todo.data.model.TodoSave;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;

/**
 * Created by caofeng on 16-9-20.
 */

public class TodoDao {
    private static final String TAG = "tododao";
    private static SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

    public static boolean insertTodo(ArrayList<TodoSave> saves){
        boolean result = false;
        db.beginTransaction();
        try {
            for (TodoSave save : saves){
                db.execSQL("insert into " + TableTodo.TABLE_NAME + " ( "
                        + TableTodo.ID + " , "
                        + TableTodo.START_TIME + " , "
                        + TableTodo.END_TIME + " , "
                        + TableTodo.TAG + " , "
                        + TableTodo.DESC + " , "
                        + TableTodo.LOCATION + " , "
                        + TableTodo.USER_ID + " , "
                        + TableTodo.DAY + " , "
                        + TableTodo.PERMISSION_LEVEL +  " ) "
                        + " values ( ?,?,?,?,?,?,?,?,? ) ",new String[]{
                        String.valueOf(save.id),
                        String.valueOf(save.startTime),
                        String.valueOf(save.endTime),
                        save.tag,save.description,save.location,
                        String.valueOf(save.userId),
                        String.valueOf(save.day),
                        String.valueOf(save.level)
                });
            }
            db.setTransactionSuccessful();
        }catch (Exception  e){
            result = false;
        }finally {
            db.endTransaction();
            result = true;
        }

        return result;
    }

    public static ArrayList<TodoSave> getTodo(int day){
        String query = " select * from todo  where day = " +day ;
        return dealCurcor(query,null);
    }

    public static ArrayList<TodoSave> getUnSyncedTodo(){
        String querySql = " select * from todo where sync = 0 ";
        return dealCurcor(querySql,null);
    }

    public static ArrayList<TodoSave> getAllTodo(){
        String querySql = " select * from todo ";
        return dealCurcor(querySql,null);
    }

    public static  ArrayList<TodoSave> getChangedTodo() {
        String querySql = " select * from todo where sync = 2";
        return dealCurcor(querySql,null);
    }
    public static void updateUnSynced(int newState,int oldState){
        String updateSql = " update todo set sync = " + newState +"  where sync = " + oldState ;
        db.execSQL(updateSql);
    }

    public static void todoChanged(TodoSave todo){
        LogUtil.d(TAG," changed todo "+todo.toString());
        String updateSql = " update todo set "
                + " start = " + todo.startTime
                + " , end =  " + todo.endTime
                + " , tag = " + "'" + todo.tag + "'"
                + " , location = " + "'" + todo.location + "'"
                + " , desc = " + "'" +todo.description+"'"
                + " , level = " + todo.level
                + " , day = " + todo.day
                + " , sync = " + 2
                + " where id = " + todo.id
                + " and user_id = " + todo.userId;
        LogUtil.d(TAG," sql " + updateSql);
        db.execSQL(updateSql);
    }
    public static void clear(){
        DbHelper.getInstance().getWritableDatabase().execSQL("delete from " + TableTodo.TABLE_NAME);
        SpUtil.getInstance().putInt(SpUtil.KEY_QUERY_ID,0);
    }

    public static void initTodoDao(){
        db.execSQL("delete from " + TableTodo.TABLE_NAME);
        String sql =  " insert into todo "
                +" ( " + " start , end ,day ,id )"
                +" select  start_time,end_time,day ,_id "
                +" from app_usage "
                +" where time > 300000 and package_name ='idle' ";

        int user_id = SpUtil.getInstance().getInt(SpUtil.KEY_USER_ID);
        String updateSql = " update todo set "
                + " user_id = " + user_id
                + " , sync  = 0 ";
        try {
            db.execSQL(sql);
            db.execSQL(updateSql);
            saveLastId();
        }catch (Exception e){
            LogUtil.e(TAG," make new todo failed");
        }

    }

    /**
     * make new todo from table app usage
     */
    public static void makeNewTodo() {
        int queryId = SpUtil.getInstance().getInt(SpUtil.KEY_QUERY_ID);
        int day = DateUtil.day(System.currentTimeMillis());
        LogUtil.d(TAG," query id " + queryId + " day " + 0);
        if (queryId == 0){
            initTodoDao();
        }else {
            SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
            String insertSql = " insert into todo "
                    +" ( " + " start , end ,day ,id )"
                    +" select  start_time,end_time,day ,_id "
                    +" from app_usage "
                    +" where time > 300000 and package_name ='idle' "
                    +" and _id > " + queryId
                    +" and day = " + day;

            int user_id = SpUtil.getInstance().getInt(SpUtil.KEY_USER_ID);
            String updateSql = " update todo set "
                    + " user_id = " + user_id
                    + " , sync  = 0 "
                    + " where  id > " + queryId
                    + " and day = " + day;

            try {
                db.execSQL(insertSql);
                db.execSQL(updateSql);
                saveLastId();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, " make new todo failed");
            }
        }
    }

    public static void saveLastId(){
        Cursor cursor = db.rawQuery("select * from todo order by id desc limit 1",null);
        if (cursor.moveToFirst()){
            SpUtil.getInstance().putInt(SpUtil.KEY_QUERY_ID,cursor.getInt(0));
        }
        cursor.close();
    }

    public static ArrayList<TodoSave> dealCurcor(String query,String[] selectionArgs){
        Cursor cursor = db.rawQuery(query,selectionArgs);
        ArrayList<TodoSave> todos = new ArrayList<>();
        if (cursor.moveToFirst()){
            int idId = cursor.getColumnIndex(TableTodo.ID);
            int startTimeId = cursor.getColumnIndex(TableTodo.START_TIME);
            int endTimeId = cursor.getColumnIndex(TableTodo.END_TIME);
            int tagId = cursor.getColumnIndex(TableTodo.TAG);
            int descId = cursor.getColumnIndex(TableTodo.DESC);
            int locationId = cursor.getColumnIndex(TableTodo.LOCATION);
            int userId = cursor.getColumnIndex(TableTodo.USER_ID);
            int permiId = cursor.getColumnIndex(TableTodo.PERMISSION_LEVEL);
            int dayId = cursor.getColumnIndex(TableTodo.DAY);
            int syncId = cursor.getColumnIndex(TableTodo.SYNC);
            do {
                TodoSave todo = new TodoSave();
                todo.id = cursor.getLong(idId);
                todo.startTime = cursor.getLong(startTimeId);
                todo.endTime = cursor.getLong(endTimeId);
                todo.tag = cursor.getString(tagId);
                todo.description = cursor.getString(descId);
                todo.location = cursor.getString(locationId);
                todo.userId= cursor.getInt(userId);
                todo.level = cursor.getInt(permiId);
                todo.day = cursor.getInt(dayId);
                todo.sync = cursor.getInt(syncId);
                LogUtil.d(TAG,todo.toString());
                LogUtil.d(TAG," day " + DateUtil.day(todo.id)+" "+DateUtil.day(todo.startTime));
                todos.add(todo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return todos;
    }

    public static void insertTodo(AppInfo appInfo) {
        int userId = SpUtil.getInstance().getInt(SpUtil.KEY_USER_ID);
        String insertSql = " insert into todo "
                + " ( id , start , end , day , sync ,user_id  , level ) "
                + " values "
                + " ( " +  appInfo.startTime
                + " , " + appInfo.startTime
                + " , " + appInfo.endTime
                + " , " + appInfo.day
                + " , " + 0
                + " , " + userId
                + " , " + 0
                + " ) ";
        LogUtil.d(TAG, insertSql);
        db.execSQL(insertSql);
    }

    public static void insertTodo(TodoSave todo) {
        int userId = SpUtil.getInstance().getInt(SpUtil.KEY_USER_ID);
        String insertSql = " insert into todo "
                + " ( id , start , end , day , sync ,user_id ,level ) "
                + " values "
                + " ( " +  todo.startTime
                + " , " + todo.startTime
                + " , " + todo.endTime
                + " , " + todo.day
                + " , " + 0
                + " , " + userId
                + " , " + 0
                + " ) ";
        LogUtil.d(TAG, insertSql);
        db.execSQL(insertSql);

    }


}
