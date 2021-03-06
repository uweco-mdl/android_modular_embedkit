package com.mdlive.myaccounts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.myaccounts.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.CardIOPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddCreditCardInfoService;
import com.mdlive.unifiedmiddleware.services.myaccounts.ReplaceCreditCardService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by venkataraman_r on 6/22/2015.
 */
public class CreditCardInfoFragment extends MDLiveBaseFragment {

    //    private EditText mCardNumber = null;
//    private EditText mSecurityCode = null;
    private TextView mCardExpirationMonth = null;
    private EditText mNameOnCard = null;
    private EditText mAddress1 = null;
    private EditText mAddress2 = null;
    private EditText mCity = null;
    private TextView mState = null;
    private EditText mZip = null;

    private String cardExpirationMonth = null;
    private String cardExpirationYear = null;
    private String nameOnCard = null;
    private String address1 = null;
    private String address2 = null;
    private String city = null;
    private String state = null;
    private String country = null;
    private String zip = null;
    RelativeLayout mAddressVisibility,mStateLayout;
    private WebView myAccountHostedPCI;
    //private Button mScanCardBtn;

    private int year, month;
    Calendar expiryDate = Calendar.getInstance();

    public static CreditCardInfoFragment newInstance(String response, String view) {
        final CreditCardInfoFragment cardInfo = new CreditCardInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Response", response);
        bundle.putString("View", view);
        cardInfo.setArguments(bundle);
        return cardInfo;
    }

    public CreditCardInfoFragment() {
        super();
    }

    String response;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View billingInformation = inflater.inflate(R.layout.fragments_billing_info, null);
        mZip = (EditText) billingInformation.findViewById(R.id.zip);
        mZip.setTag(null);
        mZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MdliveUtils.validateZipcodeFormat(mZip);
            }
        });

        init(billingInformation);
        myAccountHostedPCI.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            myAccountHostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (MDLiveConfig.CURRENT_ENVIRONMENT == MDLiveConfig.ENVIRON.PROD) {
            myAccountHostedPCI.loadUrl("file:///android_asset/htdocs/index_prod.html");
        } else {
            myAccountHostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        }
        myAccountHostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");
/*        mScanCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardIOPlugin.scanCard(getActivity());
            }
        });*/
        return billingInformation;

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
                    JSONObject billingObj = jobj.getJSONObject("billing_information");
                    addCreditCardInfo(billingObj.getString("cc_num"),billingObj.getString("cc_cvv2"),billingObj.getString("cc_hsa"),billingObj.getString("cc_type_id"));
                } else {
                    MdliveUtils.alert(getProgressDialog(), getActivity(), jobj.getString("status"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // This annotation is required in JellyBean and later:
        @JavascriptInterface
        public void scanCreditCard() {
        }
    }

    public void callHpci() {
        myAccountHostedPCI.loadUrl("javascript:tokenizeForm()");
    }

    public void init(View billingInformation) {

        mNameOnCard = (EditText) billingInformation.findViewById(R.id.nameOnCard);
        mAddress1 = (EditText) billingInformation.findViewById(R.id.addressLine1);
        mAddress2 = (EditText) billingInformation.findViewById(R.id.addressLine2);
        mCity = (EditText) billingInformation.findViewById(R.id.city);
        mState = (TextView) billingInformation.findViewById(R.id.state);
        mZip = (EditText) billingInformation.findViewById(R.id.zip);
        mCardExpirationMonth = (TextView) billingInformation.findViewById(R.id.expirationDate);

        SwitchCompat changeAddress = (SwitchCompat) billingInformation.findViewById(R.id.addressChange);
        changeAddress.setChecked(false);
        mAddressVisibility = (RelativeLayout) billingInformation.findViewById(R.id.addressVisibility);
        myAccountHostedPCI = (WebView) billingInformation.findViewById(R.id.myAccountHostedPCI);
        mStateLayout = (RelativeLayout)billingInformation.findViewById(R.id.stateLayout);
        //mScanCardBtn = (Button)billingInformation.findViewById(R.id.ScanCardBtn);
        mNameOnCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MdliveUtils.showSoftKeyboard(getActivity(), mNameOnCard);
                }
            }
        });

        mAddress1.setText("");
        mAddress2.setText("");
        mCity.setText("");
        mState.setText("");
        mZip.setText("");

        changeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    SharedPreferences prefs = getActivity().getSharedPreferences("ADDRESS_CHANGE", Context.MODE_PRIVATE);

                    String name = prefs.getString("Profile_Address", "");
                    try {
                        JSONObject myProfile = new JSONObject(name);
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("address1"))) {
                            mAddress1.setText("");
                        } else {
                            mAddress1.setText(myProfile.getString("address1").trim());
                        }

                        if (MdliveUtils.checkIsEmpty(myProfile.getString("address2"))) {
                            mAddress2.setText("");
                        } else {
                            mAddress2.setText(myProfile.getString("address2").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("state"))) {
                            mState.setText("");
                        } else {
                            mState.setText(myProfile.getString("state").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("city"))) {
                            mCity.setText("");
                        } else {
                            mCity.setText(myProfile.getString("city").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("zipcode"))) {
                            mZip.setText("");
                        } else {
                            mZip.setText(myProfile.getString("zipcode").trim());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
//                    mAddress1.setText(address1);
//                    mAddress2.setText(address2);
//                    mCity.setText(city);
//                    mState.setText(state);
//                    mZip.setText(zip);
                    mAddress1.setText("");
                    mAddress2.setText("");
                    mCity.setText("");
                    mState.setText("");
                    mZip.setText("");
                }
            }
        });
        response = getArguments().getString("Response");
        if (getArguments().getString("View").equalsIgnoreCase("view") || getArguments().getString("View").equalsIgnoreCase("replace")) {
            if (response != null) {

//                if (getArguments().getString("View").equalsIgnoreCase("view")) {
//                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
//                        ((MyAccountsHome) getActivity()).hideTick();
//                    }
//                    mCardExpirationMonth.setEnabled(false);
//                    mNameOnCard.setEnabled(false);
//                    mAddress1.setEnabled(false);
//                    mAddress2.setEnabled(false);
//                    mCity.setEnabled(false);
//                    mStateLayout.setEnabled(false);
//                    mZip.setEnabled(false);
//                    changeAddress.setEnabled(false);
//                }

                try {
                    JSONObject myProfile = new JSONObject(response);
                    country = myProfile.getString("billing_country");
                    cardExpirationYear = myProfile.getString("cc_expyear");
                    nameOnCard = myProfile.getString("billing_name");
                    zip = myProfile.getString("billing_zip5");
                    state = myProfile.getString("billing_state");
                    address2 = myProfile.getString("billing_address2");
                    city = myProfile.getString("billing_city");
                    address1 = myProfile.getString("billing_address1");
                    cardExpirationMonth = myProfile.getString("cc_expmonth");
/*                    if (myProfile.optBoolean("allow_cc_scan", false)){
                        mScanCardBtn.setVisibility(View.VISIBLE);
                    }*/



//                    mCardNumber.setText(cardNumber);
//                    mSecurityCode.setText(securityCode);
                    mCardExpirationMonth.setText(cardExpirationMonth+"/"+cardExpirationYear);
                    if (MdliveUtils.checkIsEmpty(nameOnCard)) {
                        nameOnCard = "";
                        mNameOnCard.setText(nameOnCard);
                    } else {
                        mNameOnCard.setText(nameOnCard);
                    }

                    if (MdliveUtils.checkIsEmpty(address1)) {
                        address1 = "";
                        mAddress1.setText(address1);
                    } else {
                        mAddress1.setText(address1);
                    }

                    if (MdliveUtils.checkIsEmpty(address2)) {
                        address2 = "";
                        mAddress2.setText(address2);
                    } else {
                        mAddress2.setText(address2);
                    }

                    if (MdliveUtils.checkIsEmpty(city)) {
                        city = "";
                        mCity.setText(city);
                    } else {
                        mCity.setText(city);
                    }

                    if (MdliveUtils.checkIsEmpty(state)) {
                        state = "";
                        mState.setText(state);
                    } else {
                        mState.setText(state);
                    }
                    if (MdliveUtils.checkIsEmpty(zip)) {
                        zip = "";
                        mZip.setText(zip);
                    } else {
                        mZip.setText(zip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }else if(response!=null){
            try {
                JSONObject myProfile = new JSONObject(response);
                /*if (myProfile.optBoolean("allow_cc_scan", false)) {
                    mScanCardBtn.setVisibility(View.VISIBLE);
                }*/
            } catch(Exception e){
                e.printStackTrace();
            }
        }


        mCardExpirationMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mStateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });

        changeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    SharedPreferences prefs = getActivity().getSharedPreferences("ADDRESS_CHANGE", Context.MODE_PRIVATE);

                    String name = prefs.getString("Profile_Address", "");
                    try {
                        JSONObject myProfile = new JSONObject(name);
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("address1"))) {
                            mAddress1.setText("");
                        } else {
                            mAddress1.setText(myProfile.getString("address1").trim());
                        }

                        if (MdliveUtils.checkIsEmpty(myProfile.getString("address2"))) {
                            mAddress2.setText("");
                        } else {
                            mAddress2.setText(myProfile.getString("address2").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("state"))) {
                            mState.setText("");
                        } else {
                            mState.setText(myProfile.getString("state").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("city"))) {
                            mCity.setText("");
                        } else {
                            mCity.setText(myProfile.getString("city").trim());
                        }
                        if (MdliveUtils.checkIsEmpty(myProfile.getString("zipcode"))) {
                            mZip.setText("");
                        } else {
                            mZip.setText(myProfile.getString("zipcode").trim());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
//                    mAddress1.setText(address1);
//                    mAddress2.setText(address2);
//                    mCity.setText(city);
//                    mState.setText(state);
//                    mZip.setText(zip);
                    mAddress1.setText("");
                    mAddress2.setText("");
                    mCity.setText("");
                    mState.setText("");
                    mZip.setText("");
                }
            }
        });
    }
    private void showDatePicker() {
        final Dialog d = new Dialog(getActivity());
        d.setTitle(getString(R.string.mdl_expiration_month_year));
        d.setContentView(R.layout.monthly_picker_dialog);
        Button buttonDone = (Button) d.findViewById(R.id.set_button);
        final NumberPicker monthPicker = (NumberPicker) d.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = (NumberPicker) d.findViewById(R.id.year_picker);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v("Values",""+newVal);
                Log.v("OldValues",""+oldVal);
                Calendar c = TimeZoneUtils.getCalendarWithOffset(getActivity());
//                c.setTimeZone("");
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
            Calendar c = TimeZoneUtils.getCalendarWithOffset(getActivity());
            Date mDate = c.getTime();
            c.setTime(mDate);
            monthPicker.setMaxValue(12);
            monthPicker.setMinValue(c.get(Calendar.MONTH) + 1);
            monthPicker.setValue(c.get(Calendar.MONTH) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar c = TimeZoneUtils.getCalendarWithOffset(getActivity());
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
                    expiryDate = TimeZoneUtils.getCalendarWithOffset(getActivity());
                    expiryDate.set(year, month, 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
                    dateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(getActivity()));
                    cardExpirationMonth = String.valueOf(month);
                    cardExpirationYear = String.valueOf(year);
                    mCardExpirationMonth.setText(dateFormat.format(expiryDate.getTime()));
                    d.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
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
    public void addCreditCardInfo(String cardNumber,String cvv,String isHSA,String ccTypeId) {
//        cardNumber = mCardNumber.getText().toString();
//        securityCode = mSecurityCode.getText().toString();
        nameOnCard = mNameOnCard.getText().toString().trim();
        address1 = mAddress1.getText().toString().trim();
        address2 = mAddress2.getText().toString().trim();
        city = mCity.getText().toString().trim();
        state = mState.getText().toString();
        cardExpirationMonth = mCardExpirationMonth.getText().toString();
        country = "1";
        zip = mZip.getText().toString().trim();

        if (isEmpty(cardExpirationMonth) && isEmpty(cardExpirationYear) && isEmpty(nameOnCard) && isEmpty(address1) && isEmpty(city) && isEmpty(state) && isEmpty(zip) && isEmpty(cardExpirationMonth) && isEmpty(country)) {
            if(!MdliveUtils.validateZipCode(zip)){
                MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_valid_zip), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }else {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cc_type_id", ccTypeId);
                jsonObject.put("billing_address1", address1);
                jsonObject.put("billing_zip5", zip);
                jsonObject.put("billing_address2", address2);
                jsonObject.put("cc_hsa", Boolean.valueOf(isHSA));
                jsonObject.put("cc_expyear", cardExpirationYear);
                jsonObject.put("billing_name", nameOnCard);
                jsonObject.put("billing_city", city);
                jsonObject.put("billing_state_id", state);
                jsonObject.put("cc_expmonth", cardExpirationMonth);
                jsonObject.put("cc_cvv2", cvv);
                jsonObject.put("cc_num", cardNumber);
                jsonObject.put("billing_country_id", country);

                parent.put("billing_information", jsonObject);
                loadBillingInfo(parent.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                }
            }
        } else {
//            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_all_fields_required), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        }
    }

    public Boolean isEmpty(String cardInfo) {
        return !TextUtils.isEmpty(cardInfo);
    }

    private void loadBillingInfo(String params) {
        showDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddBillingInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
                try {
                    MdliveUtils.handleVolleyErrorResponse(getActivity(), error, getProgressDialog());
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        if (getArguments().getString("View").equalsIgnoreCase("view") || getArguments().getString("View").equalsIgnoreCase("replace")) {
            ReplaceCreditCardService service = new ReplaceCreditCardService(getActivity(), null);
            service.replaceCreditCardInfo(successCallBackListener, errorListener, params);
        } else {
            AddCreditCardInfoService service = new AddCreditCardInfoService(getActivity(), null);
            service.addCreditCardInfo(successCallBackListener, errorListener, params);
        }
    }

    private void handleAddBillingInfoSuccessResponse(JSONObject response) {
        try {
            dismissDialog();
//            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            MdliveUtils.showDialog(getActivity(), response.getString("message"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
        final List<String> stateIds = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));

        final String[] stringArray = stateList.toArray(new String[stateList.size()]);

        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String SelectedText = stateIds.get(i);
                mState.setText(SelectedText);
                dialogInterface.dismiss();

            }
        });
        builder.show();
    }

    private void dismissDialog() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    hideProgressDialog();
                } catch (final Exception ex) {
                }
            }
        });
    }

    public void showDialog() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    showProgressDialog();
                } catch (final Exception ex) {

                }
            }
        });
    }

    protected void setCardNumber(String number){
        String javascriptString = "javascript:setCardNumber('"+ number + "');";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            myAccountHostedPCI.evaluateJavascript(javascriptString,null);
        } else {
            myAccountHostedPCI.loadUrl(javascriptString);
        }
    }
}
