package com.mdlive.embedkit.uilayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment.OnEmailConfirmationClicked;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnUserSelectionChanged;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment.OnAppointmentClicked;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.AddFamilyMemberActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.BroadcastConstant;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;

import java.lang.Class;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.NavigationDrawerCallbacks;

/**
 * Created by dhiman_da on 8/16/2015.
 */
public abstract class MDLiveBaseAppcompatActivity extends AppCompatActivity implements NavigationDrawerCallbacks,
        com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.OnUserInformationLoaded,
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

        if (showPinScreen() && MdliveUtils.getLockType(getBaseContext()).equals("Pin")) {
            sendBroadcast(getUnlockBroadcast());
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
    public void onNavigationDrawerItemSelected(int position){
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            ArrayList<String>drawerList = ((NavigationDrawerFragment) fragment).getDrawerList();
            String string = drawerList.get(position).toString();

            if(string.equalsIgnoreCase(getString(R.string.mdl_home))) {
                // Home
                onHomeClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_see_a_doctor_now))){
                // See a Doctor
                onSeeADoctorClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_my_health))) {
                // MDLive My Health
                startActivityWithClassName(MedicalHistoryActivity.class);
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_mdlive_assist))) {
                // MDLIVE Assist
                String[] modules = getBaseContext().getResources().getStringArray(R.array.left_navigation_modules);
                for(int i=0; i<modules.length;i++) {
                    try {
                        Class clazz = Class.forName(modules[i]);
                        Method method = clazz.getMethod("showMDLiveAssistDialog", Activity.class, String.class);
                        method.invoke(null, this, UserBasicInfo.readFromSharedPreference(getBaseContext()).getAssistPhoneNumber());
                    } catch (ClassNotFoundException e){
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_message_center))) {
                // Message Center
                onMessageClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_symptom_checker))) {
                // Symptom Checker
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_my_accounts))) {
                // My Accounts
                startActivityWithClassName(MyAccountActivity.class);
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_support))) {
                // Support
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_share_app))) {
                // Share
                shareApplication();
            }
        }
    }

    public void onSignoutClicked(View view) {
        MdliveUtils.clearNecessarySharedPrefernces(getApplicationContext());
        sendBroadcast(getLogoutIntent());
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

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).hideNotification();
        }

        fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).loadDependendUserDetails(user, true);
        }
    }

    @Override
    public void onPrimarySelected(User user) {
        if (user.mMode == User.MODE_ADD_CHILD) {
            Toast.makeText(getBaseContext(), "Add Child To Be Started", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).hideNotification();
        }

        fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).loadUserInformationDetails(true);
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
            if(findViewById(R.id.drawer_layout) != null) {
                findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
            }
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

    @Override
    public void onCloseDrawer() {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);
    }

    private void onAddChildSelcted(final User user, final int dependentUserSize) {
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());

        if (userBasicInfo.getRemainingFamilyMembersLimit() < 1) {
            MdliveUtils.showAddChildExcededDialog(this,userBasicInfo.getAssistPhoneNumber());
        } else {
            Intent addFamilyMember = new Intent(getBaseContext(), AddFamilyMemberActivity.class);
            startActivity(addFamilyMember); ;
        }
    }

    public void shareApplication() {
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mdl_share_details_text));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.mdl_share_using)));
    }

    public void clearMinimizedTime() {
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
                        MdliveUtils.showDialog(MDLiveBaseAppcompatActivity.this, getString(R.string.mdl_app_name), response.getString("message"));
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

    public Intent getLoginBroadcast() {
        final Intent intent = new Intent();
        intent.setAction(BroadcastConstant.LOGIN_ACTION);
        intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN);

        return intent;
    }

    public Intent getUnlockBroadcast() {
        final Intent intent = new Intent();
        intent.setAction(BroadcastConstant.UNLOCK_ACTION);
        
        if (MdliveUtils.getLockType(getBaseContext()).equals("Pin")) {
            intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOCK);
        } else {
            intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN);
        }

        return intent;
    }

    public Intent getLogoutIntent() {
        final Intent intent = new Intent();
        intent.setAction(BroadcastConstant.LOGIN_ACTION);
        intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN_AFTER_LOGOUT);

        return intent;
    }


    public void reloadSlidingMenu() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).reload();
        }
    }


    public void elevateToolbar(final Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar != null) {
                toolbar.setElevation(7 * getResources().getDisplayMetrics().density);
            }
        }
    }

}
