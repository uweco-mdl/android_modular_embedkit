package com.mdlive.embedkit.uilayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.CreateAccountFragment;
import com.mdlive.embedkit.uilayer.login.CreateAccountFragment.OnSignupSuccess;
import com.mdlive.embedkit.uilayer.login.LoginActivity;
import com.mdlive.embedkit.uilayer.login.PinActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class UnlockActivity extends AppCompatActivity implements OnSignupSuccess, OnBackStackChangedListener {
    public static final String TAG = "UNLOCK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        clearMinimizedTime();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.main_container, UnlockFragment.newInstance()).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return ;
        } else {
            super.onBackPressed();
        }
    }

    private void clearMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
    }

    public void onSignUpClicked(View view) {
        getSupportActionBar().hide();

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.main_container, CreateAccountFragment.newInstance(), TAG).
                commit();
    }

    public void onForgotPinClicked(View view) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(AppSpecificConfig.URL_FORGOT_PASSWORD));
        startActivity(intent);
    }

    public void onSignInClicked(View view) {
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void onSignUpSucess() {
        final Intent intent = new Intent(getBaseContext(), PinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
