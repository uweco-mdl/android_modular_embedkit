package com.mdlive.embedkit.uilayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by srinivasan_ka on 7/8/2015.
 */
public class MDLiveBaseActivity extends Activity {
    public View progressBarLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.status_bar_color);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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

    public void movetohome() {

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


}
