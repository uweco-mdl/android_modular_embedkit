package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mdlive.embedkit.R;


/**
 * Created by sudha_s on 6/1/2015.
 */
public class MDLiveAddFamilymember extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_add_familymember);
        ((LinearLayout) findViewById(R.id.AddFamilyLl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MDLiveAddFamilymember.this,MDLiveFamilymember.class);
                startActivity(intent);
            }
        });
    }
}
