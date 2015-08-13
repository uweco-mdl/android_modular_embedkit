package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.LoginService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/15/2015.
 */

public class LoginFragment extends MDLiveBaseFragment{
    private OnLoginResponse mOnLoginResponse;

    private EditText mUserNameEditText = null;
    private EditText mPasswordEditText = null;

    public static LoginFragment newInstance() {
        final LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    public LoginFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnLoginResponse = (OnLoginResponse) activity;
        } catch (ClassCastException cce) {
            logE("Login Fragment", "Exception : " + cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserNameEditText = (EditText)view.findViewById(R.id.userName);
        mPasswordEditText = (EditText)view.findViewById(R.id.password);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnLoginResponse = null;
    }

    public void loginService() {
        final String userName = mUserNameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", userName);
                jsonObject.put("password", password);
                parent.put("login", jsonObject);
                loadLoginService(parent.toString());
            } catch (JSONException e) {
                logE("Error", e.getMessage());
            }

        } else {
            if (getActivity() != null) {
                MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.app_name), getActivity().getString(R.string.please_enter_mandetory_fileds));
            }
        }
    }

    private void loadLoginService(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Login Response", response.toString());
                handleChangePasswordSuccessResponse(response);
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

        logD("Login", params.toString());

        LoginService service = new LoginService(getActivity(), null);
        service.login(successCallBackListener, errorListener, params);
    }

    private void handleChangePasswordSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();

            if(response.getString("msg").equalsIgnoreCase("Success")) {
                logD("Login", "Login Sucessful : " + response.toString().trim());

                // For saving the device token
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Device_Token", response.getString("token"));
                editor.commit();

                // For saving the REMOTE USER ID
                sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                editor.commit();

                if (mOnLoginResponse != null) {
                    mOnLoginResponse.onLoginSucess();
                }
            }

            else {
                //Toast.makeText(getActivity(),"Login Failed",Toast.LENGTH_SHORT).show();
                MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.app_name), getActivity().getString(R.string.please_enter_mandetory_fileds));
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
        }
    }

    public interface OnLoginResponse {
        void onLoginSucess();
    }
}
