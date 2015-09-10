package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class BillingInformationFragment extends MDLiveBaseFragment  {

    private TextView mCreditCardDate = null;
    private TextView mCreditCardAddress = null;
    private TextView mReplaceCreditCard = null;
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
    android.support.v7.widget.CardView mviewCreditCard;
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
        mReplaceCreditCard = (TextView) view.findViewById(R.id.addCreditCard);
        mviewCreditCard = (android.support.v7.widget.CardView) view.findViewById(R.id.viewCreditCard);

        mReplaceCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePhone = new Intent(getActivity(), MyAccountsHome.class);

                changePhone.putExtra("Fragment_Name", "REPLACE CREDIT CARD");
                if ((mReplaceCreditCard.getText().toString()).equalsIgnoreCase("Add credit card")) {

                    changePhone.putExtra("Fragment_Name1", "ADD CREDIT CARD");
                    changePhone.putExtra("Credit_Card_View", "Add");
                    changePhone.putExtra("Credit_Card_Response", "Add_New");
                } else {
                    changePhone.putExtra("Fragment_Name1", "REPLACE CREDIT CARD");
                    changePhone.putExtra("Credit_Card_View", "replace");
                    changePhone.putExtra("Credit_Card_Response", myProfile.toString());
                }
                startActivityForResult(changePhone, 1);
            }
        });

        mviewCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePhone = new Intent(getActivity(), MyAccountsHome.class);
                changePhone.putExtra("Fragment_Name", "REPLACE CREDIT CARD");
                changePhone.putExtra("Fragment_Name1", "VIEW CREDIT CARD");
                changePhone.putExtra("Credit_Card_Response", myProfile.toString());
                changePhone.putExtra("Credit_Card_View", "view");
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
        Log.i("response", response.toString());
        try {

            if (response != null) {
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
                if (address2.equalsIgnoreCase("null") || (address2 == null)  || (TextUtils.isEmpty(address2))) {
                    address2="";
                }

                city = myProfile.getString("billing_city");
                address1 = myProfile.getString("billing_address1");
                cardExpirationMonth = myProfile.getString("cc_expmonth");


                if (TextUtils.isEmpty(cardNumber) && TextUtils.isEmpty(securityCode) && TextUtils.isEmpty(cardExpirationMonth) && TextUtils.isEmpty(cardExpirationYear) && TextUtils.isEmpty(nameOnCard)) {
                    mReplaceCreditCard.setText(getResources().getString(R.string.mdl_add_card));
                    mviewCreditCard.setVisibility(View.GONE);
                } else {
                    mCreditCardDate.setText("Mastercard ending in " + cardExpirationMonth + "/" + cardExpirationYear);

                    mCreditCardAddress.setText("Billing Address:" + "\n" + address1 + " " + address2 + "\n" +
                            city + ", " + state + "\n" + country);

                    mReplaceCreditCard.setText(getResources().getString(R.string.mdl_replace_card));
                    mviewCreditCard.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
