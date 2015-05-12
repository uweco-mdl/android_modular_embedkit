package com.mdlive.mobile.uilayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mdlive.mobile.uilayer.sav.MDLiveGetStarted;
import com.mdlive.mobile.R;

/**
 * Created by unnikrishnan_b on 4/3/2015.
 */
public class MDLiveDashboard extends Activity {

    LinearLayout savLl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        savLl=(LinearLayout)findViewById(R.id.SavLl);
        savLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });


    }
    private void onButtonClick(){
        //card io implementation

        Intent i = new Intent(getApplicationContext(),MDLiveGetStarted.class);
        startActivity(i);
    }
}
