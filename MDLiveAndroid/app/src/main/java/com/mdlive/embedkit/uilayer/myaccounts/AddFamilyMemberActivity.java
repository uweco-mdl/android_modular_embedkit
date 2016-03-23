package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.AnalyticsApplication;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddFamilyMemberInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by venkataraman_r on 8/22/2015.
 */
public class AddFamilyMemberActivity extends AppCompatActivity {
    private EditText mUsername = null;
    private EditText mEmail = null;
    private EditText mFirstName = null;
    private EditText mLastName = null;
    private EditText mAddress1 = null;
    private EditText mCity = null;
    private TextView mState = null;
    private TextView mRelationship = null;
    private EditText mPhone = null;
    private TextView mDOB = null;
    private TextView mGender = null;
    private EditText mZip = null;
    private TextView mValidEmailText = null;
    private TextView mValidationEmail = null;
    private TextView mUsernameLength = null;
    private TextView mUsernameAlphaNumericCheck = null;
    private TextView mUsernameSpecialCharactersCheck = null;

    private List<String> stateIds = new ArrayList<String>();
    private RelativeLayout mStateLayout, mDOBLayout, mGenderLayout, mRelationshipLayout;
    private boolean mayIAllowToEdit = true;
    private ProgressDialog pDialog;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_familymember);
        mZip = (EditText) findViewById(R.id.zipcodeEditText);
        mZip.setTag(null);
        mZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MdliveUtils.validateZipcodeFormat(mZip);
            }
        });

        clearMinimizedTime();

        init();

        UserBasicInfo mUserBasicInfo = UserBasicInfo.readFromSharedPreference(this);
        mAddress1 = (EditText) findViewById(R.id.streetAddress);
        mCity = (EditText) findViewById(R.id.city);
        mState = (TextView) findViewById(R.id.state);

        if(mUserBasicInfo  != null){
            if(mUserBasicInfo.getPersonalInfo().getZipcode() != null){
                mZip.setText(mUserBasicInfo.getPersonalInfo().getZipcode());
            }
            if(mUserBasicInfo.getPersonalInfo().getAddress1() != null){
                mAddress1.setText(mUserBasicInfo.getPersonalInfo().getAddress1());
            }
            if(mUserBasicInfo.getPersonalInfo().getCity() != null){
                mCity.setText(mUserBasicInfo.getPersonalInfo().getCity());
            }
            if(mUserBasicInfo.getPersonalInfo().getState() != null){
                for(int i=0;i< Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size();i++){
                    if(mUserBasicInfo.getPersonalInfo().getState().equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(i))){
                        mState.setText(Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(i));
                    }
                }
                mState.setText(mUserBasicInfo.getPersonalInfo().getState());
            }
        }

        Toolbar toolbar = null;
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setTitle(getString(R.string.mdl_add_family_member));
        ImageView back = (ImageView) toolbar.findViewById(R.id.backImg);
        back.setContentDescription(getString(R.string.mdl_ada_back_button));
        TextView title = (TextView) toolbar.findViewById(R.id.headerTxt);
        title.setText(getString(R.string.mdl_add_family_member).toUpperCase());
        ImageView apply = (ImageView) toolbar.findViewById(R.id.txtApply);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFamilyMemberInfo();
            }
        });

        mDOBLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR) + 4;
                int m = c.get(Calendar.MONTH) - 2;
                int d = c.get(Calendar.DAY_OF_MONTH);
                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                CustomDatePickerDialog dp = new CustomDatePickerDialog(AddFamilyMemberActivity.this,
                        new CustomDatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String erg = "";
                                erg += String.valueOf(monthOfYear + 1);
                                erg += "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                                erg += "/" + year;

                                mDOB.setText(erg);
                            }

                        }, y, m, d);

                dp.setTitle("Calender");
                dp.getDatePicker().setMaxDate(System.currentTimeMillis());
                dp.show();
            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    mValidationEmail.setVisibility(View.VISIBLE);
                    mValidEmailText.setVisibility(View.VISIBLE);
                } else {
                    mValidationEmail.setVisibility(View.GONE);
                    mValidEmailText.setVisibility(View.GONE);
                }
            }
        });

        mStateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });

        mGenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {
                        "Male", "Female"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddFamilyMemberActivity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mGender.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        mRelationshipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Self", "Spouse" , "Child" , "Other"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddFamilyMemberActivity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mRelationship.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mayIAllowToEdit) {
                    try {

                        String formattedString = MdliveUtils.formatDualString(mPhone.getText().toString());
                        mayIAllowToEdit = false;
                        mPhone.setText(formattedString);
                        mPhone.setSelection(mPhone.getText().toString().length());
                        mayIAllowToEdit = true;
                    } catch (Exception e) {
                    }
                }
            }
        });


        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    if (mUsername.getText().length() == 0) {
                        mUsernameLength.setVisibility(View.GONE);
                        mUsernameAlphaNumericCheck.setVisibility(View.GONE);
                        mUsernameSpecialCharactersCheck.setVisibility(View.GONE);
                    } else {
                        mUsernameLength.setVisibility(View.VISIBLE);
                        mUsernameAlphaNumericCheck.setVisibility(View.VISIBLE);
                        mUsernameSpecialCharactersCheck.setVisibility(View.VISIBLE);
                    }

                    if (mUsername.getText().toString().length() > 5 && mUsername.getText().toString().length() < 16) {
                        mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);
                    } else {
                        mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                    }
                    if (mUsername.getText().toString().matches(".*[a-zA-Z]+.*")) {
                        mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);

                    }
                    else {
                        mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                    }
                } else {
                    mUsernameLength.setVisibility(View.GONE);
                    mUsernameAlphaNumericCheck.setVisibility(View.GONE);
                    mUsernameSpecialCharactersCheck.setVisibility(View.GONE);
                }
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mUsername.getText().length() == 0) {
                    mUsernameLength.setVisibility(View.GONE);
                    mUsernameAlphaNumericCheck.setVisibility(View.GONE);
                    mUsernameSpecialCharactersCheck.setVisibility(View.GONE);
                } else {
                    mUsernameLength.setVisibility(View.VISIBLE);
                    mUsernameAlphaNumericCheck.setVisibility(View.VISIBLE);
                    mUsernameSpecialCharactersCheck.setVisibility(View.VISIBLE);
                }

                if (mUsername.getText().toString().length() > 5 && mUsername.getText().length() < 16) {
                    mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);
                } else {
                    mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                }
                if (mUsername.getText().toString().matches(".*[a-zA-Z]+.*")) {
                    mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);

                }
                else {
                    mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String result = editable.toString().replaceAll(" ", "");
                if (!editable.toString().equals(result)) {
                    mUsername.setText(result);
                    mUsername.setSelection(result.length());
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        if(!getIntent().hasExtra("user_info")){
            try {
                Class clazz = Class.forName(getString(R.string.mdl_mdlive_myaccount_module));
                Intent upIntent = new Intent(this, clazz);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(upIntent);
            } catch (ClassNotFoundException e){
                super.onBackPressed();
            }
        }
        finish();
    }

    public void leftBtnOnClick(View view) {
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myaccount_module));
            Intent upIntent = new Intent(this, clazz);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(upIntent);
            finish();
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void rightBtnOnClick(View view) {
        addFamilyMemberInfo();
    }

    public void init() {
        mUsername = (EditText) findViewById(R.id.userName);
        mEmail = (EditText) findViewById(R.id.email);
        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mAddress1 = (EditText) findViewById(R.id.streetAddress);
        mCity = (EditText) findViewById(R.id.city);
        mState = (TextView) findViewById(R.id.state);
        mPhone = (EditText) findViewById(R.id.phone);
        mRelationship = (TextView) findViewById(R.id.relationship);
        mDOB = (TextView) findViewById(R.id.DOB);
        mZip = (EditText) findViewById(R.id.zipcodeEditText);
        mGender = (TextView) findViewById(R.id.gender);
        mDOBLayout = (RelativeLayout) findViewById(R.id.DOBLayout);
        mStateLayout = (RelativeLayout) findViewById(R.id.stateLayout);
        mRelationshipLayout = (RelativeLayout) findViewById(R.id.relationshipLayout);
        mGenderLayout = (RelativeLayout) findViewById(R.id.genderLayout);
        mValidEmailText = (TextView) findViewById(R.id.validEmailText);
        mValidationEmail = (TextView) findViewById(R.id.validationEmail);
        mUsernameLength = (TextView) findViewById(R.id.userNameLength);
        mUsernameAlphaNumericCheck = (TextView) findViewById(R.id.userNameAlphaNumericCheck);
        mUsernameSpecialCharactersCheck = (TextView) findViewById(R.id.userNameSpecialCharactersCheck);

        pDialog = MdliveUtils.getFullScreenProgressDialog(this);
    }

    public void addFamilyMemberInfo() {
        String userName = mUsername.getText().toString().trim();
        String eMail = mEmail.getText().toString().trim();
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String address1 = mAddress1.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        String state = mState.getText().toString().trim();
        String phone = mPhone.getText().toString().trim().replaceAll("[-() ]", "");
        String dob = mDOB.getText().toString().trim();
        String zipCode = mZip.getText().toString();
        String gender = mGender.getText().toString().trim();
        String relationship = mRelationship.getText().toString().trim();

        if (isEmpty(userName) && isEmpty(eMail) && isEmpty(firstName) && isEmpty(lastName) && isEmpty(address1) && isEmpty(city)
                && isEmpty(state) && isEmpty(phone) && isEmpty(dob) && isEmpty(gender) && isEmpty(relationship)) {
            if (validEmail(eMail)) {
                if(!MdliveUtils.validateZipCode(zipCode)){
                    /*Toast.makeText(getApplicationContext(), getString(R.string.mdl_valid_zip), Toast.LENGTH_SHORT).show();*/
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.mdl_valid_zip),
                            Snackbar.LENGTH_SHORT).show();
                }else {
                    try {
                        JSONObject parent = new JSONObject();

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("computer", "MAC");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("username", userName);
                        jsonObject1.put("first_name", firstName);
                        jsonObject1.put("last_name", lastName);
                        jsonObject1.put("gender", gender);
                        jsonObject1.put("email", eMail);
                        jsonObject1.put("phone", phone);
                        jsonObject1.put("address1", address1);
                        jsonObject1.put("city", city);
                        jsonObject1.put("state_id", state);
                        jsonObject1.put("relationship", relationship);
                        jsonObject1.put("zip", zipCode.replace("-", ""));
                        jsonObject1.put("birthdate", dob);
                        jsonObject1.put("answer", "idontknow");

                        parent.put("member", jsonObject1);
                        parent.put("camera", jsonObject);

                    // Obtain the shared Tracker instance.
                    AnalyticsApplication application = new AnalyticsApplication();
                    for(AnalyticsApplication.TrackerName tn : AnalyticsApplication.TrackerName.values()) {
                        Tracker mTracker = application.getTracker(getApplication(), tn);
                        String age = getString(R.string.mdl_mdlive_child);
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            int years = MdliveUtils.calculateAge(sdf.parse(dob), this);
                            if (years >= 18) {
                                age = getString(R.string.mdl_mdlive_adult);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mTracker.setScreenName(getString(R.string.mdl_mdlive_add_family_session));
                        mTracker.send(new HitBuilders.ScreenViewBuilder()
                                .setCustomDimension(MDLiveConfig.GA_DIMENSIONS.GENDER.ordinal(), gender)
                                .setCustomDimension(MDLiveConfig.GA_DIMENSIONS.AGE.ordinal(), age)
                                .build());
                    }
                        addFamilyMember(parent.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                /*Toast.makeText(getBaseContext(), "eMail id is invalid", Toast.LENGTH_SHORT).show();*/
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.mdl_email_id_invalid),
                        Snackbar.LENGTH_SHORT).show();
            }
        } else {
            /*Toast.makeText(AddFamilyMemberActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.mdl_all_fields_required),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo) {
        return !TextUtils.isEmpty(cardInfo);
    }

    private void addFamilyMember(String params) {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddFamilyInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(AddFamilyMemberActivity.this, error, pDialog);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, AddFamilyMemberActivity.this);
                }
            }
        };

        AddFamilyMemberInfoService service = new AddFamilyMemberInfoService(AddFamilyMemberActivity.this, null);
        service.addFamilyMemberInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddFamilyInfoSuccessResponse(JSONObject response) {
        try {

            pDialog.dismiss();

            /*Toast.makeText(AddFamilyMemberActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();*/
            Snackbar.make(findViewById(android.R.id.content),
                    response.getString("message"),
                    Snackbar.LENGTH_SHORT).show();

            final User user = User.getSelectedUser(getBaseContext());
            if (user == null) {
                final Intent intent = new Intent(this, MDLiveDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                String activityCaller  = getIntent().getStringExtra("activitycaller");
                Log.v("Caller bname", activityCaller);
                if(activityCaller.equals("getstarted")){
                    try {
                        Class clazz = Class.forName(getString(R.string.mdl_mdlive_sav_module));
                        Method method = clazz.getMethod("getGetStartedIntentWithUser", Context.class, User.class);
                        startActivity((Intent) method.invoke(null, getBaseContext(), user));
                    } catch (ClassNotFoundException e){
                        /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                        Snackbar.make(findViewById(android.R.id.content),
                                getString(R.string.mdl_mdlive_module_not_found),
                                Snackbar.LENGTH_LONG).show();
                    }
            }else {
                    startActivity(MDLiveDashboardActivity.getDashboardIntentWithUser(getBaseContext(), user));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddFamilyMemberActivity.this);

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));

        final String[] stringArray = stateList.toArray(new String[stateList.size()]);

        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String SelectedText = stateIds.get(i);
                mState.setText(SelectedText);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void clearMinimizedTime() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Log.d("Timer", "clear called");
            }
        }, 1000);
    }

    private boolean validEmail(String email) {

        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}