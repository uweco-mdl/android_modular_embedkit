package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangePasswordService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/17/2015.
 */
public class ChangePasswordFragment extends MDLiveBaseFragment {

    public static ChangePasswordFragment newInstance() {
        final ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        return changePasswordFragment;
    }

    private EditText mCurrentPassword = null;
    private EditText mNewPassword = null;
    private EditText mConfirmPassword = null;
    private TextView mPasswordLength = null;
    private TextView mPasswordConfirmCheck = null;
    private TextView mPasswordAlphaNumericCheck = null;
    private TextView passworddiffName = null;
    private TextView passwordspecialChars = null;
    private TextView passwordtwoRepeatChars = null;
    private TextView severityTv = null;
    private TextView confirmSeverityTv = null;
    private ImageButton selectedImageIcon = null;
    private ImageButton mCurrentPasswordShow = null;
    private ImageButton mNewPasswordShow = null;
    private ImageButton mConfirmPasswordShow = null;
    private boolean passwordLength = false, passwordDiffName = false;
    private boolean passwordAlphaNumericCheck = false;
    private boolean passwordConfirmCheck = false;
    private boolean confirmPasswordAlphaNumericCheck = false;
    private boolean confirmPasswordLength = false;

    public static final String TAG = "CHANGE PASSWORD";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePasswordView = inflater.inflate(R.layout.fragments_change_password, null);

        mCurrentPassword = (EditText) changePasswordView.findViewById(R.id.currentPassword);
        mNewPassword = (EditText) changePasswordView.findViewById(R.id.NewPassword);
        mConfirmPassword = (EditText) changePasswordView.findViewById(R.id.confirmPassword);
        mPasswordLength = (TextView) changePasswordView.findViewById(R.id.passwordLength);
        passworddiffName = (TextView) changePasswordView.findViewById(R.id.passworddiffName);
        passwordspecialChars = (TextView) changePasswordView.findViewById(R.id.passwordspecialChars);
        passwordtwoRepeatChars = (TextView) changePasswordView.findViewById(R.id.passwordtwoRepeatChars);
        mPasswordConfirmCheck = (TextView) changePasswordView.findViewById(R.id.passwordConfirmCheck);
        mPasswordAlphaNumericCheck = (TextView) changePasswordView.findViewById(R.id.passwordAlphaNumericCheck);
        mCurrentPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.currentPasswordShow);
        mNewPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.newPasswordShow);
        mConfirmPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.confirmPasswordShow);
        severityTv = (TextView) changePasswordView.findViewById(R.id.severityTv);
        confirmSeverityTv = (TextView) changePasswordView.findViewById(R.id.confirmSeverityTv);


        mCurrentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (view.hasFocus()) {
                    if (mCurrentPassword.getText().length() == 0) {
                        mCurrentPasswordShow.setVisibility(View.GONE);
                    } else {
                        mCurrentPasswordShow.setVisibility(View.VISIBLE);
                    }
                    /*if (mCurrentPassword.getText().length() >= 8 && mCurrentPassword.getText().length() <= 15) {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    } else {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    }
                    if (mCurrentPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                    } else {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    }*/
                } else {
                    mCurrentPasswordShow.setVisibility(View.GONE);
                }
            }
        });

        mNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {

                    showViewsOnCondition();

                    if (mNewPassword.getText().length() == 0) {
                        mNewPasswordShow.setVisibility(View.GONE);
//                        severityTv.setVisibility(View.GONE);
                    } else {
                        mNewPasswordShow.setVisibility(View.VISIBLE);
                        /*severityTv.setText("WEAK");
                        severityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        severityTv.setVisibility(View.VISIBLE);*/
                        Log.d("Inside ", "inside" + confirmSeverityTv.getText().toString());
                    }

                    if (mNewPassword.getText().length() >= 8 && mNewPassword.getText().length() <= 15) {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                        passwordLength = true;
                    } else {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                        passwordLength = false;
                    }
                    if (mNewPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                        passwordAlphaNumericCheck = true;

                    } else {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                        passwordAlphaNumericCheck = false;
                    }
                } else {
                    mNewPasswordShow.setVisibility(View.GONE);
                    severityTv.setVisibility(View.GONE);
                }
            }
        });


        mConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (view.hasFocus()) {

                    showViewsOnCondition();

                    if (mConfirmPassword.getText().length() == 0) {
                        mConfirmPasswordShow.setVisibility(View.GONE);
//                        confirmSeverityTv.setVisibility(View.GONE);
                    } else {
                        mConfirmPasswordShow.setVisibility(View.VISIBLE);
//                        confirmSeverityTv.setText("WEAK");
//                        confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
//                        confirmSeverityTv.setVisibility(View.VISIBLE);
                        Log.d("Inside ", "inside" + severityTv.getText().toString());
                    }
                   /* if (mConfirmPassword.getText().length() < 6 && mConfirmPassword.getText().toString().matches("\\d+")) {
                        confirmSeverityTv.setText("WEAK");
                        confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    } else if (mConfirmPassword.getText().length() >= 6 && mConfirmPassword.getText().length() < 9) {
                        confirmSeverityTv.setText("MEDIUM");
                        confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_medium_text_color_yellow));
                    } else if (mConfirmPassword.getText().length() > 8 && mConfirmPassword.getText().toString().matches("[^a-z0-9]")) {
                        confirmSeverityTv.setText("STRONG");
                        confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    }*/


                    if (mConfirmPassword.getText().length() >= 8 && mConfirmPassword.getText().length() <= 15) {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                        confirmPasswordLength = true;
                    } else {
                        mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                        confirmPasswordLength = false;
                    }
                    if (mConfirmPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                        confirmPasswordAlphaNumericCheck = true;

                    } else {
                        mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                        confirmPasswordAlphaNumericCheck = false;
                    }

                    if (mConfirmPassword.getText().toString().equals(mNewPassword.getText().toString())) {
                        mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                        passwordConfirmCheck = true;
                    } else {
                        mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                        passwordConfirmCheck = false;
                    }
                } else {
                    mConfirmPasswordShow.setVisibility(View.GONE);
                }
            }
        });

        mCurrentPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPasswordShow.getTag() == null) {
                    mCurrentPasswordShow.setTag("eye_on");
                    mCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mCurrentPasswordShow.setImageResource(R.drawable.eye_on);
                } else if (mCurrentPasswordShow.getTag().equals("eye_on")) {
                    mCurrentPasswordShow.setTag("eye_off");
                    mCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mCurrentPasswordShow.setImageResource(R.drawable.eye_off);
                } else {
                    mCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mCurrentPasswordShow.setTag("eye_on");
                    mCurrentPasswordShow.setImageResource(R.drawable.eye_on);
                }
                mCurrentPassword.setSelection(mCurrentPassword.getText().length());
            }
        });


        mNewPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewPasswordShow.getTag() == null) {
                    mNewPasswordShow.setTag("eye_on");
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPasswordShow.setImageResource(R.drawable.eye_on);
                } else if (mNewPasswordShow.getTag().equals("eye_on")) {
                    mNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mNewPasswordShow.setTag("eye_off");
                    mNewPasswordShow.setImageResource(R.drawable.eye_off);
                } else {
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPasswordShow.setTag("eye_on");
                    mNewPasswordShow.setImageResource(R.drawable.eye_on);
                }
                mNewPassword.setSelection(mNewPassword.getText().length());
            }
        });


        mConfirmPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfirmPasswordShow.getTag() == null) {
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmPasswordShow.setTag("eye_on");
                    mConfirmPasswordShow.setImageResource(R.drawable.eye_on);
                } else if (mConfirmPasswordShow.getTag().equals("eye_on")) {
                    mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConfirmPasswordShow.setTag("eye_off");
                    mConfirmPasswordShow.setImageResource(R.drawable.eye_off);
                } else {
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmPasswordShow.setTag("eye_on");
                    mConfirmPasswordShow.setImageResource(R.drawable.eye_on);
                }
                mConfirmPassword.setSelection(mConfirmPassword.getText().length());
            }
        });


        mCurrentPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeVisibilityOfImageButton(mCurrentPasswordShow, mCurrentPassword, confirmSeverityTv);
                return false;
            }
        });

        mNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeVisibilityOfImageButton(mNewPasswordShow, mNewPassword, severityTv);
                return false;
            }
        });

        mConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeVisibilityOfImageButton(mConfirmPasswordShow, mConfirmPassword, null);
                return false;
            }
        });

        mCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkThatUserEnteredPassword();
                changeVisibilityOfImageButton(mCurrentPasswordShow, mCurrentPassword, null);

                /*if (mCurrentPassword.getText().length() >= 8 && mCurrentPassword.getText().length() <= 15) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);

                }
                if (mCurrentPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);


                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);

                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkThatUserEnteredPassword();
                changeVisibilityOfImageButton(mNewPasswordShow, mNewPassword, severityTv);
                if (mNewPassword.getText().length() > 8 && mNewPassword.getText().toString().matches("^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
                    severityTv.setText("STRONG");
                    severityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                } else if(mNewPassword.getText().length() >= 6 && mNewPassword.getText().toString().matches("^(\\w*(\\d+[a-zA-Z]|[a-zA-Z]+\\d)\\w*)+$")) {
                    severityTv.setText("MEDIUM");
                    severityTv.setTextColor(getResources().getColor(R.color.change_password_medium_text_color_yellow));
                } else{
                    severityTv.setText("WEAK");
                    severityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                }

                UserBasicInfo info = UserBasicInfo.readFromSharedPreference(getActivity());
                if(!info.getPersonalInfo().getUsername().equals(mNewPassword)){
                    passworddiffName.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    passworddiffName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    passwordDiffName = true;
                } else {
                    passworddiffName.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    passworddiffName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    passwordDiffName = false;
                }
                if (mNewPassword.getText().length() >= 8 && mNewPassword.getText().length() <= 15) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    passwordLength = true;
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    passwordLength = false;
                }
                if (mNewPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    passwordAlphaNumericCheck = true;
                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    passwordAlphaNumericCheck = false;
                }
                showViewsOnCondition();

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkThatUserEnteredPassword();
                changeVisibilityOfImageButton(mConfirmPasswordShow, mConfirmPassword, confirmSeverityTv);

                if (mConfirmPassword.getText().length() > 8  && mConfirmPassword.getText().toString().matches("^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
                    confirmSeverityTv.setText("STRONG");
                    confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                } else if (mConfirmPassword.getText().length() >= 6 && mConfirmPassword.getText().toString().matches("^(\\w*(\\d+[a-zA-Z]|[a-zA-Z]+\\d)\\w*)+$")) {
                    confirmSeverityTv.setText("MEDIUM");
                    confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_medium_text_color_yellow));
                } else {
                    confirmSeverityTv.setText("WEAK");
                    confirmSeverityTv.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                }

                if (mConfirmPassword.getText().length() >= 8 && mConfirmPassword.getText().length() <= 15) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    confirmPasswordLength = true;
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    confirmPasswordLength = false;
                }

                if (mConfirmPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    confirmPasswordAlphaNumericCheck = true;
                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    confirmPasswordAlphaNumericCheck = false;
                }
                if (charSequence != null && charSequence.length() != 0) {
                    if (frequencyCount(charSequence.toString())) {
                        passwordtwoRepeatChars.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                        passwordtwoRepeatChars.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    } else {
                        passwordtwoRepeatChars.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                        passwordtwoRepeatChars.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    }
                }
                showViewsOnCondition();

                if (mConfirmPassword.getText() != null && !TextUtils.isEmpty(mConfirmPassword.getText().toString())
                        && mNewPassword.getText() != null && !TextUtils.isEmpty(mNewPassword.getText().toString())
                        && (mConfirmPassword.getText().toString()).equals(mNewPassword.getText().toString())) {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                    passwordConfirmCheck = true;
                } else {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                    passwordConfirmCheck = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        return changePasswordView;
    }

    public void showViewsOnCondition() {
        try {
            if (mNewPassword.getText().toString().length() == 0
                    && mConfirmPassword.getText().toString().length() == 0) {
                passworddiffName.setVisibility(View.GONE);
                mPasswordLength.setVisibility(View.GONE);
                mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                mPasswordConfirmCheck.setVisibility(View.GONE);
                passwordtwoRepeatChars.setVisibility(View.GONE);
                passwordspecialChars.setVisibility(View.GONE);
            } else {
                passworddiffName.setVisibility(View.VISIBLE);
                mPasswordLength.setVisibility(View.VISIBLE);
                mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                mPasswordConfirmCheck.setVisibility(View.VISIBLE);
                passwordtwoRepeatChars.setVisibility(View.VISIBLE);
                passwordspecialChars.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean frequencyCount(String userText) {
        int count = 0;
        for (int i = 0; i < userText.length(); i++) {
            for (int j = 0; j < userText.length(); j++) {
                if (userText.charAt(i) == userText.charAt(j)) {
                    count++;
                }
            }
            if (count > 2) {
                System.out.println(userText.charAt(i) + "--" + count);
                return true;
            }
            count = 0;
        }
        return false;
    }

    private void changeVisibilityOfImageButton(ImageButton newSelectedImageIcon, TextView selectedTv, TextView severityTv) {
        if (selectedImageIcon == null) {
            selectedImageIcon = newSelectedImageIcon;
            selectedImageIcon.setTag("eye_on");
        } else {
            selectedImageIcon.setVisibility(View.GONE);
            selectedImageIcon = newSelectedImageIcon;
            if (selectedImageIcon.getTag() == null) {
                selectedImageIcon.setTag("eye_on");
            }
            if (severityTv != null) {
                severityTv.setVisibility(View.GONE);
            }
        }
        if (selectedTv.getText() == null || selectedTv.getText().toString().length() == 0) {
            selectedImageIcon.setVisibility(View.GONE);
            if (severityTv != null) {
                severityTv.setVisibility(View.GONE);
            }
        } else {
            selectedImageIcon.setVisibility(View.VISIBLE);
            if(severityTv!=null) {
                severityTv.setVisibility(View.VISIBLE);
            }
        }
        if (selectedImageIcon.getTag() != null && selectedImageIcon.getTag().equals("eye_on")) {
            selectedImageIcon.setImageResource(R.drawable.eye_on);
        } else {
            selectedImageIcon.setImageResource(R.drawable.eye_off);
            selectedImageIcon.setTag("eye_off");
        }
    }

    private void checkThatUserEnteredPassword() {
        if ((mCurrentPassword.getText() != null && mCurrentPassword.getText().toString().length() != 0) ||
                (mConfirmPassword.getText() != null && mConfirmPassword.getText().toString().length() != 0) ||
                (mNewPassword.getText() != null && mNewPassword.getText().toString().length() != 0)) {
            passworddiffName.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
            passworddiffName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
            passwordspecialChars.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
            passwordspecialChars.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
        } else {
            passworddiffName.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
            passworddiffName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
            passwordspecialChars.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
            passwordspecialChars.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
        }
    }

    private void loadChangePasswordService(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleChangePasswordSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                } else {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
            }
        };

        ChangePasswordService service = new ChangePasswordService(getActivity(), null);
        service.changePassword(successCallBackListener, errorListener, params);
    }

    private void handleChangePasswordSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            //Fetch Data From the Services
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            getActivity().finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changePassword() {
        if (!TextUtils.isEmpty(mCurrentPassword.getText().toString()) && !TextUtils.isEmpty(mNewPassword.getText().toString()) && !TextUtils.isEmpty(mConfirmPassword.getText().toString())) {
            if (passwordConfirmCheck && passwordLength && passwordAlphaNumericCheck && confirmPasswordAlphaNumericCheck && confirmPasswordLength && passwordDiffName) {
                try {
                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("password", mNewPassword.getText().toString());
                    jsonObject.put("current_password", mCurrentPassword.getText().toString());
                    jsonObject.put("password_confirmation", mConfirmPassword.getText().toString());
                    parent.put("user", jsonObject);
                    Log.i("jsonobject", parent.toString());
                    loadChangePasswordService(parent.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "New & Confirm password mismatch", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
