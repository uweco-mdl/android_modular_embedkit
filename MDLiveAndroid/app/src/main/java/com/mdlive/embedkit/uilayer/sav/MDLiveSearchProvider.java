package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.FilterSearchServices;
import com.mdlive.unifiedmiddleware.services.provider.SearchProviderDetailServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Calendar.MONTH;

/**
 * class : MDLiveSearchProvider - This class is used to Filter the provider list.
 */
public class MDLiveSearchProvider extends Activity {
    private ProgressDialog pDialog;
    private  TextView AppointmentTxtView,LocationTxtView, ProviderTypeTxtView,genderTxtView,edtSearch;
    private int month,day,year;
    private static final int DATE_PICKER_ID = 1111;
    private ArrayList<HashMap<String, String>> SearchArrayListProvider = new ArrayList<HashMap<String,String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListSpeciality = new ArrayList<HashMap<String,String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListSpeaks = new ArrayList<HashMap<String,String>>();
    private ArrayList<HashMap<String, String>> SearchArrayListAvailableBy= new ArrayList<HashMap<String,String>>();
    private ArrayList<HashMap<String, String>> SearchArrayList =new ArrayList<HashMap<String,String>>();
    private ArrayList<String> AvailableByArrayList = new ArrayList<String>();
    private ArrayList<String> ProviderTypeArrayList = new ArrayList<String>();
    private Map<String,Map<String,String>> tempmap=new HashMap<>();
    private ArrayList<String> SortByArrayList = new ArrayList<String>();
    private ArrayList<String> SpecialityArrayList = new ArrayList<String>();
    private ArrayList<String> SpeaksArrayList = new ArrayList<String>();
    private ArrayList<String> GenderArrayList = new ArrayList<String>();
    private HashMap<String,String> postParams=new HashMap<>();
    private RelativeLayout progressDialog;
    public String filter_SavedLocation,SavedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_search_provider);
//        pDialog = Utils.getProgressDialog("Please wait...", this);
        initialiseData();

      //Load Services
        loadSearchproviderDetails();
//        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
        SavedLocation = searchPref.getString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, null);
        filter_SavedLocation = searchPref.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        LocationTxtView.setText(SavedLocation);
    }

    /**
     *This method is to declare the view id's and setting the click listener for the
     * corresponding views .
     *
     */
    private void initialiseData() {
        LinearLayout availableLl = (LinearLayout) findViewById(R.id.availableLl);
        availableLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(AvailableByArrayList,(TextView)findViewById(R.id.AvailableTxtView),"available_by",SearchArrayListAvailableBy);
            }
        });
        /**
         *This method is to fetch the apoointment date and the native date picker is called for selecting
         * the required date.
         *
         */
        LinearLayout appointmentLl = (LinearLayout) findViewById(R.id.appointmentLl);
        appointmentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCurrentDate((TextView)findViewById(R.id.DateTxtView));
                // On button click show datepicker dialog
                showDialog(DATE_PICKER_ID);
            }
        });
        /**
         *This method is to fetch the Provider Type details.Here the Provider type can be either
         * Family Physician or Pediatrician.These things will be populated in the arraylist.
         *
         */
        LinearLayout providertypeLl = (LinearLayout) findViewById(R.id.providertypeLl);
        providertypeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(ProviderTypeArrayList,(TextView)findViewById(R.id.ProviderTypeTxtView),"provider_type",SearchArrayListProvider);
            }
        });
        /**
         *This method is to fetch the Sort order details.
         * The sorted list will be populated in the arraylist.
         *
         */
        LinearLayout SortByLl = (LinearLayout) findViewById(R.id.sortbyLl);
        SortByLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(SortByArrayList,(TextView)findViewById(R.id.SortbyTxtView),"sort_by",SearchArrayList);
            }
        });
        /**
         *This method is to fetch the Speciality type and this specialisation will be
         * completely based on the Provider type.If the provider type is pediatrician then the corresponding
         * specialisation for the particular Provider type will be populated.
         *
         */
        LinearLayout SpecialityLl = (LinearLayout) findViewById(R.id.specialityLl);
        SpecialityLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(SpecialityArrayList,(TextView)findViewById(R.id.SpecialityTxtView),"speciality",SearchArrayListSpeciality);
            }
        });
        /**
         *This method is to fetch the Location.Here the location can be fetched by
         * using either the current location or by using the manual search like either through
         * the Zip code or by selecting the state or the city.
         *
         */
        LinearLayout LocationLl = (LinearLayout) findViewById(R.id.locLl);
        LocationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MDLiveSearchProvider.this, MDLiveLocation.class);
                intent.putExtra("activitycaller", "searchprovider");
                startActivity(intent);
            }
        });
        /**
         *This method is to fetch the what the provider speaks.
         * THe provider speaking languages will be fetched and populated in the arraylist
         *
         */
        LinearLayout speaksLl = (LinearLayout) findViewById(R.id.speaksLl);
        speaksLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(SpeaksArrayList,(TextView)findViewById(R.id.SpeaksTxtView),"speaks",SearchArrayListSpeaks);
            }
        });
        /**
         *This method is to fetch what the provider gender is.
         * THe provider can be either Male or Female.
         *
         */
        LinearLayout GenderLl = (LinearLayout) findViewById(R.id.genderLl);
        GenderLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog(GenderArrayList,(TextView)findViewById(R.id.GenderTxtView),"gender",SearchArrayList);
            }
        });
        /**
         *The Search button taps the user to the Provider screen and it filters
         * tHe provider's category based on the corresponding selection of the filters.
         *
         */
        TextView SearchBtn = (TextView) findViewById(R.id.doneTxt);
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!edtSearch.getText().toString().isEmpty()) {
//                    if(!Utils.isValidName(edtSearch.getText().toString())){
//                        Utils.showDialog(MDLiveSearchProvider.this,getResources().getString(R.string.app_name),getResources().getString(R.string.invalid_name));
//                        return;
//                    }
                    postParams.put("located_in",filter_SavedLocation);
//                    postParams.put("located_in","FL");
                    postParams.put("available_by","3");
                    postParams.put("appointment_date",AppointmentTxtView.getText().toString());
                    postParams.put("gender",genderTxtView.getText().toString());
                    if(edtSearch.getText().toString().length()!=0){
                        postParams.put("provider_name",edtSearch.getText().toString());
                    }
                    if(postParams.get("provider_type")==null){
                        postParams.put("provider_type","3");
                    }
                    LoadFilterSearchServices();
//                } else {
//                    Utils.alert(pDialog,MDLiveSearchProvider.this,"Please select the Provider Name");
//                }
            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveSearchProvider.this);
                finish();

            }
        });
        AppointmentTxtView = (TextView) findViewById(R.id.DateTxtView);
        Calendar now = Calendar.getInstance();
        int month=now.get(Calendar.MONTH)+1;
        String currentDate=now.get(Calendar.YEAR)+"/"+month+"/"+now.get(Calendar.DAY_OF_MONTH);
        AppointmentTxtView.setText(currentDate);
        ProviderTypeTxtView = (TextView) findViewById(R.id.ProviderTypeTxtView);
        LocationTxtView = (TextView) findViewById(R.id.LocatioTxtView);
        TextView SpeaksTxtView = (TextView) findViewById(R.id.SpeaksTxtView);
        genderTxtView = (TextView) findViewById(R.id.GenderTxtView);
        edtSearch= (TextView) findViewById(R.id.edt_searchProvider);
        progressDialog = (RelativeLayout)findViewById(R.id.progressDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
        SavedLocation = searchPref.getString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, "FLORIDA");
        filter_SavedLocation = searchPref.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL");
        LocationTxtView.setText(SavedLocation);
    }

    /**
     *
     * Load Search provider Details.
     * Class : SearchproviderDetails - Service class used to fetch the search details
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadSearchproviderDetails() {
//        pDialog.show();
        progressDialog.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                pDialog.dismiss();
                progressDialog.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveSearchProvider.this);
                    }
                }
            }};
        SearchProviderDetailServices services = new SearchProviderDetailServices(MDLiveSearchProvider.this, pDialog);
        services.getsearchdetails(successCallBackListener, errorListener);
    }

    /**
     *
     *  Successful Response Handler for Search Provider's details.
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {

            progressDialog.setVisibility(View.GONE);
//            pDialog.dismiss();
            //Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            //Available by response
            getAvailableData(response);
            //Gender response
            getGenderData(response);
            //SortBy response
            getSortData(response);
            //Provider type response
            getSpecialityData(response);
            //Speakstype response
            getSpeaksData(response);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *
     * This method will return the Specialities name based on the provider Type and corresponding speciality
     * for the provider will be returned.
     *
     */
    private void getSpecialityData(JSONObject response) throws JSONException {
//        JSONArray provider_type_array=null;
//        provider_type_array.put("Any");
        JSONArray provider_type_array = response.getJSONArray("provider_type");


        for(int i = 0;i< provider_type_array.length();i++) {
            JSONObject licenseObject = provider_type_array.getJSONObject(i);
            String str_provider_type = licenseObject.getString("provider_type");
            String str_provider_type_id = licenseObject.getString("id");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(str_provider_type_id, str_provider_type);
            SearchArrayListProvider.add(map);
            ProviderTypeArrayList.add(str_provider_type);
            HashMap<String, String> specialitymap = null;
            //Speciality response
//            JSONArray speciality_array=null;
//            speciality_array.put("Any");
            JSONArray speciality_array = licenseObject.getJSONArray("speciality");
            SpecialityArrayList.clear();
            specialitymap = new HashMap<String, String>();
            for (int j = 0; j < speciality_array.length(); j++) {
                JSONObject specialityObj = speciality_array.getJSONObject(j);
                specialitymap.put(specialityObj.getString("name"), specialityObj.getString("id"));
                SearchArrayListSpeciality.add(specialitymap);
                SpecialityArrayList.add(specialityObj.getString("name"));
            }
                    tempmap.put(str_provider_type,specialitymap);



        }


    }
    /**
     *
     * This method will return the Sort types based on the  Availibility
     * of the Provider.
     *
     */

    private void getSortData(JSONObject response) throws JSONException {
//        JSONArray Sort_array=null;
//        Sort_array.put("Any");
        JSONArray Sort_array = response.getJSONArray("sort_by");
        ArrayList<String> keysList = new ArrayList<String>();
        for(int i = 0;i< Sort_array.length();i++){
            HashMap<String,String> map = new HashMap<String,String>();
          JSONObject itemObj=Sort_array.getJSONObject(i);

            Iterator<String> iter=  itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key,(String)itemObj.get(key));
                System.out.println(key);
            }
            SearchArrayList.add(map);

            SortByArrayList.add(Sort_array.getJSONObject(i).getString(Sort_array.getJSONObject(i).keys().next()));
            Log.e("SortByArrayList----->", Sort_array.getJSONObject(i).getString(Sort_array.getJSONObject(i).keys().next()));
        }
    }
    /**
     *
     * This method will return the Sort types based on the  Availibility
     * of the Provider.
     *
     */

    private void getSpeaksData(JSONObject response) throws JSONException {
//        JSONArray Speaks_array=null;
//        Speaks_array.put("Any");
        JSONArray Speaks_array = response.getJSONArray("speaks");
        ArrayList<String> keysList = new ArrayList<String>();
        for(int i = 0;i< Speaks_array.length();i++){
            HashMap<String,String> map = new HashMap<String,String>();
            JSONObject itemObj=Speaks_array.getJSONObject(i);

            Iterator<String> iter=  itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key,(String)itemObj.get(key));
                System.out.println(key);
            }

            SearchArrayListSpeaks.add(map);
            SpeaksArrayList.add(Speaks_array.getJSONObject(i).getString(Speaks_array.getJSONObject(i).keys().next()));
        }
    }
    /**
     *
     * This method will return the Gender  of the Provider.
     *
     */

    private void getGenderData(JSONObject response) throws JSONException {
//        JSONArray Gender_array=null;
//        Gender_array.put("Any");
        JSONArray Gender_array = response.getJSONArray("gender");

        for(int i = 0;i< Gender_array.length();i++){
            HashMap<String,String> map = new HashMap<String,String>();
            JSONObject itemObj=Gender_array.getJSONObject(i);

            Iterator<String> iter=  itemObj.keys();//Logic to get the keys form Json Object
            while (iter.hasNext()) {
                String key = iter.next();
                map.put(key,(String)itemObj.get(key));
                System.out.println(key);
            }
            SearchArrayList.add(map);
            GenderArrayList.add(Gender_array.getJSONObject(i).getString(Gender_array.getJSONObject(i).keys().next()));
        }
    }
    /**
     * This method will return the Availability Type whether the provider is
     * available through Video or phone.
     *
     */

    private void getAvailableData(JSONObject response) throws JSONException {
//        JSONArray Available_array=null;
//        Available_array.put("Any");
        JSONArray Available_array = response.getJSONArray("available_by");
        for(int i = 0;i< Available_array.length();i++){

            HashMap<String,String> map = new HashMap<String,String>();
           JSONObject itemObj=Available_array.getJSONObject(i);

           Iterator<String> iter=  itemObj.keys();//Logic to get the keys form Json Object
                           while (iter.hasNext()) {
                               String key = iter.next();
                               map.put(key,(String)itemObj.get(key));
                               System.out.println(key);
                           }
            SearchArrayListAvailableBy.add(map);
            AvailableByArrayList.add(Available_array.getJSONObject(i).getString(Available_array.getJSONObject(i).keys().next()));
        }
    }
    /**
     * Click Listener for the Corresponding views . Datas will be pushed in the corresponding arraylist
     * based on the ArrayList custom view will be populated with the datas.
     *
     */


    /**
     *
     * Load Filter Search Details.
     *
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the
     * service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error
     * message to user or Get started screen will shown to user).
     *
     */
    private void LoadFilterSearchServices() {
//        pDialog.show();
        progressDialog.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Sucess Response", response.toString());
                handleFilterSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
//                pDialog.dismiss();
                progressDialog.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveSearchProvider.this);
                    }
                }
            }};
        FilterSearchServices services = new FilterSearchServices(MDLiveSearchProvider.this, null);
        services.getFilterSearch(postParams,successCallBackListener, errorListener);
    }
    /**
     *
     *  Successful Response Handler for getting Current Location
     *
     */

    private void handleFilterSuccessResponse(JSONObject response) {
        try {
//            pDialog.dismiss();
            progressDialog.setVisibility(View.GONE);
            //Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            Intent intent=new Intent();
            intent.putExtra("Response",response.toString());
            setResult(1,intent);
            Utils.hideSoftKeyboard(MDLiveSearchProvider.this);
            finish();
            Log.e("Filter Response----->",responObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetCurrentDate(TextView selectedText)
    {
        // Get current date by calender

        final Calendar c = Calendar.getInstance();
         year = c.get(Calendar.YEAR);
         month = c.get(MONTH);
         day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date

        selectedText.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("/").append(day).append("/")
                .append(year).append(" "));
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime());
                return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            AppointmentTxtView.setText(new StringBuilder().append(year).append("/").append(month + 1)
                    .append("/").append(day)
                    .append(" "));

        }
    };

    /**
     *     Instantiating array adapter to populate the listView
     *     The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     *     @param list : Dependent users array list
     *
     */
    private void showListViewDialog (final ArrayList<String> list,final TextView selectedText,final String key,final  ArrayList<HashMap<String, String>> typeList) {

      /*We need to get the instance of the LayoutInflater*/
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveSearchProvider.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String SelectedText = list.get(position);
                Log.e("SelectedText",SelectedText);
                HashMap<String,String> localMap=typeList.get(position);
                for(Map.Entry entry: localMap.entrySet()){
                    Log.e("Values",entry.getValue().toString());
                    if(SelectedText.equals(entry.getValue().toString())){
                        postParams.put(key,entry.getKey().toString());
                        Log.e("Key Value",postParams.get(key));
                        break; //breaking because its one to one map
                    }
                }
                specialityBasedOnProvider(SelectedText, key);

                selectedText.setText(SelectedText);
                dialog.dismiss();
            }
        });
    }
    /**
     *
     *     Select the Speciality based on the Provider name and the speciality will be displayed
     *     for the dependent users.
     *     @param key : Dependent users Key
     *
     */

    private void specialityBasedOnProvider(String selectedText, String key) {
        if("provider_type".equalsIgnoreCase(key)) {
            SpecialityArrayList.clear();
            Map<String,String> speciality=tempmap.get(selectedText);
             Log.e("Size", "" + speciality.size());

            for (Map.Entry<String,String> entry : speciality.entrySet()) {
                SpecialityArrayList.add(entry.getKey());
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

        }
    }
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */

    public void movetohome()
    {
        Utils.movetohome(MDLiveSearchProvider.this, MDLiveLogin.class);
    }
}
