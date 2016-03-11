package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.CustomDatePickerDialog;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

import java.util.Calendar;


public class MDLiveBehaviouralHealthActivity extends MedicalHistoryPluginActivity {
    boolean isFromSAVflow = false, isNewUser = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_behavioural_history_layout);
        clearMinimizedTime();

        locationService = new LocationCooridnates(getApplicationContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        if(getIntent() != null && getIntent().hasExtra("from_sav")){
            isFromSAVflow = true;
            isNewUser = false;
        }
        if(getIntent() != null && getIntent().hasExtra("isNewUser")){
            isNewUser = true;
        }
        setDrawerLayout((DrawerLayout) findViewById(com.mdlive.embedkit.R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(com.mdlive.embedkit.R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_behaviouralhealthhistory).toUpperCase());
            ((TextView) findViewById(R.id.headerTxt)).setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            elevateToolbar(toolbar);
        }
        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
        }

        if (savedInstanceState == null) {
            if(isFromSAVflow){
                getSupportFragmentManager().
                        beginTransaction().
                        add(com.mdlive.embedkit.R.id.dash_board__main_container,
                                MDLiveBehaviouralHealthFragment.newInstance(isFromSAVflow, isNewUser), MAIN_CONTENT).
                        commit();

            }else{
                getSupportFragmentManager().
                        beginTransaction().
                        add(com.mdlive.embedkit.R.id.dash_board__main_container,
                                MDLiveBehaviouralHealthFragment.newInstance(), MAIN_CONTENT).
                        commit();

            }

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

    public void onBackClicked(View view) {
        finish();
    }

    public void onTickClicked(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveBehaviouralHealthFragment) {
            ((MDLiveBehaviouralHealthFragment) fragment).updateBehaviourHealthService();
        }
    }

    /* Start of Dashboard icons click listener */
    public void onScheduleAVisitClicked(View view) {
        onSeeADoctorClicked();
    }

    public void chooseStateOnClick(View view){
        Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveBehaviouralHealthFragment) {
            CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(this, ((MDLiveBehaviouralHealthFragment) fragment).pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setMaxDate(TimeZoneUtils.getCalendarWithOffset(this).getTime().getTime());
            datePickerDialog.show();
        }
    }


    /* End of Dashboard icons click listener */


}


