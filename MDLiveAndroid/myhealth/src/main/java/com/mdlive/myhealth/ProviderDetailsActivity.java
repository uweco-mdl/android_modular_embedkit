package com.mdlive.myhealth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryFragment;
import com.mdlive.myhealth.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.util.ArrayList;
import java.util.List;

public class ProviderDetailsActivity extends MDLiveBaseAppcompatActivity {

    public static final String TAG = "ProviderDetails";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_tab_activity);
        clearMinimizedTime();

        Log.d("INSIDE --->>>>", "INSIDE --->>>>");
/* Pass 1 for Dev env,Pass 2 for QA env, Pass 3 for Stage env, Pass 4 for Prod env, 5 for Pluto*/
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.mdl_my_provider).toUpperCase());
            ((ImageView) findViewById(R.id.left_icon)).setImageResource(R.drawable.back_arrow_hdpi);
            elevateToolbar(toolbar);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        findViewById(R.id.tabs).setVisibility(View.GONE);
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
    public void onLeftDrawerClicked(View view) {
        MdliveUtils.hideSoftKeyboard(ProviderDetailsActivity.this);
        onBackPressed();
    }

    /**
     *
     * The fragments are added into viewpager.
     *
     * @param viewPager - The viewpager instance.
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(ProviderDetailsFragment.newInstance(getIntent().getStringExtra("ProviderID")), getString(R.string.mdl_providers));
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}
