package com.mdlive.embedkit.uilayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment.OnEmailConfirmationClicked;
import com.mdlive.embedkit.uilayer.login.LoginActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnUserSelectionChanged;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.OnUserInformationLoaded;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment.OnAppointmentClicked;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.AddFamilyMemberActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/16/2015.
 */
public abstract class MDLiveBaseAppcompatActivity extends AppCompatActivity implements NavigationDrawerCallbacks,
        OnUserInformationLoaded,
        OnUserSelectionChanged,
        OnAppointmentClicked,
        OnEmailConfirmationClicked {
    public static final String DIALOG_FRAGMENT = "dialog_fragment";

    public static final String MAIN_CONTENT = "main_content";
    public static final String LEFT_MENU = "left_menu";
    public static final String RIGHT_MENU = "right_menu";

    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearMinimizedTime();
        setTitle("");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (showPinScreen()) {
            if (MdliveUtils.getLockType(this).equalsIgnoreCase("password")) {
                startActivity(LoginActivity.getLockLoginIntnet(getBaseContext()));
            } else {
                startActivity(new Intent(getBaseContext(), UnlockActivity.class));
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveLastMinimizedTime();
    }

    public void setDrawerLayout(final DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;

        if (mDrawerLayout != null) {
            setDrawerListener();
        }
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public boolean isDrawerOpen() {
        return getDrawerLayout().isDrawerOpen(GravityCompat.START) || getDrawerLayout().isDrawerOpen(GravityCompat.END);
    }

    public void setDrawerListener() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /* Start of Drawer click listeners */
    public void onLeftDrawerClicked(View view) {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.END)) {
            getDrawerLayout().closeDrawer(GravityCompat.END);
            getDrawerLayout().openDrawer(GravityCompat.START);
        } else if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
        } else {
            getDrawerLayout().openDrawer(GravityCompat.START);
        }
    }

    public void onRightDrawerClicked(View view) {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
            getDrawerLayout().openDrawer(GravityCompat.END);
        } else if (getDrawerLayout().isDrawerOpen(GravityCompat.END)) {
            getDrawerLayout().closeDrawer(GravityCompat.END);
        } else {
            getDrawerLayout().openDrawer(GravityCompat.END);
        }
    }
    /* End of Drawer click listeners */


    public void startActivityWithClassName(final Class clazz) {
        final Intent intent = new Intent(getBaseContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    public void startActivityWithClassNameAddFamilyMember(final Class clazz) {
        final Intent intent = new Intent(getBaseContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public abstract void onNavigationDrawerItemSelected(int position);

    public void onSignoutClicked(View view) {
        MdliveUtils.clearNecessarySharedPrefernces(getApplicationContext());

        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    @Override
    public void sendUserInformation(UserBasicInfo userBasicInfo) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(RIGHT_MENU);
        if (fragment != null && fragment instanceof NotificationFragment) {
            ((NotificationFragment) fragment).setNotification(userBasicInfo);
        }

        fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).onUserInformationLoaded(userBasicInfo);
        }
    }

    @Override
    public void onAddChildSelectedFromDrawer(User user, final int dependentUserSize) {
        onAddChildSelcted(user, dependentUserSize);
    }

    @Override
    public void reloadApplicationForUser(User user) {
        user.saveSelectedUser(getBaseContext());
        final Intent intent = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(User.USER_TAG, user);
        startActivity(intent);
    }

    @Override
    public void setPrimaryUserSelected() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).setPrimaryUserSelected();
        }
    }

    @Override
    public void onDependentSelected(User user) {
        if (user.mMode == User.MODE_ADD_CHILD) {
            Toast.makeText(getBaseContext(), "Add Child To Be Started", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof NavigationDrawerFragment) {
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

    public void onMessageClicked(View view) {
        onMessageClicked();
    }

    public void onMessageClicked() {
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        if (userBasicInfo != null && userBasicInfo.getPersonalInfo().getEmailConfirmed()) {
            startActivityWithClassName(MessageCenterActivity.class);
        } else {
            showEmailConfirmationDialog();
        }
    }

    public void onHomeClicked() {
        final User user = User.getSelectedUser(getBaseContext());

        if (user == null) {
            startActivityWithClassName(MDLiveDashboardActivity.class);
        } else {
            startActivity(MDLiveDashboardActivity.getDashboardIntentWithUser(getBaseContext(), user));
        }
    }

    public void onSeeADoctorClicked() {
        final User user = User.getSelectedUser(getBaseContext());

        if (user == null) {
            startActivityWithClassName(MDLiveGetStarted.class);
        } else {
            startActivity(MDLiveGetStarted.getGetStartedIntentWithUser(getBaseContext(), user));
        }
    }

    public void onPersonalInfoClicked(View view) {
        final Intent intent = MedicalHistoryActivity.getSelectedTabFromMedicalHistory(getBaseContext(), 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onPreferedStoreClicked(View view) {
        final Intent intent = MedicalHistoryActivity.getSelectedTabFromMedicalHistory(getBaseContext(), 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAppointmentClicked(Appointment appointment) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);
        startActivity(AppointmentActivity.getAppointmentIntent(getBaseContext(), appointment));
    }

    private void onAddChildSelcted(final User user, final int dependentUserSize) {
        if (dependentUserSize >= IntegerConstants.ADD_CHILD_SIZE) {
            MdliveUtils.showAddChildExcededDialog(this);
        } else {
            startActivityWithClassNameAddFamilyMember(AddFamilyMemberActivity.class);

        }
    }

    public void shareApplication() {
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share_details_text)));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
    }

    private void clearMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private boolean showPinScreen() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final long lastTime = preferences.getLong(PreferenceConstants.TIME_KEY, System.currentTimeMillis());

        final long difference = System.currentTimeMillis() - lastTime;
        if (difference > 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    private void saveLastMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PreferenceConstants.TIME_KEY, System.currentTimeMillis());
        editor.commit();
    }

    public void showEmailConfirmationDialog() {
        final EmailConfirmationDialogFragment dialogFragment = EmailConfirmationDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), DIALOG_FRAGMENT);
    }

    @Override
    public void onEmailConfirmationClicked() {
        loadEmailConfirmationService();
    }

    public void loadEmailConfirmationService() {
        try {
            NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MdliveUtils.showDialog(getBaseContext(), getString(R.string.app_name), response.getString("message"));
                    } catch (JSONException e) {
                    }
                }
            };

            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            };

            EmailConfirmationService service = new EmailConfirmationService(getBaseContext(), null);
            service.emailConfirmation(successCallBackListener, errorListener, null);
        } catch (Exception e) {

        }
    }
}
