package com.mdlive.embedkit.uilayer.myhealth;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageMyRecordsFragment;

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
        this.setTitle(getString(R.string.mdl_my_record));

        try {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                setTitle("");
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_my_record).toUpperCase());
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
        findViewById(R.id.txtApply).setVisibility(View.GONE);

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
