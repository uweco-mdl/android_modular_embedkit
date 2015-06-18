package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mdlive.embedkit.R;

/**
 * Created by sudha_s on 6/8/2015.
 */
public class MDLiveJoinNow extends Activity {
    private Button joinNwBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_joinnow);
        joinNwBtn=  (Button) findViewById(R.id.joinnwBtn);
        joinNwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MDLiveJoinNow.this,MDLiveLogin.class);
                startActivity(intent);
            }
        });
    }
}
