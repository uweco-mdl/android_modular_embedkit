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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.PinCreation;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangePinService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/24/2015.
 */
public class ChangePinFragment extends MDLiveBaseFragment implements TextWatcher {

    private ToggleButton mPassCode1 = null;
    private ToggleButton mPassCode2 = null;
    private ToggleButton mPassCode3 = null;
    private ToggleButton mPassCode4 = null;
    private ToggleButton mPassCode5 = null;
    private ToggleButton mPassCode6 = null;

    private EditText mPassCode7 = null;

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private View mButtonCross;

    private TextView mTitle;

    private StringBuffer mStringBuffer;


    public static ChangePinFragment newInstance(String newPin,String oldPin, final boolean create) {

        final ChangePinFragment changePinFragment = new ChangePinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("OldPin",oldPin);
        bundle.putString("NewPin",newPin);
        bundle.putBoolean("update", create);
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
        mStringBuffer = new StringBuffer();

        mPassCode1 = (ToggleButton) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) changePin.findViewById(R.id.passCode6);

        mPassCode7 = (EditText) changePin.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();

        mTitle = (TextView) changePin.findViewById(R.id.title);

        mTitle.setText("Please enter your PIN");

               mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();

        mButton0 = (Button) changePin.findViewById(R.id.num_pad_0);
        if (mButton0 != null) {
            mButton0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton0.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton1 = (Button) changePin.findViewById(R.id.num_pad_1);
        if (mButton1 != null) {
            mButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton1.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton2 = (Button) changePin.findViewById(R.id.num_pad_2);
        if (mButton2 != null) {
            mButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton2.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton3 = (Button) changePin.findViewById(R.id.num_pad_3);
        if (mButton3 != null) {
            mButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton3.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton4 = (Button) changePin.findViewById(R.id.num_pad_4);
        if (mButton4 != null) {
            mButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton4.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton5 = (Button) changePin.findViewById(R.id.num_pad_5);
        if (mButton5 != null) {
            mButton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton5.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton6 = (Button) changePin.findViewById(R.id.num_pad_6);
        if (mButton6 != null) {
            mButton6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton6.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton7 = (Button) changePin.findViewById(R.id.num_pad_7);
        if (mButton7 != null) {
            mButton7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton7.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton8 = (Button) changePin.findViewById(R.id.num_pad_8);
        if (mButton8 != null) {
            mButton8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton8.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton9 = (Button) changePin.findViewById(R.id.num_pad_9);
        if (mButton9 != null) {
            mButton9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton9.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButtonCross = changePin.findViewById(R.id.num_pad_cross);
        if (mButtonCross != null) {
            mButtonCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logD("Text", "length :" + mPassCode7.getText().toString().length());
                    if (mStringBuffer.length() > 0) {
                        mStringBuffer.deleteCharAt(mStringBuffer.length() - 1);
                        logD("Text", "After -1 :" + mStringBuffer.toString());
                    }
                    mPassCode7.setText(mStringBuffer.toString());
                    mPassCode7.invalidate();
                }
            });
        }

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

        if (mPassCode7.getText().length() < 6) {
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                ((MyAccountsHome) getActivity()).hideTick();
            }
        }
        else{
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                //((MyAccountsHome) getActivity()).showTick();
                uploadChangePin();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

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

            MdliveUtils.setLockType(getActivity(), getActivity().getString(R.string.mdl_pin));
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
        boolean update = getArguments().getBoolean("update");

        String confirmPin = mPassCode7.getText().toString();
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(confirmPin.equals(newPin)){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("passcode", oldPin);
                jsonObject.put("new_passcode", confirmPin);
                jsonObject.put("device_token", sharedPref.getString("Device_Token", "0") );
                Log.i("params", jsonObject.toString());
                if (update) {
                    loadConfirmPin(confirmPin);
                } else {
                    loadPinService(jsonObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_app_name), getString(R.string.mdl_pin_mismatch));
        }
    }

    public void loadConfirmPin(final String confirmPin) {
        try {
            //final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_token", MdliveUtils.getDeviceToken(getActivity()));
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
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        PinCreation service = new PinCreation(getActivity(), null);
        service.createPin(successCallBackListener, errorListener, params);
    }
}
