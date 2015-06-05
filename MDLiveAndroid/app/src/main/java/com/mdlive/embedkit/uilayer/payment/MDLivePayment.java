package com.mdlive.embedkit.uilayer.payment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

import java.util.Calendar;
import java.util.HashMap;

public class MDLivePayment extends ActionBarActivity {

    private EditText dateView,offercode;
    private int year, month,day;
    static final int DATE_PICKER_ID = 1111;
    private PopupWindow pwindo;
    private Button cls_btn, ok_btn,payNow;
    private String offerCode;
    private WebView HostedPCI;
    private DatePicker datePicker;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);
        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (EditText) findViewById(R.id.datepicker);
        payNow = (Button) findViewById(R.id.paynow);
        TextView textview = (TextView) findViewById((R.id.textView5));
        HostedPCI.getSettings().setJavaScriptEnabled(true);
        HostedPCI.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
        pDialog = Utils.getProgressDialog("Loading...", this);
        hcidatepicker();
        textview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
        payNow.setOnClickListener(paynow_button_click_listener);
    }

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void sendToAndroid(String text) {
            // this is called from JS with passed value
//            Toast t = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
//            t.show();
            doConfirmAppointment();
//            Intent intent = new Intent(MDLivePayment.this,Wait)
        }
    }
    private void hcidatepicker()
    {
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        dateView.setText(new StringBuilder()
                .append(month + 1).append("-")
                .append(year).append(" "));

        dateView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                showDialog(DATE_PICKER_ID);

            }

        });
    }
    private void initiatePopupWindow() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MDLivePayment.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.mdlive_popup,
                    (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 800, 600, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            cls_btn = (Button) layout.findViewById(R.id.btn_close_popup);
            ok_btn = (Button) layout.findViewById(R.id.btn_ok_popup);
            offercode = (EditText) layout.findViewById((R.id.offerCode));
            cls_btn.setOnClickListener(cancel_button_click_listener);
            ok_btn.setOnClickListener(ok_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private View.OnClickListener paynow_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            HostedPCI.loadUrl("javascript:tokenizeForm()");

        }
    };
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };
    private View.OnClickListener ok_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {

            offerCode = offercode.getText().toString();
            pwindo.dismiss();
        }
    };
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

               // return new DatePickerDialog(this, pickerListener, year, month,day);
            DatePickerDialog dpd = new DatePickerDialog(this, pickerListener,year,month, day);
            try{
                java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
                for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                    if (datePickerDialogField.getName().equals("mDatePicker")) {
                        datePickerDialogField.setAccessible(true);
                        datePicker.setCalendarViewShown(false);
                        datePicker.setSpinnersShown(true);
                        datePicker = (DatePicker) datePickerDialogField.get(dpd);
                       java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                        for (java.lang.reflect.Field datePickerField : datePickerFields) {
                           if ("mDaySpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = new Object();
                                dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }

                }
            }catch(Exception ex){
            }
            return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


               public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
                   month = selectedMonth;
                   day = selectedDay;
            dateView.setText(new StringBuilder().append(month + 1)
                    .append("-").append(year)
                    .append(" "));

        }
    };


    private void doConfirmAppointment() {
       // pDialog.show();


      /*  NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    pDialog.dismiss();
                    //String apptId = response.getString("appointment_id");
                    String apptId= "40363";
                    if (apptId!=null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
                startActivity(i);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(null, MDLivePayment.this);
                    }
                }
            }};*/
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("appointment_method","1");
        params.put("phys_availability_id",null);
        params.put("timeslot","Now");
        params.put("provider_id",settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint","Not Sure");
        params.put("state_id", "FL");
        params.put("customer_call_in_number","9068906789");
        params.put("chief_complaint_reasons",null);
        params.put("alternate_visit_option","alternate_visit_option");
        params.put("do_you_have_primary_care_physician","No");
        Gson gson = new GsonBuilder().serializeNulls().create();

        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePayment.this, null);

        String apptId= "40363";
        if (apptId!=null) {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.APPT_ID, apptId);
            editor.commit();
            Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
            startActivity(i);
        }

        //services.doConfirmAppointment(gson.toJson(params),responseListener,errorListener);
    }
}
