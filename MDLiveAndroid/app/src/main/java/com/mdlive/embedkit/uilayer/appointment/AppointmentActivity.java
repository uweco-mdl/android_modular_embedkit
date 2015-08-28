package com.mdlive.embedkit.uilayer.appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class AppointmentActivity extends MDLiveBaseAppcompatActivity {
    private static final String APPOINTMENT_TAG = "APPOINTMENT";

    public static Intent getAppointmentIntent(final Context context, final Appointment appointment) {
        final Intent intent = new Intent(context, AppointmentActivity.class);
        intent.putExtra(APPOINTMENT_TAG, appointment);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        setTitle("");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.appointment_details).toUpperCase());
        }

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(APPOINTMENT_TAG) != null) {
                final Appointment appointment = getIntent().getExtras().getParcelable(APPOINTMENT_TAG);

                getSupportFragmentManager().
                        beginTransaction().
                        add(R.id.main_container, AppointmentFragment.newInstance(appointment), MAIN_CONTENT).
                        commit();
            }

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
                startActivityWithClassName(MDLiveGetStarted.class);
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

    public void onBackClicked(View view) {
        finish();
    }

    public void onCallClicked(View view) {
        MdliveUtils.showMDLiveHelpAndSupportDialog(this);
    }

    public void onStartAppointmentClicked(View view) {
        startActivityWithClassName(MDLiveWaitingRoom.class);
    }

    public void onCancelAppointmentClicked(View view) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof  AppointmentFragment) {
            ((AppointmentFragment) fragment).onCancelAppointmentClicked();
        }
    }
}
