package com.mdlive.embedkit.uilayer.myhealth;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by venkataraman_r on 8/25/2015.
 */
public class PrimaryCarePhysicianActivity extends MDLiveBaseAppcompatActivity {

    public static String TAG = "PRIMARY CARE PHYSICIAN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccounts_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView title = (TextView) toolbar.findViewById(R.id.headerTxt);

        title.setText(getResources().getString(R.string.pcp_title));

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.container, new PrimaryCarePhysicianFragment(), "PRIMARY CARE PHYSICIAN").
                commit();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:
                startActivityWithClassName(MDLiveDashboardActivity.class);
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

    public void leftBtnOnClick(View v) {
        finish();
    }

    public void rightBtnOnClick(View v) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);

        if (fragment != null && fragment instanceof PrimaryCarePhysicianFragment) {
            ((PrimaryCarePhysicianFragment) fragment).uploadPCP();
        }
    }
}