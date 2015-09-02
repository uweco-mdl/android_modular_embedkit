package com.mdlive.embedkit.uilayer.myaccounts;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.MessageCenterViewPagerAdapter;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by sanjibkumar_p on 7/27/2015.
 */
public class MyAccountActivity extends MDLiveBaseAppcompatActivity implements FragmentTabHost.OnTabChangeListener {

    private static final String MY_ACCOUNT_TAG = "Account";
    private static final String BILLING_TAG = "Billing";
    private static final String FAMILY_TAG = "Family";
//    MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_tab_activity);

        setTitle("");
//        adapter= new MyAdapter(getSupportFragmentManager());
        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.account_details).toUpperCase());
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
//            viewPager.setAdapter(adapter);
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState == null) {
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
    public void onBackPressed() {
        return;
    }

    private void setupViewPager(ViewPager viewPager) {
        final MessageCenterViewPagerAdapter adapter = new MessageCenterViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MyProfileFragment.newInstance(), getString(R.string.account));
        adapter.addFragment(BillingInformationFragment.newInstance(), getString(R.string.billing));
        adapter.addFragment(GetFamilyMemberFragment.newInstance(), getString(R.string.family_history));
        viewPager.setAdapter(adapter);
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
                onMessageClicked();
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

    @Override
    public void onTabChanged(String s) {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

//    public void onChangePasswordClicked() {
//        setFirstTabLayerLevel(1);
//    }

    public void onChangePinClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_content, OldPinFragment.newInstance(), MY_ACCOUNT_TAG).
                addToBackStack(MY_ACCOUNT_TAG).
                commit();
    }

    public void onSecurityQuestionClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_content, new SecurityQuestionsFragment(), MY_ACCOUNT_TAG).
                addToBackStack(MY_ACCOUNT_TAG).
                commit();
    }

//    public void setFirstTabLayerLevel(final int level) {
//        adapter.setFirstTabLayerLevel(level);
//        adapter.notifyDataSetChanged();
//
//        Toast.makeText(getBaseContext(), "Coming here", Toast.LENGTH_SHORT).show();
//    }
}
