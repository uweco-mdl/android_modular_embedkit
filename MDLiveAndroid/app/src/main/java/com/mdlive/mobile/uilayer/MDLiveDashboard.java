package com.mdlive.mobile.uilayer;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.mdlive.mobile.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.parentclasses.activity.UMWDashboard;
import com.mdlive.mobile.R;

/**
 * Created by unnikrishnan_b on 4/3/2015.
 */
public class MDLiveDashboard extends UMWDashboard {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dashboard);
        setData(MDLiveGetStarted.class,(LinearLayout)findViewById(R.id.SavLl));
        super.onCreate(savedInstanceState);
    }
}
