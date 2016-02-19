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
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.messagecenter.adapter.ProviderAdapter;
import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PrimaryCarePhysician;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Provider;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageProviderFragment extends MDLiveBaseFragment {
    private ListView mListView;
    private ProviderAdapter mProviderAdapter;

    private View mListLayout;
    private View mBlankLayout;

    View header;

    public static MessageProviderFragment newInstance() {
        final MessageProviderFragment messageProviderFragment = new MessageProviderFragment();
        return messageProviderFragment;
    }

    public MessageProviderFragment() {
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
        return inflater.inflate(R.layout.fragment_message_provider, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.fragment_message_provider_list_view);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        header = inflater.inflate(R.layout.fragment_compose_list_header, null);
        mListView.addHeaderView(header);

        if (mListView != null) {
            mProviderAdapter = new ProviderAdapter(view.getContext(), R.layout.adapter_provider, android.R.id.text1);
            mListView.setAdapter(mProviderAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (getActivity() != null && getActivity() instanceof MessageCenterActivity) {
                        ((MessageCenterActivity) getActivity()).onMyProviderClicked(mProviderAdapter.getItem(i - 1));
                    }
                }
            });
        }

        mListLayout = view.findViewById(R.id.list_layout);
        mBlankLayout = view.findViewById(R.id.blank_layout);
        mBlankLayout.setVisibility(View.GONE);
        final ImageView image = (ImageView) view.findViewById(R.id.message_center_empty_image_view);
        if (image != null) {
            image.setImageResource(R.drawable.empty_provider);
        }

        final TextView header = (TextView) view.findViewById(R.id.message_center_empty_header_text_view);
        if (header != null) {
            header.setVisibility(View.GONE);
        }

        final TextView details = (TextView) view.findViewById(R.id.message_center_empty_details_text_view);
        if (details != null) {
            details.setText(R.string.mdl_no_messages_compose_details);
        }

        mListLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMessageprovider();
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
        mProviderAdapter.clear();
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

    private void fetchMessageprovider() {
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
        messageCenter.getProvider(successListener, errorListener);
    }

    private void handleSucess(final JSONObject response) {
        logD("Provider Response", response.toString().trim());

        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final Provider provider =  gson.fromJson(response.toString(), Provider.class);
        /*
        * This is a work around, as for new user rather sending on blank Json object
        * It sends a empty JSON Array
        * */
        try {
            JSONObject primaryPhysicianJSONObject = response.getJSONObject("primary_care_physician");
            final PrimaryCarePhysician primaryCarePhysician = new PrimaryCarePhysician();
            primaryCarePhysician.zip = primaryPhysicianJSONObject.getString("zip");
            primaryCarePhysician.phone = primaryPhysicianJSONObject.getString("phone");
            primaryCarePhysician.fax = primaryPhysicianJSONObject.getString("fax");
            primaryCarePhysician.middleName = primaryPhysicianJSONObject.getString("middle_name");
            primaryCarePhysician.cell = primaryPhysicianJSONObject.getString("cell");
            primaryCarePhysician.state = primaryPhysicianJSONObject.getString("state");
            primaryCarePhysician.address1 = primaryPhysicianJSONObject.getString("address1");
            primaryCarePhysician.address2 = primaryPhysicianJSONObject.getString("address2");
            primaryCarePhysician.suffix = primaryPhysicianJSONObject.getString("suffix");
            primaryCarePhysician.city = primaryPhysicianJSONObject.getString("city");
            primaryCarePhysician.stateprov = primaryPhysicianJSONObject.getString("stateprov");
            primaryCarePhysician.country = primaryPhysicianJSONObject.getString("country");
            primaryCarePhysician.firstName = primaryPhysicianJSONObject.getString("first_name");
            primaryCarePhysician.email = primaryPhysicianJSONObject.getString("email");
            primaryCarePhysician.practice = primaryPhysicianJSONObject.getString("practice");
            primaryCarePhysician.prefix = primaryPhysicianJSONObject.getString("prefix");
            primaryCarePhysician.lastName = primaryPhysicianJSONObject.getString("last_name");
        } catch (JSONException e) {
            provider.primaryCarePhysician = null;
        }
        if (provider.myProviders != null && provider.myProviders.size() > 0) {
            if (mProviderAdapter != null) {
                mProviderAdapter.addAll(provider.myProviders);
                mProviderAdapter.notifyDataSetChanged();
            }

            mListLayout.setVisibility(View.VISIBLE);
            mBlankLayout.setVisibility(View.GONE);
        } else {
            mListLayout.setVisibility(View.GONE);
            mBlankLayout.setVisibility(View.VISIBLE);
        }
    }

    private void handleError() {
        if (mProviderAdapter != null && mProviderAdapter.getCount() > 0) {
            // Do nothing in this case
        } else {
            mListLayout.setVisibility(View.GONE);
            mBlankLayout.setVisibility(View.VISIBLE);
        }
    }
}
