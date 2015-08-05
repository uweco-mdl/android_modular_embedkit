package com.mdlive.embedkit.uilayer.helpandsupport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.helpandsupport.HelpandSupportAskQuestionPostService;

import org.json.JSONObject;

import java.util.HashMap;

public class MDLiveHelpAndSupportActivity extends AppCompatActivity {
    private static final String TAG = "HELP_SUPPORT";

//    Tracker mTracker;
//    GoogleAnalytics analytics;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;
    JSONObject outerJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_help_and_support_activity);


        if (savedInstanceState == null) {
            getFragmentManager().
                    beginTransaction().
                    add(R.id.mdlive_help_support_activity_container, HelpAndSupportFragment.newInstance(), TAG).
                    commit();
        }

//        analytics = GoogleAnalytics.getInstance(MDLiveHelpAndSupportActivity.this);
//        mTracker = analytics.newTracker(R.string.ga_trackingId);
//        mTracker.enableExceptionReporting(true);
//        mTracker.setScreenName("MDLiveHelpAndSupportActivity");
//        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();

//        mTracker.set("MDLiveHelpAndSupportActivity", "MDLiveHelpAndSupportActivity");
//        mTracker.send(new HitBuilders.AppViewBuilder().build());
//        analytics.reportActivityStart(this);
//        GoogleAnalytics.getInstance(MDLiveHelpAndSupportActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        GoogleAnalytics.getInstance(MDLiveHelpAndSupportActivity.this).reportActivityStop(this);
    }

    public void callAction(View view) {
        callButtonEvent();
    }

    public void askQuestion(View view) {
        getFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                replace(R.id.mdlive_help_support_activity_container, AskQuestionFragment.newInstance(), TAG).
                commit();
    }

    private void callButtonEvent() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            ImageView alertImage = (ImageView)view.findViewById(R.id.alertdialogimageview);
            alertImage.setImageResource(R.drawable.ic_launcher);
            TextView alertText = (TextView)view.findViewById(R.id.alertdialogtextview);
            alertText.setText(getText(R.string.call_text));

            builder.setView(view);
            builder.setPositiveButton(getText(R.string.call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + getText(R.string.callnumber)));
                                startActivity(intent);

                            } catch (Exception e) {

                            }
                        }
                    });
            builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        dialog.dismiss();

                    } catch (Exception e) {

                    }

                }
            });

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void submitAction(View view) {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof  AskQuestionFragment) {
            ((AskQuestionFragment) fragment).submitTexViewEvent();
        }
    }

    public void cancelAction(View view) {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof  AskQuestionFragment) {
            ((AskQuestionFragment) fragment).cancelTextViewEvent();
        }
    }

}
