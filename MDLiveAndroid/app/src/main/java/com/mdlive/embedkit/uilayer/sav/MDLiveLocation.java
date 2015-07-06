package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private String ZipCodeCity,selectedCity,longNameText,shortNameText,zipcode_longNameText;
    private int keyDel=0;
    boolean isCityFound=false;

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
                ZipcodeEditTxt.setText("");
                showListViewDialog(LongNameList, (TextView) v);
            }
        });
        Utils.checkGpsLocation(MDLiveLocation.this);
        ZipcodeEditTxt = (EditText) findViewById(R.id.ZipEditTxt);
        ZipcodeEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* ZipcodeEditTxt.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL)
                            keyDel = 1;
                        return false;
                    }
                });

                if (keyDel == 0) {
                    int len = ZipcodeEditTxt.getText().length();
                    if(len == 5) {
                        ZipcodeEditTxt.setText(ZipcodeEditTxt.getText() + "-");
                        ZipcodeEditTxt.setSelection(ZipcodeEditTxt.getText().length());
                    }
                } else {
                    keyDel = 0;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ZipcodeEditTxt.getText().toString().length()>=9){
                    if(!ZipcodeEditTxt.getText().toString().contains("-")){
                        String formattedString=Utils.zipCodeFormat(Long.parseLong(ZipcodeEditTxt.getText().toString()));
                        ZipcodeEditTxt.setText(formattedString);
                    }

                }

            }
        });
        TextView SavContinueBtn = (TextView) findViewById(R.id.txtApply);
        SavContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveLocation.this);
                if(ZipcodeEditTxt.getText().toString().length()!=0||StateTxt.getText().toString().length()!=0){
                    if(ZipcodeEditTxt.getText().length()!=0){
                        String getEditTextValue = ZipcodeEditTxt.getText().toString();
                        if(Utils.validateZipCode(getEditTextValue)){
                            loadZipCode(getEditTextValue);
                        }else{
                            Utils.alert(pDialog,MDLiveLocation.this,"Please enter a valid Zip Code");
                        }
                    }else{
                        SaveZipCodeCity(selectedCity);
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
                Utils.handelVolleyErrorResponse(MDLiveLocation.this,error,pDialog);
               /* if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveLocation.this);
                    }
                }*/
            }
        };

        CurrentLocationServices currentlocationservices = new CurrentLocationServices(MDLiveLocation.this, pDialog);
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

        ZipCodeServices zipcodeservices = new ZipCodeServices(MDLiveLocation.this, pDialog);
        zipcodeservices.getZipCodeServices(EditTextValue, responseListener, errorListener);
    }

    /**
     * Successful Response Handler for Zip Code.
     */
    private void ZipCodeResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services

            Log.e("Response Zip ,ciode",response.toString());

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());


            JsonArray responArray = responObj.get("results").getAsJsonArray();
            if(!response.toString().contains("ZERO_RESULTS"))
            {
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
                                //This is for long name like Florida.

                                zipcode_longNameText = localZip.get("long_name").getAsString();
                                //This is for Short name like FL

                                for(int l=0;l< Arrays.asList(getResources().getStringArray(R.array.stateName)).size();l++) {
                                    if (SelectedZipCodeCity.equals(Arrays.asList(getResources().getStringArray(R.array.stateCode)).get(l))) {
                                        longNameText = Arrays.asList(getResources().getStringArray(R.array.stateName)).get(l);
                                        SaveZipCodeCity(longNameText);
                                        isCityFound=true;
                                        Log.e("Location Service -->", Arrays.asList(getResources().getStringArray(R.array.stateName)).get(l));
                                        break;

                                    }
                                }
                                if(!isCityFound){
                                    Utils.alert(pDialog, MDLiveLocation.this, "Unable to find location by Zipcode.");
                                }


//                            SaveZipCodeCity(SelectedZipCodeCity);
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra("ZipCodeCity", ZipCodeCity);
//                            setResult(RESULT_OK, resultIntent);
                            }
                        }

                    }
                }
            }else{

          Utils.alert(pDialog, MDLiveLocation.this, "Unable to find location by Zipcode.");

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
        String activityCaller  = getIntent().getStringExtra("activitycaller");
        Log.e("Caller bname",activityCaller);
        if(activityCaller.equals("getstarted")){
            editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, shortNameText);
            editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, longNameText);
            editor.commit();
        }
        else
        {
            SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
            SharedPreferences.Editor searchEditor = searchPref.edit();
            searchEditor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, shortNameText);
            searchEditor.putString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, longNameText);
            searchEditor.commit();

        }
        finish();

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
        lv.setCacheColorHint(Color.TRANSPARENT);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shortNameText = ShortNameList.get(position);
                longNameText = LongNameList.get(position);
                SelectedText.setText(longNameText);
                selectedCity = shortNameText;
                dialog.dismiss();
            }
        });
    }

}
