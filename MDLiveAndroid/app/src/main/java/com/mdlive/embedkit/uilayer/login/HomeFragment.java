package com.mdlive.embedkit.uilayer.login;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by venkataraman_r on 7/21/2015.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{

    Toolbar toolbar;

    public static HomeFragment newInstance() {
        final HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View homePage = inflater.inflate(R.layout.fragment_home,null);
        init(homePage);

        return homePage;
    }

    public void init(View homePage)
    {
        TextView emailConfirmation = (TextView)homePage.findViewById(R.id.email_unconfirmed);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.mdl_app_name));

        emailConfirmation.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.email_unconfirmed) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container, EmailConfirmFragment.newInstance()).commit();
        }
    }

}
