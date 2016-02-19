package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacy;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyResult;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateFemaleAttributeServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by srinivasan_ka on 10/7/2015.
 */
public class MedicalHistoryPluginActivity extends MDLiveBaseActivity {
    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */


    public LocationCooridnates locationService;
    public LatLng currentLocation;
    public IntentFilter intentFilter;


    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    public void checkMedicalAggregation() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                medicalAggregationJsonObject = response;
                checkAgeAndFemale();
                if(hasFemaleAttribute){
                    updateFemaleAttributes();
                }else{
                    updateMedicalHistory();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error", error.getMessage());
                hideProgress();
                handleVollegyErrorResponse(error);
            }
        };
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(MedicalHistoryPluginActivity.this, null);
        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
    }

    /**
     * Check the age of user and sex whether male or female to enable
     * Pediatric questions.
     */
    public void checkAgeAndFemale() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String gender = sharedpreferences.getString(PreferenceConstants.GENDER, "");
            if(gender.equalsIgnoreCase("Female")){
                JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
                if(healthHistory.has("female_questions")){
                    if(healthHistory.has("is_pregnant") && !healthHistory.isNull("is_pregnant")){
                        if(healthHistory.get("is_pregnant") instanceof Boolean){
                            is_pregnant =  healthHistory.optBoolean("is_pregnant");
                        }
                    }
                    if(healthHistory.has("is_breast_feeding") && !healthHistory.isNull("is_breast_feeding")){
                        if(healthHistory.get("is_breast_feeding") instanceof Boolean){
                            is_breast_feeding =  healthHistory.optBoolean("is_breast_feeding");
                        }
                    }
                    hasFemaleAttribute = true;
                }
            }else{
                hasFemaleAttribute = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean is_pregnant = false, is_breast_feeding = false;
    boolean hasFemaleAttribute = false;
    JSONObject medicalAggregationJsonObject;

    /**
     * Sending the answer details of Female pediatric users
     */
    private void updateFemaleAttributes() {
        HashMap<String, String> femaleAttributes = new HashMap<String, String>();
        femaleAttributes.put("is_pregnant", is_pregnant+"");
        femaleAttributes.put("is_breast_feeding", is_breast_feeding+"");
        HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
        postBody.put("female_questions", femaleAttributes);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                updateMedicalHistory();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                handleVollegyErrorResponse(error);
            }
        };
        UpdateFemaleAttributeServices services = new UpdateFemaleAttributeServices(MedicalHistoryPluginActivity.this, null);
        services.updateFemaleAttributeRequest(new Gson().toJson(postBody), successCallBackListener, errorListener);
    }



    /**
     * This function is used to update Medical history data in service
     * MedicalHistoryUpdateServices :: This class is used to update medical history. This class holds data ot update service
     *
     */
    private void updateMedicalHistory(){
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                getUserPharmacyDetails();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                handleVollegyErrorResponse(error);
            }
        };
        try {
            boolean hasAllergies = false, hasConditions = false, hasMedications = false, hasProcedures = false;
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            hasAllergies = !(healthHistory.getJSONArray("allergies").length() == 0);
            hasConditions = !(healthHistory.getJSONArray("conditions").length() == 0);
            hasMedications = !(healthHistory.getJSONArray("medications").length() == 0);
            HashMap<String,String> updateMap = new HashMap<String,String>();
            updateMap.put("Do you have any health conditions?", hasConditions?"Yes":"No");
            updateMap.put("Are you currently taking any medication?", hasMedications?"Yes":"No");
            updateMap.put("Do you have any Allergies or Drug Sensitivities?", hasAllergies?"Yes":"No");
            updateMap.put("Have you ever had any surgeries or medical procedures?", hasProcedures?"Yes":"No");
            HashMap<String, HashMap<String,String>> medhistoryMap = new HashMap<String, HashMap<String,String>>();
            medhistoryMap.put("medical_history",updateMap);
            MedicalHistoryUpdateServices services = new MedicalHistoryUpdateServices(MedicalHistoryPluginActivity.this, null);
            services.updateMedicalHistoryRequest(medhistoryMap, successCallBackListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }





    
    @Override
    public void onResume() {
        try {
            locationService.setBroadCastData(StringConstants.DEFAULT);
            registerReceiver(locationReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            locationService.setBroadCastData(StringConstants.DEFAULT);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListners();
            }
            unregisterReceiver(locationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*
* This function will get latest default pharmacy details of users from webservice.
* PharmacyService class handles webservice integration.
* @responseListener - Receives webservice informatoin
* @errorListener - Received error information (if any problem in webservice)
* once message received by  @responseListener then it will redirect to handleSuccessResponse function
* to parse message content.
*/
    public void getUserPharmacyDetails() {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                handleVollegyErrorResponse(error);
            }
        };
        callPharmacyService(responseListener, errorListener);
    }


    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener - Pharmacy error response listener
     *  @param responseListener - Pharmacy detail Success response listener
     */


    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
        if(locationService.checkLocationServiceSettingsEnabled(MedicalHistoryPluginActivity.this)){
            showProgress();
            registerReceiver(locationReceiver, intentFilter);
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(MedicalHistoryPluginActivity.this);
        }else{
            PharmacyService services = new PharmacyService(MedicalHistoryPluginActivity.this, null);
            services.doMyPharmacyRequest("","",responseListener, errorListener);
        }
    }


    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(locationReceiver);
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
                    handleSuccessResponse(response);
                }
            };
            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            };
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
                double lat=intent.getDoubleExtra("Latitude",0d);
                double lon=intent.getDoubleExtra("Longitude",0d);
                currentLocation = new LatLng(lat, lon);
                if(lat!=0 && lon!=0){
                    PharmacyService services = new PharmacyService(MedicalHistoryPluginActivity.this, null);
                    services.doMyPharmacyRequest(lat+"", +lon+"",
                            responseListener, errorListener);
                }else{
                    PharmacyService services = new PharmacyService(MedicalHistoryPluginActivity.this, null);
                    services.doMyPharmacyRequest("","",responseListener, errorListener);
                }
            }else{
                PharmacyService services = new PharmacyService(MedicalHistoryPluginActivity.this, null);
                services.doMyPharmacyRequest("","",responseListener, errorListener);
            }
        }
    };

    /* This function handles webservice response and parsing the contents.
   *  Once parsing operation done, then it will update UI
   *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
   */
    Bundle bundletoSend = new Bundle();
    String jsonResponse;

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgress();
            jsonResponse = response.toString();
            if(response.has("message")){
                if(response.getString("message").equals("No pharmacy selected")){
                    getLocationBtnOnClickAction();
                }
            }else{
                JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
                bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
                JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
                bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
                bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
                bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
                bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
                bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
                bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
                bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
                bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
                bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
                bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
                bundletoSend.putString("city", pharmacyDatas.getString("city"));
                bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
                bundletoSend.putString("state", pharmacyDatas.getString("state"));
                String res = response.toString();
                Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.putExtra("Response",res);
                startActivity(i);
                MdliveUtils.startActivityAnimation(MedicalHistoryPluginActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OnClickAction for Location Button Action. This will get the current location. If the current
     * location is received, starts teh MDLivePharmacyResult activity.
     *
     *
     */
    private void getLocationBtnOnClickAction() {
        if(currentLocation != null && currentLocation.latitude != 0 && currentLocation.longitude != 0){
            Intent i = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
            i.putExtra("longitude", currentLocation.latitude+"");
            i.putExtra("latitude", currentLocation.longitude+"");
            i.putExtra("errorMesssage", "No Pharmacies listed in your location");
            startActivity(i);
            MdliveUtils.startActivityAnimation(MedicalHistoryPluginActivity.this);
        }else{
            Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
            i.putExtra("Response",jsonResponse);
            startActivity(i);
            MdliveUtils.startActivityAnimation(MedicalHistoryPluginActivity.this);
        }
    }

    public void handleVollegyErrorResponse(VolleyError error){
        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                MdliveUtils.connectionTimeoutError(getProgressDialog(), MedicalHistoryPluginActivity.this);
            }
        }
    }
}
