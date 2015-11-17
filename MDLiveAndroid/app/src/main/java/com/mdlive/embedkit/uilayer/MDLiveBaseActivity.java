package com.mdlive.embedkit.uilayer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * This class to parent class to all activities used in MDLive.
 * Class extends with FragmentActivity to enable all fragment activities
 * Basic functions used in all activities are
 *   showProgress() - show progress bar used in activity
 *   hideProgress() - hide progress bar used in activity
 */
public class MDLiveBaseActivity extends MDLiveBaseAppcompatActivity {
    public static final String LEFT_MENU = "left_menu";
    public static final String RIGHT_MENU = "right_menu";

    public View progressBarLayout;
    private ProgressDialog mProgressDialog=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LocalizationSingleton.localiseLayout(this, (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));
    }

    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }

    /**
     * Show progressbar that is used in activity when service calls.
     */
    public void showProgress() {
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        mProgressDialog.show();
    }

    /**
     * Hide progressbar that is used in activity when service call ends.
     */
    public void hideProgress() {
        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }

    /*
    * Method which clears the task & starts launcher activity
    * */
    public void movetohome(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.mdl_home_dialog_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.mdl_home_dialog_text));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.mdl_ok_upper), new DialogInterface.OnClickListener() {
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
        alertDialog.setNegativeButton(getString(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
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

    /**
     * Hide progress bar if already showing in resume state of activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!MdliveUtils.isNetworkAvailable(this)){
           hideProgress();
        }
    }

    public void setProgressBar(View progressBarLayout){
        this.progressBarLayout=progressBarLayout;
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }
}
