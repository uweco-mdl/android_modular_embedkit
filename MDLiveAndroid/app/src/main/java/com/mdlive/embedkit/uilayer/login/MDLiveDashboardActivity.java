package com.mdlive.embedkit.uilayer.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnNotificationCliked;
import com.mdlive.embedkit.uilayer.login.NotificationFragment.NotifyDashboard;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDLiveDashboardActivity extends MDLiveBaseAppcompatActivity implements NotifyDashboard, OnNotificationCliked {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_activity_dashboard);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__main_container, MDLiveDashBoardFragment.newInstance(), MAIN_CONTENT).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(user), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        showEmailConfirmationDialog();
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:

                break;

            // See a Doctor
            case 1:
                startActivityWithClassName(MDLiveGetStarted.class);
                break;

            // MDLive My Health
            case 2:
                startActivityWithClassName(MedicalHistoryActivity.class);
                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:
                onMessageClicked();
                break;

            // Symptom Checker
            case 5:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 6:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 7:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share
            case 8:

                break;
        }
    }

    /* Start of Dashboard icons click listener */
    public void onSeeADoctorNowClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MDLiveGetStarted.class);
        }
    }

    public void onScheduleAVisitClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MDLiveGetStarted.class);
        }
    }

    public void onMyHealthClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MedicalHistoryActivity.class);
        }
    }

    public void onMessageCenterClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MessageCenterActivity.class);
        }
    }

    public void onMdliveAssistClicked(View view) {
        if (!isDrawerOpen()) {
            MdliveUtils.showMDLiveAssistDialog(this);
        }
    }

    public void onSymptomCheckerClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
        }
    }

    public void onMyAccountClicked(View view) {
        if (!isDrawerOpen()) {
            startActivityWithClassName(MyAccountActivity.class);
        }
    }

    /* End of Dashboard icons click listener */

    @Override
    public void onEmailConfirmationClicked() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).loadEmailConfirmationService();
        }
    }

    @Override
    public void onShowNofifyDashboard(Appointment appointment) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).showNotification(appointment);
        }
    }

    @Override
    public void onHideNotifyDashboard() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).hideNotification();
        }
    }

    @Override
    public void onNotificationClicked(Appointment appointment) {
        onAppointmentClicked(appointment);
    }
}
