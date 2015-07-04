package com.mdlive.embedkit.uilayer.pharmacy.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.SuggestPharmayService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * The wrapper class for Change Pharmacy Activity.
 * The layout will have the details about option for searching pharmacy details.
 * Once user chooses options from search, it will navigate to MDLBTPharmacy_ResultTab page.
 */

public class MDLivePharmacyChange extends Activity {

    private AutoCompleteTextView pharmacy_search_name;
    private EditText zipcodeText, cityText;
    private TextView chooseState;
    private ListView stageListView;
    private AlertDialog stateDialog;
    private Button getlocationButton;
    private ProgressDialog pDialog;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suggestionList = new ArrayList<String>();
    private LocationCooridnates locationService;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private Intent sendingIntent;
    private int keyDel=0;
    private String errorMesssage = "No Pharmacies listed in your location.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy_choose);
        //initialize views of activity
        Utils.checkGpsLocation(MDLivePharmacyChange.this);
        initializeViews();
    }

    /*
     *This function is used to initialized views defined in layout.
     * LocationService is initialized here. It is used to get current location of user.
     * pharmacy_search_name is a edit text. When users enters the text, it will call to webservice
     * and feed results in drop down box.
     */
    private void initializeViews() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LocalisationHelper.localiseLayout(this, view);
        chooseState = ((TextView) findViewById(R.id.chooseState));
        pharmacy_search_name = ((AutoCompleteTextView) findViewById(R.id.pharmacy_search_name));
        zipcodeText = ((EditText) findViewById(R.id.zipcodeText));
        cityText = ((EditText) findViewById(R.id.cityText));
        getlocationButton = ((Button) findViewById(R.id.getlocationButton));
        pDialog = Utils.getProgressDialog("Loading...", this);
        //Initialize Intent for Pharmacy Results
        sendingIntent = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
        sendingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pharmacy_search_name.addTextChangedListener(pharmacySearchNameTextWatcher());
        adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
        pharmacy_search_name.setThreshold(3);
        pharmacy_search_name.setAdapter(adapter);
        zipcodeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cityText.setText("");
                    stageListView.setItemChecked(0, true);
                    chooseState.setText("Select State");
                }
            }
        });
        //validation for Zip code
        zipcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(zipcodeText.getText().toString().length()>=9){
                    if(!zipcodeText.getText().toString().contains("-")){
                        String formattedString=Utils.zipCodeFormat(Long.parseLong(zipcodeText.getText().toString()));
                        zipcodeText.setText(formattedString);
                    }

                }
            }
        });
        cityText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    zipcodeText.setText("");
                }
            }
        });

        ((TextView) findViewById(R.id.doneTxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hasErrorMessage = hasValidationMessage();
                if(hasErrorMessage == null){
                    addExtrasInIntent();
                    Utils.hideSoftKeyboard(MDLivePharmacyChange.this);
                    startActivity(sendingIntent);
                    finish();
                }else{
                    Utils.showDialog(MDLivePharmacyChange.this, "Alert", hasErrorMessage);
                }
            }
        });

        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLivePharmacyChange.this);
                finish();
            }
        });

        locationService = new LocationCooridnates();
        getlocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationBtnOnClickAction();
            }
        });
        initializeStateDialog();
        chooseState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipcodeText.setText("");
                zipcodeText.clearFocus();
                cityText.requestFocus();
                stateDialog.show();
            }
        });
    }

    public String hasValidationMessage(){
        if(zipcodeText.getText()!=null && !zipcodeText.getText().toString().trim().equals("")){
            if(Utils.validateZipCode(zipcodeText.getText().toString())){
                if(pharmacy_search_name.getText() !=null && pharmacy_search_name.getText().toString().length() != 0){
                    errorMesssage = "Sorry, we could not find "+pharmacy_search_name.getText().toString()
                         +" in the "+zipcodeText.getText().toString()+" ZIP Code. Please check the pharmacy ZIP Code or search by city and state.";
                }else{
                    errorMesssage = "No Pharmacies listed in your location.";
                }
            }else{
                return "Please enter a valid Zipcode.";
            }
        }else if(chooseState.getText()!=null && !chooseState.getText().toString().equals("Select State") && !chooseState.getText().toString().trim().equals("")){
            if(cityText.getText() == null || cityText.getText().toString().trim().equals("")){
                return "Please input City.";
            }
            if(pharmacy_search_name.getText() !=null && pharmacy_search_name.getText().toString().length() != 0){
                errorMesssage = "Sorry, we could not find "+pharmacy_search_name.getText().toString()
                        +" near "+cityText.getText().toString()+", "+chooseState.getText().toString();
            }else{
                errorMesssage = "No Pharmacies listed in your location.";
            }
        }else if(cityText.getText()!=null && !cityText.getText().toString().trim().equals("")){
            if(chooseState.getText() == null || chooseState.getText().toString().equals("Select State") || chooseState.getText().toString().trim().equals("")){
                return "Please select a State!";
            }
            if(pharmacy_search_name.getText() != null && pharmacy_search_name.getText().toString().trim().length() != 0){
                errorMesssage = "Sorry, we could not find "+pharmacy_search_name.getText().toString()
                        +" near "+cityText.getText().toString()+", "+chooseState.getText().toString();
            }else{
                errorMesssage = "No Pharmacies listed in your location.";
            }
        }else if( (TextUtils.isEmpty(zipcodeText.getText().toString()))
                && ((TextUtils.isEmpty(chooseState.getText().toString())
                || chooseState.getText().toString().equals("Select State")))){
            return "Please choose any state/zipcode to proceed search.";
        }
        return null;
    }


    /**
     * The textwatcher for Pharmacy Search Name. This will initiate the autosuggestion for
     * pharmacy search name.
     *
     * @return TextWatcher - The text watcher instance
     */
    private TextWatcher pharmacySearchNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 3&&!s.toString().startsWith(" ")) {
                    addSearchTextHistory(s.toString());
                }
            }
        };
    }

    /**
     * OnClickAction for Location Button Action. This will get the current location. If the current
     * location is received, starts teh MDLivePharmacyResult activity.
     */
    private void getLocationBtnOnClickAction() {
        if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
            pDialog.show();
            locationService.getLocation(this, new LocationCooridnates.LocationResult() {
                @Override
                public void gotLocation(final Location location) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();
                            if (location != null) {
                                addExtrasForLocationInIntent(location);
                                Utils.hideSoftKeyboard(MDLivePharmacyChange.this);
                                startActivity(sendingIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to get your location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            Utils.showGPSSettingsAlert(MDLivePharmacyChange.this);
        }
    }

    /**
     * This function is used to get suggestion results from webservice
     * <p/>
     * responseListener - handleSuggestionSuccessResponse handles the reponse results
     * <p/>
     * SuggestPharmayService class handling webservice integration for suggestions
     *
     * @param searchText - This text is enter by users
     */
    public void addSearchTextHistory(String searchText) {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuggestionSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Utils.handelVolleyErrorResponse(MDLivePharmacyChange.this, error, pDialog);
            }
        };
        SuggestPharmayService services = new SuggestPharmayService(getApplicationContext(), pDialog);
        services.doSuggestionRequest(searchText, responseListener, errorListener);
    }

    /**
     * This function is used to handle the response of suggestion results.
     * <p/>
     * Once response text is parsed, then it will be added in pharmacy_search_name suggestion box.
     *
     * @param response - Response recevied from addSearchTextHistory - onResponseReceiver.
     */
    private void handleSuggestionSuccessResponse(JSONObject response) {
        try {
            suggestionList.clear();
            JSONArray jarray = response.getJSONArray("pharmacies");
            for (int i = 0; i < jarray.length(); i++) {
                suggestionList.add(jarray.getString(i));
            }
            adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
            pharmacy_search_name.setThreshold(3);
            pharmacy_search_name.setAdapter(adapter);
            pharmacy_search_name.showDropDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *This function creates an array adapter for the AutoCompletionTextView. Once the list item is
     * clicked, the text is set to the edit text and the autocompletion list is dismissed.
     *
     *
     * @param atv :: The AutoCompletionTextView
     * @param conditionList :: The conditions array list
     * @return The array adapter
     */
    private ArrayAdapter<String> getAutoCompletionArrayAdapter(final AutoCompleteTextView atv, final ArrayList<String> conditionList) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionList) {
            @Override
            public View getView(int position,View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView, parent);
                parent.setBackgroundColor(Color.WHITE);
                final TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        atv.setText(text.getText().toString());
                        atv.dismissDropDown();
                        atv.setAdapter(null);
                    }
                });
                return view;
            }
        };
    }

    /**
     * This function is used to get post body text depends upon user currrent location
     * which is required to get results from webservice.
     *
     * @param location - location of user currently located which is fetch by location service.
     */
    private void addExtrasForLocationInIntent(Location location) {
        sendingIntent.putExtra("longitude", location.getLongitude());
        sendingIntent.putExtra("latitude", location.getLatitude());
        errorMesssage = "No Pharmacies listed in your location";
    }

    /**
     * This function is used to get post body depends upon option that users selected.
     */
    private void addExtrasInIntent() {
        HashMap<String, Object> keyParams = new HashMap<String, Object>();
        if (!TextUtils.isEmpty(pharmacy_search_name.getText().toString()))
            sendingIntent.putExtra("name", pharmacy_search_name.getText().toString());
        try {
            if (!TextUtils.isEmpty(zipcodeText.getText().toString())) {
                sendingIntent.putExtra("zipcode", zipcodeText.getText().toString());
            } else {
                if (!TextUtils.isEmpty(cityText.getText().toString())) {
                    sendingIntent.putExtra("city", cityText.getText().toString());
                }
                if (stageListView.getCheckedItemPosition() > 0)
                    sendingIntent.putExtra("state", stateIds.get(stageListView.getCheckedItemPosition()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendingIntent.putExtra("errorMesssage", errorMesssage);
    }

    /**
     * This function is for initialize Dialogs used in MDLivePharmacyChange Page.
     * <p/>
     * Statelist will be displayed using this function.
     */
    private void initializeStateDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLivePharmacyChange.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        stageListView = (ListView) convertView.findViewById(R.id.popupListview);
        stateList = Arrays.asList(getResources().getStringArray(R.array.stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.stateCode));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, stateList);
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
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLivePharmacyChange.this, MDLiveLogin.class);
    }
}