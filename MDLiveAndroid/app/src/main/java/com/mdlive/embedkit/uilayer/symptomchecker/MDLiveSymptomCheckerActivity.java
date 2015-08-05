package com.mdlive.embedkit.uilayer.symptomchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mdlive.embedkit.R;

/**
 * This class provides to call Symptom checker fragment
 */
public class MDLiveSymptomCheckerActivity extends AppCompatActivity {
    private static final String TAG = "IFRAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_symptom_checker_activity);

        // call symptomchecker fragment
        if (savedInstanceState == null) {
            getFragmentManager().
                    beginTransaction().
                    add(R.id.mdliveiframelayout, MDLiveSymptomCheckerFragment.newInstance(), TAG).
                    commit();
        }

    }
}
