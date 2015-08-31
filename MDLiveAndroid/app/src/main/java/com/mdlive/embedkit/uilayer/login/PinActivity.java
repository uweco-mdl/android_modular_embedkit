package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/21/2015.
 */
public class PinActivity extends AppCompatActivity implements CreatePinFragment.OnCreatePinCompleted,
        ConfirmPinFragment.OnCreatePinSucessful, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        setTitle("");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            showPinToolbar();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, CreatePinFragment.newInstance(), TAG).
                    commit();
        }
    }

    @Override
    public void onCreatePinCompleted(String pin) {
        showConfirmPinToolbar();

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, ConfirmPinFragment.newInstance(pin), TAG).
                commit();
    }

    @Override
    public void onClickNoPin() {
        startDashboard();
    }

    @Override
    public void startDashboard() {
        MdliveUtils.setLockType(getBaseContext(), "password");

        final Intent dashboard = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
        startActivity(dashboard);
        finish();
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            showPinToolbar();
        } else {
            showConfirmPinToolbar();
        }
    }

    public void onBackClicked(View view) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void onTickClicked(View view) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            String pin = null;

            if (fragment instanceof  CreatePinFragment) {
                pin = ((CreatePinFragment) fragment).getEnteredPin();
                if (pin != null && pin.length() == 6) {
                    onCreatePinCompleted(pin);
                }
            } else if (fragment instanceof  ConfirmPinFragment) {
                pin = ((ConfirmPinFragment) fragment).getEnteredPin();
                if (pin != null && pin.length() == 6) {
                    ((ConfirmPinFragment) fragment).loadConfirmPin(pin);
                }
            }
        }
    }

    private void showPinToolbar() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.create_a_pin).toUpperCase());
    }

    private void showConfirmPinToolbar() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.confirm_your_pin).toUpperCase());
    }
}
