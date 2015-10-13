package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.BehaviouralService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MDLiveBehaviouralHealthHistory extends Activity {
    private CheckBox chkAlcoholParent, chkObsessiveParent, chkAnxietyParent, chkPanicParent, chkBipolarParent, chkSchizParent, chkDepressionParent, chkSubstanceParent,
            chkAlcoholChild, chkObsessiveChild, chkBipolarChild, chkSchizChild, chkDepressionChild, chkSubstanceChild;
    private Button btnYesParent, btnNoParent, btnYesChild, btnNoChild, btnSave;
    private TextView txtDate, txtGender;
    private EditText edtPeriod;
    private ProgressDialog pDialog;
    private HashMap<String, String> conditonMap;

    private String hospitalized;
    private String familyHospitalized;
    private String hospitalizedDate;
    private String hospitalizedDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_behavioural_health_history);
        initializeUI();
        pDialog = MdliveUtils.getFullScreenProgressDialog(this);
        conditonMap = new HashMap<>();
        getUserHealthHistories();

    }

    public void initializeUI() {
        chkAlcoholParent = (CheckBox) findViewById(R.id.check_alcohol_parent);
        chkAlcoholChild = (CheckBox) findViewById(R.id.check_alcohol_child);
        chkObsessiveParent = (CheckBox) findViewById(R.id.check_obsessive_parent);
        chkObsessiveChild = (CheckBox) findViewById(R.id.check_obsessive_child);
        chkAnxietyParent = (CheckBox) findViewById(R.id.check_anxiety_parent);
        chkPanicParent = (CheckBox) findViewById(R.id.check_panic_parent);
        chkBipolarParent = (CheckBox) findViewById(R.id.check_bipolar_parent);
        chkBipolarChild = (CheckBox) findViewById(R.id.check_bipolar_child);
        chkSchizParent = (CheckBox) findViewById(R.id.check_schiz_parent);
        chkSchizChild = (CheckBox) findViewById(R.id.check_schiz_child);
        chkDepressionParent = (CheckBox) findViewById(R.id.check_depression_parent);
        chkDepressionChild = (CheckBox) findViewById(R.id.check_depression_child);
        chkSubstanceParent = (CheckBox) findViewById(R.id.check_substance_parent);
        chkSubstanceChild = (CheckBox) findViewById(R.id.check_substance_child);

        btnSave = (Button) findViewById(R.id.btn_save_behavioral);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MDLiveBehaviouralHealthHistory.this, "" + chkAlcoholParent.isChecked(), Toast.LENGTH_SHORT).show();
            }
        });


        chkAlcoholParent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(MDLiveBehaviouralHealthHistory.this, buttonView.getText().toString() + isChecked, Toast.LENGTH_SHORT).show();

            }
        });

    }


    /**
     * This function will retrieve all user behavioural health stories from the server.
     *
     * @Listner-successListner will  handles the success response from server.
     * @Listner-errorListener will handles error response from server.
     * BehaviouralService class will send the request to the server and receives the corresponding response
     */
    public void getUserHealthHistories() {
        MdliveUtils.showProgressDialog(pDialog);
        NetworkSuccessListener successListner = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                Log.e("Response History", response.toString());
                handleSuccessResponse(response.toString());
                MdliveUtils.hideProgressDialog(pDialog);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLiveBehaviouralHealthHistory.this, error, pDialog);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveBehaviouralHealthHistory.this);
                }
            }
        };
        BehaviouralService behaviourService = new BehaviouralService(MDLiveBehaviouralHealthHistory.this, pDialog);
        behaviourService.doGetBehavioralHealthService(successListner, errorListener);
    }

    public void handleSuccessResponse(String response) {

        try {
            JSONObject resObj = new JSONObject(response);
            JSONArray arrayObj = resObj.getJSONArray("behavioral_mconditions");
            JSONArray familyArrayObj = resObj.getJSONArray("behavioral_family_history");
            for (int i = 0; i < arrayObj.length(); i++) {
                JSONObject mCondItemObj = arrayObj.getJSONObject(i);
                conditonMap.put(mCondItemObj.getString("condition"), mCondItemObj.getString("active"));
            }
            for (int i = 0; i < familyArrayObj.length(); i++) {
                JSONObject mCondfamyItemObj = familyArrayObj.getJSONObject(i);
                conditonMap.put(mCondfamyItemObj.getString("condition"), mCondfamyItemObj.getString("active"));
            }
            hospitalized = resObj.getString("hospitalized");
            familyHospitalized = resObj.getString("family_hospitalized");
            hospitalizedDate = resObj.getString("hospitalized_date");
            hospitalizedDuration = resObj.getString("hospitalized_duration");

            enableConditions();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void enableConditions() {
        checkConditions(chkAlcoholParent, conditonMap.get(chkAlcoholParent.getText().toString()));
        checkConditions(chkAlcoholChild, conditonMap.get(chkAlcoholChild.getText().toString()));
        checkConditions(chkAnxietyParent, conditonMap.get(chkAnxietyParent.getText().toString()));
        checkConditions(chkObsessiveParent, conditonMap.get(chkObsessiveParent.getText().toString()));
        checkConditions(chkObsessiveChild, conditonMap.get(chkObsessiveChild.getText().toString()));
        checkConditions(chkPanicParent, conditonMap.get(chkPanicParent.getText().toString()));
        checkConditions(chkBipolarParent, conditonMap.get(chkBipolarParent.getText().toString()));
        checkConditions(chkBipolarChild, conditonMap.get(chkBipolarChild.getText().toString()));
        checkConditions(chkSchizParent, conditonMap.get(chkSchizParent.getText().toString()));
        checkConditions(chkSchizChild, conditonMap.get(chkSchizChild.getText().toString()));
        checkConditions(chkDepressionParent, conditonMap.get(chkDepressionParent.getText().toString()));
        checkConditions(chkSubstanceParent, conditonMap.get(chkSubstanceParent.getText().toString()));
        checkConditions(chkSubstanceChild, conditonMap.get(chkSubstanceChild.getText().toString()));
    }

    public void checkConditions(CheckBox chkBox, String active) {
        if (conditonMap.get(chkBox.getText().toString()).equals(active)) {
            chkBox.setChecked(true);
        } else {
            chkBox.setChecked(false);
        }
    }

}
