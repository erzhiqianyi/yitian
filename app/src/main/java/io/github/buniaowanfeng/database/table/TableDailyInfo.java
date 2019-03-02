package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-7-14.
 */
public class TableDailyInfo {
    public static final String TABLE_NAME = "daily_info";

    public static final String DAY = "day";

    public static final String USED_TIME = "used_time";

    public static final String NUMBER_OF_APPS = "number_of_apps";

    public static final String NUMBER_OF_TIMES = "number_of_times";

    public static final String NUMBER_OF_WAKE = "number_of_wake";

    public static final String CREATE_TABLE =
            " create table " + TABLE_NAME + " ( "
                    + DAY + " text , "
                    + USED_TIME + " integer , "
                    + NUMBER_OF_APPS + " integer , "
                    + NUMBER_OF_TIMES + " integer , "
                    + NUMBER_OF_WAKE + " integer , "
                    + " unique ( " + DAY + " ) "
                    + " ) ";
    public static final String DROP_TABLE =
            " drop table if exists " + TABLE_NAME;
}
