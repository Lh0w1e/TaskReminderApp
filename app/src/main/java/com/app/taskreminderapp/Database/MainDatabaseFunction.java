package com.app.taskreminderapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.taskreminderapp.DatabaseTableStructure.ReminderTable;

/**
 * Created by Colinares on 11/7/2017.
 */
public class MainDatabaseFunction {

    private static Context mContext;
    private static SQLiteDatabase mDb;

    public static void init(Context context) {
        mContext = context;
        mDb = MainDatabase.getInstance(mContext).getWritableDatabase();
    }

    //method for inserting new data.
    public static void insert(String tableName, ContentValues contentValues){
        mDb.insert(tableName, null, contentValues);
    }

    public static boolean update(int id, ContentValues values){

        int result = mDb.update(ReminderTable.TABLE_NAME, values, ReminderTable.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        if(result > 0){
            return true;
        }else {
            return false;
        }

    }

    public static void delete(long id){
        mDb.delete(ReminderTable.TABLE_NAME, ReminderTable.COLUMN_ID + " = ?", new String[]{String.valueOf((id))});
    }
}
