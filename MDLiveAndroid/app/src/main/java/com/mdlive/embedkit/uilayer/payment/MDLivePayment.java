package com.mdlive.embedkit.uilayer.payment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacy;
import com.mdlive.embedkit.uilayer.sav.MDLiveChooseProvider;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.CardIOPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetCreditCardInfoService;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MDLivePayment extends MDLiveBaseActivity {

    private TextView dateView;/*edtZipCode*/
    private int year, month;
    private String promoCode = null;
    private WebView HostedPCI;
    private HashMap<String, HashMap<String, String>> billingParams;
    private double payableAmount;
    private String finalAmount = "";
    private boolean setExistingCardDetailUser=false;
    JSONObject myProfile;
    Calendar expiryDate = Calendar.getInstance();
    private Button mScanCardBtn;
    private static String errorPhoneNumber=null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);
        clearMinimizedTime();
        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_payment_txt));
        mScanCardBtn = (Button)findViewById(R.id.ScanCardBtn);
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            finalAmount = String.format("%.2f", Double.parseDouble(extras.getString("final_amount")));
            storePayableAmount(finalAmount);
            ((TextView) findViewById(R.id.cost)).setText("$" + finalAmount);
        }
        ((RelativeLayout) findViewById(R.id.masterCardRl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete
                setExistingCardDetailUser=true;
                moveToNextPage();
//                getCreditCardInfoService();

            }
        });
        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (TextView) findViewById(R.id.edtExpiryDate);
        setProgressBar(findViewById(R.id.progressDialog));
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        billingParams = new HashMap<>();
      /*  //edtZipCode = (EditText) findViewById(R.id.edtZipCode);
        edtZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtZipCode.getText().toString().length() >= 9) {
                    if (!edtZipCode.getText().toString().contains("-")) {
                        String formattedString = MdliveUtils.zipCodeFormat(Long.parseLong(edtZipCode.getText().toString()));
                        edtZipCode.setText(formattedString);
                    }

                }
            }
        });*/
        HostedPCI.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (MDLiveConfig.CURRENT_ENVIRONMENT == MDLiveConfig.ENVIRON.PROD) {
            HostedPCI.loadUrl("file:///android_asset/htdocs/index_prod.html");
        } else {
            HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        }

        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
        mScanCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardIOPlugin.scanCard(MDLivePayment.this);
            }
        });
        getCreditCardInfoService();
    }

    public void rightBtnOnClick(View view){
     //Delete

        if (finalAmount.equals("0.00")) {
            //Remove this..it is in next screen
             //* doConfirmAppointment();*//*
            moveToNextPage();
        } else {
            HostedPCI.loadUrl("javascript:tokenizeForm()");
        }



    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLivePayment.this);
        onBackPressed();
    }

    public void getCreditCardInfoService() {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlegetCreditCardInfoSuccessResponse(response);

            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLivePayment.this);
                }
            }
        };

        GetCreditCardInfoService service = new GetCreditCardInfoService(MDLivePayment.this, null);
        service.getCreditCardInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetCreditCardInfoSuccessResponse(JSONObject response) {
        dismissDialog();
        Log.i("response", response.toString());
        try {
            if (response != null) {
                Log.e("inside","Am not in main Null");
                myProfile = response.getJSONObject("billing_information");
                Log.e("inside",myProfile.getString("cc_number"));
                if (myProfile.optBoolean("allow_cc_scan",false)){
                    mScanCardBtn.setVisibility(View.VISIBLE);
                }
                if (myProfile.getString("cc_number").equals(null)||myProfile.getString("cc_number").equals("null")||myProfile.getString("cc_number").equals("")||myProfile.getString("cc_number").isEmpty()
                        ) {
                    Log.e("inside","Am in Null");
                    ((RelativeLayout) findViewById(R.id.masterCardRl)).setVisibility(View.GONE);
                    ((LinearLayout) findViewById(R.id.parentMasterCardLl)).setVisibility(View.GONE);
                } else {
                    Log.e("inside","Am not in Null");
                    if(myProfile.getString("cc_type_id").equalsIgnoreCase("1")) {

                        ((TextView) findViewById(R.id.useMasterCardtxt)).setText(getString(R.string.mdl_visa_card_details) + " " + myProfile.getString("cc_number"));
                    }else if(myProfile.getString("cc_type_id").equalsIgnoreCase("3")) {

                        ((TextView) findViewById(R.id.useMasterCardtxt)).setText(getString(R.string.mdl_discover_card_details) + " " + myProfile.getString("cc_number"));
                    }
                    else if(myProfile.getString("cc_type_id").equalsIgnoreCase("5")) {
                        ((TextView) findViewById(R.id.useMasterCardtxt)).setText(getString(R.string.mdl_amex_card_details) + " " + myProfile.getString("cc_number"));
                    }

                    else
                    {
                        ((TextView) findViewById(R.id.useMasterCardtxt)).setText(getString(R.string.mdl_card_details) + " " + myProfile.getString("cc_number"));
                    }
                    ((RelativeLayout) findViewById(R.id.masterCardRl)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.parentMasterCardLl)).setVisibility(View.VISIBLE);
                }

                //this is called when master card image view is clicked
                if(setExistingCardDetailUser) {
                    setExistingCardDetailUser=false;
                    String params = getExistingBillingPutParams(response.toString());
                    updateCardDetails(params);
//                    HostedPCI.loadUrl("javascript:tokenizeForm()");
                }
//                getBillingPutParams(response.toString());
            }else
            {
                Log.e("inside","Am in main  Null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void sendToAndroid(String billingResponse) {
            // this is called from JS with passed value
            try {
                JSONObject jobj = new JSONObject(billingResponse);
                Log.e("token response",jobj.getString("status"));
                if(setExistingCardDetailUser)
                {
                    if (jobj.getString("status").equals("success")) {
                        String params = getExistingBillingPutParams(billingResponse);
                        updateCardDetails(params);
                        Log.e("get Existing params->",params);
                    } else {
                        MdliveUtils.alert(getProgressDialog(), MDLivePayment.this, jobj.getString("status"));
                    }
                }else
                {
                    if (jobj.getString("status").equals("success")) {
                        String params = getBillingPutParams(billingResponse);
                        updateCardDetails(params);
                        Log.e("print params->",params);
                    } else {
                        MdliveUtils.alert(getProgressDialog(), MDLivePayment.this, jobj.getString("status"));
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // This annotation is required in Jelly Bean and later:
        @JavascriptInterface
        public void scanCreditCard() {
        }
    }

    private void showDatePicker() {
        final Dialog d = new Dialog(this);
        d.setTitle(getString(R.string.mdl_expiration_month_year));
        d.setContentView(R.layout.monthly_picker_dialog);
        Button buttonDone = (Button) d.findViewById(R.id.set_button);
        final NumberPicker monthPicker = (NumberPicker) d.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = (NumberPicker) d.findViewById(R.id.year_picker);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("Values",""+newVal);
                Log.e("OldValues",""+oldVal);
                Calendar c = TimeZoneUtils.getCalendarWithOffset(MDLivePayment.this);
                int minimumYear = c.get(Calendar.YEAR);
                if(newVal!=minimumYear){
                    monthPicker.setMaxValue(12);
                    monthPicker.setMinValue(1);
                    monthPicker.setValue(c.get(Calendar.MONTH) + 1);
                }else{
                    monthPicker.setMaxValue(12);
                    monthPicker.setMinValue(c.get(Calendar.MONTH) + 1);
                    monthPicker.setValue(1);
                }
            }
        });

        monthPicker.setWrapSelectorWheel(true);
        try {
            Calendar c = TimeZoneUtils.getCalendarWithOffset(this);
            Date mDate = new Date();
            c.setTime(mDate);
            monthPicker.setMaxValue(12);
            monthPicker.setMinValue(c.get(Calendar.MONTH) + 1);
            monthPicker.setValue(c.get(Calendar.MONTH) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar c = TimeZoneUtils.getCalendarWithOffset(this);
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
                    expiryDate = TimeZoneUtils.getCalendarWithOffset(MDLivePayment.this);
                    expiryDate.set(year, month, 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
                    dateView.setText(dateFormat.format(expiryDate.getTime()));
                    d.dismiss();
                } catch (Exception e) {

                }
            }
        });

        monthPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

        d.findViewById(R.id.CancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
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

    public void updateCardDetails(final String params) {
        showProgressDialog();
        final NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                try {
                    dismissDialog();
                    JSONObject resObj = new JSONObject(response.toString());
                    Log.e("billing success res-->",response.toString());
                    if (resObj.has("message")) {
                //Remove this..it is in next screen
                           /* doConfirmAppointment();*/
                        CheckdoconfirmAppointment(true);
                        Intent i = new Intent(MDLivePayment.this, MDLiveConfirmappointment.class);
                        startActivity(i);
                        MdliveUtils.startActivityAnimation(MDLivePayment.this);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    dismissDialog();
                    Log.e("error", error.toString());
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.e("responseBody",responseBody);
                        JSONObject errorObj = new JSONObject(responseBody);
                        if (errorObj.has("message")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("message"));
                        } else if (errorObj.has("error")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("error"));
                        }
                    }

                    MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, getProgressDialog());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ConfirmAppointmentServices billingUpdateService = new ConfirmAppointmentServices(MDLivePayment.this, getProgressDialog());
        billingUpdateService.doUpdateBillingInformation(params, successListener, errorListener);

    }

    /**
     * This method will convert user card details and information in to Json Parameters
     *
     * @param billingDetails-Contains Billing details information as Json     *
     * @return-Billing parameter in a Json format
     */


    public String getBillingPutParams(String billingDetails) {
        try {
            JSONObject resObj = new JSONObject(billingDetails);
            Log.e("success res-->",resObj.toString());
            JSONObject billingObj = resObj.getJSONObject("billing_information");
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
            HashMap<String, String> cardInfo = new HashMap<>();
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            cardInfo.put("billing_name", settings.getString(PreferenceConstants.PATIENT_NAME, ""));
            cardInfo.put("billing_address1", userBasicInfo.getPersonalInfo().getAddress1());
            cardInfo.put("billing_address2", userBasicInfo.getPersonalInfo().getAddress2());
            cardInfo.put("billing_state_id", settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"));
            cardInfo.put("billing_city", userBasicInfo.getPersonalInfo().getCity());
            cardInfo.put("billing_country_id", "1");
            cardInfo.put("cc_num", billingObj.getString("cc_num"));
            cardInfo.put("cc_cvv2", billingObj.getString("cc_cvv2"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
            cardInfo.put("cc_expyear", sdf.format(expiryDate.getTime()));
            cardInfo.put("cc_expmonth", String.valueOf(month + 1));
            cardInfo.put("cc_hsa", billingObj.getString("cc_hsa"));
            cardInfo.put("billing_zip5", userBasicInfo.getPersonalInfo().getZipcode());
            cardInfo.put("cc_type_id", billingObj.getString("cc_type_id"));
            billingParams.put("billing_information", cardInfo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(billingParams);
    }

    public String getExistingBillingPutParams(String billingDetails) {
        try {
            JSONObject resObj = new JSONObject(billingDetails);
            Log.e("payment res-->",resObj.toString());
            JSONObject billingObj = resObj.getJSONObject("billing_information");
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
            HashMap<String, String> cardInfo = new HashMap<>();
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            cardInfo.put("billing_name", settings.getString(PreferenceConstants.PATIENT_NAME, ""));
            cardInfo.put("billing_address1",userBasicInfo.getPersonalInfo().getAddress1() );
            cardInfo.put("billing_address2",userBasicInfo.getPersonalInfo().getAddress1());
            cardInfo.put("billing_state_id", settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"));
            cardInfo.put("billing_city", userBasicInfo.getPersonalInfo().getCity());
            cardInfo.put("billing_country_id", "1");
            cardInfo.put("cc_num", billingObj.getString("cc_number"));
            cardInfo.put("cc_cvv2", billingObj.getString("cc_cvv2"));
            cardInfo.put("cc_expyear", billingObj.getString("cc_expyear"));
            cardInfo.put("cc_expmonth", billingObj.getString("cc_expmonth"));
            cardInfo.put("cc_hsa", billingObj.getString("cc_hsa"));
            cardInfo.put("billing_zip5", userBasicInfo.getPersonalInfo().getZipcode());
            cardInfo.put("cc_type_id", billingObj.getString("cc_type_id"));
            billingParams.put("billing_information", cardInfo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(billingParams);
    }

    public void storeOfferCode(String offercode) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.OFFER_CODE, offercode);
        editor.commit();
    }
    public void storePayableAmount(String amount) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.AMOUNT, amount);
        editor.commit();
    }
    public void CheckdoconfirmAppointment(boolean checkExixtingCard) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(PreferenceConstants.EXISTING_CARD_CHECK,checkExixtingCard);
        editor.commit();
    }


    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            try {
                year = selectedYear;
                month = selectedMonth;
                Calendar c = TimeZoneUtils.getCalendarWithOffset(MDLivePayment.this);
                c.set(selectedYear, selectedMonth, 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
                dateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLivePayment.this));

                SimpleDateFormat tmpDateFormat = new SimpleDateFormat("MM/yyyy");
                tmpDateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLivePayment.this));

                tmpDateFormat.parse(dateFormat.format(c.getTime())).compareTo(TimeZoneUtils.getCalendarWithOffset(MDLivePayment.this).getTime());
                dateView.setText(dateFormat.format(c.getTime()));
            } catch (Exception e) {

            }

        }
    };


    private void dismissDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    hideProgress();
                } catch (final Exception ex) {
                }
            }
        });
    }

    private void showProgressDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    showProgress();
                } catch (final Exception ex) {

                }
            }
        });
    }



    public void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLivePayment.this);
        // set title
        LayoutInflater inflater = MDLivePayment.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mdlive_popup, null);
        alertDialogBuilder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.offerCode);
        // set dialog message

        alertDialogBuilder.setCancelable(false).setPositiveButton(getString(R.string.mdl_apply), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editText.getText().toString().length() != IntegerConstants.NUMBER_ZERO) {
                    applyPromoCode(editText.getText().toString());
                    promoCode = editText.getText().toString();
                    storeOfferCode(promoCode);
                }


            }
        }).setNegativeButton(getString(R.string.mdl_cancel_upper), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (editText.hasFocus()) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

     /*   alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });*/
        // show it
        alertDialog.show();

    }

    public void applyPromoCode(final String promoCode) {
        showProgressDialog();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                dismissDialog();
                handlePromocodeResponse(response.toString(),promoCode);


            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
                MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, getProgressDialog());


            }
        };

        ConfirmAppointmentServices promocodeService = new ConfirmAppointmentServices(MDLivePayment.this, getProgressDialog());
        promocodeService.doGetPromocode(promoCode, successListener, errorListener);

    }

    public void handlePromocodeResponse(String response,String promoCode) {
        try {
            JSONObject resObject = new JSONObject(response);
            if (resObject.has("discount_amount")){
                checkInsuranceEligibility(promoCode);
            }

           /* if (resObject.has("discount_amount")) {
                String discountAmount = resObject.getString("discount_amount").replace("$", "");
                payableAmount = Double.parseDouble(finalAmout) - Double.parseDouble(discountAmount.trim());
                if (payableAmount <= 0.00) {
                    payableAmount = 0.00;
                    finalAmout = String.format("%.2f", payableAmount);
                    storePayableAmount(finalAmout);
                   *//* Delete it
                    doConfirmAppointment();*//*//Call the  confirm Appointment service if the user is Zero Dollar
                    CheckdoconfirmAppointment(true);

                } else {
                    finalAmout = String.format("%.2f", payableAmount);
                    storePayableAmount(finalAmout);
                }
                ((TextView) findViewById(R.id.cost)).setText("$" + finalAmout);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        try {
            if (getIntent() != null && getIntent().hasExtra("redirect_mypharmacy")) {
                Intent startMyPharmacyIntent = new Intent(getApplicationContext(), MDLivePharmacy.class);
                    startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if ((getIntent().getBooleanExtra("redirect_mypharmacy", false))) {
                    startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(startMyPharmacyIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
        MdliveUtils.closingActivityAnimation(this);
    }


    /**
     * This method will be called on clicking Next Button.
     * @param paymentButton--corresponding view to be passed
     */


    public void payNow(View paymentButton) {
        if (finalAmount.equals("0.00")) {
            //Remove this..it is in next screen
                           /* doConfirmAppointment();*/;
            CheckdoconfirmAppointment(true);
        } else {
            if (dateView.getText().toString().length() != IntegerConstants.NUMBER_ZERO) {
                HostedPCI.loadUrl("javascript:tokenizeForm()");
            } else {
                MdliveUtils.alert(getProgressDialog(), MDLivePayment.this, getString(R.string.mdl_please_fill_required_fields));

            }
        }
    }

    /**
     * This method will be called on clicking apply Offer code text.
     * @param offerCodeView--corresponding view to be passed
     */

    public void applyOfferCode(View offerCodeView) {
        showDialog();
    }






    /**
     * This method handles checks user insurance eligibility and return the final amount for the user.
     * successListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * PharmacyService-Class will send the request to the server and get the responses
     * doPostCheckInsulranceEligibility-Method will carries required parameters for sending request to the server.
     */

    public void checkInsuranceEligibility(String promocode) {
        showProgress();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                Log.e("Zero Dollar Insurance", response.toString());
                try {
                    JSONObject jobj = new JSONObject(response.toString());
                    if (jobj.has("final_amount")) {
                        String discountAmount = jobj.getString("final_amount").replace("$", "");
                        payableAmount = Double.parseDouble(discountAmount);
                        if (payableAmount <= 0.00) {
                            finalAmount = String.format("%.2f", Double.parseDouble(jobj.getString("final_amount")));
                            storePayableAmount(finalAmount);
                            moveToNextPage();

                        }
                        else{
                            finalAmount = String.format("%.2f", payableAmount);
                            storePayableAmount(finalAmount);

                        }
                    }
                    ((TextView) findViewById(R.id.cost)).setText("$" + finalAmount);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, null);
            }
        };
        PharmacyService insuranceService = new PharmacyService(MDLivePayment.this, null);
        insuranceService.doPostCheckInsulranceEligibility(formPostInsuranceParams(promocode), successListener, errorListener);
    }
    // This is For navigating to the next Screen

    private void moveToNextPage() {
        CheckdoconfirmAppointment(true);
        Intent i = new Intent(MDLivePayment.this, MDLiveConfirmappointment.class);
        startActivity(i);
        MdliveUtils.startActivityAnimation(MDLivePayment.this);
    }

    /**
     * This function is used to get post body content for Check Insurance Eligibility
     * Values hard coded are default criteria from get response of Insurance Eligibility of all users.
     */

    public String formPostInsuranceParams(String promoCode) {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, String> insuranceMap = new HashMap<>();
        insuranceMap.put("appointment_method", "1");
        insuranceMap.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        insuranceMap.put("timeslot", "Now");
        insuranceMap.put("provider_type_id",settings.getString(PreferenceConstants.PROVIDERTYPE_ID, ""));
        insuranceMap.put("promocode", promoCode);
        insuranceMap.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        Log.e("insurance Map",insuranceMap.toString());
        return new Gson().toJson(insuranceMap);
    }


    public void showAlertPopup(String errorMessage){
        try {
            Log.e("Alert","Cominr Alert");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLivePayment.this);
            if(errorPhoneNumber==null){
                alertDialogBuilder
                        .setTitle("")
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.mdl_Ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }else{
                alertDialogBuilder
                        .setTitle("")
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton(StringConstants.ALERT_CALLNOW, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("Phone Inside",errorPhoneNumber);
                                if (errorPhoneNumber != null) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + errorPhoneNumber.replaceAll("-", "")));
                                    startActivity(intent);
                                    MdliveUtils.startActivityAnimation(MDLivePayment.this);
                                }else{
                                    dialog.dismiss();
                                }


                            }
                        }).setNegativeButton(StringConstants.ALERT_DISMISS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MDLiveChooseProvider.isDoctorOnCall=false;
                        MDLiveChooseProvider.isDoctorOnVideo=false;
                        dialog.dismiss();
                       /* Intent intent = new Intent();
                        intent.setAction("com.mdlive.embedkit.HOME_PRESSED");
                        sendBroadcast(intent);*/
                    }
                });
            }



            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
//                    alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                    if(errorPhoneNumber!=null){
//                        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                    }
                }
            });

            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IdConstants.CREDITCARD_SCAN) {
            String resultStr = "";
            if (intent != null && intent.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT) && requestCode != Activity.RESULT_CANCELED) {
                CreditCard scanResult = intent.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                resultStr = scanResult.cardNumber;
                String javascriptString = "javascript:setCardNumber('" + resultStr + "');";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    HostedPCI.evaluateJavascript(javascriptString, null);
                } else {
                    HostedPCI.loadUrl(javascriptString);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }


}
