package com.app.taskreminderapp.AddNewRecord;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
public class AddNewReminder extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private EditText txtAddTitle, txtAddContent, txtAddTime, txtAddDate;
    private FloatingActionButton btnSave;

    //for custom time and date dialog
    private DatePicker custom_date_picker;
    private TimePicker custom_time_picker;

    private Button custom_date_btn_ok;
    private Button custom_time_btn_ok;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reminder);

        initViews();
        initDatabase();
        initOnClickListeners();

    }

    private void initDatabase() {
        mDb = MainDatabase.getInstance(getApplicationContext()).getReadableDatabase();
        MainDatabaseFunction.init(getApplicationContext());
    }

    private void initOnClickListeners() {
        txtAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtAddDate.getText().toString().isEmpty()){
                    Snackbar.make(coordinatorLayout, "Please select date first", Snackbar.LENGTH_SHORT).show();
                }else{
                    timePickerDialog();
                }

            }
        });

        txtAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyFields()) {
                    Snackbar.make(coordinatorLayout, "Please complete all fields", Snackbar.LENGTH_SHORT).show();
                } else {
                    saveNewReminder();
                }
            }
        });
    }

    private void saveNewReminder() {

        String saveTitle = txtAddTitle.getText().toString();
        String saveContent = txtAddContent.getText().toString();
        String saveTime = txtAddTime.getText().toString();
        String saveDate = txtAddDate.getText().toString();
        int year = custom_date_picker.getYear();
        int month = custom_date_picker.getMonth();
        int day = custom_date_picker.getDayOfMonth();
        int hour = custom_time_picker.getCurrentHour();
        int minute = custom_time_picker.getCurrentMinute();

        ContentValues values = new ContentValues();

        values.put(ReminderTable.COLUMN_TITLE, saveTitle);
        values.put(ReminderTable.COLUMN_CONTENT, saveContent);
        values.put(ReminderTable.COLUMN_SELECTED_TIME, saveTime);
        values.put(ReminderTable.COLUMN_SELECTED_DATE, saveDate);
        values.put(ReminderTable.COLUMN_YEAR, year);
        values.put(ReminderTable.COLUMN_MONTH, month + 1);
        values.put(ReminderTable.COLUMN_DAY, day);
        values.put(ReminderTable.COLUMN_HOUR, hour);
        values.put(ReminderTable.COLUMN_MINUTE, minute);
        values.put(ReminderTable.COLUMN_IS_ARCHIVE, Constants.NO);
        values.put(ReminderTable.COLUMN_IS_TRASHED, Constants.NO);
        values.put(ReminderTable.COLUMN_DATE_CREATED, DateTimeUtils.getDateTime());

        MainDatabaseFunction.insert(ReminderTable.TABLE_NAME, values);

        Log.e("add reminder", "save successful");

        saveSuccessfulDialog();
    }

    private void saveSuccessfulDialog() {
        final AlertDialog.Builder success = new AlertDialog.Builder(this);

        success.setTitle(Constants.APP_NAME);
        success.setMessage("Save successful.");
        success.setCancelable(true);
        success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();

                calendar.set(custom_date_picker.getYear(),
                        custom_date_picker.getMonth(),
                        custom_date_picker.getDayOfMonth(),
                        custom_time_picker.getCurrentHour(),
                        custom_time_picker.getCurrentMinute(),
                        0);

                String query = "SELECT * FROM " + ReminderTable.TABLE_NAME +
                        " ORDER BY " + ReminderTable.COLUMN_ID + " DESC LIMIT 1";

                Cursor cursor = mDb.rawQuery(query, null);
                int getIdToPass = 0;

                if (cursor.getCount() > 0) {
                    Log.e("cursor count", cursor.getCount() + "");
                    cursor.moveToFirst();

                    getIdToPass = cursor.getInt(cursor.getColumnIndex(ReminderTable.COLUMN_ID));
                }

                Log.e("get id", getIdToPass + "");

                setNotification(getIdToPass, calendar);

                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        success.create().show();

    }

    private void setNotification(int id, Calendar selectedSched) {

        Intent goAlertReceiver = new Intent(getBaseContext(), ReminderBroadcastReceiver.class);

        goAlertReceiver.putExtra("sched", selectedSched);
        goAlertReceiver.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, goAlertReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedSched.getTimeInMillis(), pendingIntent);

    }

    private boolean emptyFields() {
        if (txtAddTitle.getText().toString().isEmpty()
                || txtAddContent.getText().toString().isEmpty()
                || txtAddTime.getText().toString().isEmpty()
                || txtAddDate.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void datePickerDialog() {
        final AlertDialog.Builder dateDialog = new AlertDialog.Builder(this);

        //set custom layout
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.custom_date_picker, null);

        dateDialog.setView(mView);
        dateDialog.setCancelable(true);

        final AlertDialog dialog = dateDialog.create();

        custom_date_picker = (DatePicker) mView.findViewById(R.id.custom_add_date);
        custom_date_btn_ok = (Button) mView.findViewById(R.id.custom_add_date_ok);

        final Calendar now = Calendar.getInstance();

        custom_date_picker.init(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH), null);

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

                    txtAddDate.setText(selectedDate);
                    txtAddTime.setEnabled(true);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void invalidDateDialog() {
        final AlertDialog.Builder invalidDate = new AlertDialog.Builder(this);

        invalidDate.setTitle(Constants.APP_NAME);
        invalidDate.setMessage("Previous date cannot be select.");
        invalidDate.setCancelable(false);
        invalidDate.setPositiveButton("OK", null);
        invalidDate.create().show();

    }

    private void timePickerDialog() {
        final AlertDialog.Builder timeDialog = new AlertDialog.Builder(this);

        //set custom layout
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.custom_time_picker, null);

        timeDialog.setView(mView);
        timeDialog.setCancelable(true);

        final AlertDialog dialog = timeDialog.create();

        custom_time_picker = (TimePicker) mView.findViewById(R.id.custom_add_time);
        custom_time_btn_ok = (Button) mView.findViewById(R.id.custom_add_time_ok);

        final Calendar now = Calendar.getInstance();
        custom_time_picker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        custom_time_picker.setCurrentMinute(now.get(Calendar.MINUTE));

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
                    txtAddTime.setText("");
                    dialog.dismiss();
                }else{
                    String finalTime = (hour_of_12_hour_format == 0 ? "12:" + (minute <= 9 ? "0" + minute : minute) + " " + status
                            :
                            hour_of_12_hour_format + ":" + (minute <= 9 ? "0" + minute : minute) + " " + status);

                    txtAddTime.setText(finalTime);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();


    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.add_new_reminder_coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_add_circle_24dp);

        txtAddTitle = (EditText) findViewById(R.id.add_title);
        txtAddContent = (EditText) findViewById(R.id.add_content);
        txtAddTime = (EditText) findViewById(R.id.add_time);
        txtAddDate = (EditText) findViewById(R.id.add_date);
        btnSave = (FloatingActionButton) findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
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
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
