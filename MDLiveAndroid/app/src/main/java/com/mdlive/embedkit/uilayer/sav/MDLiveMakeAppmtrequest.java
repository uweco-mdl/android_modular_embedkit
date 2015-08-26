package com.mdlive.embedkit.uilayer.sav;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.MakeanappmtServices;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.MONTH;

/**
 * Created by sudha_s on 8/20/2015.
 */
public class MDLiveMakeAppmtrequest extends MDLiveBaseActivity {
    private TextView appointmentIdealDate,appointmentNextAvailable;
    private EditText appointmentContactNumber,appointmentReason,appointmentComment;
    private HashMap<String,Object> params = new HashMap<>();
    private ArrayList<String> nextAvailableList = new ArrayList<>();
    private int month, day, year,selectedvideo=1;
    private String DoctorId,postidealTime;
    private static final int DATE_PICKER_ID = IdConstants.SEARCHPROVIDER_DATEPICKER;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_appointment_request_form);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.exit_icon);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.make_appointment_txt));


        initialization();
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        DoctorId = settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null);

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
        MdliveUtils.hideSoftKeyboard(MDLiveMakeAppmtrequest.this);
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveMakeAppmtrequest.this);
    }

    public void rightBtnOnClick(View v){
        String strappmtreason = appointmentReason.getText().toString().trim();
        String strappmtcomment = appointmentComment.getText().toString().trim();
        String strappointmentContactNumber = appointmentContactNumber.getText().toString().trim();
        String strnxtavailable = appointmentNextAvailable.getText().toString().trim();
        String stridealdate = appointmentIdealDate.getText().toString().trim();
        Log.e("post value","appmt reason->"+strappmtreason+"   strappmtcomment->"+strappmtcomment+"   strappointmentContactNumber->"+strappointmentContactNumber+"   strnxtavailable"+strnxtavailable+"   stridealdate"+postidealTime+"   SelectVideo"+selectedvideo+"  DoctorId"+DoctorId);
        if (!TextUtils.isEmpty(strappmtcomment) && !TextUtils.isEmpty(strappmtreason)&& !TextUtils.isEmpty(strappointmentContactNumber)

                && !TextUtils.isEmpty(strnxtavailable) && !TextUtils.isEmpty(stridealdate)) {

            HashMap params1 = new HashMap();
            params1.put("appointment_method", selectedvideo);
            params1.put("contact_number",strappointmentContactNumber);
            params1.put("chief_complaint", "Tendinitis");
            params1.put("physician_id", DoctorId);
            params1.put("appointment_date",postidealTime);
            params1.put("preferred_time", strnxtavailable);//
            params.put("alternate_visit_option","No Answer");
            params.put("do_you_have_primary_care_physician","No");
            params.put("appointment",params1);
            LoadappmtRequest();
            saveDateAndTime();


        } else {
            MdliveUtils.showDialog(MDLiveMakeAppmtrequest.this, getResources().getString(R.string.app_name), getResources().getString(R.string.please_enter_mandetory_fileds));
        }
    }
    public void saveDateAndTime()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.IDEAL_DATE,  appointmentIdealDate.getText().toString().trim());
        editor.putString(PreferenceConstants.NEXT_AVAIL_DATE, appointmentNextAvailable.getText().toString().trim());
        editor.commit();
    }

    public void initialization()
    {
        appointmentIdealDate = (TextView) findViewById(R.id.appointmentIdealDate);
        appointmentNextAvailable = (TextView) findViewById(R.id.appointmentNextAvailable);
        appointmentContactNumber = (EditText) findViewById(R.id.appointmentContactNumber);
        appointmentReason = (EditText) findViewById(R.id.appointmentReason);
        appointmentComment = (EditText) findViewById(R.id.appointmentComment);

    }
    public void onclickVideo(View v)
    {
        selectedvideo = 1;
    }
    public void onclickPhone(View v)
    {
        selectedvideo=2;
    }

    /**
     *
     * The Click event for the Provider Type will be showing the dialog which
     * contains the provider type like Family Physician or Pediatrician or
     * Therapist.For switching over to the dependent we will be having the
     * change for provider type .This will be based on the corresponding
     * dependents.
     *
     */
    public void onclickNxtAvailable(View v) {
        nextAvailableList.clear();
        nextAvailableList.add("morning");
        nextAvailableList.add("afternoon");
        nextAvailableList.add("evening");

        showListViewDialog(nextAvailableList,(TextView)findViewById(R.id.appointmentNextAvailable));

    }
    //date click listener
    // this is for Ideal date. The user can select the date from the picker to select
    //the corresponding date from the picker and set it to the corresponding label
    public void onclickDate(View v)
    {
        GetCurrentDate((TextView) findViewById(R.id.appointmentIdealDate));
        // On button click show datepicker dialog
        showDialog(DATE_PICKER_ID);
    }

    public void GetCurrentDate(TextView selectedText) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

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

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Show selected date
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            cal.set(Calendar.MONTH, selectedMonth);
            String format = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());
            ((TextView)findViewById(R.id.appointmentIdealDate)).setText(format);
            DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
            postidealTime = format1.format(cal.getTime());


        }
    };

    private void LoadappmtRequest() {
//        pDialog.show();
        //progressDialog.setVisibility(View.VISIBLE);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Sucess Response", response.toString());
                handlepostSuccessResponse(response);
                Intent intent = new Intent(MDLiveMakeAppmtrequest.this,MDLiveAppointmentThankYou.class);
                startActivity(intent);
                finish();
                MdliveUtils.startActivityAnimation(MDLiveMakeAppmtrequest.this);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    Log.e("Response Body", errorObj.toString());
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("error") || errorObj.has("message")) {
                            final String errorMsg = errorObj.has("error") ? errorObj.getString("error") : (errorObj.has("message") ? errorObj.getString("message") : "");
                            if(errorMsg != null && errorMsg.length() != 0){
                                (MDLiveMakeAppmtrequest.this).runOnUiThread(new Runnable() {
                                    public void run() {
                                        MdliveUtils.showDialog(MDLiveMakeAppmtrequest.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }, null);
                                    }
                                });
                            }
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveMakeAppmtrequest.this, error, null);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        MakeanappmtServices services = new MakeanappmtServices(MDLiveMakeAppmtrequest.this, null);
        services.makeappmt(params, successCallBackListener, errorListener);
        Log.e("params",params.toString());
    }
    private void handlepostSuccessResponse(JSONObject response) {
        try {
            hideProgress();
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            Log.e("mak res",response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
* Event for Add Child button click
* */

    /**
     * Instantiating array adapter to populate the listView
     * The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     * The dialog will be showing the provider type in the inflated listview and the
     * user can select either one among the list so it can be set to the Provider Type Text.
     * @param list : Dependent users array list
     */
    private void showListViewDialog(final ArrayList<String> list, final TextView selectedText) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveMakeAppmtrequest.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
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
