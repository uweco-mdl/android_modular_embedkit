package com.mdlive.embedkit.uilayer.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/25/2015.
 */
public class EmailConfirmFragment extends MDLiveBaseFragment {
    private Button resendEmail = null;
    private Button dismiss = null;


    public EmailConfirmFragment() {
        super();
    }

    public static EmailConfirmFragment newInstance() {
        final EmailConfirmFragment emailConfirmFragment = new EmailConfirmFragment();
        return emailConfirmFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resendEmail = (Button) view.findViewById(R.id.resend_email);
        dismiss = (Button) view.findViewById(R.id.dismiss);

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadEmailConfirmationService();
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadEmailConfirmationService() {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
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

        EmailConfirmationService service = new EmailConfirmationService(getActivity(), null);
        service.emailConfirmation(successCallBackListener, errorListener, null);
    }

    private void handleSuccessResponse(JSONObject response) {

        try {
            hideProgressDialog();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getActivity().getString(R.string.mdl_app_name));
            builder.setMessage(response.getString("message"));

            builder.setPositiveButton(getActivity().getString(R.string.mdl_ok_upper), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    getActivity().onBackPressed();
                }

            });


            AlertDialog alert = builder.create();
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
