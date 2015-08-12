package com.mdlive.embedkit.uilayer.sav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ReasonForVisitServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class has the list of Symptoms.The user can Search for the Symptom
 * else the user is able to add the new symptom.Selection of symptom is must required
 * for Starting the Consultation.
 */
public class MDLiveReasonForVisit extends MDLiveBaseActivity {
    private ListView listView;
    private ArrayList<String> ReasonList;
    ReasonForVisitAdapter baseadapter;
    private ImageView deleteTextBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_reason);
        setProgressBar(findViewById(R.id.progressBar));
        deleteTextBtn = (ImageView) findViewById(R.id.deleteTextBtn);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME, ""));
        ReasonList = new ArrayList<String>();
        ReasonForVisit();

        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the SSo activity
         * The Cross button comes when the user searches for the symptom and if there is no
         * symptom the cross button will be hidden.
         */
        deleteTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) findViewById(R.id.search_edit)).setText("");

            }
        });

        ((ImageView) findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveReasonForVisit.this);
                onBackPressed();
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
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveReasonForVisit.this, error, null);
            }
        };
        ReasonForVisitServices services = new ReasonForVisitServices(MDLiveReasonForVisit.this, null);
        services.getReasonList(successCallBackListener, errorListener);
    }

    /**
     * Successful Response Handler for Provider Request.
     * The response will provide the list of symptoms.If there is no symptoms the user can
     * create the new symptom and add the symptom.
     */
    private void handleSuccessListener(JSONObject response) {
        try {
            hideProgress();
            JSONArray arr = response.getJSONArray("chief_complaint");
            for (int i = 0; i < arr.length(); i++) {
                ReasonList.add(arr.getJSONObject(i).getString(arr.getJSONObject(i).keys().next()));
            }

        } catch (Exception e) {
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
* Here the footer can set as static and also in dynamic ways.if the list has dats the the
* footer will be added in the listview which is the dynamic creation of the footer.
* If there is no data on the list then the static footer will be shown.
* */
    public void showOrHideFooter() {
        final View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.mdlive_footer, null, false);

        // If list size is greater than zero then show the bottom footer
        if (ReasonList != null && ReasonList.size() > IntegerConstants.NUMBER_ZERO) {
            findViewById(R.id.footer).setVisibility(View.GONE);

            if (listView.getFooterViewsCount() == IntegerConstants.NUMBER_ZERO) {

                listView.addFooterView(footerView, null, false);
            }
        }
        // If list size is zero then remove the bootm footer & add the list footer
        else {
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
            if (listView.getFooterViewsCount() > IntegerConstants.NUMBER_ZERO) {
                listView.removeFooterView(footerView);
            }
        }
    }

    /**
     * Filter Search for the Listview. we can filter the list by giving the name and if the name
     * is not in the listview then it will ask for submitting the name to the service.
     */
    public void RefineSearch() {
        final EditText search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > IntegerConstants.NUMBER_ZERO && s.subSequence(0, 1).toString().equalsIgnoreCase(" ")) {
                    search_edit.setText("");
                    search_edit.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() == IntegerConstants.NUMBER_ZERO) {
                    deleteTextBtn.setVisibility(View.GONE);
                } else {
                    deleteTextBtn.setVisibility(View.VISIBLE);
                }
                if (!text.startsWith(" ")) {
                    baseadapter.getFilter().filter(s.toString());
                }
                baseadapter.notifyDataSetChanged();
                baseadapter.getFilter().filter(s.toString());
            }
        });
    }

    /**
     * Item Click Listener for the ListView.Here the validations for the age has been
     * done.This validation is for the Pediatric users.
     */

    public void ListItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(PreferenceConstants.REASON, listView.getAdapter().getItem(position).toString());
                    editor.commit();
                    //MDLivePharmacy
                    if (MdliveUtils.calculteAgeFromPrefs(MDLiveReasonForVisit.this) <= IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
                        Intent Reasonintent = new Intent(MDLiveReasonForVisit.this, MDLivePediatric.class);
                        startActivity(Reasonintent);
                        MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);

                    } else {
                        Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveMedicalHistory.class);
                        startActivity(medicalIntent);
                        MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveReasonForVisit.this);
    }
}


