package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetCreditCardInfoService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/22/2015.
 */
public class BillingInformationFragment extends MDLiveBaseFragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    private Button mAddCreditCard = null;
    private TextView mCreditCardDate = null;
    private TextView mCreditCardAddress = null;
    private TextView mReplaceCreditCard = null;
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
    JSONObject myProfile;
    public static BillingInformationFragment newInstance() {
        final BillingInformationFragment fragment = new BillingInformationFragment();
        return fragment;
    }

    public BillingInformationFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_creditcard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCreditCardDate = (TextView) view.findViewById(R.id.cardEndDate);
        mCreditCardAddress = (TextView) view.findViewById(R.id.cardAddress);
        mReplaceCreditCard =(TextView)view.findViewById(R.id.addCreditCard);

//        sharedpreferences = view.getContext().getSharedPreferences("MDLIVE_BILLING", Context.MODE_PRIVATE);

        mReplaceCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent changePhone = new Intent(getActivity(),MyAccountsHome.class);
                changePhone.putExtra("Fragment_Name","REPLACE CREDIT CARD");
                changePhone.putExtra("Credit_Card_Response",myProfile.toString());
                startActivityForResult(changePhone, 1);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getCreditCardInfoService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case 1:
                getCreditCardInfoService();
                break;
        }
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

                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        GetCreditCardInfoService service = new GetCreditCardInfoService(getActivity(), null);
        service.getCreditCardInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetCreditCardInfoSuccessResponse(JSONObject response) {
        hideProgressDialog();
        try {
             myProfile = response.getJSONObject("billing_information");
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

//            mViewCreditCard.setText("Visa ending in " + cardExpirationMonth + "/" + cardExpirationYear + "\n" + "Billing Address : " + nameOnCard + "\n" + address1 + address2 + "\n" +
//                    city + ", " + state + "\n" + country);

            mCreditCardDate.setText("Mastercard ending in " + cardExpirationMonth + "/" + cardExpirationYear );
            mCreditCardAddress.setText("Billing Address:" + "\n" +address1 + address2 + "\n" +
                    city + ", " + state + "\n" + country);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
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

//    public void addCreditCardInfo() {
//        cardNumber = mCardNumber.getText().toString();
//        securityCode = mSecurityCode.getText().toString();
//        cardExpirationMonth = mCardExpirationMonth.getText().toString();
//        cardExpirationYear = mCardExpirationYear.getText().toString();
//        nameOnCard = mNameOnCard.getText().toString();
//        address1 = mAddress1.getText().toString();
//        address2 = mAddress2.getText().toString();
//        city = mCity.getText().toString();
//        state = mState.getText().toString();
//        country = mCountry.getText().toString();
//        zip = mZip.getText().toString();
//
//        if (isEmpty(cardNumber) && isEmpty(securityCode) && isEmpty(cardExpirationMonth) && isEmpty(nameOnCard) && isEmpty(address1) && isEmpty(address2) && isEmpty(city) && isEmpty(state) && isEmpty(zip) && isEmpty(cardExpirationYear) && isEmpty(country)) {
//            try {
//                JSONObject parent = new JSONObject();
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("cc_type_id", "1");
//                jsonObject.put("billing_address1", address1);
//                jsonObject.put("billing_zip5", zip);
//                jsonObject.put("billing_address2", address2);
//                jsonObject.put("cc_hsa", "true");
//                jsonObject.put("cc_expyear", cardExpirationYear);
//                jsonObject.put("billing_name", nameOnCard);
//                jsonObject.put("billing_city", city);
//                jsonObject.put("billing_state_id", state);
//                jsonObject.put("cc_expmonth", cardExpirationMonth);
//                jsonObject.put("cc_cvv2", securityCode);
//                jsonObject.put("cc_num", cardNumber);
//                jsonObject.put("billing_country_id", country);
//
//                parent.put("billing_information", jsonObject);
//                loadBillingInfo(parent.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public Boolean isEmpty(String cardInfo) {
//        if (!TextUtils.isEmpty(cardInfo))
//            return true;
//        return false;
//    }
//
//    private void loadBillingInfo(String params) {
//        showProgressDialog();
//
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                handleAddBillingInfoSuccessResponse(response);
//            }
//        };
//
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                hideProgressDialog();
//                try {
//                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
//                } catch (Exception e) {
//                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
//                }
//            }
//        };
//
//        AddCreditCardInfoService service = new AddCreditCardInfoService(getActivity(), null);
//        service.addCreditCardInfo(successCallBackListener, errorListener, params);
//    }

//    private void handleAddBillingInfoSuccessResponse(JSONObject response) {
//        try {
//            hideProgressDialog();
//            //Fetch Data From the Services
//            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
////            SharedPreferences.Editor editor = sharedpreferences.edit();
////            editor.putBoolean("Add_CREDIT_CARD", true);
////            editor.commit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
