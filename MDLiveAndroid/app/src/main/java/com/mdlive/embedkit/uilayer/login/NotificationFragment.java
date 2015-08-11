package com.mdlive.embedkit.uilayer.login;

import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Notifications;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class NotificationFragment extends MDLiveBaseFragment {
    private static final long MILIS_IN_SECOND = 1000;
    private static final long DURATION = 30 * MILIS_IN_SECOND;

    private TextView mMessagesTextView;
    private TextView mPersonalInfoTextView;
    private TextView mPreferedStoreTextView;
    private TextView mUpcomingAppoinmantTextView;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    public void setNotification(final UserBasicInfo userBasicInfo) {
        final Notifications notification = userBasicInfo.getNotifications();

        mMessagesTextView.setText(mMessagesTextView.getResources().getQuantityString(R.plurals.messages, notification.getMessages(), notification.getMessages()));

        mPersonalInfoTextView.setText(MdliveUtils.getDaysAgo(mPersonalInfoTextView.getContext(), userBasicInfo.getHealthLastUpdate()));

        final StringBuilder store = new StringBuilder();
        store.append(notification.getPharmacyDetails().getStoreName() + "\n");
        store.append(notification.getPharmacyDetails().getAddress1() + "\n");
        store.append(notification.getPharmacyDetails().getState() + "," + notification.getPharmacyDetails().getState() + " " + notification.getPharmacyDetails().getZipcode());
        mPreferedStoreTextView.setText(store.toString());

        if (notification.getUpcomingAppointments() > 0) {

        } else {
            mUpcomingAppoinmantTextView.setText(mUpcomingAppoinmantTextView.getResources().getString(R.string.no_upcoming_appoinments));
        }
    }
}
