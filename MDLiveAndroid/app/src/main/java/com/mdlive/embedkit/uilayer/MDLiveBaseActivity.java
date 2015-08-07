package com.mdlive.embedkit.uilayer;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by srinivasan_ka on 7/8/2015.
 */
public class MDLiveBaseActivity extends AppCompatActivity {
    public static final String LEFT_MENU = "left_menu";
    public static final String RIGHT_MENU = "right_menu";

    public View progressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showProgress() {
        if(progressBarLayout!=null&&progressBarLayout.getVisibility()== View.GONE){
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(progressBarLayout!=null&&progressBarLayout.getVisibility()== View.VISIBLE){
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    /*
    * Method which clears the task & starts launcher activity
    * */
    public void movetohome(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.home_dialog_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.home_dialog_text));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                            MdliveUtils.ssoInstance.getparentClassname());
                    intent.setComponent(cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!MdliveUtils.isNetworkAvailable(this)){
           hideProgress();
        }

    }
    public void setProgressBar(View progressBarLayout){
        this.progressBarLayout=progressBarLayout;
    }

    public void startActivityWithClassName(final Class clazz) {
        startActivity(new Intent(getBaseContext(), clazz));
    }

    public void showMDLiveAssistDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            ImageView alertImage = (ImageView) view.findViewById(R.id.alertdialogimageview);
            alertImage.setImageResource(R.drawable.ic_launcher);
            TextView alertText = (TextView) view.findViewById(R.id.alertdialogtextview);
            alertText.setText(getText(R.string.call_text));

            builder.setView(view);
            builder.setPositiveButton(getText(R.string.call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
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
}
