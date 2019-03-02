package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-7-14.
 */
public class TableAppUsage {
    public static final String TABLE_NAME = "app_usage";
    public static final String ID = "_id";

    /**
     * 应用名字
     */
    public static final String APP_NAME = "name";

    /**
     * 应用启动时间
     */
    public static final String START_TIME = "start_time";

    /**
     * 应用关闭时间
     */
    public static final String END_TIME = "end_time";

    /**
     * 日期
     */
    public static final String DAY = "day";

    public static final String USED_TIME = "time";

    public static final String PACKAGE_NAME = "package_name";
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + " ( "
                    + ID + " integer primary key autoincrement , "
                    + APP_NAME + " text , "
                    + START_TIME + " integer , "
                    + END_TIME + " integer , "
                    + USED_TIME + " integer , "
                    + DAY + " integer , "
                    + PACKAGE_NAME + " text , "
                    + " unique ( " + APP_NAME + " , " + START_TIME + " ) "
                    +" ) ";
    public static final String DROP_TABLE =
            "drop table if exists " + TABLE_NAME;
}
