package com.mdlive.embedkit.uilayer.pharmacy;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryActivity;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
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
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suggestionList = new ArrayList<String>();
    private LocationCooridnates locationService;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private Intent sendingIntent;
    private String errorMesssage;
    protected boolean isPerformingAutoSuggestion, mayIShowSuggestions = true;
    protected static String previousSearch = "";
    private IntentFilter intentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pharmacy_choose);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.exit_icon);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_search_pharm_txt));



        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        //initialize views of activity
        initializeViews();
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

    }


    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
        onBackPressed();
    }

    public void rightBtnOnClick(View v){
        String hasErrorMessage = hasValidationMessage();
        if (hasErrorMessage == null) {
            addExtrasInIntent();
            startActivity(sendingIntent);
            MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
            finish();
        } else {
            MdliveUtils.showDialog(MDLivePharmacyChange.this, "Alert", hasErrorMessage);
        }
    }


    /*
     *This function is used to initialized views defined in layout.
     * LocationService is initialized here. It is used to get current location of user.
     * pharmacy_search_name is a edit text. When users enters the text, it will call to webservice
     * and feed results in drop down box.
     */
    private void initializeViews() {
        chooseState = ((TextView) findViewById(R.id.chooseState));
        pharmacy_search_name = ((AutoCompleteTextView) findViewById(R.id.pharmacy_search_name));
        zipcodeText = ((EditText) findViewById(R.id.zipcodeText));
        cityText = ((EditText) findViewById(R.id.cityText));
        setProgressBar(findViewById(R.id.progressDialog));
        errorMesssage = getString(R.string.mdl_no_pharmacies_listed);

        //This function is used to initialized State Dialog.
        initializeStateDialog();

        //Initialize Intent for Pharmacy Results
        sendingIntent = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
        sendingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(getIntent().hasExtra("FROM_MY_HEALTH")){
            sendingIntent.putExtra("FROM_MY_HEALTH",getIntent().getBooleanExtra("FROM_MY_HEALTH",false));
        }
        pharmacy_search_name.addTextChangedListener(pharmacySearchNameTextWatcher());
        adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
        pharmacy_search_name.setThreshold(3);
        pharmacy_search_name.setAdapter(adapter);
        zipcodeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cityText.setText(StringConstants.EMPTY_STRING);
                    stageListView.setItemChecked(0, true);
                    chooseState.setText(getString(R.string.mdl_select_stg));
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
                if (zipcodeText.getText().toString().length() >= 9) {
                    if (!zipcodeText.getText().toString().contains("-")) {
                        String formattedString = MdliveUtils.zipCodeFormat(Long.parseLong(zipcodeText.getText().toString()));
                        zipcodeText.setText(formattedString);
                    }
                }
            }
        });
        cityText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    zipcodeText.setText(StringConstants.EMPTY_STRING);
                }
            }
        });
        locationService = new LocationCooridnates(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            locationService.setBroadCastData(StringConstants.DEFAULT);
            registerReceiver(locationReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationService.setBroadCastData(StringConstants.DEFAULT);
            unregisterReceiver(locationReceiver);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function handles click listener of chooseState
     *
     * @param view - view of button which is called.
     */
    public void chooseStateOnClick(View view) {
        zipcodeText.setText(StringConstants.EMPTY_STRING);
        zipcodeText.clearFocus();
        cityText.requestFocus();
        stateDialog.show();
    }

    /**
     * This function handles click listener of getlocationButton
     *
     * @param view - view of button which is called.
     */
    public void getlocationButtonOnClick(View view) {
        mayIShowSuggestions = false;
        getLocationBtnOnClickAction();
    }

    //The back image will pull you back to the Previous activity
    //The home button will pull you back to the Dashboard activity
    public void backImgOnClick(View view) {
        MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
        onBackPressed();
    }

    /**
     * This function handles click listener of doneTxt
     *
     * @param view - view of button which is called.
     */
    public void doneTxtOnClick(View view) {
        String hasErrorMessage = hasValidationMessage();
        if (hasErrorMessage == null) {
            addExtrasInIntent();
            startActivity(sendingIntent);
            MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
            finish();
        } else {
            MdliveUtils.showDialog(MDLivePharmacyChange.this, "Alert", hasErrorMessage);
        }
    }

    /**
     * This fucntion is used to generate error message which will be used in MDLivePharmacyResult
     * According to options which is selected by user, error message will be differs.
     * returning error message will be attached to intent and send over to MDLivePharmacyResult page.
     */
    public String hasValidationMessage() {
        if (zipcodeText.getText() != null && !zipcodeText.getText().toString().trim().equals("")) {
            if (MdliveUtils.validateZipCode(zipcodeText.getText().toString())) {
                if (pharmacy_search_name.getText() != null && pharmacy_search_name.getText().toString().length() != 0) {
                    errorMesssage = getString(R.string.mdl_not_find_pharmacy_zip,
                            pharmacy_search_name.getText().toString(),
                            zipcodeText.getText().toString());
                } else {
                    errorMesssage = getString(R.string.mdl_no_pharmacies_listed);
                }
            } else {
                return getString(R.string.mdl_valid_zip);
            }
        } else if (chooseState.getText() != null && !chooseState.getText().toString().equals(getString(R.string.mdl_select_stg)) && !chooseState.getText().toString().trim().equals("")) {
            if (cityText.getText() == null || cityText.getText().toString().trim().equals("")) {
                return getString(R.string.mdl_input_city);
            }
            if (pharmacy_search_name.getText() != null && pharmacy_search_name.getText().toString().length() != 0) {
                errorMesssage = getString(R.string.mdl_not_find_pharmacy_state,
                        pharmacy_search_name.getText().toString(),
                        cityText.getText().toString(),
                        chooseState.getText().toString());
            } else {
                errorMesssage = getString(R.string.mdl_no_pharmacies_listed);
            }
        } else if (cityText.getText() != null && !cityText.getText().toString().trim().equals("")) {
            if (chooseState.getText() == null || chooseState.getText().toString().equals(getString(R.string.mdl_select_stg)) || chooseState.getText().toString().trim().equals("")) {
                return getString(R.string.mdl_input_state);
            }
            if (pharmacy_search_name.getText() != null && pharmacy_search_name.getText().toString().trim().length() != 0) {
                errorMesssage = getString(R.string.mdl_not_find_pharmacy_state,
                        pharmacy_search_name.getText().toString(),
                        cityText.getText().toString(),
                        chooseState.getText().toString());
            } else {
                errorMesssage = getString(R.string.mdl_no_pharmacies_listed);
            }
        } else if ((TextUtils.isEmpty(zipcodeText.getText().toString()))
                && ((TextUtils.isEmpty(chooseState.getText().toString())
                || chooseState.getText().toString().equals(getString(R.string.mdl_select_stg))))) {
            return getString(R.string.mdl_input_state_city);
        }
        return null;
    }


    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgress();
            mayIShowSuggestions = true;
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                double lat = intent.getDoubleExtra("Latitude", 0d);
                double lon = intent.getDoubleExtra("Longitude", 0d);
                Location loc = new Location("dummyprovider");
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                addExtrasForLocationInIntent(loc);
                MdliveUtils.hideSoftKeyboard(MDLivePharmacyChange.this);
                startActivity(sendingIntent);
                finish();
                MdliveUtils.startActivityAnimation(MDLivePharmacyChange.this);
            }else{
                Toast.makeText(getApplicationContext(), "Unable to get your location", Toast.LENGTH_SHORT).show();
            }
        }
    };


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
        mayIShowSuggestions = false;
        if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
            showProgress();
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(getApplicationContext());
        } else {
            MdliveUtils.showGPSSettingsAlert(MDLivePharmacyChange.this, (RelativeLayout) findViewById(R.id.progressDialog));
            hideProgress();
        }
    }

    /**
     * This function is used to get suggestion results from webservice
     *
     * responseListener - handleSuggestionSuccessResponse handles the reponse results
     *
     * SuggestPharmayService class handling webservice integration for suggestions
     *
     * @param searchText - This text is enter by users
     */
    public void addSearchTextHistory(String searchText) {
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
                MdliveUtils.handelVolleyErrorResponse(MDLivePharmacyChange.this, error, getProgressDialog());
            }
        };
        if (!isPerformingAutoSuggestion && !previousSearch.equalsIgnoreCase(searchText)) {
            ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
            SuggestPharmayService services = new SuggestPharmayService(getApplicationContext(), null);
            mayIShowSuggestions = true;
            services.doSuggestionRequest(searchText, responseListener, errorListener);
            previousSearch = searchText;
            isPerformingAutoSuggestion = true;
        }
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
            for (int i = 0; i < jarray.length(); i++) {
                suggestionList.add(jarray.getString(i));
            }
            if (suggestionList.size() > 0 && mayIShowSuggestions) {
                ArrayAdapter<String> adapter = getAutoCompletionArrayAdapter(pharmacy_search_name, suggestionList);
                pharmacy_search_name.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function creates an array adapter for the AutoCompletionTextView. Once the list item is
     * clicked, the text is set to the edit text and the autocompletion list is dismissed.
     *
     * @param atv           :: The AutoCompletionTextView
     * @param conditionList :: The conditions array list
     * @return The array adapter
     */
    private ArrayAdapter<String> getAutoCompletionArrayAdapter(final AutoCompleteTextView atv, final ArrayList<String> conditionList) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
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
        errorMesssage = getString(R.string.mdl_no_pharmacies_listed);
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
     *
     * Statelist will be displayed using this function.
     */
    private void initializeStateDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLivePharmacyChange.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        stageListView = (ListView) convertView.findViewById(R.id.popupListview);
        stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));
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
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("FROM_MY_HEALTH")){
            Intent i = new Intent(getBaseContext(),MedicalHistoryActivity.class);
            i.putExtra("FROM_PHARMACY",true);
            startActivity(i);
            MdliveUtils.closingActivityAnimation(this);
        } else {
            super.onBackPressed();
            MdliveUtils.closingActivityAnimation(this);
        }
    }

    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
    }
}