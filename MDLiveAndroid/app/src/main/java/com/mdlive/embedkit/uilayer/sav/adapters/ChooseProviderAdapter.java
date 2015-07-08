package com.mdlive.embedkit.uilayer.sav.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.sav.CircularNetworkImageView;
import com.mdlive.embedkit.uilayer.sav.MDLiveReasonForVisit;
import com.mdlive.embedkit.uilayer.sav.MDLiveSearchProvider;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sudha_s on 5/15/2015.
 */
public class ChooseProviderAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();
    Context context;
    LayoutInflater inflate;
    LinearLayout DocOnCalLinLay;

    public ChooseProviderAdapter(Context applicationContext,
                                 ArrayList<HashMap<String, String>> arraylist) {

        this.context = applicationContext;
        this.array = arraylist;

    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int arg0) {
        return array.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    /**
     *     The getView Method displays the provider list items.
     *     The datas are fetched from the Arraylist based on the position the dates will be placed
     *     in the listview.
     *
     *
     */

    @Override
    public View getView(int pos, View convertview, ViewGroup parent) {
        TextView PatientNmaeTxt,SPecialistTxt,DateTxt;
        ImageView callImg;
        final CircularNetworkImageView ProfileImg;
        View row = null;
        if(array.get(pos).get("isheader").equals("1"))
        {
            if(row==null)

            inflate = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.mdlive_chooseproviderheader, parent,false);
            ((TextView)row.findViewById(R.id.filterTxt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(context, MDLiveSearchProvider.class);
                    ((Activity)context).startActivityForResult(intent,1);
                    Utils.hideSoftKeyboard(((Activity)context));
                }
            });
            ((Button)row.findViewById(R.id.seenextAvailableBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(context, MDLiveReasonForVisit.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                    Utils.hideSoftKeyboard(((Activity)context));
                }
            });
        }
        else {
            if(row==null)
            inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.mdlive_chooseprovider_baseadapter, parent,false);
            PatientNmaeTxt = (TextView) row.findViewById(R.id.PatientName);
            PatientNmaeTxt.setText(array.get(pos).get("name"));
            SPecialistTxt = (TextView) row.findViewById(R.id.specalist);
            SPecialistTxt.setText(array.get(pos).get("speciality"));
            ProfileImg = (CircularNetworkImageView) row.findViewById(R.id.ProfileImglist);

            ProfileImg.setImageUrl(array.get(pos).get("provider_image_url"), ApplicationController.getInstance().getImageLoader(context));

             //    This is to Check the availability of the Doctor. If the next availability of doctor
             //   is available then the time stamp should be  visible else it should be hidden.
            DateTxt = (TextView) row.findViewById(R.id.Time);
            try {
//                if (array.get(pos).get("next_availability") == null || array.get(pos).get("next_availability").equals("0") || array.get(pos).get("next_availability").equalsIgnoreCase("null")) {
//                    DateTxt.setVisibility(View.GONE);
//                } else {
//                    DateTxt.setVisibility(View.VISIBLE);
//                    DateTxt.setText(array.get(pos).get("next_availability"));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

             //   This is to Check the availability of the Doctor is through either by phone or video
             //   if it is through phone calling icon should be visible or if it is either through
             //   video then the video icon should be visible .

            callImg = (ImageView) row.findViewById(R.id.callImg);
            if (array.get(pos).get("availability_type").equalsIgnoreCase(context.getResources().getString(R.string.video_or_phonr))) {
                callImg.setVisibility(View.GONE);
                callImg.setBackgroundResource(R.drawable.videoicon);
            }
            if (array.get(pos).get("availability_type").equalsIgnoreCase(context.getResources().getString(R.string.phone))) {
                callImg.setVisibility(View.GONE);
                callImg.setBackgroundResource(R.drawable.callicon);
            }
        }



        return row;
    }






}
