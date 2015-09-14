package com.mdlive.embedkit.uilayer.pharmacy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class MDLivePharmacyFragment extends MDLiveBaseFragment {
    private TextView addressline1, addressline2, addressline3;
    private SupportMapFragment mapView;
//    private RelativeLayout progressBar;
    private GoogleMap map;
    private Bundle bundletoSend = new Bundle();
    private IntentFilter intentFilter;
    private static View view;
    private Activity parentActivity;
    private LocationCooridnates locationService;
    private boolean isVisibleToUser = false;
    public static MDLivePharmacyFragment newInstance() {
        final MDLivePharmacyFragment pharmacyFragment = new MDLivePharmacyFragment();
        return pharmacyFragment;
    }

    public MDLivePharmacyFragment() {
        super();
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
        getUserPharmacyDetails();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationService = new LocationCooridnates(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        initializeMapView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserPharmacyDetails();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
            getUserPharmacyDetails();
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
                MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
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
            PharmacyService services = new PharmacyService(getActivity(), null);
            services.doMyPharmacyRequest("", "", responseListener, errorListener);
    }


    /* This function is used to initialize map view for MDLivePharmacy activity */
    public void initializeMapView() {
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        mapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));
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
                    View v = getActivity().getLayoutInflater().inflate(R.layout.mdlive_pharm_custom_mapinfowindow_view, null);
                    TextView addressline1 = (TextView) v.findViewById(R.id.addressText1);
                    TextView addressline2 = (TextView) v.findViewById(R.id.addressText2);
                    TextView addressline3 = (TextView) v.findViewById(R.id.addressText3);
                    addressline1.setText(bundletoSend.get("store_name") + "");
                    addressline2.setText(bundletoSend.get("address1") + "");
                    addressline3.setText(bundletoSend.get("city") + "  " + (TextUtils.isEmpty(bundletoSend.getString("zipcode")) ? "" : MdliveUtils.zipCodeFormat(bundletoSend.get("zipcode").toString())));
                    return v;
                }

                @Override
                public View getInfoContents(Marker arg0) {
                    return null;
                }
            });

            map.getUiSettings().setScrollGesturesEnabled(false);
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
                JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
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
                    Marker marker = map.addMarker(new MarkerOptions().position(markerPoint)
                            .title("Marker"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
                }
            } else {
                if(isVisibleToUser && parentActivity instanceof MedicalHistoryActivity && ((MedicalHistoryActivity)parentActivity).getViewPager().getCurrentItem() == 1){
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
                    if(userBasicInfo.getNotifications().getPharmacyDetails() == null){
                        if(locationService!=null && locationService.checkLocationServiceSettingsEnabled(getActivity())){
                            showProgressDialog();
                            getActivity().registerReceiver(locationReceiver, intentFilter);
                            locationService.setBroadCastData(getClass().getSimpleName());
                            locationService.startTrackingLocation(getActivity());
                        } else {
                            Intent pharmacyintent = new Intent(parentActivity, MDLivePharmacyChange.class);
                            pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                            startActivity(pharmacyintent);
                        }
                    }else {
                        getUserPharmacyDetails();
                    }
                }
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
                Marker marker = map.addMarker(new MarkerOptions().position(markerPoint)
                        .title("Marker"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
                double lat=intent.getDoubleExtra("Latitude",0d);
                double lon=intent.getDoubleExtra("Longitude",0d);
                if(lat!=0 && lon!=0){
                    Intent i = new Intent(getActivity(), MDLivePharmacyResult.class);
                    i.putExtra("longitude", lat + "");
                    i.putExtra("latitude", lon + "");
                    i.putExtra("FROM_MY_HEALTH", true);
                    i.putExtra("errorMesssage", "No Pharmacies listed in your location");
                    startActivity(i);
                    MdliveUtils.startActivityAnimation(getActivity());
                } else{
                    Intent pharmacyintent = new Intent(parentActivity,MDLivePharmacyChange.class);
                    pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                    startActivity(pharmacyintent);
                }
            } else {
                Intent pharmacyintent = new Intent(parentActivity,MDLivePharmacyChange.class);
                pharmacyintent.putExtra("FROM_MY_HEALTH", true);
                startActivity(pharmacyintent);
            }
        }
    };
}