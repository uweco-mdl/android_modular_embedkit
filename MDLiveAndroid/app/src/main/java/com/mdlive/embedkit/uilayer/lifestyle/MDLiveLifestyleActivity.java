package com.mdlive.embedkit.uilayer.lifestyle;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthFragment;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmFragment;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

public class MDLiveLifestyleActivity extends MDLiveBaseAppcompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_lifestyle_activity);
        MDLiveConfig.setData(3);
        setDrawerLayout((DrawerLayout) findViewById(com.mdlive.embedkit.R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(com.mdlive.embedkit.R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setTitle("BEHAVIORAL HEALTH HISTORY");
        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(com.mdlive.embedkit.R.id.dash_board__main_container, MDLiveBehaviouralHealthFragment.newInstance(), MAIN_CONTENT).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(com.mdlive.embedkit.R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(user), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(com.mdlive.embedkit.R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(MAIN_CONTENT).
                replace(com.mdlive.embedkit.R.id.dash_board__main_container, EmailConfirmFragment.newInstance()).
                commit();
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
                startActivityWithClassName(MessageCenterActivity.class);
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
        startActivityWithClassName(MDLiveGetStarted.class);
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
}