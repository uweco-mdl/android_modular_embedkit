package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.sav.adapters.PediatricProfileAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.PediatricPostServices;
import com.mdlive.unifiedmiddleware.services.myhealth.PediatricProfileServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class : MDLivePediatricProfile - This class is used to View the Pediatric
 * profile below 12 years.
 */
public class MDLivePediatricProfile extends Activity {
    private ProgressDialog pDialog;
    private ListView listView;
    private Button saveBtn;
    private ArrayList<HashMap<String, String>> PediatricList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> postBodyArray = new ArrayList<HashMap<String, String>>();
    PediatricProfileAdapter baseadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pediatric_profile);
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", this);
        saveBtn = (Button) findViewById(R.id.SavContinueBtn);
        //Load Lifestyle Services
        LoadPediatricProfileServices();
    }

    /**
     * Load Pediatric Profile Services.
     * Class : PediatricProfileServices - Service class used to fetch the Pediatric Profile Services     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void LoadPediatricProfileServices() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlePediatricResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLivePediatricProfile.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLivePediatricProfile.this);
                }
            }
        };
        PediatricProfileServices pediatricServices = new PediatricProfileServices(MDLivePediatricProfile.this, null);
        pediatricServices.getPediatricServices(responseListener, errorListener);
    }

    /**
     * Post Pediatric Profile Services.
     * Class : PostPediatricServices - Service class used to updated the pediatric Profile to the Services
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void PostLifeStyleServices() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
               Log.e("Respone pediatric",response.toString());
                handlePostPediatricResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLivePediatricProfile.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLivePediatricProfile.this);
                }
            }
        };
        postPediatricValues();
        PediatricPostServices pediatricServices = new PediatricPostServices(MDLivePediatricProfile.this, null);
        pediatricServices.postPediatricServices(postBodyArray,responseListener, errorListener);
    }
    /**
     * This methos is to post the pediatric values that is the selected pediatric
     * values whether it can be either yes or no or the condition values.
     */
    private void postPediatricValues() {
        postBodyArray.clear();
        for(HashMap<String, String> item : baseadapter.getResult()){
            if(!item.isEmpty()){
                HashMap <String, String> temps = new HashMap<String, String>();
                temps.put("name", item.get("name"));
                temps.put("value", item.get("value"));
                postBodyArray.add(temps);
            }
        }
    }

    /**
     * Successful Response Handler for getting Current Location
     */

    private void handlePostPediatricResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            Log.e("MDlivePediatric->",responObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     *
     *  Successful Response Handler for getting Current Location
     *
     */

    private void handlePediatricResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonObject Str_Pediatric = responObj.get("pediatric").getAsJsonObject();
            Log.e("Life Response-->",responObj.toString());
            JsonArray responArray = Str_Pediatric.get("questions").getAsJsonArray();
            PediatricList.clear();
            HashMap<String,String> map;
            PediatricList.add(new HashMap<String, String>());
            PediatricParseData(responArray);
            ViewForList();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void ViewForList() {
        listView = (ListView) findViewById(R.id.pediatricList);
        baseadapter = new PediatricProfileAdapter(MDLivePediatricProfile.this, PediatricList);
        listView.setAdapter(baseadapter);
        /**
         *  This method is used to update the Pediatric Profile below 12 years and above 2 years
         *  also it will update below 2 years.
         */
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Pediatric value",baseadapter.getResult().toString());
                baseadapter.saveAllQuestionAnswersForPerdiotric();
                PostLifeStyleServices();
                finish();
            }
        });
    }
    /**
     *
     *  Get the Pediatric datas from the services that is to fetch
     *  the name and values of the pediatric profile.
     *
     */

    private void PediatricParseData(JsonArray responArray) {
        HashMap<String, String> map;
        for (int i = 0; i < responArray.size(); i++) {
            JsonObject lifeStyleObject = responArray.get(i).getAsJsonObject();
            String name = lifeStyleObject.get("name").getAsString();
            String value = lifeStyleObject.get("value").getAsString();
            map = new HashMap<String,String>();
            map.put("condition",name);
            map.put("name",name);
            map.put("active",value);
            map.put("value",value);
            if(!name.equals("Birth complications explanation")
                    && !name.equals("Newborn complication explanation")
                    && !name.equals("Last shot"))
                PediatricList.add(map);
        }
    }
}
