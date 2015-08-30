package com.mdlive.embedkit.uilayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.CreateAccountFragment;
import com.mdlive.embedkit.uilayer.login.CreateAccountFragment.OnSignupSuccess;
import com.mdlive.embedkit.uilayer.login.ForgotPinFragment;
import com.mdlive.embedkit.uilayer.login.LoginActivity;
import com.mdlive.embedkit.uilayer.login.PinActivity;
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
        showInitialToolbar();

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

    public void onBackClicked(View view) {
        onBackPressed();
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
        showForgotPinToolbar();

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.main_container, ForgotPinFragment.newInstance(), TAG).
                commit();
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
            showInitialToolbar();
        }
    }

    @Override
    public void onSignUpSucess() {
        final Intent intent = new Intent(getBaseContext(), PinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onResetPinClicked(View view) {
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showInitialToolbar() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.window_background_color));
        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.sign_up).setVisibility(View.VISIBLE);
        findViewById(R.id.forgot_pin).setVisibility(View.VISIBLE);
        findViewById(R.id.headerTxt).setVisibility(View.GONE);

    }

    private void showForgotPinToolbar() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.red_theme_primary));
        findViewById(R.id.toolbar_cross).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_up).setVisibility(View.GONE);
        findViewById(R.id.forgot_pin).setVisibility(View.GONE);
        findViewById(R.id.headerTxt).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.forgot_pin).toUpperCase());
    }
}
