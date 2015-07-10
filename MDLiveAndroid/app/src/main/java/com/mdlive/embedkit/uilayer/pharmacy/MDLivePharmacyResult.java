package com.mdlive.embedkit.uilayer.pharmacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.payment.MDLivePayment;
import com.mdlive.embedkit.uilayer.pharmacy.adapter.PharmacyListAdaper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;
import com.mdlive.unifiedmiddleware.services.pharmacy.ResultPharmacyService;
import com.mdlive.unifiedmiddleware.services.pharmacy.SetPharmacyService;

import org.json.JSONObject;

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
public class MDLivePharmacyResult extends FragmentActivity {

    private RelativeLayout rl_footer;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private HashMap<Marker, Integer> markerIdCollection = new HashMap<Marker, Integer>();
    private ProgressDialog pDialog;
    private RelativeLayout progressBar;
    private Bundle bundleToSend = new Bundle();
    private SupportMapFragment mapView;
    private GoogleMap googleMap;
    private ListView pharmList;
    private PharmacyListAdaper adaper;
//    private ProgressBar loadingIndicator;
    private HashMap<String, Object> keyParams;
    private boolean isPageLimitReached = false, isLoading = false;
    boolean isMarkerPointAdded = false;
    private String errorMesssage ="No Pharmacies Found!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This code this added here because we are not extending from MDLiveBaseActivity, due to support map
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }

        setContentView(R.layout.mdlive_pharmacy_resultnew);
        initializeViews();
        initializeListView();
        initializeMapView();
        getPharmacySearchResults(getPostBody(getIntent()));
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
        if(resultCode == RESULT_OK && requestCode == 4000){
            if(data != null){
                getPharmacySearchResults(getPostBody(data));
            }
        }
    }

    /**
     * This funciton initialize views of activity
     */
    public void initializeViews() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        rl_footer = (RelativeLayout) findViewById(R.id.rl_footer);
        keyParams = new HashMap<String, Object>();
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);


        ((ImageView) findViewById(R.id.filterImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                startActivityForResult(i, 4000);
                MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
                finish();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });
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
        googleMap = mapView.getMap();
        if (googleMap != null) {
            if (googleMap != null) {
                googleMap.setInfoWindowAdapter(markerInfoAdapter);
            }
        }
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
//        pDialog = Utils.getProgressDialog("Please Wait...", MDLivePharmacyResult.this);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //handleSuccessResponse(response);
                handleListSuccessResponse(response);
                resetLoadingViews();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
//                infoView.setVisibility(View.VISIBLE);
                resetLoadingViews();
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, pDialog);
            }
        };
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
//        infoView.setVisibility(View.GONE);
        ResultPharmacyService services = new ResultPharmacyService(MDLivePharmacyResult.this, null);
        services.doPharmacyLocationRequest(postBody, responseListener, errorListener);
    }


    /*
    * This function is for reset views in scroll up update of list.
    * */
    private void resetLoadingViews() {
        isLoading = false;
        pharmList.setEnabled(true);
//        loadingIndicator.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
//        infoView.setVisibility(View.VISIBLE);
    }

    /**
     * This adapter is used to display info window when users click on the marker on google map
     * <p/>
     * It has a layout to show user when click on marker.
     */

    GoogleMap.InfoWindowAdapter markerInfoAdapter = new GoogleMap.InfoWindowAdapter() {
        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker arg0) {
            View v = getLayoutInflater().inflate(R.layout.mdlive_pharm_custom_mapinfowindow_view, null);
            TextView addressText = (TextView) v.findViewById(R.id.addressText);
            HashMap<String, Object> info = list.get(markerIdCollection.get(arg0));
            addressText.setText(info.get("store_name") + "\n" +
                    info.get("address1") + "\n" +
                    info.get("city") + ", "
                    + info.get("state") + " "
                    + info.get("zipcode"));
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
     * <p/>
     * Parsing json content and updating UI works done here.
     *
     * @param response - response is catched response from getPharmacySearchResults network response
     */

    private void handleListSuccessResponse(JSONObject response) {
        JsonObject responObj = null;
        try {
//            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
//            infoView.setVisibility(View.VISIBLE);
            Log.e("response", response.toString());
            JsonParser parser = new JsonParser();
            responObj = (JsonObject) parser.parse(response.toString());
            int total_pages = responObj.get("total_pages").getAsInt();
            if (total_pages == ((int) keyParams.get("page")))
                isPageLimitReached = true;
            JsonArray responArray = responObj.get("pharmacies").getAsJsonArray();
            int pharmacy_id=0;
            double longitude=0, latitude=0;
            boolean twenty_four_hours=false, active=false, is_preferred =false;
            String store_name="", phone="", address1="", address2="", zipcode="", fax="", city="",
                    distance="", state="";
            // For google map
            googleMap = mapView.getMap();
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
                    zipcode = responArray.get(i).getAsJsonObject().get("zipcode").getAsString();
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
                        longitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject()
                                .get("longitude").getAsDouble();
                        latitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject()
                                .get("latitude").getAsDouble();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    longitude = 0;
                    latitude = 0;
                }
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
                if (googleMap != null && !isMarkerPointAdded) {
                    if(store_name.contains("Walgreen")){
                        markerPoint = new LatLng(latitude, longitude);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                        markerPoint)
                                        .title(store_name)
                        );
                        markerIdCollection.put(marker, i);
                        isMarkerPointAdded = true;
                    }
                }
                list.add(map);
            }
            adaper.notifyDataSetChanged();
            //For Google map initialize view
            if (markerPoint != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(responObj.get("total_pages").isJsonNull()){
//            MdliveUtils.showDialog(MDLivePharmacyResult.this, "", errorMesssage);
            MdliveUtils.showDialog(MDLivePharmacyResult.this, errorMesssage,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                            startActivityForResult(i, 4000);
                            finish();
                            MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);
                            //MdliveUtils.startActivityAnimation(MDLivePharmacyResult.this);
                        }
                    });
        }
        if(!responObj.get("total_pages").isJsonNull() && !responObj.get("total_pages").getAsString().equals("0")){
            pharmList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if ((firstVisibleItem + visibleItemCount) == totalItemCount &&
                            rl_footer.getVisibility() == View.VISIBLE &&
                            !isPageLimitReached
                            ) {
                        if (!isLoading) {
                            pharmList.setEnabled(false);
                            isLoading = true;
//                            loadingIndicator.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
//                            infoView.setVisibility(View.GONE);
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
    }

    /**
     * This function has a webservice call of SetPharmacyService
     * While user clicks on the usePharmacy button which will set pharmacy as a user's default.
     */
    public void setPharmacyAsADefault(int pharmacyId) {
//        pDialog = Utils.getProgressDialog("Please Wait...", MDLivePharmacyResult.this);
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
//        infoView.setVisibility(View.GONE);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*pDialog.dismiss();*/
                progressBar.setVisibility(View.GONE);
//                infoView.setVisibility(View.VISIBLE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, pDialog);
            }
        };
        HashMap<String, Integer> gsonMap = new HashMap<String, Integer>();
        gsonMap.put("pharmacy_id", pharmacyId);
        SetPharmacyService services = new SetPharmacyService(MDLivePharmacyResult.this, null);
        services.doPharmacyResultsRequest(new Gson().toJson(gsonMap), String.valueOf(pharmacyId), responseListener, errorListener);
               // responseListener, errorListener);
    }

    /**
     * This function is handling response of SetPharmacyService which was thrown from
     * <p/>
     * function setPharmacyAsADefault()
     */
    private void handleSuccessResponse(JSONObject response) {
        try {
//            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
//            infoView.setVisibility(View.VISIBLE);
            Log.d("Response", response.toString());
            if (response.getString("message").equals("Pharmacy details updated")) {
                checkInsuranceEligibility();
                /*Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                MdliveUtils.hideSoftKeyboard(MDLivePharmacyResult.this);*/
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
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                Log.e("Zero Dollar Insurance", response.toString());
                try{
                    JSONObject jobj=new JSONObject(response.toString());
                    if(jobj.has("final_amount")){
                        if(Integer.parseInt(jobj.getString("final_amount"))>0){
                            Intent i = new Intent(getApplicationContext(), MDLivePayment.class);
                            i.putExtra("final_amount",jobj.getString("final_amount"));
                            startActivity(i);
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
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, pDialog);
            }
        };
        PharmacyService insuranceService=new PharmacyService(MDLivePharmacyResult.this,pDialog);
        insuranceService.doPostCheckInsulranceEligibility(formPostInsuranceParams(),successListener,errorListener);
    }

    public String formPostInsuranceParams(){
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String,String> insuranceMap=new HashMap<>();
        insuranceMap.put("appointment_method","1");
        insuranceMap.put("provider_id",settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        insuranceMap.put("timeslot","Now");
        insuranceMap.put("provider_type_id","3");
        insuranceMap.put("state_id",settings.getString(PreferenceConstants.LOCATION,"FL"));
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
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    pDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePharmacyResult.this, MDLiveWaitingRoom.class);
                        startActivity(i);
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
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyResult.this, error, pDialog);
                /*if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacyResult.this);
                    }
                }*/
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);

        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("appointment_method", "1");
        params.put("do_you_have_primary_care_physician","No");
        // params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON,"Not Sure"));
        params.put("customer_call_in_number", "9068906789");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION,"FL"));
        Log.e("ConfirmAPPT Params",params.toString());


      /*  params.put("appointment_method", "1");
        params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", "Not Sure");
        params.put("state_id", "FL");
        params.put("customer_call_in_number", "9068906789");
        params.put("chief_complaint_reasons", null);
        params.put("alternate_visit_option", "alternate_visit_option");
        params.put("do_you_have_primary_care_physician", "No");*/
        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePharmacyResult.this, pDialog);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
    }


    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        MdliveUtils.movetohome(MDLivePharmacyResult.this);
    }

    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}
