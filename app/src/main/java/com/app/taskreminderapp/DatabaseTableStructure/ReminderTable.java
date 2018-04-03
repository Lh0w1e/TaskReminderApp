package com.app.taskreminderapp.DatabaseTableStructure;

/**
 * Created by Colinares on 11/7/2017.
 */
public class ReminderTable {

    public static final String TABLE_NAME = "reminder_table";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SELECTED_TIME = "selected_time";
    public static final String COLUMN_SELECTED_DATE = "selected_date";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_IS_ARCHIVE = "is_archived";
    public static final String COLUMN_IS_TRASHED = "is_trashed";
    public static final String COLUMN_DATE_CREATED = "date_created";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_SELECTED_TIME + " TEXT, " +
                    COLUMN_SELECTED_DATE + " TEXT, " +
                    COLUMN_YEAR + " INTEGER, " +
                    COLUMN_MONTH + " INTEGER, " +
                    COLUMN_DAY + " INTEGER, " +
                    COLUMN_HOUR + " INTEGER, " +
                    COLUMN_MINUTE + " INTEGER, " +
                    COLUMN_IS_ARCHIVE + " TEXT, " +
                    COLUMN_IS_TRASHED + " TEXT, " +
                    COLUMN_DATE_CREATED + " TEXT " +
            ")";


}
