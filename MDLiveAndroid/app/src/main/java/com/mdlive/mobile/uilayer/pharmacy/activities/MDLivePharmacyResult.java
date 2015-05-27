package com.mdlive.mobile.uilayer.pharmacy.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.pharmacy.adapter.PharmacyListAdaper;
import com.mdlive.mobile.uilayer.pharmacy.customui.CustomFlt;
import com.mdlive.mobile.uilayer.pharmacy.services.ResultPharmacyService;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by srinivasan_ka on 5/20/2015.
 */
public class MDLivePharmacyResult extends FragmentActivity {

    private RelativeLayout rl_footer;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private HashMap<Marker, Integer> markerIdCollection = new HashMap<Marker, Integer>();
    private ProgressDialog pDialog;
    private Bundle bundleToSend = new Bundle();
    private SupportMapFragment mapView;
    private GoogleMap googleMap;
    private ListView pharmList;
    private PharmacyListAdaper adaper;
    private GestureDetector mDetector;
    private CustomFlt mapContainer;
    private ProgressBar loadingIndicator;
    private HashMap<String, Object> keyParams;
    private boolean isPageLimitReached = false, isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_resultnew);

        //This function is for initialize views in layout
        initializeViews();

        //This function is for initialize listview for pharmacy list
        initializeListView();

        //This function is for initialize google map that was used in layout
        initializeMapView();

        keyParams = new HashMap<String, Object>();

        // This function is for get pharmacy search results
        getPharmacySearchResults(getPostBody(getIntent()));
    }

    public void initializeViews(){

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        rl_footer = (RelativeLayout) findViewById(R.id.rl_footer);

        ((Button) findViewById(R.id.footerButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.footerButton)).setVisibility(View.GONE);
                rl_footer.setVisibility(View.VISIBLE);
            }
        });
        mapContainer = ((CustomFlt) findViewById(R.id.mapContainer));
        mDetector = mapContainer.getGestureListener();
        mDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(rl_footer.getVisibility() == View.VISIBLE){
                    rl_footer.setVisibility(View.GONE);
                    ((Button) findViewById(R.id.footerButton)).setVisibility(View.VISIBLE);
                }else{
                    rl_footer.setVisibility(View.VISIBLE);
                    ((Button) findViewById(R.id.footerButton)).setVisibility(View.GONE);
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });

        loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
    }


   /*
    * This function is mainly focused on initializing listview for pharmacy list in layout.
    */

    public void initializeListView(){

        pharmList = (ListView) findViewById(R.id.pharmList);

        adaper = new PharmacyListAdaper(MDLivePharmacyResult.this, list);

        pharmList.setAdapter(adaper);

        pharmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MDLivePharmacyResult.this.getApplicationContext(), MDLivePharmacyDetails.class);

                bundleToSend.putInt("pharmacy_id",(int) list.get(position).get("pharmacy_id"));
                bundleToSend.putDouble("longitude",(Double) list.get(position).get("longitude"));
                bundleToSend.putDouble("latitude",(Double) list.get(position).get("latitude"));
                bundleToSend.putBoolean("twenty_four_hours", (Boolean) list.get(position).get("twenty_four_hours"));
                bundleToSend.putBoolean("active", (Boolean) list.get(position).get("active"));
                bundleToSend.putString("store_name",(String) list.get(position).get("store_name"));
                bundleToSend.putString("phone",(String) list.get(position).get("phone"));
                bundleToSend.putString("address1",(String) list.get(position).get("address1"));
                bundleToSend.putString("address2",(String) list.get(position).get("address2"));
                bundleToSend.putString("zipcode",(String) list.get(position).get("zipcode"));
                bundleToSend.putString("fax",(String) list.get(position).get("fax"));
                bundleToSend.putString("city",(String) list.get(position).get("city"));
                bundleToSend.putString("distance",(String) list.get(position).get("distance"));
                bundleToSend.putString("state",(String) list.get(position).get("state"));

                i.putExtra("datas", bundleToSend);

                startActivity(i);
            }
        });

        //getPharmacySearchResults

        pharmList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if((firstVisibleItem + visibleItemCount) == totalItemCount &&
                        rl_footer.getVisibility() == View.VISIBLE &&
                        !isPageLimitReached
                        ) {
                    if(!isLoading){
                        pharmList.setEnabled(false);
                        isLoading = true;
                        loadingIndicator.setVisibility(View.VISIBLE);
                        keyParams.put("page", ((int)keyParams.get("page"))+1);
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

    /**
     *  This function is for initialize Google map that was used in layout.
     *                   *  This adapter is used to display info window when users click on the marker on google map
     *
     *  It has a layout to show user when click on marker.
     *
     */


    public void initializeMapView(){

        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());

        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));

        googleMap = mapView.getMap();

            if(googleMap != null){
                googleMap.setInfoWindowAdapter(markerInfoAdapter);

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        Intent intent = new Intent(MDLivePharmacyResult.this, MDLivePharmacyDetails.class);

                        bundleToSend.putInt("pharmacy_id",(int) list.get(markerIdCollection.get(marker)).get("pharmacy_id"));
                        bundleToSend.putDouble("longitude",(Double) list.get(markerIdCollection.get(marker)).get("longitude"));
                        bundleToSend.putDouble("latitude",(Double) list.get(markerIdCollection.get(marker)).get("latitude"));
                        bundleToSend.putBoolean("twenty_four_hours", (Boolean) list.get(markerIdCollection.get(marker)).get("twenty_four_hours"));
                        bundleToSend.putBoolean("active", (Boolean) list.get(markerIdCollection.get(marker)).get("active"));
                        bundleToSend.putString("store_name",(String) list.get(markerIdCollection.get(marker)).get("store_name"));
                        bundleToSend.putString("phone",(String) list.get(markerIdCollection.get(marker)).get("phone"));
                        bundleToSend.putString("address1",(String) list.get(markerIdCollection.get(marker)).get("address1"));
                        bundleToSend.putString("address2",(String) list.get(markerIdCollection.get(marker)).get("address2"));
                        bundleToSend.putString("zipcode",(String) list.get(markerIdCollection.get(marker)).get("zipcode"));
                        bundleToSend.putString("fax",(String) list.get(markerIdCollection.get(marker)).get("fax"));
                        bundleToSend.putString("city",(String) list.get(markerIdCollection.get(marker)).get("city"));
                        bundleToSend.putString("distance",(String) list.get(markerIdCollection.get(marker)).get("distance"));
                        bundleToSend.putString("state",(String) list.get(markerIdCollection.get(marker)).get("state"));

                        intent.putExtra("datas", bundleToSend);

                        startActivity(intent);
                    }
                });

            }


    }


    /**
     *  This function is used to get pharmacy search results from webservice.
     *
     *  ResultPharmacyService is used to get results from webservice.
     *
     *  handleSuccessResponse is used to handle success response
     *
     *  @param postBody - post body required for make a request to web-server.
     *
     */

    public void getPharmacySearchResults(String postBody){

        pDialog = Utils.getProgressDialog(LocalizationSingleton.getLocalizedString(R.string.loading_txt, "loading_txt", this), this);

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
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacyResult.this);
                    }
                }
                resetLoadingViews();

            }};

        pDialog.show();

        ResultPharmacyService services = new ResultPharmacyService(MDLivePharmacyResult.this, null);
        services.doLoginRequest(postBody, responseListener, errorListener);
    }


    /*
    * This function is for reset views in scroll up update of list.
    * */
    private void resetLoadingViews(){
        isLoading = false;
        pharmList.setEnabled(true);
        loadingIndicator.setVisibility(View.GONE);
    }

    /**
     *  This adapter is used to display info window when users click on the marker on google map
     *
     *  It has a layout to show user when click on marker.
     */

    GoogleMap.InfoWindowAdapter markerInfoAdapter = new GoogleMap.InfoWindowAdapter() {

        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker arg0) {
            View v = getLayoutInflater().inflate(R.layout.pharm_custom_mapinfowindow_view, null);

            TextView addressText = (TextView) v.findViewById(R.id.addressText);

            HashMap<String, Object> info = list.get(markerIdCollection.get(arg0));

            addressText.setText(info.get("store_name")+"\n"+
                    info.get("address1")+"\n"+
                    info.get("city")+", "
                    +info.get("state") +" "
                    +info.get("zipcode"));

            return v;
        }

        @Override
        public View getInfoContents(Marker arg0) {
            return null;
        }
    };


    /**
     * This function is for get parsed post body details from received Intent.
     */


    private String getPostBody(Intent receivedIntent){

        if(receivedIntent.hasExtra("latitude"))
            keyParams.put("latitude", receivedIntent.getDoubleExtra("latitude", 0));

        if(receivedIntent.hasExtra("longitude"))
            keyParams.put("longitude", receivedIntent.getDoubleExtra("longitude", 0));

        if(receivedIntent.hasExtra("name"))
            keyParams.put("name", receivedIntent.getStringExtra("name"));

        if(receivedIntent.hasExtra("zipcode")){
            keyParams.put("zipcode", receivedIntent.getStringExtra("zipcode"));
        }else{
            if(receivedIntent.hasExtra("city"))
                keyParams.put("city", receivedIntent.getStringExtra("city"));
            if(receivedIntent.hasExtra("state"))
                keyParams.put("state", receivedIntent.getStringExtra("state"));
        }

        keyParams.put("per_page", 10);
        keyParams.put("page", 1);

        Gson gson = new Gson();
        String postBody = gson.toJson(keyParams);
        return postBody;
    }


    /**
     *
     *  This function is used to handle response which was thrown from getPharmacySearchResults function
     *
     *  Parsing json content and updating UI works done here.
     *
     *  @param response - response is catched response from getPharmacySearchResults network response
     */

    private void handleListSuccessResponse(JSONObject response) {

        try {
            pDialog.dismiss();
            Log.d("Response", response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            int total_pages =  responObj.get("total_pages").getAsInt();
            if(total_pages == ((int)keyParams.get("page")))
                isPageLimitReached = true;
            JsonArray responArray = responObj.get("pharmacies").getAsJsonArray();
            double longitude, latitude;
            // For google map
            googleMap = mapView.getMap();
            LatLng markerPoint = null;
            for(int i=0;i<responArray.size();i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map = setMapData(responArray, i);
                try {
                    if(responArray.get(i).getAsJsonObject().get("coordinates").isJsonNull()) {
                        longitude =0;
                        latitude =0;
                    }
                    else {
                        longitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject()
                                .get("longitude").getAsDouble();
                        latitude = responArray.get(i).getAsJsonObject().get("coordinates").getAsJsonObject()
                                .get("latitude").getAsDouble();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    longitude =0;
                    latitude =0;
                }
                map.put("longitude", longitude);
                map.put("latitude", latitude);
                if(googleMap != null){
                    markerPoint = new LatLng(latitude, longitude);
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                    markerPoint)
                                    .title(responArray.get(i).getAsJsonObject().get("store_name").getAsString())
                    );
                    markerIdCollection.put(marker, i);
                }
                list.add(map);
            }
            adaper.notifyDataSetChanged();

            //For Google map initialize view
            if(markerPoint != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private HashMap<String, Object> setMapData(JsonArray responArray, int i) {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("state", responArray.get(i).getAsJsonObject().get("state").getAsString());
            map.put("pharmacy_id", responArray.get(i).getAsJsonObject().get("pharmacy_id").getAsInt());
            map.put("store_name", responArray.get(i).getAsJsonObject().get("store_name").getAsString());
            map.put("phone", responArray.get(i).getAsJsonObject().get("phone").getAsString());
            map.put("address1", responArray.get(i).getAsJsonObject().get("address1").getAsString());
            map.put("active", responArray.get(i).getAsJsonObject().get("active").getAsBoolean());
            map.put("address2", responArray.get(i).getAsJsonObject().get("address2").getAsString());
            map.put("zipcode", responArray.get(i).getAsJsonObject().get("zipcode").getAsString());
            map.put("fax", responArray.get(i).getAsJsonObject().get("fax").getAsString());
            map.put("city", responArray.get(i).getAsJsonObject().get("city").getAsString());
            map.put("twenty_four_hours", responArray.get(i).getAsJsonObject().get("twenty_four_hours").getAsBoolean());
            map.put("distance", responArray.get(i).getAsJsonObject().get("distance").getAsString());
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<String, Object>();
        }
    }


}
