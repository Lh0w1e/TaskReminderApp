package com.app.taskreminderapp.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.taskreminderapp.Adapters.ArchiveAdapter;
import com.app.taskreminderapp.Constants.Constants;
import com.app.taskreminderapp.Database.MainDatabase;
import com.app.taskreminderapp.Database.MainDatabaseFunction;
import com.app.taskreminderapp.DatabaseTableStructure.ReminderTable;
import com.app.taskreminderapp.EditRecord.EditReminder;
import com.app.taskreminderapp.Models.ArchiveModel;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.Utils.OnTapListener;

import java.util.ArrayList;

/**
 * Created by Colinares on 11/9/2017.
 */
public class AsyncTasksArchive extends AsyncTask<Void, ArchiveModel, Void> {

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private LinearLayout noRecords;
    private Activity mActivity;

    private ArchiveAdapter mAdapter;
    private ArrayList<ArchiveModel> archiveModels = new ArrayList<>();

    //private int index = 0;

    private int[] reminder_id;
    private String[] reminder_title, reminder_content,
            reminder_selected_time,
            reminder_selected_date,
            reminder_is_archive,
            reminder_is_trashed,
            reminder_date_created;

    private SQLiteDatabase mDb;

    private View custom_preview_view;

    //for custom preview
    private TextView preview_title,
            preview_content,
            preview_selected_time,
            preview_selected_date;

    private LinearLayout linearLayoutEdit, linearLayoutUnArchive, linearLayoutDelete, linearLayoutClose;

    private Animation goFadeOut;


    public AsyncTasksArchive(CoordinatorLayout coordinatorLayout, RecyclerView recyclerView,
                             ProgressDialog progressDialog, LinearLayout noRecords,
                             Activity mActivity) {

        this.coordinatorLayout = coordinatorLayout;
        this.recyclerView = recyclerView;
        this.progressDialog = progressDialog;
        this.noRecords = noRecords;
        this.mActivity = mActivity;

        goFadeOut = AnimationUtils.loadAnimation(this.mActivity, R.anim.fade_out);
    }

    @Override
    protected void onPreExecute() {

        mAdapter = new ArchiveAdapter(archiveModels);
        recyclerView.setAdapter(mAdapter);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        initDatabase();
        loadArchives();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(ArchiveModel... values) {
        archiveModels.add(values[0]);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();

        mAdapter.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(int position) {
                previewDialog(position);
            }
        });

    }

    private void previewDialog(final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity.getApplicationContext());
        custom_preview_view = layoutInflater.inflate(R.layout.custom_preview_archive, null);

        initCustomLayoutViews();

        final AlertDialog.Builder preview = new AlertDialog.Builder(this.mActivity);
        preview.setCancelable(false);
        preview.setView(custom_preview_view);

        displayPreview(position);

        final AlertDialog dialog = preview.create();

        dialog.show();

        initOnClickListener(dialog, position);

    }

    private void initOnClickListener(final AlertDialog dialog, final int position) {
        linearLayoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutEdit.startAnimation(goFadeOut);

                Intent goEdit = new Intent(mActivity.getApplicationContext(), EditReminder.class);

                goEdit.putExtra(Constants.EXTRA_ID, reminder_id[position]);
                goEdit.putExtra(Constants.EXTRA_TITLE, reminder_title[position]);
                goEdit.putExtra(Constants.EXTRA_CONTENT, reminder_content[position]);
                goEdit.putExtra(Constants.EXTRA_SELECTED_TIME, reminder_selected_time[position]);
                goEdit.putExtra(Constants.EXTRA_SELECTED_DATE, reminder_selected_date[position]);
                goEdit.putExtra(Constants.EXTRA_IS_ARCHIVE, reminder_is_archive[position]);
                goEdit.putExtra(Constants.EXTRA_IS_DELETED, reminder_is_trashed[position]);
                goEdit.putExtra(Constants.EXTRA_DATE_CREATED, reminder_date_created[position]);
                goEdit.putExtra(Constants.EXTRA_ACTIVITY_NAME, AsyncTasksArchive.class.getSimpleName());

                mActivity.startActivity(goEdit);
                dialog.dismiss();
                mActivity.finish();

            }
        });
        linearLayoutUnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutUnArchive.startAnimation(goFadeOut);
                //archiveWarningDialog(dialog, position);
                goUnArchive(position);
                dialog.dismiss();
            }
        });
        linearLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutDelete.startAnimation(goFadeOut);
                deleteWarningDialog(dialog, position);
            }
        });
        linearLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutClose.startAnimation(goFadeOut);
                dialog.dismiss();
            }
        });
    }

    private void goUnArchive(final int position) {
        int id = reminder_id[position];

        Log.e("unarchive_reminder_id", id + "");

        ContentValues update = new ContentValues();

        update.put(ReminderTable.COLUMN_IS_ARCHIVE, Constants.NO);

        boolean result = MainDatabaseFunction.update(id, update);

        if (result == true) {
            Snackbar.make(coordinatorLayout, "Unarchived Successful", Snackbar.LENGTH_LONG).show();

            mAdapter.refreshList(archiveModels);
            initDatabase();
            loadArchives();

        } else {
            Snackbar.make(coordinatorLayout, "Unarchived Failed", Snackbar.LENGTH_LONG).show();
        }

    }

    private void deleteWarningDialog(final AlertDialog alertDialog, final int position) {
        final AlertDialog.Builder delete = new AlertDialog.Builder(this.mActivity);

        delete.setTitle("Delete");
        delete.setIcon(R.drawable.ic_warning_24dp);
        delete.setMessage("Are you sure you want to send " + reminder_title[position].toUpperCase() + " to the trash can?");
        delete.setCancelable(true);
        delete.setNegativeButton("Cancel", null);
        delete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goTrash(position);
                alertDialog.dismiss();
            }
        });

        delete.create().show();

    }

    private void goTrash(final int position) {
        int id = reminder_id[position];

        Log.e("trash_reminder_id", id + "");

        ContentValues update = new ContentValues();

        update.put(ReminderTable.COLUMN_IS_TRASHED, Constants.YES);
        update.put(ReminderTable.COLUMN_IS_ARCHIVE, Constants.NO);

        boolean result = MainDatabaseFunction.update(id, update);

        if (result == true) {
            Snackbar.make(coordinatorLayout, "Item has been moved to Trash.", Snackbar.LENGTH_LONG).show();

            mAdapter.refreshList(archiveModels);
            initDatabase();
            loadArchives();

        } else {
            Snackbar.make(coordinatorLayout, "Failed to move in Trash", Snackbar.LENGTH_LONG).show();
        }

    }

    private void displayPreview(final int position) {
        preview_title.setText(reminder_title[position]);
        preview_content.setText("Content : " +"\n"+ reminder_content[position]);
        preview_selected_time.setText("Selected Time : " + reminder_selected_time[position]);
        preview_selected_date.setText("Selected Date : " + reminder_selected_date[position]);
    }

    private void initCustomLayoutViews() {
        preview_title = (TextView) custom_preview_view.findViewById(R.id.archive_preview_title);
        preview_content = (TextView) custom_preview_view.findViewById(R.id.archive_preview_content);
        preview_selected_time = (TextView) custom_preview_view.findViewById(R.id.archive_preview_time);
        preview_selected_date = (TextView) custom_preview_view.findViewById(R.id.archive_preview_date);

        linearLayoutEdit = (LinearLayout) custom_preview_view.findViewById(R.id.archive_preview_edit);
        linearLayoutUnArchive = (LinearLayout) custom_preview_view.findViewById(R.id.archive_preview_unarchive);
        linearLayoutDelete = (LinearLayout) custom_preview_view.findViewById(R.id.archive_preview_delete);
        linearLayoutClose = (LinearLayout) custom_preview_view.findViewById(R.id.archive_preview_close);
    }

    private void loadArchives() {
        int index = 0;

        String query = "SELECT * FROM " + ReminderTable.TABLE_NAME +
                " WHERE " + ReminderTable.COLUMN_IS_ARCHIVE +
                " = " + "'" + Constants.YES + "'" +
                " AND " + ReminderTable.COLUMN_IS_TRASHED +
                " = " + "'"+Constants.NO+"'" +
                " ORDER BY " + ReminderTable.COLUMN_ID +
                " DESC";

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        reminder_id = new int[cursor.getCount()];
        reminder_title = new String[cursor.getCount()];
        reminder_content = new String[cursor.getCount()];
        reminder_selected_time = new String[cursor.getCount()];
        reminder_selected_date = new String[cursor.getCount()];
        reminder_is_archive = new String[cursor.getCount()];
        reminder_is_trashed = new String[cursor.getCount()];
        reminder_date_created = new String[cursor.getCount()];

        if (cursor.getCount() == 0) {
            noRecords.setVisibility(View.VISIBLE);
        } else {
            noRecords.setVisibility(View.INVISIBLE);

            if (cursor.moveToFirst()) {
                do {
                    reminder_id[index] = cursor.getInt(cursor.getColumnIndex(ReminderTable.COLUMN_ID));
                    reminder_title[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_TITLE));
                    reminder_content[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_CONTENT));
                    reminder_selected_time[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_SELECTED_TIME));
                    reminder_selected_date[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_SELECTED_DATE));
                    reminder_is_archive[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_IS_ARCHIVE));
                    reminder_is_trashed[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_IS_TRASHED));
                    reminder_date_created[index] = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_DATE_CREATED));

                    publishProgress(new ArchiveModel(reminder_id[index], reminder_title[index], reminder_content[index],
                            reminder_selected_time[index], reminder_selected_date[index],
                            reminder_is_archive[index], reminder_is_trashed[index],
                            reminder_date_created[index]));

                    index++;
                } while (cursor.moveToNext());
            }

        }
    }

    private void initDatabase() {
        mDb = MainDatabase.getInstance(mActivity).getReadableDatabase();
        MainDatabaseFunction.init(mActivity.getApplicationContext());
    }

}







