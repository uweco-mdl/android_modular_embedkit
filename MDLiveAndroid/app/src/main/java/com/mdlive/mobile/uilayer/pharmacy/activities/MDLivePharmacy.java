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
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.pharmacy.services.PharmacyService;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;


/**
 * The wrapper class for Current Pharmacy Activity. This layout have the details about users default pharmacy details
 * Google map will indicate location of pharmacy. Click on change pharmacy will redirect to MDLivePharmacyChange page
 */

public class MDLivePharmacy extends FragmentActivity {

    private TextView addressLine1, addressLine2, addressLine3, addressLine4;
    private ProgressDialog pDialog;
    private SupportMapFragment mapView;
    private GoogleMap map;
    private Bundle bundletoSend = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);

        //This function is for initialize views in layout
        initializeViews();

        //This function is for initialize google map that was used in layout
        initializeMapView();

        //This function is for get user pharmacy details
        getUserPharmacyDetails();
    }

    /*
    * This function is mainly focused on initializing view in layout.
    * LocalisationHelper will be initialized over here to update tag details of view declared in xml
    */
    public void initializeViews(){

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);

        addressLine1 = ((TextView) findViewById(R.id.addressline1));
        addressLine2 = ((TextView) findViewById(R.id.addressline2));
        addressLine3 = ((TextView) findViewById(R.id.addressline3));
        addressLine4 = ((TextView) findViewById(R.id.addressline4));

        ((Button) findViewById(R.id.changePharmacyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                startActivity(i);
            }
        });

        ((RelativeLayout) findViewById(R.id.defaultPharmlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyDetails.class);
                i.putExtra("datas", bundletoSend);
                startActivity(i);
            }
        });
        pDialog = Utils.getProgressDialog(LocalizationSingleton.getLocalizedString(R.string.loading_txt, "loading_txt", this), this);

    }

    /*
   * This function will get latest default pharmacy details of users from webservice.
   *
   * PharmacyService class handles webservice integration.
   *
   *
   * @responseListener - Receives webservice information
   * @errorListener - Received error information (if any problem in webservice)
   *
   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
   * to parse message content.
   */

    public void getUserPharmacyDetails(){
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
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacy.this);
                    }
                }
            }};

        PharmacyService services = new PharmacyService(MDLivePharmacy.this, null);
        services.doLoginRequest(responseListener,errorListener);
    }

    /*
     * This function is used to initialize map view for MDLivePharmacy activity
     * The setInfoWindowAdapter adapter is used to display info window when users click on
     * the marker on google map. It has a layout to show user when click on marker.
     *
     * */

    public void initializeMapView(){
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        map = mapView.getMap();

        if(map != null){
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    View v = getLayoutInflater().inflate(R.layout.pharm_custom_mapinfowindow_view, null);

                    TextView addressText = (TextView) v.findViewById(R.id.addressText);

                    addressText.setText(bundletoSend.get("store_name")+"\n"+
                            bundletoSend.get("address1")+"\n"+
                            bundletoSend.get("city")+", "
                            +bundletoSend.get("state") +" "
                            +bundletoSend.get("zipcode"));

                    return v;
                }
                @Override
                public View getInfoContents(Marker arg0) {
                    return null;
                }
            });

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    Intent i = new Intent(getApplicationContext(), MDLivePharmacyDetails.class);
                    i.putExtra("datas", bundletoSend);
                    startActivity(i);
                }
            });

        }

    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            Log.d("Response", response.toString());
            JSONObject pharmacyDatas = response.getJSONObject("pharmacy");

            addressLine1.setText(pharmacyDatas.getString("store_name"));
            addressLine2.setText(pharmacyDatas.getString("address1"));
            addressLine3.setText(pharmacyDatas.getString("city") + ", "
                    + pharmacyDatas.getString("state") + " "
                    + pharmacyDatas.getString("zipcode"));

            addressLine4.setText(pharmacyDatas.getString("phone"));
            bundletoSend.putInt("pharmacy_id",pharmacyDatas.getInt("pharmacy_id"));
            JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
            bundletoSend.putDouble("longitude",coordinates.getDouble("longitude"));
            bundletoSend.putDouble("latitude",coordinates.getDouble("latitude"));
            bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
            bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
            bundletoSend.putString("store_name",pharmacyDatas.getString("store_name"));
            bundletoSend.putString("phone",pharmacyDatas.getString("phone"));
            bundletoSend.putString("address1",pharmacyDatas.getString("address1"));
            bundletoSend.putString("address2",pharmacyDatas.getString("address2"));
            bundletoSend.putString("zipcode",pharmacyDatas.getString("zipcode"));
            bundletoSend.putString("fax",pharmacyDatas.getString("fax"));
            bundletoSend.putString("city",pharmacyDatas.getString("city"));
            bundletoSend.putString("distance",pharmacyDatas.getString("distance"));
            bundletoSend.putString("state",pharmacyDatas.getString("state"));

            if(map != null){
              LatLng markerPoint = new LatLng(Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("latitude")),
                      Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("longitude")));

               map.addMarker(new MarkerOptions().position(markerPoint).title("Marker"));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }



}