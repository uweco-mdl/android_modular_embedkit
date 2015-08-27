package com.mdlive.embedkit.uilayer.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;

/**
 * Created by dhiman_da on 8/27/2015.
 */
public class ForgotPinFragment extends MDLiveBaseFragment {
    public static ForgotPinFragment newInstance() {
        final ForgotPinFragment fragment = new ForgotPinFragment();
        return fragment;
    }

    public ForgotPinFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onResetPinClicked() {
        Toast.makeText(getActivity(), "To Be implemented", Toast.LENGTH_SHORT).show();
    }


    
}
