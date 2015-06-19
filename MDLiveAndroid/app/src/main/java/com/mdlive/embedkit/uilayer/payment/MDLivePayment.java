package com.mdlive.embedkit.uilayer.payment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MDLivePayment extends Activity {

    private EditText dateView, offercode,edtZipCode;
    private int year, month, day;
    static final int DATE_PICKER_ID = 1111;
    private Button cls_btn, ok_btn, payNow;
    private String applyOfferCode;
    private WebView HostedPCI;
    private DatePicker datePicker;
    protected ProgressDialog pDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);
        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (EditText) findViewById(R.id.edtExpiryDate);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDialog(DATE_PICKER_ID);
            }
        });
        payNow = (Button) findViewById(R.id.paynow);
        edtZipCode= (EditText) findViewById(R.id.edtZipCode);
        TextView textview = (TextView) findViewById((R.id.textView5));
        HostedPCI.getSettings().setJavaScriptEnabled(true);
        HostedPCI.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }


        SimpleDateFormat dateFormat=new SimpleDateFormat("MM/yy");
        dateView.setText(dateFormat.format(new Date()));

        // HostedPCI.loadUrl("file:///android_asset/htdocs/templateIndex.html");
        HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
        pDialog = Utils.getProgressDialog("Please wait...", this);
        textview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ed=new EditText(getApplicationContext());
                showDialog();            }
        });
        payNow.setOnClickListener(paynow_button_click_listener);
        ((ImageView) findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLivePayment.this);
                finish();
            }
        });
        ((ImageView) findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // movetohome();
            }
        });
    }
    EditText ed;

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void sendToAndroid(String text) {
            // this is called from JS with passed value
            try{
                JSONObject jobj=new JSONObject(text);
                if(jobj.getString("status").equals("success")){
                    doConfirmAppointment();
                }else{
                    Utils.alert(pDialog,MDLivePayment.this,jobj.getString("status"));

                }



            }catch (Exception e){
                e.printStackTrace();
            }

//            Intent intent = new Intent(MDLivePayment.this,Wait)
        }
        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void scanCreditCard(){
        }
    }

    private View.OnClickListener paynow_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {

            if(edtZipCode.getText().toString().length()!=0&&dateView.getText().toString().length()!=0){
                HostedPCI.loadUrl("javascript:tokenizeForm()");
            }else{
                Utils.alert(pDialog,MDLivePayment.this,"Please fill all the details");

            }



        }
    };


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // return new DatePickerDialog(this, pickerListener, year, month,day);
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int Currentday = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(this, pickerListener, currentYear, currentMonth, Currentday);
                try {
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
                } catch (Exception ex) {
                }
                return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat dateFormat=new SimpleDateFormat("MM/yy");
            dateView.setText(dateFormat.format(c.getTime()));
        }
    };


    private void populateTable(final ProgressDialog dialog) {
        runOnUiThread(new Runnable(){
            public void run() {

                try {
                    dialog.dismiss();
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        });
    }


    private void doConfirmAppointment() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               populateTable(pDialog);
                Log.e("Response Payment",response.toString());
                try {
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                populateTable(pDialog);
                Log.e("Response error",error.toString());
                try{
                    JSONObject jsonObject=new JSONObject(error.getMessage());
                    if(jsonObject.has("message")){
                        Utils.alert(pDialog,MDLivePayment.this,jsonObject.getString("message"));
                    }
                }catch (Exception e){

                }

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
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("appointment_method", "1");
        params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", "Not Sure");
        params.put("state_id", "FL");
        params.put("customer_call_in_number", "9068906789");
        params.put("chief_complaint_reasons", null);
        params.put("alternate_visit_option", "alternate_visit_option");
        params.put("do_you_have_primary_care_physician", "No");
        Gson gson = new GsonBuilder().serializeNulls().create();

        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePayment.this, null);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);



    }


    public  void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLivePayment.this);
        // set title
        LayoutInflater inflater = MDLivePayment.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mdlive_popup, null);
        alertDialogBuilder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.offerCode);
        // set dialog message

        alertDialogBuilder.setCancelable(false).setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editText.getText().toString().length() != 0) {
                    applyPromoCode(editText.getText().toString());
                }/*else{
                    Toast.makeText(getApplicationContext(), "Please enter the promocode", Toast.LENGTH_SHORT).show();
                }*/


            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    public void applyPromoCode(String promoCode){
        pDialog.show();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                pDialog.dismiss();
                handlePromocodeResponse(response.toString());
                Log.e("Response Succeed",response.toString());
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hanldeErrorResponse(error.getMessage());
                Log.e("Response Failure",error.toString());
                pDialog.dismiss();

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
        };

        ConfirmAppointmentServices promocodeService=new ConfirmAppointmentServices(MDLivePayment.this,pDialog);
        promocodeService.doGetPromocode(promoCode,successListener,errorListener);

    }

    public void handlePromocodeResponse(String response){
        try{
            JSONObject resObject=new JSONObject(response);
            if(resObject.has("discount_amount")){
                String discountAmount=resObject.getString("discount_amount").replace("$","");
                double payableAmount=Double.parseDouble(discountAmount.trim())-49.00;
                Log.e("Payable AMOU","Discount ASmount"+payableAmount);
                ((TextView) findViewById(R.id.cost)).setText("Total :$"+payableAmount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hanldeErrorResponse(String errorResponse){
        try{
            JSONObject resObject=new JSONObject(errorResponse);
            if(resObject.has("error")){
                Utils.alert(pDialog,MDLivePayment.this,resObject.getString("error"));
            }
        }catch (Exception e){

        }
    }




 /*   public  void showDialog(final Context context, String title, String message, EditText promoCode, DialogInterface.OnClickListener positiveOnclickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", positiveOnclickListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(promoCode);
        alertDialog.show();
    }*/

    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */

   /* public void movetohome() {
        Utils.movetoback(MDLivePayment.this, MDLiveLogin.class);
    }*/

}
