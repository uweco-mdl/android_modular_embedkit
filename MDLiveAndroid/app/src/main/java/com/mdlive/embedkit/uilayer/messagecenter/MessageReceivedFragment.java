package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.MessageReceivedAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.listeners.InfiniteScrollListener;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessages;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageReceivedFragment extends Fragment {
    private static final int NUMBER_OF_ITEMS_PER_PAGE = 10;

    private ProgressDialog pDialog;
    private ListView mListView;
    private MessageReceivedAdapter mMessageReceivedAdapter;

    private int mPageCount = 1;

    public static MessageReceivedFragment newInstance() {
        final MessageReceivedFragment messageInboxFragment = new MessageReceivedFragment();
        return messageInboxFragment;
    }

    public MessageReceivedFragment() {
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
        return inflater.inflate(R.layout.fragment_message_received, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.fragment_message_received_list_view);
        if (mListView != null) {
            mMessageReceivedAdapter = new MessageReceivedAdapter(view.getContext(), R.layout.adapter_message_received, android.R.id.text1);
            mListView.setAdapter(mMessageReceivedAdapter);
            mListView.setOnScrollListener(new InfiniteScrollListener(0) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    fetchReceivedMessages(mPageCount, NUMBER_OF_ITEMS_PER_PAGE);
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    mMessageReceivedAdapter.getItem(i).readStatus = true;
                    mMessageReceivedAdapter.getView(i, view, adapterView).invalidate();

                    if (getActivity() != null && getActivity() instanceof MessageCenterActivity) {
                        ((MessageCenterActivity) getActivity()).onReceivedMessageClicked(mMessageReceivedAdapter.getItem(i));
                    }
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

        fetchReceivedMessages(mPageCount, NUMBER_OF_ITEMS_PER_PAGE);
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

    private void fetchReceivedMessages(final int from, final int numberOfItems) {
        pDialog.show();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();

                final Gson gson = new Gson();
                final ReceivedMessages newReceivedMessages =  gson.fromJson(response.toString(), ReceivedMessages.class);
                if (mMessageReceivedAdapter != null) {
                    mMessageReceivedAdapter.addAll(newReceivedMessages.receivedMessages);
                    mMessageReceivedAdapter.notifyDataSetChanged();
                    mPageCount += 1;
                }

                Toast.makeText(getActivity(), newReceivedMessages.toString(), Toast.LENGTH_SHORT).show();
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
        final MessageCenter messageCenter = new MessageCenter(getActivity(), pDialog);
        messageCenter.getReceivedMessages(successListener, errorListener, from, numberOfItems);
    }
}
