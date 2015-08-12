package com.mdlive.embedkit.uilayer.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMyHealthActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDliveDashboardActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        MDLiveDashBoardFragment.SendNotification {
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

            // Talk to a Doctor
            case 1:

                break;

            // Schedule a Visit
            case 2:

                break;

            // My Health
            case 3:
                startActivityWithClassName(MDLiveMyHealthActivity.class);
                break;

            // Message Center
            case 4:
                startActivityWithClassName(MessageCenterActivity.class);
                break;

            // MDLIVE Assist
            case 5:
                showMDLiveAssistDialog();
                break;

            // Symptom Checker
            case 6:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 7:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 8:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share this App
            case 9:

                break;

            // Sign Out
            case 10:

                break;
        }
    }

    /* Start of Dashboard icons click listener */
    public void onTalkToDoctorClicked(View view) {

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
        showMDLiveAssistDialog();
    }

    public void onSymptomChecker(View view) {
        startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
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

    private void showMDLiveAssistDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            ImageView alertImage = (ImageView) view.findViewById(R.id.alertdialogimageview);
            alertImage.setImageResource(R.drawable.ic_launcher);
            TextView alertText = (TextView) view.findViewById(R.id.alertdialogtextview);
            alertText.setText(getText(R.string.call_text));

            builder.setView(view);
            builder.setPositiveButton(getText(R.string.call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + getText(R.string.callnumber)));
                                startActivity(intent);
                            } catch (Exception e) {
                            }

                        }
                    });
            builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotification(UserBasicInfo userBasicInfo) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(RIGHT_MENU);
        if (fragment != null && fragment instanceof  NotificationFragment) {
            ((NotificationFragment) fragment).setNotification(userBasicInfo);
        }
    }
}
