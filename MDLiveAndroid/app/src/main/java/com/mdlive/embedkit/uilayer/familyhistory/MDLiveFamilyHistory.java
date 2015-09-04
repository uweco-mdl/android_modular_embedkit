package com.mdlive.embedkit.uilayer.familyhistory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.FamilyHistoryModel;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.familyhistory.FamilyHistoryServices;
import com.mdlive.unifiedmiddleware.services.familyhistory.FamilyHistoryUpdateServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MDLiveFamilyHistory extends MDLiveBaseAppcompatActivity {
    private EditText mFamilyHistoryOtherEditText;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;

    private JSONObject innerJsonObject;
    private JSONArray innerJsonArray;
    private String rootJsonObjectString;
    private HashMap<String, String> familyHistory_conditionValue = new HashMap<String, String>();

    private String mFamilyHistoryOtherEditTextValue;

    private List<FamilyHistoryModel> familyHistoryList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_family_histroy);

        findWidgetId();
        getFamilyHistoryServiceData();

    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:
                onHomeClicked();
                break;

            // See a Doctor
            case 1:
                onSeeADoctorClicked();
                break;

            // MDLive My Health
            case 2:
                startActivityWithClassName(MedicalHistoryActivity.class);
                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:
                onMessageClicked();
                break;

            // Symptom Checker
            case 5:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 6:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 7:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share
            case 8:
                shareApplication();
                break;
        }
    }

    private void findWidgetId() {

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        mFamilyHistoryOtherEditText = (EditText)findViewById(R.id.my_family_history_other_editText);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void getFamilyHistoryServiceData() {

        setProgressBarVisibility();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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
                    MdliveUtils.handelVolleyErrorResponse(MDLiveFamilyHistory.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveFamilyHistory.this);
                }
            }};

        FamilyHistoryServices services = new FamilyHistoryServices(MDLiveFamilyHistory.this , pDialog);
        services.getFamilyHistoryServices(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("FamilyHistory Res", response.toString());

            JSONObject resObj = new JSONObject(response.toString());
            JSONArray resArray = resObj.getJSONArray("family_histories");
            JSONObject resJsonObj;

            familyHistoryList = new ArrayList<FamilyHistoryModel>();

            for(int i = 0; i < resArray.length(); i++) {
                resJsonObj = resArray.getJSONObject(i);
                final FamilyHistoryModel model = new FamilyHistoryModel(resJsonObj.getString("relationship"), resJsonObj.getString("condition"), resJsonObj.getString("active"));

                Log.d("FamilyHistoryResVal = ", model.toString());
                familyHistoryList.add(model);
            }

            setFamilyHistory();

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

    public void saveAction(View view) {
        final JSONObject requestJSON = new JSONObject();

        try {

            final JSONArray lifeStyleConditionJSONArray = new JSONArray();

            for (int i = 0; i < familyHistoryList.size(); i++) {
                final JSONObject jsonObject = new JSONObject();

                jsonObject.put("relationship", familyHistoryList.get(i).relationship);
                jsonObject.put("condition", familyHistoryList.get(i).condition);
                jsonObject.put("active", familyHistoryList.get(i).active);

                Log.d("HELLO", familyHistoryList.get(i).toString());

                lifeStyleConditionJSONArray.put(jsonObject);
            }

            requestJSON.put("family_histories", lifeStyleConditionJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initializeJsonElements();

        getFamilyHistoryUpdateServiceData(requestJSON);
    }

    private void getFamilyHistoryUpdateServiceData(final JSONObject requestJSON) {

        setProgressBarVisibility();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    handleUpdateSuccessResponse(response);
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
                    MdliveUtils.handelVolleyErrorResponse(MDLiveFamilyHistory.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveFamilyHistory.this);
                }
            }};

        FamilyHistoryUpdateServices familyHistoryUpdateServices = new FamilyHistoryUpdateServices(MDLiveFamilyHistory.this, pDialog);
        familyHistoryUpdateServices.postFamilyHistoryUpdateServices(requestJSON, responseListener, errorListener);

    }

    private void handleUpdateSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("FamilyHistoryUpdateRes", response.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void initializeJsonElements() {

        innerJsonObject = new JSONObject();
        innerJsonArray = new JSONArray();

        try {
            innerJsonObject.put("family_histories",innerJsonArray);
            rootJsonObjectString = innerJsonObject.toString();
            familyHistory_conditionValue.put("", rootJsonObjectString);
        }
        catch (JSONException e) {

        }

    }

    public void addAction (View view) {
        if(mFamilyHistoryOtherEditText.getText().length() != 0) {
            mFamilyHistoryOtherEditTextValue = mFamilyHistoryOtherEditText.getText().toString();
            Log.d("FamilyHistoryOtherValue",mFamilyHistoryOtherEditTextValue.toString());
        }
        else {
            mFamilyHistoryOtherEditTextValue = "";
            Log.d("FamilyHistoryOtherValue",mFamilyHistoryOtherEditTextValue);
        }
    }

    private void getFamilyHistoryOtherEditTextEvent() {

        mFamilyHistoryOtherEditText.addTextChangedListener(new TextWatcher() {

            @SuppressLint("ShowToast")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                mFamilyHistoryOtherEditText.setCursorVisible(true);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                mFamilyHistoryOtherEditTextValue = mFamilyHistoryOtherEditText.getText().toString();
                Log.d("FamilyHistoryOtherValue", mFamilyHistoryOtherEditTextValue.toString());

            }
        });


    }

    private void setFamilyHistory() {
        if (familyHistoryList == null || familyHistoryList.size() == 0) {
            return;
        }

        final LinearLayout scrollLinearLayout = (LinearLayout) findViewById(R.id.mdlive_family_scroll_view);

        for (int i = 0; i < familyHistoryList.size(); i++) {
            final int position = i;

            // Inflate Row
            final LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            final View rootLinearLayout = inflater.inflate(R.layout.mdlive_familyhistory_addrows, null, false);

            // Inflte & get reference of Views to be used
            final CheckBox checkBox = (CheckBox) rootLinearLayout.findViewById(R.id.my_family_history_checkBox);
            final Spinner spinner = (Spinner) rootLinearLayout.findViewById(R.id.my_family_history_checkBox_spinner);

            // Set Checkbox values & check changed listener
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        familyHistoryList.get(position).active = FamilyHistoryModel.YES;
                        spinner.setVisibility(View.VISIBLE);
                    } else {
                        familyHistoryList.get(position).active = FamilyHistoryModel.NO;
                        spinner.setVisibility(View.INVISIBLE);
                    }
                }
            });

            if (FamilyHistoryModel.YES.toString().equalsIgnoreCase(familyHistoryList.get(position).active)) {
                checkBox.setChecked(true);
                spinner.setVisibility(View.VISIBLE);

                if ("null".equalsIgnoreCase(familyHistoryList.get(position).relationship) || familyHistoryList.get(position).relationship == null) {
                    // Do not set selection
                    spinner.setPrompt(getString(R.string.family_history_spinner_promt));
                    Log.d("Hello", "I am here");
                }
            } else {
                checkBox.setChecked(false);
                spinner.setVisibility(View.INVISIBLE);
            }

            checkBox.setText(familyHistoryList.get(position).condition);

            // Set Spinner values & selection listener
            final List<String> relationShpList = Arrays.asList(getResources().getStringArray(R.array.Relationship));
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (getBaseContext(), android.R.layout.simple_spinner_item, relationShpList);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int selectedIndex, long id) {
                    familyHistoryList.get(position).relationship = relationShpList.get(selectedIndex);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if ("null".equalsIgnoreCase(familyHistoryList.get(position).relationship) || familyHistoryList.get(position).relationship == null) {
                // Do not set selection
                spinner.setPrompt(getString(R.string.family_history_spinner_promt));
            } else {
                int selectedPosition = 0;
                for (int j = 0; j < relationShpList.size(); j++) {
                    if (familyHistoryList.get(position).relationship.toLowerCase().trim().equalsIgnoreCase(relationShpList.get(j).toString().trim())) {
                        selectedPosition = j;
                        break;
                    }
                }
                spinner.setSelection(selectedPosition);
            }
            spinner.setPrompt(getString(R.string.family_history_spinner_promt));


            scrollLinearLayout.addView(rootLinearLayout);
        }
    }
}
