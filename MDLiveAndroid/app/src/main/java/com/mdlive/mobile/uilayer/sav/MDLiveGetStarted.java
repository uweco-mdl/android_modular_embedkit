package com.mdlive.mobile.uilayer.sav;

import android.os.Bundle;
import android.widget.Button;

import com.mdlive.unifiedmiddleware.parentclasses.activity.sav.UMWGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.mobile.R;

/**
 * Created by unnikrishnan_b on 4/6/2015.
 */
public class MDLiveGetStarted extends UMWGetStarted {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.get_started);
        Utils.hideSoftKeyboard(this);
        setData(null,(Button)findViewById(R.id.SavContinueBtn)); // TODO : Replace the null when the next screen is ready
        super.onCreate(savedInstanceState);
    }
}
