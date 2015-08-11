package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.PinCreation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/23/2015.
 */
public class ConfirmPinFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {
    private static final String PIN_TAG = "PIN";

    private OnCreatePinSucessful mOnCreatePinSucessful;

    public SharedPreferences sharedPref;
    private EditText mPassCode1 = null;
    private EditText mPassCode2 = null;
    private EditText mPassCode3 = null;
    private EditText mPassCode4 = null;
    private EditText mPassCode5 = null;
    private EditText mPassCode6 = null;
    private EditText mPassCode7 = null;
    private TextView mTitle = null;
    private View dummyEditText1 = null;
    private View dummyEditText2 = null;
    private View dummyEditText3 = null;
    private View dummyEditText4 = null;
    private View dummyEditText5 = null;
    private View dummyEditText6 = null;

    public static ConfirmPinFragment newInstance(String createPin) {
        final Bundle bundle = new Bundle();
        bundle.putString(PIN_TAG, createPin);

        final ConfirmPinFragment confirmPinFragment = new ConfirmPinFragment();
        confirmPinFragment.setArguments(bundle);
        return confirmPinFragment;
    }

    public ConfirmPinFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCreatePinSucessful = (OnCreatePinSucessful) activity;
        } catch (ClassCastException cce) {
            logE("Error", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_change_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCreatePinSucessful = null;
    }

    public void init(View changePin) {

        mPassCode1 = (EditText) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (EditText) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (EditText) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (EditText) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (EditText) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (EditText) changePin.findViewById(R.id.passCode6);
        mPassCode7 = (EditText) changePin.findViewById(R.id.dumy_field_passcode);

        dummyEditText1 = (View) changePin.findViewById(R.id.dumy_passcode_field_1);
        dummyEditText2 = (View) changePin.findViewById(R.id.dumy_passcode_field_2);
        dummyEditText3 = (View) changePin.findViewById(R.id.dumy_passcode_field_3);
        dummyEditText4 = (View) changePin.findViewById(R.id.dumy_passcode_field_4);
        dummyEditText5 = (View) changePin.findViewById(R.id.dumy_passcode_field_5);
        dummyEditText6 = (View) changePin.findViewById(R.id.dumy_passcode_field_6);
        mTitle = (TextView) changePin.findViewById(R.id.title);


        mTitle.setText(changePin.getContext().getString(R.string.please_confirm_pin));

        mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();
        setFocus(mPassCode1, dummyEditText1);

        dummyEditText1.setOnClickListener(this);
        dummyEditText2.setOnClickListener(this);
        dummyEditText3.setOnClickListener(this);
        dummyEditText4.setOnClickListener(this);
        dummyEditText5.setOnClickListener(this);
        dummyEditText6.setOnClickListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() <= 6) {
            if (count != 0) {
                String text = s.charAt(s.length() - 1) + "";
                switch (s.length()) {

                    case 1:
                        mPassCode1.setText(text);
                        setFocus(mPassCode2, dummyEditText2);
                        break;
                    case 2:
                        mPassCode2.setText(text);
                        setFocus(mPassCode3, dummyEditText3);
                        break;
                    case 3:
                        mPassCode3.setText(text);
                        setFocus(mPassCode4, dummyEditText4);
                        break;
                    case 4:
                        mPassCode4.setText(text);
                        setFocus(mPassCode5, dummyEditText5);
                        break;
                    case 5:
                        mPassCode5.setText(text);
                        setFocus(mPassCode6, dummyEditText6);
                        break;
                    case 6:
                        mPassCode6.setText(text);

                        String createPin = getArguments().getString(PIN_TAG);
                        String confirmPin = mPassCode7.getText().toString();
                        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                        if (createPin.equals(confirmPin)) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("device_token", sharedPref.getString("Device_Token", "0"));
                                jsonObject.put("passcode", confirmPin);
                                loadPinService(jsonObject.toString());
                            } catch (JSONException e) {
                                logE("Error", e.getMessage());
                            }
                        } else {
                            showToast(R.string.pin_mismatch);
                        }

                        break;
                }
            } else {
                switch (s.length() + 1) {
                    case 1:
                        mPassCode1.setText("");
                        setFocus(mPassCode1, dummyEditText1);
                        break;
                    case 2:
                        mPassCode2.setText("");
                        setFocus(mPassCode2, dummyEditText2);
                        break;
                    case 3:
                        mPassCode3.setText("");
                        setFocus(mPassCode3, dummyEditText3);
                        break;
                    case 4:
                        mPassCode4.setText("");
                        setFocus(mPassCode4, dummyEditText4);
                        break;
                    case 5:
                        mPassCode5.setText("");
                        setFocus(mPassCode5, dummyEditText5);
                        break;
                    case 6:
                        mPassCode6.setText("");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setFocus(EditText editText, View dummyEditText) {

        dummyEditText1.setClickable(false);
        mPassCode1.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText2.setClickable(false);
        mPassCode2.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText3.setClickable(false);
        mPassCode3.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText4.setClickable(false);
        mPassCode4.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText5.setClickable(false);
        mPassCode5.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText6.setClickable(false);
        mPassCode6.setBackgroundResource(R.drawable.edittext_lostfocus);

        if (editText != null) {
            editText.setBackgroundResource(R.drawable.edittext_focus);
            dummyEditText.setClickable(true);
            dummyEditText.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
    }

    private void loadPinService(String params) {
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
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
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
                if (mOnCreatePinSucessful != null) {
                    mOnCreatePinSucessful.startDashboard();
                }
            } else {
                showToast(R.string.pin_creation_failed);
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
        }
    }

    public interface OnCreatePinSucessful {
        void startDashboard();
    }
}
