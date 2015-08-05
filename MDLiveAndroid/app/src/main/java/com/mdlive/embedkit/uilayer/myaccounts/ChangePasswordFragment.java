package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangePasswordService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/17/2015.
 */
public class ChangePasswordFragment extends Fragment{

    private EditText mCurrentPassword = null;
    private EditText mNewPassword = null;
    private EditText mConfirmPassword = null;
    private Button mSave = null;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View changePasswordView = inflater.inflate(R.layout.fragments_change_password, null);

        mCurrentPassword = (EditText)changePasswordView.findViewById(R.id.currentPassword);
        mNewPassword = (EditText)changePasswordView.findViewById(R.id.newPassword);
        mConfirmPassword = (EditText)changePasswordView.findViewById(R.id.confirmPassword);
        mSave = (Button)changePasswordView.findViewById(R.id.save);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.change_password));

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = mNewPassword.getText().toString();
                String currentPassword = mCurrentPassword.getText().toString();
                String confirmPasssword = mConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(newPassword)&& !TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(confirmPasssword)) {

                    if(newPassword.length() > 7 && newPassword.length() < 16) {
                        if(newPassword.matches("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]*$")) {
                            if(newPassword.equals(confirmPasssword)) {
                                try {
                                    JSONObject parent = new JSONObject();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("password", newPassword);
                                    jsonObject.put("current_password", currentPassword);
                                    jsonObject.put("password_confirmation", confirmPasssword);
                                    parent.put("user", jsonObject);
                                    loadChangePasswordService(parent.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getActivity(),"Mismatch confirmation password",Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getActivity(),"Must contain atleast 1 letter and 1 character",Toast.LENGTH_SHORT).show();
                    }

                    else
                        Toast.makeText(getActivity(),"Must contain 8 to 15 characters",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(),"All fileds are mandatory",Toast.LENGTH_SHORT).show();

            }
        });

        return changePasswordView;
    }
    private void loadChangePasswordService(String params) {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleChangePasswordSuccessResponse(response);
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
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                    }
                }
            }
        };

        ChangePasswordService service = new ChangePasswordService(getActivity(), null);
        service.changePassword(successCallBackListener, errorListener, params);
    }

    private void handleChangePasswordSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            Toast.makeText(getActivity(),response.getString("message"),Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
