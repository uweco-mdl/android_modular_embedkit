package com.mdlive.embedkit.uilayer.familyhistory;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmFragment;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MDLiveFamilyActivity extends MDLiveBaseAppcompatActivity {
    MDLiveFamilyFragment mdLiveFamilyFragment = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_lifestyle_activity);

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.my_family_history).toUpperCase());
        }

        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
        }

        if (savedInstanceState == null) {
            mdLiveFamilyFragment = MDLiveFamilyFragment.newInstance();
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__main_container, mdLiveFamilyFragment, MAIN_CONTENT).
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



    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(MAIN_CONTENT).
                replace(R.id.dash_board__main_container, EmailConfirmFragment.newInstance()).
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


    /* Start of Dashboard icons click listener */
    public void onSeeADoctorNowClicked(View view) {
        onSeeADoctorClicked();
    }

    public void onMyHealthClicked(View view) {

    }

    public void onMessageCenterClicked(View view) {
        onMessageClicked();
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

    public void onBackClicked(View view) {
        finish();
    }

    public void onTickClicked(View view) {
        saveAction(view);
    }

    public void addAction (View view) {
        if(mdLiveFamilyFragment.mFamilyHistoryOtherEditText.getText().length() != 0) {
            mdLiveFamilyFragment.mFamilyHistoryOtherEditTextValue = mdLiveFamilyFragment.mFamilyHistoryOtherEditText.getText().toString();
            Log.d("FamilyHistoryOtherValue", mdLiveFamilyFragment.mFamilyHistoryOtherEditTextValue.toString());
        }
        else {
            mdLiveFamilyFragment.mFamilyHistoryOtherEditTextValue = "";
            Log.d("FamilyHistoryOtherValue",mdLiveFamilyFragment.mFamilyHistoryOtherEditTextValue);
        }
    }


    public void saveAction(View view) {
        final JSONObject requestJSON = new JSONObject();

        try {

            final JSONArray lifeStyleConditionJSONArray = new JSONArray();

            for (int i = 0; i < mdLiveFamilyFragment.familyHistoryList.size(); i++) {
                final JSONObject jsonObject = new JSONObject();

                jsonObject.put("relationship", mdLiveFamilyFragment.familyHistoryList.get(i).relationship);
                jsonObject.put("condition", mdLiveFamilyFragment.familyHistoryList.get(i).condition);
                jsonObject.put("active", mdLiveFamilyFragment.familyHistoryList.get(i).active);

                Log.d("HELLO", mdLiveFamilyFragment.familyHistoryList.get(i).toString());

                lifeStyleConditionJSONArray.put(jsonObject);
            }

            requestJSON.put("family_histories", lifeStyleConditionJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initializeJsonElements();

        mdLiveFamilyFragment.getFamilyHistoryUpdateServiceData(requestJSON);
    }
}