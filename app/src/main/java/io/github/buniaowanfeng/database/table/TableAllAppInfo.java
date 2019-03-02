package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-7-24.
 */
public class TableAllAppInfo {
    public static final String TABLE_NAME = "apps";
    public static final String ID = "_id";

    /**
     * 应用名字
     */
    public static final String APP_NAME = "name";

    /**
     * 应用包名
     */
    public static final String PACKAGE_NAME = "package_name";

    public static final String GOAL_USE_TIME = "use_time";

    public static final String GOAL_NUMBER_OF_TIME = "number_of_time";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + " ( "
                    + ID + " integer primary key autoincrement , "
                    + APP_NAME + " text , "
                    + PACKAGE_NAME + " text , "
                    + GOAL_USE_TIME + " integer , "
                    + GOAL_NUMBER_OF_TIME + " integer , "
                    + " unique ( " + PACKAGE_NAME + " ) "
                    +" ) ";

    public static final String ADD_USE_TIME = "alter table " + TABLE_NAME
            + " add column " + GOAL_USE_TIME + " integer ";
    public static final String ADD_NUMBER_OF_TIME = "alter table " + TABLE_NAME
            + " add column " + GOAL_NUMBER_OF_TIME + " integer ";

}
