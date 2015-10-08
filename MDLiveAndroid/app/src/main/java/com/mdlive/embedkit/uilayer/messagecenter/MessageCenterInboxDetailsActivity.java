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
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

import static com.mdlive.embedkit.uilayer.messagecenter.MessageReceivedDetailsFragment.ReloadMessageCount;
import static com.mdlive.embedkit.uilayer.messagecenter.MessageReceivedDetailsFragment.newInstance;

/**
 * Created by dhiman_da on 8/19/2015.
 */
public class MessageCenterInboxDetailsActivity extends MDLiveBaseAppcompatActivity implements ReloadMessageCount {
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
        clearMinimizedTime();

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
                    ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_inbox).toUpperCase());
                    getSupportFragmentManager().
                            beginTransaction().
                            add(R.id.container, newInstance((ReceivedMessage) parcelable)).
                            commit();
                } else if (parcelable instanceof SentMessage) {
                    ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_sent).toUpperCase());
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

    @Override
    public void reloadMessageCount() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null) {
            ((NavigationDrawerFragment) fragment).reload();
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
