package com.mdlive.mobile.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.mobile.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.CurrentLocationServices;
import com.mdlive.unifiedmiddleware.services.ZipCodeServices;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sudha_s on 5/14/2015.
 */
public class MDLiveLocation extends Activity implements View.OnClickListener {
    private ProgressDialog pDialog;
    private  EditText ZipcodeEditTxt;
    private TextView CurrentLocationTxt;
    private String SelectedZipCodeCity;
    private  ArrayList<String> StateName = new ArrayList<String>();
    private ArrayList<String> LongNameList = new ArrayList<String>();
    private ArrayList<String> ShortNameList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        pDialog = Utils.getProgressDialog(LocalisationHelper.getLocalizedStringFromPrefs(this,getResources().getString(R.string.please_wait)), this);
        TextView StateTxt = (TextView)findViewById(R.id.StateTxt);
        StateTxt.setOnClickListener(this);
        ZipcodeEditTxt = (EditText)findViewById(R.id.ZipEditTxt);
        ZipcodeEditTxt.setOnClickListener(this);
        Button SavContinueBtn = (Button) findViewById(R.id.SavContinueBtn);
        SavContinueBtn.setOnClickListener(this);
        CurrentLocationTxt = (TextView)findViewById(R.id.currentLocation);
        CurrentLocationTxt.setOnClickListener(this);
//        String localisedJSON = LocalisationHelper.getJsonObjectForLanguage(this,"en.json");

        /**
         *
         * This is to Parse the location that is to get long name and short name
         * of the state from the localisation
         */
        //todo: Remove the HardCoded Strings once you get it from Service
LongNameList.add("California");
LongNameList.add("Florida");
LongNameList.add("Alabama");


ShortNameList.add("CA");
ShortNameList.add("FL");
ShortNameList.add("AL");

    }
    /**
     *
     * On Click listener event for the views
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.StateTxt:
                showListViewDialog(LongNameList,(TextView)v);
                break;

            case R.id.ZipEditTxt:
                break;

            case R.id.currentLocation:
                getLocationCoordinates();
                break;

            case R.id.SavContinueBtn:
                String getEditTextValue = ZipcodeEditTxt.getText().toString();
                loadZipCode(getEditTextValue);
                Intent ZipLocationIntent = new Intent(MDLiveLocation.this,MDLiveGetStarted.class);
                startActivity(ZipLocationIntent);
                break;

        }
    }

    /**
     *
     * Load Current location.
     * Class : CurrentLocationServices - Service class used to fetch the current latitude and longitude
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadCurrentLocation(String latitude,String longitude) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                CurrentLocationResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveLocation.this);
                    }
                }
            }};

        CurrentLocationServices currentlocationservices = new CurrentLocationServices(MDLiveLocation.this, null);
        currentlocationservices.getCurrentLocation(latitude, longitude, responseListener, errorListener);
    }

    /**
     *
     * Load ZipCode.
     * Class : zipcodeservices - Service class used to fetch the Zip Code information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadZipCode(String EditTextValue) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                ZipCodeResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveLocation.this);
                    }
                }
            }};



        ZipCodeServices zipcodeservices = new ZipCodeServices(MDLiveLocation.this, null);
        zipcodeservices.getZipCodeServices(EditTextValue,responseListener, errorListener);
    }

    /**
     *
     * Successful Response Handler for Zip Code.
     *
     */
    private void ZipCodeResponse(JSONObject response) {
        try {

            pDialog.dismiss();
           //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonArray  responArray = responObj.get("results").getAsJsonArray();
           for(int i = 0;i < responArray.size(); i++) {
               JsonObject ZipJsonObject = responArray.get(i).getAsJsonObject();
               JsonArray ZipresponArray = ZipJsonObject.get("address_components").getAsJsonArray();

               for (int j = 0; j < ZipresponArray.size(); j++) {
                   JsonObject localZip = ZipresponArray.get(j).getAsJsonObject();
                   JsonArray TyprArray = localZip.get("types").getAsJsonArray();
                   for (int k = 0; k < TyprArray.size(); k++) {
                       String zip = TyprArray.get(k).getAsString();
                       if(zip.equals(LocalisationHelper.getLocalizedStringFromPrefs(this,getResources().getString(R.string.administrative_level1))))
                       {
                            SelectedZipCodeCity = localZip.get("short_name").getAsString();
                           Log.e("Current SelectedZipCodeCity State",SelectedZipCodeCity);
                           SaveZipCodeCity(SelectedZipCodeCity);
                       }
                   }

               }
           }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     *  Successful Response Handler for getting Current Location
     *
     */

    private void CurrentLocationResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            String state = responObj.get("state").getAsJsonObject().getAsString();
            SaveZipCodeCity(state);
            Intent ZipLocationIntent = new Intent(MDLiveLocation.this,MDLiveGetStarted.class);
            startActivity(ZipLocationIntent);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *
     *  getLocationCoordinates method is to get the Current latitude and longitude of the location
     *
     */
     public void getLocationCoordinates()
     {
         LocationCooridnates locationService = new LocationCooridnates();

         if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
             locationService.getLocation(this, new LocationCooridnates.LocationResult(){
                 @Override
                 public void gotLocation(Location location) {
                     if(location != null)
                     loadCurrentLocation(location.getLatitude()+"",location.getLongitude()+"");
                     else
                         Toast.makeText(getApplicationContext(), "Unable to get location!", Toast.LENGTH_SHORT).show();
                 }
             });
         }else{
             Toast.makeText(getApplicationContext(), "Please enable location service...", Toast.LENGTH_SHORT).show();
         }
     }
    /**
     *      @param ZipCodeCity : Pass the selected zipcode String
     *      The Corresponding Zip Code and the Short name of the city should be saved in the Preferences and will be
     *      triggerred in the Requird places.
     *
     *
     */


    public void SaveZipCodeCity(String ZipCodeCity)
    {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES,ZipCodeCity);
        editor.commit();
    }
    /**
     *      @param list : Dependent users array list
     *     Instantiating array adapter to populate the listView
     *     The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     *
     */
    private void showListViewDialog (final ArrayList<String> list,final TextView SelectedText) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveLocation.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = ShortNameList.get(position);
                SelectedText.setText(selectedText);
                SaveZipCodeCity(selectedText);
                dialog.dismiss();
            }
        });
    }

}
