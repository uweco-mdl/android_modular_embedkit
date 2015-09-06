package com.mdlive.embedkit.uilayer.myhealth;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.sav.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;

import org.json.JSONObject;

/**
 * Created by unnikrishnan_b on 8/22/2015.
 */
public class ProviderDetailsFragment extends MDLiveBaseFragment {
    private static final String PROVIDER_TAG = "provider";

    private TextView aboutme_txt,education_txt,specialities_txt, hospitalAffilations_txt,location_txt,lang_txt, doctorNameTv;
    private LinearLayout aboutmeLl, educationLl, boardCertificationsLl,providerImageHolder,hosaffiliationsLl, languagesLl, specialitiesLl;
    private CircularNetworkImageView ProfileImg;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MDLiveMyHealthProvidersFragment.
     */

    public static ProviderDetailsFragment newInstance(String providerId) {
        Bundle args = new Bundle();
        args.putString(PROVIDER_TAG, providerId);

        ProviderDetailsFragment fragment = new ProviderDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProviderDetailsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.provider_details_fragment, null, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.detailsGroupAffiliations).setVisibility(View.GONE);
        view.findViewById(R.id.dateTxt).setVisibility(View.GONE);
        view.findViewById(R.id.AvailableByLl).setVisibility(View.GONE);
        view.findViewById(R.id.panelMsgLl).setVisibility(View.GONE);
        view.findViewById(R.id.tapBtn).setVisibility(View.GONE);
        view.findViewById(R.id.reqfutureapptBtn).setVisibility(View.GONE);
        view.findViewById(R.id.providerDetailsFooterLl).setVisibility(View.GONE);
        view.findViewById(R.id.dash_board__left_container).setVisibility(View.GONE);
        view.findViewById(R.id.dash_board__right_container).setVisibility(View.GONE);
        educationLl= (LinearLayout)view.findViewById(R.id.educationLl);
        providerImageHolder = (LinearLayout) view.findViewById(R.id.providerImageHolder);
        boardCertificationsLl = (LinearLayout)view.findViewById(R.id.boardCertificationsLl);
        hosaffiliationsLl = (LinearLayout)view.findViewById(R.id.hosaffiliationsLl);
        languagesLl = (LinearLayout)view.findViewById(R.id.languagesLl);
        specialitiesLl = (LinearLayout)view.findViewById(R.id.specialitiesLl);


        aboutmeLl = (LinearLayout)view.findViewById(R.id.aboutmeLl);
        aboutme_txt = (TextView)view.findViewById(R.id.aboutMe_txt);
        education_txt = (TextView)view.findViewById(R.id.education_txt);
        specialities_txt = (TextView)view.findViewById(R.id.specialities_txt);
        hospitalAffilations_txt = (TextView)view.findViewById(R.id.license_txt);
        location_txt = (TextView)view.findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)view.findViewById(R.id.provider_lang_txt);
        doctorNameTv = (TextView)view.findViewById(R.id.DoctorName);
        ProfileImg = (CircularNetworkImageView)view.findViewById(R.id.ProfileImg1);


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getProviderDetails();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getProviderDetails() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    Log.e("Response pdetails", response.toString());
                    JsonParser parser = new JsonParser();
                    JsonObject responObj = (JsonObject) parser.parse(response.toString());
                    JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();
                    JsonObject providerdetObj = profileobj.get("provider_details").getAsJsonObject();
                    String str_DoctorName ="";
                    if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "name")) {
                        str_DoctorName = providerdetObj.get("name").getAsString();
                    }
                    String str_BoardCertifications="";
                    if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "board_certifications")) {
                        str_BoardCertifications = providerdetObj.get("board_certifications").getAsString();
                    }
                    String str_AboutMe="";
                    if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "about_me"))
                    {
                        str_AboutMe = providerdetObj.get("about_me").getAsString();
                    }
                    String str_ProfileImg="";
                    if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "provider_image_url")) {
                        str_ProfileImg = providerdetObj.get("provider_image_url").getAsString();
                    }

                    String str_education="";
                    if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "education")) {
                        str_education = providerdetObj.get("education").getAsString();
                    }

                    ProfileImg.setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(getActivity()));
                    ProfileImg.setDefaultImageResId(R.drawable.doctor_icon);
                    ProfileImg.setErrorImageResId(R.drawable.doctor_icon);
                    doctorNameTv.setText(str_DoctorName);

                    if(str_AboutMe.length()!= IntegerConstants.NUMBER_ZERO)
                    {
                        aboutme_txt.setText(str_AboutMe);
                    }else
                    {
                        aboutmeLl.setVisibility(View.GONE);
                    }
                    if(!str_education.equals("") && !str_education.isEmpty()||str_education.length()!=0)
                    {
                        education_txt.setText(str_education);
                    }else
                    {
                        education_txt.setVisibility(View.GONE);
                        educationLl.setVisibility(View.GONE);
                    }

                    if(!str_BoardCertifications.equals("")||str_BoardCertifications == null && !str_BoardCertifications.isEmpty()||str_BoardCertifications.length()!=0)
                    {
                        location_txt.setText(str_BoardCertifications);
                    }else
                    {
                        location_txt.setVisibility(View.GONE);
                        boardCertificationsLl.setVisibility(View.GONE);
                    }
                    String license_state = "";
                    //License Array
                    getLicenseArrayResponse(providerdetObj, license_state);

                    //Language Array
                    getLanguageArrayResponse(providerdetObj);

                    //Specialities Array
                    getSpecialitiesArrayResponse(providerdetObj);

                    //Provider Image Array
                    getProviderImageArrayResponse(providerdetObj);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };
        final ProviderDetailServices providerDetails = new ProviderDetailServices(getActivity(), getProgressDialog());
        Log.d("Hello", "Provider Id :" + getArguments().getString(PROVIDER_TAG));
        providerDetails.getProviderDetails(getArguments().getString(PROVIDER_TAG),successListener, errorListener);
    }

    private void getLicenseArrayResponse(JsonObject providerdetObj, String license_state) {
        JsonArray responArray = providerdetObj.get("provider_affiliations").getAsJsonArray();
        String hospitalAffilations = "";
        for(int i=0;i<responArray.size();i++)
        {
//            JsonObject licenseObject = responArray.get(i).getAsJsonObject();
//            license_state +=licenseObject.get("state").getAsString()+"\n";

            hospitalAffilations+= responArray.get(i).toString().substring(1,responArray.get(i).toString().length()-1)+"\n";
            if(!hospitalAffilations.equals("")|| !hospitalAffilations.isEmpty()||hospitalAffilations.length()!=0)
            {
                hospitalAffilations_txt.setText(hospitalAffilations);
            }else
            {
                hospitalAffilations_txt.setVisibility(View.GONE);
                hosaffiliationsLl.setVisibility(View.GONE);
            }

        }
    }

    /**
     *  Response Handler for getting the languages , what the provider speaks.
     *  This method returns what the Provider speaks .The speaks will be populated in the arraylist
     *  and it will be loaded in the TextView.
     *
     */

    private void getLanguageArrayResponse(JsonObject providerdetObj) {
        String lang = "";
        JsonArray langArray = providerdetObj.get("Language").getAsJsonArray();
        for(int i=0;i<langArray.size();i++)
        {
            lang+="\u2022"+" "+langArray.get(i).toString().substring(1,langArray.get(i).toString().length()-1)+"\n";
            if(!lang.equals("")&& !lang.isEmpty()||lang.length()!=IntegerConstants.NUMBER_ZERO)
            {
                lang_txt.setText(lang);
            }else
            {
                lang_txt.setVisibility(View.GONE);
                languagesLl.setVisibility(View.GONE);
            }

        }
    }

    /**
     *  Successful Response Handler for getting the Affillitations and the provider image.
     *  This method will give the successful response of the Provider's affilitations.
     *  This response is for the Affilations Purpose.The image can be placed one below the other
     *
     */
    private void getProviderImageArrayResponse(JsonObject providerdetObj) {
        String ProviderImage = "";
        JsonArray ProviderImageArray = providerdetObj.get("provider_groups").getAsJsonArray();
        Log.e("Size", ProviderImageArray.size() + "");
//        providerImageHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        for(int i=0;i<ProviderImageArray.size();i++)
        {
            NetworkImageView imageView = new NetworkImageView(getActivity().getApplicationContext());
            imageView.setImageUrl(ProviderImageArray.get(i).getAsJsonObject().get("logo").getAsString(),
                    ApplicationController.getInstance().getImageLoader(getActivity().getApplicationContext()));
            imageView.setLayoutParams(params);
            providerImageHolder.addView(imageView);

        }
    }
    /**
     *  Response Handler for getting the Speciality and this is completely depend upon the provider type.
     *  Here the Provider type can either be family physician or Pediatrician.so based on this type
     *  the Speciality data will be populated .
     *
     */

    private void getSpecialitiesArrayResponse(JsonObject providerdetObj) {
        String specialities = "";
        JsonArray specialityArray = providerdetObj.get("speciality_qualifications").getAsJsonArray();
        for(int i=0;i<specialityArray.size();i++)
        {
            specialities+= "\u2022"+" "+specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
            if(!specialities.equals("")||specialities.length()!=0)
            {
                specialities_txt.setText(specialities);
            }else
            {
                specialities_txt.setVisibility(View.GONE);
                specialitiesLl.setVisibility(View.GONE);
            }

        }
    }

}