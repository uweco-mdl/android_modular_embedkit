package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.SummaryService;

public class MDLiveSummary extends Activity {

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_summary);
        TextView payText=(TextView)findViewById(R.id.txtPaymentSummary);
        SharedPreferences amountPreferences =this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        payText.setText("$"+amountPreferences.getString(PreferenceConstants.AMOUNT,"0.00"));
        pDialog = Utils.getProgressDialog("Please wait", this);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(0);
        findViewById(R.id.DoneRatingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRating();
            }
        });
    }

    public void sendRating(){
        pDialog.show();
        String rating = ((int)((RatingBar)findViewById(R.id.ratingBar)).getRating()) + "";
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
                pDialog.dismiss();
                Log.e("Response Summary", response.toString());
                Intent i = new Intent(MDLiveSummary.this, MDLiveLogin.class);
                startActivity(i);
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Vsee", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {

                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(null, MDLiveSummary.this);
                    }
                } else {
                    Intent i = new Intent(MDLiveSummary.this, MDLiveLogin.class);
                    startActivity(i);
                }
            }
        };
        SummaryService summaryService = new SummaryService(this,pDialog );
        summaryService.sendRating(rating,successListener,errorListner );
    }

    @Override
    public void onBackPressed() {

    }
}
