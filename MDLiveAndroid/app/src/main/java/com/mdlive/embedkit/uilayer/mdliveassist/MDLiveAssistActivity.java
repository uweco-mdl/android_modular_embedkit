package com.mdlive.embedkit.uilayer.mdliveassist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mdlive.embedkit.R;

public class MDLiveAssistActivity extends AppCompatActivity {
    private static final String TAG = "MDLIVE_ASSIST";

    private static final String MEDICALHISTORY = "MedicalHistory";
    private static final String PHARMACY = "pharmacy";
    private static final String PROVIDERS = "Providers";
    private static final String VISITS = "Visits";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_assist_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.mdlive_assist_activity_container, MDLiveAssistFragment.newInstance(), TAG).
                    commit();
        }
    }

}
