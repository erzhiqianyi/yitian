package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-7-14.
 */
public class TableUsageAmount {

    public static final String TABLE_NAME = "usage_amount";

    public static final String DAY = "day";

    public static final String APP_NAME = "name";

    public static final String USED_TIME = "used_time";

    public static final String NUMBER_OF_TIMES = "number_of_times";

    public static final String PACKAGE_NAME = "package_name";

    public static final String CREATE_TABLE =
            " create table " + TABLE_NAME + " ( "
                    + DAY + " text , "
                    + APP_NAME + " text , "
                    + USED_TIME + " integer , "
                    + NUMBER_OF_TIMES + " integer , "
                    + PACKAGE_NAME + " text , "
                    + "unique ( " + DAY + " , " + PACKAGE_NAME + " ) "
                    + " ) ";
    public static final String DROP_TABLE =
            "drop table if exists " + TABLE_NAME;

}
