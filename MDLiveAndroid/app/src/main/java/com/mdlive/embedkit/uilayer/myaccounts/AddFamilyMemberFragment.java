package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddFamilyMemberInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by venkataraman_r on 7/27/2015.
 */
public class AddFamilyMemberFragment extends MDLiveBaseFragment {

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

    public static AddFamilyMemberFragment newInstance() {

        final AddFamilyMemberFragment addFamilyMember = new AddFamilyMemberFragment();
        return addFamilyMember;
    }

    public AddFamilyMemberFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View addFamilyMember = inflater.inflate(R.layout.fragment_add_familymember, null);

        init(addFamilyMember);

        mDOBLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = TimeZoneUtils.getCalendarWithOffset(getActivity());

                int y = c.get(Calendar.YEAR) + 4;
                int m = c.get(Calendar.MONTH) - 2;
                int d = c.get(Calendar.DAY_OF_MONTH);
                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(getActivity(),
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
                dp.getDatePicker().setMinDate(TimeZoneUtils.getDateBeforeNumberOfYears(IntegerConstants.ADD_CHILD_AGELIMIT, getActivity()));
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


        return addFamilyMember;

    }

    public void init(View addFamilyMember) {
        mUsername = (EditText) addFamilyMember.findViewById(R.id.userName);
        mEmail = (EditText) addFamilyMember.findViewById(R.id.email);
        mFirstName = (EditText) addFamilyMember.findViewById(R.id.firstName);
        mLastName = (EditText) addFamilyMember.findViewById(R.id.lastName);
        mAddress1 = (EditText) addFamilyMember.findViewById(R.id.streetAddress);
        mCity = (EditText) addFamilyMember.findViewById(R.id.city);
        mState = (TextView) addFamilyMember.findViewById(R.id.state);
        mPhone = (EditText) addFamilyMember.findViewById(R.id.phone);
        mDOB = (TextView) addFamilyMember.findViewById(R.id.DOB);
        mValidEmailText = (TextView) addFamilyMember.findViewById(R.id.validEmailText);
        mValidationEmail = (TextView) addFamilyMember.findViewById(R.id.validationEmail);
        mUsernameLength = (TextView) addFamilyMember.findViewById(R.id.userNameLength);
        mUsernameAlphaNumericCheck = (TextView) addFamilyMember.findViewById(R.id.userNameAlphaNumericCheck);
        mUsernameSpecialCharactersCheck = (TextView) addFamilyMember.findViewById(R.id.userNameSpecialCharactersCheck);
        mGender = (TextView) addFamilyMember.findViewById(R.id.gender);
        mDOBLayout = (RelativeLayout) addFamilyMember.findViewById(R.id.DOBLayout);
        mStateLayout = (RelativeLayout) addFamilyMember.findViewById(R.id.stateLayout);
        mGenderLayout = (RelativeLayout) addFamilyMember.findViewById(R.id.genderLayout);
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
                    addFamilyMember(parent.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Email id is invalid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo) {
        if (!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    private void addFamilyMember(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddFamilyInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        AddFamilyMemberInfoService service = new AddFamilyMemberInfoService(getActivity(), null);
        service.addFamilyMemberInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddFamilyInfoSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

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

    private boolean validEmail(String email) {

        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}