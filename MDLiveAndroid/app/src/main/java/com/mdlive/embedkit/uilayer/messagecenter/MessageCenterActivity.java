package com.mdlive.embedkit.uilayer.messagecenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.MessageCenterViewPagerAdapter;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageCenterActivity extends MDLiveBaseAppcompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_tab_activity);
        setTitle("");

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.message_center).toUpperCase());
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

    private void setupViewPager(ViewPager viewPager) {
        final MessageCenterViewPagerAdapter adapter = new MessageCenterViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MessageReceivedFragment.newInstance(), getString(R.string.inbox));
        adapter.addFragment(MessageSentFragment.newInstance(), getString(R.string.sent));
        adapter.addFragment(MessageProviderFragment.newInstance(), getString(R.string.compose));
        viewPager.setAdapter(adapter);
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
                startActivityWithClassName(MDLiveDashboardActivity.class);
                break;

            // See a Doctor
            case 1:
                startActivityWithClassName(MDLiveGetStarted.class);
                break;

            // MDLive My Health
            case 2:

                break;

            // MDLIVE Assist
            case 3:
                MdliveUtils.showMDLiveAssistDialog(this);
                break;

            // Message Center
            case 4:

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

                break;
        }
    }

    public void onReceivedMessageClicked(final ReceivedMessage receivedMessage) {
        startActivity(MessageCenterInboxDetailsActivity.getMessageDetailsIntent(getBaseContext(), receivedMessage));
    }

    public void onSentMessageClicked(final SentMessage sentMessage) {
        startActivity(MessageCenterInboxDetailsActivity.getMessageDetailsIntent(getBaseContext(), sentMessage));
    }

    public void onMyProviderClicked(final MyProvider myProvider) {
        startActivity(MessageCenterComposeActivity.getMessageComposeDetailsIntent(getBaseContext(), myProvider));
    }

}
