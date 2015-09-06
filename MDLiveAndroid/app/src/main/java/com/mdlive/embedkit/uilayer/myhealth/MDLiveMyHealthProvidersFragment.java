package com.mdlive.embedkit.uilayer.myhealth;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.ProviderAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PrimaryCarePhysician;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Provider;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MDLiveMyHealthProvidersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MDLiveMyHealthProvidersFragment extends MDLiveBaseFragment {
    private ListView mListView;
    private ProviderAdapter mProviderAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MDLiveMyHealthProvidersFragment.
     */
    public static MDLiveMyHealthProvidersFragment newInstance() {
        MDLiveMyHealthProvidersFragment fragment = new MDLiveMyHealthProvidersFragment();
        return fragment;
    }

    public MDLiveMyHealthProvidersFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mdlive_my_health_providers, null, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.chooseProviderList);
        if (mListView != null) {
            mProviderAdapter = new ProviderAdapter(view.getContext(), R.layout.new_adapter_layout, android.R.id.text1);
            View header = getActivity().getLayoutInflater().inflate(R.layout.mdlive_my_health_provider_header, null);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent PrimaryCarePhysician =new Intent(getActivity(),PrimaryCarePhysicianActivity.class);
                    startActivity(PrimaryCarePhysician);
                }
            });
            mListView.addHeaderView(header);
            mListView.setAdapter(mProviderAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    i -= mListView.getHeaderViewsCount();
                    final MyProvider provider = mProviderAdapter.getItem(i);
                    Intent intent = new Intent(getActivity(),ProviderDetailsActivity.class);
                    intent.putExtra("ProviderID", String.valueOf(provider.providerId));
                    getActivity().startActivity(intent);
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

        fetchMessageprovider();
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

    private void fetchMessageprovider() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

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

                if (mProviderAdapter != null) {
                    mProviderAdapter.addAll(provider.myProviders);
                    Log.e("Response - ", provider.myProviders.size() + " -- ");
                    if(provider.myProviders.size()>0){
                        try {
                            getView().findViewById(R.id.health_no_provider_container).setVisibility(View.GONE);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    mProviderAdapter.notifyDataSetChanged();
                }
                Log.e("Response - ", provider.toString());
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
        messageCenter.getProvider(successListener, errorListener);
    }

}
