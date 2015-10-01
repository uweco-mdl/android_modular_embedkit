package com.mdlive.embedkit.uilayer.sav;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailsAffiliationServices;

import org.json.JSONObject;

import static com.android.volley.Response.*;

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

        ((ImageView) findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MDLiveProviderGroupDetailsAffiliations.this, "Hellow12", Toast.LENGTH_SHORT).show();
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
                Log.d("onResponse loadProviderGroupDetailsAffiliation ", response.toString());
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
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
                        ((NetworkImageView) findViewById(R.id.LinkImg1)).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg1)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg1)));
                        break;
                    case 1:
                        ((NetworkImageView) findViewById(R.id.LinkImg2)).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg2)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg2)));
                        break;
                    case 2:
                        ((NetworkImageView) findViewById(R.id.LinkImg3)).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg3)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg3)));
                        break;
                    case 3:
                        ((NetworkImageView) findViewById(R.id.LinkImg4)).setVisibility(View.VISIBLE);
                        ((NetworkImageView) findViewById(R.id.LinkImg4)).setImageUrl(linkItem.get("logo_url").getAsString(),
                                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
                        applyActionListner(linkItem, ((NetworkImageView) findViewById(R.id.LinkImg4)));
                        break;
                }
            }

            JsonArray providerArray = responObj.getAsJsonArray("providers_list");

            LinearLayout providerScrollView = (LinearLayout) findViewById(R.id.providerScrollView);

            for(int i = 0; i<providerArray.size(); i++){
                JsonObject providerItem = providerArray.get(i).getAsJsonObject();
                final View view = View.inflate(this, R.layout.mdlive_affliation_details, null);
                // Retrieves an image specified by the URL, displays it in the UI.
                ImageRequest request = new ImageRequest(providerItem.get("provider_image_url").getAsString(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                ((NetworkImageView) view.findViewById(R.id.providerImg)).setImageBitmap(bitmap);
                            }
                        }, 0, 0, null,
                        new ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                ((NetworkImageView) view.findViewById(R.id.providerImg)).setImageResource(R.drawable.doctor_icon);
                            }
                        });
                ApplicationController.getInstance().getRequestQueue(this).add(request);
               ((TextView) view.findViewById(R.id.providerName)).setText(providerItem.get("name").getAsString());
                providerScrollView.addView(view);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        hideProgress();
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
