package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.UpcominAppointmentAdapter;
import com.mdlive.embedkit.uilayer.payment.MDLiveStartVisit;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Notifications;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.OncallAppointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PendingAppointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class NotificationFragment extends MDLiveBaseFragment {
    private OnAppointmentClicked mOnAppointmentClicked;
    private NotifyDashboard mNotifyDashboard;

    private static final long MILIS_IN_SECOND = 1000;
    private static final long DURATION = 30 * MILIS_IN_SECOND;

    private PendingAppointment mPendingAppointment;

    private View mMessagesLinearLayout;
    private View mNoAppointmentLinearLayout;
    private TextView mMessagesTextView;
    private TextView mPersonalInfoTextView;
    private TextView mPreferedStoreTextView;
    private TextView mUpcomingAppoinmantTextView;
    private ListView mUpcomingAppoinmantListView;

    private LinearLayout onCallNotificationLayout;
    private TextView onCallNotifyTextview;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadPendingAppoinments();

            mHandler.postDelayed(mRunnable, DURATION);
        }
    };

    private Notification mNotification;

    public static NotificationFragment instance;

    public static NotificationFragment getInstance(){
        return instance;
    }

    public static NotificationFragment newInstance() {
        final NotificationFragment fragment = new NotificationFragment();
        instance = fragment;
        return fragment;
    }

    public NotificationFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnAppointmentClicked = (OnAppointmentClicked) activity;
            mNotifyDashboard = (NotifyDashboard) activity;
        } catch (ClassCastException cce) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesLinearLayout = view.findViewById(R.id.notification_fragment_messages_linear_layout);
        mNoAppointmentLinearLayout = view.findViewById(R.id.notification_fragment_upcoming_appoinment_linear_layout);
        mMessagesTextView = (TextView) view.findViewById(R.id.notification_fragment_messages_text_view);
        mPersonalInfoTextView = (TextView) view.findViewById(R.id.notification_fragment_personal_text_view);
        mPreferedStoreTextView = (TextView) view.findViewById(R.id.notification_fragment_prefered_store_text_view);
        mUpcomingAppoinmantTextView = (TextView) view.findViewById(R.id.notification_fragment_upcoming_appoinment_text_view);
        mUpcomingAppoinmantListView = (ListView) view.findViewById(R.id.notification_fragment_upcoming_appoinment_list_view);
        onCallNotificationLayout= (LinearLayout) view.findViewById(R.id.onCallNotifyLayout);
        onCallNotifyTextview= (TextView) view.findViewById(R.id.oncall_appointment_textview);

        view.findViewById(R.id.notification_button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
        if (userBasicInfo != null) {
            setNotification(userBasicInfo);
        }

        if (mNotifyDashboard != null) {
            mNotifyDashboard.onHideNotifyDashboard();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.post(mRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnAppointmentClicked = null;
        mNotifyDashboard = null;
    }

    public void setNotification(final UserBasicInfo userBasicInfo) {
        try {
            final Notifications notification = userBasicInfo.getNotifications();

            if (userBasicInfo.getPersonalInfo().getEmailConfirmed()) {
                mMessagesTextView.setText(mMessagesTextView.getResources().getQuantityString(R.plurals.mdl_messages, notification.getMessages(), notification.getMessages()));
                mMessagesLinearLayout.setVisibility(View.VISIBLE);
            } else {
                //mMessagesTextView.setVisibility(View.GONE);
                mMessagesLinearLayout.setVisibility(View.GONE);
            }

            mPersonalInfoTextView.setText(userBasicInfo.getHealthMessage()  + ".");

            final StringBuilder store = new StringBuilder();

            if (notification.getPharmacyDetails() != null) {
                store.append(notification.getPharmacyDetails().getStoreName() + "\n");
                store.append(notification.getPharmacyDetails().getAddress1() + ",\n");
                store.append(notification.getPharmacyDetails().getCity() + ", " +
                        notification.getPharmacyDetails().getState() + " " +
                        notification.getPharmacyDetails().getZipcode()  + ".");
            } else {
                store.append(getActivity().getString(R.string.mdl_no_prefered_store));
            }
            mPreferedStoreTextView.setText(store.toString());
        } catch (Exception e) {

        }
    }

    private void loadPendingAppoinments() {
        mUpcomingAppoinmantListView.setAdapter(null);
        if (MdliveUtils.isNetworkAvailable(getActivity())) {
        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logD("PendingAppoinments", response.toString().trim());
                mPendingAppointment = PendingAppointment.fromJsonString(response.toString().trim());
                mPendingAppointment.saveToSharedPreference(getActivity(), response.toString().trim());
                onNotificationLoaded();
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };

        final MDLivePendigVisitService service = new MDLivePendigVisitService(getActivity(), null);
        service.getUserPendingHistory(successCallBackListener, errorListener);
        }
    }

    private void onNotificationLoaded() {
        if (mPendingAppointment.getAppointments() != null && mPendingAppointment.getAppointments().size() > 0) {
            mUpcomingAppoinmantTextView.setVisibility(View.GONE);
            mNoAppointmentLinearLayout.setVisibility(View.GONE);
            mUpcomingAppoinmantListView.setVisibility(View.VISIBLE);

            if (mUpcomingAppoinmantListView != null) {
                mUpcomingAppoinmantListView.setOnItemClickListener(null);
                mUpcomingAppoinmantListView.setAdapter(null);
                final UpcominAppointmentAdapter adapter = new UpcominAppointmentAdapter(mPendingAppointment.getAppointments());
                mUpcomingAppoinmantListView.setAdapter(adapter);
                mUpcomingAppoinmantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mOnAppointmentClicked != null) {
                            mOnAppointmentClicked.onCloseDrawer();
                            mOnAppointmentClicked.onAppointmentClicked(adapter.getAppointment(position));
                        }
                    }
                });

                // For Showing Dashboard Notification
                if (mNotifyDashboard != null) {
                    if ( mPendingAppointment.getAppointments().size() > 0) {
                        mNotifyDashboard.onShowNofifyDashboard(mPendingAppointment.getAppointments().get(0));
                    } else {
                        mNotifyDashboard.onHideNotifyDashboard();
                    }

                }
            }
        }else {
            mUpcomingAppoinmantTextView.setVisibility(View.VISIBLE);
            mNoAppointmentLinearLayout.setVisibility(View.VISIBLE);
            mUpcomingAppoinmantTextView.setText(mUpcomingAppoinmantTextView.getResources().getString(R.string.mdl_no_upcoming_appoinments));
            mUpcomingAppoinmantListView.setVisibility(View.GONE);
        }


        //Condition will handle if pending appointments for on call appointments
        if(mPendingAppointment.getOncallAppointments() != null && mPendingAppointment.getOncallAppointments().size() > 0){

            mUpcomingAppoinmantTextView.setVisibility(View.GONE);
            mNoAppointmentLinearLayout.setVisibility(View.GONE);
            onCallNotificationLayout.setVisibility(View.VISIBLE);
            String apptId = mPendingAppointment.getOncallAppointments().get(0).getId();
            Log.e("Appoint Ment Id Save", apptId);
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.APPT_ID, apptId);
            editor.commit();
            final StringBuilder builder = new StringBuilder();
            builder.append("Doctor On Call" + "\n");
            builder.append(TimeZoneUtils.convertMiliSeconedsToStringWithTimeZone(System.currentTimeMillis()/1000, "", getActivity()) + "\n");//Sending Current time for Doctoro on call

            builder.append(mPendingAppointment.getOncallAppointments().get(0).getApptType() + " " + getResources().getString(R.string.mdl_consultation) + "\n");

            onCallNotifyTextview.setText(builder.toString());

            Log.e("Appointment Side", mPendingAppointment.getOncallAppointments().get(0).getApptType());
            if(mPendingAppointment.getOncallAppointments().get(0).getApptType().equalsIgnoreCase("video")){
                onCallNotificationLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), MDLiveStartVisit.class);
                        startActivity(intent);
                        MdliveUtils.startActivityAnimation(getActivity());
                    }
                });
            }





                // For Showing Dashboard Notification
                if (mNotifyDashboard != null) {
                    if ( mPendingAppointment.getOncallAppointments().size() > 0) {
                        mNotifyDashboard.onShowNotifyOnCallDashBorad(mPendingAppointment.getOncallAppointments().get(0));
                    } else {
                        mNotifyDashboard.onHideNotifyDashboard();
                    }
                }
        }
    }
    public  void reloadPendingAppointment() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.post(mRunnable);
        }
    }

    public interface OnAppointmentClicked {
        void onAppointmentClicked(final Appointment appointment);
        void onCloseDrawer();
    }

    public interface NotifyDashboard {
        void onShowNofifyDashboard(final Appointment appointment);
        void onShowNotifyOnCallDashBorad(final OncallAppointment appointment);
        void onHideNotifyDashboard();
    }
}
