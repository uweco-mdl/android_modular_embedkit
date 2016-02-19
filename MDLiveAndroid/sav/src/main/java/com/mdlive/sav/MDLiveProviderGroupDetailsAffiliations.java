package com.mdlive.sav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.sav.adapters.AffiliationAdapter;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailsAffiliationServices;

import org.json.JSONObject;

/**
 * Created by raja_rath on 10/1/2015.
 */
public class MDLiveProviderGroupDetailsAffiliations extends MDLiveBaseActivity {
    private String GroupID;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_provider_group_details_affiliation);
        initialiseData();
        if(getIntent().hasExtra("affurl")){
            GroupID = getIntent().getStringExtra("affurl");
        }
        // Service Call
        loadProviderGroupDetailsAffiliation();
    }

    private void initialiseData() {
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */

        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveProviderGroupDetailsAffiliations.this);
                onBackPressed();
            }
        });
    }

    /**
     * ProviderGroupAffiliationService
     * Class : ProviderGroupAffiliationService - Service class used to fetch the Provider's affiliation information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to
     * the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error
     * message to user or Get started screen will shown to user).
     */
    private void loadProviderGroupDetailsAffiliation() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }};
        ProviderDetailsAffiliationServices services = new ProviderDetailsAffiliationServices(MDLiveProviderGroupDetailsAffiliations.this, null);
        services.getProviderDetailsAffiliation(GroupID, successCallBackListener, errorListener);
    }

    /**
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            /*
            JsonObject groupName = responObj.get("group_name").getAsJsonObject();
            JsonArray providersList = responObj.get("providers_list").getAsJsonArray();
            JsonArray links = responObj.get("links").getAsJsonArray();
            */
            String groupMission = responObj.get("group_mission").getAsString();
            ((NetworkImageView) findViewById(R.id.affiliationLogo)).setImageUrl(responObj.get("logo").getAsString(),
                    ApplicationController.getInstance().getImageLoader(getApplicationContext()));
            ((TextView)findViewById(R.id.mission_txt)).setText(groupMission);

            JsonArray linksArray = responObj.getAsJsonArray("links");
            //LinkImg1, LinkImg2, LinkImg2, LinkImg4
            for(int i = 0; i<linksArray.size(); i++){
                final JsonObject linkItem = linksArray.get(i).getAsJsonObject();
                switch (i){
                    case 0:
                        findViewById(R.id.LinkImg1).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg1)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg1)));
                        break;
                    case 1:
                        findViewById(R.id.LinkImg2).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg2)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg2)));
                        break;
                    case 2:
                        findViewById(R.id.LinkImg3).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg3)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg3)));
                        break;
                    case 3:
                        findViewById(R.id.LinkImg4).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg4)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg4)));
                        break;
                }
            }

            JsonArray providerArray = responObj.getAsJsonArray("providers_list");

            AffiliationAdapter mAdapter=new AffiliationAdapter(MDLiveProviderGroupDetailsAffiliations.this,providerArray);
            GridView gridView= (GridView) findViewById(R.id.affliationView);
            gridView.setAdapter(mAdapter);
            setGridViewHeightBasedOnChildren(gridView,3);
            mAdapter.notifyDataSetChanged();

            LinearLayout providerVerticalScrollView = (LinearLayout) findViewById(R.id.providerVertical);

        }catch (Exception e) {
            e.printStackTrace();
        }

        hideProgress();
    }


    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight+50;
        gridView.setLayoutParams(params);

    }



    public LinearLayout createLayout(){
        LinearLayout linearItemLayout = new LinearLayout(this);
        linearItemLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearItemLayout.setLayoutParams(LLParams);
        return linearItemLayout;
    }



    //Saving Doctor details in shared Pref

    public void saveDoctorId(String DocorId)
    {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES,DocorId);
        editor.commit();
    }

    private void applyActionListner(final JsonObject linkItem, NetworkImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!linkItem.get("url").isJsonNull() && linkItem.get("url").getAsString().length() != 0){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkItem.get("url").getAsString()));
                    startActivity(i);
                }
            }
        });
    }

    /**
     * This method will close the activity with transition effect.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveProviderGroupDetailsAffiliations.this);
    }
}
