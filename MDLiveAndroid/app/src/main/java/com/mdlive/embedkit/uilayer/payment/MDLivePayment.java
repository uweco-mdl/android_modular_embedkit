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
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MDLivePayment extends Activity {

    private EditText dateView, edtZipCode;
    private int year, month, day;
    private Button cls_btn, ok_btn, payNow;
    private String promoCode=null;
    private WebView HostedPCI;
    private DatePicker datePicker;
    protected static ProgressDialog pDialog;
    private boolean isPaymentLoading;
    private int keyDel=0;
    private HashMap<String,HashMap<String,String>> billingParams;
    private  double payableAmount;
    private  String finalAmout="";
    Calendar expiryDate=  Calendar.getInstance();;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);

        if(getIntent()!=null){
            Bundle extras=getIntent().getExtras();
            finalAmout=String.format( "%.2f",Double.parseDouble(extras.getString("final_amount")));
            storePayableAmount(finalAmout);
            ((TextView) findViewById(R.id.cost)).setText("Total :$"+finalAmout);
        }


        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (EditText) findViewById(R.id.edtExpiryDate);
        getDateOfBirth();
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        payNow = (Button) findViewById(R.id.paynow);
        billingParams=new HashMap<>();
        edtZipCode= (EditText) findViewById(R.id.edtZipCode);
        edtZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtZipCode.getText().toString().length()>=9){
                    if(!edtZipCode.getText().toString().contains("-")){
                        String formattedString=Utils.zipCodeFormat(Long.parseLong(edtZipCode.getText().toString()));
                        edtZipCode.setText(formattedString);
                    }

                }
            }
        });

        TextView textview = (TextView) findViewById((R.id.textView5));
        HostedPCI.getSettings().setJavaScriptEnabled(true);
     /*   HostedPCI.setWebChromeClient(new WebChromeClient(){
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
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
        pDialog = Utils.getProgressDialog("Please wait...", MDLivePayment.this);
        //pDialog.show();
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
                 Utils.movetohome(MDLivePayment.this, MDLiveLogin.class);
            }
        });
    }
    EditText ed;

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void sendToAndroid(String billingResponse) {
            // this is called from JS with passed value
            try{
                JSONObject jobj=new JSONObject(billingResponse);
                if(jobj.getString("status").equals("success")){
                    String params=getBillingPutParams(billingResponse);
                    Log.e("Params",params+billingResponse);
                    updateCardDetails(params);
                }else{
                    Utils.alert(pDialog, MDLivePayment.this, jobj.getString("status"));
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

    private void showDatePicker(){
        final Dialog d = new Dialog(this);
        d.setTitle("Expiration month/year");
        d.setContentView(R.layout.monthly_picker_dialog);
        Button buttonDone = (Button) d.findViewById(R.id.set_button);
        final NumberPicker monthPicker = (NumberPicker) d.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = (NumberPicker) d.findViewById(R.id.year_picker);
        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(01);
        monthPicker.setWrapSelectorWheel(true);
        try {
            Calendar c = Calendar.getInstance();
            Date mDate = new Date();
            c.setTime(mDate);
            monthPicker.setValue(c.get(Calendar.MONTH)+1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        int minimumYear = c.get(Calendar.YEAR);

        yearPicker.setMaxValue(9999);
        yearPicker.setMinValue(minimumYear);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(minimumYear);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    year = yearPicker.getValue();
                    month = monthPicker.getValue() - 1;
                    expiryDate.set(year, month, 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
                    /*new SimpleDateFormat("MM/yyyy").parse(dateFormat.format(c.getTime())).compareTo(new Date());*/
                    dateView.setText(dateFormat.format(expiryDate.getTime()));
                    d.dismiss();
                }catch(Exception e){

                }
            }
        });

        d.show();
    }

    /**
     * This method will update the credit card information to server
     * ConfirmAppointmentServices class handles sending request to the server.
     * doUpdateBillingInformation() methos post request to server with params
     * successListener-Listner will invoke on Success response
     * errorListener-Listner will invoke on Error response
     */

    public void updateCardDetails(String params){
        showDialog(pDialog);
        final NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                try{
                    dismissDialog(pDialog);
                    Log.e("Succes Update",response.toString());
                    JSONObject resObj=new JSONObject(response.toString());
                    if(resObj.has("message")){
                        Log.e("Confirm","Calling");
                        doConfirmAppointment();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismissDialog(pDialog);
                    Utils.handelVolleyErrorResponse(MDLivePayment.this,error,pDialog);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        ConfirmAppointmentServices billingUpdateService=new ConfirmAppointmentServices(MDLivePayment.this,pDialog);
        billingUpdateService.doUpdateBillingInformation(params,successListener,errorListener);

    }

    /**
     * This method will convert user card details and information in to Json Parameters
     * @param billingDetails-Contains Billing details information as Json     *
     * @return-Billing parameter in a Json format
     */


    public String getBillingPutParams(String billingDetails){
        try{
            JSONObject resObj=new JSONObject(billingDetails);
            JSONObject billingObj=resObj.getJSONObject("billing_information");
            HashMap<String,String> cardInfo=new HashMap<>();
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            cardInfo.put("billing_name",settings.getString(PreferenceConstants.PATIENT_NAME,""));
            cardInfo.put("billing_address1","test1");
            cardInfo.put("billing_address2","test");
            Log.e("USer Location", settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"));
            cardInfo.put("billing_state_id", settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"));
            cardInfo.put("billing_city","Test");
            cardInfo.put("billing_country_id","1");
            cardInfo.put("cc_num",billingObj.getString("cc_num"));
            cardInfo.put("cc_cvv2",billingObj.getString("cc_cvv2"));
            Log.e("test Year",new SimpleDateFormat("yyyy").format(expiryDate.getTime()));
            cardInfo.put("cc_expyear",new SimpleDateFormat("yyyy").format(expiryDate.getTime()));
            cardInfo.put("cc_expmonth",String.valueOf(month));
            cardInfo.put("cc_hsa",billingObj.getString("cc_hsa"));
            cardInfo.put("billing_zip5",edtZipCode.getText().toString());
            cardInfo.put("cc_type_id",billingObj.getString("cc_type_id"));
            billingParams.put("billing_information",cardInfo);
            Log.e("Forming Params",new Gson().toJson(billingParams).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Gson().toJson(billingParams);
    }

    private View.OnClickListener paynow_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            if(finalAmout.equals("0.00")){
                doConfirmAppointment();
            }else{
                if(edtZipCode.getText().toString().length()!=0&&dateView.getText().toString().length()!=0){
                    if(Utils.validateZipCode(edtZipCode.getText().toString())){
                        HostedPCI.loadUrl("javascript:tokenizeForm()");
                    }else{
                        Utils.alert(pDialog,MDLivePayment.this,"Please enter a valid Zipcode.");
                    }

                }else{
                    Utils.alert(pDialog,MDLivePayment.this,"Please fill in all the required fields");

                }
            }




        }
    };


    public void storePayableAmount(String amount){
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.AMOUNT, amount);
        editor.commit();
    }





    /**
     * Fetching the values from the native date picker and the picker listener was implemented
     * for the particular native date picker.
     */
    private void getDateOfBirth() {
        try{
//            Calendar calendar = Calendar.getInstance();
//            datePickerDialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.getDatePicker().setCalendarViewShown(false);
//            datePickerDialog.getDatePicker().setSpinnersShown(true);
//            ((ViewGroup) datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
//            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());

        }catch (Exception e){
            e.printStackTrace();
        }

    }




    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            try{
                year = selectedYear;
                month = selectedMonth;
                Calendar c = Calendar.getInstance();
                c.set(selectedYear, selectedMonth, 1);
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
    private void showDialog(final ProgressDialog dialog) {
        runOnUiThread(new Runnable(){
            public void run() {
                try {
                    dialog.show();
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        });
    }


    private void doConfirmAppointment() {
       showDialog(pDialog);
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog(pDialog);
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
                    } else {
                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    dismissDialog(pDialog);
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{

                    dismissDialog(pDialog);
                    Utils.handelVolleyErrorResponse(MDLivePayment.this,error,pDialog);
                    /* if (error.networkResponse != null) {
                         NetworkResponse errorResponse=error.networkResponse;
                         Log.e("Status Code",""+error.networkResponse.statusCode);
                        if (error.getClass().equals(TimeoutError.class)) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            };
                            // Show timeout error message

                            Utils.connectionTimeoutError(pDialog, MDLivePayment.this);


                        }

                         else if (errorResponse.statusCode==HttpStatus.SC_UNPROCESSABLE_ENTITY||errorResponse.statusCode==HttpStatus.SC_NOT_FOUND||errorResponse.statusCode==HttpStatus.SC_UNAUTHORIZED)
                         {
                             Log.e("Status Code",""+error.networkResponse.statusCode);
                             String responseBody = new String(error.networkResponse.data, "utf-8" );
                             JSONObject errorObj = new JSONObject( responseBody );
                             if(errorObj.has("message")){
                                 Utils.alert(pDialog, MDLivePayment.this, errorObj.getString("message"));
                             }else if(errorObj.has("error")){
                                 Utils.alert(pDialog, MDLivePayment.this, errorObj.getString("error"));
                             }
                         }

                    }
*/                }catch (Exception e){
                    dismissDialog(pDialog);
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("appointment_method", "1");
       // params.put("phys_availability_id", null);
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON_PREFERENCES,"Not Sure"));
        params.put("customer_call_in_number", "9068906789");
        params.put("do_you_have_primary_care_physician","No");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION,"FL"));
        if(promoCode != null && !promoCode.isEmpty()){
            params.put("promocode", promoCode);
        }

      /*  params.put("customer_call_in_number", "9068906789");
        params.put("chief_complaint_reasons", null);
        params.put("alternate_visit_option", "alternate_visit_option");
        params.put("do_you_have_primary_care_physician", "No");*/
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
                    promoCode=editText.getText().toString();
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
       showDialog(pDialog);
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                dismissDialog(pDialog);
                handlePromocodeResponse(response.toString());
                Log.e("Response Succeed",response.toString());
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog(pDialog);
                Utils.handelVolleyErrorResponse(MDLivePayment.this,error,pDialog);
               /* try{
                    String responseBody = new String(error.networkResponse.data, "utf-8" );
                    JSONObject errorObj = new JSONObject( responseBody );
                    Log.e("Response Failure",responseBody);
                    if(errorObj.has("message")){
                        Utils.alert(pDialog, MDLivePayment.this, errorObj.getString("message"));
                    }else if(errorObj.has("error")){
                        Utils.alert(pDialog, MDLivePayment.this, errorObj.getString("error"));
                    }
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message

                        Utils.connectionTimeoutError(null, MDLivePayment.this);
                    }
                }catch (Exception e){
                    dismissDialog(pDialog);
                    e.printStackTrace();

                }*/

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
                payableAmount=Double.parseDouble(finalAmout)-Double.parseDouble(discountAmount.trim());

                Log.e("Amount",payableAmount+"");
                if(payableAmount<=0.00)
                {
                    payableAmount=0.00;
                    finalAmout=String.format( "%.2f",payableAmount);
                    storePayableAmount(finalAmout);
                    doConfirmAppointment();//Call the  confirm Appointment service if the user is Zero Dollar

                  Log.e("Condition",finalAmout+"");
                }else{
                    finalAmout=String.format( "%.2f",payableAmount);
                    storePayableAmount(finalAmout);
                }
                Log.e("Payable AMOU","Discount Amount"+payableAmount);
                ((TextView) findViewById(R.id.cost)).setText("Total :$"+finalAmout);
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
