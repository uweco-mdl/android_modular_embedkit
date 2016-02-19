package com.mdlive.sav;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.sav.adapters.ChooseProviderAdapter;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ChooseProviderServices;
import com.mdlive.unifiedmiddleware.services.provider.FilterSearchServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * This class basically returns the provider list and the doctor on call.The Doctor on call
 * will be visible only when the response from the service for the Doctor on call is true
 * and Doctor on call will be hidden when the response is false.
 */
public class MDLiveChooseProvider extends MDLiveBaseActivity {
    private static final long THIRTY_SECONDS = 60 * 1000;
    private ListView listView;
    private String providerName,availabilityType, imageUrl, doctorId,groupAffiliations, postParams;
    private long strDate,shared_timestamp;
    private ArrayList<HashMap<String, String>> providerListMap;
    private ChooseProviderAdapter baseadapter;
    private boolean available_now_status = false;
    private FrameLayout filterMainRl;
    private RelativeLayout docOnCalLinLay;
    private TextView loadingTxt;
    private boolean flag = false;
    public static boolean isDoctorOnCall = false, isDoctorOnVideo = false, fromGetSartedPage = true;
    public static boolean mDoctorOnCall = false, mDoctorOnVideo = false;

    private Button seeFirstAvailDoctor;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(flag){
                ChooseProviderResponseList();
            }else{
                flag = true;
            }
            mHandler.postDelayed(this, THIRTY_SECONDS);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chooseprovider);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_choose_provider));

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fromGetSartedPage = true;
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
       /* ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);*/
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_choose_provider));

        Initailization();
        ChooseProviderResponseList();



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

        mHandler = new Handler();
        setListViews();
        mHandler.post(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
        baseadapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        flag = false;
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveChooseProvider.this);
        onBackPressed();
    }

    /**
     *
     * The initialization of the views was done here.All the labels was defined here and
     * the click event for the back button and the home button was done here.
     * On clicking the back button image will be finishing the current Activity
     * and on clicking the Home button you will be navigated to the SSo Screen with
     * an alert.     *
     * **/

    private void Initailization() {
        elevateCircularImage((CircularNetworkImageView) findViewById(R.id.ProfileImg));

        providerListMap = new  ArrayList<HashMap<String, String>>();
        docOnCalLinLay = (RelativeLayout)findViewById(R.id.docOnCalLinLay);
        filterMainRl = (FrameLayout)findViewById(R.id.filterMainRl);
        loadingTxt= (TextView)findViewById(R.id.loadingTxt);
        //setProgressBar(findViewById(R.id.progressDialog));
        seeFirstAvailDoctor= (Button) findViewById(R.id.btn_see_first_available_doctor);
        elevateButton(seeFirstAvailDoctor);
        listView = (ListView) findViewById(R.id.chooseProviderList);

        seeFirstAvailDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDoctorOnCall=mDoctorOnCall;
                isDoctorOnVideo=mDoctorOnVideo;
                Intent seeFirstAvailableDocIntent=new Intent(MDLiveChooseProvider.this,MDLiveDoctorOnCall.class);
                startActivity(seeFirstAvailableDocIntent);
            }
        });

    }
    /**
     * This function is invoked when the doctor on call returns true from the service.
     * if the list returns the providers in that case the filter will be added.if there
     * is no providers then the filter icon will be hidden.
     *
     */
    private void doctorOnCallButtonClick() {
        findViewById(R.id.filterTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MDLiveChooseProvider.this, MDLiveSearchProvider.class);
                startActivityForResult(intent, 1);
                MdliveUtils.startActivityAnimation(MDLiveChooseProvider.this);
            }
        });
    }
    /**
     *
     * Choose Provider List Details.
     * Class : ChooseProviderServices - Service class used to fetch the provider list information.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     *
     */
    private void ChooseProviderResponseList() {
        if (getProgressDialog().isShowing()) {
            return;
        }

        setProgressBarVisibility();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response.toString());
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setInfoVisibilty();
                docOnCalLinLay.setVisibility(View.VISIBLE);
                filterMainRl.setVisibility(View.GONE);
                findViewById(R.id.txtFilter).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                doctorOnCallButtonClick();
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == MDLiveConfig.HTTP_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("message") || errorObj.has("error")) {
                            final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                            (MDLiveChooseProvider.this).runOnUiThread(new Runnable() {
                                public void run() {
                                    filterMainRl.setVisibility(View.GONE);
                                    findViewById(R.id.txtFilter).setVisibility(View.GONE);
                                    MdliveUtils.showDialog(MDLiveChooseProvider.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, getString(R.string.mdl_ok_upper), null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(MDLiveChooseProvider.this, MDLiveGetStarted.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            dialog.dismiss();
                                            MDLiveChooseProvider.this.finish();
                                        }
                                    }, null);
                                }
                            });
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveChooseProvider.this, error, getProgressDialog());
                        filterMainRl.setVisibility(View.GONE);
                        findViewById(R.id.txtFilter).setVisibility(View.GONE);
                    }
                }catch(Exception e){
                    setInfoVisibilty();
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveChooseProvider.this);
                    e.printStackTrace();
                }
            }};
        if(fromGetSartedPage){
            ChooseProviderServices services = new ChooseProviderServices(MDLiveChooseProvider.this, getProgressDialog());
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            services.doChooseProviderRequest(settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl)), settings.getString(PreferenceConstants.PROVIDERTYPE_ID, ""), successCallBackListener, errorListener);
        }else{
            FilterSearchServices services = new FilterSearchServices(MDLiveChooseProvider.this, null);
            services.getFilterSearch(postParams, successCallBackListener, errorListener);
        }

    }
    /**
     *
     *  Successful Response Handler for Load Basic Info.
     *   Here if the doctor on call String returns true then the Doctor On Call should
     *   be available else the doctor on call should be hidden.
     *
     */
    private void handleSuccessResponse(String response) {
        try {
            docOnCalLinLay.setVisibility(View.GONE);
            filterMainRl.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response);
            boolean StrDoctorOnCall = false;
            JSONObject resObject=new JSONObject(response);

            //Doctor call Validation
            if(resObject.has("doctor_on_call_video") || resObject.has("doctor_on_call")){
                if(resObject.isNull("doctor_on_call_video")&&resObject.isNull("doctor_on_call")) {
                    isDoctorOnCall=false;
                    isDoctorOnVideo=false;
                    StrDoctorOnCall=false;
                    mDoctorOnCall=false;
                    mDoctorOnVideo=false;
                }else if(resObject.getBoolean("doctor_on_call_video")) {
                    isDoctorOnVideo=true;
                    isDoctorOnCall=false;
                    StrDoctorOnCall=true;
                    mDoctorOnCall=false;
                    mDoctorOnVideo=true;
                } else if(resObject.getBoolean("doctor_on_call")) {
                    isDoctorOnVideo=false;
                    isDoctorOnCall=true;
                    StrDoctorOnCall=true;
                    mDoctorOnCall=true;
                    mDoctorOnVideo=false;

                }
                if(resObject.getBoolean("doctor_on_call")&&resObject.getBoolean("doctor_on_call_video")){
                    isDoctorOnVideo=true;
                    isDoctorOnCall=true;
                    StrDoctorOnCall=true;
                    mDoctorOnCall=true;
                    mDoctorOnVideo=true;

                }else if(!resObject.getBoolean("doctor_on_call")&&!resObject.getBoolean("doctor_on_call_video"))
                {
                    isDoctorOnVideo=false;
                    isDoctorOnCall=false;
                    StrDoctorOnCall=false;
                    mDoctorOnCall=false;
                    mDoctorOnVideo=false;
                }

            }


            if(responObj.get("doctor_on_call").isJsonNull())
            {

                if (responObj.has("physicians")) {
                    JsonArray responseArray = responObj.get("physicians").getAsJsonArray();
                    if (responseArray.size() != 0) {
                        if (responseArray.get(0).isJsonObject()) {
                            providerListMap.clear();
                            setBodyContent(responseArray);
                            //setListView();
                        } else {
                            filterMainRl.setVisibility(View.GONE);
                           findViewById(R.id.txtFilter).setVisibility(View.GONE);
                            MdliveUtils.showDialog(MDLiveChooseProvider.this, responseArray.getAsString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        }

                    }
                }

            }else  if(!responObj.get("physicians").isJsonNull()){
                if (responObj.has("physicians")){
                    JsonArray  responArray = responObj.get("physicians").getAsJsonArray();
                    if(responArray.size()!=0){
                        if(responArray.get(0).isJsonObject()){

                            providerListMap.clear();
                            setHeaderContent(StrDoctorOnCall);
                            setBodyContent(responArray);
                            //setListView();
                        }else{
                            if (StrDoctorOnCall) {
                                docOnCalLinLay.setVisibility(View.VISIBLE);
                                filterMainRl.setVisibility(View.GONE);
                                findViewById(R.id.txtFilter).setVisibility(View.GONE);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                            } else {
                                showOrHideFooter();
                                findViewById(R.id.txtFilter).setVisibility(View.GONE);
                                filterMainRl.setVisibility(View.GONE);
                                MdliveUtils.showDialog(MDLiveChooseProvider.this, responArray.getAsString(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                    }

                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        baseadapter.notifyDataSetChanged();
        setInfoVisibilty();
    }
    /**
     *
     *  Set the ListView values.Here the Listview is populated with two list values
     *  the flag value for the header will ba added for displaying the DoctorOnCall
     *  and the doctor's list will be added in the other list.The doctor name and
     *  the specialty will be displayed here
     *
     */
    private void setListViews() {
        showOrHideFooter();
        baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, providerListMap);
        listView.setAdapter(baseadapter);
        baseadapter.notifyDataSetChanged();
        ListItemClickListener();
    }

    /*
    * shows or hide list footer/ bottom footer. This is for the static and the dynamic footer
    * implementation. If there is no list that is the response is null then the static footer
    * has been implemented.In case if the list has items then the footer in the listview
    * has been implemented.
    * Also here a new field called shared_timestamp is added this is because we have next
    * availability for timestamp that cannot be saved in preferences it might result like
    * todat and tommorrow which may cause datye parse exception , so i have used seperate
    * key for saving the timestamp that is shared_timestamp.
    * */
    public void showOrHideFooter() {
//        //
    }

    /**
     *
     *  Set the Body content of the Listview.In this the Dependent Name and the
     *   specility of the doctor will be displayed.Also the profile image of
     *   the particular doctor is also displayed .The corresponding profile image of the
     *  the Particular doctor will be displayed.
     *
     */
    private void setBodyContent(JsonArray responArray) {
        doctorOnCallButtonClick();
        for(int i=0;i<responArray.size();i++) {
            try {
                 providerName = responArray.get(i).getAsJsonObject().get("name").getAsString();

                 doctorId = responArray.get(i).getAsJsonObject().get("id").getAsString();

                 imageUrl = responArray.get(i).getAsJsonObject().get("provider_image_url").getAsString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonArray  affiliationsArray = responArray.get(i).getAsJsonObject().get("provider_groups").getAsJsonArray();
            String nxtavaildate="";
            for(int j=0;j<affiliationsArray.size();j++) {
                groupAffiliations = affiliationsArray.get(j).getAsJsonObject().get("group_name").getAsString();
            }
            try {

                if(!responArray.get(i).getAsJsonObject().get("next_availability").isJsonNull()){
                    shared_timestamp = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();
                    strDate = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();
                    nxtavaildate= TimeZoneUtils.getReceivedTimeForProvider(strDate, "", this);
                }else{
                    nxtavaildate=null;
                    shared_timestamp = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                strDate = IntegerConstants.DATE_FLAG;
            }
            availabilityType =  responArray.get(i).getAsJsonObject().get("availability_type").getAsString();
            available_now_status =  responArray.get(i).getAsJsonObject().get("available_now_status").getAsBoolean();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_FALSE);
            map.put("id", doctorId);
            map.put("provider_image_url", imageUrl);
            map.put("availability_type", availabilityType);
            map.put("available_now_status", available_now_status+"");
            map.put("group_name", groupAffiliations);
            map.put("next_availability",nxtavaildate);
            map.put("shared_timestamp", shared_timestamp + "");
            providerListMap.add(map);
        }
    }
    /**
     *
     *  Set the Header content of the Listview.In this the Doctor Name and the
     *   specility of the doctor will be displayed.Also the profile image of
     *   the particular doctor is also displayed .The corresponding profile image of the
     *  the Particular doctor will be displayed.
     *
     */

    private void setHeaderContent(boolean strDoctorOnCall) {
        if(StringConstants.TRUE == strDoctorOnCall)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_TRUE);
            map.put("provider_image_url", imageUrl);
            map.put("id", doctorId);
            map.put("availability_type", availabilityType);
            map.put("group_name", groupAffiliations);
            map.put("available_now_status", available_now_status+"");
            map.put("next_availability", TimeZoneUtils.getReceivedTimeForProvider(strDate, "", this));
            providerListMap.add(map);
            filterMainRl.setVisibility(View.GONE);
        }
    }

    /**
     *
     *  The on Item Click listener for the listview will be displayed .The action for the
     *  corresponding list item will be triggered.The provider id will be saved in the
     *  preferences for the Further references.
     *
     */
    public void ListItemClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                isDoctorOnCall=false;
                isDoctorOnVideo=false;
                saveDoctorId(providerListMap.get(position).get("id"), providerListMap.get(position).get("shared_timestamp"),
                        providerListMap.get(position).get("name"), providerListMap.get(position).get("group_name"),providerListMap.get(position).get("availability_type"),providerListMap.get(position).get("available_now_status"));
                Intent Reasonintent = new Intent(MDLiveChooseProvider.this,MDLiveProviderDetails.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveChooseProvider.this);
            }
        });

    }

    /**
     * getDateCurrentTimeZone method will convert the Timestamp to the current
     * Simple Date Format.
     * @param timestamp the timestamp in milli seconds will be passed.
     */
    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            if(timestamp != IntegerConstants.DATE_FLAG){
                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.setTimeInMillis(timestamp * 1000);
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MM yyyy HH:mm a", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("EDT"));


                Calendar today = Calendar.getInstance();
                today.set(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                          Calendar.getInstance().get(Calendar.AM_PM);


                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DATE, 1);  // number of days to add
                tomorrow.set(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH), 23, 59, 59);


                String sendData="";
                if(timestamp <= today.getTimeInMillis()){
                    sendData = "Today "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
                }else if(timestamp > today.getTimeInMillis() && timestamp <= tomorrow.getTimeInMillis()){
                    sendData = "Tomorrow "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
                }else{
                    Date currenTimeZone = calendar.getTime();
                    sendData = sdf.format(currenTimeZone);
                }

                return sendData;
            }

        }catch (Exception e) {

        }
        return StringConstants.ISHEADER_FALSE;
    }

    /**
     *      @param DocorId : Pass the selected Doctor's Id String
     *      The Corresponding id of the Doctor should be saved in the Preferences and will be
     *      triggerred in the Requird places.
     *
     */
    public void saveDoctorId(String DocorId, String AppointmentDate, String docName ,String groupAffiliations,String availability_type,String availability_status)
    {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES,DocorId);
        editor.putString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, docName);
        editor.putString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, AppointmentDate);
        editor.putString(PreferenceConstants.PROVIDER_GROUP_AFFILIATIONS_PREFERENCES, groupAffiliations);
        editor.putString(PreferenceConstants.PROVIDER_AVAILABILITY_TYPE_PREFERENCES, availability_type);
        editor.putString(PreferenceConstants.PROVIDER_AVAILABILITY_STATUS_PREFERENCES, availability_status);
        editor.commit();
    }
    /**
     *      @param requestCode,resultCode : Pass the request code and the result code
     *      The Corresponding doctor's list will be displayed based on the corresponding
     *     Requestcode and the result code
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==1){
            String response=data.getStringExtra("Response");
            postParams = data.getStringExtra("postParams");
            try{
                // Clear the ListView
                fromGetSartedPage = false;
                providerListMap.clear();
                baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, providerListMap);
                listView.setAdapter(baseadapter);
                handleSuccessResponse(response);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /*
    * set visible for the progress bar
    */
    public void setProgressBarVisibility()
    {
        //progressBar.setVisibility(View.VISIBLE);
        showProgress();
        loadingTxt.setVisibility(View.GONE);
    }
    /*
    * set visible for the details view layout
    */
    public void setInfoVisibilty()
    {
        //progressBar.setVisibility(View.GONE);
        hideProgress();
        loadingTxt.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveChooseProvider.this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void elevateButton(final Button button) {
        if (button == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(7 * getResources().getDisplayMetrics().density);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void elevateCircularImage(final CircularNetworkImageView view) {
        if (view == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    // Or read size directly from the view's width/height
                    int size = (int) (16 * getResources().getDisplayMetrics().density);
                    outline.setOval(0, 0, size, size);
                }
            };

            view.setElevation(16 * getResources().getDisplayMetrics().density);
        }
    }
}
