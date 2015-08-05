package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mdlive.embedkit.R;

public class MDLiveBehaviouralHealthActivity extends AppCompatActivity {
    private static final String TAG = "CONTAINER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_behavioural_health_activity);

        if (savedInstanceState == null) {
            getFragmentManager().
                    beginTransaction().
                    add(R.id.mdlive_behavioural_health_activity_container, MDLiveBehaviouralHealthFragment.newInstance(), TAG).
                    commit();
        }
    }

    public void saveAction(View view) {
        Toast.makeText(this, "Save Clicked", Toast.LENGTH_LONG).show();
    }
}
