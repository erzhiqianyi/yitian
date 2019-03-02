package io.github.buniaowanfeng.database.table;

/**
 * Created by caofeng on 16-7-9.
 * screen record table
 */
public class TableScreenRecord {
    public static final String TABLE_NAME = "screen_usage";
    public static final String ID = "_id";

    /**
     * the type of screen record ,on or off
     */
    public static final String TYPE = "type";

    public static final String TIME = "time";
    public static final String DAY = "day";

    public static final String CREATE_TABLE =
            " create table " + TABLE_NAME + " ( "
                    + ID + " integer primary key autoincrement , "
                    + TYPE + " text , "
                    + TIME + " long , "
                    + DAY + " int , "
                    + " unique ( " + TIME + " ) "
                    + " ) ";
    public static final String DROP_TABLE =
            "drop table if exists " + TABLE_NAME;
}
