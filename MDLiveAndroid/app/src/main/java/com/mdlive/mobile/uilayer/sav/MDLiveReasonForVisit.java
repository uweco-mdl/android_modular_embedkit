package com.mdlive.mobile.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.MDLiveDashboard;
import com.mdlive.mobile.uilayer.adapters.ReasonForVisitAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ReasonForVisitServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sudha_s on 5/18/2015.
 */
public class MDLiveReasonForVisit extends Activity {
    private ListView listView;
    private ProgressDialog pDialog;
    private ArrayList<String> ReasonList;
    private TextView NoResults,SubmitResults;
    private LinearLayout NoresultsLinearlay;
    ReasonForVisitAdapter baseadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reason);
        NoResults = (TextView) findViewById(R.id.noresults);
        SubmitResults = (TextView) findViewById(R.id.submitresult);
        NoresultsLinearlay = (LinearLayout) findViewById(R.id.linearlayoutresults);
        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        ReasonList = new ArrayList<String>();
        pDialog = Utils.getProgressDialog(LocalisationHelper.getLocalizedStringFromPrefs(this,getResources().getString(R.string.please_wait)), this);
        ReasonForVisit();
        SubmitResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Reasonintent = new Intent(MDLiveReasonForVisit.this,MDLiveDashboard.class);
                startActivity(Reasonintent);
            }
        });
    }



    /**
     *
     * Choose Provider List Details.
     * Class : ChooseProviderServices - Service class used to fetch the Provider information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void ReasonForVisit() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessListener(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveReasonForVisit.this);
                    }
                }
            }};
        ReasonForVisitServices services = new ReasonForVisitServices(MDLiveReasonForVisit.this, null);
        services.getReasonList(successCallBackListener, errorListener);
    }

    /**
     *
     * Successful Response Handler for Provider Request
     *
     */
    private void handleSuccessListener(JSONObject response) {
        try {
            pDialog.dismiss();
           //Fetch Data From the Services
            JSONArray arr = response.getJSONArray("chief_complaint");

            for(int i = 0;i< arr.length();i++){
                ReasonList.add(arr.getJSONObject(i).getString(arr.getJSONObject(i).keys().next()));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

            listView = (ListView) findViewById(R.id.reasonList);
            baseadapter = new ReasonForVisitAdapter(getApplicationContext(), ReasonList,(LinearLayout)findViewById(R.id.linearlayoutresults),(TextView)findViewById(R.id.noresults),(TextView)findViewById(R.id.submitresult));
            listView.setAdapter(baseadapter);
            RefineSearch();
            ListItemClickListener();



        }
    public void RefineSearch()
    {
         EditText search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                baseadapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void ListItemClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent Reasonintent = new Intent(MDLiveReasonForVisit.this,MDLiveDashboard.class);
                startActivity(Reasonintent);
                finish();
            }
        });
    }
    }

