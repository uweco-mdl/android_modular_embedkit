package com.mdlive.embedkit.uilayer.pharmacy.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacyDetails;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.ResultPharmacyService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The wrapper class for map tab Fragment. This layout have the details of map with pins.
 * While click on particular marker from map will redirect to MDLBTPharmacy_Details page
 */

public class MDLivePharmacyResultTabMapFragment extends Fragment {

    private SupportMapFragment mapView;
    private GoogleMap googleMap;
    private ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
    private HashMap<Marker, Integer> markerIdCollection = new HashMap<Marker, Integer>();

    private View mRootView;
    private ProgressDialog pDialog;
    private Bundle bundleToSend = new Bundle();

    /**
     * onCreateView is called first in fragment lifecycle
     * View required for this fragment will be have to be inflated and returned from here.
     *
     * @param inflater - It is OS inflater to inflate layouts
     * @param container - It is group container which holds views
     * @param savedInstanceState - saved bundle instance of fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.mdlive_pharmacy_search_mapview, container, false);
        return mRootView;
    }


    /**
     * onActivityCrated is called next to onCreateView
     *
     * mapView initialization is done here
     *
     * getPharmacySearchResults function is called over here to get results of pharmacies.
     *
     * @param savedInstanceState - savedInstanceState is a bundle of fragment
     */


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeMapViews();
        Log.e("Received Post Body", getActivity().getIntent().getStringExtra("post_body"));
        getPharmacySearchResults(getActivity().getIntent().getStringExtra("post_body"));

    }


    /**
     *  getting Google map from mapView instance and initializing googleMap work done here.
     *
     *  Google map adapter is set to google map for handling markers.
     *
     *  While click on marker info window it will redirect MDLBTPharmacy_Details page with details.
     *
     */


    public void initializeMapViews(){

        FragmentManager fm = getChildFragmentManager();
        //mapView = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapView == null) {
            mapView = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapView).commit();
        }

    }

    /**
     *  This function is used to get pharmacy search results from webservice.
     *
     *  ResultPharmacyService is used to get results from webservice.
     *
     *  handleSuccessResponse is used to handle success response
     *
     *  @param postBody - post body required for make a request to webserver.
     *
     */



    public void getPharmacySearchResults(String postBody){

        pDialog = MdliveUtils.getProgressDialog("Please Wait...", getActivity());

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
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        pDialog.show();

        ResultPharmacyService services = new ResultPharmacyService(getActivity(), null);
        services.doPharmacyLocationRequest(postBody, responseListener, errorListener);
    }

    /**
     * This function is used to handle the response from webservice
     *
     * @param response - It is catched from getPharmacySearchResults function network listener.
     */

    private void handleSuccessResponse(JSONObject response) {

        try {
            pDialog.dismiss();

            Log.d("Response", response.toString());

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            String current_page =  responObj.get("current_page").getAsString();
            String total_pages =  responObj.get("total_pages").getAsString();
            String total_records =  responObj.get("total_records").getAsString();

            JsonArray responArray = responObj.get("pharmacies").getAsJsonArray();

            int pharmacy_id;
            double longitude, latitude;
            boolean twenty_four_hours, active;
            String store_name, phone, address1, address2, zipcode, fax, city, distance, state;

            list.clear();

            googleMap = mapView.getMap();

            if(googleMap != null)
                googleMap.clear();
            else
                Toast.makeText(getActivity(), "Google map null", Toast.LENGTH_SHORT).show();

            LatLng markerPoint = null;

            for(int i=0;i<responArray.size();i++) {

                state =  responArray.get(i).getAsJsonObject().get("state").getAsString();
                pharmacy_id =  responArray.get(i).getAsJsonObject().get("pharmacy_id").getAsInt();
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

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("state",state);
                map.put("id",pharmacy_id);
                map.put("store_name",store_name);
                map.put("phone",phone);
                map.put("address1",address1);
                map.put("active", active);
                map.put("address2",address2);
                map.put("zipcode",zipcode);
                map.put("fax",fax);
                map.put("longitude",longitude);
                map.put("latitude",latitude);
                map.put("city",city);
                map.put("twenty_four_hours",twenty_four_hours);
                map.put("distance",distance);
                map.put("active",active);


                if(googleMap != null){

                    markerPoint = new LatLng(latitude, longitude);

                    Marker marker = googleMap.addMarker(new MarkerOptions().position(
                                    markerPoint)
                                    .title(store_name)
                    );

                    markerIdCollection.put(marker, i);
                }

                list.add(map);

                }

            if(markerPoint != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));

        if(googleMap != null){

            googleMap.setInfoWindowAdapter(adapter);

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    Intent intent = new Intent(getActivity(),MDLivePharmacyDetails.class);

                    bundleToSend.putInt("pharmacy_id",(int) list.get(markerIdCollection.get(marker)).get("id"));
                    bundleToSend.putFloat("longitude",(Float) list.get(markerIdCollection.get(marker)).get("longitude"));
                    bundleToSend.putFloat("latitude",(Float) list.get(markerIdCollection.get(marker)).get("latitude"));
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

        catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     *  This adapter is used to display info window when users click on the marker on google map
     *
     *  It has a layout to show user when click on marker.
     */

    GoogleMap.InfoWindowAdapter adapter = new GoogleMap.InfoWindowAdapter() {

        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker arg0) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.mdlive_pharm_custom_mapinfowindow_view, null);

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



}