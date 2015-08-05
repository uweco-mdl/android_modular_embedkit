package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddCreditCardInfoService;
import com.mdlive.unifiedmiddleware.services.myaccounts.ReplaceCreditCardService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by venkataraman_r on 6/22/2015.
 */
public class CreditCardInfoFragment extends Fragment{

    private EditText mCardNumber = null;
    private EditText mSecurityCode = null;
    private EditText mCardExpirationMonth = null;
    private EditText mNameOnCard = null;
    private EditText mAddress1 = null;
    private EditText mAddress2 = null;
    private EditText mCity = null;
    private EditText mState = null;
    private EditText mZip = null;
    private Button mSave = null;
    SharedPreferences sharedpreferences;
    private ProgressDialog pDialog;

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

    public static CreditCardInfoFragment newInstance(String response) {
        final CreditCardInfoFragment cardInfo = new CreditCardInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Response", response);
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

        View billingInformation = inflater.inflate(R.layout.fragments_billing_info,null);

        init(billingInformation);

        return billingInformation;

    }

    public void init(View billingInformation)
    {
        mCardNumber = (EditText)billingInformation.findViewById(R.id.edt_cardNumber);
        mSecurityCode = (EditText)billingInformation.findViewById(R.id.edt_securityCode);
        mCardExpirationMonth = (EditText)billingInformation.findViewById(R.id.edt_cardExpirationMonth);
        mCardExpirationMonth.setCursorVisible(false);
        mNameOnCard = (EditText)billingInformation.findViewById(R.id.edt_nameOnCard);
        mAddress1 = (EditText)billingInformation.findViewById(R.id.edt_address1);
        mAddress2 = (EditText)billingInformation.findViewById(R.id.edt_address2);
        mCity = (EditText)billingInformation.findViewById(R.id.edt_city);
        mState = (EditText)billingInformation.findViewById(R.id.edt_state);
        mZip = (EditText)billingInformation.findViewById(R.id.edt_zipCode);
        mSave = (Button)billingInformation.findViewById(R.id.btn_save);

        sharedpreferences = getActivity().getSharedPreferences("MDLIVE_BILLING", Context.MODE_PRIVATE);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

         response = getArguments().getString("Response");
        if(response != null)
        {
            try {
                Log.i("response",response);
                JSONObject myProfile = new JSONObject(response);
//                Log.i("response",jsonObject.toString());
//                JSONObject myProfile = jsonObject.getJSONObject("billing_information");
                country = myProfile.getString("billing_country");
                cardExpirationYear = myProfile.getString("cc_expyear");
                nameOnCard = myProfile.getString("billing_name");
                zip = myProfile.getString("billing_zip5");
                securityCode = myProfile.getString("cc_cvv2");
                cardNumber = myProfile.getString("cc_number");
                state = myProfile.getString("billing_state");
//            mobile = myProfile.getString("cc_type_id");
                address2 = myProfile.getString("billing_address2");
                city = myProfile.getString("billing_city");
                address1 = myProfile.getString("billing_address1");
                cardExpirationMonth = myProfile.getString("cc_expmonth");

                mCardNumber.setText(cardNumber);
                mSecurityCode.setText(securityCode);
                mCardExpirationMonth.setText(cardExpirationMonth);
                mNameOnCard.setText(nameOnCard);
                mAddress1.setText(address1);
                mAddress2.setText(address2);
                mCity.setText(city);
                mState.setText(state);
                mZip.setText(zip);

                mSave.setText("Replace");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCreditCardInfo();
            }
        });

        mCardExpirationMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR)+4;
                int m = c.get(Calendar.MONTH)-2;
                int d = c.get(Calendar.DAY_OF_MONTH);
                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String erg = "";
                                erg = String.valueOf(monthOfYear + 1);
                                erg += "/" + year;

                                mCardExpirationMonth.setText(erg);
                                cardExpirationMonth = String.valueOf(monthOfYear + 1);
                                cardExpirationYear = String.valueOf(year);
                            }

                        }, y, m, d);
                dp.setTitle("Calender");
                dp.show();
            }
        });

    }
    public void addCreditCardInfo()
    {
        cardNumber = mCardNumber.getText().toString();
        securityCode = mSecurityCode.getText().toString();
        nameOnCard = mNameOnCard.getText().toString();
        address1 = mAddress1.getText().toString();
        address2 = mAddress2.getText().toString();
        city = mCity.getText().toString();
        state = mState.getText().toString();
        country = "1";
        zip = mZip.getText().toString();

        if(isEmpty(cardNumber)&& isEmpty(securityCode)&& isEmpty(cardExpirationMonth)&& isEmpty(nameOnCard)&& isEmpty(address1)&& isEmpty(address2)&& isEmpty(city)&& isEmpty(state)&& isEmpty(zip) && isEmpty(cardExpirationYear) && isEmpty(country))
        {
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
                jsonObject.put("cc_cvv2", securityCode);
                jsonObject.put("cc_num", cardNumber);
                jsonObject.put("billing_country_id", country);

                parent.put("billing_information", jsonObject);
                loadBillingInfo(parent.toString());
                Log.i("ADD Credit Card",jsonObject.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(getActivity(),"All fields are required",Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo)
    {
        if(!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    private void loadBillingInfo(String params) {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddBillingInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };

        if(response == null) {
            AddCreditCardInfoService service = new AddCreditCardInfoService(getActivity(), null);
            service.addCreditCardInfo(successCallBackListener, errorListener, params);
        }
        else
        {
            ReplaceCreditCardService service = new ReplaceCreditCardService(getActivity(), null);
            service.replaceCreditCardInfo(successCallBackListener, errorListener, params);
        }
    }

    private void handleAddBillingInfoSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("Add_CREDIT_CARD", true);
            editor.commit();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.tabcontent, new ViewCreditCard()).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
