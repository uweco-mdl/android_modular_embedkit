package com.mdlive.embedkit.uilayer.myhealth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddPCP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by venkataraman_r on 8/25/2015.
 */
public class PrimaryCarePhysicianFragment extends MDLiveBaseFragment {

    private EditText mMiddleName = null;
    private EditText mEmail = null;
    private EditText mFirstName = null;
    private EditText mLastName = null;
    private EditText mAddress1 = null;
    private EditText mCountry = null;
    private EditText mZip = null;
    private EditText mPracticeName = null;
    private EditText mPhoneNumber = null;
    private TextView mState = null;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View primaryCarePhysician = inflater.inflate(R.layout.activity_primary_care_physician, null);

        init(primaryCarePhysician);

        mState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });

        return primaryCarePhysician;
    }

    public void init(View primaryCarePhysician) {
        mMiddleName = (EditText) primaryCarePhysician.findViewById(R.id.middleName);
        mEmail = (EditText) primaryCarePhysician.findViewById(R.id.email);
        mFirstName = (EditText) primaryCarePhysician.findViewById(R.id.firstName);
        mLastName = (EditText) primaryCarePhysician.findViewById(R.id.lastName);
        mAddress1 = (EditText) primaryCarePhysician.findViewById(R.id.streetAddress);
        mCountry = (EditText) primaryCarePhysician.findViewById(R.id.country);
        mState = (TextView) primaryCarePhysician.findViewById(R.id.state);
        mZip = (EditText) primaryCarePhysician.findViewById(R.id.zip);
        mPhoneNumber = (EditText) primaryCarePhysician.findViewById(R.id.phoneNumber);
        mPracticeName = (EditText) primaryCarePhysician.findViewById(R.id.practiceName);
    }


    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        stateList = Arrays.asList(getResources().getStringArray(R.array.stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.stateCode));

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

    public void uploadPCP() {

        if (isEmpty(mFirstName.getText().toString()) && isEmpty(mFirstName.getText().toString()) && isEmpty(mFirstName.getText().toString()) && isEmpty(mFirstName.getText().toString())) {
            try {
                JSONObject parent = new JSONObject();

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("practice", mPracticeName.getText().toString());
                jsonObject.put("first_name", mFirstName.getText().toString());
                jsonObject.put("middle_name", mMiddleName.getText().toString());
                jsonObject.put("last_name", mLastName.getText().toString());
                jsonObject.put("email", mEmail.getText().toString());
                jsonObject.put("address1", mAddress1.getText().toString());
                jsonObject.put("country_id", mCountry.getText().toString());
                jsonObject.put("state", mState.getText().toString());
                jsonObject.put("zip", mZip.getText().toString());
                jsonObject.put("phone", mPhoneNumber.getText().toString());

                parent.put("primary_care_physician", jsonObject);

                Log.i("params", parent.toString());
                addPCP(parent.toString());

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

    private void addPCP(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddPCPSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        AddPCP service = new AddPCP(getActivity(), null);
        service.addPCPInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddPCPSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();

            if(response != null) {
                Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
