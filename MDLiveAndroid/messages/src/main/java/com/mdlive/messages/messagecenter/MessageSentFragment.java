package com.mdlive.messages.messagecenter;

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
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.messagecenter.adapter.MessageSentAdapter;
import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.listeners.InfiniteScrollListener;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessages;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageSentFragment extends MDLiveBaseFragment {
    private static final int NUMBER_OF_ITEMS_PER_PAGE = 10;

    private ListView mListView;
    private MessageSentAdapter mMessageSentAdapter;

    private View mBlankLayout;

    private int mPageCount = 1;

    public static MessageSentFragment newInstance() {
        final MessageSentFragment messageSentFragment = new MessageSentFragment();
        return messageSentFragment;
    }

    public MessageSentFragment() {
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
        return inflater.inflate(R.layout.fragment_message_sent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.fragment_message_sent_list_view);
        if (mListView != null) {
            mMessageSentAdapter = new MessageSentAdapter(view.getContext(), R.layout.adapter_message_sent, android.R.id.text1);
            mListView.setAdapter(mMessageSentAdapter);
            mListView.setOnScrollListener(new InfiniteScrollListener(0) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    fetchSentMessages(mPageCount, NUMBER_OF_ITEMS_PER_PAGE);
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    mMessageSentAdapter.getItem(i).readStatus = true;
                    mMessageSentAdapter.getView(i, view, adapterView).invalidate();


                    if (getActivity() != null && getActivity() instanceof MessageCenterActivity) {
                        ((MessageCenterActivity) getActivity()).onSentMessageClicked(mMessageSentAdapter.getItem(i));
                    }
                }
            });
        }

        mBlankLayout = view.findViewById(R.id.blank_layout);

        final ImageView image = (ImageView) view.findViewById(R.id.message_center_empty_image_view);
        if (image != null) {
            image.setImageResource(R.drawable.empty_inbox_sent);
        }

        final TextView header = (TextView) view.findViewById(R.id.message_center_empty_header_text_view);
        if (header != null) {
            header.setText(R.string.mdl_no_messages_sent_header);
        }

        final TextView details = (TextView) view.findViewById(R.id.message_center_empty_details_text_view);
        if (details != null) {
            details.setText(R.string.mdl_no_messages_sent_details);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSentMessages(mPageCount, NUMBER_OF_ITEMS_PER_PAGE);
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
        mMessageSentAdapter.clear();
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

    private void fetchSentMessages(final int from, final int numberOfItems) {
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
                    MdliveUtils.handleVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };
        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.getSentMessages(successListener, errorListener, from, numberOfItems);
    }

    private void handleSucess(final JSONObject response) {
        logD("Hello", "Page Number : " + mPageCount);
        logD("Hello", response.toString());

        final Gson gson = new Gson();
        final SentMessages newSentMessages =  gson.fromJson(response.toString(), SentMessages.class);
        if (newSentMessages.sentMessages != null && newSentMessages.sentMessages.size() > 0) {
            if (mMessageSentAdapter != null) {
                mMessageSentAdapter.addAll(newSentMessages.sentMessages);
                mMessageSentAdapter.notifyDataSetChanged();
                if (newSentMessages.sentMessages.size() < NUMBER_OF_ITEMS_PER_PAGE) {
                    /* Do Not increase the Page number, Remove the Scroll listener */
                    mListView.setOnScrollListener(null);
                } else {
                    mPageCount += 1;
                }
            }

            mListView.setVisibility(View.VISIBLE);
            mBlankLayout.setVisibility(View.GONE);
        } else {
            if (mMessageSentAdapter.getCount() >= 0) {
                // Do nothing for this case
            } else {
                mListView.setVisibility(View.GONE);
                mBlankLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleError() {
        if (mMessageSentAdapter != null && mMessageSentAdapter.getCount() > 0) {
            // Do nothing in this case
        } else {
            mListView.setVisibility(View.GONE);
            mBlankLayout.setVisibility(View.VISIBLE);
        }
    }
}
