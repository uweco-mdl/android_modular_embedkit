package com.mdlive.embedkit.uilayer.pharmacy.activities;

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
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.pharmacy.adapter.PharmacyListAdaper;
import com.mdlive.embedkit.uilayer.pharmacy.customui.CustomFlt;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.ResultPharmacyService;

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
        setContentView(R.layout.mdlive_pharmacy_resultnew);
        initializeViews();
        initializeListView();
        initializeMapView();
        keyParams = new HashMap<String, Object>();
        getPharmacySearchResults(getPostBody(getIntent()));
    }

    /**
     * This funciton initialize views of activity
     */
    public void initializeViews() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
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
                if (rl_footer.getVisibility() == View.VISIBLE) {
                    rl_footer.setVisibility(View.GONE);
                    ((Button) findViewById(R.id.footerButton)).setVisibility(View.VISIBLE);
                } else {
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
    * This function is mainly focused on initializing view in layout.
    */

    public void initializeListView() {
        pharmList = (ListView) findViewById(R.id.pharmList);
        adaper = new PharmacyListAdaper(MDLivePharmacyResult.this, list);
        pharmList.setAdapter(adaper);
        pharmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MDLivePharmacyResult.this.getApplicationContext(), MDLivePharmacyDetails.class);
                bundleToSend.putInt("pharmacy_id", (int) list.get(position).get("pharmacy_id"));
                bundleToSend.putDouble("longitude", (Double) list.get(position).get("longitude"));
                bundleToSend.putDouble("latitude", (Double) list.get(position).get("latitude"));
                bundleToSend.putBoolean("twenty_four_hours", (Boolean) list.get(position).get("twenty_four_hours"));
                bundleToSend.putBoolean("active", (Boolean) list.get(position).get("active"));
                bundleToSend.putString("store_name", (String) list.get(position).get("store_name"));
                bundleToSend.putString("phone", (String) list.get(position).get("phone"));
                bundleToSend.putString("address1", (String) list.get(position).get("address1"));
                bundleToSend.putString("address2", (String) list.get(position).get("address2"));
                bundleToSend.putString("zipcode", (String) list.get(position).get("zipcode"));
                bundleToSend.putString("fax", (String) list.get(position).get("fax"));
                bundleToSend.putString("city", (String) list.get(position).get("city"));
                bundleToSend.putString("distance", (String) list.get(position).get("distance"));
                bundleToSend.putString("state", (String) list.get(position).get("state"));
                i.putExtra("datas", bundleToSend);
                startActivity(i);
            }
        });
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
                        loadingIndicator.setVisibility(View.VISIBLE);
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
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(MDLivePharmacyResult.this, MDLivePharmacyDetails.class);
                        bundleToSend.putInt("pharmacy_id", (int) list.get(markerIdCollection.get(marker)).get("pharmacy_id"));
                        bundleToSend.putDouble("longitude", (Double) list.get(markerIdCollection.get(marker)).get("longitude"));
                        bundleToSend.putDouble("latitude", (Double) list.get(markerIdCollection.get(marker)).get("latitude"));
                        bundleToSend.putBoolean("twenty_four_hours", (Boolean) list.get(markerIdCollection.get(marker)).get("twenty_four_hours"));
                        bundleToSend.putBoolean("active", (Boolean) list.get(markerIdCollection.get(marker)).get("active"));
                        bundleToSend.putString("store_name", (String) list.get(markerIdCollection.get(marker)).get("store_name"));
                        bundleToSend.putString("phone", (String) list.get(markerIdCollection.get(marker)).get("phone"));
                        bundleToSend.putString("address1", (String) list.get(markerIdCollection.get(marker)).get("address1"));
                        bundleToSend.putString("address2", (String) list.get(markerIdCollection.get(marker)).get("address2"));
                        bundleToSend.putString("zipcode", (String) list.get(markerIdCollection.get(marker)).get("zipcode"));
                        bundleToSend.putString("fax", (String) list.get(markerIdCollection.get(marker)).get("fax"));
                        bundleToSend.putString("city", (String) list.get(markerIdCollection.get(marker)).get("city"));
                        bundleToSend.putString("distance", (String) list.get(markerIdCollection.get(marker)).get("distance"));
                        bundleToSend.putString("state", (String) list.get(markerIdCollection.get(marker)).get("state"));
                        intent.putExtra("datas", bundleToSend);
                        startActivity(intent);
                    }
                });
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
        pDialog = Utils.getProgressDialog("Please Wait...", MDLivePharmacyResult.this);
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
            }
        };
        pDialog.show();
        ResultPharmacyService services = new ResultPharmacyService(MDLivePharmacyResult.this, null);
        services.doPharmacyLocationRequest(postBody, responseListener, errorListener);
    }


    /*
    * This function is for reset views in scroll up update of list.
    * */
    private void resetLoadingViews() {
        isLoading = false;
        pharmList.setEnabled(true);
        loadingIndicator.setVisibility(View.GONE);
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
        try {
            pDialog.dismiss();
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            int total_pages = responObj.get("total_pages").getAsInt();
            if (total_pages == ((int) keyParams.get("page")))
                isPageLimitReached = true;
            JsonArray responArray = responObj.get("pharmacies").getAsJsonArray();
            int pharmacy_id;
            double longitude, latitude;
            boolean twenty_four_hours, active;
            String store_name, phone, address1, address2, zipcode, fax, city, distance, state;
            // For google map
            googleMap = mapView.getMap();
            LatLng markerPoint = null;
            for (int i = 0; i < responArray.size(); i++) {
                state = responArray.get(i).getAsJsonObject().get("state").getAsString();
                pharmacy_id = responArray.get(i).getAsJsonObject().get("pharmacy_id").getAsInt();
                store_name = responArray.get(i).getAsJsonObject().get("store_name").getAsString();
                phone = responArray.get(i).getAsJsonObject().get("phone").getAsString();
                address1 = responArray.get(i).getAsJsonObject().get("address1").getAsString();
                active = responArray.get(i).getAsJsonObject().get("active").getAsBoolean();
                address2 = responArray.get(i).getAsJsonObject().get("address2").getAsString();
                zipcode = responArray.get(i).getAsJsonObject().get("zipcode").getAsString();
                fax = responArray.get(i).getAsJsonObject().get("fax").getAsString();
                city = responArray.get(i).getAsJsonObject().get("city").getAsString();
                twenty_four_hours = responArray.get(i).getAsJsonObject().get("twenty_four_hours").getAsBoolean();
                distance = responArray.get(i).getAsJsonObject().get("distance").getAsString();
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
                if (googleMap != null) {
                    markerPoint = new LatLng(latitude, longitude);
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                    markerPoint)
                                    .title(store_name)
                    );
                    markerIdCollection.put(marker, i);
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
    }

}
