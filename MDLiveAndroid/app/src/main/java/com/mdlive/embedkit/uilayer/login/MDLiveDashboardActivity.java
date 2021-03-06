package com.mdlive.embedkit.uilayer.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnNotificationClicked;
import com.mdlive.embedkit.uilayer.login.NotificationFragment.NotifyDashboard;
import com.mdlive.unifiedmiddleware.commonclasses.utils.AnalyticsApplication;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.OncallAppointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDLiveDashboardActivity extends MDLiveBaseAppcompatActivity implements NotifyDashboard, OnNotificationClicked {
    public static Intent getDashboardIntentWithUser(final Context context, final User user) {
        final Intent intent = new Intent(context, MDLiveDashboardActivity.class);
        intent.putExtra(User.USER_TAG, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_activity_dashboard);
        clearMinimizedTime();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);

        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__main_container, MDLiveDashBoardFragment.newInstance(), MAIN_CONTENT).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(user), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

        if (DeepLinkUtils.DEEPLINK_DATA != null
                && DeepLinkUtils.DEEPLINK_DATA.getPage() != null
                && DeepLinkUtils.DEEPLINK_DATA.getPage().length() > 0) {
            findViewById(R.id.drawer_layout).setVisibility(View.GONE);
        }

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = new AnalyticsApplication();
        for(AnalyticsApplication.TrackerName tn : AnalyticsApplication.TrackerName.values()) {
            Tracker mTracker = application.getTracker(getApplication(), tn);
            if(mTracker != null) {
                mTracker.setScreenName(getString(R.string.mdl_mdlive_home_page));
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        }
    }
    private void checkNotification(){
        try {
            SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            if (settings.contains("has_push_notification")) {
                String message = settings.getString("has_push_notification", "{}");
                settings.edit().remove("has_push_notification").commit();
                JsonParser parser = new JsonParser();
                JsonObject originalPayload = parser.parse(message).getAsJsonObject();
                try {
                    Class clazz;
                    if(originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message")){
                        clazz = Class.forName("com.mdlive.messages.messagecenter.MessageCenterInboxDetailsActivity");
                    } else {
                        clazz = Class.forName("com.mdlive.sav.appointment.AppointmentActivity");
                    }
                    Intent messageIntent = new Intent(this, clazz);

                    messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());

                    messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(messageIntent);
                }catch (ClassNotFoundException e){
                    /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.mdl_mdlive_module_not_found),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isScreenLoaded = false;
    @Override
    public void onResume() {
        super.onResume();
        if(isScreenLoaded && findViewById(R.id.drawer_layout) != null) {
            findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
        }
        MdliveUtils.hideSoftKeyboard(this);
        isScreenLoaded = true;

    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment && ((MDLiveDashBoardFragment) fragment).isWebView) {
            ((MDLiveDashBoardFragment) fragment).mWebView.setVisibility(View.GONE);  // Since there is no further navigation from the webview
        }
        return;
    }

    /* On Email Unconfirmed Click listener */
    public void onEmailUnconfirmClicked(View view) {
        showEmailConfirmationDialog();
    }

    /* Start of Dashboard icons click listener */
    public void onSeeADoctorNowClicked(View view) {
        if (!isDrawerOpen()) {
            onSeeADoctorClicked();
        }
    }

    public void onMyHealthClicked(View view) {
        if (!isDrawerOpen()) {
            onMyHealthClicked();
        }
    }

    public void onMessageCenterClicked(View view) {
        if (!isDrawerOpen()) {
            onMessageClicked();
        }
    }

    public void onMdliveAssistClicked(View view) {
        if (!isDrawerOpen()) {
            onAssistClicked();
        }
    }

    public void onSymptomCheckerClicked(View view) {
        if (!isDrawerOpen()) {
            onSymptomCheckerClicked();
        }
    }

    public void onMyAccountClicked(View view) {
        if (!isDrawerOpen()) {
            onMyAccountClicked();
        }
    }

    /* End of Dashboard icons click listener */

    @Override
    public void onEmailConfirmationClicked() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).loadEmailConfirmationService();
        }
    }

    @Override
    public void onShowNofifyDashboard(Appointment appointment) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).showNotification(appointment);
        }
    }

    @Override
    public void onShowNotifyOnCallDashBoard(OncallAppointment appointment) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).showOnCallNotification(appointment);
        }
    }

    @Override
    public void onHideNotifyDashboard() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).hideNotification();
        }
    }

    @Override
    public void onNotificationClicked(Appointment appointment) {
        onAppointmentClicked(appointment);
    }

    @Override
    public void sendUserInformation(UserBasicInfo userBasicInfo) {
        super.sendUserInformation(userBasicInfo);

        checkDeeplink();
    }

    public void checkDeeplink() {
        Log.d("DeepLink", "In Deeplink check");
        checkNotification();
        if (DeepLinkUtils.DEEPLINK_DATA != null ) {
            Log.d("DeepLink", "Data : " + DeepLinkUtils.DEEPLINK_DATA);

            final int pageCode = DeepLinkUtils.getPageCode();

            switch (pageCode) {
                case 0:
                    // Home screen
                    // Do nothing, already in Home
                    findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                    // Assist screen
                    onAssistClicked();
                    break;
                case 2:
                    // SAV screen
                    onSeeADoctorClicked();
                    break;
                case 3:
                    // Medical health screen
                    onMyHealthClicked();
                    break;
                case 4:
                    // Message center screen
                    onMessageClicked();
                    break;
                case 5:
                    // Symptom Checker screen
                    onSymptomCheckerClicked();
                    break;
                case 6:
                    // My Account screen
                    onMyAccountClicked();
                    break;
            }

            DeepLinkUtils.DEEPLINK_DATA.setPage("");
        }
    }
}
