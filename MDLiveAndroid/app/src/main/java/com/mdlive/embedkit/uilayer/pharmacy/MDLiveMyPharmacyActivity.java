package com.mdlive.embedkit.uilayer.pharmacy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.lifestyle.MDLiveLifeStyleFragment;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

/**
 * Created by unnikrishnan_b on 8/25/2015.
 */
public class MDLiveMyPharmacyActivity extends MDLiveBaseAppcompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_lifestyle_activity);
        clearMinimizedTime();

        setDrawerLayout((DrawerLayout) findViewById(com.mdlive.embedkit.R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(com.mdlive.embedkit.R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            elevateToolbar(toolbar);
            ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_my_pharmacy).toUpperCase());
        }

        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(com.mdlive.embedkit.R.id.dash_board__main_container, MDLiveLifeStyleFragment.newInstance(), MAIN_CONTENT).
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

    /* Start of Dashboard icons click listener */
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
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveLifeStyleFragment) {
            ((MDLiveLifeStyleFragment) fragment).lifeStyleSaveButtonEvent();
        }
    }
}