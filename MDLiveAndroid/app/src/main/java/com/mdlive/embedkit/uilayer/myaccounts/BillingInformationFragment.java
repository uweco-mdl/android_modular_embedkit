package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddCreditCardInfoService;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetCreditCardInfoService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/22/2015.
 */
public class BillingInformationFragment extends Fragment implements  View.OnClickListener{

    private Button mAddCreditCard = null;
    private TextView mViewCreditCard = null;
    private Button mReplaceCreditCard = null;
    private EditText mCardNumber = null;
    private EditText mSecurityCode = null;
    private EditText mCardExpirationMonth = null;
    private EditText mCardExpirationYear = null;
    private EditText mNameOnCard = null;
    private EditText mAddress1 = null;
    private EditText mAddress2 = null;
    private EditText mCity = null;
    private EditText mState = null;
    private EditText mZip = null;
    private EditText mCountry = null;
    private Button mSave = null;
    private View mBillingInfoView = null;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View billingInformation = inflater.inflate(R.layout.fragment_view_creditcard,null);

        sharedpreferences = getActivity().getSharedPreferences("MDLIVE_BILLING", Context.MODE_PRIVATE);

        init(billingInformation);

        getCreditCardInfoService();
//        mReplaceCreditCard.setVisibility(View.VISIBLE);
//        mBillingInfoView.setVisibility(View.GONE);
//        mViewCreditCard.setVisibility(View.VISIBLE);
//        mAddCreditCard.setVisibility(View.GONE);

//        if(sharedpreferences.getBoolean("Add_CREDIT_CARD",false))
//        {
//            getCreditCardInfoService();
//            mReplaceCreditCard.setVisibility(View.VISIBLE);
//            mBillingInfoView.setVisibility(View.GONE);
//            mViewCreditCard.setVisibility(View.VISIBLE);
//            mAddCreditCard.setVisibility(View.GONE);
//        }
//        else
//        {
//            mReplaceCreditCard.setVisibility(View.GONE);
//            mBillingInfoView.setVisibility(View.GONE);
//            mViewCreditCard.setVisibility(View.GONE);
//            mAddCreditCard.setVisibility(View.VISIBLE);
//        }
        return billingInformation;
    }

    public void getCreditCardInfoService()
    {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlegetCreditCardInfoSuccessResponse(response);

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

        GetCreditCardInfoService service = new GetCreditCardInfoService(getActivity(), null);
        service.getCreditCardInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetCreditCardInfoSuccessResponse(JSONObject response)
    {
        pDialog.dismiss();
        try {
            JSONObject myProfile = response.getJSONObject("billing_information");
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

            mViewCreditCard.setText("Visa ending in " + cardExpirationMonth + "/" + cardExpirationYear + "\n" + "Billing Address : " + nameOnCard + "\n" + address1 + address2 + "\n" +
            city + ", " + state + "\n" + country);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void init(View billingInformation)
    {
//        mAddCreditCard = (Button)billingInformation.findViewById(R.id.btn_addCreditCard);
        mViewCreditCard = (TextView)billingInformation.findViewById(R.id.txt_viewCreditCard);
//        mReplaceCreditCard = (Button)billingInformation.findViewById(R.id.btn_replaceCreditCard);
//        mCardNumber = (EditText)billingInformation.findViewById(R.id.edt_cardNumber);
//        mSecurityCode = (EditText)billingInformation.findViewById(R.id.edt_securityCode);
//        mCardExpirationMonth = (EditText)billingInformation.findViewById(R.id.edt_cardExpirationMonth);
////        mCardExpirationYear = (EditText)billingInformation.findViewById(R.id.edt_cardExpirationYear);
//        mNameOnCard = (EditText)billingInformation.findViewById(R.id.edt_nameOnCard);
//        mAddress1 = (EditText)billingInformation.findViewById(R.id.edt_address1);
//        mAddress2 = (EditText)billingInformation.findViewById(R.id.edt_address2);
//        mCity = (EditText)billingInformation.findViewById(R.id.edt_city);
//        mState = (EditText)billingInformation.findViewById(R.id.edt_state);
////        mCountry = (EditText)billingInformation.findViewById(R.id.edt_country);
//        mZip = (EditText)billingInformation.findViewById(R.id.edt_zipCode);
//        mSave = (Button)billingInformation.findViewById(R.id.btn_save);

//        mBillingInfoView = (View)billingInformation.findViewById(R.id.billing_info);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

//        mAddCreditCard.setOnClickListener(this);
//        mReplaceCreditCard.setOnClickListener(this);
//        mSave.setOnClickListener(this);
//        sharedpreferences = getActivity().getSharedPreferences("MDLIVE", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
//            case R.id.btn_addCreditCard:
//                mBillingInfoView.setVisibility(View.VISIBLE);
//                mAddCreditCard.setVisibility(View.GONE);
//                mReplaceCreditCard.setVisibility(View.GONE);
//                mViewCreditCard.setVisibility(View.GONE);
//                break;
//            case R.id.btn_replaceCreditCard:
//                mBillingInfoView.setVisibility(View.VISIBLE);
//                mReplaceCreditCard.setVisibility(View.GONE);
//                mAddCreditCard.setVisibility(View.GONE);
//                mViewCreditCard.setVisibility(View.GONE);
//                break;
//            case R.id.btn_save:
//                addCreditCardInfo();
//                break;
        }
    }

    public void addCreditCardInfo()
    {
         cardNumber = mCardNumber.getText().toString();
         securityCode = mSecurityCode.getText().toString();
         cardExpirationMonth = mCardExpirationMonth.getText().toString();
         cardExpirationYear = mCardExpirationYear.getText().toString();
         nameOnCard = mNameOnCard.getText().toString();
         address1 = mAddress1.getText().toString();
         address2 = mAddress2.getText().toString();
         city = mCity.getText().toString();
         state = mState.getText().toString();
         country = mCountry.getText().toString();
         zip = mZip.getText().toString();

        if(isEmpty(cardNumber)&& isEmpty(securityCode)&& isEmpty(cardExpirationMonth)&& isEmpty(nameOnCard)&& isEmpty(address1)&& isEmpty(address2)&& isEmpty(city)&& isEmpty(state)&& isEmpty(zip) && isEmpty(cardExpirationYear) && isEmpty(country))
        {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cc_type_id", "1");
                jsonObject.put("billing_address1", address1);
                jsonObject.put("billing_zip5", zip);
                jsonObject.put("billing_address2", address2);
                jsonObject.put("cc_hsa", "true");
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

        AddCreditCardInfoService service = new AddCreditCardInfoService(getActivity(), null);
        service.addCreditCardInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddBillingInfoSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            Toast.makeText(getActivity(),response.getString("message"),Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("Add_CREDIT_CARD", true);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
