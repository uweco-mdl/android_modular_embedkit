package com.mdlive.embedkit.uilayer.myaccounts;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;

/**
 * Created by venkataraman_r on 7/26/2015.
 */
public class MyAccountsHome extends Fragment {

    private FragmentTabHost mTabHost;
    SharedPreferences sharedpreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myAccountsHome = inflater.inflate(R.layout.myaccounts_tabs, null);

        mTabHost = (FragmentTabHost)myAccountsHome.findViewById(android.R.id.tabhost);
        sharedpreferences = getActivity().getSharedPreferences("MDLIVE_BILLING", Context.MODE_PRIVATE);

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator(prepareTabView("Account", R.drawable.icon_i)),
                MyProfileFragment.class, null);

//        if(sharedpreferences.getBoolean("Add_CREDIT_CARD",false)) {
//            mTabHost.addTab(
//                    mTabHost.newTabSpec("tab2").setIndicator(prepareTabView("Billing", R.drawable.icon_i)),
//                    ViewCreditCard.class, null);
//        }
//        else
//        {
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator(prepareTabView("Billing", R.drawable.icon_i)),
                    AddCreditCard.class, null);
//        }

        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator(prepareTabView("Family", R.drawable.icon_i)),
                GetFamilyMemberFragment.class, null);

        return myAccountsHome;
    }

    private View prepareTabView(String text, int resId) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.myaccounts_tab_indicator, null);
        ImageView iv = (ImageView) view.findViewById(R.id.TabImageView);
        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        iv.setImageResource(resId);
        tv.setText(text);
        return view;
    }
}