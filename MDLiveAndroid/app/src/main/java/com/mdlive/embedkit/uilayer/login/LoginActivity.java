package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mdlive.embedkit.R;

/**
 * Created by venkataraman_r on 7/22/2015.
 */

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginResponse,
        CreatePinFragment.OnCreatePinCompleted,
        ConfirmPinFragment.OnCreatePinSucessful {
    public static final String TAG = "LOGIN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, LoginFragment.newInstance(), TAG).
                    commit();
        }
    }

    public void joinNowAction(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.newInstance(), TAG).commit();
    }

    /* Start Of Click listeners for Login Fragment*/
    public void onForgotUserNameClicked(View view) {

    }

    public void onForgotPasswordClicked(View view) {

    }

    public void onSignInClicked(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if(fragment != null && fragment instanceof LoginFragment) {
            ((LoginFragment)fragment).loginService();
        }
    }

    public void onCreateFreeAccountClicked(View view) {

    }
    /* End Of Click listeners for Login Fragment*/

    @Override
    public void onLoginSucess() {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.container, CreatePinFragment.newInstance(), TAG).
                commit();
    }

    @Override
    public void onCreatePinCompleted(String pin) {
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, ConfirmPinFragment.newInstance(pin), TAG).
                commit();
    }


    @Override
    public void onCreatPinSucessful() {
        Intent dashboard = new Intent(getBaseContext(), DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }
}
