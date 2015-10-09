package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
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
public class MessageReceivedFragment extends MDLiveBaseFragment {
    private static final int NUMBER_OF_ITEMS_PER_PAGE = 10;

    private ListView mListView;
    private MessageReceivedAdapter mMessageReceivedAdapter;

    private View mBlankLayout;

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
        mBlankLayout = view.findViewById(R.id.blank_layout);
        mBlankLayout.setVisibility(View.GONE);
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

        final ImageView image = (ImageView) view.findViewById(R.id.message_center_empty_image_view);
        if (image != null) {
            image.setImageResource(R.drawable.empty_inbox_sent);
        }

        final TextView header = (TextView) view.findViewById(R.id.message_center_empty_header_text_view);
        if (header != null) {
            header.setText(R.string.mdl_no_messages_inbox_header);
        }

        final TextView details = (TextView) view.findViewById(R.id.message_center_empty_details_text_view);
        if (details != null) {
            details.setText(R.string.mdl_no_messages_inbox_details);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchReceivedMessages(mPageCount, NUMBER_OF_ITEMS_PER_PAGE);
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

        mPageCount = 1;
        mMessageReceivedAdapter.clear();
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
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

                handleSucess(response);
            }
        };
        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                handleError();

                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };
        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.getReceivedMessages(successListener, errorListener, from, numberOfItems);
    }

    private void handleSucess(final JSONObject response) {
        logD("Hello", "Page Number : " + mPageCount);
        logD("Hello", response.toString());
        final Gson gson = new Gson();
        final ReceivedMessages newReceivedMessages =  gson.fromJson(response.toString(), ReceivedMessages.class);
        if (newReceivedMessages.receivedMessages != null && newReceivedMessages.receivedMessages.size() > 0) {
            if (mMessageReceivedAdapter != null) {
                mMessageReceivedAdapter.addAll(newReceivedMessages.receivedMessages);
                mMessageReceivedAdapter.notifyDataSetChanged();
                if (newReceivedMessages.receivedMessages.size() < NUMBER_OF_ITEMS_PER_PAGE) {
                    /* Do Not increase the Page number, Remove the Scroll listener */
                    mListView.setOnScrollListener(null);
                } else {
                    mPageCount += 1;
                }
            }

            mListView.setVisibility(View.VISIBLE);
            mBlankLayout.setVisibility(View.GONE);
        } else {
            if (mMessageReceivedAdapter.getCount() > 0) {
                // Do nothing for this case
            } else {
                mListView.setVisibility(View.GONE);
                mBlankLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleError() {
        if (mMessageReceivedAdapter != null && mMessageReceivedAdapter.getCount() > 0) {
            // Do nothing in this case
        } else {
            mListView.setVisibility(View.GONE);
            mBlankLayout.setVisibility(View.VISIBLE);
        }
    }
}
