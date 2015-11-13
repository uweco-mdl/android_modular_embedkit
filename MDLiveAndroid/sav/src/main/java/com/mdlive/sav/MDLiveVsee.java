package com.mdlive.sav;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveSummary;
import com.mdlive.embedkit.uilayer.login.MDLiveWaitingRoomFragment;
import com.mdlive.sav.WaitingRoom.WaitingRoomViewPager;
import com.mdlive.unifiedmiddleware.commonclasses.utils.AnalyticsApplication;
import com.vsee.kit.VSeeKit;
import com.vsee.kit.VSeeServerConnection;
import com.vsee.kit.VSeeVideoManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MDLiveVsee extends MDLiveBaseActivity
{
    private boolean stopUpdatingStatus = false;
    private static boolean CONSULTED = false,CALL_ENDED = false, RETURNED = false,
            FINISH = false;

    private static VSeeServerConnection.SimpleVSeeServerConnectionReceiver simpleServerConnectionReceiver = null;
    private static VSeeVideoManager.SimpleVSeeVideoManagerReceiver simpleVidManagerReceiver = null;

    private static final long DELAY = 5000;
    private static final int MAX_TIPS = 10;

    WaitingRoomViewPager pager;
    int viewPagerCurrentItem = 0;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerCurrentItem == pager.getChildCount() - 1) {
                viewPagerCurrentItem = 0;
            } else {
                viewPagerCurrentItem++;
            }
            pager.setCurrentItem(viewPagerCurrentItem, true);

            mHandler.postDelayed(this, DELAY);
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_vsee);
        stopPinTimer();

        pager = (WaitingRoomViewPager) findViewById(R.id.viewPager);
        pager.setClipToPadding(false);
        pager.setPageMargin(12);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), getWaitWatingRoomTips()));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPagerCurrentItem = position;
            }

            @Override
            public void onPageSelected(int position) {
                viewPagerCurrentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setCurrentItem(viewPagerCurrentItem);
        CONSULTED = false;
        CALL_ENDED = false;

        // Must always come first when using VSeeKit.  Doesn't consume many resources so it can always be done at app startup.
        //
        VSeeKit.setContext(getApplicationContext());

        if(simpleServerConnectionReceiver==null)
            simpleServerConnectionReceiver = new VSeeServerConnection.SimpleVSeeServerConnectionReceiver() {
                @Override
                public void onInitializationComplete() {
                    VSeeVideoManager.instance().setWaitingForCallView(getWaitingMessageView());
                    VSeeServerConnection.instance().setAutoAccept(true);
                    VSeeVideoManager.instance().setConfirmEndCall(true);
                }

                @Override
                public void onNewStatusMessage(String message) {
                    if (message.toLowerCase().contains("password")) {
                        stopUpdatingStatus = true;
                    }
                }

                @Override
                public void onLoginStateChange(VSeeServerConnection.LoginState newState) {
                    updateLoginState(newState);
                }
            };

        if(simpleVidManagerReceiver == null)
            simpleVidManagerReceiver = new VSeeVideoManager.SimpleVSeeVideoManagerReceiver() {
                @Override
                public void onRemoveRemoteVideoView(String username, boolean isLast) {
                    if (isLast && !RETURNED && !CALL_ENDED && !isFinishing()) {
                        // video call is over... end the call activity if it is open
                        VSeeServerConnection.instance().logout();
                        VSeeVideoManager.instance().finishVideoActivity();
                        Intent i = new Intent(MDLiveVsee.this, MDLiveSummary.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        RETURNED = false;
                        i.putExtra("isReturning", true);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onDeclinedCall(String username) {
                    super.onDeclinedCall(username);
                    CALL_ENDED = true;
                    VSeeServerConnection.instance().logout();
                    VSeeVideoManager.instance().finishVideoActivity();
                    Intent i = new Intent(MDLiveVsee.this, MDLiveSummary.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }


                @Override
                public void onEndedCall(String[] usernames) {
                    super.onEndedCall(usernames);
                    CALL_ENDED = true;
                    VSeeServerConnection.instance().logout();
                    VSeeVideoManager.instance().finishVideoActivity();
                    if(!isFinishing()) {
                        Intent i = new Intent(MDLiveVsee.this, MDLiveSummary.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                }
            };

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uid = extras.getString("username");
            String pass = extras.getString("password");
            MDLiveVseeApplication.setCredentials(uid, pass);
            VSeeServerConnection.instance().loginUser(uid, pass);
        }

        if (!MDLiveVseeApplication.loginCredentialsValid())
        {
            return;
        }
        updateLoginState(VSeeServerConnection.instance().getLoginState());


        if(simpleServerConnectionReceiver!=null)
            VSeeServerConnection.instance().addReceiver(simpleServerConnectionReceiver);
        if(simpleVidManagerReceiver!=null)
            VSeeVideoManager.instance().addReceiver(simpleVidManagerReceiver);
    }

    @SuppressLint("InflateParams")
    private View getWaitingMessageView() {

        FrameLayout frame = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.mdlive_vseewaitingmessage, null);
        TextView waitConnectMesg = (TextView)frame.findViewById(R.id.txtWaitCallConnect);
        waitConnectMesg.setText("Waiting for call to connect...");
        return(frame);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        RETURNED = true;
        if(CONSULTED && !isFinishing() && !CALL_ENDED)   // if user returned to this page after a consultation
        {
            FINISH = true;
            VSeeServerConnection.instance().logout();
            VSeeVideoManager.instance().finishVideoActivity();
			VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
            VSeeVideoManager.instance().removeReceiver(simpleVidManagerReceiver);
			simpleServerConnectionReceiver = null;
			simpleVidManagerReceiver = null;
            Intent i = new Intent(MDLiveVsee.this, MDLiveSummary.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("isReturning", true);
            startActivity(i);
            finish();
        }

        mHandler.postDelayed(mRunnable, DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        try{
            VSeeVideoManager.instance().endVideoCalls();
            VSeeServerConnection.instance().logout();
            VSeeVideoManager.instance().finishVideoActivity();
        } catch(Throwable e){
            e.printStackTrace();
        }
        VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
        VSeeVideoManager.instance().removeReceiver(simpleVidManagerReceiver);
        simpleServerConnectionReceiver = null;
        simpleVidManagerReceiver = null;
        clearMinimizedTime();
        super.onDestroy();
    }

    /**
     * checks to see whether the named service is already running or not
     *
     * @param serviceClass
     * @return TRUE if service is already running, FALSE otherwise
     */
    private boolean isServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the consultation status.
     * Reset the consultation flag if consult was finished/closed
     *
     * @return
     */
    public static boolean getResetConsultStatus()
    {
        if(FINISH)
        {
            FINISH = false;
            CONSULTED = false;
            return (true);
        }

        return(false);
    }

    private void updateLoginState(VSeeServerConnection.LoginState loginState)
    {
        if (stopUpdatingStatus) {
            return;
        }

        switch (loginState) {
            case TRY_LOGIN:

                break;

            case LOGGED_IN:
                RETURNED = false;
                // make sure auto-accept is enabled even for cases of reconnection
                VSeeServerConnection.instance().setAutoAccept(true);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AnalyticsApplication application = new AnalyticsApplication();
                                for(AnalyticsApplication.TrackerName tn : AnalyticsApplication.TrackerName.values()) {
                                    // Obtain the shared Tracker instance.
                                    Tracker mTracker = application.getTracker(getApplication(), tn);
                                    if (mTracker != null) {
                                        mTracker.setScreenName(getString(R.string.mdl_mdlive_video_session_screen));
                                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(getString(R.string.mdl_mdlive_session))
                                                .setAction(getString(R.string.mdl_mdlive_video_session_initiated))
                                                .setLabel(getString(R.string.mdl_mdlive_video_session_initiated))
                                                .build());
                                    }
                                }
                                CONSULTED = true;
                                startActivity(VSeeVideoManager.instance().getVideoLaunchIntent());
                            }
                        });
                    }
                }, 2300);
                break;
        }
    }


    @Override
    public void onBackPressed() {
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
        private WatingRoomTips mWatingRoomTips;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyPagerAdapter(FragmentManager fm, final WatingRoomTips watingRoomTips) {
            super(fm);

            mWatingRoomTips = watingRoomTips;
        }

        @Override
        public Fragment getItem(int pos) {
            return MDLiveWaitingRoomFragment.newInstance(mWatingRoomTips.mHeader, mWatingRoomTips.mColors[pos], mWatingRoomTips.mBodyText[pos]);
        }

        @Override
        public int getCount() {
            return MAX_TIPS;
        }

        @Override
        public float getPageWidth (int position) {
            return 0.93f;
        }

    }

    private WatingRoomTips getWaitWatingRoomTips() {
        final WatingRoomTips tips = new WatingRoomTips();

        final Random random = new Random(3);
        int randomNumber = random.nextInt(3);

        String[] strings = getResources().getStringArray(R.array.waiting_room_details);
        String[] randomizeStrins = new String[MAX_TIPS];

        int[] colors = getResources().getIntArray(R.array.waiting_room_header_colors);
        int[] randomizeColors = new int[MAX_TIPS];

        for (int i = 0; i < MAX_TIPS; i++) {
            randomizeStrins[i] = strings[randomNumber];
            randomizeColors[i] = colors[randomNumber];

            randomNumber += 3;
        }

        tips.mHeader = getResources().getString(R.string.mdl_did_you_know);
        tips.mColors = randomizeColors;
        tips.mBodyText = randomizeStrins;

        return tips;
    }

    public static class WatingRoomTips {
        public String mHeader;
        public int[] mColors;
        public String[] mBodyText;
    }
}
