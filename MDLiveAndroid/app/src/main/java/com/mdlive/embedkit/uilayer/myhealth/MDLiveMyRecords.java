package com.mdlive.embedkit.uilayer.myhealth;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageMyRecordsFragment;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by venkataraman_r on 8/31/2015.
 */
public class MDLiveMyRecords extends MDLiveBaseAppcompatActivity {
    public static final String DATA_TAG = "data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_compose);
        clearMinimizedTime();

        try {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                setTitle("");
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_my_record).toUpperCase());
                ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
                ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, MessageMyRecordsFragment.newInstance(), MAIN_CONTENT).
                    commit();

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

    public void leftBtnOnClick(View view) {
        finish();
    }

    public void addPhotoOnClick(View view) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);

        if (fragment != null && fragment instanceof MessageMyRecordsFragment) {
            ((MessageMyRecordsFragment) fragment).showChosserDialog();
        }
    }


}
