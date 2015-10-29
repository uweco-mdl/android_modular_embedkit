package com.mdlive.symptomchecker;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;

/**
 * This class provides to call Symptom checker fragment
 */
public class MDLiveSymptomCheckerActivity extends MDLiveBaseAppcompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_symptom_checker_activity);
        clearMinimizedTime();
        setTitle("");

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            showHamburgerBell();
            elevateToolbar(toolbar);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.main_container, MDLiveSymptomCheckerFragment.newInstance(), MAIN_CONTENT).
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

    @Override
    public void onBackPressed() {
        onHomeClicked();
    }

    public void showHamburgerBell() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_symptom_checker).toUpperCase());

        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.toolbar_tick).setVisibility(View.GONE);

        findViewById(R.id.toolbar_hamburger).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_bell).setVisibility(View.VISIBLE);
    }
}
