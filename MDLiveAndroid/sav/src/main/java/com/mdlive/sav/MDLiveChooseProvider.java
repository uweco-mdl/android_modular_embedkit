package com.mdlive.sav;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.sav.adapters.ChooseProviderAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ChooseProviderServices;

import org.apache.http.HttpStatus;
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
    private String providerName,specialty,availabilityType, imageUrl, doctorId, appointmentDate,groupAffiliations;
    private long strDate,shared_timestamp;
    private ArrayList<HashMap<String, String>> providerListMap;
    private ChooseProviderAdapter baseadapter;
    private boolean isDoctorOnCallReady = false,available_now_status;
    private FrameLayout filterMainRl;
    private RelativeLayout docOnCalLinLay;
    private Button seeNextAvailableBtn;
    private TextView loadingTxt;
    private boolean flag = false;

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
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
       /* ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);*/
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_choose_provider).toUpperCase());

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
        providerListMap = new  ArrayList<HashMap<String, String>>();
        docOnCalLinLay = (RelativeLayout)findViewById(R.id.docOnCalLinLay);
        filterMainRl = (FrameLayout)findViewById(R.id.filterMainRl);
        loadingTxt= (TextView)findViewById(R.id.loadingTxt);
        //setProgressBar(findViewById(R.id.progressDialog));
        seeNextAvailableBtn = (Button) findViewById(R.id.seenextAvailableBtn);
        listView = (ListView) findViewById(R.id.chooseProviderList);

//        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MdliveUtils.hideSoftKeyboard(MDLiveChooseProvider.this);
//                onBackPressed();
//            }
//        });
    }
    /**
     * This function is invoked when the doctor on call returns true from the service.
     * if the list returns the providers in that case the filter will be added.if there
     * is no providers then the filter icon will be hidden.
     *
     */
    private void doctorOnCallButtonClick() {
//        seenextAvailableBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MDLiveChooseProvider.this,MDLiveReasonForVisit.class);
//                startActivity(intent);
//                MdliveUtils.startActivityAnimation(MDLiveChooseProvider.this);
//
//            }
//        });
        ((ImageView)findViewById(R.id.filterTxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MDLiveChooseProvider.this, MDLiveSearchProvider.class);
                startActivityForResult(intent,1);
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
                Log.e("Status Code", "" + error.networkResponse.statusCode);
                setInfoVisibilty();
                docOnCalLinLay.setVisibility(View.VISIBLE);
                filterMainRl.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.txtFilter)).setVisibility(View.GONE);
                ((RelativeLayout)findViewById(R.id.progressBar)).setVisibility(View.GONE);
                doctorOnCallButtonClick();
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("message") || errorObj.has("error")) {
                            final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                            (MDLiveChooseProvider.this).runOnUiThread(new Runnable() {
                                public void run() {
                                    filterMainRl.setVisibility(View.GONE);
                                    ((TextView)findViewById(R.id.txtFilter)).setVisibility(View.GONE);
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
                        ((TextView)findViewById(R.id.txtFilter)).setVisibility(View.GONE);
                    }
                }catch(Exception e){
                    setInfoVisibilty();
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveChooseProvider.this);
                    e.printStackTrace();
                }
            }};
        ChooseProviderServices services = new ChooseProviderServices(MDLiveChooseProvider.this, getProgressDialog());
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        services.doChooseProviderRequest(settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl)), settings.getString(PreferenceConstants.PROVIDERTYPE_ID,""), successCallBackListener, errorListener);
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
            //Log.v("Response--->", response.toString());
            setInfoVisibilty();
            docOnCalLinLay.setVisibility(View.GONE);
            filterMainRl.setVisibility(View.VISIBLE);
            ((RelativeLayout)findViewById(R.id.progressBar)).setVisibility(View.GONE);
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response);
            boolean StrDoctorOnCall = false;
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
                           ((TextView)findViewById(R.id.txtFilter)).setVisibility(View.GONE);
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
                    Log.v("Coming","First");
                    JsonArray  responArray = responObj.get("physicians").getAsJsonArray();
                    if(responArray.size()!=0){
                        if(responArray.get(0).isJsonObject()){
                            if(!responObj.get("doctor_on_call_video").isJsonNull()){
                                StrDoctorOnCall =  responObj.get("doctor_on_call_video").getAsBoolean();
                            }else{
                                StrDoctorOnCall = false;
                            }
                            providerListMap.clear();
                            setHeaderContent(StrDoctorOnCall);
                            setBodyContent(responArray);
                            //setListView();
                        }else{
                            showOrHideFooter();
                            ((TextView)findViewById(R.id.txtFilter)).setVisibility(View.GONE);
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
        }catch(Exception e){
            e.printStackTrace();
        }
        baseadapter.notifyDataSetChanged();
        //setListView();
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
//        final View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.mdlive_footer, null, false);
//
//        // If list size is greater than zero then show the bottom footer
//        if (providerListMap != null && providerListMap.size() > 0) {
//            findViewById(R.id.footer).setVisibility(View.GONE);
//
//            if (listView.getFooterViewsCount() == 0) {
//
//                listView.addFooterView(footerView, null, false);
//            }
//        }
//        // If list size is zero then remove the bottom footer & add the list footer
//        else {
//            findViewById(R.id.footer).setVisibility(View.VISIBLE);
//            if (listView.getFooterViewsCount() > 0) {
//                listView.removeFooterView(footerView);
//            }
//        }
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
/*
                doctorId = responArray.get(i).getAsJsonObject().get("id").getAsString();

                imageUrl = responArray.get(i).getAsJsonObject().get("provider_image_url").getAsString();
*/
                if (responArray.get(i).getAsJsonObject().get("speciality").isJsonNull()){
                    specialty="";
                }else{
                    specialty = responArray.get(i).getAsJsonObject().get("speciality").getAsString();
                }

                    doctorId = responArray.get(i).getAsJsonObject().get("id").getAsString();

                    imageUrl = responArray.get(i).getAsJsonObject().get("provider_image_url").getAsString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonArray  affiliationsArray = responArray.get(i).getAsJsonObject().get("provider_groups").getAsJsonArray();
            String nxtavaildate="";
            for(int j=0;j<affiliationsArray.size();j++) {
                groupAffiliations = affiliationsArray.get(j).getAsJsonObject().get("group_name").getAsString();
                Log.v("affiliationsArray-->", groupAffiliations);
            }
            try {
                long nextAvailabilityTimeStamp = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();

//                if(responArray.get(i).getAsJsonObject().get("next_availability").isJsonNull())
//                    strDate = IntegerConstants.DATE_FLAG;
//                else
//                    strDate = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();

                if(!responArray.get(i).getAsJsonObject().get("next_availability").isJsonNull()){
                    shared_timestamp = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();
                    strDate = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();
                    nxtavaildate= MdliveUtils.getReceivedTimeForProvider(strDate,"EST");
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
            appointmentDate = MdliveUtils.getReceivedTimeForProvider(strDate,"EST");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_FALSE);
            map.put("id", doctorId);
            map.put("provider_image_url", imageUrl);
            map.put("availability_type", availabilityType);
            map.put("available_now_status", available_now_status+"");
            map.put("group_name", groupAffiliations);
//            map.put("next_availability",MdliveUtils.getReceivedTimeForProvider(strDate,"EST"));
//            map.put("next_availability",strDate+"");
            map.put("next_availability",nxtavaildate);
            map.put("shared_timestamp",shared_timestamp+"");
            providerListMap.add(map);
            Log.v("check providerlist",providerListMap.toString());
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
            isDoctorOnCallReady = true;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_TRUE);
            map.put("provider_image_url", imageUrl);
            map.put("id", doctorId);
            map.put("availability_type", availabilityType);
            map.put("group_name", groupAffiliations);
            map.put("available_now_status", available_now_status+"");
            map.put("next_availability",MdliveUtils.getReceivedTimeForProvider(strDate,"EST"));
//            map.put("next_availability",strDate+"");
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
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Provider Id",providerListMap.get(position).get("id"));
                Log.v("Provider availabilityType",providerListMap.get(position).get("availability_type"));
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
                Date currenTimeZone1 = (Date) calendar.getTime();
                Log.v("general Timezone-->",calendar.getTimeInMillis()+"");
                Log.v("today Timezone-->",today.getTimeInMillis()+"");
                Log.v("tomrw Timezone-->",tomorrow.getTimeInMillis()+"");

                String sendData="";
                if(timestamp <= today.getTimeInMillis()){
                    sendData = "Today "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
                    Log.v("Kobe Timezone-->","Kobe today");
                }else if(timestamp > today.getTimeInMillis() && timestamp <= tomorrow.getTimeInMillis()){
                    sendData = "Tomorrow "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
                    Log.v("Kobe Timezone-->","Kobe tmr");
                }else{
                    Date currenTimeZone = (Date) calendar.getTime();
                    sendData = sdf.format(currenTimeZone);
                    Log.v("Kobe Timezone-->","Kobe future");
                }

                //Date currenTimeZone = (Date) calendar.getTime();

                //return sdf.format(currenTimeZone);
//                return dateFormat.format(calendar.getTime());
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
            try{
                // Clear the ListView
                providerListMap.clear();
                baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, providerListMap);
                listView.setAdapter(baseadapter);
                handleSuccessResponse(response);


            }catch (Exception e){
                e.printStackTrace();
            }

        }
//        if(resultCode==1){
//            String response=data.getStringExtra("Response");
//            try{
//                // Clear the ListView
//                providerListMap.clear();
//                baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, providerListMap);
//                listView.setAdapter(baseadapter);
//                JSONObject jobj=new JSONObject(response);
//                JSONArray jArray=jobj.getJSONArray("physicians");
//                Log.e("jArray.toString()",jArray.toString());
//                if(jArray.toString().length()<=1){
//                    docOnCalLinLay.setVisibility(View.GONE);
//                    filterMainRl.setVisibility(View.GONE);
//                    MdliveUtils.showDialog(MDLiveChooseProvider.this,jArray.toString(),new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//                }
//                else if(jArray.length()==0)
//                {
//                    docOnCalLinLay.setVisibility(View.GONE);
//                    filterMainRl.setVisibility(View.GONE);
//                    MdliveUtils.showDialog(MDLiveChooseProvider.this,jArray.toString(),new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//                }
//                else{
//                    providerListMap.clear();
//                    handleSuccessResponse(response);
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
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
}
