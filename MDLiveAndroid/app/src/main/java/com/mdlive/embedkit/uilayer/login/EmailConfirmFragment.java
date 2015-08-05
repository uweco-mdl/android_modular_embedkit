package com.mdlive.embedkit.uilayer.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/25/2015.
 */
public class EmailConfirmFragment extends Fragment
{
    Toolbar toolbar;
    private TextView toolbarTitle;
    private ProgressDialog pDialog;

    public static EmailConfirmFragment newInstance() {
        final EmailConfirmFragment emailConfirmFragment = new EmailConfirmFragment();
        return emailConfirmFragment;
    }

    public EmailConfirmFragment() {
        super();
    }


    private Button resendEmail = null;
    private Button dismiss = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View emailConfirmation = inflater.inflate(R.layout.fragment_email_confirmation,null);

        resendEmail = (Button)emailConfirmation.findViewById(R.id.resend_email);
        dismiss= (Button)emailConfirmation.findViewById(R.id.dismiss);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());
        toolbarTitle.setText(getResources().getString(R.string.email_confirmation));

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

        return emailConfirmation;
    }

    private void loadEmailConfirmationService() {

        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
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

        EmailConfirmationService service = new EmailConfirmationService(getActivity(), null);
        service.emailConfirmation(successCallBackListener, errorListener, null);
    }

    private void handleSuccessResponse(JSONObject response) {

        try {
            pDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("MDLive");
            builder.setMessage(response.getString("message"));

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

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
