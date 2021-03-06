package com.mdlive.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.myaccounts.CustomDatePickerDialog;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.AddChildServices;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.MONTH;


/**
 * This class handles the Add FamilyMember related functionalities implementation.
 */
public class MDLiveFamilymember extends MDLiveBaseActivity {
    private SwitchCompat mySwitch;
    private Button addChildBtn;
    private EditText firstNameEditText, lastNameEditText;
    private TextView genderTxt,dateTxt;
    private int month,day,year;
    private String strGender,strDate;
    private CustomDatePickerDialog datePickerDialog;
    private  boolean isAllFieldsfilled = true;
    private ArrayList<String> GenderList = new ArrayList<String>();
    private  HashMap<String,HashMap<String,String>>  array = new HashMap<>();

    private String userInfoJSONString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_familymember);
        clearMinimizedTime();

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("user_info") != null) {
            userInfoJSONString = getIntent().getExtras().getString("user_info");
        }

        firstNameEditText = (EditText) findViewById(R.id.patientEt);
        lastNameEditText = (EditText) findViewById(R.id.lastNamePatientEt);
        genderTxt= (TextView) findViewById(R.id.genderTxt);
        dateTxt = (TextView) findViewById(R.id.dobTxt);
        LinearLayout genderll = (LinearLayout) findViewById(R.id.genderLl);
        LinearLayout dobLl = (LinearLayout) findViewById(R.id.dobLl);
        addChildBtn = (Button)findViewById(R.id.addChildBtn);
        setProgressBar(findViewById(R.id.progressDialog));
        getDateOfBirth();
        GetCurrentDate((TextView) findViewById(R.id.dobTxt));

        firstNameEditText.addTextChangedListener(new TextWatcher() {
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

        lastNameEditText.addTextChangedListener(new TextWatcher() {
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

        mySwitch = (SwitchCompat) findViewById(R.id.mySwitch);
        //set the switch to OFF
        mySwitch.setChecked(false);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);

        mySwitch.setText(getString(R.string.mdl_legalparent_guardian_formated, sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,"")));

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isAllFieldsfilled = isChecked;
                ValidateModuleFields();
            }
        });

        //check the current state before we display the screen
        isAllFieldsfilled = mySwitch.isChecked();

        ValidateModuleFields();
    }

    /*
     * Event for Add Child button click
     */
    public void onAddChildButtonClicked(View view) {
        String firstNameEditTextValue = firstNameEditText.getText().toString().trim();
        String lastNameEditTextValue = lastNameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(firstNameEditTextValue) && !TextUtils.isEmpty(lastNameEditTextValue)
                && !TextUtils.isEmpty(strDate) && !TextUtils.isEmpty(strGender)) {
            if(checkPerdiatricAge()){
                HashMap<String, HashMap<String, String>> map = new HashMap<>();
                HashMap params = new HashMap();
                params.put("computer", "Mac");
                array.put("camera", params);

                JSONObject userObject = null;

                try {

                    userObject = new JSONObject(userInfoJSONString);
                } catch (Exception e) {
                    return;
                }


                HashMap params1 = new HashMap();
                //params1.put("username", firstNameEditTextValue);
                params1.put("first_name", firstNameEditTextValue);
                params1.put("middle_name", "");
                params1.put("last_name", lastNameEditTextValue);
                params1.put("gender", strGender);
                params1.put("email", userObject.optString("email"));
                params1.put("phone", userObject.optString("phone"));
                params1.put("address1", userObject.optString("address1"));
                params1.put("address2", userObject.optString("address2"));
                params1.put("work_phone", "");
                params1.put("fax", "");
                params1.put("city", userObject.optString("city"));
                params1.put("state_id", userObject.optString("state"));
                params1.put("zip", userObject.optString("zipcode"));
                params1.put("birthdate", strDate);
                params1.put("answer", "");

                array.put("member", params1);

                PostDependentServices();
            }else{
                MdliveUtils.showDialog(MDLiveFamilymember.this, getResources().getString(R.string.mdl_app_name), getResources().getString(R.string.mdl_age_error_message));
            }

        } else {
            MdliveUtils.showDialog(MDLiveFamilymember.this, getResources().getString(R.string.mdl_app_name), getResources().getString(R.string.mdl_please_enter_mandatory_fields));
        }
    }

    /*
     * Event for on screen back button click
     */
    public void onBackClicked(View view) {
        MdliveUtils.hideSoftKeyboard(MDLiveFamilymember.this);
        onBackPressed();
    }

    /*
     * Event for gender click
     */
    public void onGenderClicked(View view) {
        if(GenderList.size() == IntegerConstants.NUMBER_ZERO){
            GenderList.add("Male");
            GenderList.add("Female");
        }
        showListViewDialog(GenderList, (TextView) findViewById(R.id.genderTxt));
    }

    /*
     * Event for Date of Birth click
     */
    public void onDateOfBirthClicked(View view) {
        datePickerDialog.show();
    }

    public boolean checkPerdiatricAge(){
        return calculteAgeFromPrefs(strDate, this) < IntegerConstants.ADD_CHILD_AGELIMIT;
    }

    public static int calculteAgeFromPrefs(String strDate, Context context){
        try {
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            format.setTimeZone(TimeZoneUtils.getOffsetTimezone(context));
            Date date = format.parse(strDate);
            return MdliveUtils.calculateAge(date,context);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Applying validation on form and enable/disable continue button for further steps over.
     */
    public void ValidateModuleFields() {

        isAllFieldsfilled = !TextUtils.isEmpty(firstNameEditText.getText().toString());
        if(TextUtils.isEmpty(lastNameEditText.getText().toString())){
            isAllFieldsfilled = false;
        }

        if(TextUtils.isEmpty(genderTxt.getText().toString())){
            isAllFieldsfilled = false;
        }
        if(!mySwitch.isChecked())
        {
            isAllFieldsfilled = false;
        }

        if(isAllFieldsfilled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                addChildBtn.setBackground(getResources().getDrawable(R.drawable.btn_rounded_bg));
            } else {
                addChildBtn.setBackgroundResource(R.drawable.btn_rounded_bg);
            }
            addChildBtn.setClickable(true);
        }else{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                addChildBtn.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
            } else {
                addChildBtn.setBackgroundResource(R.drawable.btn_rounded_grey);
            }
            addChildBtn.setClickable(false);
        }
    }
    /**
     * Post Pediatric Profile Services.
     * Class : PostPediatricServices - Service class used to updated the pediatric Profile to the Services
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void PostDependentServices() {
        showProgress();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                finish();
                MdliveUtils.closingActivityAnimation(MDLiveFamilymember.this);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    MdliveUtils.handleVolleyErrorResponse(MDLiveFamilymember.this,error, getProgressDialog());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        AddChildServices addChildServices = new AddChildServices(MDLiveFamilymember.this, getProgressDialog());
        addChildServices.getChildDependent(array, responseListener, errorListener);
    }

    /**
     * Fetching the values from the native date picker and the picker listener was implemented
     * for the particular native date picker.
     */
    private void getDateOfBirth() {
        Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);
        datePickerDialog = new CustomDatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setMinDate(TimeZoneUtils.getDateBeforeNumberOfYears(IntegerConstants.ADD_CHILD_AGELIMIT, this));
        datePickerDialog.getDatePicker().setMaxDate(TimeZoneUtils.getCalendarWithOffset(this).getTime().getTime());
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
        final Calendar c = TimeZoneUtils.getCalendarWithOffset(this);
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
    }


    private CustomDatePickerDialog.OnDateSetListener pickerListener = new CustomDatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            Calendar calendar = TimeZoneUtils.getCalendarWithOffset(MDLiveFamilymember.this);
            calendar.set(selectedYear, selectedMonth, selectedDay);
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            Calendar currendDate = TimeZoneUtils.getCalendarWithOffset(MDLiveFamilymember.this);
            currendDate.setTime(calendar.getTime());
            strDate = (new StringBuilder().append(((month+1) > 9) ? month+1:"0"+(month+1))
                    .append("/").append((day > 9) ? day :"0"+(day))
                    .append("/").append(year)
                    .append(" ")+"");
            dateTxt.setText(strDate);
            strDate = (new StringBuilder().append(month+1).append("/").append(day)
                    .append("/").append(year)
                    .append(" ")+"");
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
        View convertView = inflater.inflate(R.layout.mdlive_screen_popup, null);

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