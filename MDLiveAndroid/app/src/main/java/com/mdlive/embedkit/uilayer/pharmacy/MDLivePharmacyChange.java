package com.mdlive.embedkit.uilayer.pharmacy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
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

public class MDLivePharmacyChange extends MDLiveBaseActivity {

    private AutoCompleteTextView pharmacy_search_name;
    private EditText zipcodeText, cityText;
    private TextView chooseState;
    private ListView stageListView;
    private AlertDialog stateDialog;
    private Button getlocationButton;
    private ProgressDialog pDialog;
    //private RelativeLayout progressBar;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suggestionList = new ArrayList<String>();
    private LocationCooridnates locationService;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private Intent sendingIntent;
    private int keyDel=0;
    private String errorMesssage = "No Pharmacies listed in your location.";
    protected boolean isPerformingAutoSuggestion;
    protected static String previousSearch = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy_choose);
        //initialize views of activity
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
        //progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        setProgressBar(findViewById(R.id.progressDialog));
//        pDialog = Utils.getProgressDialog("Loading...", this);
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
                        String formattedString= MdliveUtils.zipCodeFormat(Long.parseLong(zipcodeText.getText().toString()));
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
                    startActivity(sendingIntent);
                    MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
                    finish();
                    //MdliveUtils.startActivityAnimation(MDLivePharmacyChange.this);
                }else{
                    MdliveUtils.showDialog(MDLivePharmacyChange.this, "Alert", hasErrorMessage);
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
                MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
                finish();
                //MdliveUtils.closingActivityAnimation(MDLivePharmacyChange.this);
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
            if(MdliveUtils.validateZipCode(zipcodeText.getText().toString())){
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
                return "Please select a state.";
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
                if (s != null && s.length() >= 3 && !s.toString().startsWith(" ") && !previousSearch.trim().equalsIgnoreCase(s.toString().trim())) {
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
//            pDialog.show();
            //progressBar.setVisibility(View.VISIBLE);
            showProgress();
            locationService.getLocation(this, new LocationCooridnates.LocationResult() {
                @Override
                public void gotLocation(final Location location) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            pDialog.dismiss();
                            //progressBar.setVisibility(View.GONE);
                            hideProgress();
                            if (location != null) {
                                addExtrasForLocationInIntent(location);
                                startActivity(sendingIntent);
                                MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
                                MdliveUtils.startActivityAnimation(MDLivePharmacyChange.this);
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to get your location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            MdliveUtils.showGPSSettingsAlert(MDLivePharmacyChange.this,(RelativeLayout)findViewById(R.id.progressDialog));
            //progressBar.setVisibility(View.GONE);
            hideProgress();
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
        Log.e("Hitting Service....", "********************");
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isPerformingAutoSuggestion = false;
                handleSuggestionSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isPerformingAutoSuggestion = false;
                 MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyChange.this, error, pDialog);
            }
        };
        if (!isPerformingAutoSuggestion && !previousSearch.equalsIgnoreCase(searchText)) {
            ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
            SuggestPharmayService services = new SuggestPharmayService(getApplicationContext(), pDialog);
            services.doSuggestionRequest(searchText, responseListener, errorListener);
            previousSearch = searchText;
            isPerformingAutoSuggestion = true;
        }
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

            if(suggestionList.size() > 0){
                ArrayAdapter<String> adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                pharmacy_search_name.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

/*
            adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
            pharmacy_search_name.setThreshold(3);
            pharmacy_search_name.setAdapter(adapter);
            if(!selectedFromAutoComplete)
                 pharmacy_search_name.showDropDown();
*/


            //    pharmacy_search_name.showDropDown();
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
                        previousSearch = text.getText().toString();
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
        MdliveUtils.movetohome(MDLivePharmacyChange.this, getString(R.string.home_dialog_title), getString(R.string.home_dialog_text));
    }


    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}