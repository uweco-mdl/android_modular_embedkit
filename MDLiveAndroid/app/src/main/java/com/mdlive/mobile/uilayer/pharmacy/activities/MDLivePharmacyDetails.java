package com.mdlive.mobile.uilayer.pharmacy.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.pharmacy.services.SetPharmacyService;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

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
        setContentView(R.layout.activity_pharmacy_details);

        //This function is for initialize views in layout
        initializeViews();

        //This function is for initialize google map that was used in layout
        initializeMapView();

        //This function is for set values for views for intent that was passed from previous
        updateUiFromGetIntent();
    }

    /**
     *This function is used to initialized views defined in layout.
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
        pDialog = Utils.getProgressDialog(LocalizationSingleton.getLocalizedString(R.string.loading_txt, "loading_txt", this), this);
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

        pDialog = Utils.getProgressDialog(LocalizationSingleton.getLocalizedString(R.string.loading_txt, "loading_txt", this), MDLivePharmacyDetails.this);

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
                /*pDialog.dismiss();*/
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacyDetails.this);
                    }
                }
            }};

        HashMap<String, Integer> gsonMap = new HashMap<String, Integer>();
        gsonMap.put("pharmacy_id", receivedBundle.getInt("pharmacy_id"));
        SetPharmacyService services = new SetPharmacyService(MDLivePharmacyDetails.this, null);
        services.doLoginRequest(new Gson().toJson(gsonMap),String.valueOf(receivedBundle.getInt("pharmacy_id")),
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

    /**
     *
     * This function is used to update ui while MDLBTPharmacy_Details page getting load.
     *
     * */

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

            map = mapView.getMap();
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


    /**
     * This function is for configure Google map when this page is getting loading.
     *
     * This function will solve the problem if google map is not properly initialized.
     *
     */
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