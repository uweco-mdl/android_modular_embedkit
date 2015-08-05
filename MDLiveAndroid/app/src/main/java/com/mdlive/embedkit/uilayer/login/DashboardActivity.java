package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.mdliveassist.MDLiveAssistActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMyHealthActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;

/**
 * Created by venkataraman_r on 7/15/2015.
 */

public class DashboardActivity extends AppCompatActivity implements NavigationDrawerFragment
        .NavigationDrawerCallbacks,NavigationDrawerFragment1.NavigationDrawerCallbacks1 {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private NavigationDrawerFragment1 mNavigationDrawerFragment1;

    private ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//
//        mNavigationDrawerFragment1 = (NavigationDrawerFragment1)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout =  (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                DashboardActivity.this,
                drawerLayout,
                toolbar,
                R.string.ok,
                R.string.action_cancel_calls){

        };

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(drawerLayout.isDrawerOpen(Gravity.RIGHT))
                {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                else if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else
                    drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, HomeFragment.newInstance()).commit();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        drawerLayout.closeDrawer(Gravity.LEFT);
        drawerLayout.closeDrawer(Gravity.RIGHT);
        switch (position) {
            // Home
            case 0:

                break;

            // Talk to a Doctor
            case 1:

                break;

            // Schedule a Visit
            case 2:

                break;

            // My Health
            case 3:
                startActivity(new Intent(getBaseContext(), MDLiveMyHealthActivity.class));
                break;

            // Message Center
            case 4:
                startActivity(new Intent(getBaseContext(), MessageCenterActivity.class));
                break;

            // MDLIVE Assist
            case 5:
                startActivity(new Intent(getBaseContext(), MDLiveAssistActivity.class));
                break;

            // Symptom Checker
            case 6:
                startActivity(new Intent(getBaseContext(), MDLiveSymptomCheckerActivity.class));
                break;

            // My Accounts
            case 7:
                startActivity(new Intent(getBaseContext(), MyAccountActivity.class));
                break;

            // Support
            case 8:
                startActivity(new Intent(getBaseContext(), MDLiveHelpAndSupportActivity.class));
                break;

            // Share this App
            case 9:

                break;

            // Sign Out
            case 10:

                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.drawer_right) {
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
            else if(drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
            else
                drawerLayout.openDrawer(Gravity.RIGHT);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}

