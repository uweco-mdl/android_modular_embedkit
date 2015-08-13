package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDliveDashboardActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        MDLiveDashBoardFragment.SendNotification {
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
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.openDrawer(GravityCompat.END);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }
    /* End of Drawer click listeners */

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerLayout.closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:

                break;

            // See a Doctor
            case 1:

                break;

            // MDLIVE Assist
            case 2:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 3:
                startActivityWithClassName(MessageCenterActivity.class);
                break;

            // Symptom Checker
            case 4:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 5:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Symptom Checker
            case 6:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 7:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 8:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share this App
            case 9:

                break;
        }
    }

    /* Start of Dashboard icons click listener */
    public void onSeeADoctorNowClicked(View view) {

    }

    public void onScheduleAVisitClicked(View view) {
        startActivityWithClassName(MDLiveGetStarted.class);
    }

    public void onMyHealthClicked(View view) {

    }

    public void onMessageCenterClicked(View view) {
        startActivityWithClassName(MessageCenterActivity.class);
    }

    public void onMdliveAssistClicked(View view) {
        MdliveUtils.showMDLiveAssistDialog(this);
    }

    public void onSymptomCheckerClicked(View view) {
        startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
    }

    public void onMyAccountClicked(View view) {
        startActivityWithClassName(MyAccountActivity.class);
    }

    public void onSignoutClicked(View view) {

    }
    /* End of Dashboard icons click listener */

    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(DASH_BOARD).
                replace(R.id.dash_board__main_container, EmailConfirmFragment.newInstance()).
                commit();
    }

    private void startActivityWithClassName(final Class clazz) {
        startActivity(new Intent(getBaseContext(), clazz));
    }

    @Override
    public void sendNotification(UserBasicInfo userBasicInfo) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(RIGHT_MENU);
        if (fragment != null && fragment instanceof  NotificationFragment) {
            ((NotificationFragment) fragment).setNotification(userBasicInfo);
        }
    }
}
