package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

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

        final SentMessage sentMessage = (SentMessage) getArguments().getParcelable(SENT_MESSAGE_TAG);

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
            timeTextView.setText(MdliveUtils.getReceivedSentTime(sentMessage.inMilliseconds, sentMessage.timeZone));
        }

        final View replyView = view.findViewById(R.id.fragment_message_reply_image_view);
        if (replyView != null) {
            replyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null && getActivity() instanceof  MessageCenterInboxDetailsActivity) {
                        ((MessageCenterInboxDetailsActivity) getActivity()).onReplyClicked(sentMessage);
                    }
                }
            });
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
}
