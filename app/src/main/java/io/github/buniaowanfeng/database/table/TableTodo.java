package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-9-20.
 */

public class TableTodo {
    public static final String TABLE_NAME = "todo";
    public static final String ID = "id";
    public static final String START_TIME = "start";
    public static final String END_TIME = "end";
    public static final String TAG = "tag";
    public static final String DESC = "desc";
    public static final String LOCATION = "location";
    public static final String USER_ID = "user_id";
    public static final String PERMISSION_LEVEL = "level";
    public static final String DAY = "day";
    public static final String SYNC = "sync";

    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + ID + " integer  , "
            + START_TIME + " integer , "
            + END_TIME + " integer , "
            + TAG + " text , "
            + DESC + " text , "
            + LOCATION + " location , "
            + USER_ID + " integer , "
            + DAY + " integer , "
            + SYNC + " integer , "
            + PERMISSION_LEVEL + " integer "
            + " ) ";

}
