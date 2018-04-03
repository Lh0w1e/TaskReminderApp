package com.app.taskreminderapp.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.app.taskreminderapp.About.About;
import com.app.taskreminderapp.Adapters.ReminderAdapter;
import com.app.taskreminderapp.AddNewRecord.AddNewReminder;
import com.app.taskreminderapp.Archive.Archive;
import com.app.taskreminderapp.AsyncTasks.AsyncTasksReminder;
import com.app.taskreminderapp.Help.Help;
import com.app.taskreminderapp.Models.ReminderModel;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.TrashCan.TrashCan;

import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private LinearLayout noRecords;

    private FloatingActionButton btnAdd;
    private RecyclerView mRecyclerView;

    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager layoutManager;


    //for double back press
    private boolean doubleBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        new AsyncTasksReminder(drawer, mRecyclerView, progressDialog, noRecords, this).execute();
        initOnClickListener();

    }

    private void initOnClickListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddNewReminder.class));
                finish();
            }
        });
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        noRecords = (LinearLayout) findViewById(R.id.no_records);

        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (doubleBackPressed) {
            super.onBackPressed();
            return;
        }
        doubleBackPressed = true;
        Snackbar.make(drawer, "Press back again to exit.", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressed = false;
            }
        }, 2000);

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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(getApplicationContext(), Archive.class));
            finish();
        } else if (id == R.id.nav_trash) {
            startActivity(new Intent(getApplicationContext(), TrashCan.class));
            finish();
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(getApplicationContext(), Help.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), About.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
