package com.mdlive.embedkit.uilayer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment;
import com.mdlive.embedkit.uilayer.login.EmailConfirmationDialogFragment.OnEmailConfirmationClicked;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment.OnUserSelectionChanged;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveSummary;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment.OnAppointmentClicked;
import com.mdlive.embedkit.uilayer.myaccounts.AddFamilyMemberActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.BroadcastConstant;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.plugins.SocialSharingHandler;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;
import com.mdlive.unifiedmiddleware.services.login.SSOBaylorService;

import java.lang.Class;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

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
    public static boolean IS_DEPENDENT_SELECTED;
    private DrawerLayout mDrawerLayout;

    private Handler mHandler;

    // Collection that contains all Activities that need to be terminated upon embedkit exit in SSO mode
    public static List<WeakReference<? extends MDLiveBaseAppcompatActivity>> VisitedScreens_SSO = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearMinimizedTime();
        MdliveUtils.activity = this;
        setTitle("");

        if(MDLiveConfig.IS_SSO)
        {
            WeakReference<MDLiveBaseAppcompatActivity> weakRef = new WeakReference<>(this);
            VisitedScreens_SSO.add(weakRef);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (showPinScreen()) {
            if(DeepLinkUtils.DEEPLINK_DATA != null && DeepLinkUtils.DEEPLINK_DATA.getAffiliate().equalsIgnoreCase(DeepLinkUtils.DeeplinkAffiliate.BAYLOR.name()))
            {
                DialogInterface.OnClickListener backToBaylor = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackToBaylorClicked(null);
                    }
                };
                MdliveUtils.showDialog(this,getString(R.string.mdl_app_name),getString(R.string.mdl_baylor_session_expired),getString(R.string.mdl_Ok),null,backToBaylor,null);
            }else if (MdliveUtils.getLockType(getBaseContext()).equals("Pin")){
                sendBroadcast(getUnlockBroadcast());
            }
        }else if(DeepLinkUtils.DEEPLINK_DATA != null && DeepLinkUtils.DEEPLINK_DATA.getAffiliate().equalsIgnoreCase(DeepLinkUtils.DeeplinkAffiliate.BAYLOR.name())){
            MakeBaylorSSOLogin();
        }
    }
    /**
     * Call the SSO service to auto login user if they come through baylor application
     * For baylor user the login and pin creation screens are not applicable
     * The user will be directed to destination screen without any interruption
     */
    private void MakeBaylorSSOLogin() {
        try {
//            final ProgressDialog pDialog = MdliveUtils.getFullScreenProgressDialog(this);
            final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.has("error")) {
                        BaylorSSOError();
                    }
                }
            };
            final NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    try {
                        BaylorSSOError();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            SSOBaylorService service = new SSOBaylorService(this, null);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String guid = sharedPref.getString(PreferenceConstants.BAYLOR_GUID, null);
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("user_guid", guid);
            postParam.put("affiliation_id", DeepLinkUtils.DEEPLINK_DATA.getAffiliationId() + "");

            service.BaylorSSO(successCallBackListener, errorListener, (new JSONObject(postParam)).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * When the baylor sso integration fails to login then display this error and ask your to login again from baylor app
     * This current activity will be closed once the user hits ok button
     */
    private void BaylorSSOError(){
        DialogInterface.OnClickListener backToBaylor = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackToBaylorClicked(null);
            }
        };
        MdliveUtils.showDialog(this, getString(com.mdlive.embedkit.R.string.mdl_app_name), getString(com.mdlive.embedkit.R.string.mdl_failed_baylor_login), getString(com.mdlive.embedkit.R.string.mdl_Ok), null, backToBaylor, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        MdliveUtils.isAppInForground = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MdliveUtils.isAppInForground = true;
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
            if(MDLiveConfig.IS_SSO){
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
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
    public void onNavigationDrawerItemSelected(int position) {
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
                onMyHealthClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_mdlive_assist))) {
                // MDLIVE Assist
                onAssistClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_message_center))) {
                // Message Center
                onMessageClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_symptom_checker))) {
                // Symptom Checker
                onSymptomCheckerClicked();
            }
            else if(string.equalsIgnoreCase(getString(R.string.mdl_my_accounts))) {
                // My Accounts
                onMyAccountClicked();
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

    public void onBackToBaylorClicked(View view) {
        MdliveUtils.clearNecessarySharedPrefernces(getApplicationContext());
        DeepLinkUtils.openBaylorApp(this);
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
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_add_child_start), Toast.LENGTH_SHORT).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_add_child_start),
                    Snackbar.LENGTH_LONG).show();
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
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_add_child_start), Toast.LENGTH_SHORT).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_add_child_start),
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        IS_DEPENDENT_SELECTED = false;
        /*if(NotificationFragment.getInstance() != null && NotificationFragment.getInstance().mUpcomingAppoinmantListView != null){
            NotificationFragment.getInstance().mUpcomingAppoinmantListView.setAdapter(null);
        }*/
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
        if (fragment != null && fragment instanceof MDLiveDashBoardFragment) {
            ((MDLiveDashBoardFragment) fragment).hideNotification();
        }
        fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
            //((NavigationDrawerFragment) fragment).loadUserInformationDetails(true);
            ((NavigationDrawerFragment) fragment).loadUserInformationDetailsOnSelection(true);
        }
    }

    @Override
    public void onAddChildSelectedFromDashboard(User user, final int dependentUserSize) {
        onAddChildSelcted(user, dependentUserSize);
    }

    public void onAssistClicked() {
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_assist_module));
            Method method = clazz.getMethod("showMDLiveAssistDialog", Activity.class, String.class);
            method.invoke(null, this, UserBasicInfo.readFromSharedPreference(getBaseContext()).getAssistPhoneNumber());
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onMessageClicked(View view) {
        onMessageClicked();
    }

    public void onMessageClicked() {
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        try {
            if (userBasicInfo != null && userBasicInfo.getPersonalInfo().getEmailConfirmed()) {
                Class clazz = Class.forName(getString(R.string.mdl_mdlive_messages_module));
                startActivityWithClassName(clazz);
            } else {
                if (findViewById(R.id.drawer_layout) != null) {
                    findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                }
                showEmailConfirmationDialog();
            }
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void onHomeClicked() {
        if(MDLiveConfig.IS_SSO){

            // send signal to notify caller that we are terminating
            MDLiveSummary.generateExitBroadcast(this);

            // purge all EmbedKit screens from the activity stack
            performCleanExit();
        }
        else {
            final User user = User.getSelectedUser(getBaseContext());

            if (user == null) {
                startActivityWithClassName(MDLiveDashboardActivity.class);
            } else {
                startActivity(MDLiveDashboardActivity.getDashboardIntentWithUser(getBaseContext(), user));
            }
        }
    }

    public void onSeeADoctorClicked() {
        final User user = User.getSelectedUser(getBaseContext());
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_sav_module));
            Method method = clazz.getMethod("getGetStartedIntentWithUser", Context.class, User.class);
            if (user == null) {
                startActivityWithClassName(clazz);
            } else {
                startActivity((Intent) method.invoke(null, getBaseContext(), user));
            }
        }catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onMyHealthClicked(){
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myhealth_module));
            startActivityWithClassName(clazz);
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void onSymptomCheckerClicked(){
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_symptomchecker_module));
            startActivityWithClassName(clazz);
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void onMyAccountClicked(){
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myaccount_module));
            startActivityWithClassName(clazz);
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void onPersonalInfoClicked(View view) {
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myhealth_module));
            Method method = clazz.getMethod("getSelectedTabFromMedicalHistory", Context.class, Integer.class);
            final Intent intent = (Intent) method.invoke(null, getBaseContext(), 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch(ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        } catch(Exception e){
            e.printStackTrace();
        }
        finish();
    }

    public void onPreferedStoreClicked(View view) {
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myhealth_module));
            Method method = clazz.getMethod("getSelectedTabFromMedicalHistory", Context.class, Integer.class);
            final Intent intent = (Intent) method.invoke(null, getBaseContext(), 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }catch(ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        } catch(Exception e){
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onAppointmentClicked(Appointment appointment) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);
        try {
            Class clazz = Class.forName("com.mdlive.sav.appointment.AppointmentActivity");
            Method method = clazz.getMethod("getAppointmentIntent", Context.class, Appointment.class);
            startActivity((Intent)method.invoke(null, getBaseContext(), appointment));
        }catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCloseDrawer() {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);
    }

    private void onAddChildSelcted(final User user, final int dependentUserSize) {
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());

        if (userBasicInfo.getRemainingFamilyMembersLimit() < 1) {
            MdliveUtils.showAddChildExceededDialog(this,userBasicInfo.getAssistPhoneNumber());
        } else {
            Intent addFamilyMember = new Intent(getBaseContext(), AddFamilyMemberActivity.class);
            startActivity(addFamilyMember);
        }
    }

    public void shareApplication() {
        SocialSharingHandler sShare = new SocialSharingHandler();
        try {
            sShare.doSendIntent(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPinTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void clearMinimizedTime() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Log.d("Timer", "clear called");
            }
        }, 1000);
    }

    private boolean showPinScreen() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final long lastTime = preferences.getLong(PreferenceConstants.TIME_KEY, -1l);

        if (lastTime < 0) {
            return false;
        }

        final long difference = System.currentTimeMillis() - lastTime;
        return difference > IntegerConstants.SESSION_TIMEOUT;
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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

    /**
     * wipe away previous activities from activity stack and exit the EmbedKit
     *
     */
    public void performCleanExit()
    {
        for(WeakReference<? extends MDLiveBaseAppcompatActivity> ref : VisitedScreens_SSO){
            MDLiveBaseAppcompatActivity screen = ref.get();
            if(screen!=null)
                screen.finish();
        }

        // now explicitly purge the weakref container, just to be safe
        VisitedScreens_SSO.clear();

    }
}
