package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
    private ImageButton mCurrentPasswordShow = null;
    private ImageButton mNewPasswordShow = null;
    private ImageButton mConfirmPasswordShow = null;

    public static final String TAG = "CHANGE PASSWORD";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePasswordView = inflater.inflate(R.layout.fragments_change_password, null);

        mCurrentPassword = (EditText) changePasswordView.findViewById(R.id.currentPassword);
        mNewPassword = (EditText) changePasswordView.findViewById(R.id.NewPassword);
        mConfirmPassword = (EditText) changePasswordView.findViewById(R.id.confirmPassword);
        mPasswordLength = (TextView) changePasswordView.findViewById(R.id.passwordLength);
        mPasswordConfirmCheck = (TextView) changePasswordView.findViewById(R.id.passwordConfirmCheck);
        mPasswordAlphaNumericCheck = (TextView) changePasswordView.findViewById(R.id.passwordAlphaNumericCheck);
        mCurrentPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.currentPasswordShow);
        mNewPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.newPasswordShow);
        mConfirmPasswordShow = (ImageButton) changePasswordView.findViewById(R.id.confirmPasswordShow);

        mCurrentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (view.hasFocus()) {
                    if (mCurrentPassword.getText().length() == 0) {
                        mCurrentPasswordShow.setVisibility(View.GONE);
                        mPasswordLength.setVisibility(View.GONE);
                        mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                        mPasswordConfirmCheck.setVisibility(View.GONE);
                    } else {
                        mCurrentPasswordShow.setVisibility(View.VISIBLE);
                        mPasswordLength.setVisibility(View.VISIBLE);
                        mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                    }

                    if (mCurrentPassword.getText().length() > 7 && mCurrentPassword.getText().length() < 16) {
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
                    }
                }
            }
        });

        mNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mNewPassword.getText().length() == 0) {
                    mNewPasswordShow.setVisibility(View.GONE);
                    mPasswordLength.setVisibility(View.GONE);
                    mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                } else {
                    mNewPasswordShow.setVisibility(View.VISIBLE);
                    mPasswordLength.setVisibility(View.VISIBLE);
                    mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                }

                if (mNewPassword.getText().length() > 7 && mNewPassword.getText().length() < 16) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }
                if (mNewPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }
            }
        });

        mConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mConfirmPassword.getText().length() == 0) {
                    mConfirmPasswordShow.setVisibility(View.GONE);
                    mPasswordLength.setVisibility(View.GONE);
                    mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                } else {
                    mConfirmPasswordShow.setVisibility(View.VISIBLE);
                    mPasswordLength.setVisibility(View.VISIBLE);
                    mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                    mPasswordConfirmCheck.setVisibility(View.VISIBLE);
                }

                if (mConfirmPassword.getText().length() > 7 && mConfirmPassword.getText().length() < 16) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }
                if (mConfirmPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }

                if (mConfirmPassword.equals(mNewPassword)) {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }

            }
        });

        mCurrentPasswordShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return true;
                }
                return false;
            }
        });

        mNewPasswordShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return true;
                }
                return false;
            }
        });

        mConfirmPasswordShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return true;
                }
                return false;
            }
        });

        mCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mCurrentPassword.getText().length() == 0) {
                    mCurrentPasswordShow.setVisibility(View.GONE);
                    mPasswordLength.setVisibility(View.GONE);
                    mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                } else {
                    mCurrentPasswordShow.setVisibility(View.VISIBLE);
                    mPasswordLength.setVisibility(View.VISIBLE);
                    mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                }


                if (mCurrentPassword.getText().length() > 7 && mCurrentPassword.getText().length() < 16) {
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
                }
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

                if (mNewPassword.getText().length() == 0) {
                    mNewPasswordShow.setVisibility(View.GONE);
                    mPasswordLength.setVisibility(View.GONE);
                    mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                } else {
                    mNewPasswordShow.setVisibility(View.VISIBLE);
                    mPasswordLength.setVisibility(View.VISIBLE);
                    mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                }

                if (mNewPassword.getText().length() > 7 && mNewPassword.getText().length() < 16) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }
                if (mNewPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }


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

                if (mConfirmPassword.getText().length() == 0) {
                    mConfirmPasswordShow.setVisibility(View.GONE);
                    mPasswordLength.setVisibility(View.GONE);
                    mPasswordAlphaNumericCheck.setVisibility(View.GONE);
                    mPasswordConfirmCheck.setVisibility(View.GONE);
                } else {
                    mConfirmPasswordShow.setVisibility(View.VISIBLE);
                    mPasswordLength.setVisibility(View.VISIBLE);
                    mPasswordAlphaNumericCheck.setVisibility(View.VISIBLE);
                    mPasswordConfirmCheck.setVisibility(View.VISIBLE);
                }

                if (mConfirmPassword.getText().length() > 7 && mConfirmPassword.getText().length() < 16) {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordLength.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordLength.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }
                if (mConfirmPassword.getText().toString().matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);

                } else {
                    mPasswordAlphaNumericCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordAlphaNumericCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }

                if ((mConfirmPassword.getText().toString()).equals(mNewPassword.getText().toString())) {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_green));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.green_circle), null, null, null);
                } else {
                    mPasswordConfirmCheck.setTextColor(getResources().getColor(R.color.change_password_alert_text_color_red));
                    mPasswordConfirmCheck.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.red_circle), null, null, null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        return changePasswordView;
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
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
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

            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", mNewPassword.getText().toString());
                jsonObject.put("current_password", mCurrentPassword.getText().toString());
                jsonObject.put("password_confirmation", mConfirmPassword.getText().toString());
                parent.put("user", jsonObject);
                loadChangePasswordService(parent.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
