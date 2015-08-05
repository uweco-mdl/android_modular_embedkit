package com.mdlive.embedkit.uilayer.pharmacy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.payment.MDLivePayment;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONObject;

import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


/**
 * The wrapper class for Current Pharmacy Activity. This layout have the details about users default pharmacy details
 * Google map will indicate location of pharmacy. Click on change pharmacy will redirect to MDLivePharmacyChange page
 */

public class MDLivePharmacy extends FragmentActivity {

    private TextView addressline1, addressline2, addressline3;
    private ProgressDialog pDialog;
    private Button continueButton;
    private SupportMapFragment mapView;
    private GoogleMap map;
    private Bundle bundletoSend = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy);
        //This function is for initialize views in layout
        initializeViews();
        //This function is for initialize google map that was used in layout
        initializeMapView();
        //This function is for get user pharmacy details
        //getUserPharmacyDetails();
        if(getIntent() != null && getIntent().hasExtra("Response"))
            loadDatas(getIntent().getStringExtra("Response"));
        else
            getUserPharmacyDetails();
    }

    /*
    * This function is mainly focused on initializing view in layout.
    * LocalisationHelper will be initialized over here to update tag details of view declared in xml
    */
    public void initializeViews() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        addressline1 = ((TextView) findViewById(R.id.addressline1));
        addressline2 = ((TextView) findViewById(R.id.addressline2));
        addressline3 = ((TextView) findViewById(R.id.addressline3));
        ((Button) findViewById(R.id.SavContinueBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                doConfirmAppointment();
                Intent i = new Intent(getApplicationContext(), MDLivePayment.class);
                startActivity(i);
            }
        });
        ((Button) findViewById(R.id.changePharmacyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                startActivity(i);
            }
        });

      /*  ((RelativeLayout) findViewById(R.id.defaultPharmlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyDetails.class);
                i.putExtra("datas", bundletoSend);
                startActivity(i);
            }
        });*/
        pDialog = MdliveUtils.getProgressDialog("Please wait...", this);
    }

    private void doConfirmAppointment() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    pDialog.dismiss();
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePharmacy.this, MDLiveWaitingRoom.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(MDLivePharmacy.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLivePharmacy.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLivePharmacy.this);
                }
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("appointment_method", "1");
        params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", "Not Sure");
        params.put("state_id", "FL");
        params.put("customer_call_in_number", "9068906789");
        params.put("chief_complaint_reasons", null);
        params.put("alternate_visit_option", "alternate_visit_option");
        params.put("do_you_have_primary_care_physician", "No");
        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePharmacy.this, null);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
    }

    /*
   * This function will get latest default pharmacy details of users from webservice.
   * PharmacyService class handles webservice integration.
   * @responseListener - Receives webservice informatoin
   * @errorListener - Received error information (if any problem in webservice)
   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
   * to parse message content.
   */

    public void getUserPharmacyDetails() {
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
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLivePharmacy.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLivePharmacy.this);
                }
            }
        };
        PharmacyService services = new PharmacyService(MDLivePharmacy.this, null);
        services.doMyPharmacyRequest(responseListener, errorListener);
    }

    /* This function is used to initialize map view for MDLivePharmacy activity */

    public void initializeMapView() {
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        map = mapView.getMap();
        if (map != null) {

            /**
             *  This adapter is used to display info window when users click on the marker on google map
             *
             *  It has a layout to show user when click on marker.
             */
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    View v = getLayoutInflater().inflate(R.layout.mdlive_pharm_custom_mapinfowindow_view, null);
                    TextView addressText = (TextView) v.findViewById(R.id.addressText);
                    addressText.setText(bundletoSend.get("store_name") + "\n" +
                            bundletoSend.get("address1") + "\n" +
                            bundletoSend.get("city") + ", "
                            + bundletoSend.get("state") + " "
                            + bundletoSend.get("zipcode"));
                    return v;
                }

                @Override
                public View getInfoContents(Marker arg0) {
                    return null;
                }
            });
            /*map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent i = new Intent(getApplicationContext(), MDLivePharmacyDetails.class);
                    i.putExtra("datas", bundletoSend);
                    startActivity(i);
                }
            });*/
        }
    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();;
            Log.d("Response", response.toString());
            JSONObject pharmacyDatas = response.getJSONObject("pharmacy");

            addressline1.setText(pharmacyDatas.getString("store_name")+" "+pharmacyDatas.getString("distance"));
            addressline2.setText(pharmacyDatas.getString("address1"));
            addressline3.setText(pharmacyDatas.getString("city") + ", "
                    + pharmacyDatas.getString("state") + " "
                    + pharmacyDatas.getString("zipcode"));
            bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
            JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
            bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
            bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
            bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
            bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
            bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
            bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
            bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
            bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
            bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
            bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
            bundletoSend.putString("city", pharmacyDatas.getString("city"));
            bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
            bundletoSend.putString("state", pharmacyDatas.getString("state"));
            if (map != null) {
                LatLng markerPoint = new LatLng(Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("latitude")),
                        Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("longitude")));
                Marker marker = map.addMarker(new MarkerOptions().position(markerPoint)
                        .title("Marker"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDatas(String response){
        try {
        JSONObject jobj=new JSONObject(response);
        JSONObject pharmacyDatas = jobj.getJSONObject("pharmacy");
        addressline1.setText(pharmacyDatas.getString("store_name"));
        addressline2.setText(pharmacyDatas.getString("address1"));
        addressline3.setText(pharmacyDatas.getString("city") + ", "
                + pharmacyDatas.getString("state") + " "
                + pharmacyDatas.getString("zipcode"));
    //        addressline4.setText(pharmacyDatas.getString("phone"));
        bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
        JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
        bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
        bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
        bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
        bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
        bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
        bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
        bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
        bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
        bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
        bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
        bundletoSend.putString("city", pharmacyDatas.getString("city"));
        bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
        bundletoSend.putString("state", pharmacyDatas.getString("state"));
        if (map != null) {
            LatLng markerPoint = new LatLng(Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("latitude")),
                    Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("longitude")));
            Marker marker = map.addMarker(new MarkerOptions().position(markerPoint)
                    .title("Marker"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}