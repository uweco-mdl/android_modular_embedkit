package com.mdlive.embedkit.uilayer.helpandsupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

public class MDLiveHelpAndSupportActivity extends MDLiveBaseAppcompatActivity implements FragmentManager.OnBackStackChangedListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_help_and_support_activity);
        setTitle("");

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            showHamburgerBell();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.main_container, HelpAndSupportFragment.newInstance(), MAIN_CONTENT).
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

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount == 1) {
            showTickCross();
        } else {
            showHamburgerBell();
        }
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
                startActivityWithClassName(MDLiveDashboardActivity.class);
                break;

            // See a Doctor
            case 1:
                startActivityWithClassName(MDLiveGetStarted.class);
                break;

            // MDLive My Health
            case 2:

                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:
                startActivityWithClassName(MessageCenterActivity.class);
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

                break;

            // Share
            case 8:

                break;
        }
    }

    public void callAction(View view) {
        MdliveUtils.showMDLiveHelpAndSupportDialog(this);
    }

    public void askQuestion(View view) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(MAIN_CONTENT).
                add(R.id.main_container, AskQuestionFragment.newInstance(), MAIN_CONTENT).
                commit();

        showTickCross();
    }

    public void onTickClicked(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof  AskQuestionFragment) {
            ((AskQuestionFragment) fragment).onTickClicked();
        }
    }

    public void onCrossClicked(View view) {
        getSupportFragmentManager().popBackStack();
    }

    public void showHamburgerBell() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.help_and_support_title).toUpperCase());

        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.toolbar_tick).setVisibility(View.GONE);

        findViewById(R.id.toolbar_hamburger).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_bell).setVisibility(View.VISIBLE);
    }

    public void showTickCross() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.new_question).toUpperCase());

        findViewById(R.id.toolbar_cross).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_tick).setVisibility(View.VISIBLE);

        findViewById(R.id.toolbar_hamburger).setVisibility(View.GONE);
        findViewById(R.id.toolbar_bell).setVisibility(View.GONE);
    }
}
