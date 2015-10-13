package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddAllergyServices extends BaseServicesPlugin{
    public AddAllergyServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void addAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener,
                                  ArrayList<String> name) {
        try {
            HashMap<String, ArrayList<HashMap<String, String>>> allergies = new HashMap<>();
            ArrayList<HashMap<String, String>> namelist = new ArrayList<>();
            for(int i = 0; i<name.size(); i++){
                HashMap<String, String> map = new HashMap<>();
                map.put("name", name.get(i));
                namelist.add(map);
            }
            allergies.put("allergies", namelist);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST,
                    new Gson().toJson(allergies), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener,
                                  String name) {
        try {
            HashMap<String, ArrayList<HashMap<String, String>>> allergies = new HashMap<>();
            ArrayList<HashMap<String, String>> namelist = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            namelist.add(map);
            allergies.put("allergies", namelist);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST,
                    new Gson().toJson(allergies), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}