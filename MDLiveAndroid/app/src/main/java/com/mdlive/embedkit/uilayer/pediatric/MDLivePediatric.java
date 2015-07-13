package com.mdlive.embedkit.uilayer.pediatric;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveMedicalHistory;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
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

public class MDLivePediatric extends MDLiveBaseActivity {
    private RadioGroup birthComplicationGroup,newBornComplicationGroup,lastShotGroup,prematureGroup,jaundiceGroup
            ,breathingPbmGroup,infectionsGroup,colicGroup,feedingGroup,smokingGroup,childOutGroup,siblingsGroup;
    public EditText edtBirthComplications,edtLastShot,edtCurrentWeight;
    private List<String> dietList;
    private TextView txtDietType;
    private ProgressDialog pDialog;
    //private RelativeLayout progressDialog;

//    private ProgressBar progressBar;
    public ArrayList<HashMap<String,String>> questionList;
    public HashMap<String ,String> questionItem,weightMap;
    public HashMap<String,ArrayList<HashMap<String,String>>> questionsMap;
    public HashMap<String,Object> postParams;
    private TextView lasShotLabel;
    private RelativeLayout birthComplicationLayout;
    private Button saveButton;
    Activity cxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cxt=this;
        initializeUI();
        touchHandlers();


    }
    /***
     * This method will handles on touch listers and hides the keyboard when user touches outside necessary UI
     */
    public void touchHandlers(){
        ScrollView scrollTouch= (ScrollView) findViewById(R.id.pediatricScroll);
        scrollTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MdliveUtils.hideKeyboard(cxt, v);
                return false;
            }
        });
        LinearLayout container= (LinearLayout) findViewById(R.id.pediatricContainer);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MdliveUtils.hideKeyboard(cxt, v);
                return false;
            }
        });

    }

    /***
     * This method will initialize all necessary UI
     */


    public void initializeUI(){
        setContentView(R.layout.mdlive_pediatric_new);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        questionList=new ArrayList<>();
        questionsMap=new HashMap<>();
        postParams=new HashMap<>();
//        pDialog = Utils.getProgressDialog(getResources().getString(R.string.please_wait), this);
        weightMap=new HashMap<>();
        txtDietType= (TextView) findViewById(R.id.txt_dietType);
        TextView txtAge= (TextView) findViewById(R.id.ageTxt);
        saveButton= (Button) findViewById(R.id.btn_continue_pediatric);
        edtCurrentWeight= (EditText) findViewById(R.id.edt_currentweight);
//        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        //progressDialog = (RelativeLayout)findViewById(R.id.progressDialog);
        setProgressBar(findViewById(R.id.progressDialog));

        edtCurrentWeight.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton();//Function to check whether all fileds are filled or not?
                if(s.length() != 0) {
                    weightMap.put("weight",s.toString());
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
                onBackPressed();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });
        radioGroupInitialization();
        edtBirthComplications= (EditText) findViewById(R.id.edt_pleaseDescribe);
        edtLastShot= (EditText) findViewById(R.id.edt_lastshot);
        lasShotLabel= (TextView) findViewById(R.id.txt_lastShot_label);
        birthComplicationLayout= (RelativeLayout) findViewById(R.id.layout_birthComplications);
        if(checkPerdiatricAge()){
            txtAge.setText("Patient under 13 years of age");
            birthComplicationLayout.setVisibility(View.GONE);//Hiding this layout for adult users
            edtBirthComplications.setVisibility(View.GONE);
            txtDietType.setVisibility(View.GONE);
        }else{
            txtAge.setText("Patient under 2 years of age");
            birthComplicationLayout.setVisibility(View.VISIBLE);//view  this layout for adult users
            //edtBirthComplications.setVisibility(View.VISIBLE);
            txtDietType.setVisibility(View.VISIBLE);
        }
        explanationListners();
        dietList=new ArrayList<>();
        dietList = Arrays.asList(getResources().getStringArray(R.array.dietlist));
        buttonClick();
        radioClick();
        getPediatricProfileBelowTwo();
    }

    public boolean checkPerdiatricAge(){
        if(MdliveUtils.calculteAgeFromPrefs(MDLivePediatric.this)>2 && MdliveUtils.calculteAgeFromPrefs(MDLivePediatric.this)<13){
            return true;
        }else if(MdliveUtils.calculteAgeFromPrefs(MDLivePediatric.this) == 2){
            if(MdliveUtils.calculteMonthFromPrefs(MDLivePediatric.this) > 0 && MdliveUtils.daysFromPrefs(MDLivePediatric.this) > 0){
                return true;
            }
        }else if(MdliveUtils.calculteAgeFromPrefs(MDLivePediatric.this) == 13){
            if(MdliveUtils.calculteMonthFromPrefs(MDLivePediatric.this) == 0 && MdliveUtils.daysFromPrefs(MDLivePediatric.this) == 0){
                return true;
            }
        }
        return false;
    }


    /**
     * This function will invokes when user enter some descriptions
     */
    public void explanationListners(){
        edtBirthComplications.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSaveButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              /*  String text=s.toString();
                if(text.length()!=0 && !text.startsWith(" ")){
                    updateExplanationParams("Birth complications explanation",s.toString());
                }else{
                    enableSaveButton();
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if(edtBirthComplications.getText().toString().startsWith(" ")){
                    Toast.makeText(getApplicationContext(),"Positive",Toast.LENGTH_SHORT).show();
                }*/


                enableSaveButton();
                String text = s.toString();
                if (text.length() != 0 && !text.startsWith(" ")) {
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
               /* String text=s.toString();
                if(text.length()!=0 && !text.startsWith(" ")){
                    updateExplanationParams("Last shot",s.toString());
                }else{
                    enableSaveButton();
                }*/
                if (s.length() > 0 && s.subSequence(0, 1).toString().equalsIgnoreCase(" ")) {
                    edtLastShot.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s){
                enableSaveButton();
                String text = s.toString().trim();
                if (text.length() != 0) {
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
    public void radioGroupInitialization(){
        birthComplicationGroup= (RadioGroup) findViewById(R.id.birthComplications_group);
        smokingGroup= (RadioGroup) findViewById(R.id.smoking_group);
        childOutGroup= (RadioGroup) findViewById(R.id.childcare_group);
        siblingsGroup= (RadioGroup) findViewById(R.id.siblings_group);
        lastShotGroup= (RadioGroup) findViewById(R.id.immunization_group);
    }

    /**
     * This function will invoke when user clicks on radio Button
     * Base on user Choice Update params function will be invoked and post params will be updated correspondingly
     */

    public void radioClick(){

        birthComplicationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId== R.id.birthComplications_yesButton){
                    updateParams("Birth complications","Yes");
                    edtBirthComplications.setVisibility(View.VISIBLE);
                    updateExplanationParams("Birth complications explanation",edtBirthComplications.getText().toString());//To update birth Params
                    enableSaveButton();
                }else{
                    updateParams("Birth complications","No");
                    edtBirthComplications.setVisibility(View.GONE);
                    updateExplanationParams("Birth complications explanation","");
                    enableSaveButton();

                }
            }
        });
        lastShotGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId== R.id.immunization_yesButton){
                    updateParams("Immunization up to date?","Yes");
                    lasShotLabel.setVisibility(View.GONE);
                    edtLastShot.setVisibility(View.GONE);
                    updateExplanationParams("Last shot","");//If user clicks no update the post params as empty
                    enableSaveButton();
                }else{
                    updateParams("Immunization up to date?","No");
                    //If user clicks no update the post params.
                    lasShotLabel.setVisibility(View.VISIBLE);
                    edtLastShot.setVisibility(View.VISIBLE);
                    updateExplanationParams("Last shot",edtLastShot.getText().toString().trim());
                    enableSaveButton();
                }
            }
        });

        smokingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableSaveButton();
                if(checkedId== R.id.smoking_yesButton){
                    updateParams("Smoking exposure","Yes");
                }else{
                    updateParams("Smoking exposure","No");
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


    /***
     * This function will be resposible for update post params values based on user inputs
     * @param name---It is a key value to match the key present inside the questionItem map
     * @param value--it is normally the yes or no value.
     */

    public void updateParams(String name,String value){
        if("Yes".equals(value)){
            for(int i=0;i<questionList.size();i++){
                questionItem=questionList.get(i);
                if(questionItem.get("name").equals(name)){
                    questionItem.put("value","Yes");
                    break;
                }
            }
        }else{
            for(int i=0;i<questionList.size();i++){
                questionItem=questionList.get(i);
                if(questionItem.get("name").equals(name)){
                    questionItem.put("value","No");
                    break;
                }
            }
        }



        Gson gs=new Gson();
        Log.e("Post Params", gs.toJson(postParams).toString());


    }


    /**
     * Method hanldes uodating values in dropdown to post params --Diet type
     * @param name---It is a key value to match the key present inside the questionItem map
     * @param value--user selected value from the dropdown.
     */

    public void updateDropDownParams(String name,String value) {
        for (int i = 0; i < questionList.size(); i++) {
            questionItem = questionList.get(i);
            if (questionItem.get("name").equals(name)) {
                questionItem.put("value", value);
                break;
            }
        }
    }
    public void updateExplanationParams(String name,String value) {
        for (int i = 0; i < questionList.size(); i++) {
            questionItem = questionList.get(i);
            if (questionItem.get("name").equals(name)) {
                questionItem.put("value", value);
                break;
            }
        }
    }

    public void buttonClick(){
        txtDietType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(dietList,txtDietType,"Current Diet");
                MdliveUtils.hideSoftKeyboard(MDLivePediatric.this);
                touchHandlers();

            }
        });
    }

    /**
     * This function will retrieve the Pediatric profile information from the server.
     *
     * PediatricService - This service class will make the service calls to get the
     * Pediatric profile.
     */
    private void getPediatricProfileBelowTwo(){
//        Utils.showProgressDialog(pDialog);
        setProgressBarVisibility();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                handleSuccessResponse(response.toString());
                Log.e("Pediatric Profile",response.toString());
                //Utils.hideProgressDialog(pDialog);
                setInfoVisibilty();
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Utils.hideProgressDialog(pDialog);
                setInfoVisibilty();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                Utils.hideProgressDialog(pDialog);
                                setInfoVisibilty();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, MDLivePediatric.this);
                    }
                }
            }
        };
        PediatricService getProfileData=new PediatricService(MDLivePediatric.this,pDialog);
        getProfileData.doGetPediatricBelowTwo(successListener,errorListener);
    }

    private  void handleSuccessResponse(String response){
        try{
            JSONObject resObj=new JSONObject(response);
            JSONObject pediatricObj=resObj.getJSONObject("pediatric");
            if(!pediatricObj.getJSONArray("questions").equals("null")){
                JSONArray questionArray=pediatricObj.getJSONArray("questions");
                for(int i=0;i<questionArray.length();i++){
                    JSONObject questionObj=questionArray.getJSONObject(i);
                    enableRadioButtons(questionObj.getString("name"),questionObj.getString("value"));
                    questionItem=new HashMap<>();
                    questionItem.put("name", questionObj.getString("name"));
                    questionItem.put("value",questionObj.getString("value"));
                    questionList.add(questionItem);
                }
                questionsMap.put("questions",questionList);
                postParams.put("pediatric",questionsMap);
                JSONObject personalInfoObj=resObj.getJSONObject("personal_info");

                weightMap.put("birth_weight","10");
                weightMap.put("weight",personalInfoObj.getString("weight"));

               /* if(!personalInfoObj.getString("weight").equals("null")) {
                    edtCurrentWeight.setText(personalInfoObj.getString("weight"));
                }*/


                postParams.put("personal_info",weightMap);
                enableSaveButton();
                Gson gs=new Gson();
                Log.e("Post Params", gs.toJson(postParams).toString());

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * This method will be responsible for filling all the profile information based on the response
     * @param name --Corresponding field anem
     * @param value---Correspomding vlaue to the field.
     */

    public void enableRadioButtons(String name,String value){
        if("Smoking exposure".equals(name)){
            if("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.smoking_yesButton)).setChecked(true);
            }else if("No".equals(value)){
                ((RadioButton) findViewById(R.id.smoking_noButton)).setChecked(true);
            }
        }else if("Childcare outside home".equals(name)){
            if("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.childcare_yesButton)).setChecked(true);
            }else if("No".equals(value)){
                ((RadioButton) findViewById(R.id.childcare_noButton)).setChecked(true);
            }
        }else if("Siblings".equals(name)){
            if("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.siblings_yesButton)).setChecked(true);
            }else if("No".equals(value)){
                ((RadioButton) findViewById(R.id.siblings_noButton)).setChecked(true);
            }
        }else if("Birth complications".equals(name)){
            if("Yes".equals(value)) {
                ((RadioButton) findViewById(R.id.birthComplications_yesButton)).setChecked(true);
            }else if("No".equals(value)){
                ((RadioButton) findViewById(R.id.birthComplications_noButton)).setChecked(true);
            }
        }else if("Birth complications explanation".equals(name)){
            edtBirthComplications.setText(value);
        }else if("Current Diet".equals(name)) {
            txtDietType.setText(value);
        }
    }

    /**
     * This function will be invoked when user clicks on save button
     * @param v--Button variable
     */


    public void continueBtn(View v){
        callUpdateService();

    }


    /**
     * This method will be responsible for updating  all the profile information based on the user
     * PediatricService - This service class will make the service calls to update the pediatric profile

     */

    public void callUpdateService(){
//        Utils.showProgressDialog(pDialog);
        //progressDialog.setVisibility(View.VISIBLE);
        showProgress();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                // handleSuccessResponse(response.toString());
                Log.e("Pediatric Update ",response.toString());
//                Utils.hideProgressDialog(pDialog);
                //progressDialog.setVisibility(View.GONE);
                hideProgress();
                Intent intent = new Intent(MDLivePediatric.this, MDLiveMedicalHistory.class);
                startActivity(intent);
                MdliveUtils.startActivityAnimation(MDLivePediatric.this);
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                Utils.hideProgressDialog(pDialog);
                                //progressDialog.setVisibility(View.GONE);

                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, MDLivePediatric.this);
                    }
                }
            }
        };
        PediatricService getProfileData=new PediatricService(MDLivePediatric.this,pDialog);
        Gson gs=new Gson();
        Log.e("Final Post Params", gs.toJson(postParams).toString());
        getProfileData.doPostPediatricBelowTwo(gs.toJson(postParams),successListener,errorListener);
    }


    private void showListViewDialog(final List<String> list, final TextView selectedText, final String typeName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLivePediatric.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = list.get(position);
                selectedText.setText(selectedType);
                updateDropDownParams(typeName, selectedType);
                enableSaveButton();
                dialog.dismiss();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void enableSaveButton(){
        if(isFieldsNotEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                saveButton.setBackground(getResources().getDrawable(R.drawable.btn_rounded_bg));
            } else {
                saveButton.setBackgroundResource(R.drawable.btn_rounded_bg);

            }
            saveButton.setClickable(true);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                saveButton.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
            } else {
                saveButton.setBackgroundResource(R.drawable.btn_rounded_grey);

            }
            saveButton.setClickable(false);
        }
    }



    public boolean isFieldsNotEmpty(){
        if(edtCurrentWeight.getText().toString().length()==0||edtCurrentWeight.getText().toString().startsWith(" ")){
            return false;
        }
        if(txtDietType.getVisibility()==View.VISIBLE){
            Log.e("Diet","Coming");
            if(txtDietType.getHint().toString().equals("Current Diet") && txtDietType.getText().toString().isEmpty()){
                return false;
            }
        }
        if(birthComplicationLayout.getVisibility()==View.VISIBLE){
            Log.e("birthComplicationGroup","Coming");
            if(birthComplicationGroup.getCheckedRadioButtonId()<0){
                return false;
            }
        }
        if(lastShotGroup.getCheckedRadioButtonId()<0){
            return false;
        }
        if(smokingGroup.getCheckedRadioButtonId()<0){
            return false;
        }
        if(childOutGroup.getCheckedRadioButtonId()<0){
            return false;
        }
        if(siblingsGroup.getCheckedRadioButtonId()<0){
            return false;
        }
        if(edtBirthComplications.getVisibility()==View.VISIBLE){
            Log.e("birthComplication","Coming");
            if(edtBirthComplications.getText().toString().length()==0||edtBirthComplications.getText().toString().startsWith(" ")){
                return false;
            }
        }
        if(lasShotLabel.getVisibility()==View.VISIBLE){
            Log.e("lasShotLabel","Coming");
            if(edtLastShot.getText().toString().trim().length()==0){
                return false;
            }

        }
        return true;
    }



    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        MdliveUtils.movetohome(MDLivePediatric.this, getString(R.string.home_dialog_title), getString(R.string.home_dialog_text));
    }

    /*
       * set visible for the progress bar
       */
    public void setProgressBarVisibility()
    {
        //progressDialog.setVisibility(View.VISIBLE);
        showProgress();
        saveButton.setVisibility(View.GONE);
    }

    /*
    * set visible for the details view layout
    */
    public void setInfoVisibilty()
    {
        hideProgress();
        saveButton.setVisibility(View.VISIBLE);
    }



    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }


}
