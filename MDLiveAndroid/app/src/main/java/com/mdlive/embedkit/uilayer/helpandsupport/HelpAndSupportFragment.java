package com.mdlive.embedkit.uilayer.helpandsupport;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
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
public class HelpAndSupportFragment extends Fragment {

    private View view;
    private ProgressBar progressBar;

    private ListView mListView;
    private ProgressDialog pDialog = null;
    ListAdapter adapter;

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.mdlive_help_and_support_fragment, container, false);

        findWidgetId();
        getHelpAndSupportServiceData();

        return view;
    }

    private void findWidgetId() {

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mListView = (ListView)view.findViewById(R.id.helpandsupport_listview);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getHelpAndSupportServiceData() {

        setProgressBarVisibility();

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
                setInfoVisibilty();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        HelpAndSupportServices helpAndSupportServices = new HelpAndSupportServices(getActivity(), pDialog);
        helpAndSupportServices.getHelpAndSupportServices(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONArray response) {
        try {
            String question = null;
            String answer = null;
            setInfoVisibilty();

            Log.d("HelpandSupport Response", response.toString());
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            for(int faqIndex = 0; faqIndex < response.length(); faqIndex++){
                question = response.getJSONObject(faqIndex).getString("question");
                answer = response.getJSONObject(faqIndex).getString("answer");
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("question", question);
                hm.put("answer", answer);
                aList.add(hm);

            }

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
            adapter = new HelpAndSupportAdapter(getActivity(), aList);

            mListView.setAdapter(adapter);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /*
     * set visible for the progress bar
     */
    public void setProgressBarVisibility()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    /*
     * set visible for the details view layout
     */
    public void setInfoVisibilty()
    {
        progressBar.setVisibility(View.GONE);
    }
}
