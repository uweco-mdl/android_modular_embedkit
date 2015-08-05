package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SendMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Message;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageComposeFragment extends Fragment {
    private static final String MY_PROVIDER_TAG = "MY_PROVIDER";

    private ProgressDialog pDialog;

    private EditText mSubjectEditText;
    private EditText mBodyEditText;

    public static MessageComposeFragment newInstance(final MyProvider myProvider) {
        final Bundle args = new Bundle();
        args.putParcelable(MY_PROVIDER_TAG, myProvider);

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

        final TextView toTextView = (TextView) view.findViewById(R.id.fragment_message_compose_to_text_view);
        if (toTextView != null) {
            toTextView.setText(((MyProvider) getArguments().getParcelable(MY_PROVIDER_TAG)).name);
        }

        mSubjectEditText = (EditText) view.findViewById(R.id.fragment_message_compose_subject_edit_text);
        mBodyEditText = (EditText) view.findViewById(R.id.fragment_message_compose_body_edit_text);
        if (mBodyEditText != null) {
            mBodyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEND) {
                        sendComposedMessage();

                        return true;
                    }

                    return false;
                }
            });
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

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());
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

    private void sendComposedMessage() {
        pDialog.show();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();

                final Gson gson = new Gson();
                final Message message =  gson.fromJson(response.toString(), Message.class);
                Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };

        final SendMessage sendMessage = new SendMessage();
        sendMessage.destinationUserId = ((MyProvider) getArguments().getParcelable(MY_PROVIDER_TAG)).providerId;
        sendMessage.repliedToMessageId = null;
        sendMessage.subject = mSubjectEditText == null ? "" : mSubjectEditText.getText().toString().trim();
        sendMessage.message = mBodyEditText == null ? "" : mBodyEditText.getText().toString().trim();

        final Gson gson = new Gson();
        final String params = gson.toJson(sendMessage);

        final MessageCenter messageCenter = new MessageCenter(getActivity(), pDialog);
        messageCenter.postMessage(successListener, errorListener, params);
    }
}
