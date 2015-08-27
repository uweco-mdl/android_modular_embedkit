package com.mdlive.embedkit.uilayer.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by sudha_s on 8/26/2015.
 */
public class MDLiveStartVisit extends MDLiveBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_for_visit);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.left_icon)).setImageResource(R.drawable.exit_icon);
        ((ImageView) findViewById(R.id.right_icon)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.make_appointment_txt));



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

    }

    public void onLeftDrawerClicked(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveStartVisit.this);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveStartVisit.this);
    }

    public void onRightDrawerClicked(View v) {
        Intent intent = new Intent(MDLiveStartVisit.this, MDLiveWaitingRoom.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveStartVisit.this);
    }

    public void rightBtnOnClick(View v) {
        Intent intent = new Intent(MDLiveStartVisit.this, MDLiveWaitingRoom.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveStartVisit.this);
    }


    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveStartVisit.this);
        onBackPressed();
    }





    }

