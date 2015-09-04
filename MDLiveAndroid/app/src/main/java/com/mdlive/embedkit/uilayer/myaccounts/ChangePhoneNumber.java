package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
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

    private String response;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePhoneNumberView = inflater.inflate(R.layout.fragment_change_phonenumber, null);

        mPhoneNumber = (EditText) changePhoneNumberView.findViewById(R.id.phoneNumber);
        mEmergencyContactNumber = (EditText) changePhoneNumberView.findViewById(R.id.emergencyContactNumber);

        response = getArguments().getString("Response");

        if (response != null) {
            try {
                JSONObject responseDetail = new JSONObject(response);
                if (MdliveUtils.checkJSONResponseHasString(responseDetail, "phone")) {
                    mPhoneNumber.setText(responseDetail.getString(""));
                } else {
                    mPhoneNumber.setText(responseDetail.getString("phone"));
                }
                if (MdliveUtils.checkJSONResponseHasString(responseDetail, "emergency_contact_number")) {
                    mEmergencyContactNumber.setText(responseDetail.getString(""));
                } else {
                    mEmergencyContactNumber.setText(responseDetail.getString("emergency_contact_number"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    jsonObject.put("phone", mPhoneNumber.getText().toString().trim());
                    jsonObject.put("birthdate", responseDetail.getString("birthdate"));
                    jsonObject.put("state_id", responseDetail.getString("state"));
                    jsonObject.put("city", responseDetail.getString("country"));
                    jsonObject.put("zipcode", responseDetail.getString("zipcode"));
                    jsonObject.put("first_name", responseDetail.getString("first_name"));
                    jsonObject.put("address1", responseDetail.getString("address1"));
                    jsonObject.put("address2", responseDetail.getString("address2"));
                    jsonObject.put("gender", responseDetail.getString("gender"));
                    jsonObject.put("last_name", responseDetail.getString("last_name"));
                    jsonObject.put("emergency_contact_number", mEmergencyContactNumber.getText().toString().trim());
                    jsonObject.put("language_preference", "ko");

                    parent.put("member", jsonObject);
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
        if (!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
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