package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnUserSelectionChanged;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.OnUserInformationLoaded;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDliveDashboardActivity extends AppCompatActivity implements NavigationDrawerCallbacks,
        OnUserInformationLoaded,
        OnUserSelectionChanged {
    private static final String DASH_BOARD = "dash_board";
    private static final String LEFT_MENU = "left_menu";
    private static final String RIGHT_MENU = "right_menu";

    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_activity_dashboard);

        setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__main_container, MDLiveDashBoardFragment.newInstance(), DASH_BOARD).
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

    /* Start of Drawer click listeners */
    public void onLeftDrawerClicked(View view) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void onRightDrawerClicked(View view) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.openDrawer(GravityCompat.END);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }
    /* End of Drawer click listeners */

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerLayout.closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:

                break;

            // See a Doctor
            case 1:

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

            // Symptom Checker
            case 7:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 8:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 9:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share this App
            case 10:

                break;
        }
    }

    /* Start of Dashboard icons click listener */
    public void onSeeADoctorNowClicked(View view) {

    }

    public void onScheduleAVisitClicked(View view) {
        startActivityWithClassName(MDLiveGetStarted.class);
    }

    public void onMyHealthClicked(View view) {

    }

    public void onMessageCenterClicked(View view) {
        startActivityWithClassName(MessageCenterActivity.class);
    }

    public void onMdliveAssistClicked(View view) {
        MdliveUtils.showMDLiveAssistDialog(this);
    }

    public void onSymptomCheckerClicked(View view) {
        startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
    }

    public void onMyAccountClicked(View view) {
        startActivityWithClassName(MyAccountActivity.class);
    }

    public void onSignoutClicked(View view) {

    }
    /* End of Dashboard icons click listener */

    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(DASH_BOARD).
                replace(R.id.dash_board__main_container, EmailConfirmFragment.newInstance()).
                commit();
    }

    private void startActivityWithClassName(final Class clazz) {
        startActivity(new Intent(getBaseContext(), clazz));
    }

    @Override
    public void sendUserInformation(UserBasicInfo userBasicInfo) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(RIGHT_MENU);
        if (fragment != null && fragment instanceof  NotificationFragment) {
            ((NotificationFragment) fragment).setNotification(userBasicInfo);
        }

        fragment = getSupportFragmentManager().findFragmentByTag(DASH_BOARD);
        if (fragment != null && fragment instanceof  MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).onUserInformationLoaded(userBasicInfo);
        }
    }

    @Override
    public void onDependentSelected(User user) {
        if (user.mMode == User.MODE_ADD_CHILD) {
            Toast.makeText(getBaseContext(), "Add Child To Be Started", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).loadDependendUserDetails(user);
        }
    }

    @Override
    public void onPrimarySelected(User user) {
        if (user.mMode == User.MODE_ADD_CHILD) {
            Toast.makeText(getBaseContext(), "Add Child To Be Started", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).loadUserInformationDetails();
        }
    }

    @Override
    public void onAddChildSelectedFromDashboard(User user, final int dependentUserSize) {
        onAddChildSelcted(user, dependentUserSize);
    }

    @Override
    public void onAddChildSelectedFromDrawer(User user, final int dependentUserSize) {
        onAddChildSelcted(user, dependentUserSize);
    }

    private void onAddChildSelcted(final User user, final int dependentUserSize) {
        if (dependentUserSize >= IntegerConstants.ADD_CHILD_SIZE) {
            MdliveUtils.showAddChildExcededDialog(this);
        } else {
            Toast.makeText(getBaseContext(), "Navigate to Add Child screen", Toast.LENGTH_SHORT).show();
        }
    }
}
