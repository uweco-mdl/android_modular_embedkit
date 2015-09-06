package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangePinService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/24/2015.
 */
public class ChangePinFragment extends MDLiveBaseFragment implements TextWatcher,View.OnClickListener{

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


    public static ChangePinFragment newInstance(String newPin,String oldPin) {

        final ChangePinFragment changePinFragment = new ChangePinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("OldPin",oldPin);
        bundle.putString("NewPin",newPin);
        changePinFragment.setArguments(bundle);
        return changePinFragment;
    }
    public ChangePinFragment(){ super(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePin = inflater.inflate(R.layout.fragments_pin_change, null);

        init(changePin);

        return changePin;
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

        dummyEditText1.setClickable(true);
        mPassCode1.setBackgroundResource(R.drawable.gray_circle);
        dummyEditText2.setClickable(false);
        mPassCode2.setBackgroundResource(R.drawable.gray_circle);
        dummyEditText3.setClickable(false);
        mPassCode3.setBackgroundResource(R.drawable.gray_circle);
        dummyEditText4.setClickable(false);
        mPassCode4.setBackgroundResource(R.drawable.gray_circle);
        dummyEditText5.setClickable(false);
        mPassCode5.setBackgroundResource(R.drawable.gray_circle);
        dummyEditText6.setClickable(false);
        mPassCode6.setBackgroundResource(R.drawable.gray_circle);
        mTitle.setText("Please enter your PIN");

        mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();

        dummyEditText1.setOnClickListener(this);
        dummyEditText2.setOnClickListener(this);
        dummyEditText3.setOnClickListener(this);
        dummyEditText4.setOnClickListener(this);
        dummyEditText5.setOnClickListener(this);
        dummyEditText6.setOnClickListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        if (mPassCode7.getText().length() < 6) {
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                ((MyAccountsHome) getActivity()).hideTick();
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() <= 6) {
            if (count != 0) {
                String text = s.charAt(s.length() - 1) + "";
                switch (s.length()) {

                    case 1:
                        mPassCode1.setText(text);
                        setFocus(mPassCode1, dummyEditText2);
                        break;
                    case 2:
                        mPassCode2.setText(text);
                        setFocus(mPassCode2, dummyEditText3);
                        break;
                    case 3:
                        mPassCode3.setText(text);
                        setFocus(mPassCode3, dummyEditText4);
                        break;
                    case 4:
                        mPassCode4.setText(text);
                        setFocus(mPassCode4, dummyEditText5);
                        break;
                    case 5:
                        mPassCode5.setText(text);
                        setFocus(mPassCode5, dummyEditText6);
                        break;
                    case 6:
                        mPassCode6.setText(text);
                        mPassCode6.setBackgroundResource(R.drawable.red_circle);
                        break;
                }
            } else {
                switch (s.length() + 1) {

                    case 1:
                        mPassCode1.setText("");
                        setFocus1(mPassCode1, dummyEditText1);
                        break;
                    case 2:
                        mPassCode2.setText("");
                        setFocus1(mPassCode2, dummyEditText2);
                        break;
                    case 3:
                        mPassCode3.setText("");
                        setFocus1(mPassCode3, dummyEditText3);
                        break;
                    case 4:
                        mPassCode4.setText("");
                        setFocus1(mPassCode4, dummyEditText4);
                        break;
                    case 5:
                        mPassCode5.setText("");
                        setFocus1(mPassCode5, dummyEditText5);
                        break;
                    case 6:
                        mPassCode6.setText("");
                        setFocus1(mPassCode6, dummyEditText6);
                        break;
                    default:
                        break;
                }
            }
        }

        if (mPassCode7.getText().length() < 6) {
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                ((MyAccountsHome) getActivity()).hideTick();
            }
        }
        else{
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                ((MyAccountsHome) getActivity()).showTick();
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
        dummyEditText2.setClickable(false);
        dummyEditText3.setClickable(false);
        dummyEditText4.setClickable(false);
        dummyEditText5.setClickable(false);
        dummyEditText6.setClickable(false);

        if (editText != null) {
            editText.setBackgroundResource(R.drawable.red_circle);
            dummyEditText.setClickable(true);
        }
    }

    public void setFocus1(EditText editText, View dummyEditText) {

        dummyEditText1.setClickable(false);
        dummyEditText2.setClickable(false);
        dummyEditText3.setClickable(false);
        dummyEditText4.setClickable(false);
        dummyEditText5.setClickable(false);
        dummyEditText6.setClickable(false);

        if (editText != null) {
            editText.setBackgroundResource(R.drawable.gray_circle);
            dummyEditText.setClickable(true);
        }
    }


    @Override
    public void onClick(View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mPassCode7, InputMethodManager.SHOW_IMPLICIT);
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
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }

            }
        };

        ChangePinService service = new ChangePinService(getActivity(), null);
        service.changePin(successCallBackListener, errorListener, params);
    }

    private void handleCreatePinSuccessResponse(JSONObject response) {

        try {
            hideProgressDialog();

            Toast.makeText(getActivity(),"Pin Changed Successfully",Toast.LENGTH_SHORT).show();
            getActivity().finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadChangePin()
    {
        String oldPin = getArguments().getString("OldPin");
        String newPin = getArguments().getString("NewPin");
        String confirmPin = mPassCode7.getText().toString();
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(confirmPin.equals(newPin)){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("passcode", oldPin);
                jsonObject.put("new_passcode", confirmPin);
                jsonObject.put("device_token", sharedPref.getString("Device_Token", "0") );
                Log.i("params", jsonObject.toString());
                loadPinService(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getActivity(), "create pin and confirm pin must be same", Toast.LENGTH_SHORT).show();
        }
    }

}
