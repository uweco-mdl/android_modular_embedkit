package com.mdlive.embedkit.uilayer.sav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.sav.adapters.ChooseProviderAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ChooseProviderServices;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


/**
 * This class basically returns the provider list and the doctor on call.The Doctor on call
 * will be visible only when the response from the service for the Doctor on call is true
 * and Doctor on call will be hidden when the response is false.
 */
public class MDLiveChooseProvider extends MDLiveBaseActivity {

    private ListView listView;
    private ProgressDialog pDialog;
    private String providerName,speciality,availabilityType, imageUrl, doctorId, appointmentDate;
    private long StrDate;
    private ArrayList<HashMap<String, String>> ProviderListMap;
    private ChooseProviderAdapter baseadapter;
    private boolean isDoctorOnCallReady = false;
    private LinearLayout DocOnCalLinLay;
    private RelativeLayout filterMainRl;
    private Button seenextAvailableBtn;
    private TextView loadingTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider);
        Initailization();
        ChooseProviderResponseList();
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
        pDialog = MdliveUtils.getProgressDialog("Please wait...", this);
        ProviderListMap = new  ArrayList<HashMap<String, String>>();
        DocOnCalLinLay = (LinearLayout)findViewById(R.id.DocOnCalLinLay);
        filterMainRl = (RelativeLayout)findViewById(R.id.filterMainRl);
        loadingTxt= (TextView)findViewById(R.id.loadingTxt);
        setProgressBar(findViewById(R.id.progressBar));
        seenextAvailableBtn = (Button) findViewById(R.id.seenextAvailableBtn);
        listView = (ListView) findViewById(R.id.chooseProviderList);

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveChooseProvider.this);
                onBackPressed();
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
        seenextAvailableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MDLiveChooseProvider.this,MDLiveReasonForVisit.class);
                startActivity(intent);
                MdliveUtils.startActivityAnimation(MDLiveChooseProvider.this);

            }
        });
        ((TextView)findViewById(R.id.filterTxt)).setOnClickListener(new View.OnClickListener() {
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
                Log.d("Error Response", error.toString());
                setInfoVisibilty();
                DocOnCalLinLay.setVisibility(View.VISIBLE);
                filterMainRl.setVisibility(View.GONE);
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
                                    MdliveUtils.showDialog(MDLiveChooseProvider.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, "OK", null, new DialogInterface.OnClickListener() {
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
                        MdliveUtils.handelVolleyErrorResponse(MDLiveChooseProvider.this, error, null);
                    }
                }catch(Exception e){
                    setInfoVisibilty();
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveChooseProvider.this);
                    e.printStackTrace();
                }
            }};
        ChooseProviderServices services = new ChooseProviderServices(MDLiveChooseProvider.this, pDialog);
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        services.doChooseProviderRequest(settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"), StringConstants.PROVIDERTYPE, successCallBackListener, errorListener);
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
            setInfoVisibilty();
            DocOnCalLinLay.setVisibility(View.GONE);
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response);
            String StrDoctorOnCall =  responObj.get("doctor_on_call").getAsString();
            if(StrDoctorOnCall.equals("false"))
            {
                JsonArray  responArray = responObj.get("physicians").getAsJsonArray();
                if(responArray.toString().contains(StringConstants.NO_PROVIDERS)){
                    // Here the Array has "No Providers listed with given criteria" string in response
                    MdliveUtils.showDialog(MDLiveChooseProvider.this,StringConstants.NO_PROVIDERS,new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    DocOnCalLinLay.setVisibility(View.GONE);
                    filterMainRl.setVisibility(View.GONE);

                    return;
                }else {
                    // Here the Array has blank string in response
                    if (responArray.size() > 0) {
                        DocOnCalLinLay.setVisibility(View.GONE);
                        filterMainRl.setVisibility(View.VISIBLE);
                        doctorOnCallButtonClick();
                    } else {
                        MdliveUtils.alert(pDialog, MDLiveChooseProvider.this, StringConstants.NO_PROVIDERS);
                        DocOnCalLinLay.setVisibility(View.GONE);
                        filterMainRl.setVisibility(View.GONE);
                    }
                }
            }
            //Setting the Doctor On Call Header
            JsonArray  responArray = responObj.get("physicians").getAsJsonArray();
            if(responArray.toString().contains(StringConstants.NO_PROVIDERS)){
                doctorOnCallButtonClick();
            }else{
                setHeaderContent(StrDoctorOnCall);
                setBodyContent(responArray);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
          setListView();
    }
    /**
     *
     *  Set the ListView values.Here the Listview is populated with two list values
     *  the flag value for the header will ba added for displaying the DoctorOnCall
     *  and the doctor's list will be added in the other list.The doctor name and
     *  the speciality will be displayed here
     *
     */
    private void setListView() {
        showOrHideFooter();
        baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, ProviderListMap);
        listView.setAdapter(baseadapter);
        baseadapter.notifyDataSetChanged();
        ListItemClickListener();
    }

    /*
    * shows or hide list footer/ bottom footer. This is for the static and the dynamic footer
    * implementation. If there is no list that is the response is null then the static footer
    * has been implemented.In case if the list has items then the footer in the listview
    * has been implemented.
    * */
    public void showOrHideFooter() {
        final View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.mdlive_footer, null, false);

        // If list size is greater than zero then show the bottom footer
        if (ProviderListMap != null && ProviderListMap.size() > 0) {
            findViewById(R.id.footer).setVisibility(View.GONE);

            if (listView.getFooterViewsCount() == 0) {

                listView.addFooterView(footerView, null, false);
            }
        }
        // If list size is zero then remove the bottom footer & add the list footer
        else {
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
            if (listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(footerView);
            }
        }
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
        for(int i=0;i<responArray.size();i++) {
            providerName =  responArray.get(i).getAsJsonObject().get("name").getAsString();
            speciality =  responArray.get(i).getAsJsonObject().get("speciality").getAsString();
            doctorId =  responArray.get(i).getAsJsonObject().get("id").getAsString();
            imageUrl = responArray.get(i).getAsJsonObject().get("provider_image_url").getAsString();
            try {

                if(responArray.get(i).getAsJsonObject().get("next_availability").isJsonNull())
                    StrDate = IntegerConstants.DATE_FLAG;
                else
                    StrDate = responArray.get(i).getAsJsonObject().get("next_availability").getAsLong();

            } catch (Exception e) {
                e.printStackTrace();
                StrDate = IntegerConstants.DATE_FLAG;
            }
            availabilityType =  responArray.get(i).getAsJsonObject().get("availability_type").getAsString();
            appointmentDate = getDateCurrentTimeZone(StrDate);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_FALSE);
            map.put("speciality", speciality);
            map.put("id", doctorId);
            map.put("provider_image_url", imageUrl);
            map.put("availability_type", availabilityType);
            //map.put("next_availability",getDateCurrentTimeZone(StrDate));
            ProviderListMap.add(map);
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

    private void setHeaderContent(String strDoctorOnCall) {
        if(StringConstants.TRUE.equalsIgnoreCase(strDoctorOnCall))
        {
            isDoctorOnCallReady = true;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", providerName);
            map.put("isheader",StringConstants.ISHEADER_TRUE);
            map.put("speciality", speciality);
            map.put("provider_image_url", imageUrl);
            map.put("id", doctorId);
            map.put("availability_type", availabilityType);
            //map.put("next_availability",getDateCurrentTimeZone(StrDate));
            ProviderListMap.add(map);
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
                    Log.e("Provider Id",ProviderListMap.get(position).get("id"));
                    saveDoctorId(ProviderListMap.get(position).get("id"), ProviderListMap.get(position).get("next_availability"),
                            ProviderListMap.get(position).get("name"));
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date currenTimeZone = (Date) calendar.getTime();
                return sdf.format(currenTimeZone);
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
    public void saveDoctorId(String DocorId, String AppointmentDate, String docName)
    {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES,DocorId);
        editor.putString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, docName);
        editor.putString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, AppointmentDate);
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
                ProviderListMap.clear();
                baseadapter = new ChooseProviderAdapter(MDLiveChooseProvider.this, ProviderListMap);
                listView.setAdapter(baseadapter);

                JSONObject jobj=new JSONObject(response);
                JSONArray jArray=jobj.getJSONArray("physicians");

                if(jArray.toString().contains(StringConstants.NO_PROVIDERS)){

                    DocOnCalLinLay.setVisibility(View.GONE);
                    filterMainRl.setVisibility(View.GONE);
                    MdliveUtils.alert(pDialog, MDLiveChooseProvider.this, jArray.get(0).toString());

                }
                else if(jArray.length()==0)
                {
                    DocOnCalLinLay.setVisibility(View.GONE);
                    filterMainRl.setVisibility(View.GONE);
                    MdliveUtils.alert(pDialog, MDLiveChooseProvider.this, StringConstants.NO_PROVIDERS);
                }

                else{
                    ProviderListMap.clear();
                    handleSuccessResponse(response);
                }

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
        loadingTxt.setVisibility(View.VISIBLE);
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
