package com.mdlive.messages.messagecenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.messages.messagecenter.adapter.MessageCenterViewPagerAdapter;
import com.mdlive.messages.R;
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
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_message_center));

        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.toolbar_text_view)).setText(getString(R.string.mdl_message_center).toUpperCase());
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

    @Override
    public void onResume() {
        super.onResume();

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null) {
            ((NavigationDrawerFragment) fragment).reload();
        }
    }

    @Override
    public void onBackPressed() {
        onHomeClicked();
    }

    private void setupViewPager(ViewPager viewPager) {
        final MessageCenterViewPagerAdapter adapter = new MessageCenterViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MessageReceivedFragment.newInstance(), getString(R.string.mdl_inbox));
        adapter.addFragment(MessageSentFragment.newInstance(), getString(R.string.mdl_sent));
        adapter.addFragment(MessageProviderFragment.newInstance(), getString(R.string.mdl_compose));
        viewPager.setAdapter(adapter);
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
