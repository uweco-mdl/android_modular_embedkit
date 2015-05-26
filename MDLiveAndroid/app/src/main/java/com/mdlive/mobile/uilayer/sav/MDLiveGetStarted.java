package com.mdlive.mobile.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.mobile.R;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.FamilyMembersList;
import com.mdlive.unifiedmiddleware.services.ProviderTypeList;
import com.mdlive.unifiedmiddleware.services.UserBasicInfoServices;

import org.json.JSONObject;

import java.util.ArrayList;

public class MDLiveGetStarted extends Activity implements View.OnClickListener{
    private Button SavContinueBtn;
    private ImageView imgView,patientNameimg,locationImg,providerImg;
    private ProgressDialog pDialog;
    private TextView D0ctorNmaeTxt, phoneNumberTxt, providerTypeTxt,SeenMyProviderEtxt,locationTxt;
    private ArrayList<String> PatientList = new ArrayList<String>();
    private  ArrayList<String> LocationList = new ArrayList<String>();
    private ArrayList<String> ProviderList = new ArrayList<String>();
    private ArrayList<String> VisitList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        pDialog = Utils.getProgressDialog("Loading...", this);

        Utils.hideSoftKeyboard(this);
        initialiseData();

      /*  Load Services*/
        loadUserInformationDetails();
        loadProviderType();
        loadFamilyMember();
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        String SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        locationTxt.setText(SavedLocation);
    }

    private void initialiseData() {
        SavContinueBtn = (Button) findViewById(R.id.SavContinueBtn);
        imgView = (ImageView) findViewById(R.id.ListImg);
        SeenMyProviderEtxt= (TextView) findViewById(R.id.Provider_editTxt);
        locationTxt= (TextView) findViewById(R.id.LocationTxtView);
        patientNameimg = (ImageView) findViewById(R.id.PatientNameImg);
        locationImg = (ImageView) findViewById(R.id.LocationImg);
        providerImg = (ImageView) findViewById(R.id.ProviderTypeImg);
        D0ctorNmaeTxt = (TextView)findViewById(R.id.PatientNameTv);
        providerTypeTxt = (TextView)findViewById(R.id.ProvidertypeTv);
        phoneNumberTxt = (TextView)findViewById(R.id.PhoneNumberTv);
        SavContinueBtn.setOnClickListener(this);
        Button yesBtn = (Button) findViewById(R.id.YesBtn);
        yesBtn.setOnClickListener(this);
        Button noBtn = (Button) findViewById(R.id.NoBtn);
        noBtn.setOnClickListener(this);
        imgView.setOnClickListener(this);
        patientNameimg.setOnClickListener(this);
        locationImg.setOnClickListener(this);
        providerImg.setOnClickListener(this);
    }

    /**
     *
     * Load user information Details.
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadUserInformationDetails() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest(successCallBackListener, errorListener);
    }
    /**
     *
     * Load Provider Type Details.
     * Class : ProviderTypeList - Service class used to fetch the Provider Type List information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */

    private void loadProviderType() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponseProviderList(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};



        ProviderTypeList provider_services = new ProviderTypeList(MDLiveGetStarted.this, null);
        provider_services.getProviderType(responseListener, errorListener);
    }
    /**
     *
     * Load Family Member Type Details.
     * Class : FamilyMembersList - Service class used to fetch the Family Member List information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadFamilyMember() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponseFamilyMember(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};



        FamilyMembersList family_services = new FamilyMembersList(MDLiveGetStarted.this, null);
        family_services.getFamilyMember(responseListener, errorListener);
    }


    /**
     *
     *  Successful Response Handler for Load Basic Info
     *
     */

    private void  handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.USER_PREFERENCES, response.toString());
            editor.commit();

            //Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
//            JsonArray responArray = responObj.get("personal_info").getAsJsonArray();
//            for(int i=0;i<responArray.size();i++) {
//                String StrPatientName =  responArray.get(i).getAsJsonObject().get("first_name").getAsString();
//                String StrPhoneNumber =  responArray.get(i).getAsJsonObject().get("phone").getAsString();
//                Log.d("patientName----->", StrPatientName);
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *
     *  Successful Response Handler for Provider List
     *
     */

    private void handleSuccessResponseProviderList(JSONObject response) {
        try {
            pDialog.dismiss();
            Log.d("Response", response.toString());

            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.USER_PREFERENCES, response.toString());
            editor.commit();

//Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            String StrProviderTypePediatrician =  responObj.get("provider_types").getAsJsonObject().get("2").getAsString();
            String StrProviderTypeFamily =  responObj.get("provider_types").getAsJsonObject().get("3").getAsString();
            ProviderList.add(StrProviderTypePediatrician);
            ProviderList.add(StrProviderTypeFamily);
            providerTypeTxt.setText(StrProviderTypePediatrician);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *
     * Successful Response Handler for Family Member
     *
     */
    private void handleSuccessResponseFamilyMember(JSONObject response) {
        try {
            pDialog.dismiss();
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.USER_PREFERENCES, response.toString());
            // Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            Log.d("Family Members ----->", responObj.toString());

            JsonArray conditionsSearch = responObj.get("dependant_users").getAsJsonArray();
            for(int i=0;i<conditionsSearch.size();i++) {
            String StrpatientName = conditionsSearch.get(i).getAsJsonObject().get("name").getAsString();
            PatientList.add(StrpatientName);
            D0ctorNmaeTxt.setText(conditionsSearch.get(0).getAsJsonObject().get("name").getAsString());
            }



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * Definition for Click Event Listeners.Based on the reference id corresponding
     * functions will be defined
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.SavContinueBtn:
           Intent intent = new Intent(MDLiveGetStarted.this, MDLiveChooseProvider.class);
           startActivity(intent);
                break;
            case R.id.YesBtn:
                changeButtonColor((Button) v, (Button) findViewById(R.id.NoBtn));
                break;
            case R.id.NoBtn:
                changeButtonColor((Button) v, (Button) findViewById(R.id.YesBtn));
                break;
            case R.id.ListImg:
                VisitList.clear();
                VisitList.add(LocalisationHelper.getLocalizedStringFromPrefs(this, "please_choose", R.string.please_choose));
                VisitList.add(LocalisationHelper.getLocalizedStringFromPrefs(this, "gone_to_emergency",R.string.gone_to_emergency));
                VisitList.add(LocalisationHelper.getLocalizedStringFromPrefs(this, "used_urgent_care",R.string.used_urgent_care));
                VisitList.add(LocalisationHelper.getLocalizedStringFromPrefs(this, "seen_my_provider",R.string.seen_my_provider));
                VisitList.add(LocalisationHelper.getLocalizedStringFromPrefs(this, "done_nothing",R.string.done_nothing));
                showListViewDialog(VisitList,(TextView)findViewById(R.id.Provider_editTxt));
                break;
            case R.id.PatientNameImg:
                showListViewDialog(PatientList,(TextView)findViewById(R.id.PatientNameTv));
                break;
            case R.id.LocationImg:
                Intent LocationIntent  = new Intent(MDLiveGetStarted.this,MDLiveLocation.class);
                startActivity(LocationIntent);
                break;
            case R.id.ProviderTypeImg:
                showListViewDialog(ProviderList,(TextView)findViewById(R.id.ProvidertypeTv));
                break;
        }
    }

    /**
     * @param selected ; Selected Button object
     * @param unSelected : Un-selected Button object
     *
     * Based on the button selection the background color (Transference or Blue) and text color(Gray or White) will be displayed
     */
    private void changeButtonColor(Button selected, Button unSelected){
        selected.setBackgroundResource(R.drawable.btn_selected);
        unSelected.setBackgroundResource(R.drawable.btn_unselected);
        unSelected.setTextColor(Color.parseColor("#A4A4A4"));
        selected.setTextColor(Color.parseColor("#ffffff"));
    }

    /**
     *      @param list : Dependent users array list
     *     Instantiating array adapter to populate the listView
     *     The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     *
    */
    private void showListViewDialog (final ArrayList<String> list,final TextView selectedText) {

      /*We need to get the instance of the LayoutInflater*/
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveGetStarted.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String SelectedText = list.get(position);
                selectedText.setText(SelectedText);
                dialog.dismiss();
            }
        });
    }

}

