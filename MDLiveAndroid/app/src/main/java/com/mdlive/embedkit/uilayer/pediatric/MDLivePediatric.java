package com.mdlive.embedkit.uilayer.pediatric;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MedicalHistoryPluginActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveMedicalHistory;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCoordinates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.GoogleFitUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.PediatricService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MDLivePediatric extends MedicalHistoryPluginActivity {
    private RadioGroup birthComplicationGroup, lastShotGroup, smokingGroup, childOutGroup, siblingsGroup;
    private EditText edtBirthComplications, edtLastShot, edtCurrentWeight;
    private List<String> dietList;
    private TextView txtDietType, txtDietTypeHeader;
    public ArrayList<HashMap<String, String>> questionList;
    public HashMap<String, String> questionItem, weightMap;
    public HashMap<String, ArrayList<HashMap<String, String>>> questionsMap;
    public HashMap<String, Object> postParams;
    private TextView lasShotLabel;
    private RelativeLayout birthComplicationLayout;
    CardView dietLayout;

    Activity cxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
        cxt = this;
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_pediatric_header_text));
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
        touchHandlers();

    }

    /**
     * This method will handles on touch listers and hides the keyboard when user touches outside necessary UI
     */
    public void touchHandlers() {
        ScrollView scrollTouch = (ScrollView) findViewById(R.id.pediatricScroll);
        scrollTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MdliveUtils.hideKeyboard(cxt, v);
                return false;
            }
        });
        LinearLayout container = (LinearLayout) findViewById(R.id.pediatricContainer);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MdliveUtils.hideKeyboard(cxt, v);
                return false;
            }
        });

    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLivePediatric.this);
        onBackPressed();
    }

    public void rightBtnOnClick(View v){
        callUpdateService();
    }

    /**
     * This method will initialize all necessary UI
     */
    public void initializeUI() {
        setContentView(R.layout.mdlive_pediatric);

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

        locationService = new LocationCoordinates(getApplicationContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());

        dietLayout = (CardView) findViewById(R.id.diet_layout);

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
        findViewById(R.id.txtApply).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        findViewById(R.id.txtApply).setContentDescription(getString(R.string.mdl_ada_tick_button));
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_pediatric_header_text));

        // SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
//        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME, ""));
        questionList = new ArrayList<>();
        questionsMap = new HashMap<>();
        postParams = new HashMap<>();
        weightMap = new HashMap<>();
        txtDietType = (TextView) findViewById(R.id.txt_dietType);
        txtDietTypeHeader = (TextView) findViewById(R.id.txt_dietTypeHeader);
        TextView txtAge = (TextView) findViewById(R.id.ageTxt);
        edtCurrentWeight = (EditText) findViewById(R.id.edt_currentweight);

        setProgressBar(findViewById(R.id.progressDialog));

        edtCurrentWeight.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != IntegerConstants.NUMBER_ZERO) {
                    weightMap.put("weight", s.toString());
                    enableSaveButton();//Function to check whether all fileds are filled or not?
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton();//Function to check whether all fileds are filled or not?
                if (s.length() != IntegerConstants.NUMBER_ZERO) {
                    weightMap.put("weight", s.toString());
                }
            }
        });

        radioGroupInitialization();
        edtBirthComplications = (EditText) findViewById(R.id.edt_pleaseDescribe);
        edtLastShot = (EditText) findViewById(R.id.edt_lastshot);
        lasShotLabel = (TextView) findViewById(R.id.txt_lastShot_label);
        birthComplicationLayout = (RelativeLayout) findViewById(R.id.layout_birthComplications);
        View birthCompView = findViewById(R.id.birth_complication_view);
        if (checkPediatricAge()) {
            txtAge.setText(getString(R.string.mdl_AgeUnder13));
            birthComplicationLayout.setVisibility(View.GONE);//Hiding this layout for adult users
            birthCompView.setVisibility(View.GONE);
            edtBirthComplications.setVisibility(View.GONE);
            dietLayout.setVisibility(View.GONE);
            txtDietType.setVisibility(View.GONE);
            txtDietTypeHeader.setVisibility(View.GONE);
        } else {
            txtAge.setText(getString(R.string.mdl_AgeUnder2));
            birthComplicationLayout.setVisibility(View.VISIBLE);//view  this layout for adult users
            birthCompView.setVisibility(View.VISIBLE);
            dietLayout.setVisibility(View.VISIBLE);
            txtDietType.setVisibility(View.VISIBLE);
            txtDietTypeHeader.setVisibility(View.VISIBLE);
        }

        explanationListeners();
        dietList = new ArrayList<>();
        dietList = Arrays.asList(getResources().getStringArray(R.array.mdl_dietlist));
        buttonClick();
        radioClick();
        getPediatricProfileBelowTwo();
    }

    /***
     * This method is used to check the user age based on which label will be displayed
     * @return true when the necessary conditions met else returns false
     */
    public boolean checkPediatricAge() {
        if (TimeZoneUtils.calculteAgeFromPrefs(MDLivePediatric.this) > IntegerConstants.PEDIATRIC_AGE_TWO && TimeZoneUtils.calculteAgeFromPrefs(MDLivePediatric.this) < IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
            return true;
        } else if (TimeZoneUtils.calculteAgeFromPrefs(MDLivePediatric.this) == IntegerConstants.PEDIATRIC_AGE_TWO) {
            if (TimeZoneUtils.calculteMonthFromPrefs(MDLivePediatric.this) > IntegerConstants.PEDIATRIC_AGE_ZERO && TimeZoneUtils.daysFromPrefs(MDLivePediatric.this) > IntegerConstants.PEDIATRIC_AGE_ZERO) {
                return true;
            }
        } else if (TimeZoneUtils.calculteAgeFromPrefs(MDLivePediatric.this) == IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
            if (TimeZoneUtils.calculteMonthFromPrefs(MDLivePediatric.this) == IntegerConstants.PEDIATRIC_AGE_ZERO && TimeZoneUtils.daysFromPrefs(MDLivePediatric.this) == IntegerConstants.PEDIATRIC_AGE_ZERO) {
                return true;
            }
        }
        return false;
    }


    /**
     * This function will invokes when user enter some descriptions in Birth complications field
     */
    public void explanationListeners() {
        edtBirthComplications.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSaveButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton();
                String text = s.toString();
                if (text.length() != IntegerConstants.NUMBER_ZERO && !text.startsWith(" ")) {
                    updateExplanationParams("Birth complications explanation", s.toString());
                } else {
                    enableSaveButton();
                }

            }
        });
        edtLastShot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSaveButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > IntegerConstants.NUMBER_ZERO && s.subSequence(IntegerConstants.NUMBER_ZERO, 1).toString().equalsIgnoreCase(" ")) {
                    edtLastShot.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton();
                String text = s.toString().trim();
                if (text.length() != IntegerConstants.NUMBER_ZERO) {
                    updateExplanationParams("Last shot", text);
                } else {
                    enableSaveButton();
                }
            }
        });
    }

    /**
     * This method deals with radio group initialization
     */
    public void radioGroupInitialization() {
        birthComplicationGroup = (RadioGroup) findViewById(R.id.birthComplications_group);
        smokingGroup = (RadioGroup) findViewById(R.id.smoking_group);
        childOutGroup = (RadioGroup) findViewById(R.id.childcare_group);
        siblingsGroup = (RadioGroup) findViewById(R.id.siblings_group);
        lastShotGroup = (RadioGroup) findViewById(R.id.immunization_group);
    }

    /**
     * This function will invoke when user clicks on radio Button
     * Base on user Choice Update params function will be invoked and post params will be updated correspondingly
     */
    public void radioClick() {

        birthComplicationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MdliveUtils.hideSoftKeyboard(MDLivePediatric.this);
                if (checkedId == R.id.birthComplications_yesButton) {
                    updateBirthComplication("Birth complications", "Yes");
                    edtBirthComplications.setVisibility(View.VISIBLE);
                    //updateBirthComplication("Birth complications explanation", edtBirthComplications.getText().toString());//To update birth Params
                    enableSaveButton();
                } else {
                    updateBirthComplication("Birth complications", "No");
                    edtBirthComplications.setVisibility(View.GONE);
                    //updateBirthComplication("Birth complications explanation", "");
                    enableSaveButton();

                }
            }
        });

        lastShotGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.immunization_yesButton) {
                    updateParams("Immunization up to date?", "Yes");
                    lasShotLabel.setVisibility(View.GONE);
                    edtLastShot.setVisibility(View.GONE);

                    updateExplanationParams("Last shot", "");//If user clicks no update the post params as empty
                    enableSaveButton();
                } else {
                    updateParams("Immunization up to date?", "No");
                    //If user clicks no update the post params.
                    lasShotLabel.setVisibility(View.VISIBLE);
                    edtLastShot.setVisibility(View.VISIBLE);
                    updateExplanationParams("Last shot", edtLastShot.getText().toString().trim());
                    enableSaveButton();
                }
            }
        });

        smokingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableSaveButton();
                if (checkedId == R.id.smoking_yesButton) {
                    updateParams("Smoking exposure", "Yes");
                } else {
                    updateParams("Smoking exposure", "No");
                }
            }
        });
        childOutGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableSaveButton();
                if (checkedId == R.id.childcare_yesButton) {
                    updateParams("Childcare outside home", "Yes");
                } else {
                    updateParams("Childcare outside home", "No");
                }
            }
        });
        siblingsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableSaveButton();
                if (checkedId == R.id.siblings_yesButton) {
                    updateParams("Siblings", "Yes");
                } else {
                    updateParams("Siblings", "No");
                }
            }
        });

    }


    /**
     * Update post params values based on user inputs
     *
     * @param name---It is a key value to match the key present inside the questionItem map
     * @param value--it is normally the yes or no value.
     */
    public void updateParams(String name, String value)
    {
        if ("Yes".equals(value)) {
            for (int i = IntegerConstants.NUMBER_ZERO; i < questionList.size(); i++) {
                questionItem = questionList.get(i);
                if (questionItem.get("name").equals(name)) {
                    questionItem.put("value", "Yes");
                    break;
                }
            }
        } else {
            for (int i = IntegerConstants.NUMBER_ZERO; i < questionList.size(); i++) {
                questionItem = questionList.get(i);
                if (questionItem.get("name").equals(name)) {
                    questionItem.put("value", "No");
                    break;
                }
            }
        }
        //Gson gs = new Gson();
    }

    public void updateBirthComplication(String name, String value)
    {
        boolean isPresent = false;
        for (int i = IntegerConstants.NUMBER_ZERO; i < questionList.size(); i++) {
            questionItem = questionList.get(i);
            if (questionItem.get("name").equals(name)) {
                isPresent = true;
                break;
            }
        }
        if(isPresent)
        {
            updateParams(name,value);
        }
        else
        {
            HashMap<String, String> birthField =  new HashMap<>();
            birthField.put("name", name);
            birthField.put("value", value);
            questionList.add(birthField);
        }

    }

    /**
     * Method hanldes uodating values in dropdown to post params --Diet type
     *
     * @param name      a key value to match the key present inside the questionItem map
     * @param value     user selected value from the dropdown.
     */
    public void updateDropDownParams(String name, String value)
    {
        boolean isPresent = false;
        for (int i = IntegerConstants.NUMBER_ZERO; i < questionList.size(); i++) {
            questionItem = questionList.get(i);
            if (questionItem.get("name").equals(name)) {
                questionItem.put("value", value);
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            HashMap<String, String> dietField =  new HashMap<>();
            dietField.put("name", "Current Diet");
            dietField.put("value", value);
            questionList.add(dietField);
        }
    }


    /**
     *
     * @param name      Key values to get the value from Map which is stored in Question list
     * @param value     User entered valued to be updated in post params.
     */
    public void updateExplanationParams(String name, String value) {
        for (int i = IntegerConstants.NUMBER_ZERO; i < questionList.size(); i++) {
            questionItem = questionList.get(i);
            if (questionItem.get("name").equals(name)) {
                questionItem.put("value", value);
                break;
            }
        }
    }


    public void buttonClick() {
        txtDietType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(dietList, txtDietType, "Current Diet");
                MdliveUtils.hideSoftKeyboard(MDLivePediatric.this);
                touchHandlers();

            }
        });
    }

    /**
     * This function will retrieve the Pediatric profile information from the server.
     * <p/>
     * PediatricService - This service class will make the service calls to get the
     * Pediatric profile.
     */
    private void getPediatricProfileBelowTwo() {
        setProgressBarVisibility();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                handleSuccessResponse(response.toString());
                Log.d("Pediatric Profile", response.toString());
                setInfoVisibilty();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setInfoVisibilty();
                MdliveUtils.handleVolleyErrorResponse(MDLivePediatric.this, error, getProgressDialog());
            }
        };
        PediatricService getProfileData = new PediatricService(MDLivePediatric.this, null);
        getProfileData.doGetPediatricBelowTwo(successListener, errorListener);
    }

    private void handleSuccessResponse(String response) {
        try {
            JSONObject resObj = new JSONObject(response);
            if (resObj != null) {
                JSONObject pediatricObj = resObj.getJSONObject("pediatric");
                JSONArray questionArray = pediatricObj.getJSONArray("questions");
                if (questionArray != null && questionArray.length() != 0) {
                    for (int i = IntegerConstants.NUMBER_ZERO; i < questionArray.length(); i++) {
                        JSONObject questionObj = questionArray.getJSONObject(i);
                        enableRadioButtons(questionObj.getString("name"), questionObj.getString("value"));//This method will enable radio buttons based on values
                        questionItem = new HashMap<>();
                        questionItem.put("name", questionObj.getString("name"));
                        questionItem.put("value", questionObj.getString("value"));
                        questionList.add(questionItem);

                        if ("Last shot".equalsIgnoreCase(questionObj.getString("name"))) {
                            edtLastShot.setText(questionObj.getString("value"));
                        }
                        if ("Birth complications explanation".equalsIgnoreCase(questionObj.getString("name"))) {
                            edtBirthComplications.setText(questionObj.getString("value"));
                        }
                    }
                }
                questionsMap.put("questions", questionList);
                postParams.put("pediatric", questionsMap);
                JSONObject personalInfoObj = resObj.getJSONObject("personal_info");

                weightMap.put("birth_weight", "10");
                weightMap.put("weight", personalInfoObj.getString("weight"));

                postParams.put("personal_info", weightMap);
                String weight = personalInfoObj.getString("weight");
                boolean fromMedicalHistory = getIntent().getBooleanExtra("FROM_MEDICAL_HISTORY", false);
                if (weight != null && !weight.trim().isEmpty() && !weight.trim().equals("0") && isInteger(weight) && fromMedicalHistory) {
                    ((EditText) findViewById(R.id.edt_currentweight)).setText(weight);
                }

                enableSaveButton();
            }
           // Gson gs = new Gson();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will be responsible for filling all the profile information based on the response
     *
     * @param name      Corresponding field name
     * @param value     Corresponding value to the field.
     */
    public void enableRadioButtons(String name, String value) {

        boolean fromMedicalHistory = getIntent().getBooleanExtra("FROM_MEDICAL_HISTORY", false);

        if(fromMedicalHistory){
            if ("Immunization up to date?".equals(name)) {
                if ("Yes".equals(value)) {
                    ((RadioButton) findViewById(R.id.immunization_yesButton)).setChecked(true);
                } else if ("No".equals(value)) {
                    ((RadioButton) findViewById(R.id.immunization_noButton)).setChecked(true);
                }
            }
        }
        if ("Smoking exposure".equals(name)) {
            if ("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.smoking_yesButton)).setChecked(true);
            } else if ("No".equals(value)) {
                ((RadioButton) findViewById(R.id.smoking_noButton)).setChecked(true);
            }
        } else if ("Childcare outside home".equals(name)) {
            if ("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.childcare_yesButton)).setChecked(true);
            } else if ("No".equals(value)) {
                ((RadioButton) findViewById(R.id.childcare_noButton)).setChecked(true);
            }
        } else if ("Siblings".equals(name)) {
            if ("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.siblings_yesButton)).setChecked(true);
            } else if ("No".equals(value)) {
                ((RadioButton) findViewById(R.id.siblings_noButton)).setChecked(true);
            }
        } else if ("Birth complications".equals(name)) {
            if ("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.birthComplications_yesButton)).setChecked(true);
            } else if ("No".equals(value)) {
                ((RadioButton) findViewById(R.id.birthComplications_noButton)).setChecked(true);
            }
        } else if ("Birth complications explanation".equals(name)) {
            edtBirthComplications.setText(value);
        } else if ("Current Diet".equalsIgnoreCase(name)) {
            if (value != null && !value.isEmpty()) {
                txtDietType.setText(value);
            } else {
                txtDietType.setText(StringConstants.DIET_TYPE);
            }
        }
    }

    /**
     * This function will be invoked when user clicks on save button in the UI
     *
     * @param v     Button variable
     */
    public void continueBtn(View v) {
        callUpdateService();
    }

    /**
     * This method will be responsible for updating  all the profile information based on the user inputs
     * PediatricService - This service class will make the service calls to update the pediatric profile
     */
    public void callUpdateService() {
        showProgress();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                if(getIntent() != null && getIntent().hasExtra("firstTimeUser")){
                    if(getIntent().hasExtra("theraphyFlow")){
                        checkMedicalAggregation();
                    } else {
                        Intent medicalIntent = new Intent(MDLivePediatric.this, MDLiveMedicalHistory.class);
                        startActivity(medicalIntent);
                        MdliveUtils.startActivityAnimation(MDLivePediatric.this);
                    }
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MdliveUtils.handleVolleyErrorResponse(MDLivePediatric.this, error, getProgressDialog());
            }
        };

        SharedPreferences sharedPref = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        String dependentId = sharedPref.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        EditText weightEt = (EditText) findViewById(R.id.edt_currentweight);
        if(dependentId == null && userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, false)){
            if((weightEt.getText().toString().equals("0"))){
                GoogleFitUtils.getInstance().buildFitnessClient(false, new String[]{"0", Integer.parseInt(weightEt.getText().toString()) + ""}, this);
            }
        }
        PediatricService getProfileData = new PediatricService(MDLivePediatric.this, null);
        getProfileData.doPostPediatricBelowTwo(new Gson().toJson(postParams), successListener, errorListener);
    }


    /**
     * This method will display diet type as drop down.
     * @param list          contains list values which has to be displayed in dropdown,
     * @param selectedText  View will be updated based on user selection from the dropdown list.
     * @param typeName      Diet type name
     */
    private void showListViewDialog(final List<String> list, final TextView selectedText, final String typeName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLivePediatric.this);
        alertDialog.setItems(getResources().getStringArray(R.array.mdl_dietlist), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedType = list.get(which);
                selectedText.setText(selectedType);
                selectedText.setContentDescription(getString(R.string.mdl_ada_dropdown) + selectedType);
                updateDropDownParams(typeName, selectedType);
                enableSaveButton();
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alertDialog.create();
        dialog.show();
    }


    /**
     * This method wiil Highlight the save button based on user input,
     * If user failes to fill all the values the button will be greyed out and viceversa.
     */
    public void enableSaveButton() {
        if (isFieldsNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                findViewById(R.id.txtApply).setVisibility(View.GONE);
            } else {
                findViewById(R.id.txtApply).setVisibility(View.GONE);
            }
        }
    }

    public boolean isFieldsNotEmpty() {
        String check = edtCurrentWeight.getText().toString().trim();
        if (TextUtils.isEmpty(check) || Float.parseFloat(check) <= 0f) {
            return false;
        }
        if (txtDietType.getVisibility() == View.VISIBLE) {
            if (txtDietType.getHint().toString().equals(StringConstants.DIET_TYPE) && txtDietType.getText().toString().isEmpty()) {
                return false;
            }
        }
        if (birthComplicationLayout.getVisibility() == View.VISIBLE) {
            if (birthComplicationGroup.getCheckedRadioButtonId() < IntegerConstants.NUMBER_ZERO) {
                return false;
            }
        }
        if (lastShotGroup.getCheckedRadioButtonId() < IntegerConstants.NUMBER_ZERO) {
            return false;
        }
        if (smokingGroup.getCheckedRadioButtonId() < IntegerConstants.NUMBER_ZERO) {
            return false;
        }
        if (childOutGroup.getCheckedRadioButtonId() < IntegerConstants.NUMBER_ZERO) {
            return false;
        }
        if (siblingsGroup.getCheckedRadioButtonId() < IntegerConstants.NUMBER_ZERO) {
            return false;
        }
        if (edtBirthComplications.getVisibility() == View.VISIBLE) {
            if (edtBirthComplications.getText().toString().length() == IntegerConstants.NUMBER_ZERO || edtBirthComplications.getText().toString().startsWith(" ")) {
                return false;
            }
        }
        if (lasShotLabel.getVisibility() == View.VISIBLE) {
            if (edtLastShot.getText().toString().trim().length() == IntegerConstants.NUMBER_ZERO) {
                return false;
            }

        }
        return true;
    }

    /**
     * Display the progress bar
     */
    public void setProgressBarVisibility() {
        showProgress();
        findViewById(R.id.txtApply).setVisibility(View.GONE);
    }

    /**
     * Hide the progress bar
     */
    public void setInfoVisibilty() {
        hideProgress();
    }

    /**
     * This method will close the activity with a transition effect.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
