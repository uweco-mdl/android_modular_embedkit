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

package com.mdlive.myhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthActivity;
import com.mdlive.embedkit.uilayer.familyhistory.MDLiveFamilyActivity;
import com.mdlive.embedkit.uilayer.lifestyle.MDLiveLifestyleActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveAddAllergies;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveAddConditions;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveAddMedications;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveAddProcedures;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryFragment;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyFragment;
import com.mdlive.myhealth.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.GoogleFitUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class MedicalHistoryActivity extends MDLiveBaseAppcompatActivity implements MedicalHistoryFragment.OnGoogleFitSyncResponse, MedicalHistoryFragment.OnGoogleFitGetData {

    public static final String TAG = "MYHEALTH";
    private static final String SELECTED_TAB = "slected_tab";
    public static boolean isFromMyHealth = false;
    ViewPager viewPager;

    public static Intent getSelectedTabFromMedicalHistory(final Context context, final int selectedTab) {
        final Intent intent = new Intent(context, MedicalHistoryActivity.class);
        intent.putExtra(SELECTED_TAB, selectedTab);
        return intent;
    }

    public ViewPager getViewPager(){
        return viewPager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_tab_activity);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_my_health));
        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setHeaderTitle(R.string.mdl_medical_history_txt);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setId(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);

        }

        // Initializing the "GOOGLE_FIT_SYNC_BTN_CLICKED" shared preference to 'false'. This preference protects
        // the user from being exposed to continuance selection prompts when there is more than one Gmail acct on the device.
        SharedPreferences sharedPref = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        if (!userPrefs.contains(PreferenceConstants.GOOGLE_FIT_SYNC_BTN_CLICKED)) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putBoolean(PreferenceConstants.GOOGLE_FIT_SYNC_BTN_CLICKED, false);
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL | TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);

        if (getIntent().getExtras() != null && getIntent().hasExtra(SELECTED_TAB)) {
            viewPager.setCurrentItem(getIntent().getIntExtra(SELECTED_TAB, 0));
            viewPager.invalidate();
        }
        if (savedInstanceState == null) {
// **** DEBUG ONLY.   o.uwechue
Log.e("Medical History","+++++++++++++++++\nAbout to create nav drawer");
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

    /**
     *
     * The fragments are added into viewpager.
     *
     * @param viewPager - The viewpager instance.
     */
    private void setupViewPager(ViewPager viewPager) {
        final List<Integer> headerNames = new ArrayList<>();
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MedicalHistoryFragment(), getString(R.string.mdl_medical_history));
        adapter.addFragment(MDLivePharmacyFragment.newInstance(), getString(R.string.mdl_pharmacy));
        adapter.addFragment(MDLiveMyHealthProvidersFragment.newInstance(), getString(R.string.mdl_providers));
        adapter.addFragment(MDLiveMyHealthVisitsFragment.newInstance(), getString(R.string.mdl_visits));
        headerNames.add(R.string.mdl_medical_history);
        headerNames.add(R.string.mdl_pharmacy);
        headerNames.add(R.string.mdl_providers);
        headerNames.add(R.string.mdl_visits);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setHeaderTitle(headerNames.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void getGoogleFitData(String data) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        if(frag!=null && frag instanceof MedicalHistoryFragment && ((MedicalHistoryFragment)frag).mHealthSyncContainer != null){
            ((MedicalHistoryFragment)frag).setFitDataEvent(data);
        }
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
    public void setHeaderTitle(int titleId) {
        ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(titleId).toUpperCase());
    }

    /**
     * This function is used to initialize clickListeners of Buttons used in MedicalHistory page
     */
    public void pediatricOnClick(View view) {
        Intent i = new Intent(getBaseContext(), MDLivePediatric.class);
        i.putExtra("FROM_MEDICAL_HISTORY",true);
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

    public void myRecordsOnClick(View view){
        Intent i = new Intent(getBaseContext(), MDLiveMyRecords.class);
        startActivity(i);
    }

    public void SyncAction(View view){
        Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        if(frag!=null && frag instanceof MedicalHistoryFragment && ((MedicalHistoryFragment)frag).mHealthSyncContainer != null){
            ((MedicalHistoryFragment)frag).mHealthSyncContainer.setVisibility(View.GONE);
        }
        SharedPreferences sharedPref = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, false);
        editor.putBoolean(PreferenceConstants.GOOGLE_FIT_SYNC_BTN_CLICKED, true);
        editor.commit();
        GoogleFitUtils.getInstance().buildFitnessClient(false,null,this);
    }

    public void SyncNotNowAction(View view){
        Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        if(frag!=null && frag instanceof MedicalHistoryFragment && ((MedicalHistoryFragment)frag).mHealthSyncContainer != null){
            ((MedicalHistoryFragment)frag).mHealthSyncContainer.setVisibility(View.GONE);
        }

        SharedPreferences sharedPref = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, false);
        editor.putBoolean(PreferenceConstants.GOOGLE_FIT_SYNC_BTN_CLICKED, false);
        editor.commit();
    }

    /**
     * This function handles click listener of changePharmacyButton
     *
     * @param view - view of button which is called.
     */
    public void changePharmacyButtonOnClick(View view) {
        Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
        i.putExtra("FROM_MY_HEALTH",true);
        i.putExtra("PHARMACY_SELECTED", true);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra("FROM_PHARMACY")){
            getViewPager().setCurrentItem(0);
        }else if(getIntent().hasExtra("FROM_SELECTION")){
            reloadSlidingMenu();
            getViewPager().setCurrentItem(1);
        }
    }

    public void setHealthStatus(String data){
        Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        if(frag!=null && frag instanceof MedicalHistoryFragment){
            ((MedicalHistoryFragment)frag).setFitStatus(data);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GoogleFitUtils.getInstance().REQUEST_OAUTH){
            GoogleFitUtils.getInstance().authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (! GoogleFitUtils.getInstance().mClient.isConnecting() && ! GoogleFitUtils.getInstance().mClient.isConnected()) {
                    GoogleFitUtils.getInstance().mClient.connect();
                }
            }
        }
    }
}
