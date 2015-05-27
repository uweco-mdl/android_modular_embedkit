package com.mdlive.mobile.uilayer.pharmacy.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.mobile.R;
import com.mdlive.mobile.uilayer.pharmacy.services.SuggestPharmayService;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The wrapper class for MDLive Change Pharmacy Activity.
 * The layout will have the details about option for searching pharmacy details.
 * Once user chooses options from search, it will navigate to MDLBTPharmacy_ResultTab page.
 */

public class MDLivePharmacyChange extends Activity {

    private AutoCompleteTextView pharmacy_search_name;
    private EditText zipcodeText, cityText;
    private TextView chooseState;
    private ListView stageListView;
    private AlertDialog stateDialog;
    private Button find_pharmacy, getlocationButton;
    private ProgressDialog pDialog;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suggestionList = new ArrayList<String>();
    private LocationCooridnates locationService;
    private ArrayList<String> stateIds = new ArrayList<String>();
    private ArrayList<String> stateList = new ArrayList<String>();
    private Intent sendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_choose);

        // This function is for initialize views inflated from this activity
        initializeViews();
    }

    /**
     *  This function is used to initialized views defined in layout.
     *
     * LocationCooridnates is initialized here. It is used to get current location of user.
     * pharmacy_search_name is a edit text. When users enters the text, it will call to webservice
     * and feed results in drop down box.
     *
     */

    private void initializeViews(){

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);

        chooseState = ((TextView) findViewById(R.id.chooseState));
        pharmacy_search_name = ((AutoCompleteTextView) findViewById(R.id.pharmacy_search_name));
        zipcodeText = ((EditText) findViewById(R.id.zipcodeText));
        cityText = ((EditText) findViewById(R.id.cityText));
        find_pharmacy = ((Button) findViewById(R.id.find_pharmacy));
        getlocationButton = ((Button) findViewById(R.id.getlocationButton));
        pDialog = Utils.getProgressDialog(LocalizationSingleton.getLocalizedString(R.string.loading_txt, "loading_txt", this), this);

        pharmacy_search_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Toast.makeText(getApplicationContext(), "hitting"+start, Toast.LENGTH_SHORT).show();
                if (start > 2) {
                    addSearchTextHistory(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adapter = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item, suggestionList);

        pharmacy_search_name.setThreshold(3);

        pharmacy_search_name.setAdapter(adapter);

        zipcodeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    cityText.setText("");
                    stageListView.setItemChecked(0, true);
                    chooseState.setText("Select State");
                }
            }
        });

       cityText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    zipcodeText.setText("");
                }
            }
        });

        find_pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendingIntent = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
                addExtrasInIntent();
                startActivity(sendingIntent);
            }
        });

        locationService = new LocationCooridnates();

        getlocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
                    locationService.getLocation(MDLivePharmacyChange.this, new LocationCooridnates.LocationResult(){
                        @Override
                        public void gotLocation(final Location location) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                               if(location != null){
                                           sendingIntent = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
                                           addExtrasForLocationInIntent(location);
                                           startActivity(sendingIntent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Unable to get your location!", Toast.LENGTH_SHORT).show();
                                }
                                }
                            });
                        }
                    });
                }
            }
        });

        // Initialize State Selection Alert window
        initializeStateDialog();

        chooseState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateDialog.show();
            }
        });

    }


    /**
     * This function is used to get suggestion results from webservice
     *
     * responseListener - handleSuggestionSuccessResponse handles the reponse results
     *
     * SuggestPharmayService class handling webservice integration for suggestions
     *
     * @param searchText  - This text is enter by users
     *
     *
     */

    public void addSearchTextHistory(String searchText){
        /*pDialog.show();*/

        Log.e("Search Text", searchText);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuggestionSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*pDialog.dismiss();*/
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLivePharmacyChange.this);
                    }
                }
            }};

        SuggestPharmayService services = new SuggestPharmayService(getApplicationContext(), null);
        services.doLoginRequest(searchText, responseListener, errorListener);
    }

    /**
     * This function is used to handle the response of suggestion results.
     *
     * Once response text is parsed, then it will be added in pharmacy_search_name suggestion box.
     *
     * @param response - Response recevied from addSearchTextHistory - onResponseReceiver.
     */


    private void handleSuggestionSuccessResponse(JSONObject response) {
        try {
            suggestionList.clear();
            JSONArray jarray = response.getJSONArray("pharmacies");
            for(int i = 0; i<jarray.length(); i++) {
                suggestionList.add(jarray.getString(i));
            }
            adapter = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item, suggestionList);
            pharmacy_search_name.setThreshold(3);
            pharmacy_search_name.setAdapter(adapter);
            pharmacy_search_name.showDropDown();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * This function is used to get post body text depends upon user currrent location
     *  which is required to get results from webservice.
     *
     * @param location - location of user currently located which is fetch by location service.
     */

    private void addExtrasForLocationInIntent(Location location){
        sendingIntent.putExtra("longitude", location.getLongitude());
        sendingIntent.putExtra("latitude", location.getLatitude());
    }

    /**
     * This function is used to get post body depends upon option that users selected.
     */

    private void addExtrasInIntent(){
        HashMap<String, Object> keyParams = new HashMap<String, Object>();

        if(!TextUtils.isEmpty(pharmacy_search_name.getText().toString()))
            sendingIntent.putExtra("name", pharmacy_search_name.getText().toString());
        try {
            if(!TextUtils.isEmpty(zipcodeText.getText().toString())){
                sendingIntent.putExtra("zipcode", zipcodeText.getText().toString());
        }else{
            if(!TextUtils.isEmpty(cityText.getText().toString())){
                sendingIntent.putExtra("city", cityText.getText().toString());
            }
            if(stageListView.getCheckedItemPosition() > 0)
                sendingIntent.putExtra("state", stateIds.get(stageListView.getCheckedItemPosition()));
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Initialize States Details in Dialog.
     */

    private void initializeStateDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLivePharmacyChange.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.screen_popup, null);
        alertDialog.setView(convertView);
        stageListView = (ListView) convertView.findViewById(R.id.popupListview);

        stateList.add("Alabama");
        stateList.add("Alaska");
        stateList.add("AmericanSamoa");

        stateIds.add("AL");
        stateIds.add("AK");
        stateIds.add("AM");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, stateList);
        stageListView.setAdapter(adapter);
        stageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        stateDialog = alertDialog.create();

        stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String SelectedText = stateList.get(position);
                chooseState.setText(SelectedText);
                stateDialog.dismiss();
            }
        });
    }
}