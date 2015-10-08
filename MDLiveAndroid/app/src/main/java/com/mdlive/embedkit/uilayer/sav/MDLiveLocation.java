package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.location.CurrentLocationServices;
import com.mdlive.unifiedmiddleware.services.location.ZipCodeServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.newInstance;

/**
 * This Class defines the location.That is the user can select the location
 * either by using Current location or the user can select the location by
 * using the Zip code or either by selecting the state or by selecting
 * the City.
 */
public class MDLiveLocation extends MDLiveBaseActivity {
    private EditText ZipcodeEditTxt;
    private TextView CurrentLocationTxt,StateTxt;
    private String SelectedZipCodeCity;
    private ArrayList<String> StateName = new ArrayList<String>();
    private List<String> LongNameList = new ArrayList<String>();
    private List<String> ShortNameList = new ArrayList<String>();
    private String ZipCodeCity,selectedCity,longNameText,shortNameText,zipcode_longNameText;
    boolean isCityFound=false;
    private LocationCooridnates locationService;
    private IntentFilter intentFilter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_location);
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
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.exit_icon);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_update_location));


        StateTxt = (TextView) findViewById(R.id.StateTxt);
        setProgressBar(findViewById(R.id.progressDialog));
        StateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZipcodeEditTxt.setText("");
                showListViewDialog(LongNameList, (TextView) v);
            }
        });

        locationService = new LocationCooridnates(getApplicationContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());

        ZipcodeEditTxt = (EditText) findViewById(R.id.ZipEditTxt);
        ZipcodeEditTxt.setTag(null);
        ZipcodeEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MdliveUtils.validateZipcodeFormat(ZipcodeEditTxt);
            }
        });
        //TextView SavContinueBtn = (TextView) findViewById(R.id.txtApply);
        /*SavContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveLocation.this);
                if(ZipcodeEditTxt.getText().toString().length()!=IntegerConstants.NUMBER_ZERO||StateTxt.getText().toString().length()!=IntegerConstants.NUMBER_ZERO){
                    if(ZipcodeEditTxt.getText().length()!=IntegerConstants.NUMBER_ZERO){
                        String getEditTextValue = ZipcodeEditTxt.getText().toString();
                        if(MdliveUtils.validateZipCode(getEditTextValue)){
                            loadZipCode(getEditTextValue);
                        }else{
                            MdliveUtils.alert(pDialog, MDLiveLocation.this, getString(R.string.valid_zip));
                        }
                    }else{
                        SaveZipCodeCity(selectedCity);
                        finish();
                    }

                }else{
                    MdliveUtils.alert(pDialog, MDLiveLocation.this, getString(R.string.valid_zip_state));
                }

            }
        });
*/
        CurrentLocationTxt = (Button) findViewById(R.id.currentLocation);
        CurrentLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                getLocationCoordinates();
            }
        });

        /**
         *
         * This is to Parse the location that is to get long name and short name
         * of the state from the localisation
         */

        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            String stateJson = sharedpreferences.getString(PreferenceConstants.USER_STATE_LIST, "[]");
            JSONArray stateJsonArray = new JSONArray(stateJson);
            Log.d("JSON Count", stateJsonArray.length() + "");
            if(stateJsonArray.length()>0){
                for(int i = 0; i<stateJsonArray.length();i++){
                    ShortNameList.add(stateJsonArray.getJSONObject(i).keys().next());
                    LongNameList.add(stateJsonArray.getJSONObject(i).getString(ShortNameList.get(i)));
                }
                Log.d("stateList->",LongNameList.toString());
                Log.d("stateList->",ShortNameList.toString());
            } else {
                LongNameList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
                ShortNameList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));
            }
        } catch (Exception e) {
            LongNameList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
            ShortNameList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveLocation.this);
        onBackPressed();
    }

    public void rightBtnOnClick(View view){
        MdliveUtils.hideSoftKeyboard(MDLiveLocation.this);
        if(ZipcodeEditTxt.getText().toString().length()!=IntegerConstants.NUMBER_ZERO||StateTxt.getText().toString().length()!=IntegerConstants.NUMBER_ZERO){
            if(ZipcodeEditTxt.getText().length()!=IntegerConstants.NUMBER_ZERO){
                String getEditTextValue = ZipcodeEditTxt.getText().toString();
                if(MdliveUtils.validateZipCode(getEditTextValue)){
                    loadZipCode(getEditTextValue);
                }else{
                    MdliveUtils.alert(getProgressDialog(), MDLiveLocation.this, getString(R.string.mdl_valid_zip));
                }
            }else{
                SaveZipCodeCity(selectedCity);
                finish();
            }

        }else{
            MdliveUtils.alert(getProgressDialog(), MDLiveLocation.this, getString(R.string.mdl_valid_zip_state));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            registerReceiver(locationReceiver, intentFilter);
            locationService.setBroadCastData(StringConstants.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(locationReceiver);
            locationService.setBroadCastData(StringConstants.DEFAULT);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Current location.
     * Class : CurrentLocationServices - Service class used to fetch the current latitude and longitude
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadCurrentLocation(String latitude, String longitude) {
        showProgress();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                CurrentLocationResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response",error.toString());
                hideProgress();
//                MdliveUtils.handelVolleyErrorResponse(MDLiveLocation.this,error,getProgressDialog());
                MdliveUtils.showDialog(MDLiveLocation.this, "Unable to find location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                });

            }
        };

        CurrentLocationServices currentlocationservices = new CurrentLocationServices(MDLiveLocation.this, getProgressDialog());
        currentlocationservices.getCurrentLocation(latitude, longitude, responseListener, errorListener);
    }

    /**
     * Load ZipCode.
     * Class : zipcodeservices - Service class used to fetch the Zip Code information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */

    private void loadZipCode(String EditTextValue) {
        showProgress();
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
                hideProgress();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveLocation.this);
                    }
                }
            }
        };

        ZipCodeServices zipcodeservices = new ZipCodeServices(MDLiveLocation.this, getProgressDialog());
        zipcodeservices.getZipCodeServices(EditTextValue, responseListener, errorListener);
    }

    /**
     * Successful Response Handler for Zip Code.
     */
    private void ZipCodeResponse(JSONObject response) {
        try {
            hideProgress();
            //Fetch Data From the Services
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
                                for(int l=0;l< Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size();l++) {
                                    if (SelectedZipCodeCity.equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(l))) {
                                        longNameText = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(l);
                                        shortNameText = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(l);
                                        SaveZipCodeCity(longNameText);
                                        isCityFound=true;
                                        break;
                                    }
                                }
                                if(!isCityFound){
                                    MdliveUtils.alert(getProgressDialog(), MDLiveLocation.this, getString(R.string.mdl_find_location_zipcode));
                                }

                            }
                        }

                    }
                }
            }else{

          MdliveUtils.alert(getProgressDialog(), MDLiveLocation.this, "Unable to find location by Zipcode.");
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
            hideProgress();
            //Fetch Data From the Serviceservices
            Log.i("UseCurrentLocation",response.toString());
            selectedCity = response.getString("state");
            for(int l=0;l< Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size();l++) {
                if (selectedCity.equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(l))) {
                    longNameText = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(l);
                    shortNameText = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(l);
                    SaveZipCodeCity(longNameText);
                    break;
                }
            };
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * getLocationCoordinates method is to get the Current latitude and longitude of the location
     */
    public void getLocationCoordinates() {
        if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(getApplicationContext());
        } else {
            hideProgress();
            MdliveUtils.showGPSSettingsAlert(MDLiveLocation.this,(RelativeLayout)findViewById(R.id.progressDialog));
        }
    }



    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgress();
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                double lat = intent.getDoubleExtra("Latitude", 0d);
                double lon = intent.getDoubleExtra("Longitude", 0d);
                loadCurrentLocation(lat + "", lon + "");
            }else{
                MdliveUtils.showDialog(MDLiveLocation.this, "Unable to find location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
    };


    public void SavecurrentLocation(String ZipCodeCity) {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        String activityCaller = getIntent().getStringExtra("activitycaller");
        Log.e("Caller bname", activityCaller);
        if (activityCaller.equals("getstarted")) {
            editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, ZipCodeCity);
            editor.commit();
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
            Log.e("print short name",shortNameText);
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
        Intent intent = new Intent();
        intent.putExtra("shortNameText", shortNameText);
        intent.putExtra("longNameText", longNameText);
        setResult(Activity.RESULT_OK, intent);
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

    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveLocation.this);
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }

}
