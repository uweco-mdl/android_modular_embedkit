package com.mdlive.embedkit.uilayer.login;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    //private RelativeLayout progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_summary);

        //progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        setProgressBar(findViewById(R.id.progressDialog));
        TextView payText=(TextView)findViewById(R.id.txtPaymentSummary);
        TextView txtDocName=(TextView)findViewById(R.id.txtDoctorName);

        SharedPreferences amountPreferences =this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences docPreferences =this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String docName = docPreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES,"");
        txtDocName.setText(docName);
        payText.setText("$"+amountPreferences.getString(PreferenceConstants.AMOUNT,"0.00"));
        pDialog = MdliveUtils.getProgressDialog("Please wait", this);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(0);
        String shortDocName = docName.split(",")[0];
        ((TextView)findViewById(R.id.NextStepsContentTv)).setText(java.text.MessageFormat.format(getResources().getString(R.string.next_steps_content_txt) ,new Object[]{shortDocName.substring(shortDocName.lastIndexOf(" ")+1)+"'"}));
        findViewById(R.id.DoneRatingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRating();
            }
        });
//        pDialog = Utils.getProgressDialog("Please wait", this);



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
    private void redirectToParent(){
        try{
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                    MdliveUtils.ssoInstance.getparentClassname());
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }catch (Exception e){
            Log.e("Execption Occured", e.getLocalizedMessage()+"");
        }

    }
    public void sendRating(){
//        pDialog.show();
        //progressBar.setVisibility(View.VISIBLE);
        showProgress();
        String rating = ((int)((RatingBar)findViewById(R.id.ratingBar)).getRating()) + "";
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
//                pDialog.dismiss();
                //progressBar.setVisibility(View.GONE);
                hideProgress();
                Log.e("Response Summary", response.toString());
                try {
                    clearPref();
                    clearCacheInVolley();
                } catch (Exception e) {
                    e.printStackTrace();
            }
                Intent intent = new Intent();
                ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                        MdliveUtils.ssoInstance.getparentClassname());
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
                //progressBar.setVisibility(View.GONE);
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveSummary.this, error, pDialog);
            }
        };
        SummaryService summaryService = new SummaryService(this, pDialog );
        summaryService.sendRating(rating,successListener,errorListner );
    }

    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        MdliveUtils.closingActivityAnimation(this);
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
//        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}
