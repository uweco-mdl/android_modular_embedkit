package com.mdlive.embedkit.uilayer.WaitingRoom;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.MDLiveVsee;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveSummary;
import com.mdlive.embedkit.uilayer.login.MDLiveWaitingRoomFragment;
import com.mdlive.embedkit.uilayer.sav.MDLiveAppointmentThankYou;
import com.mdlive.embedkit.uilayer.sav.MDLiveChooseProvider;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * THis is the waiting Room screen. The provider status is checked here. If the Provider status is
 * true, then the VSeeActivity is called. In the returning user case, where the user is taken back
 * to this swcreen by Vsee, if the provider status is false, MDLiveSummary activity  is called.
 *
 */
public class MDLiveWaitingRoom extends MDLiveBaseActivity{
    private static final long DELAY = 15000;
    private static final int MAX_TIPS = 29;

    private WaitingRoomService waitingService;
    public static String OPEN_URI = "mdlive://mdlivemobile/vsee?result=thankyou";
    private String userName=null,password=null;
    private boolean isReturning,isStartedSummary;

    WaitingRoomViewPager pager;
    int viewPagerCurrentItem = 0;
    private String positiveCallBackUrl,negativCallBackUrl;
    public static boolean isEscalated;
    private Handler handler,providerStatusHandler;
    private Runnable runnable,providerStatusRunnable;
    public static int providerStatusDelay=15000,docOnCallDelay=30000;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_waiting_room);
        clearMinimizedTime();

        isReturning = getIntent().getBooleanExtra("isReturning",false);



        isEscalated=false;
        Log.e("IsDoctorOnCall",""+ MDLiveChooseProvider.isDoctorOnVideo);
        handler=new Handler();
        setProgressBar(findViewById(R.id.progressDialog));
        providerStatusHandler=new Handler();
        providerStatusRunnable=new Runnable() {
            @Override
            public void run() {
                getProviderStatus();
            }
        };
        providerStatusRunnable.run();

        if(MDLiveChooseProvider.isDoctorOnVideo){//if true getOnCallProviderStatus() method to be executed synchronously
            Log.e("Run","Run Strart");
            runnable=new Runnable() {
                @Override
                public void run() {
                    Log.e("Run","Run Inside");
                    getOnCallProviderStatus();
                }
            };
            runnable.run();
        }



        //getProviderStatus();
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });

        pager = (WaitingRoomViewPager) findViewById(R.id.viewPager);
        pager.setClipToPadding(false);
        pager.setPageMargin(44);
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

    /***
     *This function will retrieve user Provider status from the server.
     *@Listner-successListner will  handles the success response from server.
     *@Listner-errorListener will handles error response from server.
     *WaitingRoomService class will send the get request to the server and receives the corresponding response
     */

    public void getProviderStatus() {
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
                handleSuccessResponse(response.toString());//Method to handle the response from Server
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    NetworkResponse errorResponse = error.networkResponse;
                    Log.e("WaitingRoom 1 REsponse", errorObj.toString());
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("message") || errorObj.has("error")) {
                            final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                            (MDLiveWaitingRoom.this).runOnUiThread(new Runnable() {
                                public void run() {
                                    MdliveUtils.showDialog(MDLiveWaitingRoom.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, "OK", null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveGetStarted.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            dialog.dismiss();
                                            MDLiveWaitingRoom.this.finish();
                                        }
                                    }, null);
                                }
                            });
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoom.this, error, getProgressDialog());
                    }

                    if (isReturning && !isStartedSummary) {
                        Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                        startActivity(i);
                        MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                        isStartedSummary = true;
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        waitingService = new WaitingRoomService(MDLiveWaitingRoom.this, null);
        waitingService.doGetProviderStatus(successListener, errorListner);
    }


    /***
     *This function will retrieve username and password from the server.
     *@Listner-successListner will  handles the success response from server.
     *@Listner-errorListener will handles error response from server.
     *WaitingRoomService class will send the post request to the server and receives the corresponding response
     */

    public void getVSEECredentials() {
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
               handleVSeeResponse(response.toString());
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveWaitingRoom.this);
                    }
                }
                if(isReturning && !isStartedSummary){
                    Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                    startActivity(i);
                    MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                    isStartedSummary = true;
                    finish();
                }
            }
        };
        waitingService = new WaitingRoomService(MDLiveWaitingRoom.this, null);
        waitingService.doPostVseeCredentials(getPostBody(), successListener, errorListner);
    }


    /***
     * This method will invoked on successful completion of getProvider method.
     * @param response-Object contains the response in Json Format
     *  if Provider status is true getVSEECredentials credential method will be invoked else
     *   again getProviderStatus method will be called.
     */
    public void handleSuccessResponse(String response) {
        try{
            JSONObject resObj=new JSONObject(response);
            if(resObj.getString("provider_status").equals("true")){
                ((TextView)findViewById(R.id.txt_waitingtext)).setText(R.string.mdl_provider_arrived);
                ((ImageView)findViewById(R.id.consultation_image_view)).setImageResource(R.drawable.provider_arrived);
                getVSEECredentials();
            }else if(isReturning && !resObj.getString("provider_status").equals("true") && !isStartedSummary) {
                Log.d("Waiting Room -->","handleSuccessResponse ==");
                Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                startActivity(i);
                MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                isStartedSummary = true;
                finish();
            }else {
                getProviderStatus();
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /***
     * This method will be responsible for parsing the response from VseeCredential,
     * @param response-Object contains the response in Json Format
     *
     */

    public void handleVSeeResponse(String response){
        try{
            if(response == null){
                getVSEECredentials();
            }else{
                JSONObject resObj=new JSONObject(response);
                userName=resObj.getString("username");
                password=resObj.getString("password");

//                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Start Consultation");
//                ((ImageView)findViewById(R.id.consultation_image_view)).setImageResource(R.drawable.start_consultation);

                Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveVsee.class);
                Log.e("VeeSEE -->", "Final reached....");
                i.putExtra("username",userName);
                i.putExtra("password",password);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    /***
     * This method will be responsible for creating post params in Json Format
     * @return -postparams as json format.
     */
    public String getPostBody() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");

        HashMap<String, String> postBody = new HashMap<String, String>();
        postBody.put("id", apptId);
        postBody.put("open_uri", OPEN_URI);
        return new Gson().toJson(postBody);
    }

    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.mdl_home_dialog_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.mdl_home_dialog_text));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.mdl_ok_upper), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                            MdliveUtils.ssoInstance.getparentClassname());
                    intent.setComponent(cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        isReturning = getIntent().getBooleanExtra("isReturning",false);
        isStartedSummary = false;
        if(isReturning){
            getProviderStatus();
        }

        mHandler.postDelayed(mRunnable, DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMinimizedTime();
        handler.removeCallbacks(runnable);
        providerStatusHandler.removeCallbacks(providerStatusRunnable);
        Log.e("Test", "Destroy Coming");
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

    /***
     *This function will retrieve user Provider status from the server on for oncall video consultation.
     *@Listner-successListner will  handles the success response from server.
     *@Listner-errorListener will handles error response from server.
     *WaitingRoomService class will send the get request to the server and receives the corresponding response
     */

    public void getOnCallProviderStatus() {
        Log.e("Coming", "Called");
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
                Log.e("Response", response.toString());

                if (!response.toString().equals("{}")) {
                    handler.removeCallbacks(runnable);
                    handleOnCallVideoResponse(response.toString());//Method to handle the response from Server
                } else {
                    Log.e("NEgative Response", "Called");
                    handler.postDelayed(runnable, 30000);//Method to call the method with 30s delay
                }

            }
        }

                ;
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Provider Status Inside","error Response"+"--"+error.toString());
                MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoom.this, error, null);
            }
        };

        waitingService=new WaitingRoomService(MDLiveWaitingRoom.this, null);
        waitingService.doGetOnCallProviderStatus(successListener,errorListner);
    }


    public void handleOnCallVideoResponse(String response){
        try{
            if(!response.isEmpty()) {
                JSONObject resObj = new JSONObject(response);
                if (resObj.has("message")) {
                    showAlertPopup(resObj.getString("message"),resObj);
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    /**
     * This method will be used to show the alert based on service response.
     * @param errorMessage--The error message to be shown to the user is received from the server.
     * @param resObj-Response object which is received from the server contains callback url as Json Array
     */

    public void showAlertPopup(String errorMessage,JSONObject resObj){
        try {
            JSONArray responseArray= resObj.getJSONArray("actions");
            JSONObject negativeObject=responseArray.getJSONObject(0);
            JSONObject positiveObject=responseArray.getJSONObject(1);
            String baseUrl= AppSpecificConfig.BASE_URL.replace("/services", "");
            positiveCallBackUrl=baseUrl+positiveObject.getString("callback_url");
            negativCallBackUrl=baseUrl+negativeObject.getString("callback_url");
            if(positiveObject.has("phone")){
                positiveCallBackUrl=positiveCallBackUrl+positiveObject.getString("phone");
            }
            final String positiveLabel=positiveObject.getString("label");
            final String negativeLabel=negativeObject.getString("label");




            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLiveWaitingRoom.this);

            alertDialogBuilder
                    .setTitle("")
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setPositiveButton(positiveObject.getString("label").trim(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            yesCallBackServerRequest(positiveCallBackUrl);
                            //Need to make Call me by Phone", callback_url: "/services/waiting_room/:id/switch_to_phone?phone=", phone: "555555555"
                        }
                    }).setNegativeButton(negativeObject.getString("label").trim(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    noCallBackServerRequest(negativCallBackUrl);
                }
            });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                  /*  if(negativeLabel.length()>5&& positiveLabel.length()>5){
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 25);
                        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 25);
                    }*/
                    alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                    alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));


                }
            });

            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will be when user click on yes.
     * doPutCallBackRequest Method send the request to the server and get the corresponding response back.
     * @param url--Call Url from the response
     */
    public void yesCallBackServerRequest(String url){
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                Log.e("YEsCallBackResponse", "" + response.toString());
                try{

                    JSONObject yesResObj=new JSONObject(response.toString());
                    if(yesResObj.has("keep_waiting")){
                        if(!yesResObj.getBoolean("keep_waiting")){
                            Intent thankYouIntent=new Intent(MDLiveWaitingRoom.this, MDLiveAppointmentThankYou.class);
                            handler.removeCallbacks(runnable);
                            providerStatusHandler.removeCallbacks(providerStatusRunnable);
                            if(yesResObj.has("message")){
                                if(yesResObj.getString("message").contains("escalated")){
                                    isEscalated=true;
                                }else{
                                    isEscalated=false;
                                }
                            }
                            thankYouIntent.putExtra("activitycaller","OnCall");
                            thankYouIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
                            startActivity(thankYouIntent);
                            MdliveUtils.startActivityAnimation(MDLiveWaitingRoom.this);


                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                //To do: Need to handle the success response
            }
        };NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //To do: Need to handle the Error response
                MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoom.this,volleyError,null);
            }
        };
        waitingService=new WaitingRoomService(MDLiveWaitingRoom.this,null);
        waitingService.doPutCallBackRequest(url,successListener,errorListener);
    }



    /**
     * This method will be when user click on No.
     * doPutCallBackRequest Method send the request to the server and get the corresponding response back from the server.
     * @param url--Call back Url from the response
     */

    public void noCallBackServerRequest(String url){
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                Log.e("NoCallBackResponse", "" + response.toString());
                try{
                    JSONObject noResObj=new JSONObject(response.toString());
                    if(noResObj.has("keep_waiting")){
                        if(noResObj.getBoolean("keep_waiting")){
                            handler.postDelayed(runnable,docOnCallDelay);
                        }else if(!noResObj.getBoolean("keep_waiting")){
                            Intent thankYouIntent=new Intent(MDLiveWaitingRoom.this, MDLiveAppointmentThankYou.class);
                            handler.removeCallbacks(runnable);
                            providerStatusHandler.removeCallbacks(providerStatusRunnable);
                            if(noResObj.has("message")){
                                if(noResObj.getString("message").contains("escalated")){
                                    isEscalated=true;
                                    thankYouIntent.putExtra("activitycaller","escalated");
                                }else{
                                    isEscalated=false;
                                }
                            }
                            ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
                            startActivity(thankYouIntent);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //To do: Need to handle the Error response
                MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoom.this,volleyError,null);
            }
        };
        waitingService=new WaitingRoomService(MDLiveWaitingRoom.this,null);
        waitingService.doPutCallBackRequest(url, successListener, errorListener);
    }





}
