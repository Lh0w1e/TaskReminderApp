package com.app.taskreminderapp.BroadcastReceiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.taskreminderapp.Constants.Constants;
import com.app.taskreminderapp.Database.MainDatabase;
import com.app.taskreminderapp.DatabaseTableStructure.ReminderTable;
import com.app.taskreminderapp.Main.Home;
import com.app.taskreminderapp.R;

/**
 * Created by Colinares on 11/7/2017.
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        SQLiteDatabase mDb = MainDatabase.getInstance(context).getReadableDatabase();
        int id = intent.getExtras().getInt("id");

        String query = "SELECT * FROM " + ReminderTable.TABLE_NAME +
                " WHERE " + ReminderTable.COLUMN_ID +
                "=" + id +
                " AND " + ReminderTable.COLUMN_IS_ARCHIVE +
                " = " + "'"+Constants.NO+"'" +
                " AND " + ReminderTable.COLUMN_IS_TRASHED +
                " = " + "'"+Constants.NO+"'";

        Log.e("broadcastReceiver", query);

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_CONTENT));

        Intent forPendingIntent = new Intent(context, Home.class);
        forPendingIntent.putExtra("id", String.valueOf(id));
        forPendingIntent.putExtra("type", 0);

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), id, forPendingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.main_icon)
                .setTicker(Constants.APP_NAME)
                .setContentTitle(title.equals("") ? "" : title)
                .setContentText(content)
                .setStyle(new Notification.BigTextStyle().bigText(content.equals("") ? "" : content))
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(id, notification);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                id,
                forPendingIntent,

                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent1);
        pendingIntent1.cancel();


    }
}
