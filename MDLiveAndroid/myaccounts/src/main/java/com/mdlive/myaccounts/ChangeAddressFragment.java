package com.mdlive.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.myaccounts.R;
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

public class ChangeAddressFragment extends MDLiveBaseFragment {

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
    private String mtimeZone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changeAddressView = inflater.inflate(R.layout.fragment_change_address, null);
        getActivity().setTitle(getString(R.string.mdl_current_address_caps));

        mAddressLine1 = (EditText) changeAddressView.findViewById(R.id.addressLine1);
        mAddressLine2 = (EditText) changeAddressView.findViewById(R.id.addressLine2);
        mState = (TextView) changeAddressView.findViewById(R.id.state);
        mZip = (EditText) changeAddressView.findViewById(R.id.zip);
        mCity = (EditText) changeAddressView.findViewById(R.id.city);
        mAddressLine1.addTextChangedListener(addressLine1);
        mAddressLine2.addTextChangedListener(addressLine2);
        mState.addTextChangedListener(stateWatcher);
        mCity.addTextChangedListener(cityWatcher);
        mZip.addTextChangedListener(zipWatcher);
        RelativeLayout mStateLayout = (RelativeLayout)changeAddressView.findViewById(R.id.stateLayout);

        mZip.setTag(null);
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

                if (MdliveUtils.checkIsEmpty(responseDetail.getString("address1"))) {
                    mAddressLine1.setText("");
                } else {
                    mAddressLine1.setText(responseDetail.getString("address1"));
                }

                if (MdliveUtils.checkIsEmpty(responseDetail.getString("address2"))) {
                    mAddressLine2.setText("");
                } else {
                    mAddressLine2.setText(responseDetail.getString("address2"));
                }

                if (MdliveUtils.checkIsEmpty(responseDetail.getString("state"))) {
                    mState.setText("");
                } else {
                    mState.setText(responseDetail.getString("state"));
                }

                if (MdliveUtils.checkIsEmpty(responseDetail.getString("city"))) {
                    mCity.setText("");
                } else {
                    mCity.setText(responseDetail.getString("city"));
                }

                if (MdliveUtils.checkIsEmpty(responseDetail.getString("zipcode"))) {
                    mZip.setText("");
                } else {
                    mZip.setText(responseDetail.getString("zipcode"));
                }

                if(MdliveUtils.checkIsEmpty(responseDetail.getString("timezone"))){
                    mtimeZone = responseDetail.getString("timezone");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((MyAccountsHome) getActivity()).hideTick();
        return changeAddressView;
    }


    public void changeAddressInfo()
    {
        if(response != null){
            try {
                JSONObject responseDetail = new JSONObject(response);
                String getEditTextValue = mZip.getText().toString();
                if (isEmpty(mAddressLine1.getText().toString().trim()) && isEmpty(mState.getText().toString().trim())
                        && isEmpty(mCity.getText().toString().trim()) && isEmpty(mZip.getText().toString().trim())) {

                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", responseDetail.getString("email"));
                    jsonObject.put("phone", responseDetail.getString("phone"));
                    jsonObject.put("birthdate", responseDetail.getString("birthdate"));
                    jsonObject.put("state_id", mState.getText().toString().trim());
                    jsonObject.put("city", mCity.getText().toString().trim());
                    jsonObject.put("zip", mZip.getText().toString().trim());
                    jsonObject.put("first_name", responseDetail.getString("first_name"));
                    jsonObject.put("address1", mAddressLine1.getText().toString().trim());
                    jsonObject.put("address2", mAddressLine2.getText().toString().trim());
                    jsonObject.put("gender", responseDetail.getString("gender"));
                    jsonObject.put("last_name", responseDetail.getString("last_name"));
                    jsonObject.put("emergency_contact_number", responseDetail.getString("emergency_contact_number"));
                    jsonObject.put("language_preference", "en");
                    if(mtimeZone != null) {
                        jsonObject.put("timezone", mtimeZone);
                    }

                    parent.put("member", jsonObject);
                    Log.i("request:", jsonObject.toString());
                    String errorMessage = ValidateForm();
                    if(errorMessage == null){
                        loadProfileInfo(parent.toString());
                    }else{
                        /*Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();*/
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                errorMessage,
                                Snackbar.LENGTH_SHORT).show();
                    }

                }else
                {
                    /*Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();*/
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getString(R.string.mdl_all_fields_required),
                            Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    TextWatcher stateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAddressFields();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    TextWatcher cityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAddressFields();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher addressLine2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAddressFields();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher addressLine1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAddressFields();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher zipWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAddressFields();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void checkAddressFields() {
        if (!TextUtils.isEmpty(mAddressLine1.getText().toString().trim())
                && !TextUtils.isEmpty(mCity.getText().toString().trim()) && !TextUtils.isEmpty(mState.getText().toString().trim()) && !TextUtils.isEmpty(mZip.getText().toString().trim())) {
            ((MyAccountsHome) getActivity()).showTick();
        } else {
            ((MyAccountsHome) getActivity()).hideTick();
        }
    }

    /**
     * Perform a sanity check on the input fields; make sure all mandatory fields are populated.
     *
     * @return  an error message if sanity check fails, else nothing.
     */
    public String ValidateForm()
    {
        if(mAddressLine1.getText() == null && mAddressLine1.getText().toString().trim().length() == 0
    //     || mAddressLine2.getText() == null && mAddressLine2.getText().toString().trim().length() == 0
           || mState.getText() == null && mState.getText().toString().trim().length() == 0
           || mZip.getText() == null && mZip.getText().toString().trim().length() == 0
           || mCity.getText() == null && mCity.getText().toString().trim().length() == 0) {
            return (getString(R.string.mdl_please_enter));
        }
        else if(!MdliveUtils.validateZipCode(mZip.getText().toString()))
            return(getString(R.string.mdl_enter_valid_zip));

        return null;
    }

    public Boolean isEmpty(String cardInfo)
    {
        return !TextUtils.isEmpty(cardInfo);
    }

    public void loadProfileInfo(String params)
    {
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

            if(response != null)
            {
                /*Toast.makeText(getActivity(),getString(R.string.mdl_address_updated),Toast.LENGTH_SHORT).show();*/
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        getString(R.string.mdl_address_updated),
                        Snackbar.LENGTH_SHORT).show();
                getActivity().finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));

        final String[] stringArray = stateList.toArray(new String[stateList.size()]);

        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String SelectedText = stateIds.get(i);
                mState.setText(SelectedText);
                mState.setContentDescription(getString(R.string.mdl_ada_dropdown)+SelectedText);
                if(MyProfileFragment.timeZoneByStateValue!=null){
                    try{
                        JSONObject stateTimezoneObj = new JSONObject(MyProfileFragment.timeZoneByStateValue);
                        mtimeZone = stateTimezoneObj.getString(stateIds.get(i));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}