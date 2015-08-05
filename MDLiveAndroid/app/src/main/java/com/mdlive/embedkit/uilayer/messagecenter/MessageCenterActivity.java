package com.mdlive.embedkit.uilayer.messagecenter;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Record;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageCenterActivity extends AppCompatActivity implements FragmentTabHost.OnTabChangeListener {
    private static final String MESSAGES_TAG = "Messages";
    private static final String COMPOSE_MESSAGE_TAG = "Composs_Message";
    private static final String MY_RECORDS_TAG = "My Records";
    private static final String TAG = "MessageCenter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);

        final FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        if (tabHost != null) {
            tabHost.setup(this, getSupportFragmentManager(), R.id.activity_message_center_content);

            tabHost.addTab(createTabSpec(tabHost.newTabSpec(MESSAGES_TAG), MESSAGES_TAG, R.drawable.ic_launcher), MessagesTabFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(COMPOSE_MESSAGE_TAG), COMPOSE_MESSAGE_TAG, R.drawable.ic_launcher), ComposeMessageTabFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(MY_RECORDS_TAG), MY_RECORDS_TAG, R.drawable.ic_launcher), MyRecordTabFragment.class, null);

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
        final View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_host_message_center, null);

        final TextView tv = (TextView)v.findViewById(R.id.tab_host_message_center_text_view);
        final ImageView img = (ImageView)v.findViewById(R.id.tab_host_message_center_image_view);

        tv.setText(text);
        img.setBackgroundResource(imageResourceId);

        return spec.setIndicator(v);
    }

    public void onReceivedMessageClicked(final ReceivedMessage receivedMessage) {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.activity_message_center_content, MessageReceivedDetailsFragment.newInstance(receivedMessage), MESSAGES_TAG).
                addToBackStack(MESSAGES_TAG).
                commit();
    }

    public void onSentMessageClicked(final SentMessage sentMessage) {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.activity_message_center_content, MessageSentDetailsFragment.newInstance(sentMessage), MESSAGES_TAG).
                addToBackStack(MESSAGES_TAG).
                commit();
    }

    public void onMyProviderClicked(final MyProvider myProvider) {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.activity_message_center_content, MessageComposeFragment.newInstance(myProvider), COMPOSE_MESSAGE_TAG).
                addToBackStack(COMPOSE_MESSAGE_TAG).
                commit();
    }

    public void onRecordClicked(final Record record) {

    }
}
