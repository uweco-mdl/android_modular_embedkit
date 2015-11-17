package com.mdlive.messages.messagecenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageSentDetailsFragment extends MDLiveBaseFragment {
    private static final String SENT_MESSAGE_TAG = "sent_message";

    public static MessageSentDetailsFragment newInstance(final SentMessage sentMessage) {
        final Bundle args = new Bundle();
        args.putParcelable(SENT_MESSAGE_TAG, sentMessage);

        final MessageSentDetailsFragment messageSentDetailsFragment = new MessageSentDetailsFragment();
        messageSentDetailsFragment.setArguments(args);

        return messageSentDetailsFragment;

    }
    public MessageSentDetailsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_sent_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SentMessage sentMessage = getArguments().getParcelable(SENT_MESSAGE_TAG);

        final TextView subjectTextView = (TextView) view.findViewById(R.id.fragment_message_received_subject_text_view);
        if (subjectTextView != null) {
            subjectTextView.setText(sentMessage.subject);
        }

        final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.adapter_provider_image_view);
        if (circularNetworkImageView != null) {
            circularNetworkImageView.setImageUrl(sentMessage.providerImageUrl, ApplicationController.getInstance().getImageLoader(view.getContext()));
        }

        final TextView doctorNameTextView = (TextView) view.findViewById(R.id.fragment_message_received_doctor_name_text_view);
        if (doctorNameTextView != null) {
            doctorNameTextView.setText(sentMessage.from);
        }

        final TextView timeTextView = (TextView) view.findViewById(R.id.fragment_message_received_date_text_view);
        if (timeTextView != null) {
            timeTextView.setText(TimeZoneUtils.getReceivedSentTimeInDetails(sentMessage.inMilliseconds, sentMessage.timeZone, getActivity()));
        }

        final View replyView = view.findViewById(R.id.fragment_message_reply_image_view);
        if (replyView != null) {
            replyView.setVisibility(View.GONE);
        }

        final TextView detailsTextView = (TextView) view.findViewById(R.id.fragment_message_received_details_text_view);
        if (detailsTextView != null) {
            detailsTextView.setText(sentMessage.message);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SentMessage sentMessage = getArguments().getParcelable(SENT_MESSAGE_TAG);
        if (sentMessage != null) {
            callSentMessageRead(String.valueOf(sentMessage.messageId));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void callSentMessageRead(final String id) {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
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

        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.getMessaseSentRead(id, successListener, errorListener, null);
    }
}
