package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PrimaryCarePhysician;

/**
 * Created by venkataraman_r on 8/25/2015.
 */
public class PrimaryCarePhysicianActivity extends MDLiveBaseAppcompatActivity {
    public static String TAG = "PRIMARY CARE PHYSICIAN";

    private static final String PCP_DATA = "PCP_DATA";

    public static Intent getPCPIntent(final Context context, final PrimaryCarePhysician pcp) {
        final Intent intent = new Intent(context, PrimaryCarePhysicianActivity.class);
        intent.putExtra(PCP_DATA, pcp);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccounts_home);
        clearMinimizedTime();

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView title = (TextView) findViewById(R.id.headerTxt);

        title.setText(getResources().getString(R.string.mdl_pcp_title));

        if (savedInstanceState == null && getIntent().hasExtra(PCP_DATA)) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, PrimaryCarePhysicianFragment.newInstance((PrimaryCarePhysician) getIntent().getParcelableExtra(PCP_DATA)), "PRIMARY CARE PHYSICIAN").
                    commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:
                onHomeClicked();
                break;

            // See a Doctor
            case 1:
                onSeeADoctorClicked();
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
                shareApplication();
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