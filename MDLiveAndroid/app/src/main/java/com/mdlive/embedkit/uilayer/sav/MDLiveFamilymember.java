package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.AddChildServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.MONTH;


/**
 * Created by sudha_s on 6/1/2015.
 */
public class MDLiveFamilymember extends Activity {
    private ProgressDialog pDialog;
    private SwitchCompat mySwitch;
    private Button addChildBtn;
    private EditText patientNameEt;
    private TextView genderTxt,dateTxt;
    private int month,day,year;
    private String getEditValue,strGender,strDate;
    private LinearLayout genderll,dobLl;
    private DatePickerDialog datePickerDialog;
    private  boolean isAllFieldsfilled = true;
    private ArrayList<String> GenderList = new ArrayList<String>();
    private  HashMap<String,HashMap<String,String>>  array = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_familymember);
        pDialog = Utils.getProgressDialog("Please wait...",this);
        patientNameEt= (EditText) findViewById(R.id.patientEt);
        genderTxt= (TextView) findViewById(R.id.genderTxt);
        dateTxt = (TextView) findViewById(R.id.dobTxt);
        genderll = (LinearLayout) findViewById(R.id.genderLl);
        dobLl = (LinearLayout) findViewById(R.id.dobLl);
        addChildBtn = (Button)findViewById(R.id.addChildBtn);
        getDateOfBirth();
        GetCurrentDate((TextView) findViewById(R.id.dobTxt));
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveFamilymember.this);
                finish();
            }
        });
//        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                movetohome();
//            }
//        });

        patientNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ValidateModuleFields();
            }
        });

        //Add child btn
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChildBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getEditValue = patientNameEt.getText().toString();
                        if (Utils.isValidName(getEditValue)) {
                            HashMap<String, HashMap<String, String>> map = new HashMap<>();
                            HashMap params = new HashMap();
                            params.put("computer", "Mac");
                            HashMap params1 = new HashMap();
                            params1.put("username", getEditValue);
                            params1.put("first_name", "stage");
                            params1.put("last_name", "divi");
                            params1.put("gender", strGender);
                            params1.put("date", strDate);
                            params1.put("email", "raja.rathinavel@photoninfotech.net");
                            params1.put("phone", "12345678902");
                            array.put("camera", params);
                            array.put("member", params1);
                            PostLifeStyleServices();
                        } else {
                            Utils.showDialog(MDLiveFamilymember.this,getResources().getString(R.string.app_name),getResources().getString(R.string.invalid_name));
                        }
                    }
                });

//                PostLifeStyleServices();
//                finish();

            }
        });



        //Gender
        genderll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenderList.size() == 0){
                    GenderList.add("Male");
                    GenderList.add("Female");
                }
                showListViewDialog(GenderList,(TextView)findViewById(R.id.genderTxt));
            }
        });

        //Date of Birth
        dobLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        mySwitch = (SwitchCompat) findViewById(R.id.mySwitch);
        //set the switch to ON
        mySwitch.setChecked(false);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        Log.e("Patient Name",sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
//        mySwitch.setText("I,"+sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,"") +" confirm that i'm the legal parent / guardian\nof the minor above.");
        mySwitch.setText("I, "+sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,"")+", certify that i'm the legal parent / guardian of the minor above.");
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    isAllFieldsfilled = true;
                }else{
                    isAllFieldsfilled = false;
                }
                ValidateModuleFields();
            }
        });

//        //check the current state before we display the screen
        if(mySwitch.isChecked()) {
            isAllFieldsfilled = true;
        }
        else {
            isAllFieldsfilled = false;
        }

        ValidateModuleFields();
    }
    /**
     * Applying validation on form and enable/disable continue button for further steps over.
     */

    public void ValidateModuleFields() {
        isAllFieldsfilled = true;

        if(TextUtils.isEmpty(patientNameEt.getText().toString())){
            isAllFieldsfilled = false;
        }
//        if(TextUtils.isEmpty(dateTxt.getText())){
//            isAllFieldsfilled = false;
//        }
        if(TextUtils.isEmpty(genderTxt.getText().toString())){
            isAllFieldsfilled = false;
        }
        if(!mySwitch.isChecked())
        {
            isAllFieldsfilled = false;
        }

        if(isAllFieldsfilled){
            addChildBtn.setBackgroundColor(getResources().getColor(R.color.green));
            addChildBtn.setClickable(true);
        }else{
            addChildBtn.setBackgroundColor(getResources().getColor(R.color.grey_txt));
            addChildBtn.setClickable(false);
        }
    }
    /**
     * Post Pediatric Profile Services.
     * Class : PostPediatricServices - Service class used to updated the pediatric Profile to the Services
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void PostLifeStyleServices() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Respone AddChild", response.toString());
                handlePostPediatricResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response", error.toString());
                pDialog.dismiss();
                try {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            };
                            // Show timeout error message
                            Utils.connectionTimeoutError(pDialog, MDLiveFamilymember.this);
                        }
                    }else
                    {
                        Utils.alert(pDialog,MDLiveFamilymember.this,error.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        AddChildServices addChildServices = new AddChildServices(MDLiveFamilymember.this, null);
        addChildServices.getChildDependentsr(array, responseListener, errorListener);
    }


    /**
     * Successful Response Handler for getting Current Location
     */

    private void handlePostPediatricResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            Log.e("MDlivePediatric->",responObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetching the values from the native date picker and the picker listener was implemented
     * for the particular native date picker.
     */
    private void getDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }
    /**
     * The Current date and time will be retrieved by using this method.
     * @param selectedText - the corresponding Textview will be passed as an parameter so the
     * date will be set in the corresponding view.
     *
     */
    public void GetCurrentDate(TextView selectedText)
    {
        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date
//        selectedText.setText(new StringBuilder()
//                // Month is 0 based, just add 1
//                .append(month + 1).append("/").append(day).append("/")
//                .append(year).append(" "));
    }


    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, selectedDay);
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            Calendar currendDate = Calendar.getInstance();
            currendDate.setTime(new Date());
            strDate = (new StringBuilder().append(month+1).append("/").append(day)
                    .append("/").append(year)
                    .append(" ")+"");
            dateTxt.setText(strDate);
            ValidateModuleFields();
        }
    };

    /**
     * Instantiating array adapter to populate the listView
     * The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     * @param list : Dependent users array list
     *
     */
    private void showListViewDialog (final ArrayList<String> list,final TextView selectedTxt) {

      /*We need to get the instance of the LayoutInflater*/
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveFamilymember.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,GenderList);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!(list == null)) {
                    String selectedText = list.get(position);
                    selectedTxt.setText(selectedText);
                    strGender = selectedText;
                    dialog.dismiss();
                }
                ValidateModuleFields();
            }
        });
    }
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLiveFamilymember.this, MDLiveLogin.class);
    }
    }

