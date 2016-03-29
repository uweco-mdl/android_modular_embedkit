package com.mdlive.embedkit.uilayer.helpandsupport;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.helpandsupport.HelpAndSupportServices;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpAndSupportFragment extends MDLiveBaseFragment {
    private ListView mListView;

    public static HelpAndSupportFragment newInstance() {
        final HelpAndSupportFragment fragment = new HelpAndSupportFragment();
        return fragment;
    }

    public HelpAndSupportFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mdlive_help_and_support_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView)view.findViewById(R.id.helpandsupport_listview);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getHelpAndSupportServiceData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getHelpAndSupportServiceData() {
        showProgressDialog();

        NetworkSuccessListener<JSONArray> responseListener = new NetworkSuccessListener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    handleSuccessResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handleVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }};

        HelpAndSupportServices helpAndSupportServices = new HelpAndSupportServices(getActivity(), getProgressDialog());
        helpAndSupportServices.getHelpAndSupportServices(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONArray response) {
        try {
            hideProgressDialog();

            Log.d("HelpandSupport Response", response.toString());
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            for(int faqIndex = 0; faqIndex < response.length(); faqIndex++){
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("question", response.getJSONObject(faqIndex).getString("question"));
                hm.put("answer", response.getJSONObject(faqIndex).getString("answer"));
                aList.add(hm);

            }

            if (mListView != null) {
                addFooter(mListView);
                final HelpAndSupportAdapter adapter = new HelpAndSupportAdapter(getActivity(), aList);
                mListView.setAdapter(adapter);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addFooter(final ListView listView) {
        if (listView.getFooterViewsCount() == 0) {
            final LayoutInflater inflater = LayoutInflater.from(listView.getContext());
            final View view = inflater.inflate(R.layout.include_help_support, null, false);
            try {
                String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                ((TextView) view.findViewById(R.id.mdlVersionNumber)).setText("v " + versionName + AppSpecificConfig.URL_ENVIRONMENT);
            }catch (Exception e){
                e.printStackTrace();
            }
            listView.addFooterView(view);
        }
    }
}
