package com.mdlive.embedkit.uilayer.messagecenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

/**
 * Created by dhiman_da on 8/19/2015.
 */
public class MessageCenterInboxDetailsActivity extends MDLiveBaseAppcompatActivity {
    public static final String DATA_TAG = "data";

    public static Intent getMessageDetailsIntent(final Context context, final Parcelable parcelable) {
        final Intent intent = new Intent(context, MessageCenterInboxDetailsActivity.class);
        intent.putExtra(DATA_TAG, parcelable);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_inbox_details);

        final Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setTitle("");
            setSupportActionBar(toolbar);
        }

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(DATA_TAG) != null) {
                Parcelable parcelable = getIntent().getExtras().getParcelable(DATA_TAG);

                if (parcelable instanceof ReceivedMessage) {
                    ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.inbox).toUpperCase());
                    getSupportFragmentManager().
                            beginTransaction().
                            add(R.id.container, MessageReceivedDetailsFragment.newInstance((ReceivedMessage) parcelable)).
                            commit();
                } else if (parcelable instanceof SentMessage) {
                    ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.sent).toUpperCase());
                    getSupportFragmentManager().
                            beginTransaction().
                            add(R.id.container, MessageSentDetailsFragment.newInstance((SentMessage) parcelable)).
                            commit();
                }
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

                break;
        }
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onReplyClicked(final Parcelable parcelable) {
        startActivity(MessageCenterComposeActivity.getMessageComposeDetailsIntent(getBaseContext(), parcelable));
        finish();
    }
}
