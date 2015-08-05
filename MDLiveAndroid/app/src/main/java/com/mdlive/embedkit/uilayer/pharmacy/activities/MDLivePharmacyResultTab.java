package com.mdlive.embedkit.uilayer.pharmacy.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.ViewGroup;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.pharmacy.fragment.MDLivePharmacyResultTabListFragment;
import com.mdlive.embedkit.uilayer.pharmacy.fragment.MDLivePharmacyResultTabMapFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;


/**
 * The wrapper class for Pharmacy Result Tab Activity. This layout have the details tab of list & map datas
 * list tab have the details of list of pharmacies. By clicking on the list item will redirect to MDLBTPharmacy_Details page
 * map tab have the markers of list of pharmacies.
 */

public class MDLivePharmacyResultTab extends FragmentActivity {

    private FragmentTabHost mTabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy_searchlist);
        initializeViews();
    }

  /*
  * This function is mainly focused on initializing view in layout.
  * mTabHost is used to define tab classes of
  *          1. MDLBTPharmacy_ResultTab_List_Fragment
  *          2. MDLBTPharmacy_ResultTab_Map_Fragment
  *
  */
    public void initializeViews(){

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("List").setIndicator("List", null),
                MDLivePharmacyResultTabListFragment.class, null);

        mTabHost.addTab(
                mTabHost.newTabSpec("Map").setIndicator("Map", null),
                MDLivePharmacyResultTabMapFragment.class, null);

    }


}