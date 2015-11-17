package com.mdlive.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.myaccounts.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.EditMyProfileService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 8/21/2015.
 */

public class ChangePhoneNumber extends MDLiveBaseFragment {

    public static ChangePhoneNumber newInstance(String response) {

        final ChangePhoneNumber changePhoneNumber = new ChangePhoneNumber();
        Bundle bundle = new Bundle();
        bundle.putString("Response", response);
        changePhoneNumber.setArguments(bundle);
        return changePhoneNumber;
    }

    private EditText mPhoneNumber = null;
    private EditText mEmergencyContactNumber = null;
    private final static int PHONENUMBER_LENGTH = 14;
    private boolean mayIAllowToEdit = true;
    private String response;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePhoneNumberView = inflater.inflate(R.layout.fragment_change_phonenumber, null);
        getActivity().setTitle(getString(R.string.mdl_phone_number));

        mPhoneNumber = (EditText) changePhoneNumberView.findViewById(R.id.phoneNumber);
        mEmergencyContactNumber = (EditText) changePhoneNumberView.findViewById(R.id.emergencyContactNumber);

        response = getArguments().getString("Response");

        if (response != null) {
            try {
                JSONObject responseDetail = new JSONObject(response);

                mPhoneNumber.setText(responseDetail.getString("phone"));

                String formattedString = MdliveUtils.formatDualString(mPhoneNumber.getText().toString());
                mPhoneNumber.setText(formattedString);
                mPhoneNumber.setSelection(mPhoneNumber.getText().toString().length());

                if (responseDetail.getString("emergency_contact_number").equalsIgnoreCase("null") || (responseDetail.getString("emergency_contact_number") == null)  || (TextUtils.isEmpty(responseDetail.getString("emergency_contact_number")))) {
                    mEmergencyContactNumber.setText("");
                } else {
                    mEmergencyContactNumber.setText(responseDetail.getString("emergency_contact_number"));
                    formattedString = MdliveUtils.formatDualString(mEmergencyContactNumber.getText().toString());
                    mEmergencyContactNumber.setText(formattedString);
                    mEmergencyContactNumber.setSelection(mEmergencyContactNumber.getText().toString().length());

                }


                if (mPhoneNumber.getText().length() == PHONENUMBER_LENGTH && mEmergencyContactNumber.getText().length() == PHONENUMBER_LENGTH) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).showTick();
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mPhoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int after) {


            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (mayIAllowToEdit) {
                        String formattedString = MdliveUtils.formatDualString(mPhoneNumber.getText().toString());
                        mayIAllowToEdit = false;
                        mPhoneNumber.setText(formattedString);
                        mPhoneNumber.setSelection(mPhoneNumber.getText().toString().length());
                        mayIAllowToEdit = true;
                }

                if (mPhoneNumber.getText().length() == PHONENUMBER_LENGTH && mEmergencyContactNumber.getText().length() == PHONENUMBER_LENGTH) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).showTick();
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                }
            }
        });

        mEmergencyContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (mayIAllowToEdit) {
                        String formattedString = MdliveUtils.formatDualString(mEmergencyContactNumber.getText().toString());
                        mayIAllowToEdit = false;
                        mEmergencyContactNumber.setText(formattedString);
                        mEmergencyContactNumber.setSelection(mEmergencyContactNumber.getText().toString().length());
                        mayIAllowToEdit = true;
                }


                if (mPhoneNumber.getText().length() == PHONENUMBER_LENGTH && mEmergencyContactNumber.getText().length() == PHONENUMBER_LENGTH) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).showTick();
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                }

            }
        });

        return changePhoneNumberView;

    }

    public void changePhoneNumberInfo() {
        if (response != null) {
            try {
                JSONObject responseDetail = new JSONObject(response);

                if (isEmpty(mPhoneNumber.getText().toString()) && isEmpty(mEmergencyContactNumber.getText().toString())) {

                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", responseDetail.getString("email"));
                    jsonObject.put("phone", mPhoneNumber.getText().toString().trim().replaceAll("[-() ]", ""));
                    Log.i("PhoneNumber", mPhoneNumber.getText().toString().trim().replaceAll("[-() ]", ""));
                    jsonObject.put("birthdate", responseDetail.getString("birthdate"));
                    jsonObject.put("state_id", responseDetail.getString("state"));
                    jsonObject.put("city", responseDetail.getString("city"));
                    jsonObject.put("zipcode", responseDetail.getString("zipcode"));
                    jsonObject.put("first_name", responseDetail.getString("first_name"));
                    jsonObject.put("address1", responseDetail.getString("address1"));
                    jsonObject.put("address2", responseDetail.getString("address2"));
                    jsonObject.put("gender", responseDetail.getString("gender"));
                    jsonObject.put("last_name", responseDetail.getString("last_name"));
                    jsonObject.put("emergency_contact_number", mEmergencyContactNumber.getText().toString().trim().replaceAll("[-() ]", ""));
                    Log.i("EmergencyContactNumber", mEmergencyContactNumber.getText().toString().trim().replaceAll("[-() ]", ""));
                    jsonObject.put("language_preference", "ko");

                    parent.put("member", jsonObject);
                    Log.i("Request",jsonObject.toString());
                    loadProfileInfo(parent.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo) {
        return !TextUtils.isEmpty(cardInfo);
    }

    public void loadProfileInfo(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleEditProfileInfoSuccessResponse(response);
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
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                }
            }
        };

        EditMyProfileService service = new EditMyProfileService(getActivity(), null);
        service.editMyProfile(successCallBackListener, errorListener, params);
    }

    private void handleEditProfileInfoSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();

            if (response != null) {
                Toast.makeText(getActivity(), "Update phone number successfully", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}