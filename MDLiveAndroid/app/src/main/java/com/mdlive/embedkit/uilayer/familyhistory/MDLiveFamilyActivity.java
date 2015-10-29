package com.mdlive.embedkit.uilayer.familyhistory;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmFragment;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
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
        clearMinimizedTime();

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        try {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_my_family_history).toUpperCase());
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /* Start of Dashboard icons click listener */
    public void onBackClicked(View view) {
        finish();
    }

    public void onTickClicked(View view) {
        saveAction(view);
    }

    public void addAction (View view) {
        mdLiveFamilyFragment.addNewHistoryData();
        MdliveUtils.hideSoftKeyboard(this);
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