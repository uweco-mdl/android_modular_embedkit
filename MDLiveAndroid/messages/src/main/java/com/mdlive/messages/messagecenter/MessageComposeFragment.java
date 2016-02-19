package com.mdlive.messages.messagecenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ConsultationHistory;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Message;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageComposeFragment extends MDLiveBaseFragment implements TextWatcher {
    private static final String TAG = "data";

    private EditText mSubjectEditText;
    private EditText mBodyEditText;
    private MessageCenterComposeActivity parentActivity;
    private OnBothTextEntered mOnBothTextEntered;

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
        if(activity instanceof MessageCenterComposeActivity){
            parentActivity = (MessageCenterComposeActivity) activity;
        }
        mOnBothTextEntered = (OnBothTextEntered) activity;
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
        mSubjectEditText = (EditText) view.findViewById(R.id.fragment_message_compose_subject_edit_text);
        mBodyEditText = (EditText) view.findViewById(R.id.fragment_message_compose_body_edit_text);

        final Parcelable parcelable = getArguments().getParcelable(TAG);
        if (parcelable instanceof ReceivedMessage) {
            url = ((ReceivedMessage) parcelable).providerImageUrl;
            to = ((ReceivedMessage) parcelable).from;
            if(parentActivity != null){
                parentActivity.isFromNewMessageCompose(false);
            }
            mSubjectEditText.setText("Re: "+ ((mSubjectEditText.getText() == null ||
                    mSubjectEditText.getText().toString().length() == 0) ? "" : mSubjectEditText.getText().toString()));
        } else if (parcelable instanceof SentMessage) {
            url = ((SentMessage) parcelable).providerImageUrl;
            to = ((SentMessage) parcelable).from;
        } else if (parcelable instanceof MyProvider) {
            url = ((MyProvider) parcelable).providerImageUrl;
            to = ((MyProvider) parcelable).name;
            if(parentActivity != null){
                parentActivity.isFromNewMessageCompose(true);
            }
        } else {
            url = ((ConsultationHistory) parcelable).getProviderImageUrl();
            to = ((ConsultationHistory) parcelable).getProviderName();
        }

        final CircularNetworkImageView circularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.image_view);
        if (circularNetworkImageView != null) {
            circularNetworkImageView.setImageUrl(url, ApplicationController.getInstance().getImageLoader(view.getContext()));
        }

        final TextView toTextView = (TextView) view.findViewById(R.id.fragment_message_compose_to_text_view);
        if (toTextView != null) {
            toTextView.setText(to);
        }


        mSubjectEditText.addTextChangedListener(this);
        mBodyEditText.addTextChangedListener(this);
        mBodyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBodyEditText.requestFocusFromTouch();
                mBodyEditText.setFocusableInTouchMode(true);
                mBodyEditText.requestFocus();
            }
        });
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

        mOnBothTextEntered = null;
    }

    public void sendComposedMessage() {
        try {
            MdliveUtils.hideKeyboard(getActivity(), (View)mSubjectEditText);

            if (mSubjectEditText != null && mSubjectEditText.getText().toString().trim().length() < 1) {
                MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_app_name), getString(R.string.mdl_please_enter_mandetory_fileds));
                return;
            }

            if (mBodyEditText != null && mBodyEditText.getText().toString().trim().length() < 0) {
                MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_app_name), getString(R.string.mdl_please_enter_mandetory_fileds));
                return;
            }

            showProgressDialog();

            final JSONObject jsonObject = new JSONObject();
            final JSONObject jsonObjectMessage = new JSONObject();
            final Parcelable parcelable = getArguments().getParcelable(TAG);
            if (parcelable instanceof ReceivedMessage) {
                jsonObjectMessage.put("destination_user_id", ((ReceivedMessage) parcelable).providerId);
                jsonObjectMessage.put("message", mBodyEditText.getText().toString().trim());
                jsonObjectMessage.put("subject", mSubjectEditText.getText().toString().trim());
                jsonObjectMessage.put("replied_to_message_id", ((ReceivedMessage) parcelable).messageId);
            } else if (parcelable instanceof SentMessage) {
                jsonObjectMessage.put("destination_user_id", ((SentMessage) parcelable).providerId);
                jsonObjectMessage.put("message", mBodyEditText.getText().toString().trim());
                jsonObjectMessage.put("subject", mSubjectEditText.getText().toString().trim());
                jsonObjectMessage.put("replied_to_message_id", ((SentMessage) parcelable).messageId);
            } else if (parcelable instanceof MyProvider) {
                jsonObjectMessage.put("destination_user_id", ((MyProvider) parcelable).providerId);
                jsonObjectMessage.put("message", mBodyEditText.getText().toString().trim());
                jsonObjectMessage.put("subject", mSubjectEditText.getText().toString().trim());
                jsonObjectMessage.put("replied_to_message_id", null);
            } else if (parcelable instanceof ConsultationHistory) {
                jsonObjectMessage.put("destination_user_id", ((ConsultationHistory) parcelable).getProviderId());
                jsonObjectMessage.put("message", mBodyEditText.getText().toString().trim());
                jsonObjectMessage.put("subject", mSubjectEditText.getText().toString().trim());
                jsonObjectMessage.put("replied_to_message_id", null);
            } else {
                return;
            }

            jsonObject.put("message", jsonObjectMessage);

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


            final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
            messageCenter.postMessage(successListener, errorListener, jsonObject.toString());
        } catch (JSONException e) {
            logE("JSONException", e.getMessage());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ((mSubjectEditText != null && mSubjectEditText.getText().toString().trim().length() > 0)
                &&
                mBodyEditText != null && mBodyEditText.getText().toString().trim().length() > 0) {
            mOnBothTextEntered.onBothTextEntered(true);
        } else {
            mOnBothTextEntered.onBothTextEntered(false);
        }
    }

    public interface OnBothTextEntered {
        void onBothTextEntered(final boolean value);
    }
}
