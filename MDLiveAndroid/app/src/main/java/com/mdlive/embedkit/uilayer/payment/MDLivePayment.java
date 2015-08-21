package com.mdlive.embedkit.uilayer.payment;


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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacy;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MDLivePayment extends MDLiveBaseActivity {

    private EditText dateView;/*edtZipCode*/
    private int year, month;
    private String promoCode = null;
    private WebView HostedPCI;
    protected static ProgressDialog pDialog;
    private HashMap<String, HashMap<String, String>> billingParams;
    private double payableAmount;
    private String finalAmout = "";
    Calendar expiryDate = Calendar.getInstance();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_payment_activity);
      if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            finalAmout = String.format("%.2f", Double.parseDouble(extras.getString("final_amount")));
            storePayableAmount(finalAmout);
            ((TextView) findViewById(R.id.cost)).setText("$" + finalAmout);
        }
        HostedPCI = (WebView) findViewById(R.id.HostedPCI);
        dateView = (EditText) findViewById(R.id.edtExpiryDate);
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
        if (MdliveUtils.ssoInstance.getCurrentEnvironment() == 4) {
            HostedPCI.loadUrl("file:///android_asset/htdocs/index_prod.html");
        } else {
            HostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        }
        HostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");

        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                if (jobj.getString("status").equals("success")) {
                    String params = getBillingPutParams(billingResponse);
                    updateCardDetails(params);
                } else {
                    MdliveUtils.alert(pDialog, MDLivePayment.this, jobj.getString("status"));
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
        d.setTitle(getString(R.string.expiration_month_year));
        d.setContentView(R.layout.monthly_picker_dialog);
        Button buttonDone = (Button) d.findViewById(R.id.set_button);
        final NumberPicker monthPicker = (NumberPicker) d.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = (NumberPicker) d.findViewById(R.id.year_picker);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("Values",""+newVal);
                Log.e("OldValues",""+oldVal);
                Calendar c = Calendar.getInstance();
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
            Calendar c = Calendar.getInstance();
            Date mDate = new Date();
            c.setTime(mDate);
            monthPicker.setMaxValue(12);
            monthPicker.setMinValue(c.get(Calendar.MONTH) + 1);
            monthPicker.setValue(c.get(Calendar.MONTH) + 1);
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
                    dateView.setText(dateFormat.format(expiryDate.getTime()));
                    d.dismiss();
                } catch (Exception e) {

                }
            }
        });

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

    public void updateCardDetails(String params) {
        showProgressDialog();
        final NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                try {
                    dismissDialog();
                    JSONObject resObj = new JSONObject(response.toString());
                    if (resObj.has("message")) {
                        doConfirmAppointment();
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
                    MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, pDialog);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ConfirmAppointmentServices billingUpdateService = new ConfirmAppointmentServices(MDLivePayment.this, pDialog);
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
            JSONObject billingObj = resObj.getJSONObject("billing_information");
            HashMap<String, String> cardInfo = new HashMap<>();
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            cardInfo.put("billing_name", settings.getString(PreferenceConstants.PATIENT_NAME, ""));
            cardInfo.put("billing_address1", "test1");
            cardInfo.put("billing_address2", "test");
            cardInfo.put("billing_state_id", settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL"));
            cardInfo.put("billing_city", "Test");
            cardInfo.put("billing_country_id", "1");
            cardInfo.put("cc_num", billingObj.getString("cc_num"));
            cardInfo.put("cc_cvv2", billingObj.getString("cc_cvv2"));
            cardInfo.put("cc_expyear", new SimpleDateFormat("yyyy").format(expiryDate.getTime()));
            cardInfo.put("cc_expmonth", String.valueOf(month + 1));
            cardInfo.put("cc_hsa", billingObj.getString("cc_hsa"));
            //cardInfo.put("billing_zip5", edtZipCode.getText().toString());
            cardInfo.put("cc_type_id", billingObj.getString("cc_type_id"));
            billingParams.put("billing_information", cardInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(billingParams);
    }


    public void storePayableAmount(String amount) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.AMOUNT, amount);
        editor.commit();
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            try {
                year = selectedYear;
                month = selectedMonth;
                Calendar c = Calendar.getInstance();
                c.set(selectedYear, selectedMonth, 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
                new SimpleDateFormat("MM/yyyy").parse(dateFormat.format(c.getTime())).compareTo(new Date());
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


    private void doConfirmAppointment() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog();
                try {
                    String apptId = response.getString("appointment_id");
                    if (apptId != null) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, apptId);
                        editor.commit();
                        Intent i = new Intent(MDLivePayment.this, MDLiveWaitingRoom.class);
                        startActivity(i);
                        MdliveUtils.startActivityAnimation(MDLivePayment.this);
                    } else {
                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    dismissDialog();
                    MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, pDialog);
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("appointment_method", "1");
        params.put("alternate_visit_option", "No Answer");
        params.put("phys_availability_id", "");
        params.put("timeslot", "Now");
        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON, "Not Sure"));
        params.put("customer_call_in_number", settings.getString(PreferenceConstants.PHONE_NUMBER, ""));
        params.put("do_you_have_primary_care_physician", "No");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        if (promoCode != null && !promoCode.isEmpty()) {
            params.put("promocode", promoCode);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLivePayment.this, null);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
    }


    public void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLivePayment.this);
        // set title
        LayoutInflater inflater = MDLivePayment.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mdlive_popup, null);
        alertDialogBuilder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.offerCode);
        // set dialog message

        alertDialogBuilder.setCancelable(false).setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editText.getText().toString().length() != IntegerConstants.NUMBER_ZERO) {
                    applyPromoCode(editText.getText().toString());
                    promoCode = editText.getText().toString();
                }


            }
        }).setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        // show it
        alertDialog.show();

    }

    public void applyPromoCode(String promoCode) {
        showProgressDialog();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                dismissDialog();
                handlePromocodeResponse(response.toString());

            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
                MdliveUtils.handelVolleyErrorResponse(MDLivePayment.this, error, pDialog);


            }
        };

        ConfirmAppointmentServices promocodeService = new ConfirmAppointmentServices(MDLivePayment.this, pDialog);
        promocodeService.doGetPromocode(promoCode, successListener, errorListener);

    }

    public void handlePromocodeResponse(String response) {
        try {
            JSONObject resObject = new JSONObject(response);
            if (resObject.has("discount_amount")) {
                String discountAmount = resObject.getString("discount_amount").replace("$", "");
                payableAmount = Double.parseDouble(finalAmout) - Double.parseDouble(discountAmount.trim());
                if (payableAmount <= 0.00) {
                    payableAmount = 0.00;
                    finalAmout = String.format("%.2f", payableAmount);
                    storePayableAmount(finalAmout);
                    doConfirmAppointment();//Call the  confirm Appointment service if the user is Zero Dollar

                } else {
                    finalAmout = String.format("%.2f", payableAmount);
                    storePayableAmount(finalAmout);
                }
                ((TextView) findViewById(R.id.cost)).setText("$" + finalAmout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        Intent startMyPharmacyIntent = new Intent(getApplicationContext(), MDLivePharmacy.class);
        startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            if (getIntent() != null && getIntent().hasExtra("redirect_mypharmacy")) {
                if ((getIntent().getBooleanExtra("redirect_mypharmacy", false))) {
                    startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(startMyPharmacyIntent);
        finish();
        MdliveUtils.closingActivityAnimation(this);
    }


    /**
     * This method will be called on clicking Next Button.
     * @param paymentButton--corresponding view to be passed
     */


    public void payNow(View paymentButton) {
        if (finalAmout.equals("0.00")) {
            doConfirmAppointment();
        } else {
            /*if (edtZipCode.getText().toString().length() != IntegerConstants.NUMBER_ZERO && dateView.getText().toString().length() != IntegerConstants.NUMBER_ZERO) {
                if (MdliveUtils.validateZipCode(edtZipCode.getText().toString())) {
                    HostedPCI.loadUrl("javascript:tokenizeForm()");
                } else {
                    MdliveUtils.alert(pDialog, MDLivePayment.this, getString(R.string.please_enter_valid_zipcode));
                }

            } else {
                MdliveUtils.alert(pDialog, MDLivePayment.this, getString(R.string.please_fill_required_fields));

            }*/
        }
    }

    /**
     * This method will be called on clicking apply Offer code text.
     * @param offerCodeView--corresponding view to be passed
     */

    public void applyOfferCode(View offerCodeView) {
        showDialog();
    }


}
