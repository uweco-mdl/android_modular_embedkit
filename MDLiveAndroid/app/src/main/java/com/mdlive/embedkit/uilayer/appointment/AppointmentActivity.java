package com.mdlive.embedkit.uilayer.appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;

import java.lang.ref.WeakReference;

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
        clearMinimizedTime();
        setTitle("");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_appointment_details).toUpperCase());
            elevateToolbar(toolbar);
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

    public void onBackClicked(View view) {
        finish();
    }

    public void onCallClicked(View view) {
        MdliveUtils.showMDLiveHelpAndSupportDialog(this);
    }

    public void onStartAppointmentClicked(View view) {
        try{
            Class clazz = Class.forName("com.mdlive.sav.WaitingRoom.MDLiveWaitingRoom");
            startActivityWithClassName(clazz);
        } catch (ClassNotFoundException e){
            Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
        }

    }

    public void onCancelAppointmentClicked(View view) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof  AppointmentFragment) {
            ((AppointmentFragment) fragment).onCancelAppointmentClicked();
        }
    }
}
