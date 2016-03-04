package com.mdlive.sav.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.sav.MDLiveChooseProvider;
import com.mdlive.sav.MDLiveDoctorOnCall;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to get the provider count and the provider list.
 * The view for the provider list has been set over here.
 */
public class ChooseProviderAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();
    Context context;
    LayoutInflater inflate;
    private boolean isCignaCoachUser = false;

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
     *     in the listview.If the header response is true the doctor on call will be displayed
     *     and if the header response returns false then the doctor on call will be false.
     *
     */

    @Override
    public View getView(int pos, View convertview, ViewGroup parent) {
        TextView PatientNameTxt, SpecialistTxt, group_affiliations;
        TextView withPatientTxt;
        ImageButton video_call_icon;
        final CircularNetworkImageView ProfileImg;
        View row = null;
        if (array.get(pos).get("isheader").equals(StringConstants.ISHEADER_TRUE)) {
            if (row == null)

                inflate = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.mdlive_chooseproviderheader, parent, false);

            // remove adapter text title if user is using Cigna Health Coach
            final SharedPreferences sharedpreferences = this.context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String providerMode = sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "");

            if(providerMode != null && providerMode.length() > 0 && providerMode.equalsIgnoreCase(MDLiveConfig.PROVIDERTYPE_CIGNACOACH)){
                isCignaCoachUser = true;
                row.findViewById(R.id.txtFilter).setVisibility(View.GONE);
                row.findViewById(R.id.docOnCalLinLay2).setVisibility(View.GONE);
                row.findViewById(R.id.chatCoachHeaderTxt).setVisibility(View.VISIBLE);
            }

            Button seeFirstAvailDoctor= (Button)row.findViewById(R.id.btn_see_first_available_doctor);

            seeFirstAvailDoctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MDLiveChooseProvider.isDoctorOnCall=MDLiveChooseProvider.mDoctorOnCall;
                    MDLiveChooseProvider.isDoctorOnVideo=MDLiveChooseProvider.mDoctorOnVideo;
                    Intent seeFirstAvailableDocIntent=new Intent(context,MDLiveDoctorOnCall.class);
                    context.startActivity(seeFirstAvailableDocIntent);
                }
            });
        } else {
            if (row == null)
                inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.chooseprovider_listitem, parent, false);
            PatientNameTxt = (TextView) row.findViewById(R.id.PatientName);

            PatientNameTxt.setText(array.get(pos).get("name"));
            group_affiliations = (TextView) row.findViewById(R.id.group_affiliations);
            group_affiliations.setText(array.get(pos).get("group_name"));
            SpecialistTxt = (TextView) row.findViewById(R.id.specalist);
            SpecialistTxt.setText(array.get(pos).get("specialty"));
            ProfileImg = (CircularNetworkImageView) row.findViewById(R.id.ProfileImglist);
            ProfileImg.setImageUrl(array.get(pos).get("provider_image_url"), ApplicationController.getInstance().getImageLoader(context));

            if (array.get(pos).get("available_now_status").equals("true")) {

                if(array.get(pos).get("availability_type").equalsIgnoreCase("with patient")) {
                    ((TextView) row.findViewById(R.id.specialist)).setText(array.get(pos).get("availability_type"));
                    ((TextView) row.findViewById(R.id.specialist)).setTextColor(context.getResources().getColor(R.color.choose_pro_orange_color));
                }else if(array.get(pos).get("availability_type").equalsIgnoreCase("Available"))
                {
                    ((TextView) row.findViewById(R.id.specialist)).setText(array.get(pos).get("availability_type"));
                    ((TextView) row.findViewById(R.id.specialist)).setTextColor(context.getResources().getColor(R.color.choose_pro_green_color));
                }else
                {
                    ((TextView) row.findViewById(R.id.specialist)).setText(array.get(pos).get("availability_type"));
                }

                //image view
                video_call_icon = (ImageButton)row.findViewById(R.id.video_call_icon);
                if (array.get(pos).get("availability_type").equalsIgnoreCase(StringConstants.WITH_PATIENT)) {
                    video_call_icon.setBackgroundResource(R.drawable.clock_icon);
                    ((TextView) row.findViewById(R.id.specialist)).setText("With Patient...");
                }
                else if(array.get(pos).get("availability_type").equalsIgnoreCase("phone"))
                {
                    video_call_icon.setBackgroundResource(R.drawable.phone_call_icon);
                    ((TextView) row.findViewById(R.id.specialist)).setText("Available now by phone");
                }else if(array.get(pos).get("availability_type").equalsIgnoreCase("video"))
                {
                    ((TextView) row.findViewById(R.id.specialist)).setText("Available now by video");
                    video_call_icon.setBackgroundResource(R.drawable.video_call_icon);
                }else if(array.get(pos).get("availability_type").equalsIgnoreCase("video or phone"))
                {
                    ((TextView) row.findViewById(R.id.specialist)).setText("Available now by video or phone");
                    video_call_icon.setBackgroundResource(R.drawable.video_call_icon);
                }

            } else {

                if(array.get(pos).get("next_availability") == null)
                {
                    ((TextView) row.findViewById(R.id.specialist)).setText("");
                }else {
                    ((TextView) row.findViewById(R.id.specialist)).setText(array.get(pos).get("next_availability"));
                    ((TextView) row.findViewById(R.id.specialist)).setTextColor(context.getResources().getColor(R.color.choose_pro_gray_color));
                }
            }

            if(isCignaCoachUser && (SpecialistTxt.getText()==null || SpecialistTxt.getText().toString().trim().isEmpty()))
            {
                SpecialistTxt.setText(R.string.mdl_health_coach);
            }

             /*
                This is to Check the availability of the Doctor. If the next availability of doctor
                is available then the time stamp should be  visible else it should be hidden.
                This is to Check the availability of the Doctor is through either by phone or video
                if it is through phone calling icon should be visible or if it is either through
                video then the video icon should be visible .
              */

            }

        return row;
    }






}
