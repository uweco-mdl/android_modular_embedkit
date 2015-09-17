package com.mdlive.embedkit.uilayer.messagecenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageComposeFragment.OnBothTextEntered;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/19/2015.
 */
public class MessageCenterComposeActivity extends MDLiveBaseAppcompatActivity implements OnBothTextEntered {
    public static final String DATA_TAG = "data";
    public static final String HEADING_TAG = "heading";

    public static Intent getMessageComposeDetailsIntent(final Context context, final Parcelable parcelable) {
        final Intent intent = new Intent(context, MessageCenterComposeActivity.class);
        intent.putExtra(DATA_TAG, parcelable);
        return intent;
    }

    public static Intent getMessageComposeDetailsIntentWithHeading(final Context context, final Parcelable parcelable, final String heading) {
        final Intent intent = new Intent(context, MessageCenterComposeActivity.class);
        intent.putExtra(DATA_TAG, parcelable);
        intent.putExtra(HEADING_TAG, heading);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_compose);
        clearMinimizedTime();

        showRight(false);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle("");
            if (getIntent().getExtras() != null && getIntent().hasExtra(HEADING_TAG)) {
                ((TextView) findViewById(R.id.headerTxt)).setText(getIntent().getStringExtra(HEADING_TAG).toUpperCase());
            } else {
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_compose_message).toUpperCase());
            }
        }

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(DATA_TAG) != null) {
                Parcelable parcelable = getIntent().getExtras().getParcelable(DATA_TAG);

                getSupportFragmentManager().
                        beginTransaction().
                        add(R.id.container, MessageComposeFragment.newInstance(parcelable), MAIN_CONTENT).
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
                onHomeClicked();
                break;

            // See a Doctor
            case 1:
                onSeeADoctorClicked();
                break;

            // MDLive My Health
            case 2:
                startActivityWithClassName(MedicalHistoryActivity.class);
                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:

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
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share
            case 8:
                shareApplication();
                break;
        }
    }

    public void leftBtnOnClick(View view) {
        finish();
    }

    public void rightBtnOnClick(View view) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MessageComposeFragment) {
            ((MessageComposeFragment) fragment).sendComposedMessage();
        }
    }

    @Override
    public void onBothTextEntered(boolean value) {
        showRight(value);
    }

    private void showRight(final boolean value) {
        if (value) {
            findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.txtApply).setVisibility(View.GONE);
        }
    }
}
