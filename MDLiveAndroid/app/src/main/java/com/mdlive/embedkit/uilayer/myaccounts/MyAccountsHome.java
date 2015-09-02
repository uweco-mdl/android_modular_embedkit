package com.mdlive.embedkit.uilayer.myaccounts;


import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by venkataraman_r on 7/26/2015.
 */
public class MyAccountsHome extends MDLiveBaseAppcompatActivity {

    String fragment,response;
    public static String TAG = "CHANGE SECURITY QUESTION";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccounts_home);

        fragment = getIntent().getStringExtra("Fragment_Name");
        TAG = fragment;


        try {
            Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView back = (ImageView) findViewById(R.id.backImg);
        TextView title = (TextView) findViewById(R.id.headerTxt);
        ImageView apply = (ImageView) findViewById(R.id.txtApply);


        if (fragment.equals("CHANGE PASSWORD")) {
            title.setText(getResources().getString(R.string.change_password).toUpperCase());

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, new ChangePasswordFragment(), "CHANGE PASSWORD").
                    commit();
        }

        if (fragment.equals("SECURITY QUESTION")) {
            title.setText(getResources().getString(R.string.change_security_questions).toUpperCase());

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, new SecurityQuestionsFragment(), "SECURITY QUESTION").
                    commit();
        }

        if(fragment.equals("CHANGE ADDRESS")){
            title.setText("CHANGE ADDRESS");
            response = getIntent().getStringExtra("Address_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, ChangeAddressFragment.newInstance(response), "CHANGE ADDRESS").
                    commit();
        }

        if(fragment.equals("CHANGE PHONE NUMBER")){
            title.setText("CHANGE PHONE NUMBER");
            response = getIntent().getStringExtra("Address_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, ChangePhoneNumber.newInstance(response), "CHANGE PHONE NUMBER").
                    commit();
        }

        if(fragment.equals("REPLACE CREDIT CARD")){
            title.setText("REPLACE CREDIT CARD");
            response = getIntent().getStringExtra("Credit_Card_Response");
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, CreditCardInfoFragment.newInstance(response,getIntent().getStringExtra("Credit_Card_View")), "REPLACE CREDIT CARD").
                    commit();
        }

        if(fragment.equals("Old Pin")){
            title.setText("CHANGE PIN");

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, OldPinFragment.newInstance(), "Old Pin").
                    commit();
        }

        if(fragment.equals("Add FAMILY MEMBER")){
            title.setText("Add FAMILY MEMBER");
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

    public void leftBtnOnClick(View v) {
        finish();
    }

    public void rightBtnOnClick(View v) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);

        if (fragment!= null && fragment instanceof ChangePasswordFragment) {
            ((ChangePasswordFragment) fragment).changePassword();
        }

        if (fragment!= null && fragment instanceof OldPinFragment) {
            ((OldPinFragment) fragment).oldPin();
        }

        if (fragment!= null && fragment instanceof MyAccountNewPinFragment) {
            ((MyAccountNewPinFragment) fragment).newPin();
        }

        if (fragment!= null && fragment instanceof ChangePinFragment) {
            ((ChangePinFragment) fragment).uploadChangePin();
        }

        if (fragment!= null && fragment instanceof ChangeAddressFragment) {
            ((ChangeAddressFragment) fragment).changeAddressInfo();
        }

        if (fragment!= null && fragment instanceof ChangePhoneNumber) {
            ((ChangePhoneNumber) fragment).changePhoneNumberInfo();
        }

        if (fragment!= null && fragment instanceof SecurityQuestionsFragment) {
            ((SecurityQuestionsFragment) fragment).uploadSecurityQuestions();
        }

        if (fragment!= null && fragment instanceof CreditCardInfoFragment) {
            ((CreditCardInfoFragment) fragment).addCreditCardInfo();
        }

        if (fragment!= null && fragment instanceof AddFamilyMemberFragment) {
            ((AddFamilyMemberFragment) fragment).addFamilyMemberInfo();
        }
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getDrawerLayout().closeDrawer(GravityCompat.START);
        getDrawerLayout().closeDrawer(GravityCompat.END);

        switch (position) {
            // Home
            case 0:
                onHomeClicked();
                break;

            // See a Doctor
            case 1:
                onSeeADoctorClicked();
                break;

            // MDLive My Health
            case 2:
                startActivityWithClassName(MedicalHistoryActivity.class);
                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:
                startActivityWithClassName(MessageCenterActivity.class);
                break;

            // Symptom Checker
            case 5:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 6:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 7:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share
            case 8:
                shareApplication();
                break;
        }
    }

    public void hideTick() {
        findViewById(R.id.txtApply).setVisibility(View.GONE);
    }
}