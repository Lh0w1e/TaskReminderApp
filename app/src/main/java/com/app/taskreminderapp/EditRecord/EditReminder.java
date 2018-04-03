package com.app.taskreminderapp.EditRecord;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.app.taskreminderapp.Archive.Archive;
import com.app.taskreminderapp.AsyncTasks.AsyncTasksArchive;
import com.app.taskreminderapp.AsyncTasks.AsyncTasksReminder;
import com.app.taskreminderapp.BroadcastReceiver.ReminderBroadcastReceiver;
import com.app.taskreminderapp.Constants.Constants;
import com.app.taskreminderapp.Database.MainDatabase;
import com.app.taskreminderapp.Database.MainDatabaseFunction;
import com.app.taskreminderapp.DatabaseTableStructure.ReminderTable;
import com.app.taskreminderapp.Main.Home;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.Utils.DateTimeUtils;

import java.util.Calendar;

@SuppressWarnings("deprecation")
public class EditReminder extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private EditText txtEditTitle, txtEditContent, txtEditTime, txtEditDate;
    private FloatingActionButton btnUpdate;

    //for custom time and date dialog

    private View custom_date_view, custom_time_view;

    private DatePicker custom_date_picker;
    private TimePicker custom_time_picker;

    private Button custom_date_btn_ok;
    private Button custom_time_btn_ok;

    private SQLiteDatabase mDb;

    //for getting intent extras
    private int extra_id;
    private String extra_title, extra_content,
            extra_selected_time, extra_selected_date,
            extra_is_archive, extra_is_deleted,
            extra_date_created;

    //for date/time purposes
    private int extra_year, extra_month, extra_day, extra_hour, extra_minute;

    private String ACTIVITY_NAME = "";

    private boolean hasChangesInDate = false;
    private boolean hasChangesIntime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        initViews();
        initDatabase();
        getAllIntents();
        initSetText();
        initCustomDateAndTimePickerDialog();
        initOnClickListener();

        Log.e("ACTIVITY_NAME", ACTIVITY_NAME);
    }

    private void initCustomDateAndTimePickerDialog() {
        LayoutInflater dateInflater = LayoutInflater.from(this);
        custom_date_view = dateInflater.inflate(R.layout.custom_date_picker, null);
        LayoutInflater timeInflater = LayoutInflater.from(this);
        custom_time_view = timeInflater.inflate(R.layout.custom_time_picker, null);

        custom_date_picker = (DatePicker) custom_date_view.findViewById(R.id.custom_add_date);
        custom_date_btn_ok = (Button) custom_date_view.findViewById(R.id.custom_add_date_ok);
        custom_time_picker = (TimePicker) custom_time_view.findViewById(R.id.custom_add_time);
        custom_time_btn_ok = (Button) custom_time_view.findViewById(R.id.custom_add_time_ok);

        custom_date_picker.init(extra_year, extra_month, extra_day, null);
        custom_time_picker.setCurrentHour(extra_hour);
        custom_time_picker.setCurrentMinute(extra_minute);

    }

    private void initOnClickListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyFields()) {
                    Snackbar.make(coordinatorLayout, "Please complete all fields", Snackbar.LENGTH_SHORT).show();
                } else {
                    updateReminder();
                }
            }
        });

        txtEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEditDate.getText().toString().isEmpty()) {
                    Snackbar.make(coordinatorLayout, "Please select date first", Snackbar.LENGTH_SHORT).show();
                } else {
                    timePickerDialog();
                }
            }
        });

        txtEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog();
            }
        });
    }

    private void updateReminder() {

        int updateId = extra_id;
        String updateTitle = txtEditTitle.getText().toString();
        String updateContent = txtEditContent.getText().toString();
        String updateTime = txtEditTime.getText().toString();
        String updateDate = txtEditDate.getText().toString();

        ContentValues updateFields = new ContentValues();

        updateFields.put(ReminderTable.COLUMN_TITLE, updateTitle);
        updateFields.put(ReminderTable.COLUMN_CONTENT, updateContent);
        updateFields.put(ReminderTable.COLUMN_SELECTED_TIME, updateTime);
        updateFields.put(ReminderTable.COLUMN_SELECTED_DATE, updateDate);
        updateFields.put(ReminderTable.COLUMN_DATE_CREATED, DateTimeUtils.getDateTime());

        boolean result = MainDatabaseFunction.update(updateId, updateFields);

        if (result) {

            if (hasChangesInDate || hasChangesIntime) {
                cancelExistingNotificationRequest(updateId);
                setNewUpdatedNotificationRequest(updateId);
                updateSuccessDialog();
            } else {
                updateSuccessDialog();
            }

        } else {
            Snackbar.make(coordinatorLayout, "Failed to update", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void setNewUpdatedNotificationRequest(final int updateId) {
        Calendar selectedSched = Calendar.getInstance();

        selectedSched.set(custom_date_picker.getYear(),
                custom_date_picker.getMonth(),
                custom_date_picker.getDayOfMonth(),
                custom_time_picker.getCurrentHour(),
                custom_time_picker.getCurrentMinute(),
                0);

        /*selectedSched.set(custom_date_picker.getYear(),
                custom_date_picker.getMonth(),
                custom_date_picker.getDayOfMonth(),
                custom_time_picker.getCurrentHour(),
                custom_time_picker.getCurrentMinute(),
                0);*/
        Intent goAlertReceiver = new Intent(getBaseContext(), ReminderBroadcastReceiver.class);

        goAlertReceiver.putExtra("sched", selectedSched);
        goAlertReceiver.putExtra("id", updateId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                updateId,
                goAlertReceiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedSched.getTimeInMillis(), pendingIntent);

    }

    private void cancelExistingNotificationRequest(final int updateId) {

        Intent goAlertReceiver = new Intent(getBaseContext(), ReminderBroadcastReceiver.class);
        goAlertReceiver.putExtra("id", updateId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                updateId,
                goAlertReceiver,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

    }

    private void updateSuccessDialog() {
        final AlertDialog.Builder success = new AlertDialog.Builder(this);

        success.setTitle(Constants.APP_NAME);
        success.setMessage("Update successful.");
        success.setCancelable(true);
        success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ACTIVITY_NAME.equals(AsyncTasksReminder.class.getSimpleName())) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), Archive.class));
                    finish();
                }
            }
        });

        success.create().show();

    }

    private boolean emptyFields() {
        if (txtEditTitle.getText().toString().isEmpty()
                || txtEditContent.getText().toString().isEmpty()
                || txtEditTime.getText().toString().isEmpty()
                || txtEditDate.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void timePickerDialog() {
        final AlertDialog.Builder timeDialog = new AlertDialog.Builder(this);

        timeDialog.setView(custom_time_view);
        timeDialog.setCancelable(true);

        final AlertDialog dialog = timeDialog.create();

        Calendar now = Calendar.getInstance();
        custom_time_picker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        custom_time_picker.setCurrentMinute(now.get(Calendar.MINUTE));
        //custom_time_picker.setCurrentHour(extra_hour);
        //custom_time_picker.setCurrentMinute(extra_minute);

        custom_time_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(0, 0, 0, custom_time_picker.getCurrentHour(), custom_time_picker.getCurrentMinute());

                int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
                int minute = selectedTime.get(Calendar.MINUTE);

                String status = "AM";

                if (hour > 11) {
                    status = "PM";
                }
                int hour_of_12_hour_format;

                if (hour > 11) {
                    hour_of_12_hour_format = hour - 12;
                } else {
                    hour_of_12_hour_format = hour;
                }

                Calendar current = Calendar.getInstance();
                Calendar currentDateTime = Calendar.getInstance();

                currentDateTime.set(current.get(Calendar.YEAR),
                        current.get(Calendar.MONTH),
                        current.get(Calendar.DAY_OF_MONTH),
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        0);

                Calendar selectedDate = Calendar.getInstance();

                selectedDate.set(custom_date_picker.getYear(),
                        custom_date_picker.getMonth(),
                        custom_date_picker.getDayOfMonth(),
                        custom_time_picker.getCurrentHour(),
                        custom_time_picker.getCurrentMinute(),
                        0);

                if (selectedDate.before(currentDateTime)) {
                    Snackbar.make(coordinatorLayout, "Invalid Time", Snackbar.LENGTH_SHORT).show();
                    txtEditTime.setText("");
                    dialog.dismiss();
                } else {
                    String finalTime = (hour_of_12_hour_format == 0 ? "12:" + (minute <= 9 ? "0" + minute : minute) + " " + status
                            :
                            hour_of_12_hour_format + ":" + (minute <= 9 ? "0" + minute : minute) + " " + status);

                    txtEditTime.setText(finalTime);
                    hasChangesIntime = true;
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void datePickerDialog() {
        final AlertDialog.Builder dateDialog = new AlertDialog.Builder(this);

        dateDialog.setView(custom_date_view);
        dateDialog.setCancelable(true);

        final AlertDialog dialog = dateDialog.create();

        /*final Calendar now = Calendar.getInstance();
        custom_date_picker.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), null);*/

        custom_date_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar current = Calendar.getInstance();

                final Calendar date = Calendar.getInstance();

                date.set(custom_date_picker.getYear(),
                        custom_date_picker.getMonth(),
                        custom_date_picker.getDayOfMonth());

                //if the selected date is from previous date, error message will display
                if (date.compareTo(current) < 0) {
                    invalidDateDialog();
                } else {

                    int month = date.get(Calendar.MONTH);
                    int day = date.get(Calendar.DAY_OF_MONTH);
                    int year = date.get(Calendar.YEAR);

                    String selectedDate = (month + 1) + "/" + (day > 31 ? 1 : day) + "/" + year;

                    txtEditDate.setText(selectedDate);
                    hasChangesInDate = true;
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void invalidDateDialog() {
        final AlertDialog.Builder invalidDate = new AlertDialog.Builder(this);

        invalidDate.setTitle(Constants.APP_NAME);
        invalidDate.setMessage("Previous and Current date cannot be select.");
        invalidDate.setCancelable(false);
        invalidDate.setPositiveButton("OK", null);
        invalidDate.create().show();

    }

    private void initSetText() {
        txtEditTitle.setText(extra_title);
        txtEditContent.setText(extra_content);
        txtEditTime.setText(extra_selected_time);
        txtEditDate.setText(extra_selected_date);
    }

    private void getAllIntents() {

        ACTIVITY_NAME = getIntent().getExtras().getString(Constants.EXTRA_ACTIVITY_NAME);

        extra_id = getIntent().getExtras().getInt(Constants.EXTRA_ID);
        extra_title = getIntent().getExtras().getString(Constants.EXTRA_TITLE);
        extra_content = getIntent().getExtras().getString(Constants.EXTRA_CONTENT);
        extra_selected_time = getIntent().getExtras().getString(Constants.EXTRA_SELECTED_TIME);
        extra_selected_date = getIntent().getExtras().getString(Constants.EXTRA_SELECTED_DATE);
        extra_is_archive = getIntent().getExtras().getString(Constants.EXTRA_IS_ARCHIVE);
        extra_is_deleted = getIntent().getExtras().getString(Constants.EXTRA_IS_DELETED);
        extra_date_created = getIntent().getExtras().getString(Constants.EXTRA_DATE_CREATED);
        extra_year = getIntent().getExtras().getInt(Constants.EXTRA_YEAR);
        extra_month = getIntent().getExtras().getInt(Constants.EXTRA_MONTH);
        extra_day = getIntent().getExtras().getInt(Constants.EXTRA_DAY);
        extra_hour = getIntent().getExtras().getInt(Constants.EXTRA_HOUR);
        extra_minute = getIntent().getExtras().getInt(Constants.EXTRA_MINUTE);

        Log.e("date and time", extra_year + " " + extra_month + " " + extra_day + " " + extra_hour + " " + extra_minute);
    }

    private void initDatabase() {
        mDb = MainDatabase.getInstance(getApplicationContext()).getReadableDatabase();
        MainDatabaseFunction.init(getApplicationContext());
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.edit_coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_edit_24dp);

        txtEditTitle = (EditText) findViewById(R.id.edit_title);
        txtEditContent = (EditText) findViewById(R.id.edit_content);
        txtEditTime = (EditText) findViewById(R.id.edit_time);
        txtEditDate = (EditText) findViewById(R.id.edit_date);
        btnUpdate = (FloatingActionButton) findViewById(R.id.btn_update);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //txtEditTime.setEnabled(false);

    }

    @Override
    public void onBackPressed() {

        if (ACTIVITY_NAME.equals(AsyncTasksReminder.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), Archive.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.default_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (ACTIVITY_NAME.equals(AsyncTasksReminder.class.getSimpleName())) {
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), Archive.class));
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
