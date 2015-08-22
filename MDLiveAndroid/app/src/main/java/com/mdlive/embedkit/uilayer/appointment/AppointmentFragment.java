package com.mdlive.embedkit.uilayer.appointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class AppointmentFragment extends MDLiveBaseFragment {
    private static final String APPOINTMENT_TAG = "APPOINTMENT";

    public static AppointmentFragment newInstance(final Appointment appointment) {
        final Bundle args = new Bundle();
        args.putParcelable(APPOINTMENT_TAG, appointment);

        final AppointmentFragment fragment = new AppointmentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public AppointmentFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appoinment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
