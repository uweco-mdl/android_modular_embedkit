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
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageReceivedDetailsFragment extends MDLiveBaseFragment {
    private static final String RECEIVED_MESSAGE_TAG = "received_message";

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView textView = (TextView) view.findViewById(R.id.fragment_message_received_details_text_view);
        if (textView != null) {
            textView.setText(getArguments().getParcelable(RECEIVED_MESSAGE_TAG).toString());
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
