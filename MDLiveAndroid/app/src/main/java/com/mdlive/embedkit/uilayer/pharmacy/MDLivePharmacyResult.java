package com.mdlive.embedkit.uilayer.pharmacy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.payment.MDLiveConfirmappointment;
import com.mdlive.embedkit.uilayer.payment.MDLivePayment;
import com.mdlive.embedkit.uilayer.pharmacy.adapter.PharmacyListAdaper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;
import com.mdlive.unifiedmiddleware.services.pharmacy.ResultPharmacyService;
import com.mdlive.unifiedmiddleware.services.pharmacy.SetPharmacyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class is for Pharmacy Results
 *
 * There are two type of results will be displayed here. They are
 *      1. Google map view result
 *      2. ListView result
 *
 * On clicking on Marker Info window or on the list item on result will redirect
 * to MDLivePharmacyResult Page
 */
public class MDLivePharmacyResult extends MDLiveBaseActivity {

    private RelativeLayout rl_footer;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private HashMap<Marker, Integer> markerIdCollection = new HashMap<Marker, Integer>();
    private RelativeLayout progressBar;
    private SupportMapFragment mapView, expandmapView;
    private GoogleMap googleMap, expandgoogleMap;
    private ListView pharmList;
    private PharmacyListAdaper adaper;
    private HashMap<String, Object> keyParams;
    private boolean isPageLimitReached = false, isLoading = false, isFirstItemDisplaying = true;
    private String errorMesssage;
    private ProgressBar bottomLoder;
    private ScrollView mapscrollView;
    private RelativeLayout expandableMapViewContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This code this added here because we are not extending from MDLiveBaseActivity, due to support map


        setContentView(R.layout.mdlive_pharmacy_result);
        clearMinimizedTime();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(4 * toolbar.getResources().getDisplayMetrics().density);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.options_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_choose_phr_txt).toUpperCase());

        initializeViews();
        initializeListView();
        initializeMapView();

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

        getPharmacySearchResults(getPostBody(getIntent()));
    }


    public void rightBtnOnClick(View view){
        Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
        if(getIntent().hasExtra("FROM_MY_HEALTH")){
            i.putExtra("FROM_MY_HEALTH",getIntent().getBooleanExtra("FROM_MY_HEALTH",false));
        }
        if(getIntent().hasExtra("PHARMACY_SELECTED")){
            i.putExtra("PHARMACY_SELECTED",getIntent().getBooleanExtra("PHARMACY_SELECTED", false));
        }

        startActivity(i);
        MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!MdliveUtils.isNetworkAvailable(MDLivePharmacyResult.this)){
            if(progressBar!=null&&progressBar.getVisibility()== View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IntegerConstants.PHAMRACY_RESULT_CODE){
            if(data != null){
                getPharmacySearchResults(getPostBody(data));
            }
        }
    }

    /**
     * This funciton initialize views of activity
     */
    public void initializeViews() {
        rl_footer = (RelativeLayout) findViewById(R.id.rl_footer);
        expandableMapViewContainer = (RelativeLayout) findViewById(R.id.expandableMapViewContainer);
        keyParams = new HashMap<String, Object>();
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        bottomLoder = (ProgressBar)findViewById(R.id.bottomLoader);
        errorMesssage = getString(R.string.mdl_no_pharmacies_listed);
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
        onBackPressed();
    }


    public void listResultBtnOnClick(View v){
        expandableMapViewContainer.setVisibility(View.GONE);
        if(mapscrollView != null)
            mapscrollView.scrollTo(0, 0);
    }

    /*
     * This function is mainly focused on initializing view in layout.
     */
    public void initializeListView() {
        pharmList = (ListView) findViewById(R.id.pharmList);
        adaper = new PharmacyListAdaper(MDLivePharmacyResult.this, list);
        pharmList.setAdapter(adaper);
        pharmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setPharmacyAsADefault((int) list.get(position).get("pharmacy_id"));
            }
        });
    }

    /**
     * This function is for initialize mapview used for pharmacy result.
     */
    public void initializeMapView() {
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        expandmapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.expandmapView));
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        expandgoogleMap = mapView.getMap();
        if (googleMap != null) {
            if (googleMap != null) {
                googleMap.setInfoWindowAdapter(markerInfoAdapter);
            }
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                expandableMapViewContainer.setVisibility(View.VISIBLE);
            }
        });

        if (expandgoogleMap != null) {
            if (expandgoogleMap != null) {
                expandgoogleMap.setInfoWindowAdapter(markerInfoAdapter);
            }
        }
        /*expandgoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);*/
    }




    /**
     * This function is used to get pharmacy search results from webservice.
     *
     * ResultPharmacyService is used to get results from webservice.
     *
     * handleSuccessResponse is used to handle success response
     *
     * @param postBody - post body required for make a request to webserver.
     */

    public void getPharmacySearchResults(String postBody) {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(bottomLoder.getVisibility()==View.VISIBLE){
                    bottomLoder.setVisibility(View.GONE);
                }
                try {
                    Log.e("Pharmacy Response --> ", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleListSuccessResponse(response);
                resetLoadingViews();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    Log.e("Pharmacy Response ", errorObj.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(bottomLoder.getVisibility()==View.VISIBLE){
                    bottomLoder.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                resetLoadingViews();
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, getProgressDialog());
            }
        };
        if(bottomLoder.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        ResultPharmacyService services = new ResultPharmacyService(MDLivePharmacyResult.this, null);
        services.doPharmacyLocationRequest(postBody, responseListener, errorListener);
    }


    /*
    * This function is for reset views in scroll up update of list.
    * */
    private void resetLoadingViews() {
        isLoading = false;
        pharmList.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * This adapter is used to display info window when users click on the marker on google map
     *
     * It has a layout to show user when click on marker.
     */

    GoogleMap.InfoWindowAdapter markerInfoAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker arg0) {
            View v = getLayoutInflater().inflate(R.layout.mdlive_pharm_custom_mapinfowindow_view, null);
            HashMap<String, Object> info = list.get(markerIdCollection.get(arg0));
            TextView addressline1 = (TextView) v.findViewById(R.id.addressText1);
            TextView addressline2 = (TextView) v.findViewById(R.id.addressText2);
            TextView addressline3 = (TextView) v.findViewById(R.id.addressText3);
            addressline1.setText(info.get("store_name")+"");
            addressline2.setText(info.get("address1")+"");
            addressline3.setText(info.get("city")+"  "+(TextUtils.isEmpty(info.get("zipcode")+"") ? "" : MdliveUtils.zipCodeFormat(info.get("zipcode").toString())));
            return v;
        }
        @Override
        public View getInfoContents(Marker arg0) {
            return null;
        }
    };

    /**
     * This function is for get Post Body details from received Intent from previous activity
     */
    private String getPostBody(Intent receivedIntent) {
        if (receivedIntent.hasExtra("latitude"))
            keyParams.put("latitude", receivedIntent.getDoubleExtra("latitude", 0));
        if (receivedIntent.hasExtra("longitude"))
            keyParams.put("longitude", receivedIntent.getDoubleExtra("longitude", 0));
        if (receivedIntent.hasExtra("name"))
            keyParams.put("name", receivedIntent.getStringExtra("name"));
        if(receivedIntent.hasExtra("errorMesssage"))
            errorMesssage = receivedIntent.getStringExtra("errorMesssage");
        if (receivedIntent.hasExtra("zipcode")) {
            keyParams.put("zipcode", receivedIntent.getStringExtra("zipcode"));
        } else {
            if (receivedIntent.hasExtra("city"))
                keyParams.put("city", receivedIntent.getStringExtra("city"));
            if (receivedIntent.hasExtra("state"))
                keyParams.put("state", receivedIntent.getStringExtra("state"));
        }
        keyParams.put("per_page", 10);
        keyParams.put("page", 1);
        Gson gson = new Gson();
        String postBody = gson.toJson(keyParams);
        return postBody;
    }


    /**
     * This function is used to handle response which was thrown from getPharmacySearchResults function
     *
     * Parsing json content and updating UI works done here.
     *
     * @param response - response is catched response from getPharmacySearchResults network response
     */

    private void handleListSuccessResponse(JSONObject response) {
        JsonObject responObj = null;
        try {
            progressBar.setVisibility(View.GONE);
            JsonParser parser = new JsonParser();
            responObj = (JsonObject) parser.parse(response.toString());
            int total_pages = responObj.get("total_pages").getAsInt();
            if (total_pages == ((int) keyParams.get("page")))
                isPageLimitReached = true;
            JsonArray responArray = responObj.get("pharmacies").getAsJsonArray();
            int pharmacy_id=0;
            double longitude=0, latitude=0;
            boolean twenty_four_hours=false, active=false, is_preferred =false;
            String store_name="", phone="", address1="", address2="", zipcode="", fax="", city="", distance="", state="";
            // For google map
            googleMap = mapView.getMap();
            expandgoogleMap = expandmapView.getMap();
            LatLng markerPoint = null;
            for (int i = 0; i < responArray.size(); i++) {
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "state"))
                    state = responArray.get(i).getAsJsonObject().get("state").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "pharmacy_id"))
                    pharmacy_id = responArray.get(i).getAsJsonObject().get("pharmacy_id").getAsInt();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "store_name"))
                    store_name = responArray.get(i).getAsJsonObject().get("store_name").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "phone"))
                    phone = responArray.get(i).getAsJsonObject().get("phone").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "address1"))
                    address1 = responArray.get(i).getAsJsonObject().get("address1").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "active"))
                    active = responArray.get(i).getAsJsonObject().get("active").getAsBoolean();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "address2"))
                    address2 = responArray.get(i).getAsJsonObject().get("address2").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "zipcode"))
                    zipcode = MdliveUtils.zipCodeFormat(responArray.get(i).getAsJsonObject().get("zipcode").getAsString());
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "fax"))
                    fax = responArray.get(i).getAsJsonObject().get("fax").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "city"))
                    city = responArray.get(i).getAsJsonObject().get("city").getAsString();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "twenty_four_hours"))
                    twenty_four_hours = responArray.get(i).getAsJsonObject().get("twenty_four_hours").getAsBoolean();
                if(MdliveUtils.checkJSONResponseHasString(responArray.get(i).getAsJsonObject(), "distance"))
                    distance = responArray.get(i).getAsJsonObject().get("distance").getAsString();
                try {
                    if(responArray.get(i).getAsJsonObject().has("is_preferred")){
                        if(responArray.get(i).getAsJsonObject().get("is_preferred").getAsBoolean())
                            is_preferred = true;
                        else
                            is_preferred = false;
                    }else{
                        is_preferred = false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    if (responArray.get(i).getAsJsonObject().get("coordinates").isJsonNull()) {
                        longitude = 0;
                        latitude = 0;
                    } else {
                        longitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject().get("longitude").getAsDouble();
                        latitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    longitude = 0;
                    latitude = 0;
                }
                markerPoint = new LatLng(latitude, longitude);
                addResultsDatasInMap(pharmacy_id, longitude, latitude, twenty_four_hours, active, is_preferred, store_name, phone, address1, address2, zipcode, fax, city, distance, state, markerPoint, i);
            }
            adaper.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(pharmList);
            //For Google map initialize view
            if (markerPoint != null && googleMap != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
            if (markerPoint != null && expandgoogleMap != null)
                expandgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));

        } catch (Exception e) {
            e.printStackTrace();
        }
        handleJsonDatas(responObj);
    }

    /**
     * This function is used to check whether result reached end point or not results found from service
     * @param responObj - returned JSONObject from service
     */
    private void handleJsonDatas(JsonObject responObj) {
        try{
            if(responObj.get("total_pages").isJsonNull()){
                MdliveUtils.showDialog(MDLivePharmacyResult.this, errorMesssage,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                                if(getIntent().hasExtra("FROM_MY_HEALTH")){
                                    i.putExtra("FROM_MY_HEALTH",getIntent().getBooleanExtra("FROM_MY_HEALTH",false));
                                }
                                if(getIntent().hasExtra("PHARMACY_SELECTED")){
                                    i.putExtra("PHARMACY_SELECTED",getIntent().getBooleanExtra("PHARMACY_SELECTED", false));
                                }
                                startActivity(i);
                                finish();
                                MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
                            }
                        });
            }
            if(!responObj.get("total_pages").isJsonNull() && !responObj.get("total_pages").getAsString().equals("0")){
                mapscrollView= (ScrollView) findViewById(R.id.mapscrollView);
                mapscrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        View view = (View) mapscrollView.getChildAt(mapscrollView.getChildCount() - 1);
                        int diff = (view.getBottom() - (mapscrollView.getHeight() + mapscrollView.getScrollY()));
                        if(((int) keyParams.get("page")) == 1 && isFirstItemDisplaying){
                            mapscrollView.scrollTo(0, 0);
                            isFirstItemDisplaying = false;
                        }
                        // if diff is zero, then the bottom has been reached
                        if (diff == 0 &&
                                rl_footer.getVisibility() == View.VISIBLE &&
                                !isPageLimitReached) {
                            if (!isLoading) {
                                pharmList.setEnabled(false);
                                isLoading = true;
                                bottomLoder.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                keyParams.put("page", ((int) keyParams.get("page")) + 1);
                                keyParams.put("per_page", 10);
                                Gson gson = new Gson();
                                String postBody = gson.toJson(keyParams);
                                Log.e("Post Body", postBody);
                                getPharmacySearchResults(postBody);
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
             e.printStackTrace();
        }
    }

    /**
     * This function is used to add Results content to Hashmap
     * @param active - describes active pharmacy or not
     * @param address1 - address line 1 of pharmacy
     * @param address2 - address line 2 of pharmacy
     * @param city - city of pharmacy
     * @param distance - distance of pharmacy
     * @param fax - fax of pharmacy
     * @param i - count number of pharmacy
     * @param is_preferred - is pharmacy preferred
     * @param latitude - latitude of pharmacy
     * @param longitude - longitude of pharmacy
     * @param markerPoint - markerPoint of pharmacy
     * @param pharmacy_id - pharmacy_id of pharmacy
     * @param phone - phone of pharmacy
     * @param state - state of pharmacy
     * @param store_name - store_name of pharmacy
     * @param twenty_four_hours - twenty_four_hours of pharmacy
     * @param zipcode - zipcode of pharmacy
     */
    private void addResultsDatasInMap(int pharmacy_id, double longitude, double latitude, boolean twenty_four_hours, boolean active, boolean is_preferred, String store_name, String phone, String address1, String address2, String zipcode, String fax, String city, String distance, String state, LatLng markerPoint, int i) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("state", state);
        map.put("is_preferred", is_preferred);
        map.put("pharmacy_id", pharmacy_id);
        map.put("store_name", store_name);
        map.put("phone", phone);
        map.put("address1", address1);
        map.put("active", active);
        map.put("address2", address2);
        map.put("zipcode", zipcode);
        map.put("fax", fax);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("city", city);
        map.put("twenty_four_hours", twenty_four_hours);
        map.put("distance", distance);
        map.put("active", active);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(markerPoint).title(store_name));
        expandgoogleMap.addMarker(new MarkerOptions().position(markerPoint).title(store_name));
        markerIdCollection.put(marker, i);
        list.add(map);
    }

    /**
     * This function has a webservice call of SetPharmacyService
     * While user clicks on the usePharmacy button which will set pharmacy as a user's default.
     */
    public void setPharmacyAsADefault(int pharmacyId) {
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, getProgressDialog());
            }
        };
        HashMap<String, Integer> gsonMap = new HashMap<String, Integer>();
        gsonMap.put("pharmacy_id", pharmacyId);
        SetPharmacyService services = new SetPharmacyService(MDLivePharmacyResult.this, null);
        services.doPharmacyResultsRequest(new Gson().toJson(gsonMap), String.valueOf(pharmacyId), responseListener, errorListener);
    }

    /**
     * This function is handling response of SetPharmacyService which was thrown from
     *
     * function setPharmacyAsADefault()
     */
    private void handleSuccessResponse(JSONObject response) {
        try {
            progressBar.setVisibility(View.GONE);
            reloadSlidingMenu();
            if(getIntent().hasExtra("FROM_MY_HEALTH")){
                Intent i = new Intent(getBaseContext(),MedicalHistoryActivity.class);
                i.putExtra("FROM_SELECTION", true);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                MdliveUtils.closingActivityAnimation(this);
            }else if (response.getString("message").equals("Pharmacy details updated")) {
                checkInsuranceEligibility();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles checks user insurance eligibility and return the final amount for the user.
     * successListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * PharmacyService-Class will send the request to the server and get the responses
     *doPostCheckInsulranceEligibility-Method will carries required parameters for sending request to the server.
     */

    public void checkInsuranceEligibility(){
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                progressBar.setVisibility(View.GONE);
                try{
                    JSONObject jobj=new JSONObject(response.toString());
                    if(jobj.has("final_amount")){
                        if(Integer.parseInt(jobj.getString("final_amount"))>0){
                            Intent i = new Intent(getApplicationContext(), MDLivePayment.class);
                            i.putExtra("final_amount",jobj.getString("final_amount"));
                            i.putExtra("redirect_mypharmacy", true);
                            startActivity(i);
                            finish();
                            MdliveUtils.startActivityAnimation(MDLivePharmacyResult.this);
                        }else{
                            doConfirmAppointment();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, getProgressDialog());
            }
        };
        PharmacyService insuranceService=new PharmacyService(MDLivePharmacyResult.this,null);
        insuranceService.doPostCheckInsulranceEligibility(formPostInsuranceParams(),successListener,errorListener);
    }

    /**
     * This function is used to get post body content for Check Insurance Eligibility
     * Values hard coded are default criteria from get response of Insurance Eligibility of all users.
     */
    public String formPostInsuranceParams(){
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String,String> insuranceMap=new HashMap<>();
        insuranceMap.put("appointment_method","1");
        insuranceMap.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        insuranceMap.put("timeslot","Now");
        insuranceMap.put("provider_type_id","3");
        insuranceMap.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        return new Gson().toJson(insuranceMap);
    }

    /**
     * This method handles appointment confirmation for zero dollar user
     * responseListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * ConfirmAppointmentServices-Class will send the request to the server and get the responses
     *
     */
    private void doConfirmAppointment() {
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePharmacyResult.this, MDLiveConfirmappointment.class);
                        startActivity(i);
                        finish();
                        MdliveUtils.startActivityAnimation(MDLivePharmacyResult.this);
                    } else {
                        Toast.makeText(MDLivePharmacyResult.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, getProgressDialog());
            }
        };
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("appointment_method", "1");
        params.put("phys_availability_id", "");
        params.put("alternate_visit_option", "No Answer");
        params.put("do_you_have_primary_care_physician","No");
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON,"Not Sure"));
        params.put("customer_call_in_number", settings.getString(PreferenceConstants.PHONE_NUMBER, ""));
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION,"FL"));
        Log.e("ConfirmAPPT Params", params.toString());
        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePharmacyResult.this, null);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
    }

    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("FROM_MY_HEALTH") && getIntent().hasExtra("PHARMACY_SELECTED") &&
                !getIntent().getBooleanExtra("PHARMACY_SELECTED", false)){
                Intent i = new Intent(getBaseContext(),MedicalHistoryActivity.class);
                i.putExtra("FROM_PHARMACY",true);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                MdliveUtils.closingActivityAnimation(this);
        } else {
            super.onBackPressed();
            MdliveUtils.closingActivityAnimation(this);
        }
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    public  void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if(list.size() != 0){
            listView.setLayoutParams(params);
        }
        listView.requestLayout();
    }
}
