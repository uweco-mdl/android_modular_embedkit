package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetCreditCardInfoService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/26/2015.
 */
public class ViewCreditCard extends Fragment{

    private TextView viewCreditCardInfo = null;

    private ProgressDialog pDialog;
    private Button replaceCard;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewCreditCard = inflater.inflate(R.layout.fragment_view_creditcard,null);

        viewCreditCardInfo = (TextView)viewCreditCard.findViewById(R.id.txt_viewCreditCard);
        replaceCard = (Button)viewCreditCard.findViewById(R.id.replaceCard);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.billing_title));

        getCreditCardInfoService();

        replaceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCreditcard();
            }
        });
        return viewCreditCard;

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
        Log.i("response",response.toString());
        pDialog.dismiss();
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

            viewCreditCardInfo.setText("Visa ending in " + cardExpirationMonth + "/" + cardExpirationYear + "\n" + "Billing Address : " + nameOnCard + "\n" + address1 + address2 + "\n" +
                    city + ", " + state + "\n" + country);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void editCreditcard()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tabcontent, CreditCardInfoFragment.newInstance(myProfile.toString())).commit();
    }
}
