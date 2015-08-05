package com.mdlive.embedkit.uilayer.lifestyle;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mdlive.embedkit.R;

public class MDLiveLifestyleActivity extends AppCompatActivity {
    private static final String TAG = "MDLIVE_LIFE_STYLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_lifestyle_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.mdlive_lifestyle_activity_container, MDLiveLifeStyleFragment.newInstance(), TAG).
                    commit();
        }
    }

    public void saveAction(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
                if(fragment != null && fragment instanceof MDLiveLifeStyleFragment) {
                    ((MDLiveLifeStyleFragment)fragment).lifeStyleSaveButtonEvent();
                }
    }
}
