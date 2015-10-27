package com.mdlive.embedkit.uilayer.appointment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendingVisitService;

import org.json.JSONException;
import org.json.JSONObject;

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

        final Appointment appointment = getArguments().getParcelable(APPOINTMENT_TAG);
        logD("Appointment", appointment.toString());
        logD("AppointmentType", appointment.getApptType());
        logD("AppointmentTime", appointment.getStartTime());
        logD("AppointmentMilli", appointment.getInMilliseconds()+"");

        final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.doctor_image_view);
        if (circularNetworkImageView != null) {
            circularNetworkImageView.setImageUrl(appointment.getPhysicianImageUrl(), ApplicationController.getInstance().getImageLoader(view.getContext()));
        }

        ((TextView) view.findViewById(R.id.doctor_name_text)).setText(appointment.getPhysicianName());

        if(appointment.getRole()!=null){
        ((TextView) view.findViewById(R.id.doctor_degree_text_view)).setText(appointment.getRole());}
        else
        {
            ((TextView) view.findViewById(R.id.doctor_degree_text_view)).setText("");
        }

        ((TextView) view.findViewById(R.id.consulatation_type_text_view)).setText(appointment.getApptType() + " " + getString(R.string.mdl_consultation));
        ((TextView) view.findViewById(R.id.consulatation_daye_text_view)).setText(MdliveUtils.convertMiliSeconedsToStringWithTimeZone(appointment.getInMilliseconds(), appointment.getTimeZone()));

        final int type = MdliveUtils.getRemainigTimeToAppointment(appointment.getInMilliseconds(), "EDT");
        Log.e("Appmtfragtimestamp",appointment.getInMilliseconds()+"");
        switch (type) {

            case 0 :
                // Ten minutes case
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                String Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
                Log.e("Print Time",Time);
                String consultationType =  sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, "");
                view.findViewById(R.id.help).setVisibility(View.GONE);

                // For Phone Do not show Start Button
                if(consultationType.equalsIgnoreCase("phone")) {
                    view.findViewById(R.id.start_appointment).setVisibility(View.GONE);
                    view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                    view.findViewById(R.id.help).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.phoneHelplayout).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.consulatation_type_text_view)).setText(consultationType + " " + getString(R.string.mdl_consultation));
                } else{
                    final SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                    final String timestampString = preferences.getString((MdliveUtils.getRemoteUserId(getActivity()) + PreferenceConstants.SELECTED_TIMESTAMP), null);
                    if (timestampString != null) {
                        final long timestamp = Long.parseLong(timestampString) * 1000;
                        final long now = System.currentTimeMillis();
                        final long difference = timestamp - now;

                        // Real Ten minute check
                        if (difference < 10 * 60 * 1000) {
                            view.findViewById(R.id.start_appointment).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                            view.findViewById(R.id.help).setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.start_appointment).setVisibility(View.GONE);
                            view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                            view.findViewById(R.id.help).setVisibility(View.VISIBLE);
                        }
                    } else {
                        view.findViewById(R.id.start_appointment).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                        view.findViewById(R.id.help).setVisibility(View.GONE);
                    }
                }
                break;

            // 24 hours
            case 1 :
                view.findViewById(R.id.help).setVisibility(View.VISIBLE);
                view.findViewById(R.id.start_appointment).setVisibility(View.GONE);
                view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                break;

            // More than 24 hours
            case 2 :
                view.findViewById(R.id.help).setVisibility(View.VISIBLE);
                view.findViewById(R.id.start_appointment).setVisibility(View.GONE);
                view.findViewById(R.id.cancel_appointment).setVisibility(View.GONE);
                break;
        }
    }

    public void onCancelAppointmentClicked() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logD("PendingAppoinments", response.toString().trim());
                hideProgressDialog();

                try {
                    if (response.getInt("success") == 200) {
                        MdliveUtils.showDialog(getActivity(), response.getString("message"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        });
                    }
                } catch (JSONException e) {

                }
            }
        };


        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        final Appointment appointment = getArguments().getParcelable(APPOINTMENT_TAG);

        final MDLivePendingVisitService service = new MDLivePendingVisitService(getActivity(), null);
        service.deleteAppointment(String.valueOf(appointment.getId()), successCallBackListener, errorListener);
    }
}
