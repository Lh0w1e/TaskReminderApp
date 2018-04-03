package com.app.taskreminderapp.Archive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.app.taskreminderapp.AsyncTasks.AsyncTasksArchive;
import com.app.taskreminderapp.Main.Home;
import com.app.taskreminderapp.R;

public class Archive extends AppCompatActivity {

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;

    private LinearLayout noArchive;

    private RecyclerView mRecyclerView;

    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        initViews();
        new AsyncTasksArchive(coordinatorLayout, mRecyclerView, progressDialog, noArchive, this).execute();

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_archive_small_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.archive_coordinatorLayout);

        noArchive = (LinearLayout) findViewById(R.id.no_archive);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");

        mRecyclerView = (RecyclerView) findViewById(R.id.archive_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
