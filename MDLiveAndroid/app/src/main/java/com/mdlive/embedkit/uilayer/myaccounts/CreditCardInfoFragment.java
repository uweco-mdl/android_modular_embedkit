package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
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

    private String cardNumber = null;
    private String securityCode = null;
    private String cardExpirationMonth = null;
    private String cardExpirationYear = null;
    private String nameOnCard = null;
    private String address1 = null;
    private String address2 = null;
    private String city = null;
    private String state = null;
    private String country = null;
    private String zip = null;
    private SwitchCompat changeAddress;
    RelativeLayout mAddressVisibility,mStateLayout;
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private WebView myAccountHostedPCI;

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

        init(billingInformation);
        myAccountHostedPCI.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            myAccountHostedPCI.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        myAccountHostedPCI.loadUrl("file:///android_asset/htdocs/index.html");
        myAccountHostedPCI.addJavascriptInterface(new IJavascriptHandler(), "billing");

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
                    addCreditCardInfo();
                } else {
                    MdliveUtils.alert(getProgressDialog(), getActivity(), jobj.getString("status"));
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

    public void init(View billingInformation) {

        mNameOnCard = (EditText) billingInformation.findViewById(R.id.nameOnCard);
        mAddress1 = (EditText) billingInformation.findViewById(R.id.addressLine1);
        mAddress2 = (EditText) billingInformation.findViewById(R.id.addressLine2);
        mCity = (EditText) billingInformation.findViewById(R.id.city);
        mState = (TextView) billingInformation.findViewById(R.id.state);
        mZip = (EditText) billingInformation.findViewById(R.id.zip);
        mCardExpirationMonth = (TextView) billingInformation.findViewById(R.id.expirationDate);

        changeAddress = (SwitchCompat) billingInformation.findViewById(R.id.addressChange);
        mAddressVisibility = (RelativeLayout) billingInformation.findViewById(R.id.addressVisibility);
        myAccountHostedPCI = (WebView) billingInformation.findViewById(R.id.myAccountHostedPCI);
        mStateLayout = (RelativeLayout)billingInformation.findViewById(R.id.stateLayout);
        changeAddress.setChecked(false);



        if (getArguments().getString("View").equalsIgnoreCase("view") || getArguments().getString("View").equalsIgnoreCase("replace")) {
            response = getArguments().getString("Response");
            if (response != null) {
                if (getArguments().getString("View").equalsIgnoreCase("view")) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                    mCardExpirationMonth.setEnabled(false);
                    mNameOnCard.setEnabled(false);
                    mAddress1.setEnabled(false);
                    mAddress2.setEnabled(false);
                    mCity.setEnabled(false);
                    mStateLayout.setEnabled(false);
                    mZip.setEnabled(false);
                    changeAddress.setEnabled(false);
                }

                try {
                    Log.i("response", response);
                    JSONObject myProfile = new JSONObject(response);
                    country = myProfile.getString("billing_country");
                    cardExpirationYear = myProfile.getString("cc_expyear");
                    nameOnCard = myProfile.getString("billing_name");
                    zip = myProfile.getString("billing_zip5");
                    securityCode = myProfile.getString("cc_cvv2");
                    cardNumber = myProfile.getString("cc_number");
                    state = myProfile.getString("billing_state");
                    address2 = myProfile.getString("billing_address2");
                    city = myProfile.getString("billing_city");
                    address1 = myProfile.getString("billing_address1");
                    cardExpirationMonth = myProfile.getString("cc_expmonth");

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
                    mAddress1.setText(address1);
                    mAddress2.setText(address2);
                    mCity.setText(city);
                    mState.setText(state);
                    mZip.setText(zip);
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
                    cardExpirationMonth = String.valueOf(month);
                    cardExpirationYear = String.valueOf(year);
                    mCardExpirationMonth.setText(dateFormat.format(expiryDate.getTime()));
                    d.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
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
    public void addCreditCardInfo() {
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
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cc_type_id", 2);
                jsonObject.put("billing_address1", address1);
                jsonObject.put("billing_zip5", zip);
                jsonObject.put("billing_address2", address2);
                jsonObject.put("cc_hsa", true);
                jsonObject.put("cc_expyear", cardExpirationYear);
                jsonObject.put("billing_name", nameOnCard);
                jsonObject.put("billing_city", city);
                jsonObject.put("billing_state_id", state);
                jsonObject.put("cc_expmonth", cardExpirationMonth);
                jsonObject.put("cc_cvv2", "123");
                jsonObject.put("cc_num", "4111111111111111");
                jsonObject.put("billing_country_id", country);

                parent.put("billing_information", jsonObject);
                loadBillingInfo(parent.toString());
                Log.i("ADD Credit Card", parent.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo) {
        if (!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    private void loadBillingInfo(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddBillingInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
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
            hideProgressDialog();

            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        stateList = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));

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

}
