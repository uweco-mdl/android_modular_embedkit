package com.mdlive.embedkit.uilayer.myaccounts;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.mdlive.embedkit.R;

/**
 * Created by sanjibkumar_p on 7/27/2015.
 */
public class MyAccountActivity extends AppCompatActivity implements FragmentTabHost.OnTabChangeListener {

    private static final String MY_ACCOUNT_TAG = "Account";
    private static final String BILLING_TAG = "Billing";
    private static final String FAMILY_TAG = "Family";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_activity_myaccounts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.my_account));

        final FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        if (tabHost != null) {
            tabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);

            tabHost.addTab(createTabSpec(tabHost.newTabSpec(MY_ACCOUNT_TAG), MY_ACCOUNT_TAG, R.drawable.icon_i), MyProfileFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(BILLING_TAG), BILLING_TAG, R.drawable.icon_i), ViewCreditCard.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(FAMILY_TAG), FAMILY_TAG, R.drawable.icon_i), GetFamilyMemberFragment.class, null);

            tabHost.setOnTabChangedListener(this);
        }
    }

    @Override
    public void onTabChanged(String s) {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private TabHost.TabSpec createTabSpec(final TabHost.TabSpec spec, final String text, @DrawableRes final int imageResourceId) {
        final View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.myaccounts_tab_indicator, null);

        final TextView tv = (TextView) v.findViewById(R.id.TabTextView);
        final ImageView img = (ImageView) v.findViewById(R.id.TabImageView);

        tv.setText(text);
        img.setBackgroundResource(imageResourceId);

        return spec.setIndicator(v);
    }

    public void onChangePasswordClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.tabcontent, new ChangePasswordFragment(), MY_ACCOUNT_TAG).
                addToBackStack(MY_ACCOUNT_TAG).
                commit();
    }

    public void onChangePinClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.tabcontent, OldPinFragment.newInstance(), MY_ACCOUNT_TAG).
                addToBackStack(MY_ACCOUNT_TAG).
                commit();
    }

    public void onSecurityQuestionClicked() {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.tabcontent, new SecurityQuestionsFragment(), MY_ACCOUNT_TAG).
                addToBackStack(MY_ACCOUNT_TAG).
                commit();
    }
}
