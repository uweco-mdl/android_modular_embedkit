package com.mdlive.embedkit.uilayer.sav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveMedicalHistory;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.embedkit.uilayer.sav.adapters.ReasonForVisitAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ReasonForVisitServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sudha_s on 5/18/2015.
 */
public class MDLiveReasonForVisit extends MDLiveBaseActivity {
    private ListView listView;
    private ProgressDialog pDialog;
    //private ProgressBar progressBar;
    private ArrayList<String> ReasonList;
    ReasonForVisitAdapter baseadapter;
    private ImageView deleteTextBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_reason);
        //progressBar = (ProgressBar)findViewById(R.id.progressBar);
        setProgressBar(findViewById(R.id.progressBar));

        deleteTextBtn = (ImageView) findViewById(R.id.deleteTextBtn);

        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        ReasonList = new ArrayList<String>();
        pDialog = MdliveUtils.getProgressDialog("Please wait...", this);
        ReasonForVisit();

        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */
        deleteTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.search_edit)).setText("");

            }
        });


        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveReasonForVisit.this);
                onBackPressed();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });

    }




    /**
     * Reason for Visit List Details.
     * Class : ReasonForVisitServices - Service class used to fetch the List information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void ReasonForVisit() {
//        pDialog.show();
        //progressBar.setVisibility(View.VISIBLE);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessListener(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveReasonForVisit.this, error, pDialog);
//                pDialog.dismiss();
                /*if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, MDLiveReasonForVisit.this);
                    }
                }*/
            }};
        ReasonForVisitServices services = new ReasonForVisitServices(MDLiveReasonForVisit.this, pDialog);
        services.getReasonList(successCallBackListener, errorListener);
    }

    /**
     *
     * Successful Response Handler for Provider Request
     *
     */
    private void handleSuccessListener(JSONObject response) {
        try {
            //progressBar.setVisibility(View.GONE);
            hideProgress();
//            pDialog.dismiss();
           //Fetch Data From the Services
            JSONArray arr = response.getJSONArray("chief_complaint");

                for(int i = 0;i< arr.length();i++){
                ReasonList.add(arr.getJSONObject(i).getString(arr.getJSONObject(i).keys().next()));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        findViewById(R.id.footer).setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.reasonList);
        showOrHideFooter();

        baseadapter = new ReasonForVisitAdapter(getApplicationContext(), ReasonList);
            listView.setAdapter(baseadapter);
            RefineSearch();
            ListItemClickListener();
        }

    /*
* shows or hide list footer/ bottom footer
* */
    public void showOrHideFooter() {
        final View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.mdlive_footer, null, false);

        // If list size is greater than zero then show the bottom footer
        if (ReasonList != null && ReasonList.size() > 0) {
            findViewById(R.id.footer).setVisibility(View.GONE);

            if (listView.getFooterViewsCount() == 0) {

                listView.addFooterView(footerView, null, false);
            }
        }
        // If list size is zero then remove the bootm footer & add the list footer
        else {
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
            if (listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(footerView);
            }
        }
    }

    /**
     *
     * Filter Search for the Listview. we can filter the list by giving the name and if the name
     * is not in the listview then it will ask for submitting the name to the service.
     *
     */
    public void RefineSearch()
    {
        final EditText search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.subSequence(0, 1).toString().equalsIgnoreCase(" ")) {
                    search_edit.setText("");
                    search_edit.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text=s.toString();
                if(text.length() == 0){
                    deleteTextBtn.setVisibility(View.GONE);
                }else{
                    deleteTextBtn.setVisibility(View.VISIBLE);
                }
                if(!text.startsWith(" ")){
                    baseadapter.getFilter().filter(s.toString());
                }
                //if(search_edit.getText().length() == 0){
                    //baseadapter = new ReasonForVisitAdapter(getApplicationContext(), ReasonList);
                    //listView.setAdapter(baseadapter);
                    baseadapter.notifyDataSetChanged();
                    baseadapter.getFilter().filter(s.toString());
                //}
            }
        });
    }

    /**
     *Item Click Listener for the ListView
     */

    public void ListItemClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Age,Month,Days",""+ MdliveUtils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)+"Month"+ MdliveUtils.calculteMonthFromPrefs(MDLiveReasonForVisit.this)+"Days"+ MdliveUtils.daysFromPrefs(MDLiveReasonForVisit.this));
            try{
                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("Reason",""+listView.getAdapter().getItem(position).toString());
                editor.putString(PreferenceConstants.REASON,listView.getAdapter().getItem(position).toString());
                editor.commit();

                //MDLivePharmacy
                if(MdliveUtils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)<=13){
                    Intent Reasonintent = new Intent(MDLiveReasonForVisit.this,MDLivePediatric.class);
                    startActivity(Reasonintent);
                    MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);

                }else{
                    Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveMedicalHistory.class);
                    startActivity(medicalIntent);
                    MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                }
            }catch (Exception e){
            e.printStackTrace();
            }



            }
        });
    }
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {

        MdliveUtils.movetohome(MDLiveReasonForVisit.this, getString(R.string.home_dialog_title), getString(R.string.home_dialog_text));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveReasonForVisit.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
    }


