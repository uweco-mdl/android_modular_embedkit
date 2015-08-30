package com.mdlive.embedkit.uilayer.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.SummaryService;

public class MDLiveSummary extends MDLiveBaseActivity {
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_summary);
        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.exit_icon);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.thankyou_string).toUpperCase());

        setProgressBar(findViewById(R.id.progressBar));
        TextView payText=(TextView)findViewById(R.id.txtPaymentSummary);
        TextView txtDocName=(TextView)findViewById(R.id.txtDoctorName);

        SharedPreferences amountPreferences =this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences docPreferences =this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String docName = docPreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES,"");
        txtDocName.setText(docName);
        payText.setText(getString(R.string.dollar)+amountPreferences.getString(PreferenceConstants.AMOUNT,"0.00"));
        pDialog = MdliveUtils.getProgressDialog(getString(R.string.please_wait), this);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(0);
        String shortDocName = docName.split(",")[0];
        ((TextView)findViewById(R.id.NextStepsContentTv)).setText(java.text.MessageFormat.format(getResources().getString(R.string.next_steps_content_txt) ,new String[]{shortDocName.substring(shortDocName.lastIndexOf(" ")+1)+"'"}));
    }

    public void leftBtnOnClick(View v){
    }

    public void rightBtnOnClick(View view){
        showProgress();
        String rating = ((int)((RatingBar)findViewById(R.id.ratingBar)).getRating()) + "";
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                onHomeClicked();
            }
        };

        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveSummary.this, error, pDialog);
            }
        };
        SummaryService summaryService = new SummaryService(this, pDialog );
        summaryService.sendRating(rating,successListener,errorListner );
    }

    /**
     * This function is for clear preference datas from app.
     * This is will reduce size of memory of app.
     */
    public void clearPref(){
        MdliveUtils.clearSharedPrefValues(this);
    }

    /**
     * This function is for clear preference datas from app.
     * This is will reduce size of memory of app.
     */

    public void clearCacheInVolley(){
        ApplicationController.getInstance().getRequestQueue(MDLiveSummary.this).getCache().clear();
        ApplicationController.getInstance().getBitmapLruCache().evictAll();
    }

    public void sendRating(View view){
        showProgress();
        String rating = ((int)((RatingBar)findViewById(R.id.ratingBar)).getRating()) + "";
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                onHomeClicked();
            }
        };

        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveSummary.this, error, pDialog);
            }
        };
        SummaryService summaryService = new SummaryService(this, pDialog );
        summaryService.sendRating(rating,successListener,errorListner );
    }
}
