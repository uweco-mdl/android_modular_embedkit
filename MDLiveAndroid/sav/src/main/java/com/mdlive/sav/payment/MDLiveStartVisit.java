package com.mdlive.sav.payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by sudha_s on 8/26/2015.
 */
public class MDLiveStartVisit extends MDLiveBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_for_visit);
        this.setTitle(getString(R.string.mdl_time_for_visit_txt));

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }

//            ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.exit_icon);
//            ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.mdl_time_for_visit_txt).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }



        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String str_ProfileImg = sharedpreferences.getString(PreferenceConstants.PROVIDER_PROFILE, "");
        ((CircularNetworkImageView) findViewById(R.id.doctorPic)).setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));

    }

    public void leftBtnOnClick(View v) {
        MdliveUtils.hideSoftKeyboard(MDLiveStartVisit.this);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void rightBtnOnClick(View v) {
        movetoWaitingRoom();

    }

    private void movetoHome() {
        Intent intent = new Intent(MDLiveStartVisit.this, MDLiveDashboardActivity.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveStartVisit.this);
    }

    private void movetoWaitingRoom() {
        try{
            Class clazz = Class.forName("com.mdlive.sav.WaitingRoom.MDLiveWaitingRoom");
            Intent intent = new Intent(MDLiveStartVisit.this, clazz);
            startActivity(intent);
            MdliveUtils.startActivityAnimation(MDLiveStartVisit.this);
        } catch (ClassNotFoundException e){
            Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
        }

    }


}

