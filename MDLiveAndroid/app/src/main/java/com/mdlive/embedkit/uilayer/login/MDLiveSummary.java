package com.mdlive.embedkit.uilayer.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.SummaryService;

public class MDLiveSummary extends MDLiveBaseActivity {

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

        ((ImageView) findViewById(R.id.backImg)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_summary).toUpperCase());

        setProgressBar(findViewById(R.id.progressBar));
        TextView txtDocName=(TextView)findViewById(R.id.txtDoctorName);

        SharedPreferences amountPreferences =this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences docPreferences =this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String docName = docPreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES,"");
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(MDLiveSummary.this);
//        txtDocName.setText(docName);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(0);
        String shortDocName = docName.split(",")[0];
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.ques)).setText(
                getResources().getString(R.string.mdl_summary_michelle_txt,userBasicInfo.getPersonalInfo().getFirstName()));
           //Phone number from user info service for assistance
        ((TextView) findViewById(R.id.txt_phone_summary)).setText(userBasicInfo.getAssistPhoneNumber());
        ((TextView) findViewById(R.id.txt_phone_summary)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + StringConstants.ALERT_PHONENUMBER.replaceAll("-", "")));
                startActivity(intent);
                MdliveUtils.startActivityAnimation(MDLiveSummary.this);
            }
        });

    }

    public void questionBoxOnClick(View v){
        if(((LinearLayout) findViewById(R.id.questionContainer)).getVisibility() == View.VISIBLE){
            ((ImageView) findViewById(R.id.summary_down_arrow)).setImageResource(R.drawable.down_arrow_icon);
            ((LinearLayout) findViewById(R.id.questionContainer)).setVisibility(View.GONE);
        }else{
            ((ImageView) findViewById(R.id.summary_down_arrow)).setImageResource(R.drawable.right_arrow_icon);
            ((LinearLayout) findViewById(R.id.questionContainer)).setVisibility(View.VISIBLE);
        }
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
                MdliveUtils.handelVolleyErrorResponse(MDLiveSummary.this, error, getProgressDialog());
            }
        };
        SummaryService summaryService = new SummaryService(this, getProgressDialog());
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
                MdliveUtils.handelVolleyErrorResponse(MDLiveSummary.this, error, getProgressDialog());
            }
        };
        SummaryService summaryService = new SummaryService(this, getProgressDialog());
        summaryService.sendRating(rating,successListener,errorListner );
    }
}
