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

public class AddMedicalConditionServices extends BaseServicesPlugin{

    public AddMedicalConditionServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void addMedicalConditionsRequest(NetworkSuccessListener<JSONObject> responseListener,
                                            NetworkErrorListener errorListener,
                                            ArrayList<String> name) {
        try {
            HashMap<String, ArrayList<HashMap<String, String>>> conditions = new HashMap<>();

            ArrayList<HashMap<String, String>> namelist = new ArrayList<>();
            for(int i = 0; i<name.size(); i++){
                HashMap<String, String> map = new HashMap<>();
                map.put("name", name.get(i));
                namelist.add(map);
            }
            conditions.put("conditions", namelist);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST,
                    new Gson().toJson(conditions), responseListener, errorListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMedicalConditionsRequest(NetworkSuccessListener<JSONObject> responseListener,
                                            NetworkErrorListener errorListener,
                                            String name) {
        try {
            HashMap<String, ArrayList<HashMap<String, String>>> conditions = new HashMap<>();

            ArrayList<HashMap<String, String>> namelist = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            namelist.add(map);
            conditions.put("conditions", namelist);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST,
                    new Gson().toJson(conditions), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

