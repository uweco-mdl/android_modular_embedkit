/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthActivity;
import com.mdlive.embedkit.uilayer.familyhistory.MDLiveFamilyActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.lifestyle.MDLiveLifestyleActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyFragment;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class MedicalHistoryActivity extends MDLiveBaseAppcompatActivity implements MedicalHistoryFragment.OnMedicalHistoryResponse{

    public static final String TAG = "MYHEALTH";
    private static final String SELECTED_TAB = "slected_tab";

    public static Intent getSelectedTabFromMedicalHistory(final Context context, final int selectedTab) {
        final Intent intent = new Intent(context, MedicalHistoryActivity.class);
        intent.putExtra(SELECTED_TAB, selectedTab);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_tab_activity);

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.my_health));
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            if (getIntent().getExtras() != null && getIntent().hasExtra(SELECTED_TAB)) {
                viewPager.setCurrentItem(getIntent().getIntExtra(SELECTED_TAB, 0));
            }
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL|TabLayout.GRAVITY_CENTER);
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

    /**
     *
     * The fragments are added into viewpager.
     *
     * @param viewPager - The viewpager instance.
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MedicalHistoryFragment(), getString(R.string.medical_history));
        adapter.addFragment(MDLivePharmacyFragment.newInstance(), getString(R.string.pharmacy));
        adapter.addFragment(MDLiveMyHealthProvidersFragment.newInstance(), getString(R.string.providers));
        adapter.addFragment(MDLiveMyHealthVisitsFragment.newInstance(), getString(R.string.visits));
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



    // Medical History MDLIVE


    /**
     * This function is used to initialize clickListners of Buttons used in MedicalHistory page
     */
    public void pediatricOnClick(View view) {
        Intent i = new Intent(getBaseContext(), MDLivePediatric.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MedicalHistoryActivity.this);
    }

    public void MyHealthConditionsLlOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveAddConditions.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MedicalHistoryActivity.this);
    }

    public void MedicationsLlOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveAddMedications.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MedicalHistoryActivity.this);

    }

    public void AllergiesLlOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveAddAllergies.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MedicalHistoryActivity.this);
    }

    public void BehaviouralHealthLlOnClick(View view){
        Intent i = new Intent(MedicalHistoryActivity.this, MDLiveBehaviouralHealthActivity.class);
        startActivity(i);
    }

    public void LifestyleLlOnClick(View view){
        Intent i = new Intent(MedicalHistoryActivity.this, MDLiveLifestyleActivity.class);
        startActivity(i);
    }

    public void familyHistoryOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveFamilyActivity.class);
        startActivity(i);
    }

    public void ProceduresLlOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveAddProcedures.class);
        startActivity(i);
    }

    /**
     * This function handles click listener of changePharmacyButton
     *
     * @param view - view of button which is called.
     */
    public void changePharmacyButtonOnClick(View view) {
        Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
        startActivity(i);
    }
}
