package com.mdlive.sav;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ProviderTypeList;
import com.mdlive.unifiedmiddleware.services.provider.FilterSearchServices;
import com.mdlive.unifiedmiddleware.services.provider.SearchProviderDetailServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Calendar.MONTH;

/**
 * class : MDLiveSearchProvider - This class is used to Filter the provider list.
 * We can filter the provider either by selecting the provider name or by selecting
 * the provider type, speciality, location, Speaks and the gender.
 */
public class MDLiveSearchProvider extends MDLiveBaseActivity {
    private TextView AppointmentTxtView, LocationTxtView, genderTxtView;
    private EditText edtSearch;
    private int month, day, year;
    private static final int DATE_PICKER_ID = IdConstants.SEARCHPROVIDER_DATEPICKER;
    private ArrayList<HashMap<String, String>> SearchArrayListProvider = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListSpeciality = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListSpeaks = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> searchArrayListProviderId = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListAvailableBy = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> SearchArrayList = new ArrayList<HashMap<String, String>>();
    private ArrayList<String> providerTypeArrayList = new ArrayList<>();
    private ArrayList<String> providerIdArrayList = new ArrayList<>();
    private ArrayList<String> AvailableByArrayList = new ArrayList<String>();
    private ArrayList<String> ProviderTypeArrayList = new ArrayList<String>();
    private Map<String, Map<String, String>> tempmap = new HashMap<>();
    private ArrayList<String> SortByArrayList = new ArrayList<String>();
    private ArrayList<String> SpecialityArrayList = new ArrayList<String>();
    private ArrayList<String> SpeaksArrayList = new ArrayList<String>();
    private ArrayList<String> GenderArrayList = new ArrayList<String>();
    private HashMap<String, String> postParams = new HashMap<>();
    public String filter_SavedLocation, SavedLocation,postProviderId,serverDateFormat;
    private boolean isCignaCoachUser = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_provider);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_refine_search));

        // Determine the Provider mode
        final SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String providerMode = sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "");
        // set CignaCoach-specific flag
        if(providerMode != null && providerMode.length() > 0
                && providerMode.equalsIgnoreCase(MDLiveConfig.PROVIDERTYPE_CIGNACOACH)
                && MDLiveConfig.CIGNACOACH_ENABLED)
        {
            isCignaCoachUser = true;
            // overwrite text strings
            ((TextView)findViewById(R.id.txtProviderType)).setText(getString(R.string.mdl_coach_type_hc));

            // hide Views not used for Cigna Health Coach
            findViewById(R.id.dividerLocatedIn).setVisibility(View.GONE);
            findViewById(R.id.LocatioTxtViewR6).setVisibility(View.GONE);
            findViewById(R.id.dividerAppointmentDate).setVisibility(View.GONE);
            findViewById(R.id.AppointmentDateR2).setVisibility(View.GONE);
            findViewById(R.id.dividerAvailableBy).setVisibility(View.GONE);
            findViewById(R.id.AvailableByR1).setVisibility(View.GONE);
            findViewById(R.id.sorttByR4).setVisibility(View.GONE);
            findViewById(R.id.dividerSorttBy).setVisibility(View.GONE);

            // re-wire layouts relative dependencies after Views removals
            View speaks = findViewById(R.id.SpeaksTxtViewR7);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)speaks.getLayoutParams();
            View v = findViewById(R.id.dividerSpecialty);
            params.addRule(RelativeLayout.BELOW, v.getId());
            speaks.setLayoutParams(params);

            View providerType = findViewById(R.id.ProviderTypeTxtViewR3);
            params = (RelativeLayout.LayoutParams)providerType.getLayoutParams();
            v = findViewById(R.id.edt_searchProvider);
            //params.removeRule(RelativeLayout.BELOW);  //  available only from API level 17+
            params.addRule(RelativeLayout.BELOW, v.getId());
            providerType.setLayoutParams(params);

            View specialtyType = findViewById(R.id.SpecialityTxtViewR5);
            params = (RelativeLayout.LayoutParams)specialtyType.getLayoutParams();
            v = findViewById(R.id.dividerProviderType);
            //params.removeRule(RelativeLayout.BELOW);  //  available only from API level 17+
            params.addRule(RelativeLayout.BELOW, v.getId());
            specialtyType.setLayoutParams(params);
        }

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

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        findViewById(R.id.txtApply).setContentDescription(getString(R.string.mdl_ada_tick_button));
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_refine_search));

        initialiseData();

        ((TextView) findViewById(R.id.ProviderTypeTxtView)).setText(sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "Any"));

        //Load Services
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
        SavedLocation = searchPref.getString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, null);
        filter_SavedLocation = searchPref.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        LocationTxtView.setText(SavedLocation);
        loadSearchproviderDetails();
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveSearchProvider.this);
        onBackPressed();
    }

    /**
     * The initialization of the views was done here.All the labels was defined here and
     * the click event for the back button and the home button was done here.
     * On clicking the back button image will be finishing the current Activity
     * and on clicking the Home button you will be navigated to the SSo Screen with
     * an alert.
     * <p/>
     * *
     */
    private void initialiseData() {
        AppointmentTxtView = (TextView) findViewById(R.id.DateTxtView);
        GetCurrentDate((TextView) findViewById(R.id.DateTxtView));
        LocationTxtView = (TextView) findViewById(R.id.LocatioTxtView);
        genderTxtView = (TextView) findViewById(R.id.GenderTxtView);
        edtSearch = (EditText) findViewById(R.id.edt_searchProvider);

        if(isCignaCoachUser){
            edtSearch.setHint(R.string.mdl_search_hint_hc);
        }

        setProgressBar(findViewById(R.id.progressDialog));
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("video"))
        {
            findViewById(R.id.AvailableByR1).setVisibility(View.GONE);


        }else if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("phone"))
        {
            findViewById(R.id.AvailableByR1).setVisibility(View.GONE);
        }else
        {
            if(!isCignaCoachUser)
                findViewById(R.id.AvailableByR1).setVisibility(View.VISIBLE);
        }

        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */
//        ((ImageView) findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MdliveUtils.hideSoftKeyboard(MDLiveSearchProvider.this);
//                onBackPressed();
//
//            }
//        });
    }

    public void availbleAction(View v) {
        showListViewDialog(AvailableByArrayList, (TextView) findViewById(R.id.AvailableTxtView), "available_by", SearchArrayListAvailableBy);
    }

    /**
     * This method is to fetch the apoointment date and the native date picker is called for selecting
     * the required date.
     */
    public void appointmentAction(View v) {
        //GetCurrentDate((TextView) findViewById(R.id.DateTxtView));
        // On button click show datepicker dialog
        showDialog(DATE_PICKER_ID);

    }

    /**
     * This method is to fetch the Provider Type details.Here the Provider type can be either
     * Family Physician or Pediatrician.These things will be populated in the arraylist.
     */
    public void providerTypeAction(View v) {
        ProviderTypeArrayList = MDLiveGetStarted.providerTypeArrayList;
        ArrayList<HashMap<String,String>> providerIdArray = new ArrayList<HashMap<String,String>>();

        for(int i = 0; i<ProviderTypeArrayList.size();i++){
            HashMap<String,String> providerID = new HashMap<String,String>();
            providerID.put(MDLiveGetStarted.providerTypeIdList.get(i),ProviderTypeArrayList.get(i));
            providerIdArray.add(providerID);
        }
        showListViewDialog(ProviderTypeArrayList, (TextView) findViewById(R.id.ProviderTypeTxtView), "provider_type", providerIdArray);
    }

    /**
     * This method is to fetch the Sort order details.
     * The sorted list will be populated in the arraylist.
     */
    public void sortByAction(View v) {
        showListViewDialog(SortByArrayList, (TextView) findViewById(R.id.SortbyTxtView), "sort_by", SearchArrayList);
    }

    /**
     * This method is to fetch the Speciality type and this specialisation will be
     * completely based on the Provider type.If the provider type is pediatrician then the corresponding
     * specialisation for the particular Provider type will be populated.
     */
    public void specialityAction(View v) {
        showListViewDialog(SpecialityArrayList, (TextView) findViewById(R.id.SpecialityTxtView), "speciality", SearchArrayListSpeciality);
    }

    /**
     * This method is to fetch the Location.Here the location can be fetched by
     * using either the current location or by using the manual search like either through
     * the Zip code or by selecting the state or the city.
     */
    public void locationAction(View v) {
        Intent intent = new Intent(MDLiveSearchProvider.this, MDLiveLocation.class);
        intent.putExtra("activitycaller", getString(R.string.mdl_searchprovider));
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveSearchProvider.this);
    }

    /**
     * This method is to fetch the what the provider speaks.
     * THe provider speaking languages will be fetched and populated in the arraylist
     */
    public void speaksAction(View v) {
        showListViewDialog(SpeaksArrayList, (TextView) findViewById(R.id.SpeaksTxtView), "speaks", SearchArrayListSpeaks);
    }

    /**
     * This method is to fetch what the provider gender is.
     * THe provider can be either Male or Female.
     */
    public void genderAction(View v) {
        showListViewDialog(GenderArrayList, (TextView) findViewById(R.id.GenderTxtView), "gender", SearchArrayList);
    }

    /**
     * The Search button taps the user to the Provider screen and it filters
     * tHe provider's category based on the corresponding selection of the filters.
     * The post parameters should be passed and the provider name is mandatory.
     * Along with that we can also filter the provider by using either thelocation
     * or the appointment date or the gender or the provider name.
     */
    public void rightBtnOnClick(View v) {
//        postParams.put("located_in", filter_SavedLocation);
//        postParams.put("available_by", StringConstants.AVAILABLE_BY);
//        postParams.put("appointment_date", AppointmentTxtView.getText().toString());
//        postParams.put("gender", genderTxtView.getText().toString());
//        if (edtSearch.getText().toString().length() != IntegerConstants.NUMBER_ZERO) {
//            postParams.put("provider_name", edtSearch.getText().toString());
//        }
//        if (postParams.get("provider_type") == null) {
//            postParams.put("provider_type", StringConstants.APPOINTMENT_TYPE);
//        }

        //MDLive Embed Kit Implementtaions


        if(filter_SavedLocation != null && !filter_SavedLocation.equalsIgnoreCase("Any")){
            postParams.put("located_in", filter_SavedLocation);
        }

        if(((TextView)findViewById(R.id.SpeaksTxtView)).getText() != null && ((TextView)findViewById(R.id.SpeaksTxtView)).getText().toString().length() != 0 &&
                !((TextView)findViewById(R.id.SpeaksTxtView)).getText().toString().equalsIgnoreCase("Any")){
//            postParams.put("speaks", ((TextView)findViewById(R.id.SpeaksTxtView)).getText().toString());
            postParams.put("speaks", postParams.get("speaks"));
        }

        if(serverDateFormat != null && !serverDateFormat.equalsIgnoreCase(("Any"))){
            postParams.put("appointment_date", serverDateFormat);//date needs to be sent in (yyyy/MM/dd)this format to server.
        }

        if(genderTxtView.getText() != null && genderTxtView.getText().toString().length() != 0 &&
                !genderTxtView.getText().toString().equalsIgnoreCase("Any")){
            postParams.put("gender", genderTxtView.getText().toString());
        }
        if(((TextView)findViewById(R.id.SortbyTxtView)).getText() != null && ((TextView)findViewById(R.id.SortbyTxtView)).getText().toString().length() != 0 &&
                !((TextView)findViewById(R.id.SortbyTxtView)).getText().toString().equalsIgnoreCase("Any")){
            postParams.put("sort_by", ((TextView)findViewById(R.id.SortbyTxtView)).getText().toString());
        }

        if(((TextView)findViewById(R.id.SpecialityTxtView)).getText() != null && ((TextView)findViewById(R.id.SpecialityTxtView)).getText().toString().length() != 0 &&
                !((TextView)findViewById(R.id.SpecialityTxtView)).getText().toString().equalsIgnoreCase("Any")){
            postParams.put("speciality",((TextView)findViewById(R.id.SpecialityTxtView)).getText().toString());
        }

        if(edtSearch.getText() != null && edtSearch.getText().toString().length() != 0 &&
                !edtSearch.getText().toString().equalsIgnoreCase("Any")){
            postParams.put("provider_name",edtSearch.getText().toString());
        }

        if (postParams.get("provider_type") != null) {
            postParams.put("provider_type", postParams.get("provider_type"));


        }else
        {
            String providerTypeString = ((TextView) findViewById(R.id.ProviderTypeTxtView)).getText().toString();
            int pos = MDLiveGetStarted.providerTypeArrayList.indexOf(providerTypeString);
            String providerTypeId = MDLiveGetStarted.providerTypeIdList.get(pos>=0?pos : 1);
            postParams.put("provider_type", providerTypeId);
        }

        // PHS USERS Available by
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("video"))
        {
            postParams.put("available_by", "1");

        }else if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("phone"))
        {
            postParams.put("available_by", "2");
        }else
        {
            postParams.put("available_by", postParams.get("available_by"));
        }


     //   Log.e("Post Params", new Gson().toJson(postParams));
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.PROVIDERTYPE_ID, postParams.get("provider_type"));
        if(((TextView)findViewById(R.id.ProviderTypeTxtView)).getText() != null) {
            editor.putString(PreferenceConstants.PROVIDER_MODE, ((TextView) findViewById(R.id.ProviderTypeTxtView)).getText().toString());
        }
        editor.commit();
       LoadFilterSearchServices();
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
        SavedLocation = searchPref.getString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, getString(R.string.mdl_florida));
        filter_SavedLocation = searchPref.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl));
        Log.e("filter_SavedLocation", filter_SavedLocation);
        LocationTxtView.setText(SavedLocation);
    }

    /**
     * Load Search provider Details.
     * Class : SearchproviderDetails - Service class used to fetch the search details
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadSearchproviderDetails() {
//        pDialog.show();
        //progressDialog.setVisibility(View.VISIBLE);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                pDialog.dismiss();
                //progressDialog.setVisibility(View.GONE);
                hideProgress();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveSearchProvider.this);
                    }
                }
            }
        };
        SearchProviderDetailServices services = new SearchProviderDetailServices(MDLiveSearchProvider.this, null);
        services.getSearchDetails(successCallBackListener, errorListener);
    }

    /**
     *
     * Load loadProviderType Details.
     * Class : ProviderTypeList - Service class used to fetch the Provider Detail information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadProviderType() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                handleproviderTypeSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveSearchProvider.this, error, getProgressDialog());
            }};
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        String dependent_id=  settings.getString("dependent_id","");
        ProviderTypeList services = new ProviderTypeList(MDLiveSearchProvider.this, null);
        services.getProviderType(dependent_id, successCallBackListener, errorListener);
    }

    /**
     *
     *  Successful Response Handler for Provider Type Info.The Provider type info will provider the gender
     *  of the user and the date of birth of the corresponding user.The dependent id will be
     *  passed for the the each provider while switching over the dependent so that the
     *  corresponding provider type will be changed to the selected dependents.
     *
     */
    private void handleproviderTypeSuccessResponse(JSONObject response) {
        try {
            JSONObject providertype = response.getJSONObject("provider_types");
            providerTypeArrayList.clear();
            searchArrayListProviderId.clear();
            Iterator<String> iter = providertype.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = providertype.get(key);
                    providerTypeArrayList.add(value.toString());
                    providerIdArrayList.add(key);
                } catch (JSONException e) {
                    // Something went wrong!
                }
                ((TextView)findViewById(R.id.ProviderTypeTxtView)).setText(providerTypeArrayList.get(0));
            }

        } catch (Exception e) {

        }
    }

    /**
     * Successful Response Handler for Search Provider's details.
     */
    private void handleSuccessResponse(JSONObject response) {
        try {

            hideProgress();
            //Fetch Data From the Services
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            //Available by response
            getAvailableData(response);
            //Gender response
            getGenderData(response);
            //SortBy response
            getSortData(response);
            //Provider type response
            getSpecialityData(response);
            //Speakstype response
            getSpeaksData(response);

            //provider type response
            getproviderType(response);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will return the Specialities name based on the provider Type and corresponding speciality
     * for the provider will be returned.
     */
    private void getSpecialityData(JSONObject response) throws JSONException {
        JSONArray provider_type_array = response.getJSONArray("provider_type");


        for (int i = 0; i < provider_type_array.length(); i++) {
            JSONObject licenseObject = provider_type_array.getJSONObject(i);
            String str_provider_type = licenseObject.getString("provider_type");
            String str_provider_type_id = licenseObject.getString("id");
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(str_provider_type_id, str_provider_type);
            searchArrayListProviderId.add(map);

//            ProviderTypeArrayList.add(str_provider_type);
            LinkedHashMap<String, String> specialitymap = null;
            JSONArray speciality_array = licenseObject.getJSONArray("speciality");
            SpecialityArrayList.clear();
            specialitymap = new LinkedHashMap<String, String>();
            for (int j = 0; j < speciality_array.length(); j++) {
                JSONObject specialityObj = speciality_array.getJSONObject(j);
                specialitymap.put(specialityObj.getString("name"), specialityObj.getString("id"));
                SearchArrayListSpeciality.add(specialitymap);
                SpecialityArrayList.add(specialityObj.getString("name"));
            }
            tempmap.put(str_provider_type, specialitymap);
        }


    }

    /**
     * This method will return the Sort types based on the  Availibility
     * of the Provider.
     */
    private void getSortData(JSONObject response) throws JSONException {
        JSONArray Sort_array = response.getJSONArray("sort_by");
        ArrayList<String> keysList = new ArrayList<String>();
        for (int i = 0; i < Sort_array.length(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject itemObj = Sort_array.getJSONObject(i);

            Iterator<String> iter = itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, (String) itemObj.get(key));
            }
            SearchArrayList.add(map);

            SortByArrayList.add(Sort_array.getJSONObject(i).getString(Sort_array.getJSONObject(i).keys().next()));
        }
    }

    /**
     * This method will return the Sort types based on the  Availibility
     * of the Provider.
     */
    private void getSpeaksData(JSONObject response) throws JSONException {
        JSONArray Speaks_array = response.getJSONArray("speaks");
        ArrayList<String> keysList = new ArrayList<String>();
        for (int i = 0; i < Speaks_array.length(); i++) {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            JSONObject itemObj = Speaks_array.getJSONObject(i);

            Iterator<String> iter = itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, (String) itemObj.get(key));
                System.out.println(key);
            }

            SearchArrayListSpeaks.add(map);
            SpeaksArrayList.add(Speaks_array.getJSONObject(i).getString(Speaks_array.getJSONObject(i).keys().next()));
        }
    }

    /**
     * This method will return the Sort types based on the  Availibility
     * of the Provider.
     */
    private void getproviderType(JSONObject response) throws JSONException {
        Log.d("Response", response.toString());
        JSONArray provider_array = response.getJSONArray("provider_type");
        ArrayList<String> keysList = new ArrayList<String>();
        for (int i = 0; i < provider_array.length(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject itemObj = provider_array.getJSONObject(i);

//            Iterator<String> iter = itemObj.keys();//Logic to get the keys form Json Object
//            while (iter.hasNext()) {
//                String key = iter.next();
//                map.put(key, (String) itemObj.get(key));
//                System.out.println(key);
//            }

            searchArrayListProviderId.add(map);
            providerTypeArrayList.add(provider_array.getJSONObject(i).getString(provider_array.getJSONObject(i).keys().next()));
        }
    }

    /**
     * This method will return the Gender  of the Provider.
     */
    private void getGenderData(JSONObject response) throws JSONException {
        JSONArray Gender_array = response.getJSONArray("gender");

        for (int i = 0; i < Gender_array.length(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject itemObj = Gender_array.getJSONObject(i);

            Iterator<String> iter = itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, (String) itemObj.get(key));
                System.out.println(key);
            }
            SearchArrayList.add(map);
            GenderArrayList.add(Gender_array.getJSONObject(i).getString(Gender_array.getJSONObject(i).keys().next()));
        }
    }

    /**
     * This method will return the Availability Type whether the provider is
     * available through Video or phone.
     */
    private void getAvailableData(JSONObject response) throws JSONException {
        JSONArray Available_array = response.getJSONArray("available_by");
        for (int i = 0; i < Available_array.length(); i++) {

            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject itemObj = Available_array.getJSONObject(i);
            Iterator<String> iter = itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key, (String) itemObj.get(key));
                System.out.println(key);
            }

            SearchArrayListAvailableBy.add(map);
            AvailableByArrayList.add(Available_array.getJSONObject(i).getString(Available_array.getJSONObject(i).keys().next()));
        }
    }

    /**
     * Load Filter Search Details.
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the
     * service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error
     * message to user or Get started screen will shown to user).
     */
    private void LoadFilterSearchServices() {
//        pDialog.show();
        //progressDialog.setVisibility(View.VISIBLE);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Sucess Response", response.toString());
                handleFilterSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    Log.e("Response Body", errorObj.toString());
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == MDLiveConfig.HTTP_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("error") || errorObj.has("message")) {
                            final String errorMsg = errorObj.has("error") ? errorObj.getString("error") : (errorObj.has("message") ? errorObj.getString("message") : "");
                            if(errorMsg != null && errorMsg.length() != 0){
                                (MDLiveSearchProvider.this).runOnUiThread(new Runnable() {
                                    public void run() {
                                        MdliveUtils.showDialog(MDLiveSearchProvider.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, getString(R.string.mdl_ok_upper), null, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }, null);
                                    }
                                });
                            }
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveSearchProvider.this, error, getProgressDialog());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        FilterSearchServices services = new FilterSearchServices(MDLiveSearchProvider.this, null);
        Log.e("Filter",postParams.toString());
        services.getFilterSearch(postParams, successCallBackListener, errorListener);
    }

    /**
     * Successful Response Handler for getting Current Location
     */
    private void handleFilterSuccessResponse(JSONObject response) {
        try {
            hideProgress();
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());

            if(!responObj.isJsonNull()){
                JsonArray responArray = responObj.get("physicians").getAsJsonArray();
                if (responArray.size() != 0) {
                    if (responArray.get(0).isJsonObject()) {
                        Log.e("Filter Response",responObj.toString());
                        Intent intent = new Intent();
                        intent.putExtra("Response", response.toString());
                        intent.putExtra("postParams", new Gson().toJson(postParams));
                        setResult(1, intent);
                        finish();
                        MdliveUtils.closingActivityAnimation(MDLiveSearchProvider.this);
                    }else{
                        Log.e("Filter Response",responObj.toString());
                        MdliveUtils.showDialog(MDLiveSearchProvider.this, responArray.getAsString(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetCurrentDate(TextView selectedText) {
        final Calendar c = TimeZoneUtils.getCalendarWithOffset(this);
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Show current date
        String format = new SimpleDateFormat("MMM d, yyyy").format(c.getTime());
        selectedText.setText(format);
        serverDateFormat=new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());
//        selectedText.setText(new StringBuilder()
//                // Month is 0 based, just add 1
//                .append(month + 1).append("/").append(day).append("/")
//                .append(year).append(" "));
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);

                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis() + TimeZoneUtils.getOffsetTimezone(this).getRawOffset() + TimeZoneUtils.getOffsetTimezone(this).getDSTSavings());
                return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            Calendar cal = TimeZoneUtils.getCalendarWithOffset(MDLiveSearchProvider.this);
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            cal.set(Calendar.MONTH, selectedMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveSearchProvider.this));
            String format = sdf.format(cal.getTime());
            sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveSearchProvider.this));
            serverDateFormat= sdf.format(cal.getTime());
            // Show selected date
            AppointmentTxtView.setText(format);

        }
    };

    /**
     * Instantiating array adapter to populate the listView
     * The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     * @param list : Dependent users array list
     */
    private void showListViewDialog(final ArrayList<String> list, final TextView selectedText, final String key, final ArrayList<HashMap<String, String>> typeList) {

      /*We need to get the instance of the LayoutInflater*/
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveSearchProvider.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.mdlive_screen_popup, null);
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
                String SelectedText = list.get(position);
                HashMap<String, String> localMap = typeList.get(position);
                for (Map.Entry entry : localMap.entrySet()) {
                    if (SelectedText.equals(entry.getValue().toString())) {
                        postParams.put(key, entry.getKey().toString());
                        break; //breaking because its one to one map
                    }
                }
                specialityBasedOnProvider(SelectedText, key);

                String oldChoice = selectedText.getText().toString();
                selectedText.setText(SelectedText);
                dialog.dismiss();

                // if user selects a different Provider type, then reload this screen
                if(!oldChoice.equals(SelectedText)){
                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(PreferenceConstants.PROVIDER_MODE, SelectedText);

                    int providerType = MDLiveConfig.PROVIDERTYPE_MAP.get(SelectedText)==null? MDLiveConfig.UNMAPPED : MDLiveConfig.PROVIDERTYPE_MAP.get(SelectedText);
                    if(providerType==MDLiveConfig.UNMAPPED)
                        editor.putString(PreferenceConstants.PROVIDERTYPE_ID, "");
                    else
                        editor.putString(PreferenceConstants.PROVIDERTYPE_ID, String.valueOf(providerType));

                    editor.commit();

                    // now reload the screen
                    recreate();
                }
            }
        });
    }

    /**
     * Select the Speciality based on the Provider name and the speciality will be displayed
     * for the dependent users.     *
     * @param key : Dependent users Key
     */
    private void specialityBasedOnProvider(String selectedText, String key) {
        try {
            if ("provider_type".equalsIgnoreCase(key)) {
                SpecialityArrayList.clear();
                Map<String, String> speciality = tempmap.get(selectedText);

                for (Map.Entry<String, String> entry : speciality.entrySet()) {
                    SpecialityArrayList.add(entry.getKey());
                }

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method will close the activity with transition effect.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveSearchProvider.this);
    }

}