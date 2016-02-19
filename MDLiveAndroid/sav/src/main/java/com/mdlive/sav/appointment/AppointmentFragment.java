package com.mdlive.sav.appointment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
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
    private void renderUI(View view, Appointment appointment){
        try{
            final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.doctor_image_view);
            if (circularNetworkImageView != null) {
                circularNetworkImageView.setImageUrl(appointment.getPhysicianImageUrl(), ApplicationController.getInstance().getImageLoader(view.getContext()));
            }

            ((TextView) view.findViewById(R.id.doctor_name_text)).setText(appointment.getPhysicianName());
            if(appointment.getRole().equalsIgnoreCase("null")){
                ((TextView) view.findViewById(R.id.doctor_degree_text_view)).setText("");
            }else{
                ((TextView) view.findViewById(R.id.doctor_degree_text_view)).setText(appointment.getRole());
            }


            ((TextView) view.findViewById(R.id.consulatation_type_text_view)).setText(appointment.getApptType() + " " + getString(R.string.mdl_consultation));

            ((TextView) view.findViewById(R.id.consulatation_daye_text_view)).setText(TimeZoneUtils.convertMiliSeconedsToStringMonthWithTimeZone(appointment.getInMilliseconds(), getActivity()));

            final int type = TimeZoneUtils.getRemainigTimeToAppointment(appointment.getInMilliseconds(), "", getActivity());
            Log.v("Appmtfragtimestamp",appointment.getInMilliseconds()+"");
            switch (type) {

                case 0 :
                    // Ten minutes case
                    SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                    String Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
                Log.v("Print Time",Time);
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
                    view.findViewById(R.id.help).setVisibility(View.GONE);
                    view.findViewById(R.id.start_appointment).setVisibility(View.GONE);
                    view.findViewById(R.id.phoneHelplayout).setVisibility(View.GONE);
                    view.findViewById(R.id.cancel_appointment).setVisibility(View.VISIBLE);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Appointment appointment = getArguments().getParcelable(APPOINTMENT_TAG);

        if(appointment.getApptType() != null && !appointment.getApptType().equalsIgnoreCase("null")) {
            renderUI(view, appointment);
        }else{
            getAppointmentDetails(view, appointment.getStringID() == null ? (appointment.getId() + "") : appointment.getStringID());
        }

    }
    public void getAppointmentDetails(final View view, String id) {
        showProgressDialog();
        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("PendingAppoinments", response.toString());
                try{
                    if(response.has("appointment")){
                        JSONObject apt = response.getJSONObject("appointment");
                        Appointment appointment = new Appointment();

                        appointment.setApptType(apt.getString("appt_type"));
                        appointment.setChiefComplaint(apt.getString("chief_complaint"));
                        appointment.setInMilliseconds(apt.getInt("in_milliseconds"));
                        appointment.setPhysicianImageUrl(apt.getString("physician_image_url"));
                        appointment.setPhysicianName(apt.getString("physician_name"));
                        appointment.setProviderId(apt.getInt("provider_id"));
                        appointment.setStartTime(apt.getString("start_time"));
                        appointment.setTimeZone(apt.getString("time_zone"));
                        appointment.setRole(apt.getString("role"));
                        try{
                            appointment.setId(apt.getInt("id"));
                        }catch (Exception e){
                            appointment.setStringID(apt.getString("id"));
                        }
                        renderUI(view, appointment);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                hideProgressDialog();

            }
        };


        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    if(error.networkResponse.statusCode == MDLiveConfig.HTTP_NOT_FOUND){
                        MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_appt_error), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    getActivity().finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        final MDLivePendingVisitService service = new MDLivePendingVisitService(getActivity(), null);
        service.getAppointment(id, successCallBackListener, errorListener);
    }

    public void onCancelAppointmentClicked() {
        showProgressDialog();
        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

                try {
                    if (response.has("message")) {
                        MdliveUtils.showDialog(getActivity(), response.getString("message"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    final Intent intent = new Intent(getActivity(), MDLiveDashboardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_cancel_appointment_error), new DialogInterface.OnClickListener() {
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
