package com.mdlive.sav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

public class MDLiveDoctorOnCall extends MDLiveBaseActivity {
    private LinearLayout  byvideoBtnLayout, byphoneBtnLayout;
    private TextView  byvideoBtn,byphoneBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdlive_doctor_on_call);
        initializeView();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.txtApply).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_first_available_doc).toUpperCase());
    }
    public void leftBtnOnClick(View v) {
        MdliveUtils.hideSoftKeyboard(MDLiveDoctorOnCall.this);
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveDoctorOnCall.this);
    }

    public void initializeView(){
        byvideoBtnLayout  = (LinearLayout)findViewById(R.id.byvideoBtnLayout);
        byphoneBtnLayout = (LinearLayout)findViewById(R.id.byphoneBtnLayout);
        byvideoBtn = (TextView)findViewById(R.id.byvideoBtn);
        byphoneBtn = (TextView)findViewById(R.id.byphoneBtn);

        Log.d("Doc On call", "" + MDLiveChooseProvider.isDoctorOnCall);
        Log.d("Doc On Video", "" + MDLiveChooseProvider.isDoctorOnVideo);


        byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDoctorName("Doctor On Call");//Doctor name Needs to be saved as Doctor on Call in case of user doing consultation on this flow.
                saveConsultationType("Phone");//Method need to save the consultation type which will be getting reflected in confirm appointment screen.
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                byphoneBtn.setTextColor(Color.WHITE);


                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                byvideoBtn.setTextColor(Color.GRAY);
                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);

                MDLiveChooseProvider.isDoctorOnVideo = false;
                MDLiveChooseProvider.isDoctorOnCall = true;
                Intent reasonForIntent = new Intent(MDLiveDoctorOnCall.this, MDLiveReasonForVisit.class);
                startActivity(reasonForIntent);
            }
        });
        byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDoctorName("Doctor On Call");//Doctor name Needs to be saved as Doctor on Call in case of user doing consultation on this flow.
                saveConsultationType("Video");//Method need to save the consultation type which will be getting reflected in confirm appointment screen.
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                byvideoBtn.setTextColor(Color.WHITE);


                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                byphoneBtn.setTextColor(Color.GRAY);
                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);


                MDLiveChooseProvider.isDoctorOnVideo = true;
                MDLiveChooseProvider.isDoctorOnCall = false;
                Intent reasonForIntent = new Intent(MDLiveDoctorOnCall.this, MDLiveReasonForVisit.class);
                startActivity(reasonForIntent);
            }
        });

        if(MDLiveChooseProvider.isDoctorOnCall && MDLiveChooseProvider.isDoctorOnVideo ){
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
            ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
            byphoneBtn.setTextColor(Color.WHITE);
            byphoneBtnLayout.setClickable(true);

            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
            ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
            byvideoBtn.setTextColor(Color.WHITE);
            byvideoBtnLayout.setClickable(true);
        }else if(MDLiveChooseProvider.isDoctorOnCall){
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
            ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
            byphoneBtn.setTextColor(Color.WHITE);
            byphoneBtnLayout.setClickable(true);

            byvideoBtnLayout.setClickable(false);
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            byvideoBtn.setTextColor(Color.GRAY);
            ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);

        }else if(MDLiveChooseProvider.isDoctorOnVideo){
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
            ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
            byvideoBtn.setTextColor(Color.WHITE);
            byvideoBtnLayout.setClickable(true);

            byphoneBtnLayout.setClickable(false);
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            byphoneBtn.setTextColor(Color.GRAY);
            ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);

        }else{
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);
            byphoneBtnLayout.setClickable(false);
            byphoneBtn.setTextColor(Color.GRAY);

            byvideoBtnLayout.setClickable(false);
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);
            byvideoBtn.setTextColor(Color.GRAY);
        }



    }

    public void saveConsultationType(String consultationType){
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(consultationType != null){
            editor.putString(PreferenceConstants.CONSULTATION_TYPE,consultationType);
        }
        editor.commit();
    }

    public void saveDoctorName(String docName){
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, docName);
        editor.commit();
    }








}
