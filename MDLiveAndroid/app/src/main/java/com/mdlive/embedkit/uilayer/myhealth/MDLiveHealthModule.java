package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
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

/**
 * Created by srinivasan_ka on 8/26/2015.
 */
public class MDLiveHealthModule extends MDLiveBaseActivity {

    public ArrayList<String> existingConditions;

    public enum TYPE_CONSTANT {CONDITION, ALLERGY, MEDICATION, PROCEDURE};
    protected TYPE_CONSTANT type;
    public boolean isPerformingAutoSuggestion = false, allowtoDisplayContents = true;
    public AutoCompleteTextView conditionText;
    public boolean isUpdateMode = false;
    public LinkedList<String> procedureNameList = new LinkedList<>();
    public LinkedList<String> procedureYearList = new LinkedList<>();
    public AlertDialog procedureNameDialog, procedureYearDialog, dosageDialog, timesDialog, modeDialog;
    public TextView surgeryName, surgeryYear;
    public EditText dosageTxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_add_health);

        existingConditions = new ArrayList<>();

        if(getIntent() != null && getIntent().hasExtra("type")){
            if(getIntent().getStringExtra("type").equals("condition")){
                type = TYPE_CONSTANT.CONDITION;
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.add_medical_condition));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.add_condition_with_eg_hint));
            }else if(getIntent().getStringExtra("type").equals("allergy")){
                type = TYPE_CONSTANT.ALLERGY;
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.add_allergy));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.add_allergies_with_eg_hint));
            }else if(getIntent().getStringExtra("type").equals("medication")){
                type = TYPE_CONSTANT.MEDICATION;
                ((LinearLayout) findViewById(R.id.medicationCredentailsLayout)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.add_medication));
                ((EditText) findViewById(R.id.conditionText)).setHint(getString(R.string.add_medication_hint));
            }else if(getIntent().getStringExtra("type").equals("procedure")){
                type = TYPE_CONSTANT.PROCEDURE;
                ((EditText) findViewById(R.id.conditionText)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.procedureLayout)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.add_procedure));
            }
        }

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setProgressBar(findViewById(R.id.progressBar));

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);

        conditionText = (AutoCompleteTextView) findViewById(R.id.conditionText);

        if(type.equals(TYPE_CONSTANT.PROCEDURE)){
            initializeViews();
            if(getIntent() != null && getIntent().hasExtra("Name")){
                procedureNameList.add(getIntent().getStringExtra("Name"));
                procedureYearList.add(getIntent().getStringExtra("Year"));
                surgeryName.setText(getIntent().getStringExtra("Name"));
                surgeryYear.setText(getIntent().getStringExtra("Year"));
                ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.VISIBLE);
                isUpdateMode = true;
            }else{
                isUpdateMode = false;
            }
        }else{
            if(getIntent() != null && getIntent().hasExtra("Content")){
                isUpdateMode = true;
                conditionText.setText(getIntent().getStringExtra("Content"));
            }else{
                isUpdateMode = false;
            }
            conditionText.addTextChangedListener(getEditTextWatcher(conditionText));
            if(type.equals(TYPE_CONSTANT.MEDICATION)){
                initializeMedicationViews();
            }
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

        View nameView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(nameView);
        ListView nameListView = (ListView) nameView.findViewById(R.id.popupListview);
        final String[] timesList = new String[]{
                "One Time","Two Times","Three Times","Four Times", "Five Times"
        };
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timesList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        timesDialog = alertDialog.create();

        final String[] modesList =  new String[]{
                "Daily","Morning","Night","Evening"
        };
        ((TextView)findViewById(R.id.timesTxt)).setText(timesList[0]);
        ((TextView)findViewById(R.id.modeTxt)).setText(modesList[0]);
        View yearView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveHealthModule.this);
        LayoutInflater inflater = getLayoutInflater();

        View nameView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(nameView);
        ListView nameListView = (ListView) nameView.findViewById(R.id.popupListview);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, procedureNameList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        procedureNameDialog = alertDialog.create();

        View yearView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
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
                if(!surgeryName.getText().toString().equals(getString(R.string.select_surgery_txt))
                        && !surgeryYear.getText().equals(getString(R.string.year_txt))){
                    ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.VISIBLE);
                }
            }
        });

        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.surgeryYear)).setText(procedureYearList.get(position));
                procedureYearDialog.dismiss();
                if(!surgeryName.getText().toString().equals(getString(R.string.select_surgery_txt))
                        && !surgeryYear.getText().equals(getString(R.string.year_txt))){
                    ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.VISIBLE);
                }
            }
        });

        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String dateofBirth = sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH, null);
            if(dateofBirth != null){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                int years = MdliveUtils.calculateAge(sdf.parse(dateofBirth));
                years = Calendar.getInstance().get(Calendar.YEAR) - years;
                for(int i = years; i <= Calendar.getInstance().get(Calendar.YEAR); i++){
                    procedureYearList.add(i+"");
                    Log.e("Years--->", i+"");
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
            if(response != null){
                if(response.has("surgeries")){
                    JSONArray surgeriesArray = response.getJSONArray("surgeries");
                    for(int i = 1; i < surgeriesArray.length(); i++){
                        JSONObject item = surgeriesArray.getJSONObject(i);
                        procedureNameList.add(item.getString("name"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nameAdapter.notifyDataSetChanged();
        if(getIntent() != null && getIntent().hasExtra("Name")){
            procedureNameList.add(getIntent().getStringExtra("Name"));
            procedureYearList.add(getIntent().getStringExtra("Year"));
            surgeryName.setText(getIntent().getStringExtra("Name"));
            surgeryYear.setText(getIntent().getStringExtra("Year"));
        }
    }

    public void initializeSearchViews() {

        surgeryName = ((TextView) findViewById(R.id.surgeryName));
        surgeryYear = ((TextView) findViewById(R.id.surgeryYear));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveHealthModule.this);
        LayoutInflater inflater = getLayoutInflater();

        View nameView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(nameView);
        ListView nameListView = (ListView) nameView.findViewById(R.id.popupListview);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, procedureNameList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        procedureNameDialog = alertDialog.create();

        View yearView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
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
            }
        });

        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)findViewById(R.id.surgeryYear)).setText(procedureYearList.get(position));
                procedureYearDialog.dismiss();
            }
        });

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
                map.put("name", surgeryName.getText().toString());
                map.put("surgery_year", surgeryYear.getText().toString());
                allergies.put("surgery", map);
                Log.e("Post Body ", new Gson().toJson(allergies));
                saveNewConditionsOrAllergies(new Gson().toJson(allergies));
            }
        }else{
            LinkedHashSet<String> listToSet = new LinkedHashSet<String>();
            listToSet.addAll(existingConditions);
            listToSet.add(conditionText.getText().toString());
            if (((existingConditions.size() + 1) == listToSet.size())) {
                saveNewConditionsOrAllergies(conditionText.getText().toString());
            } else {
                String name = (type == TYPE_CONSTANT.CONDITION) ? "condition" : (type == TYPE_CONSTANT.ALLERGY) ? "allergy" : "medication";
                if (type == TYPE_CONSTANT.CONDITION) {
                    MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.condition_already_exist));
                } else if (type == TYPE_CONSTANT.MEDICATION) {
                    MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.medication_already_exist));
                } else if (type == TYPE_CONSTANT.ALLERGY) {
                    MdliveUtils.alert(null, MDLiveHealthModule.this, getResources().getString(R.string.allergy_already_exist));
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
                    ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
                }else{
                    ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.VISIBLE);
                }
                if (text.length() >= 3) {
                    getAutoCompleteData((AutoCompleteTextView) conditonEt, text);
                } else {
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
            services.doLoginRequest(successCallBackListener, errorListener,text);
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
            condition.put("name", conditionName);
            condition.put("id", conditionId);
            postBody.put("medication", condition);
            UpdateMedicationService services = new UpdateMedicationService(MDLiveHealthModule.this, null);
            services.doLoginRequest(conditionId, new Gson().toJson(postBody), successCallBackListener, errorListener);
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
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null) {
            String message = "No Internet Connection";
            if (networkResponse.statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                message = "Internal Server Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                message = "Unprocessable Entity Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                message = "Page Not Found";
            }
            MdliveUtils.showDialog(MDLiveHealthModule.this, "Error",
                    "Status Code : " + error.networkResponse.statusCode + "\n" +
                            "Server Response : " + message);
        }

    }


}

