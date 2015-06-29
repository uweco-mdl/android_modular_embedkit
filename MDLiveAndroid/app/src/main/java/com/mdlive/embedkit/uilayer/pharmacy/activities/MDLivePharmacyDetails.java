package com.mdlive.embedkit.uilayer.pharmacy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.SetPharmacyService;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * The wrapper class for Pharmacy Details Activity. This layout have the details about pharmacy details
 * Google map will indicate location of pharmacy.
 */
public class MDLivePharmacyDetails extends FragmentActivity {

    private TextView addressLine1, addressLine2, addressLine3, phoneText, milesText;
    private SupportMapFragment mapView;
    private GoogleMap map;
    private ProgressDialog pDialog;
    private Bundle receivedBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy_details);
        initializeViews();
        initializeMapView();
        updateUiFromGetIntent();
    }

    /* This function is used to initialized views defined in layout.
     */

    public void initializeViews() {

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);

        addressLine1 = ((TextView) findViewById(R.id.addressLine1));
        addressLine2 = ((TextView) findViewById(R.id.addressLine2));
        addressLine3 = ((TextView) findViewById(R.id.addressLine3));
        phoneText = ((TextView) findViewById(R.id.phoneText));
        milesText = ((TextView) findViewById(R.id.milesText));


        ((Button) findViewById(R.id.usePharmacy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPharmacyAsADefault();
            }
        });

//        ((ImageView) findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        ((Button) findViewById(R.id.getDirections)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        LocationCooridnates locationService = new LocationCooridnates();

                        if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
                            locationService.getLocation(MDLivePharmacyDetails.this, new LocationCooridnates.LocationResult(){
                                @Override
                                public void gotLocation(final Location location) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(location != null){
                                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?saddr=" +
                                                                location.getLatitude() +
                                                                "," +
                                                                location.getLongitude() +
                                                                "&daddr=" +
                                                                "20.5666" +
                                                                "," +
                                                                "45.345"));
                                                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("google.navigation:q=" +
                                                                location.getLatitude() +
                                                                "," +
                                                                location.getLongitude()));*/
                                                startActivity(intent);
                                                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));*/
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Unable to get your location!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "Please enable location service", Toast.LENGTH_SHORT).show();
                        }
                    }
        });


        pDialog = Utils.getProgressDialog("Loading...", this);
    }

    /* This function is used to initialize map view for MDLBTPharmacy_Details activity */

    public void initializeMapView(){
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        map = mapView.getMap();
    }

    /**
     *  This function has a webservice call of SetPharmacyService
     *  While user clicks on the usePharmacy button which will set pharmacy as a user's default.
     */

    public void setPharmacyAsADefault(){

        pDialog = Utils.getProgressDialog("Please Wait...", MDLivePharmacyDetails.this);

        pDialog.show();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.handelVolleyErrorResponse(MDLivePharmacyDetails.this, error, pDialog);
            }};

        HashMap<String, Integer> gsonMap = new HashMap<String, Integer>();
        gsonMap.put("pharmacy_id", receivedBundle.getInt("pharmacy_id"));
        SetPharmacyService services = new SetPharmacyService(MDLivePharmacyDetails.this, pDialog);
        services.doPharmacyResultsRequest(new Gson().toJson(gsonMap),String.valueOf(receivedBundle.getInt("pharmacy_id")),
                        responseListener, errorListener);
    }

    /**
     *  This function is handling response of SetPharmacyService which was thrown from
     *
     *  function setPharmacyAsADefault()
     *
     */


    private void handleSuccessResponse(JSONObject response) {

        try {
            pDialog.dismiss();

            Log.d("Response", response.toString());

            if(response.getString("message").equals("Pharmacy details updated")){
                Toast.makeText(getApplicationContext(), "Default Pharmacy Saved!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /* This function is used to update ui while MDLBTPharmacy_Details page getting load. */

    public void updateUiFromGetIntent() {
        Intent getIntent = getIntent();
        receivedBundle = new Bundle();
        if (getIntent != null) {
            receivedBundle = getIntent.getBundleExtra("datas");
            addressLine1.setText(receivedBundle.getString("store_name"));
            addressLine2.setText(receivedBundle.getString("address1"));
            addressLine3.setText(receivedBundle.getString("city") + ", "
                    + receivedBundle.getString("state") + " "
                    + receivedBundle.getString("zipcode"));

            phoneText.setText(receivedBundle.getString("phone"));
            milesText.setText(receivedBundle.getString("distance"));

            LatLng markerPoint = new LatLng(receivedBundle.getDouble("latitude"),
                    receivedBundle.getDouble("longitude"));
            if (map != null) {
                map.addMarker(new MarkerOptions().position(markerPoint)
                        .title(receivedBundle.getString("store_name")));
                configureMap(map);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 15));
                map.getUiSettings().setZoomControlsEnabled(true);
            }
            //pharmacy_id
        }
    }

    private void configureMap(GoogleMap map)
    {
        if (map == null)
            return; // Google Maps not available
        try {
            MapsInitializer.initialize(MDLivePharmacyDetails.this);
        }
        catch (Exception e) {
            Log.e("Map Status", "Have GoogleMap but then error", e);
            return;
        }

    }
}