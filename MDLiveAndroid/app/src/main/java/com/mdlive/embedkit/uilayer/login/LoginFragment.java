package com.mdlive.embedkit.uilayer.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
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

public class LoginFragment extends Fragment{
    public static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT";

    private EditText edt_UserName = null;
    private EditText edt_Password = null;
    //private RadioButton radb_RememberMe = null;
    //Toolbar toolbar;
    //private TextView toolbarTitle;
    private ProgressDialog pDialog;

    public static LoginFragment newInstance() {
        final LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    public LoginFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View login = inflater.inflate(R.layout.fragment_login,null);

        edt_UserName = (EditText)login.findViewById(R.id.userName);
        edt_Password = (EditText)login.findViewById(R.id.password);
        //radb_RememberMe = (RadioButton)login.findViewById(R.id.rememberMe);
        //toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());
        //toolbarTitle.setText(getResources().getString(R.string.sign_up));

        return login;
    }

    public void loginService() {
        String userName = edt_UserName.getText().toString();
        String password = edt_Password.getText().toString();
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", userName);
                jsonObject.put("password", password);
                parent.put("login", jsonObject);
                loadLoginService(parent.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error", e.getMessage());
            }

        } else {
            Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLoginService(String params) {

        pDialog.show();

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

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };

        Log.d("Login", params.toString());

        LoginService service = new LoginService(getActivity(), null);
        service.login(successCallBackListener, errorListener, params);
    }

    private void handleChangePasswordSuccessResponse(JSONObject response) {

        try {
            pDialog.dismiss();

            if(response.getString("msg").equalsIgnoreCase("Success")) {

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

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, CreatePinFragment.newInstance()).commit();
            }

            else {
                Toast.makeText(getActivity(),"Login Failed",Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
