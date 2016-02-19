package com.mdlive.sav.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mdlive.sav.MDLiveProviderDetails;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

/**
 * Created by raja_rath on 10/6/2015.
 */
public class AffiliationAdapter extends BaseAdapter {
    private Context mContext;
    private JsonArray mArray;

    public AffiliationAdapter(Context cxt,JsonArray responseArray){
        mContext=cxt;
        mArray=responseArray;
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.mdlive_affliation_details, null);
            TextView txtProviderName = (TextView) grid.findViewById(R.id.providerName);
            final NetworkImageView providerImage = (NetworkImageView)grid.findViewById(R.id.providerImg);
            setValues(providerImage, txtProviderName, position);

        } else {
            grid = convertView;
        }

        return grid;
    }

    public void setValues(final NetworkImageView providerImage,TextView txtProviderName,int position){
        try {
            final JsonObject providerItem=mArray.get(position).getAsJsonObject();
            txtProviderName.setText(providerItem.get("name").getAsString());

            providerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDoctorId(providerItem.get("provider_id").getAsString());
                    Intent Reasonintent = new Intent(mContext, MDLiveProviderDetails.class);
                    mContext.startActivity(Reasonintent);


                }
            });


            ImageRequest request = new ImageRequest(providerItem.get("provider_image_url").getAsString(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            providerImage.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            providerImage.setImageResource(R.drawable.doctor_icon);
                        }
                    });
            ApplicationController.getInstance().getRequestQueue(mContext).add(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveDoctorId(String DocorId)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES,DocorId);
        editor.commit();
    }


}
