package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.AnalyticsApplication;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddFamilyMemberInfoService;

import org.json.JSONException;
import org.json.JSONObject;

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
    private EditText mPhone = null;
    private TextView mDOB = null;
    private TextView mGender = null;
    private TextView mValidEmailText = null;
    private TextView mValidationEmail = null;
    private TextView mUsernameLength = null;
    private TextView mUsernameAlphaNumericCheck = null;
    private TextView mUsernameSpecialCharactersCheck = null;
    private String Username = null;
    private String Email = null;
    private String FirstName = null;
    private String LastName = null;
    private String Address1 = null;
    private String City = null;
    private String State = null;
    private String Phone = null;
    private String DOB = null;
    private String Gender = null;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private RelativeLayout mStateLayout, mDOBLayout, mGenderLayout;
    private boolean mayIAllowToEdit = true;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_familymember);
        clearMinimizedTime();

        init();

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

        ImageView back = (ImageView) toolbar.findViewById(R.id.backImg);
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

                DatePickerDialog dp = new DatePickerDialog(AddFamilyMemberActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

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
                dp.getDatePicker().setMinDate(MdliveUtils.getDateBeforeNumberOfYears(IntegerConstants.ADD_CHILD_AGELIMIT));
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

                    if (mUsername.getText().length() > 6 && mUsername.getText().length() < 16) {
                        mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);
                    } else {
                        mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                    }
                    if (mUsername.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                        mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);

                    } else {
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

                if (mUsername.getText().length() > 6 && mUsername.getText().length() < 16) {
                    mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);
                } else {
                    mUsernameLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mUsernameLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                }
                if (mUsername.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle_small), null, null, null);

                } else {
                    mUsernameAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mUsernameAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle_small), null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        try {
            Class clazz = Class.forName(getString(R.string.mdl_mdlive_myaccount_module));
            Intent upIntent = new Intent(this, clazz);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(upIntent);
            finish();
        } catch (ClassNotFoundException e){
            super.onBackPressed();
        }
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
            Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
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
        mDOB = (TextView) findViewById(R.id.DOB);
        mGender = (TextView) findViewById(R.id.gender);
        mDOBLayout = (RelativeLayout) findViewById(R.id.DOBLayout);
        mStateLayout = (RelativeLayout) findViewById(R.id.stateLayout);
        mGenderLayout = (RelativeLayout) findViewById(R.id.genderLayout);
        mValidEmailText = (TextView) findViewById(R.id.validEmailText);
        mValidationEmail = (TextView) findViewById(R.id.validationEmail);
        mUsernameLength = (TextView) findViewById(R.id.userNameLength);
        mUsernameAlphaNumericCheck = (TextView) findViewById(R.id.userNameAlphaNumericCheck);
        mUsernameSpecialCharactersCheck = (TextView) findViewById(R.id.userNameSpecialCharactersCheck);

        pDialog = MdliveUtils.getFullScreenProgressDialog(this);
    }

    public void addFamilyMemberInfo() {
        Username = mUsername.getText().toString().trim();
        Email = mEmail.getText().toString().trim();
        FirstName = mFirstName.getText().toString().trim();
        LastName = mLastName.getText().toString().trim();
        Address1 = mAddress1.getText().toString().trim();
        City = mCity.getText().toString().trim();
        State = mState.getText().toString().trim();
        Phone = mPhone.getText().toString().trim().replaceAll("[-() ]", "");
        DOB = mDOB.getText().toString().trim();
        Gender = mGender.getText().toString().trim();

        if (isEmpty(Username) && isEmpty(Email) && isEmpty(FirstName) && isEmpty(LastName) && isEmpty(Address1) && isEmpty(City)
                && isEmpty(State) && isEmpty(Phone) && isEmpty(DOB) && isEmpty(Gender)) {
            if (validEmail(Email)) {
                try {
                    JSONObject parent = new JSONObject();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("computer", "MAC");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("username", Username);
                    jsonObject1.put("first_name", FirstName);
                    jsonObject1.put("last_name", LastName);
                    jsonObject1.put("gender", Gender);
                    jsonObject1.put("email", Email);
                    jsonObject1.put("phone", Phone);
                    jsonObject1.put("address1", Address1);
                    jsonObject1.put("city", City);
                    jsonObject1.put("state_id", State);
                    jsonObject1.put("birthdate", DOB);
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
                            int years = MdliveUtils.calculateAge(sdf.parse(DOB));
                            if (years >= 18) {
                                age = getString(R.string.mdl_mdlive_adult);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mTracker.setScreenName(getString(R.string.mdl_mdlive_add_family_session));
                        mTracker.send(new HitBuilders.ScreenViewBuilder()
                                .setCustomDimension(MDLiveConfig.GA_DIMENSIONS.GENDER.ordinal(), Gender)
                                .setCustomDimension(MDLiveConfig.GA_DIMENSIONS.AGE.ordinal(), age)
                                .build());
                    }
                    addFamilyMember(parent.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "Email id is invalid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddFamilyMemberActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo) {
        if (!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
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

            Toast.makeText(AddFamilyMemberActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

            final User user = User.getSelectedUser(getBaseContext());
            if (user == null) {
                final Intent intent = new Intent(this, MDLiveDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                startActivity(MDLiveDashboardActivity.getDashboardIntentWithUser(getBaseContext(), user));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddFamilyMemberActivity.this);

        stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
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
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private boolean validEmail(String email) {

        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}