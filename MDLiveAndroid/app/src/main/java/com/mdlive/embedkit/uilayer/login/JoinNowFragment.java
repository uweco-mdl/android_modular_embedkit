package com.mdlive.embedkit.uilayer.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mdlive.embedkit.R;

/**
 * Created by venkataraman_r on 7/22/2015.
 */

public class JoinNowFragment extends Fragment{

    public static JoinNowFragment newInstance() {
        final JoinNowFragment joinNowFragment = new JoinNowFragment();
        return joinNowFragment;
    }

    public JoinNowFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View joinNow = inflater.inflate(R.layout.mdlive_joinnow,null);

        return joinNow;
    }
}
