package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMedicalHistory;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.embedkit.uilayer.sav.adapters.ReasonForVisitAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
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
        setContentView(R.layout.mdlive_reason);
        NoResults = (TextView) findViewById(R.id.noresults);
        SubmitResults = (TextView) findViewById(R.id.submitresult);
        NoresultsLinearlay = (LinearLayout) findViewById(R.id.linearlayoutresults);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        ReasonList = new ArrayList<String>();
        pDialog = Utils.getProgressDialog("Please wait...", this);
        ReasonForVisit();
        SubmitResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isValidName(((EditText)findViewById(R.id.search_edit)).getText().toString())){
                    Utils.showDialog(MDLiveReasonForVisit.this,getResources().getString(R.string.app_name),getResources().getString(R.string.invalid_symptom));
                    return;
                }






                Log.e("Age,Month,Days",""+Utils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)+"Month"+Utils.calculteMonthFromPrefs(MDLiveReasonForVisit.this)+"Days"+Utils.daysFromPrefs(MDLiveReasonForVisit.this));
                if(Utils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)<=13){
                    Intent Reasonintent = new Intent(MDLiveReasonForVisit.this,MDLivePediatric.class);
                    startActivity(Reasonintent);
                    finish();
                }else{
                    Intent medicalIntent = new Intent(MDLiveReasonForVisit.this,MDLiveMedicalHistory.class);
                    startActivity(medicalIntent);
                }

            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveReasonForVisit.this);
                finish();
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


        if (listView.getFooterViewsCount() == 0) {
            final View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.mdlive_footer, null, false);
            listView.addFooterView(footerView, null, false);
        }

        baseadapter = new ReasonForVisitAdapter(getApplicationContext(), ReasonList,(LinearLayout)findViewById(R.id.linearlayoutresults),(TextView)findViewById(R.id.noresults),(TextView)findViewById(R.id.submitresult));
            listView.setAdapter(baseadapter);
            RefineSearch();
            ListItemClickListener();

        }
    /**
     *
     * Filter Search for the Listview. we can filter the list by giving the name and if the name
     * is not in the listview then it will ask for submitting the name to the service.
     *
     */
    public void RefineSearch()
    {
         EditText search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                String text=s.toString();
                if(!text.startsWith(" ")){
                    baseadapter.getFilter().filter(s.toString());
                }else{
                    Utils.alert(pDialog,MDLiveReasonForVisit.this,"Please enter the character");//TO DO Need to confirm the alert message

                }
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
                Log.e("Age,Month,Days",""+Utils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)+"Month"+Utils.calculteMonthFromPrefs(MDLiveReasonForVisit.this)+"Days"+Utils.daysFromPrefs(MDLiveReasonForVisit.this));
            try{
                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("Reason",""+listView.getAdapter().getItem(position).toString());
                editor.putString(PreferenceConstants.REASON,listView.getAdapter().getItem(position).toString());
                editor.commit();

                //MDLivePharmacy
                if(Utils.calculteAgeFromPrefs(MDLiveReasonForVisit.this)<=13){
                    Intent Reasonintent = new Intent(MDLiveReasonForVisit.this,MDLivePediatric.class);
                    startActivity(Reasonintent);

                }else{
                    Intent medicalIntent = new Intent(MDLiveReasonForVisit.this,MDLiveMedicalHistory.class);
                    startActivity(medicalIntent);
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
        Utils.movetohome(MDLiveReasonForVisit.this, MDLiveLogin.class);
    }
    }

