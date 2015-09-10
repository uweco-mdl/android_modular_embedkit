package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private TextView mState = null;
    private EditText mZip = null;
    private EditText mCity = null;
    private String response;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private ProgressDialog pDialog;
    private RelativeLayout mStateLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changeAddressView = inflater.inflate(R.layout.fragment_change_address, null);

        mAddressLine1 = (EditText) changeAddressView.findViewById(R.id.addressLine1);
        mAddressLine2 = (EditText) changeAddressView.findViewById(R.id.addressLine2);
        mState = (TextView) changeAddressView.findViewById(R.id.state);
        mZip = (EditText) changeAddressView.findViewById(R.id.zip);
        mCity = (EditText) changeAddressView.findViewById(R.id.city);
        mStateLayout = (RelativeLayout)changeAddressView.findViewById(R.id.stateLayout);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        response = getArguments().getString("Response");

        mStateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });

        if (response != null) {
            try {
                JSONObject responseDetail = new JSONObject(response);

                if ((responseDetail.getString("address1").equalsIgnoreCase("null")) || (responseDetail.getString("address1") == null)  || (TextUtils.isEmpty(responseDetail.getString("address1") )) ||((responseDetail.getString("address1").trim().length()) == 0)) {
                    mAddressLine1.setText("");
                } else {
                    mAddressLine1.setText(responseDetail.getString("address1"));
                }

                if ((responseDetail.getString("address2").equalsIgnoreCase("null")) || (responseDetail.getString("address2") == null)  || (TextUtils.isEmpty(responseDetail.getString("address2") )) ||((responseDetail.getString("address2").trim().length()) == 0)) {
                    mAddressLine2.setText("");
                } else {
                    mAddressLine2.setText(responseDetail.getString("address2"));
                }

                if ((responseDetail.getString("state").equalsIgnoreCase("null")) || (responseDetail.getString("state") == null)  || (TextUtils.isEmpty(responseDetail.getString("state") )) ||((responseDetail.getString("state").trim().length()) == 0)) {
                    mState.setText("");
                } else {
                    mState.setText(responseDetail.getString("state"));
                }

                if ((responseDetail.getString("city").equalsIgnoreCase("null")) || (responseDetail.getString("city") == null)  || (TextUtils.isEmpty(responseDetail.getString("city") )) ||((responseDetail.getString("city").trim().length()) == 0)) {
                    mCity.setText("");
                } else {
                    mCity.setText(responseDetail.getString("city"));
                }

                if ((responseDetail.getString("zipcode").equalsIgnoreCase("null")) || (responseDetail.getString("zipcode") == null)  || (TextUtils.isEmpty(responseDetail.getString("zipcode") )) ||((responseDetail.getString("zipcode").trim().length()) == 0)) {
                    mZip.setText("");
                } else {
                    mZip.setText(responseDetail.getString("zipcode"));
                }


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

                if (isEmpty(mAddressLine1.getText().toString().trim())&& isEmpty(mState.getText().toString().trim())
                        && isEmpty(mCity.getText().toString().trim()) && isEmpty(mZip.getText().toString().trim())) {

                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", responseDetail.getString("email"));
                    jsonObject.put("phone", responseDetail.getString("phone"));
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
                    Log.i("request:", jsonObject.toString());
                    loadProfileInfo(parent.toString());
                }

                else
                {
                    Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
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
}