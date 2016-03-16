package com.mdlive.embedkit.uilayer.pharmacy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCoordinates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


/**
 * The wrapper class for Current Pharmacy Activity. This layout have the details about users default pharmacy details
 * Google map will indicate location of pharmacy. Click on change pharmacy will redirect to MDLivePharmacyChange page
 */

public class MDLivePharmacy extends MDLiveBaseActivity {

    private TextView addressline1, addressline2, addressline3;
    private SupportMapFragment mapView;
    //private RelativeLayout progressBar;
    private GoogleMap map;
    private Bundle bundletoSend = new Bundle();
    private IntentFilter intentFilter;
    private LocationCoordinates locationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy);
        clearMinimizedTime();
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
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        findViewById(R.id.txtApply).setContentDescription(getString(R.string.mdl_ada_right_arrow_button));
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_my_pharm_txt).toUpperCase());

        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        locationService = new LocationCoordinates(this);
        // First we need to check availability of play services
        if (MdliveUtils.checkPlayServices(this)) {
            // Building the GoogleApi client
            locationService.buildGoogleApiClient();
        }

        //This function is for initialize views in layout
        initializeViews();
        //This function is for initialize google map that was used in layout
        initializeMapView();
        //This function is for get user pharmacy details

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


        if (getIntent() != null && getIntent().hasExtra("Response"))
            loadDatas(getIntent().getStringExtra("Response"));
        else
            getUserPharmacyDetails();
    }

    public void rightBtnOnClick(View view){
        /*Intent i = new Intent(getBaseContext(),MDLiveInsuranceActivity.class);
        startActivityForResult(i, IntegerConstants.INSURANCE_ERROR_CODE);
        MdliveUtils.closingActivityAnimation(this);*/
       checkInsuranceEligibility();
    }
    /**
     * This function handles click listener of SavContinueBtn
     *
     * @param view - view of button which is called.
     */
    public void SavContinueBtnOnClick(View view) {
        /*Intent i = new Intent(getBaseContext(),MDLiveInsuranceActivity.class);
        startActivityForResult(i, IntegerConstants.INSURANCE_ERROR_CODE);
        MdliveUtils.closingActivityAnimation(this);*/
        checkInsuranceEligibility();
    }


    /**
     * This function handles click listener of changePharmacyButton
     *
     * @param view - view of button which is called.
     */
    public void changePharmacyButtonOnClick(View view) {
        Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MDLivePharmacy.this);
    }

    /**
     * This function handles click listener of home button
     *
     * @param view - view of button which is called.
     */
    public void homeImgOnClick(View view) {
        movetohome();
    }

    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void backImgOnClick(View view) {
        MdliveUtils.hideSoftKeyboard(MDLivePharmacy.this);
        onBackPressed();
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLivePharmacy.this);
        onBackPressed();
    }


    /**
     * This override function will be called on every time with this page loading.
     * <p/>
     * if any progressBar loading on screen anonymously on this will stop it.
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            registerReceiver(locationReceiver, intentFilter);
            //locationService.setBroadCastData(StringConstants.DEFAULT);
            if (!MdliveUtils.isNetworkAvailable(MDLivePharmacy.this)) {
                hideProgress();
                /*if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(locationReceiver);
            //locationService.setBroadCastData(StringConstants.DEFAULT);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
            * This function is mainly focused on initializing view in layout.
            * LocalisationHelper will be initialized over here to update tag details of view declared in xml
            */
    public void initializeViews() {
        addressline1 = ((TextView) findViewById(R.id.addressline1));
        addressline2 = ((TextView) findViewById(R.id.addressline2));
        addressline3 = ((TextView) findViewById(R.id.addressline3));

        //progressBar = (RelativeLayout) findViewById(R.id.progressDialog);
    }

    /**
     * This method handles checks user insurance eligibility and return the final amount for the user.
     * successListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * PharmacyService-Class will send the request to the server and get the responses
     * doPostCheckInsulranceEligibility-Method will carries required parameters for sending request to the server.
     */

    public void checkInsuranceEligibility() {
        showProgress();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                Log.v("Zero Dollar Insurance", response.toString());
                try {
                    JSONObject jobj = new JSONObject(response.toString());
                    if (jobj.has("final_amount")) {

                        if (!jobj.getString("final_amount").equals("0") && !jobj.getString("final_amount").equals("0.00")) {
                            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
                            if(userBasicInfo.getVerifyEligibility()) {
                                Intent i = new Intent(getApplicationContext(), MDLiveInsuranceActivity.class);
                                i.putExtra("final_amount", jobj.getString("final_amount"));
                                startActivity(i);
                                MdliveUtils.startActivityAnimation(MDLivePharmacy.this);
                            } else
                            {
                                try {
                                    Class clazz = Class.forName("com.mdlive.sav.payment.MDLivePayment");
                                    Intent i = new Intent(getApplicationContext(), clazz);
                                    i.putExtra("final_amount", jobj.getString("final_amount"));
                                    startActivity(i);
                                    MdliveUtils.startActivityAnimation(MDLivePharmacy.this);
                                } catch (ClassNotFoundException e){
                                    /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                                    Snackbar.make(findViewById(android.R.id.content),
                                            getString(R.string.mdl_mdlive_module_not_found),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }

                        } else {
                            moveToNextPage();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacy.this, error, getProgressDialog());
            }
        };
        PharmacyService insuranceService = new PharmacyService(MDLivePharmacy.this, null);
        insuranceService.doPostCheckInsulranceEligibility(formPostInsuranceParams(), successListener, errorListener);
    }

    // This is For navigating to the next Screen
    //if the amount has been deducted then it should go to the Confirm Appointment Screen

    private void moveToNextPage() {
        CheckdoconfirmAppointment(true);
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        if(userBasicInfo.getVerifyEligibility())
        {
            Intent i = new Intent(getApplicationContext(), MDLiveInsuranceActivity.class);
            i.putExtra("final_amount", "0.00");
            startActivity(i);
            MdliveUtils.startActivityAnimation(MDLivePharmacy.this);
        } else
        {
            try {
                Class clazz = Class.forName("com.mdlive.sav.payment.MDLiveConfirmappointment");
                Intent i = new Intent(MDLivePharmacy.this, clazz);
                storePayableAmount("0.00");
                startActivity(i);
                MdliveUtils.startActivityAnimation(MDLivePharmacy.this);
            }catch (ClassNotFoundException e){
               /* Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.mdl_mdlive_module_not_found),
                        Snackbar.LENGTH_LONG).show();
            }
        }


    }
    public void CheckdoconfirmAppointment(boolean checkExixtingCard) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(PreferenceConstants.EXISTING_CARD_CHECK,checkExixtingCard);
        editor.commit();
    }

    public void storePayableAmount(String amount) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.AMOUNT, amount);
        editor.commit();
    }


    /**
     * This function is used to get post body content for Check Insurance Eligibility
     * Values hard coded are default criteria from get response of Insurance Eligibility of all users.
     */

    public String formPostInsuranceParams() {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, String> insuranceMap = new HashMap<>();
        insuranceMap.put("appointment_method", "1");
        insuranceMap.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        insuranceMap.put("timeslot", "Now");
        insuranceMap.put("provider_type_id",settings.getString(PreferenceConstants.PROVIDERTYPE_ID,""));

        insuranceMap.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        return new Gson().toJson(insuranceMap);
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
        showProgress();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacy.this, error, getProgressDialog());
            }
        };
        callPharmacyService(responseListener, errorListener);
    }

    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener - Pharmacy error response listener
     *  @param responseListener - Pharmacy detail Success response listener
     */
    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
        if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
            showProgress();
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(getApplicationContext());
        }else{
            PharmacyService services = new PharmacyService(MDLivePharmacy.this, null);
            services.doMyPharmacyRequest("", "", responseListener, errorListener);
        }
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleSuccessResponse(response);
                }
            };
            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    MdliveUtils.handelVolleyErrorResponse(MDLivePharmacy.this, error, getProgressDialog());
                }
            };
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                double lat = intent.getDoubleExtra("Latitude", 0d);
                double lon = intent.getDoubleExtra("Longitude", 0d);
                PharmacyService services = new PharmacyService(MDLivePharmacy.this, null);
                services.doMyPharmacyRequest(lat+"", lon+"",responseListener, errorListener);
            }else{
                PharmacyService services = new PharmacyService(MDLivePharmacy.this, null);
                services.doMyPharmacyRequest("","",responseListener, errorListener);
            }
        }
    };

    /* This function is used to initialize map view for MDLivePharmacy activity */
    public void initializeMapView() {
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        map = mapView.getMap();
        if (map != null) {
            map.setInfoWindowAdapter(null);
            map.getUiSettings().setScrollGesturesEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(false);
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    marker.hideInfoWindow();
                    return true;
                }
            });

            map.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgress();
            final JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
            addressline1.setText(pharmacyDatas.getString("store_name") + " "+
                    ((pharmacyDatas.getString("distance")!=null && !pharmacyDatas.getString("distance").isEmpty())?
                            pharmacyDatas.getString("distance").replace(" miles", "mi") : ""));
            addressline2.setText(pharmacyDatas.getString("address1"));
            addressline3.setText(pharmacyDatas.getString("city") + ", "
                    + pharmacyDatas.getString("state") + " "
                    + (TextUtils.isEmpty(pharmacyDatas.getString("zipcode")) ? "" : MdliveUtils.zipCodeFormat(pharmacyDatas.getString("zipcode"))));
            bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));

            JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
            if(pharmacyDatas.has("phone")){
                ((TextView) findViewById(R.id.txt_my_pharmacy_addressline_four)).setText(MdliveUtils.formatDualString(pharmacyDatas.getString("phone")));
                findViewById(R.id.txt_my_pharmacy_addressline_four).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + MdliveUtils.formatDualString(pharmacyDatas.getString("phone"))));
                            startActivity(intent);
                        } catch (JSONException e) {

                        }
                    }
                });
            }
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
                map.setInfoWindowAdapter(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This function is used for parse and update UI for server response.
     *
     * @param response - Response received from server.
     */
    public void loadDatas(String response) {
        try {
            JSONObject jobj = new JSONObject(response);
            final JSONObject pharmacyDatas = jobj.getJSONObject("pharmacy");
            addressline1.setText(pharmacyDatas.getString("store_name")+" "+
                    ((pharmacyDatas.getString("distance")!=null && !pharmacyDatas.getString("distance").isEmpty())?
                            pharmacyDatas.getString("distance").replace(" miles", "mi") : ""));
            addressline2.setText(pharmacyDatas.getString("address1"));
            addressline3.setText(pharmacyDatas.getString("city") + ", "
                    + pharmacyDatas.getString("state") + " "
                    + MdliveUtils.zipCodeFormat(pharmacyDatas.getString("zipcode")));
            bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
            JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
            if(pharmacyDatas.has("phone")){
                ((TextView) findViewById(R.id.txt_my_pharmacy_addressline_four)).setText(MdliveUtils.formatDualString(pharmacyDatas.getString("phone")));
                findViewById(R.id.txt_my_pharmacy_addressline_four).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + MdliveUtils.formatDualString(pharmacyDatas.getString("phone"))));
                            startActivity(intent);
                        } catch (JSONException e) {

                        }
                    }
                });
            }
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
    public void movetohome() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.mdl_home_dialog_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.mdl_home_dialog_text));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.mdl_ok_upper), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                            MdliveUtils.ssoInstance.getparentClassname());
                    intent.setComponent(cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alert.show();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IntegerConstants.INSURANCE_ERROR_CODE) {
            try {
                showDialog(getApplicationContext(),
                        "Connection Timed Out Error Occured.", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void showDialog(final Context context, String message,
                                  DialogInterface.OnClickListener positiveOnclickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setTitle("MDLIVE")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",positiveOnclickListener);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
//                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alertDialog.show();
    }

}