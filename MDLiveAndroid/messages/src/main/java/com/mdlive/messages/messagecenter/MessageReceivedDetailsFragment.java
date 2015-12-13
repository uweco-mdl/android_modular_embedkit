package com.mdlive.messages.messagecenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageReceivedDetailsFragment extends MDLiveBaseFragment {
    private static final String RECEIVED_MESSAGE_TAG = "received_message";

    private ReloadMessageCount mReloadMessageCount;

    public static MessageReceivedDetailsFragment newInstance(final ReceivedMessage receivedMessage) {
        final Bundle args = new Bundle();
        args.putParcelable(RECEIVED_MESSAGE_TAG, receivedMessage);

        final MessageReceivedDetailsFragment messageReceivedDetailsFragment = new MessageReceivedDetailsFragment();
        messageReceivedDetailsFragment.setArguments(args);

        return messageReceivedDetailsFragment;
    }

    public MessageReceivedDetailsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mReloadMessageCount = (ReloadMessageCount) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_received_details, container, false);
    }
    private void renderUI(View view, final ReceivedMessage receivedMessage){

        final TextView subjectTextView = (TextView) view.findViewById(R.id.fragment_message_received_subject_text_view);
        if (subjectTextView != null) {
            subjectTextView.setText(receivedMessage.subject);
        }

        final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.adapter_provider_image_view);
        if (circularNetworkImageView != null) {
            circularNetworkImageView.setImageUrl(receivedMessage.providerImageUrl, ApplicationController.getInstance().getImageLoader(view.getContext()));
        }

        final TextView doctorNameTextView = (TextView) view.findViewById(R.id.fragment_message_received_doctor_name_text_view);
        if (doctorNameTextView != null) {
            doctorNameTextView.setText(receivedMessage.from);
        }

        final TextView timeTextView = (TextView) view.findViewById(R.id.fragment_message_received_date_text_view);
        if (timeTextView != null) {
            timeTextView.setText(TimeZoneUtils.ReceivedSentTimeInDetails(receivedMessage.inMilliseconds, receivedMessage.timeZone, getActivity()));
        }

        final View replyView = view.findViewById(R.id.fragment_message_reply_image_view);
        if (replyView != null) {
            replyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null && getActivity() instanceof  MessageCenterInboxDetailsActivity) {
                        ((MessageCenterInboxDetailsActivity) getActivity()).onReplyClicked(receivedMessage);
                    }
                }
            });
        }

        final TextView detailsTextView = (TextView) view.findViewById(R.id.fragment_message_received_details_text_view);
        if (detailsTextView != null) {
            detailsTextView.setText(receivedMessage.message);
        }
        if(!receivedMessage.readStatus){
            callReceivedMessageRead(view, receivedMessage.messageId+"", true);
        }
    }
    @Override
    public void onViewCreated(View uiView, Bundle savedInstanceState) {
        super.onViewCreated(uiView, savedInstanceState);
        final ReceivedMessage receivedMessage = getArguments().getParcelable(RECEIVED_MESSAGE_TAG);
        Log.e("receivedMessage", receivedMessage.toString()+"");
        if(receivedMessage.message != null && !receivedMessage.message.equalsIgnoreCase("null")) {
            renderUI(uiView, receivedMessage);
        }else{
            callReceivedMessageRead(uiView, receivedMessage.messageId + "", false);
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

//        final ReceivedMessage receivedMessage = getArguments().getParcelable(RECEIVED_MESSAGE_TAG);
//        if (receivedMessage != null) {
//            callReceivedMessageRead(null, String.valueOf(receivedMessage.messageId));
//        }
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

        mReloadMessageCount = null;
    }

    private void callReceivedMessageRead(final View uiView, final String id, final boolean isMessageRendered) {
        showProgressDialog();
        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    Log.e("message response", response.toString());
                    if (!response.has("error")) {
                        JSONObject message = response.getJSONObject("message");
                        ReceivedMessage receivedMessage = new Gson().fromJson(message.toString(), ReceivedMessage.class);
                        if(!isMessageRendered) {
                            renderUI(uiView, receivedMessage);
                        }
                    }
                    if (mReloadMessageCount != null) {
                        mReloadMessageCount.reloadMessageCount();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
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
                                    ((MDLiveBaseAppcompatActivity)getActivity()).onMessageClicked();
                                    getActivity().finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                    }
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.getMessaseReceivedRead(id, successListener, errorListener, null);
    }

    public interface ReloadMessageCount {
        void reloadMessageCount();
    }

    private static String getReceivedSentTimeInDetails(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));

        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        final Format format = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        return format.format(date);
    }
}
