package com.mdlive.embedkit.uilayer.payment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

    private EditText dateView, edtZipCode;
    private int year, month, day;
    private Button cls_btn, ok_btn, payNow;
    private String applyOfferCode;
    private WebView HostedPCI;
    private DatePicker datePicker;
    protected ProgressDialog pDialog;
    private DatePickerDialog datePickerDialog;
    private boolean isPaymentLoading;
    private int keyDel=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);
        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (EditText) findViewById(R.id.edtExpiryDate);
        getDateOfBirth();
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        payNow = (Button) findViewById(R.id.paynow);
        edtZipCode= (EditText) findViewById(R.id.edtZipCode);
        edtZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtZipCode.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL)
                            keyDel = 1;
                        return false;
                    }
                });

                if (keyDel == 0) {
                    int len = edtZipCode.getText().length();
                    if(len == 5) {
                        edtZipCode.setText(edtZipCode.getText() + "-");
                        edtZipCode.setSelection(edtZipCode.getText().length());
                    }
                } else {
                    keyDel = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView textview = (TextView) findViewById((R.id.textView5));
        HostedPCI.getSettings().setJavaScriptEnabled(true);
        HostedPCI.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isPaymentLoading)
                                pDialog.dismiss();
                        }
                    });
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
        pDialog = Utils.getProgressDialog("Please wait...", MDLivePayment.this);
        pDialog.show();
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
                if(Utils.validateZipCode(edtZipCode.getText().toString())){
                    HostedPCI.loadUrl("javascript:tokenizeForm()");
                }else{
                    Utils.alert(pDialog,MDLivePayment.this,"Please enter a valid Zipcode.");
                }

            }else{
                Utils.alert(pDialog,MDLivePayment.this,"Please fill all the details");

            }



        }
    };





    /**
     * Fetching the values from the native date picker and the picker listener was implemented
     * for the particular native date picker.
     */
    private void getDateOfBirth() {
        try{
            Calendar calendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        }catch (Exception e){

        }

    }




    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            try{
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                Calendar c = Calendar.getInstance();
                c.set(selectedYear, selectedMonth, selectedDay);
                SimpleDateFormat dateFormat=new SimpleDateFormat("MM/yy");
                new SimpleDateFormat("MM/yyyy").parse(dateFormat.format(c.getTime())).compareTo(new Date());
                dateView.setText(dateFormat.format(c.getTime()));
            }catch (Exception e){

            }

        }
    };


    private void dismissDialog(final ProgressDialog dialog) {
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
        pDialog = Utils.getProgressDialog("Please wait...", MDLivePayment.this);
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                pDialog.dismiss();
                Log.e("Response Payment",response.toString());
                try {
                    isPaymentLoading = false;
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
                        startActivity(i);
                        dismissDialog(pDialog);
                    } else {
                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        dismissDialog(pDialog);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //

                Log.e("Response error",error.toString());
                try{
                    isPaymentLoading = false;
                    JSONObject jsonObject=new JSONObject(error.getMessage());
                    if(jsonObject.has("message")){
                        Utils.alert(pDialog,MDLivePayment.this,jsonObject.getString("message"));
                    } else if(jsonObject.has("error")){
                        Utils.alert(pDialog,MDLivePayment.this,jsonObject.getString("error"));
                    }
                    dismissDialog(pDialog);

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
                        dismissDialog(pDialog);
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
        isPaymentLoading = true;
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




 

   /* public void movetohome() {
        Utils.movetoback(MDLivePayment.this, MDLiveLogin.class);
    }*/

}
