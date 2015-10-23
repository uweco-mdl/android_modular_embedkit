package com.mdlive.embedkit.uilayer.myaccounts;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.adapter.MyAccountViewPagerAdapter;
import com.mdlive.embedkit.uilayer.myaccounts.GetFamilyMemberFragment.OnChildAdded;

/**
 * Created by sanjibkumar_p on 7/27/2015.
 */
public class MyAccountActivity extends MDLiveBaseAppcompatActivity implements OnTabChangeListener, OnChildAdded {

    private static final String MY_ACCOUNT_TAG = "Account";
    private static final String BILLING_TAG = "Billing";
    private static final String FAMILY_TAG = "Family";
//    MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_my_account_activity);
        clearMinimizedTime();

        setTitle("");
//        adapter= new MyAdapter(getSupportFragmentManager());
        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.mdl_account_details).toUpperCase());
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
        onHomeClicked();
    }

    private void setupViewPager(ViewPager viewPager) {
        final MyAccountViewPagerAdapter adapter = new MyAccountViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MyProfileFragment.newInstance(), getString(R.string.mdl_account));
        adapter.addFragment(BillingInformationFragment.newInstance(), getString(R.string.mdl_billing));
        adapter.addFragment(GetFamilyMemberFragment.newInstance(), getString(R.string.mdl_family_history));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabChanged(String s) {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    public void onChangePinClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_content, OldPinFragment.newInstance(true), MY_ACCOUNT_TAG).
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

    @Override
    public void reloadNavigartion() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).reload();
        }
    }
}
