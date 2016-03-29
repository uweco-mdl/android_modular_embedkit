package com.mdlive.embedkit.uilayer.pharmacy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCoordinates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import javax.net.ssl.HttpsURLConnection;

public class MDLivePharmacyFragment extends MDLiveBaseFragment {
    private TextView addressline1, addressline2, addressline3;
    private SupportMapFragment mapView;
    // private RelativeLayout progressBar;
    private GoogleMap map;
    private View mSmallMapView;
    private Bundle bundletoSend = new Bundle();
    private IntentFilter intentFilter;
    private static View view;
    private Activity parentActivity;
    private LocationCoordinates locationService;
    private boolean isVisibleToUser = false;
    private boolean isLoading = false;

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            if (intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                double lat = intent.getDoubleExtra("Latitude", 0d);
                double lon = intent.getDoubleExtra("Longitude", 0d);
                if (lat != 0 && lon != 0) {
                    Intent i = new Intent(parentActivity, MDLivePharmacyResult.class);
                    i.putExtra("longitude", lat);
                    i.putExtra("latitude", lon);
                    i.putExtra("FROM_MY_HEALTH", true);
                    i.putExtra("PHARMACY_SELECTED", false);
                    i.putExtra("errorMesssage", getString(R.string.mdl_no_pharmacies_listed));
                    startActivity(i);
                    MdliveUtils.startActivityAnimation(parentActivity);
                } else {
                    MdliveUtils.showGPSFailureDialog(parentActivity,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent pharmacyintent = new Intent(parentActivity, MDLivePharmacyChange.class);
                                    pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                                    pharmacyintent.putExtra("PHARMACY_SELECTED", false);
                                    startActivity(pharmacyintent);
                                }
                            });
                }
            } else {
                /*MdliveUtils.showGPSFailureDialog(parentActivity,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent pharmacyintent = new Intent(parentActivity, MDLivePharmacyChange.class);
                                pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                                pharmacyintent.putExtra("PHARMACY_SELECTED", false);
                                startActivity(pharmacyintent);
                            }
                        });*/
                Intent pharmacyintent = new Intent(parentActivity, MDLivePharmacyChange.class);
                pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                pharmacyintent.putExtra("PHARMACY_SELECTED", false);
                startActivity(pharmacyintent);
            }
        }
    };

    public MDLivePharmacyFragment() {
        super();
    }

    public static MDLivePharmacyFragment newInstance() {
        final MDLivePharmacyFragment pharmacyFragment = new MDLivePharmacyFragment();
        return pharmacyFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.mdlive_pharmacy_fragment, container, false);
            parentActivity.setTitle(getString(R.string.mdl_pharmacy));
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //This function is for initialize views in layout
        initializeViews(view);
        //This function is for initialize google map that was used in layout

        //This function is for get user pharmacy details
       // getUserPharmacyDetails();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationService = new LocationCoordinates(getActivity());
        if (MdliveUtils.checkPlayServices(getActivity())) {
            // Building the GoogleApi client
            locationService.buildGoogleApiClient();
        }
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        initializeMapView();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            locationService.setBroadCastData(StringConstants.DEFAULT);
            parentActivity.unregisterReceiver(locationReceiver);
            if (locationService != null && locationService.isTrackingLocation()) {
                locationService.stopListeners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            if (MdliveUtils.checkPlayServices(getActivity())) {
                locationService.setBroadCastData(StringConstants.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
        getUserPharmacyDetails();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if(!isLoading){
            isLoading = true;
            getUserPharmacyDetails();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /*
     * This function is mainly focused on initializing view in layout.
     * LocalisationHelper will be initialized over here to update tag details of view declared in xml
     */
    public void initializeViews(View view) {
        addressline1 = ((TextView) view.findViewById(R.id.addressline1));
        addressline2 = ((TextView) view.findViewById(R.id.addressline2));
        addressline3 = ((TextView) view.findViewById(R.id.addressline3));
//        progressBar = (RelativeLayout) view.findViewById(R.id.progressDialog);

        mSmallMapView = view.findViewById(R.id.small_map_layout);
    }

    /*
   * This function will get latest default pharmacy details of users from webservice.
   * PharmacyService class handles webservice integration.
   * @responseListener - Receives webservice information
   * @errorListener - Received error information (if any problem in webservice)
   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
   * to parse message content.
   */
    public void getUserPharmacyDetails() {
//        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
                MdliveUtils.handleVolleyErrorResponse(getActivity(), error, getProgressDialog());
            }
        };
        callPharmacyService(responseListener, errorListener);
    }

    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener        Pharmacy error response listener
     *  @param responseListener     Pharmacy detail Success response listener
     */
    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
            PharmacyService services = new PharmacyService(getActivity(), null);
            services.doMyPharmacyRequest("", "", responseListener, errorListener);
    }

    /**
     * This function handles webservice response and parsing the contents.
     * Once parsing operation done, then it will update UI
     * bundletoSend is stand for to send bundle of datas received from webservice to next page.
     */
    public void initializeMapView() {
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));
        map = mapView.getMap();
        if (map != null) {
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    marker.hideInfoWindow();
                    return true;
                }
            });
            map.setInfoWindowAdapter(null);
            map.getUiSettings().setScrollGesturesEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */
    private void handleSuccessResponse(JSONObject response) {
        try {
//            progressBar.setVisibility(View.GONE);
            if(response.has("pharmacy")) {
                final JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
                addressline1.setText(pharmacyDatas.getString("store_name") + " " +
                        ((pharmacyDatas.getString("distance") != null && !pharmacyDatas.getString("distance").isEmpty()) ?
                                pharmacyDatas.getString("distance").replace(" miles", "mi") : ""));
                addressline2.setText(pharmacyDatas.getString("address1"));
                addressline3.setText(pharmacyDatas.getString("city") + ", "
                        + pharmacyDatas.getString("state") + " "
                        + (TextUtils.isEmpty(pharmacyDatas.getString("zipcode")) ? "" : MdliveUtils.zipCodeFormat(pharmacyDatas.getString("zipcode"))));
                bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
                JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
                bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
                bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
                bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
                bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
                bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
                bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
                if (pharmacyDatas.has("phone")) {
                    ((TextView) getView().findViewById(R.id.txt_my_pharmacy_addressline_four)).setText(MdliveUtils.formatDualString(pharmacyDatas.getString("phone")));
                    getView().findViewById(R.id.txt_my_pharmacy_addressline_four).setClickable(true);
                    getView().findViewById(R.id.txt_my_pharmacy_addressline_four).setOnClickListener(new View.OnClickListener() {
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
                    map.addMarker(new MarkerOptions().position(markerPoint)
                            .title("Marker"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
                }
            } else {
                Class clazz = Class.forName(getString(R.string.mdl_mdlive_myhealth_module));
                Method method = clazz.getMethod("getViewPager");
                if(isVisibleToUser && parentActivity.getClass().isAssignableFrom(clazz) && (((ViewPager)method.invoke(clazz.cast(parentActivity))).getCurrentItem() == 1)){
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(parentActivity);
                    if(userBasicInfo.getNotifications().getPharmacyDetails() == null){
                        if(locationService!=null && locationService.checkLocationServiceSettingsEnabled(getActivity())){
                            showProgressDialog();
                            parentActivity.registerReceiver(locationReceiver, intentFilter);
                            locationService.setBroadCastData(getClass().getSimpleName());
                            locationService.startTrackingLocation(getActivity());
                        } else {
                            Intent pharmacyintent = new Intent(parentActivity, MDLivePharmacyChange.class);
                            pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                            pharmacyintent.putExtra("PHARMACY_SELECTED",false);
                            startActivity(pharmacyintent);
                        }
                    }else {
                        if(!isLoading){
                            isLoading = true;
                            getUserPharmacyDetails();
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e){
            /*Toast.makeText(getActivity(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    getString(R.string.mdl_mdlive_module_not_found),
                    Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoading = false;
    }

    /**
     * This function is used for parse and update UI for server response.
     *
     * @param response - Response received from server.
     */
    public void loadDatas(String response) {
        try {
            JSONObject jobj = new JSONObject(response);
            JSONObject pharmacyDatas = jobj.getJSONObject("pharmacy");
            addressline1.setText(pharmacyDatas.getString("store_name") + " " +
                    ((pharmacyDatas.getString("distance") != null && !pharmacyDatas.getString("distance").isEmpty()) ?
                            pharmacyDatas.getString("distance").replace(" miles", "mi") : ""));
            addressline2.setText(pharmacyDatas.getString("address1"));
            addressline3.setText(pharmacyDatas.getString("city") + ", "
                    + pharmacyDatas.getString("state") + " "
                    + MdliveUtils.zipCodeFormat(pharmacyDatas.getString("zipcode")));
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
                map.addMarker(new MarkerOptions().position(markerPoint)
                        .title("Marker"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}