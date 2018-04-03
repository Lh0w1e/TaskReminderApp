package com.app.taskreminderapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.taskreminderapp.DatabaseTableStructure.ReminderTable;

/**
 * Created by Colinares on 11/7/2017.
 */
public class MainDatabase extends SQLiteOpenHelper{

    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "TaskReminder.sqlite";

    private static MainDatabase mInstance = null;

    public MainDatabase(Context context){
        super(context, DB_NAME, null,DB_VERSION);
    }

    public static MainDatabase getInstance(Context context){
        if(mInstance == null){
            mInstance = new MainDatabase(context.getApplicationContext());
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReminderTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
