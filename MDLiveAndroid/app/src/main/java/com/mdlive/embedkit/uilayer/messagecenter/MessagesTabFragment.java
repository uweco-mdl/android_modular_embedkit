package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mdlive.embedkit.R;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessagesTabFragment extends Fragment {
    private static final String MESSAGE_RECEIVED_FRAGMENT_TAG = "message_received_fragment";
    private static final String MESSAGE_SENT_FRAGMENT_TAG = "message_sent_fragment";

    public static MessagesTabFragment newInstance() {
        final MessagesTabFragment messagesTabFragment = new MessagesTabFragment();
        return messagesTabFragment;
    }

    public MessagesTabFragment() {
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
        return inflater.inflate(R.layout.fragment_messages_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button inboxButton = (Button) view.findViewById(R.id.fragment_messages_tab_inbox_button);
        if (inboxButton != null) {
            inboxButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if (getChildFragmentManager().findFragmentByTag(MESSAGE_RECEIVED_FRAGMENT_TAG) != null) {
                        getChildFragmentManager().
                                beginTransaction().
                                replace(R.id.fragment_messages_tab_contatiner, MessageReceivedFragment.newInstance(), MESSAGE_RECEIVED_FRAGMENT_TAG).
                                commit();
                    //}
                }
            });
        }

        final Button sentButton = (Button) view.findViewById(R.id.fragment_messages_tab_sent_button);
        if (sentButton != null) {
            sentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if (getChildFragmentManager().findFragmentByTag(MESSAGE_SENT_FRAGMENT_TAG) != null) {
                        getChildFragmentManager().
                                beginTransaction().
                                replace(R.id.fragment_messages_tab_contatiner, MessageSentFragment.newInstance(), MESSAGE_SENT_FRAGMENT_TAG).
                                commit();
                    //}
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

        getChildFragmentManager().
                beginTransaction().
                add(R.id.fragment_messages_tab_contatiner, MessageReceivedFragment.newInstance(), MESSAGE_RECEIVED_FRAGMENT_TAG).
                commit();
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
