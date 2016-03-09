package com.mdlive.myaccounts;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.myaccounts.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by venkataraman_r on 7/26/2015.
 */
public class MyAccountsHome extends MDLiveBaseAppcompatActivity {
    private Handler mHandler;

    String fragment, response;
    public static String TAG = "CHANGE SECURITY QUESTION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccounts_home);
        clearMinimizedTime();

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        fragment = getIntent().getStringExtra("Fragment_Name");
        TAG = fragment;


        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView back = (ImageView) findViewById(R.id.backImg);
        TextView title = (TextView) findViewById(R.id.headerTxt);
        ImageView apply = (ImageView) findViewById(R.id.txtApply);


        if (fragment.equals("CHANGE PASSWORD")) {
            title.setText(getResources().getString(R.string.mdl_change_password).toUpperCase());

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, new ChangePasswordFragment(), "CHANGE PASSWORD").
                    commit();
        }

        if (fragment.equals("SECURITY QUESTION")) {
            title.setText(getResources().getString(R.string.mdl_security_questions_txt).toUpperCase());
            response = getIntent().getStringExtra("Security_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, SecurityQuestionsFragment.newInstance(response), "SECURITY QUESTION").
                    commit();
        }

        if (fragment.equals("CHANGE ADDRESS")) {
            title.setText("CURRENT ADDRESS");
            response = getIntent().getStringExtra("Address_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, ChangeAddressFragment.newInstance(response), "CHANGE ADDRESS").
                    commit();
        }

        if (fragment.equals("CHANGE PHONE NUMBER")) {
            title.setText("PHONE NUMBER");
            response = getIntent().getStringExtra("Address_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, ChangePhoneNumber.newInstance(response), "CHANGE PHONE NUMBER").
                    commit();
        }

        if (fragment.equals("REPLACE CREDIT CARD")) {
            if (getIntent().getStringExtra("Fragment_Name1").equals("ADD CREDIT CARD")) {
                title.setText(getString(R.string.mdl_add_card).toUpperCase());
            } else if (getIntent().getStringExtra("Fragment_Name1").equals("VIEW CREDIT CARD")) {
                title.setText(getString(R.string.mdl_view_card).toUpperCase());
            } else {
                title.setText(getString(R.string.mdl_replace_card).toUpperCase());
            }
            response = getIntent().getStringExtra("Credit_Card_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, CreditCardInfoFragment.newInstance(response, getIntent().getStringExtra("Credit_Card_View")), "REPLACE CREDIT CARD").
                    commit();
        }

        if (fragment.equals("Old Pin")) {
            title.setText("CHANGE PIN");

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, OldPinFragment.newInstance(false), "Old Pin").
                    commit();
        }

        if (fragment.equals("Old Pin Second")) {
            title.setText("CHANGE PIN");

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, MyAccountNewPinFragment.newInstance("", true), "Old Pin").
                    commit();
        }

        if (fragment.equals("Add FAMILY MEMBER")) {
            title.setText("ADD FAMILY MEMBER");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, AddFamilyMemberFragment.newInstance(), "Add FAMILY MEMBER").
                    commit();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void leftBtnOnClick(View v) {
        finish();

    }

    public void rightBtnOnClick(View v) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);

        if (fragment != null && fragment instanceof ChangePasswordFragment) {
            ((ChangePasswordFragment) fragment).changePassword();
        }

        if (fragment != null && fragment instanceof OldPinFragment) {
            ((OldPinFragment) fragment).oldPin();
        }

        if (fragment != null && fragment instanceof MyAccountNewPinFragment) {
            ((MyAccountNewPinFragment) fragment).newPin();
        }

        if (fragment != null && fragment instanceof ChangePinFragment) {
            ((ChangePinFragment) fragment).uploadChangePin();
        }

        if (fragment != null && fragment instanceof ChangeAddressFragment) {
            ((ChangeAddressFragment) fragment).changeAddressInfo();
        }

        if (fragment != null && fragment instanceof ChangePhoneNumber) {
            ((ChangePhoneNumber) fragment).changePhoneNumberInfo();
        }

        if (fragment != null && fragment instanceof SecurityQuestionsFragment) {
            ((SecurityQuestionsFragment) fragment).uploadSecurityQuestions();
        }

        if (fragment != null && fragment instanceof CreditCardInfoFragment) {
            ((CreditCardInfoFragment) fragment).callHpci();//Calling Hpci to validate the card details.

        }

        if (fragment != null && fragment instanceof AddFamilyMemberFragment) {
            ((AddFamilyMemberFragment) fragment).addFamilyMemberInfo();
        }
    }

    public void hideTick() {
        findViewById(R.id.txtApply).setVisibility(View.INVISIBLE);
    }

    public void showTick() {
        findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
    }

    public void clearMinimizedTime() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Log.d("Timer", "clear called");
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IdConstants.CREDITCARD_SCAN) {
            String resultStr;
            if (intent != null && intent.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT) && resultCode != Activity.RESULT_CANCELED) {
                CreditCard scanResult = intent.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                resultStr = scanResult.cardNumber;
                android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
                if(fragment!=null && fragment instanceof CreditCardInfoFragment){
                    ((CreditCardInfoFragment) fragment).setCardNumber(resultStr);
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, intent);
    }
}