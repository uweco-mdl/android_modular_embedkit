package com.mdlive.embedkit.uilayer.pharmacy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.embedkit.uilayer.payment.MDLivePayment;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
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
                checkInsuranceEligibility();
              /*  Intent i = new Intent(getApplicationContext(), MDLivePayment.class);
                startActivity(i);*/
            }
        });
        ((Button) findViewById(R.id.changePharmacyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                startActivity(i);
            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLivePharmacy.this);
                finish();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });

        pDialog = Utils.getProgressDialog("Please wait...", this);
    }


    /**
     * This method handles checks user insurance eligibility and return the final amount for the user.
     * successListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * PharmacyService-Class will send the request to the server and get the responses
     *doPostCheckInsulranceEligibility-Method will carries required parameters for sending request to the server.
     */

    public void checkInsuranceEligibility(){
        pDialog.show();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                pDialog.dismiss();
               Log.e("Zero Dollar Insurance", response.toString());
                try{
                    JSONObject jobj=new JSONObject(response.toString());
                    if(jobj.has("final_amount")){
                        if(Integer.parseInt(jobj.getString("final_amount"))>0){
                            Intent i = new Intent(getApplicationContext(), MDLivePayment.class);
                            i.putExtra("final_amount",jobj.getString("final_amount"));
                            startActivity(i);
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
                pDialog.dismiss();
                Utils.handelVolleyErrorResponse(MDLivePharmacy.this,error,pDialog);
            }
        };
        PharmacyService insuranceService=new PharmacyService(MDLivePharmacy.this,pDialog);
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
                Utils.handelVolleyErrorResponse(MDLivePharmacy.this,error,pDialog);
                /*if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacy.this);
                    }
                }*/
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("appointment_method", "1");
        params.put("do_you_have_primary_care_physician","No");
        // params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", "Not Sure");
        params.put("customer_call_in_number", "9068906789");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION,"FL"));


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
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePharmacy.this, pDialog);
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
                Utils.handelVolleyErrorResponse(MDLivePharmacy.this, error, pDialog);
            }
        };
        PharmacyService services = new PharmacyService(MDLivePharmacy.this, pDialog);
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
        }
    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();

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
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLivePharmacy.this, MDLiveLogin.class);
    }

}