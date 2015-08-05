package com.mdlive.embedkit.uilayer.pharmacy.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacyDetails;
import com.mdlive.embedkit.uilayer.pharmacy.adapter.PharmacyListAdaper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.ResultPharmacyService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The wrapper class for List tab Fragment. This layout have the details of  list tab.
 * While click on particular item from list will redirect to MDLBTPharmacy_Details page
 */
public class MDLivePharmacyResultTabListFragment extends Fragment {

    private ListView pharmList;
    private PharmacyListAdaper adaper;
    private ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
    private View mRootView;
    private ProgressDialog pDialog;
    private Bundle bundleToSend = new Bundle();


    /**
     * onCreateView is called first in fragment lifecycle
     * View required for this fragment will be have to be inflated and returned from here.
     * At same time views initialization have to be done here. It is done by the help of initializeViews function.
     *
     * @param inflater - It is OS inflater to inflate layouts
     * @param container - It is group container which holds views
     * @param savedInstanceState - saved bundle instance of fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.mdlive_pharmacy_search_tablist, container, false);
        initializeViews();
        return mRootView;
    }

    /**
     * onActivityCrated is called next to onCreateView
     *
     * getPharmacySearchResults function is called over here to get results of pharmacies.
     *
     * @param savedInstanceState - savedInstanceState is a bundle of fragment
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPharmacySearchResults(getActivity().getIntent().getStringExtra("post_body"));
    }

  /*
  * This function is mainly focused on initializing view in layout.
  */

    public void initializeViews(){

        pharmList = (ListView) mRootView.findViewById(R.id.pharmList);

        adaper = new PharmacyListAdaper(getActivity(), list);

        pharmList.setAdapter(adaper);

        pharmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity().getApplicationContext(), MDLivePharmacyDetails.class);

                bundleToSend.putInt("pharmacy_id",(int) list.get(position).get("pharmacy_id"));
                bundleToSend.putDouble("longitude",(Double) list.get(position).get("longitude"));
                bundleToSend.putDouble("latitude",(Double) list.get(position).get("latitude"));
                bundleToSend.putBoolean("twenty_four_hours", (Boolean) list.get(position).get("twenty_four_hours"));
                bundleToSend.putBoolean("active", (Boolean) list.get(position).get("active"));
                bundleToSend.putString("store_name",(String) list.get(position).get("store_name"));
                bundleToSend.putString("phone",(String) list.get(position).get("phone"));
                bundleToSend.putString("address1",(String) list.get(position).get("address1"));
                bundleToSend.putString("address2",(String) list.get(position).get("address2"));
                bundleToSend.putString("zipcode",(String) list.get(position).get("zipcode"));
                bundleToSend.putString("fax",(String) list.get(position).get("fax"));
                bundleToSend.putString("city",(String) list.get(position).get("city"));
                bundleToSend.putString("distance",(String) list.get(position).get("distance"));
                bundleToSend.putString("state",(String) list.get(position).get("state"));

                i.putExtra("datas", bundleToSend);

                startActivity(i);
            }
        });
        pDialog = MdliveUtils.getProgressDialog("Please Wait...", getActivity());
    }

    /**
     * This function is used to get pharmacy results.
     * ResultPharmacyService is used to get pharmacy result from webservice
     */

    public void getPharmacySearchResults(String postBody){
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
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        ResultPharmacyService services = new ResultPharmacyService(getActivity(), null);
        services.doPharmacyLocationRequest(postBody, responseListener, errorListener);
    }

    /**
     *
     *  This function is used to handle response which was thrown from getPharmacySearchResults function
     *
     *  Parsing json content and updating UI works done here.
     *
     *  @param response - response is catched response from getPharmacySearchResults network response
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
                map.put("pharmacy_id",pharmacy_id);
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

                list.add(map);
            }

            adaper.notifyDataSetChanged();

        }catch(Exception e){
            e.printStackTrace();
        }

    }


}