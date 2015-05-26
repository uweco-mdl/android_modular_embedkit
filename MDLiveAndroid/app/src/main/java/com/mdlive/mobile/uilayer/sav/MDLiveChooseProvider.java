package com.mdlive.mobile.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.adapters.ChooseProviderAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ChooseProviderServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


/**
 * Created by sudha_s on 5/12/2015.
 */
public class MDLiveChooseProvider extends Activity implements View.OnClickListener {
    private ListView listView;
    private ProgressDialog pDialog;
    private ImageView refineSearchImg;
    private String PatientName,Speciality,AvailabilityType,ImageUrl,DoctorId;
    private long StrDate;
    private ArrayList<HashMap<String, String>> ProviderListMap;
    ChooseProviderAdapter baseadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_provider);
//        ViewGroup view = (ViewGroup)getWindow().getDecorView();
//        LocalisationHelper.localiseLayout(this, view);
        refineSearchImg = (ImageView)findViewById(R.id.refinesearch);
        refineSearchImg.setOnClickListener(this);
        ProviderListMap = new ArrayList<HashMap<String, String>>();
        pDialog = Utils.getProgressDialog(LocalisationHelper.getLocalizedStringFromPrefs(this,getResources().getString(R.string.please_wait)), this);
        ChooseProviderResponseList();

    }

    /**
     *
     * Choose Provider List Details.
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void ChooseProviderResponseList() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveChooseProvider.this);
                    }
                }
            }};
        ChooseProviderServices services = new ChooseProviderServices(MDLiveChooseProvider.this, null);
        services.doChooseProviderRequest("FL", "3", successCallBackListener, errorListener);
    }


    /**
     *
     *  Successful Response Handler for Load Basic Info.
     *   Here if the doctor on call String returns true then the Doctor On Call should
     *   be available else the doctor on call should be hidden.
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();


            //Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
//            String StrDoctorOnCall =  responObj.get("doctor_on_call").getAsString();
            String StrDoctorOnCall =  "true";
            if(StrDoctorOnCall.equals(getResources().getString(R.string.true_txt)))
            {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name",PatientName);
                map.put("isheader","1");
                map.put("speciality",Speciality);
                map.put("provider_image_url",ImageUrl);
                map.put("id",DoctorId);
                map.put("availability_type",AvailabilityType);
                map.put("next_availability",getDateCurrentTimeZone(StrDate));
                ProviderListMap.add(map);
            }
            JsonArray  responArray = responObj.get("physicians").getAsJsonArray();
            for(int i=0;i<responArray.size();i++) {
                PatientName =  responArray.get(i).getAsJsonObject().get("name").getAsString();
                Speciality =  responArray.get(i).getAsJsonObject().get("speciality").getAsString();
                DoctorId =  responArray.get(i).getAsJsonObject().get("id").getAsString();
                ImageUrl = responArray.get(i).getAsJsonObject().get("provider_image_url").getAsString();

                try {
                    if(responArray.get(i).getAsJsonObject().get("next_availability").isJsonNull())
                        StrDate = 0;
                    else
                        StrDate = responArray.get(i).getAsJsonObject().get("next_availability").getAsInt();

                } catch (Exception e) {
                    e.printStackTrace();
                    StrDate = 0;
                }
                AvailabilityType =  responArray.get(i).getAsJsonObject().get("availability_type").getAsString();

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name",PatientName);
                map.put("isheader","0");
                map.put("speciality",Speciality);
                map.put("id",DoctorId);
                map.put("provider_image_url",ImageUrl);
                map.put("availability_type",AvailabilityType);
                map.put("next_availability",getDateCurrentTimeZone(StrDate));
                ProviderListMap.add(map);

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.chooseProviderList);
        baseadapter = new ChooseProviderAdapter(getApplicationContext(), ProviderListMap);
        listView.setAdapter(baseadapter);
        ListItemClickListener();
    }
    public void ListItemClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaveDoctorId(ProviderListMap.get(position).get("id"));
                Intent Reasonintent = new Intent(MDLiveChooseProvider.this,MDLiveProviderDetails.class);
                startActivity(Reasonintent);
                finish();
            }
        });
    }

    /**
     * getDateCurrentTimeZone method will convert the Timestamp to the current
     * Simple Date Format.
     * @param timestamp the timestamp in milli seconds will be passed.
     * @return
     */
    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            if(timestamp != 0){
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
        return null;
    }

    /**
     *      @param DocorId : Pass the selected Doctor's Id String
     *      The Corresponding id of the Doctor should be saved in the Preferences and will be
     *      triggerred in the Requird places.
     *
     *
     */
    public void SaveDoctorId(String DocorId)
    {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES,DocorId);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.refinesearch:
//                Intent Reasonintent = new Intent(MDLiveChooseProvider.this,MDLiveSearchProvider.class);
//                startActivity(Reasonintent);

        }

    }
}
