package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.EditMyProfileService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 8/21/2015.
 */

public class ChangeAddressFragment  extends Fragment {

    public static ChangeAddressFragment newInstance(String response) {

        final ChangeAddressFragment changeAddressFragment = new ChangeAddressFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Response", response);
        changeAddressFragment.setArguments(bundle);
        return changeAddressFragment;
    }

    private EditText mAddressLine1 = null;
    private EditText mAddressLine2 = null;
    private EditText mState = null;
    private EditText mZip = null;
    private EditText mCity = null;
    private String response;

    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changeAddressView = inflater.inflate(R.layout.fragment_change_address, null);

        mAddressLine1 = (EditText) changeAddressView.findViewById(R.id.addressLine1);
        mAddressLine2 = (EditText) changeAddressView.findViewById(R.id.addressLine2);
        mState = (EditText) changeAddressView.findViewById(R.id.state);
        mZip = (EditText) changeAddressView.findViewById(R.id.zip);
        mCity = (EditText) changeAddressView.findViewById(R.id.city);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        response = getArguments().getString("Response");

        if(response != null){
        try {
            JSONObject responseDetail = new JSONObject(response);

            mAddressLine1.setText(responseDetail.getString("address1"));
                if (MdliveUtils.checkJSONResponseHasString(responseDetail, "responseDetail")) {
                    mAddressLine2.setText("");
                }else
                {
                    mAddressLine2.setText(responseDetail.getString("address2"));
                }
            mState.setText(responseDetail.getString("state"));
            mCity.setText(responseDetail.getString("country"));
            mZip.setText(responseDetail.getString("zipcode"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        }
        return changeAddressView;

    }

    public void changeAddressInfo()
    {
        if(response != null){
            try {
                JSONObject responseDetail = new JSONObject(response);


                if (isEmpty(mAddressLine1.getText().toString()) && isEmpty(mAddressLine2.getText().toString()) && isEmpty(mState.getText().toString())
                        && isEmpty(mCity.getText().toString()) && isEmpty(mZip.getText().toString())) {

                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", responseDetail.getString("email"));
                    jsonObject.put("phone", "pin");
                    jsonObject.put("birthdate", responseDetail.getString("birthdate"));
                    jsonObject.put("state_id", mState.getText().toString().trim());
                    jsonObject.put("city", mCity.getText().toString().trim());
                    jsonObject.put("zipcode", mZip.getText().toString().trim());
                    jsonObject.put("first_name", responseDetail.getString("first_name"));
                    jsonObject.put("address1", mAddressLine1.getText().toString().trim());
                    jsonObject.put("address2", mAddressLine2.getText().toString().trim());
                    jsonObject.put("gender", responseDetail.getString("gender"));
                    jsonObject.put("last_name",  responseDetail.getString("last_name"));
                    jsonObject.put("emergency_contact_number", responseDetail.getString("emergency_contact_number"));
                    jsonObject.put("language_preference", "ko");

                    parent.put("member", jsonObject);
                    loadProfileInfo(parent.toString());
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
    public Boolean isEmpty(String cardInfo)
    {
        if(!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    public void loadProfileInfo(String params)
    {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleEditProfileInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                    }
                }
            }
        };

        EditMyProfileService service = new EditMyProfileService(getActivity(), null);
        service.editMyProfile(successCallBackListener, errorListener, params);
    }

    private void handleEditProfileInfoSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            if(response != null)
            {
                Toast.makeText(getActivity(),"Update address successfully",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}