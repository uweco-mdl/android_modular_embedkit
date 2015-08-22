package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.UpcominAppointmentAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Notifications;
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

    private static final long MILIS_IN_SECOND = 1000;
    private static final long DURATION = 30 * MILIS_IN_SECOND;

    private PendingAppointment mPendingAppointment;

    private TextView mMessagesTextView;
    private TextView mPersonalInfoTextView;
    private TextView mPreferedStoreTextView;
    private TextView mUpcomingAppoinmantTextView;
    private ListView mUpcomingAppoinmantListView;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadPendingAppoinments();

            mHandler.postDelayed(mRunnable, DURATION);
        }
    };

    private Notification mNotification;

    public static NotificationFragment newInstance() {
        final NotificationFragment fragment = new NotificationFragment();
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

        mMessagesTextView = (TextView) view.findViewById(R.id.notification_fragment_messages_text_view);
        mPersonalInfoTextView = (TextView) view.findViewById(R.id.notification_fragment_personal_text_view);
        mPreferedStoreTextView = (TextView) view.findViewById(R.id.notification_fragment_prefered_store_text_view);
        mUpcomingAppoinmantTextView = (TextView) view.findViewById(R.id.notification_fragment_upcoming_appoinment_text_view);
        mUpcomingAppoinmantListView = (ListView) view.findViewById(R.id.notification_fragment_upcoming_appoinment_list_view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
        if (userBasicInfo != null) {
            setNotification(userBasicInfo);
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
    }

    public void setNotification(final UserBasicInfo userBasicInfo) {
        final Notifications notification = userBasicInfo.getNotifications();

        mMessagesTextView.setText(mMessagesTextView.getResources().getQuantityString(R.plurals.messages, notification.getMessages(), notification.getMessages()));

        if (userBasicInfo.getHealthLastUpdate() < 0) {
            mPersonalInfoTextView.setText(getActivity().getString(R.string.no_health_record));
        } else {
            mPersonalInfoTextView.setText(MdliveUtils.getDaysAgo(mPersonalInfoTextView.getContext(), userBasicInfo.getHealthLastUpdate()));
        }

        final StringBuilder store = new StringBuilder();

        if (notification.getPharmacyDetails() != null) {
            store.append(notification.getPharmacyDetails().getStoreName() + "\n");
            store.append(notification.getPharmacyDetails().getAddress1() + "\n");
            store.append(notification.getPharmacyDetails().getState() + "," + notification.getPharmacyDetails().getState() + " " + notification.getPharmacyDetails().getZipcode());
        } else {
            store.append(getActivity().getString(R.string.no_prefered_store));
        }
        mPreferedStoreTextView.setText(store.toString());
    }

    private void loadPendingAppoinments() {
        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logD("PendingAppoinments", response.toString().trim());
                mPendingAppointment = PendingAppointment.fromJsonString(response.toString().trim());

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

    private void onNotificationLoaded() {
        if (mPendingAppointment.getAppointments() != null &&
                mPendingAppointment.getAppointments().size() > 0) {
            mUpcomingAppoinmantTextView.setVisibility(View.GONE);
            mUpcomingAppoinmantListView.setVisibility(View.VISIBLE);

            if (mUpcomingAppoinmantListView != null) {
                mUpcomingAppoinmantListView.setAdapter(null);
                final UpcominAppointmentAdapter adapter = new UpcominAppointmentAdapter(mPendingAppointment.getAppointments());
                mUpcomingAppoinmantListView.setAdapter(adapter);
                mUpcomingAppoinmantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mOnAppointmentClicked != null) {
                            mOnAppointmentClicked.onAppointmentClicked(adapter.getAppointment(position));
                        }
                    }
                });
            }
        } else {
            mUpcomingAppoinmantTextView.setText(mUpcomingAppoinmantTextView.getResources().getString(R.string.no_upcoming_appoinments));
            mUpcomingAppoinmantListView.setVisibility(View.GONE);
        }
    }

    public interface OnAppointmentClicked {
        void onAppointmentClicked(final Appointment appointment);
    }
}
