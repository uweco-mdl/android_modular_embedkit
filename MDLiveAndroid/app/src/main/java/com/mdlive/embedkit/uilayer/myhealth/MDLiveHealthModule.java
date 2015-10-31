package com.mdlive.embedkit.uilayer.myhealth;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.AddAllergyServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AddMedicalConditionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AddMedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.AddProcedureServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AllergiesUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AllergyAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.ProcedureAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.ProcedureListServices;
import com.mdlive.unifiedmiddleware.services.myhealth.ProcedureUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.SuggestMedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateMedicationService;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import static com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.newInstance;

/**
 * Created by srinivasan_ka on 8/26/2015.
 */
public class MDLiveHealthModule extends MDLiveBaseActivity {

//    public ArrayList<String> existingConditions;
    public enum TYPE_CONSTANT {CONDITION, ALLERGY, MEDICATION, PROCEDURE}

    protected TYPE_CONSTANT type;
    public boolean isPerformingAutoSuggestion = false, allowtoDisplayContents = true;
    public AutoCompleteTextView conditionText;
    public boolean isUpdateMode = false;
    public LinkedList<String> procedureNameList = new LinkedList<>();
    public LinkedList<String> procedureYearList = new LinkedList<>();
    public AlertDialog procedureNameDialog, procedureYearDialog, timesDialog, modeDialog;
    public TextView surgeryName, surgeryYear, errorText;
    public EditText dosageTxt, otherProcedureTxt;
    String[] timesList = new String[]{
            "Once","Twice","Three times","Four times", "Five times", "Six times"
    };
    String[] modesList =  new String[]{
            "Daily", "Hourly", "Weekly","Monthly"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_add_health);
        clearMinimizedTime();

        //existingConditions = new ArrayList<>();

        if(getIntent() != null && getIntent().hasExtra("type")){
            if(getIntent().getStringExtra("type").equals("condition")){
                type = TYPE_CONSTANT.CONDITION;
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_add_medical_condition));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.mdl_add_condition_with_eg_hint));
            }else if(getIntent().getStringExtra("type").equals("allergy")){
                type = TYPE_CONSTANT.ALLERGY;
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_add_allergy));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.mdl_add_allergies_with_eg_hint));
            }else if(getIntent().getStringExtra("type").equals("medication")){
                type = TYPE_CONSTANT.MEDICATION;
                findViewById(R.id.medicationCredentailsLayout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_add_medication));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.mdl_add_medication_hint));
            }else if(getIntent().getStringExtra("type").equals("procedure")){
                type = TYPE_CONSTANT.PROCEDURE;
                findViewById(R.id.conditionText).setVisibility(View.GONE);
                findViewById(R.id.procedureLayout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_add_procedure));
            }
        }

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setProgressBar(findViewById(R.id.progressBar));

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        findViewById(R.id.txtApply).setVisibility(View.GONE);

        conditionText = (AutoCompleteTextView) findViewById(R.id.conditionText);
        errorText = (TextView) findViewById(R.id.errorText);
        otherProcedureTxt = (EditText) findViewById(R.id.otherProcedureTxt);

        //Setting error Text for conditions/allergies/medications
        if(type.equals(TYPE_CONSTANT.CONDITION)){
            errorText.setText(getString(R.string.mdl_condition_not_found_txt));
        }else if(type.equals(TYPE_CONSTANT.MEDICATION)){
            errorText.setText(getString(R.string.mdl_medication_not_found_txt));
        }else if(type.equals(TYPE_CONSTANT.ALLERGY)){
            errorText.setText(getString(R.string.mdl_allergy_not_found_txt));
        }else{
            errorText.setText("");
        }

        if(type.equals(TYPE_CONSTANT.PROCEDURE)){
            initializeViews();
            if(getIntent() != null && getIntent().hasExtra("Name")){
                /*procedureNameList.add(getIntent().getStringExtra("Name"));
                procedureYearList.add(getIntent().getStringExtra("Year"));*/
                surgeryName.setText(getIntent().getStringExtra("Name"));
                surgeryYear.setText(getIntent().getStringExtra("Year"));
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                isUpdateMode = true;
            }else{
                isUpdateMode = false;
            }
        }else if(type.equals(TYPE_CONSTANT.MEDICATION)){
            initializeMedicationViews();
            if(getIntent() != null && getIntent().hasExtra("Name")){
                conditionText.setText(getIntent().getStringExtra("Name"));
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                isUpdateMode = true;
            }else{
                isUpdateMode = false;
            }
            if(getIntent() != null && getIntent().hasExtra("Dosage")){
                ((EditText) findViewById(R.id.dosageTxt)).setText(getIntent().getStringExtra("Dosage"));
            }
            if(getIntent() != null && getIntent().hasExtra("Frequency")){
                for(String times : timesList){
                    if(getIntent().getStringExtra("Frequency").toLowerCase().contains(times.toLowerCase())){
                        ((TextView) findViewById(R.id.timesTxt)).setText(times);
                    }
                }
                for(String modes : modesList){
                    if(getIntent().getStringExtra("Frequency").toLowerCase().contains(modes.toLowerCase())){
                        ((TextView) findViewById(R.id.modeTxt)).setText(modes);
                    }
                }
            }
            conditionText.addTextChangedListener(getEditTextWatcher(conditionText));
        }else{
            if(getIntent() != null && getIntent().hasExtra("Content")){
                isUpdateMode = true;
                conditionText.setText(getIntent().getStringExtra("Content"));
            }else{
                isUpdateMode = false;
            }
            conditionText.addTextChangedListener(getEditTextWatcher(conditionText));

            conditionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE){
                            rightBtnOnClick(null);
                    }
                    return false;
                }
            });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

    }

    public void surgeryNameClick(View view){
        if(procedureNameDialog != null){
            procedureNameDialog.show();
        }
    }
    public void surgeryYearClick(View view){
        if(procedureYearDialog != null){
            procedureYearDialog.show();
        }
    }

    public void timesTxtOnClick(View view){
        if(timesDialog != null){
            timesDialog.show();
        }
    }

    public void modeTxtOnClick(View view){
        if(modeDialog != null){
            modeDialog.show();
        }
    }


    public void initializeMedicationViews() {
        dosageTxt = ((EditText) findViewById(R.id.dosageTxt));

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveHealthModule.this);
        LayoutInflater inflater = getLayoutInflater();

        View nameView = inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(nameView);
        ListView nameListView = (ListView) nameView.findViewById(R.id.popupListview);

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timesList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        timesDialog = alertDialog.create();
        ((TextView)findViewById(R.id.timesTxt)).setText(timesList[0]);
        findViewById(R.id.timesTxt).setContentDescription(getString(R.string.mdl_ada_dropdown) + timesList[0]);
        ((TextView)findViewById(R.id.modeTxt)).setText(modesList[0]);
        findViewById(R.id.modeTxt).setContentDescription(getString(R.string.mdl_ada_dropdown) + modesList[0]);
        View yearView = inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(yearView);
        ListView yearListView = (ListView) yearView.findViewById(R.id.popupListview);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,modesList);
        yearListView.setAdapter(yearAdapter);
        yearListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        modeDialog = alertDialog.create();
        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.timesTxt)).setText(timesList[position]);
                timesDialog.dismiss();
            }
        });
        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.modeTxt)).setText(modesList[position]);
                modeDialog.dismiss();
            }
        });
    }


    public void initializeViews() {
        surgeryName = ((TextView) findViewById(R.id.surgeryName));
        surgeryYear = ((TextView) findViewById(R.id.surgeryYear));
        surgeryName.setContentDescription(getString(R.string.mdl_ada_dropdown)+getString(R.string.mdl_select_surgery_txt));
        surgeryYear.setContentDescription(getString(R.string.mdl_ada_dropdown)+getString(R.string.mdl_year_txt));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveHealthModule.this);
        LayoutInflater inflater = getLayoutInflater();

        View nameView = inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(nameView);
        ListView nameListView = (ListView) nameView.findViewById(R.id.popupListview);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, procedureNameList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        procedureNameDialog = alertDialog.create();

        View yearView = inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(yearView);
        ListView yearListView = (ListView) yearView.findViewById(R.id.popupListview);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, procedureYearList);
        yearListView.setAdapter(yearAdapter);
        yearListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        procedureYearDialog = alertDialog.create();


        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.surgeryName)).setText(procedureNameList.get(position));
                procedureNameDialog.dismiss();
                if(!surgeryName.getText().toString().equals(getString(R.string.mdl_select_surgery_txt))
                        && !surgeryYear.getText().equals(getString(R.string.mdl_year_txt))){
                    if(surgeryName.getText().toString().equals(getString(R.string.mdl_procedure_other_txt))){
                        if(otherProcedureTxt.getText() != null &&
                                otherProcedureTxt.getText().toString().length() != 0){
                            if(!surgeryName.getText().toString().equals(getString(R.string.mdl_select_surgery_txt))
                                    && !surgeryYear.getText().equals(getString(R.string.mdl_year_txt))){
                                surgeryName.setContentDescription(getString(R.string.mdl_ada_dropdown)+surgeryName.getText().toString());
                                surgeryYear.setContentDescription(getString(R.string.mdl_ada_dropdown)+surgeryYear.getText().toString());
                                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                            }else{
                                findViewById(R.id.txtApply).setVisibility(View.GONE);
                            }
                        }else{
                           findViewById(R.id.txtApply).setVisibility(View.GONE);
                        }
                    }else{
                        surgeryName.setContentDescription(getString(R.string.mdl_ada_dropdown)+surgeryName.getText().toString());
                        surgeryYear.setContentDescription(getString(R.string.mdl_ada_dropdown)+surgeryYear.getText().toString());
                        findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                    }
                }
                if(surgeryName.getText().toString().equals(getString(R.string.mdl_procedure_other_txt))){
                    otherProcedureTxt.setVisibility(View.VISIBLE);
                }else{
                    otherProcedureTxt.setVisibility(View.GONE);
                }
            }
        });

        otherProcedureTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otherProcedureTxt.getVisibility() == View.VISIBLE){
                    if(s != null && s.length() > 0){
                        if(!surgeryName.getText().toString().equals(getString(R.string.mdl_select_surgery_txt))
                                && !surgeryYear.getText().equals(getString(R.string.mdl_year_txt))){
                            findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                        }else{
                            findViewById(R.id.txtApply).setVisibility(View.GONE);
                        }
                    }else{
                        findViewById(R.id.txtApply).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.surgeryYear)).setText(procedureYearList.get(position));
                procedureYearDialog.dismiss();
                if(!surgeryName.getText().toString().equals(getString(R.string.mdl_select_surgery_txt))
                        && !surgeryYear.getText().equals(getString(R.string.mdl_year_txt))){

                    if(surgeryName.getText().toString().equals(getString(R.string.mdl_procedure_other_txt))) {
                        if (otherProcedureTxt.getText() != null &&
                                otherProcedureTxt.getText().toString().length() != 0) {
                            if (!surgeryName.getText().toString().equals(getString(R.string.mdl_select_surgery_txt))
                                    && !surgeryYear.getText().equals(getString(R.string.mdl_year_txt))) {
                                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.txtApply).setVisibility(View.GONE);
                            }
                        } else {
                            findViewById(R.id.txtApply).setVisibility(View.GONE);
                        }
                }
                else{
                        findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                    }
                }

                if(surgeryName.getText().toString().equals(getString(R.string.mdl_procedure_other_txt))){
                    otherProcedureTxt.setVisibility(View.VISIBLE);
                }else{
                    otherProcedureTxt.setVisibility(View.GONE);
                }
            }
        });

        try {
//            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
            String dateofBirth =userBasicInfo.getPersonalInfo().getBirthdate();
//            String dateofBirth = sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH, null);
            procedureYearList.clear();
            Log.e("dateofBirth", dateofBirth);
            if(dateofBirth != null){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                int years = MdliveUtils.calculateAge(sdf.parse(dateofBirth),this);
                years = TimeZoneUtils.getCalendarWithOffset(this).get(Calendar.YEAR) - years;
                for(int i = years; i <= TimeZoneUtils.getCalendarWithOffset(this).get(Calendar.YEAR); i++){
                    procedureYearList.add(i+"");
                    Log.e("Years--->", i+"");
                }
                if(procedureYearList.size() == 0){
                    procedureYearList.add(TimeZoneUtils.getCalendarWithOffset(this).get(Calendar.YEAR)+"");
                }
            }
            yearAdapter.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getSurgeryNameList(nameAdapter);

    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to delete allergy details
     *
     */
    public void getSurgeryNameList(final ArrayAdapter<String> nameAdapter) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleResponse(response, nameAdapter);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                medicalCommonErrorResponseHandler(error);
            }
        };
        ProcedureListServices services = new ProcedureListServices(MDLiveHealthModule.this, null);
        services.getAllergyListRequest(successCallBackListener, errorListener);
    }

    public void handleResponse(JSONObject response, ArrayAdapter<String> nameAdapter){
        try {
            hideProgress();
            procedureNameList.clear();
            boolean isItemAvailableInList = false;
            if(response != null){
                if(response.has("surgeries")){
                    JSONArray surgeriesArray = response.getJSONArray("surgeries");
                    for(int i = 1; i < surgeriesArray.length(); i++){
                        JSONObject item = surgeriesArray.getJSONObject(i);
                        procedureNameList.add(item.getString("name"));
                        if(surgeryName.getText().toString().equals(item.getString("name"))){
                            isItemAvailableInList = true;
                        }
                    }
                }
                if(isUpdateMode){
                    if(!isItemAvailableInList){
                        procedureNameList.add(surgeryName.getText().toString());
                    }
                }

                procedureNameList.add(getString(R.string.mdl_procedure_other_txt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nameAdapter.notifyDataSetChanged();
        if(getIntent() != null && getIntent().hasExtra("Name")){
            surgeryName.setText(getIntent().getStringExtra("Name"));
            surgeryYear.setText(getIntent().getStringExtra("Year"));
        }
    }




    /**
     * This function handles onClick event of done text in layout
     * saveBtnAction - is used to add new condition/allergy/medication
     */
    public void rightBtnOnClick(View view) {
        MdliveUtils.hideSoftKeyboard(MDLiveHealthModule.this);
        if(type.equals(TYPE_CONSTANT.PROCEDURE)){
            if(surgeryName.getText() != null && !surgeryName.getText().toString().equals("Select Surgery Name")
                    && surgeryYear.getText() != null && !surgeryYear.getText().toString().equals("Year")) {
                if(isUpdateMode){
                    saveBtnAction();
                }else{
                    saveBtnAction();
                }
            }
        }else{
            if (conditionText.getText() != null && conditionText.getText().toString().length() > 0) {
                if(isUpdateMode){
                    updateNewConditionsOrAllergies(conditionText.getText().toString(), getIntent().getStringExtra("Id"));
                }else{
                    saveBtnAction();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveHealthModule.this);
    }

    public void leftBtnOnClick(View v) {
        finish();
        MdliveUtils.closingActivityAnimation(MDLiveHealthModule.this);
    }



    /**
     * This function handles the saving of data when the user presses the save button. Only the newly
     * added conditions are saved.
     */
    private void saveBtnAction() {
        //Converting ArrayList to HashSet to remove duplicates
        if(type == TYPE_CONSTANT.PROCEDURE){
            if(surgeryName.getText() != null && !surgeryName.getText().toString().equals("Select Surgery Name")
                    && surgeryYear.getText() != null && !surgeryYear.getText().toString().equals("Year")){
                HashMap<String, HashMap<String, String>> allergies = new HashMap<>();
                HashMap<String, String> map = new HashMap<>();
                if(surgeryName.getText().toString().equals(getString(R.string.mdl_procedure_other_txt))){
                    map.put("name", otherProcedureTxt.getText().toString());
                }else{
                    map.put("name", surgeryName.getText().toString());
                }
                map.put("surgery_year", surgeryYear.getText().toString());
                allergies.put("surgery", map);
                Log.e("Post Body ", new Gson().toJson(allergies));
                saveNewConditionsOrAllergies(new Gson().toJson(allergies));
            }
        }else if(type == TYPE_CONSTANT.MEDICATION){
            LinkedHashSet<String> listToSet = new LinkedHashSet<String>();
            ArrayList<String> duplicationCollection = MDLiveCommonConditionsMedicationsActivity.conditionsCollection;
            listToSet.addAll(duplicationCollection);
            listToSet.add(conditionText.getText().toString().toLowerCase());
            if (((duplicationCollection.size() + 1) == listToSet.size())) {
                HashMap<String, HashMap<String, String>> medications = new HashMap<>();
                HashMap<String, String> map = new HashMap<>();
                map.put("name", conditionText.getText().toString());
                map.put("current_status", "Yes");
                if(dosageTxt.getText() != null && dosageTxt.getText().toString().length() != 0){
                    map.put("dosage", dosageTxt.getText().toString());
                }else{
                    map.put("dosage", "");
                }
                map.put("frequency", ((TextView)findViewById(R.id.timesTxt)).getText().toString() +" "+
                        ((TextView)findViewById(R.id.modeTxt)).getText().toString());
                medications.put("medication", map);
                Log.e("Post Body ", new Gson().toJson(medications));
                saveNewConditionsOrAllergies(new Gson().toJson(medications));
            }else{
                MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.mdl_medication_already_exist));
            }
        }else{
            LinkedHashSet<String> listToSet = new LinkedHashSet<String>();
            ArrayList<String> duplicationCollection = MDLiveCommonConditionsMedicationsActivity.conditionsCollection;
            listToSet.addAll(duplicationCollection);
            listToSet.add(conditionText.getText().toString().toLowerCase());
            if (((duplicationCollection.size() + 1) == listToSet.size())) {
                saveNewConditionsOrAllergies(conditionText.getText().toString());
            } else {
                if (type == TYPE_CONSTANT.CONDITION) {
                    MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.mdl_condition_already_exist));
                }else if (type == TYPE_CONSTANT.ALLERGY) {
                    MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.mdl_allergy_already_exist));
                }
            }
        }
    }


    /**
     * This function will n=make the service call to get the auto-completion allergies list based up
     * on the data entered in the edit text.
     * MedicalConditionAutoSuggestionServices - The service class for getting the Auto suggestion list.
     *
     * @param atv        :: The auto completion text view
     * @param constraint :: The text entered by the user.
     */
    protected void getAutoCompleteData(final AutoCompleteTextView atv, String constraint) {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isPerformingAutoSuggestion = false;
                autoCompletionHandleSuccessResponse(atv, response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isPerformingAutoSuggestion = false;
                medicalCommonErrorResponseHandler(error);
            }
        };
        if (!isPerformingAutoSuggestion /*&& !previousSearch.equalsIgnoreCase(constraint)*/) {
            if(type.equals(TYPE_CONSTANT.ALLERGY)){
                AllergyAutoSuggestionServices services = new AllergyAutoSuggestionServices(MDLiveHealthModule.this, null);
                services.getAllergyAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
                isPerformingAutoSuggestion = true;
            }else if(type.equals(TYPE_CONSTANT.CONDITION)){
                MedicalConditionAutoSuggestionServices services = new MedicalConditionAutoSuggestionServices(MDLiveHealthModule.this, null);
                services.getMedicalConditionsAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
                isPerformingAutoSuggestion = true;
            }else if(type.equals(TYPE_CONSTANT.MEDICATION)){
                SuggestMedicationService services = new SuggestMedicationService(MDLiveHealthModule.this, null);
                services.doLoginRequest(constraint, successCallBackListener, errorListener);
                isPerformingAutoSuggestion = true;
            }else if(type.equals(TYPE_CONSTANT.PROCEDURE)){
                ProcedureAutoSuggestionServices services = new ProcedureAutoSuggestionServices(MDLiveHealthModule.this, null);
                services.getAllergyAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
                isPerformingAutoSuggestion = true;
            }
        }
    }

    /**
     * The success Response Handler for Auto completion. The auto completion data is fetched from the
     * JSON response and the same is set on the array adapter. This adapter is assigned to the auto-completion-textview
     * and the same is displayed as dropdown.
     *
     * @param atv      :: The AutoCompletionTextView
     * @param response :: JSON response from the service
     */
    protected void autoCompletionHandleSuccessResponse(final AutoCompleteTextView atv, JSONObject response) {
        try {
            JSONArray conditionArray = response.getJSONArray((type == TYPE_CONSTANT.CONDITION) ? "conditions" : type == (TYPE_CONSTANT.ALLERGY) ? "allergies" : "medications");
            ArrayList<String> conditionList = new ArrayList<String>();
            for (int i = 0; i < conditionArray.length(); i++) {
                conditionList.add(conditionArray.getJSONObject(i).getString("name"));
            }
            if (conditionList.size() > 0 && atv.hasFocus() && allowtoDisplayContents) {
                if (atv.getAdapter() != null) {
                    ((ArrayAdapter<String>) atv.getAdapter()).clear();
                }
                ArrayAdapter<String> adapter = getAutoCompletionArrayAdapter(atv, conditionList);
                atv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                atv.showDropDown();
                atv.setDropDownVerticalOffset(0);
                MdliveUtils.showSoftKeyboard(this, atv);
            }
            if(errorText.getText() != null && errorText.getText().toString().length() != 0){
                if(conditionArray.length() == 0 && !atv.isPopupShowing()){
                    errorText.setVisibility(View.VISIBLE);
                }else{
                    errorText.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This function handles the text changes the add condition edit text. This function manages
     * the auto-completion feature of the edit-text.
     *
     * @param conditonEt :: The dynamic condition EditText
     * @return The TextWatcher object
     */
    private TextWatcher getEditTextWatcher(final EditText conditonEt) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                allowtoDisplayContents = true;
                if ((conditonEt.getText().toString()).startsWith(" ")) {
                    conditonEt.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = conditonEt.getText().toString().trim();
                if(text == null || text.length() == 0){
                    findViewById(R.id.txtApply).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
                }
                if (text.length() >= 3) {
                    getAutoCompleteData((AutoCompleteTextView) conditonEt, text);
                } else {
                    errorText.setVisibility(View.GONE);
                    // Need to clear the data or hide the auto compleete text dropdown
                    if (((AutoCompleteTextView) conditonEt).getAdapter() != null) {
                        ((ArrayAdapter<String>) ((AutoCompleteTextView) conditonEt).getAdapter()).clear();
                    }
                }
            }
        };
    }

    /**
     * This function creates an array adapter for the AutoCompletionTextView. Once the list item is
     * clicked, the text is set to the edit text and the autocompletion list is dismissed.
     *
     * @param atv           :: The AutoCompletionTextView
     * @param conditionList :: The conditions array list
     * @return The array adapter
     */
    private ArrayAdapter<String> getAutoCompletionArrayAdapter(final AutoCompleteTextView atv, final ArrayList<String> conditionList) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                parent.setBackgroundColor(Color.WHITE);
                final TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        atv.setText(text.getText().toString());
                        atv.dismissDropDown();
                        allowtoDisplayContents = false;
                        atv.setAdapter(null);
                    }
                });
                return view;
            }
        };
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     * <p/>
     * This function is used to save new condition details entered by user.
     */
    protected void saveNewConditionsOrAllergies(String text) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response String", response.toString());
                hideProgress();
                setResult(RESULT_OK);
                finish();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        if(type.equals(TYPE_CONSTANT.ALLERGY)){
            AddAllergyServices services = new AddAllergyServices(MDLiveHealthModule.this, null);
            services.addAllergyRequest(successCallBackListener, errorListener, text);
        }else if(type.equals(TYPE_CONSTANT.CONDITION)){
            AddMedicalConditionServices services = new AddMedicalConditionServices(MDLiveHealthModule.this, null);
            services.addMedicalConditionsRequest(successCallBackListener, errorListener, text);
        }else if(type.equals(TYPE_CONSTANT.MEDICATION)){
            AddMedicationService services = new AddMedicationService(MDLiveHealthModule.this, null);
            services.addMedicationRequest(successCallBackListener, errorListener,text);
        }else if(type.equals(TYPE_CONSTANT.PROCEDURE)){
            if(isUpdateMode){
                ProcedureUpdateServices services = new ProcedureUpdateServices(MDLiveHealthModule.this, null);
                services.updateAllergyRequest(getIntent().getStringExtra("Id"), text, successCallBackListener, errorListener);
            }else{
                AddProcedureServices services = new AddProcedureServices(MDLiveHealthModule.this, null);
                services.addAllergyRequest(successCallBackListener, errorListener, text);
            }
        }
    }





    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     * <p/>
     * This function is used to save new condition details entered by user.
     */
    protected void updateNewConditionsOrAllergies(String conditionName, String conditionId) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                setResult(RESULT_OK);
                finish();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };

        //{"medical_condition":{"id":53907,"condition":"Drug Dependence test"}}
        //{"allergy":{"id":10711,"name":"Penicillins test"}}

        HashMap<String, String> condition = new HashMap<>();
        HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();

        if(type.equals(TYPE_CONSTANT.ALLERGY)){
            condition.put("name", conditionName);
            condition.put("id", conditionId);
            postBody.put("allergy", condition);
            AllergiesUpdateServices services = new AllergiesUpdateServices(MDLiveHealthModule.this, null);
            services.updateAllergyRequest(conditionId, new Gson().toJson(postBody), successCallBackListener, errorListener);
        }else if(type.equals(TYPE_CONSTANT.CONDITION)){
            condition.put("id", conditionId);
            condition.put("condition", conditionName);
            postBody.put("medical_condition", condition);
            MedicalConditionUpdateServices services = new MedicalConditionUpdateServices(MDLiveHealthModule.this, null);
            services.updateConditionRequest(conditionId, new Gson().toJson(postBody), successCallBackListener, errorListener);
        }else if(type.equals(TYPE_CONSTANT.MEDICATION)){
            HashMap<String, HashMap<String, String>> medications = new HashMap<>();
            HashMap<String, String> map = new HashMap<>();
            map.put("name", conditionText.getText().toString());
            map.put("current_status", "Yes");
            if(dosageTxt.getText() != null && dosageTxt.getText().toString().length() != 0){
                map.put("dosage", dosageTxt.getText().toString());
            }else{
                map.put("dosage", "");
            }
            map.put("frequency", ((TextView)findViewById(R.id.timesTxt)).getText().toString() +" "+
                    ((TextView)findViewById(R.id.modeTxt)).getText().toString());
            medications.put("medication", map);
          /*  condition.put("name", conditionName);
            condition.put("id", conditionId);
            postBody.put("medication", condition);*/
            UpdateMedicationService services = new UpdateMedicationService(MDLiveHealthModule.this, null);
            services.doLoginRequest(conditionId, new Gson().toJson(medications), successCallBackListener, errorListener);
        }else if(type.equals(TYPE_CONSTANT.PROCEDURE)){
            condition.put("name", conditionName);
            condition.put("surgery_year", conditionId);
            postBody.put("surgery", condition);
            ProcedureUpdateServices services = new ProcedureUpdateServices(MDLiveHealthModule.this, null);
            services.updateAllergyRequest(conditionId, new Gson().toJson(postBody), successCallBackListener, errorListener);
        }
    }



    /**
     * Error Response Handler for Medical Conditions and allergies
     */
    protected void medicalCommonErrorResponseHandler(VolleyError error) {
        hideProgress();
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject errorObj = new JSONObject(responseBody);
            NetworkResponse errorResponse = error.networkResponse;
            if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                if (errorObj.has("message") || errorObj.has("error")) {
                    final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                    (MDLiveHealthModule.this).runOnUiThread(new Runnable() {
                        public void run() {
                            MdliveUtils.showDialog(MDLiveHealthModule.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, getString(R.string.mdl_ok_upper), null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                        }
                    });
                }
            } else {
                MdliveUtils.handelVolleyErrorResponse(MDLiveHealthModule.this, error, getProgressDialog());
            }
        }catch(Exception e){
            MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveHealthModule.this);
            e.printStackTrace();
        }

    }


}

