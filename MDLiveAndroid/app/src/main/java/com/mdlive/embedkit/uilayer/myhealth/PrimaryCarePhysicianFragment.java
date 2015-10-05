package com.mdlive.embedkit.uilayer.myhealth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PrimaryCarePhysician;
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
    private static final String PRIMARY_CAR_PHYSICIAN_TAG = "PrimaryCarePhysician";

    private EditText mMiddleName = null;
    private EditText mEmail = null;
    private EditText mFirstName = null;
    private EditText mLastName = null;
    private EditText mAddress1 = null;
    private TextView mCountry = null;
    private RelativeLayout mCountryLayout = null;
    private EditText mZip = null;
    private EditText mPracticeName = null;
    private EditText mPhoneNumber = null;
    private TextView mState = null;
    private RelativeLayout mStateLayout = null;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private List<String> countryList = new ArrayList<String>();
    private boolean mayIallowtoEdit = true;

    public static PrimaryCarePhysicianFragment newInstance(final PrimaryCarePhysician primaryCarePhysician) {
        final Bundle args = new Bundle();
        args.putParcelable(PRIMARY_CAR_PHYSICIAN_TAG, primaryCarePhysician);

        final PrimaryCarePhysicianFragment fragment = new PrimaryCarePhysicianFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public PrimaryCarePhysicianFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View primaryCarePhysician = inflater.inflate(R.layout.activity_primary_care_physician, container, false);
        mZip = (EditText) primaryCarePhysician.findViewById(R.id.zip);
        mZip.setTag(null);
        mZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MdliveUtils.validateZipcodeFormat(mZip);
            }
        });
        return primaryCarePhysician;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMiddleName = (EditText) view.findViewById(R.id.middleName);
        if (mMiddleName != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mMiddleName.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).middleName);
        }

        mEmail = (EditText) view.findViewById(R.id.email);
        if (mEmail != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mEmail.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).email);
        }

        mFirstName = (EditText) view.findViewById(R.id.firstName);
        if (mFirstName != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mFirstName.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).firstName);
        }

        mLastName = (EditText) view.findViewById(R.id.lastName);
        if (mLastName != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mLastName.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).lastName);
        }

        mAddress1 = (EditText) view.findViewById(R.id.streetAddress);
        if (mAddress1 != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mAddress1.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).address1);
        }

        mCountry = (TextView) view.findViewById(R.id.country);
        mCountryLayout = (RelativeLayout) view.findViewById(R.id.countryLayout);
        if (mCountry != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mCountry.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).country);
        }
        mCountryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicked..", "Clicked");
                initializeCountryDialog();
            }
        });

        mState = (TextView) view.findViewById(R.id.state);
        mStateLayout = (RelativeLayout) view.findViewById(R.id.stateLayout);
        if (mState != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mState.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).state);
        }
        mStateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });
        mZip = (EditText) view.findViewById(R.id.zip);
        if (mZip != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mZip.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).zip);
        }

        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mayIallowtoEdit) {
                    mayIallowtoEdit=false;
                    mPhoneNumber.setText(MdliveUtils.formatDualString(s.toString()));
                    mPhoneNumber.setSelection(mPhoneNumber.getText().toString().length());
                    mayIallowtoEdit = true;
                }

            }
        });

        if (mPhoneNumber != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mPhoneNumber.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).phone);
        }

        mPracticeName = (EditText) view.findViewById(R.id.practiceName);
        if (mPracticeName != null && getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG) != null) {
            mPracticeName.setText(((PrimaryCarePhysician) getArguments().getParcelable(PRIMARY_CAR_PHYSICIAN_TAG)).practice);
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initializeCountryDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        countryList = Arrays.asList(getResources().getStringArray(R.array.mdl_countries_array));
        final String[] stringArray = countryList.toArray(new String[countryList.size()]);
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String SelectedText = countryList.get(i);
                mCountry.setText(SelectedText);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void uploadPCP() {

        if (isEmpty(mFirstName.getText().toString().trim()) &&
                isEmpty(mLastName.getText().toString().trim()) &&
                isEmpty(mState.getText().toString().trim()) &&
                isEmpty(mCountry.getText().toString().trim()) &&
                isEmpty(mPhoneNumber.getText().toString().trim())) {
            try {
                JSONObject parent = new JSONObject();

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("practice", mPracticeName.getText().toString());
                jsonObject.put("first_name", mFirstName.getText().toString());
                jsonObject.put("middle_name", mMiddleName.getText().toString());
                jsonObject.put("last_name", mLastName.getText().toString());
                jsonObject.put("email", mEmail.getText().toString());
                jsonObject.put("address1", mAddress1.getText().toString());
                jsonObject.put("country_id", countryList.indexOf(mCountry.getText().toString()) > 0 ? countryList.indexOf(mCountry.getText().toString()) + 1 : 1);
                jsonObject.put("state", mState.getText().toString());
                jsonObject.put("zip", mZip.getText().toString().replace("-", ""));
                jsonObject.put("phone", mPhoneNumber.getText().toString());

                parent.put("primary_care_physician", jsonObject);

                Log.i("params", parent.toString());
                addPCP(parent.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.mdl_please_enter_mandetory_fileds), Toast.LENGTH_SHORT).show();
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
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
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
                Toast.makeText(getActivity(), getString(R.string.mdl_pcp_added_succesfully), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
