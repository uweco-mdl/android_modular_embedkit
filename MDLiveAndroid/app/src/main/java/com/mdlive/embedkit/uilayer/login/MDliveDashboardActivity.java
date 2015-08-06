package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDliveDashboardActivity extends AppCompatActivity {
    private static final String DASH_BOARD = "dash_board";
    private static final String LEFT_MENU = "left_menu";
    private static final String RIGHT_MENU = "right_menu";

    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_activity_dashboard);

        setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__main_container, MDLiveDashBoardFragment.newInstance(), DASH_BOARD).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, MDLiveDashBoardFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, MDLiveDashBoardFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    /* Start of Drawer click listeners */
    public void onLeftDrawerClicked(View view) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void onRightDrawerClicked(View view) {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            mDrawerLayout.openDrawer(Gravity.END);
        }
        else if(mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
        }
        else {
            mDrawerLayout.openDrawer(Gravity.END);
        }
    }
    /* End of Drawer click listeners */

    /* Start of Dashboard icons click listener */
    public void onTalkToDoctorClicked(View view) {

    }

    public void onScheduleAVisitClicked(View view) {
        startActivity(new Intent(getBaseContext(), MDLiveGetStarted.class));
    }

    public void onMyHealthClicked(View view) {

    }

    public void onMessageCenterClicked(View view) {

    }

    public void onMdliveAssistClicked(View view) {

    }

    public void onSymptomChecker(View view) {

    }
    /* End of Dashboard icons click listener */
}
