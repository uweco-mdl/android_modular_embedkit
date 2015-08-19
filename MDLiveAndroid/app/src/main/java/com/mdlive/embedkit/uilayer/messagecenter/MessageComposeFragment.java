package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SendMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Message;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageComposeFragment extends MDLiveBaseFragment {
    private static final String TAG = "data";

    private EditText mSubjectEditText;
    private EditText mBodyEditText;

    public static MessageComposeFragment newInstance(final Parcelable parcelable) {
        final Bundle args = new Bundle();
        args.putParcelable(TAG, parcelable);

        final MessageComposeFragment messageComposeFragment = new MessageComposeFragment();
        messageComposeFragment.setArguments(args);

        return messageComposeFragment;
    }

    public MessageComposeFragment() {
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
        return inflater.inflate(R.layout.fragment_message_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String url = null;
        String to = null;

        final Parcelable parcelable = getArguments().getParcelable(TAG);
        if (parcelable instanceof ReceivedMessage) {
            url = ((ReceivedMessage) parcelable).providerImageUrl;
            to = ((ReceivedMessage) parcelable).from;
        } else if (parcelable instanceof SentMessage) {
            url = ((SentMessage) parcelable).providerImageUrl;
            to = ((SentMessage) parcelable).from;
        } else {
            url = ((MyProvider) parcelable).providerImageUrl;
            to = ((MyProvider) parcelable).name;
        }

        final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.image_view);
        if (circularNetworkImageView != null) {
            circularNetworkImageView.setImageUrl(url, ApplicationController.getInstance().getImageLoader(view.getContext()));
        }

        final TextView toTextView = (TextView) view.findViewById(R.id.fragment_message_compose_to_text_view);
        if (toTextView != null) {
            toTextView.setText(to);
        }

        mSubjectEditText = (EditText) view.findViewById(R.id.fragment_message_compose_subject_edit_text);
        mBodyEditText = (EditText) view.findViewById(R.id.fragment_message_compose_body_edit_text);
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

    public void sendComposedMessage() {
        MdliveUtils.hideKeyboard(getActivity(), (View)mSubjectEditText);

        if (mSubjectEditText != null && mSubjectEditText.getText().toString().trim().length() < 1) {
            MdliveUtils.showDialog(getActivity(), getString(R.string.app_name), getString(R.string.please_enter_mandetory_fileds));
            return;
        }

        if (mBodyEditText != null && mBodyEditText.getText().toString().trim().length() < 0) {
            MdliveUtils.showDialog(getActivity(), getString(R.string.app_name), getString(R.string.please_enter_mandetory_fileds));
            return;
        }

        showProgressDialog();

        int destinationUserId = -1;

        final Parcelable parcelable = getArguments().getParcelable(TAG);
        if (parcelable instanceof ReceivedMessage) {
            destinationUserId = ((ReceivedMessage) parcelable).providerId;
        } else if (parcelable instanceof SentMessage) {
            destinationUserId =((SentMessage) parcelable).providerId;
        } else {
            destinationUserId = ((MyProvider) parcelable).providerId;
        }

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

                final Gson gson = new Gson();
                final Message message =  gson.fromJson(response.toString(), Message.class);

                MdliveUtils.showDialog(getActivity(), message.message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
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

        final SendMessage sendMessage = new SendMessage();
        sendMessage.destinationUserId = String.valueOf(destinationUserId);
        sendMessage.subject = mSubjectEditText == null ? "" : mSubjectEditText.getText().toString().trim();
        sendMessage.message = mBodyEditText == null ? "" : mBodyEditText.getText().toString().trim();

        final Gson gson = new Gson();
        final String params = gson.toJson(sendMessage);

        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.postMessage(successListener, errorListener, params);
    }
}
