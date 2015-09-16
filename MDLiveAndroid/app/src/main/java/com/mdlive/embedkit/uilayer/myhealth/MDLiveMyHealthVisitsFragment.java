package com.mdlive.embedkit.uilayer.myhealth;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.myhealth.adapter.ConsultationHistoryAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ConsultationHistoryDetails;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.ConsultationHistoryServices;

import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link MDLiveMyHealthProvidersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MDLiveMyHealthVisitsFragment extends MDLiveBaseFragment {
    private ListView mListView;

    private View mHeaderView;
    private ConsultationHistoryAdapter mProviderAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MDLiveMyHealthProvidersFragment.
     */
    public static MDLiveMyHealthVisitsFragment newInstance() {
        MDLiveMyHealthVisitsFragment fragment = new MDLiveMyHealthVisitsFragment();
        return fragment;
    }

    public MDLiveMyHealthVisitsFragment() {
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

        mHeaderView = LayoutInflater.from(view.getContext()).inflate(R.layout.mdlive_my_health_visits_header, null);
        if (mListView != null && mListView.getHeaderViewsCount() > 0 && mHeaderView != null) {
            mListView.removeHeaderView(mHeaderView);
        }

        mListView = (ListView) view.findViewById(R.id.chooseProviderList);
        if (mListView != null) {
            mListView.addHeaderView(mHeaderView);

            mProviderAdapter = new ConsultationHistoryAdapter(view.getContext(), R.layout.consultation_history_adapter_layout, android.R.id.text1);
            mListView.setAdapter(mProviderAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (getActivity() != null && getActivity() instanceof MedicalHistoryActivity) {
//                        ((MedicalHistoryActivity) getActivity()).onMyProviderClicked(mProviderAdapter.getItem(i));
//                        ((ConsultationHistoryAdapter) adapterView.getAdapter()).setSelectedPosition(i);
                        if(((ConsultationHistoryAdapter) adapterView.getAdapter()).getSelectedView()!=null){
                            ((ConsultationHistoryAdapter) adapterView.getAdapter()).getSelectedView().findViewById(R.id.history_details_ll).setVisibility(View.GONE);
                        }
                        view.findViewById(R.id.history_details_ll).setVisibility(View.VISIBLE);
                        ((ConsultationHistoryAdapter) adapterView.getAdapter()).setSelectedView(view);
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

        getMyVisitData();
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

    private void getMyVisitData() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                Log.e("Response - ", response.toString());
                final Gson gson = new Gson();
                final ConsultationHistoryDetails consultationHistoryDetails =  gson.fromJson(response.toString(), ConsultationHistoryDetails.class);
                if(consultationHistoryDetails.getConsultationHistory().size()>0){
                    try {
                        getView().findViewById(R.id.health_no_provider_container).setVisibility(View.GONE);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if (mProviderAdapter != null) {
                    mProviderAdapter.addAll(consultationHistoryDetails.getConsultationHistory());
                    Log.e("Response - ", consultationHistoryDetails.getConsultationHistory().size() + " -- ");
                    mProviderAdapter.notifyDataSetChanged();
                }
                Log.e("Response - ", consultationHistoryDetails.getConsultationHistory().toString());
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
        final ConsultationHistoryServices messageCenter = new ConsultationHistoryServices(getActivity(), getProgressDialog());
        messageCenter.getConsultationHistory(successListener, errorListener);
    }

}
