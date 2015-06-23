package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.location.CurrentLocationServices;
import com.mdlive.unifiedmiddleware.services.location.ZipCodeServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sudha_s on 5/14/2015.
 */
public class MDLiveLocation extends Activity {
    private ProgressDialog pDialog;
    private EditText ZipcodeEditTxt;
    private TextView CurrentLocationTxt,StateTxt;
    private String SelectedZipCodeCity;
    private ArrayList<String> StateName = new ArrayList<String>();
    private List<String> LongNameList = new ArrayList<String>();
    private List<String> ShortNameList = new ArrayList<String>();
    private String ZipCodeCity,selectedCity,longNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_location);
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        pDialog = Utils.getProgressDialog("Please Wait...", this);
        StateTxt = (TextView) findViewById(R.id.StateTxt);
        StateTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(LongNameList, (TextView) v);
            }
        });
        ZipcodeEditTxt = (EditText) findViewById(R.id.ZipEditTxt);
        TextView SavContinueBtn = (TextView) findViewById(R.id.txtApply);
        SavContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ZipcodeEditTxt.getText().toString().length()!=0||StateTxt.getText().toString().length()!=0){
                    if(ZipcodeEditTxt.getText().length()!=0){
                        String getEditTextValue = ZipcodeEditTxt.getText().toString();
                        if(Utils.validateZipCode(getEditTextValue)){
                            loadZipCode(getEditTextValue);
                        }else{
                            Utils.alert(pDialog,MDLiveLocation.this,"Please enter a Zipcode or select a State");
                        }
                    }else{
                        finish();
                    }
                }else{
                    Utils.alert(pDialog,MDLiveLocation.this,"Please enter a Zipcode or select a State");
                }
               /* if(StateTxt.getText().length()==0){
                    String getEditTextValue = ZipcodeEditTxt.getText().toString();
                    String zipcodePattern="^\\d{5}([\\-]?\\d{4})?$";
                    validateZipCode(getEditTextValue,zipcodePattern);

                }else{
                    finish();
                }*/
            }
        });
        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveLocation.this);
                finish();
            }
        });
        CurrentLocationTxt = (TextView) findViewById(R.id.currentLocation);
        CurrentLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                getLocationCoordinates();
            }
        });
//        String localisedJSON = LocalisationHelper.getJsonObjectForLanguage(this,"en.json");

        /**
         *
         * This is to Parse the location that is to get long name and short name
         * of the state from the localisation
         */



        LongNameList = Arrays.asList(getResources().getStringArray(R.array.stateName));
        ShortNameList = Arrays.asList(getResources().getStringArray(R.array.stateCode));
    }

    /*public void validateZipCode(String zipCode){
        String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
        Pattern zipcodePattern = Pattern.compile(regex);
        Matcher matcher = zipcodePattern.matcher(zipCode);
        if(matcher.matches()){
            loadZipCode(zipCode);
        }else{
            Utils.alert(pDialog,MDLiveLocation.this,"Please enter a Zipcode or select a State");
            //Toast.makeText(MDLiveLocation.this,"Please Enter Valid Zip code",Toast.LENGTH_SHORT).show();
        }
    }*/

  /*  public void validateZipCode(String editValue,String zipcodePattern ){
        if(editValue.matches(zipcodePattern)){
            loadZipCode(editValue);
            finish();
        }else{
            Utils.alert(pDialog, MDLiveLocation.this, "Please enter valid zip code or select state from the list below");
            //Toast.makeText(MDLiveLocation.this,"Please Enter Valid Zip code",Toast.LENGTH_SHORT).show();
        }
    }*/

    /**
     * Load Current location.
     * Class : CurrentLocationServices - Service class used to fetch the current latitude and longitude
     * <p/>
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * <p/>
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadCurrentLocation(String latitude, String longitude) {
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
            }
        };

        CurrentLocationServices currentlocationservices = new CurrentLocationServices(MDLiveLocation.this, null);
        currentlocationservices.getCurrentLocation(latitude, longitude, responseListener, errorListener);
    }

    /**
     * Load ZipCode.
     * Class : zipcodeservices - Service class used to fetch the Zip Code information
     * <p/>
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * <p/>
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
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
            }
        };

        ZipCodeServices zipcodeservices = new ZipCodeServices(MDLiveLocation.this, null);
        zipcodeservices.getZipCodeServices(EditTextValue, responseListener, errorListener);
    }

    /**
     * Successful Response Handler for Zip Code.
     */
    private void ZipCodeResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());

            JsonArray responArray = responObj.get("results").getAsJsonArray();
            for (int i = 0; i < responArray.size(); i++) {
                JsonObject ZipJsonObject = responArray.get(i).getAsJsonObject();
                JsonArray ZipresponArray = ZipJsonObject.get("address_components").getAsJsonArray();

                for (int j = 0; j < ZipresponArray.size(); j++) {
                    JsonObject localZip = ZipresponArray.get(j).getAsJsonObject();
                    JsonArray TyprArray = localZip.get("types").getAsJsonArray();
                    for (int k = 0; k < TyprArray.size(); k++) {
                        String zip = TyprArray.get(k).getAsString();
                        Log.e("responObj", zip.toString());
                        if (zip.equalsIgnoreCase("administrative_area_level_1")) {
                            SelectedZipCodeCity = localZip.get("short_name").getAsString();
                            Log.e("Results", SelectedZipCodeCity);
                            ZipCodeCity = SelectedZipCodeCity;
                            selectedCity = ZipCodeCity;
                            SaveZipCodeCity(SelectedZipCodeCity);
//                            SaveZipCodeCity(SelectedZipCodeCity);
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra("ZipCodeCity", ZipCodeCity);
//                            setResult(RESULT_OK, resultIntent);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Successful Response Handler for getting Current Location
     */

    private void CurrentLocationResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            //Fetch Data From the Services
            selectedCity = response.getString("state");
//            SaveZipCodeCity(response.getString("state"));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getLocationCoordinates method is to get the Current latitude and longitude of the location
     */
    public void getLocationCoordinates() {
        LocationCooridnates locationService = new LocationCooridnates();

        if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
            locationService.getLocation(this, new LocationCooridnates.LocationResult() {
                @Override
                public void gotLocation(final Location location) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (location != null) {
                                loadCurrentLocation(location.getLatitude() + "", location.getLongitude() + "");
                            }
                            else{
                                pDialog.dismiss();
                                Utils.showGPSSettingsAlert(MDLiveLocation.this);
                            }

                        }
                    });

                }
            });
        } else {
            Utils.showGPSSettingsAlert(MDLiveLocation.this);
//            Toast.makeText(getApplicationContext(), "Please enable location service...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param ZipCodeCity : Pass the selected zipcode String
     *                    The Corresponding Zip Code and the Short name of the city should be saved in the Preferences and will be
     *                    triggerred in the Requird places.
     */


    public void SaveZipCodeCity(String ZipCodeCity) {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, selectedCity);
        editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, longNameText);
        editor.commit();
    }

    /**
     * @param list : Dependent users array list
     *             Instantiating array adapter to populate the listView
     *             The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     */
    private void showListViewDialog(final List<String> list, final TextView SelectedText) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveLocation.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = ShortNameList.get(position);
                longNameText = LongNameList.get(position);
                SelectedText.setText(selectedText);
                selectedCity = selectedText;
//                SaveZipCodeCity(selectedText);
//                ZipCodeCity = selectedText;
                dialog.dismiss();
            }
        });
    }

}