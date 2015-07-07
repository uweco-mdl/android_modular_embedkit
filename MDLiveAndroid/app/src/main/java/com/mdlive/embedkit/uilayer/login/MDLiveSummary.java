package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.SummaryService;

public class MDLiveSummary extends Activity {

    private ProgressDialog pDialog;
    private RelativeLayout progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_summary);
//        pDialog = Utils.getProgressDialog("Please wait", this);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        ratingBar.setRating(0);
        findViewById(R.id.DoneRatingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRating();
            }
        });
    }

    /**
     * This function is for clear preference datas from app.
     * This is will reduce size of memory of app.
     */

    public void clearPref(){
        Utils.clearSharedPrefValues(this);
    }

    /**
     * This function is for clear preference datas from app.
     * This is will reduce size of memory of app.
     */

    public void clearCacheInVolley(){
        ApplicationController.getInstance().getRequestQueue(MDLiveSummary.this).getCache().clear();
        ApplicationController.getInstance().getBitmapLruCache().evictAll();
    }

    public void sendRating(){
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        String rating = ((int)((RatingBar)findViewById(R.id.ratingBar)).getRating()) + "";
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                Log.e("Response Summary", response.toString());
                try {
                    clearPref();
                    clearCacheInVolley();
                } catch (Exception e) {
                    e.printStackTrace();
            }
                Intent intent = new Intent();
                ComponentName cn = new ComponentName(Utils.ssoInstance.getparentPackagename(),
                        Utils.ssoInstance.getparentClassname());
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };

        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Vsee", error.toString());
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
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
