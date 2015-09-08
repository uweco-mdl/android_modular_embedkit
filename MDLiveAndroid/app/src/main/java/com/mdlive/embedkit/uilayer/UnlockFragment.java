package com.mdlive.embedkit.uilayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.PinCreation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class UnlockFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {
    private static final String PIN_TAG = "PIN";

    private ToggleButton mPassCode1 = null;
    private ToggleButton mPassCode2 = null;
    private ToggleButton mPassCode3 = null;
    private ToggleButton mPassCode4 = null;
    private ToggleButton mPassCode5 = null;
    private ToggleButton mPassCode6 = null;

    private EditText mPassCode7 = null;

    public static UnlockFragment newInstance() {
        final UnlockFragment fragment = new UnlockFragment();
        return fragment;
    }

    public UnlockFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unlock, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    public void init(View changePin) {

        mPassCode1 = (ToggleButton) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) changePin.findViewById(R.id.passCode6);

        mPassCode7 = (EditText) changePin.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int iLength = mPassCode7.getText().length();
        switch (iLength) {
            case 0:
                mPassCode1.setChecked(false);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 1:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 2:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 3:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 4:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 5:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(false);
                break;
            case 6:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(true);
                break;
        }
        if (iLength == 6) {
            MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
            final String pin = mPassCode7.getText().toString();
            loadConfirmPin(pin);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
    }

    public void loadConfirmPin(final String confirmPin) {
        try {
            final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_token", preferences.getString("Device_Token", "0"));
            jsonObject.put("passcode", confirmPin);
            fetachPinWebserviceCall(jsonObject.toString());
        } catch (JSONException e) {
            logE("Error", e.getMessage());
        }
    }

    private void fetachPinWebserviceCall(String params) {
        MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleCreatePinSuccessResponse(response);
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

        PinCreation service = new PinCreation(getActivity(), null);
        service.createPin(successCallBackListener, errorListener, params);
    }

    private void handleCreatePinSuccessResponse(JSONObject response) {

        try {
            hideProgressDialog();

            if (response.getString("message").equalsIgnoreCase("Success")) {
                getActivity().finish();
            } else {
                showToast(R.string.mdl_pin_creation_failed);
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
        }
    }
}
