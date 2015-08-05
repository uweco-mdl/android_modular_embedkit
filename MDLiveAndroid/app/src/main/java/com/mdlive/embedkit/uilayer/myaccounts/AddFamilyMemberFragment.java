package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddFamilyMemberInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by venkataraman_r on 7/27/2015.
 */
public class AddFamilyMemberFragment extends Fragment {


    private EditText mUsername = null;
    private EditText mEmail = null;
    private EditText mConfirmEmail = null;
    private EditText mFirstName = null;
    private EditText mMiddleName = null;
    private EditText mLastName = null;
    private EditText mAddress1 = null;
    private EditText mAddress2 = null;
    private EditText mCity = null;
    private EditText mState = null;
    private EditText mZip = null;
    private EditText mHomePhone = null;
    private EditText mWorkPhone = null;
    private EditText mCell = null;
    private EditText mFax = null;
    private EditText mDOB = null;
    private EditText mGender = null;

    private String Username = null;
    private String Email = null;
    private String ConfirmEmail = null;
    private String FirstName = null;
    private String MiddleName = null;
    private String LastName = null;
    private String Address1 = null;
    private String Address2 = null;
    private String City = null;
    private String State = null;
    private String Zip = null;
    private String HomePhone = null;
    private String WorkPhone = null;
    private String Cell = null;
    private String Fax = null;
    private String DOB = null;
    private String Gender = null;

    private Button mSubmit = null;

    private ProgressDialog pDialog;

    public static AddFamilyMemberFragment newInstance() {

        final AddFamilyMemberFragment addFamilyMember = new AddFamilyMemberFragment();
        return addFamilyMember;
    }
    public AddFamilyMemberFragment(){ super(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View addFamilyMember = inflater.inflate(R.layout.fragment_add_familymember,null);

        init(addFamilyMember);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.family_member));

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFamilyMemberInfo();

            }
        });

        mDOB.setOnClickListener(new View.OnClickListener() {
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
                                erg += dayOfMonth;
                                erg += "/"+String.valueOf(monthOfYear + 1);
                                erg += "/" + year;

                                mDOB.setText(erg);
                            }

                        }, y, m, d);
                dp.setTitle("Calender");
                dp.show();
            }
        });

        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return addFamilyMember;

    }
    public void init(View addFamilyMember)
    {
        mUsername = (EditText)addFamilyMember.findViewById(R.id.edt_userName);
        mEmail = (EditText)addFamilyMember.findViewById(R.id.edt_email);
        mConfirmEmail = (EditText)addFamilyMember.findViewById(R.id.edt_confirmEmail);
        mFirstName = (EditText)addFamilyMember.findViewById(R.id.edt_firstName);
        mMiddleName = (EditText)addFamilyMember.findViewById(R.id.edt_middleName);
        mLastName = (EditText)addFamilyMember.findViewById(R.id.edt_lastName);
        mAddress1 = (EditText)addFamilyMember.findViewById(R.id.edt_address1);
        mAddress2 = (EditText)addFamilyMember.findViewById(R.id.edt_address2);
        mCity = (EditText)addFamilyMember.findViewById(R.id.edt_city);
        mState = (EditText)addFamilyMember.findViewById(R.id.edt_state);
        mZip = (EditText)addFamilyMember.findViewById(R.id.edt_zipCode);
        mHomePhone = (EditText)addFamilyMember.findViewById(R.id.edt_homePhone);
        mWorkPhone = (EditText)addFamilyMember.findViewById(R.id.edt_workPhone);
        mCell = (EditText)addFamilyMember.findViewById(R.id.edt_cell);
        mFax = (EditText)addFamilyMember.findViewById(R.id.edt_fax);
        mDOB = (EditText)addFamilyMember.findViewById(R.id.edt_dob);
        mGender = (EditText)addFamilyMember.findViewById(R.id.edt_gender);
        mSubmit = (Button)addFamilyMember.findViewById(R.id.btn_save);

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());
    }

    public void addFamilyMemberInfo()
    {
        Username = mUsername.getText().toString();
        Email = mEmail.getText().toString();
        ConfirmEmail = mConfirmEmail.getText().toString();
        FirstName = mFirstName.getText().toString();
        MiddleName = mMiddleName.getText().toString();
        LastName = mLastName.getText().toString();
        Address1 = mAddress1.getText().toString();
        Address2 = mAddress2.getText().toString();
        City = mCity.getText().toString();
        State = mState.getText().toString();
        Zip = mZip.getText().toString();
        HomePhone = mHomePhone.getText().toString();
        WorkPhone = mWorkPhone.getText().toString();
        Cell = mCell.getText().toString();
        Fax = mFax.getText().toString();
        DOB = mDOB.getText().toString();
        Gender = mGender.getText().toString();

        if(isEmpty(Username)&& isEmpty(Email)&& isEmpty(ConfirmEmail)&& isEmpty(FirstName)&& isEmpty(MiddleName)&& isEmpty(LastName)&& isEmpty(Address1)&& isEmpty(Address2)&& isEmpty(City)
                && isEmpty(State) && isEmpty(Zip)&& isEmpty(HomePhone) && isEmpty(WorkPhone)&& isEmpty(Cell) && isEmpty(Fax)&& isEmpty(WorkPhone)&& isEmpty(DOB) && isEmpty(Gender))
        {
            try {
                JSONObject parent = new JSONObject();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("computer", "MAC");

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("username", Username);
                jsonObject1.put("first_name", FirstName);
                jsonObject1.put("middle_name", MiddleName);
                jsonObject1.put("last_name", LastName);
                jsonObject1.put("gender", Gender);
                jsonObject1.put("email", Email);
                jsonObject1.put("phone", HomePhone);
                jsonObject1.put("cell", Cell);
                jsonObject1.put("address1", Address1);
                jsonObject1.put("address2", Address2);
                jsonObject1.put("city", City);
                jsonObject1.put("state_id", State);
                jsonObject1.put("zip", Zip);
                jsonObject1.put("birthdate", DOB);
                jsonObject1.put("answer", "idontknow");


                parent.put("member", jsonObject1);
                parent.put("camera", jsonObject);

                Log.i("params", parent.toString());
                addFamilyMember(parent.toString());

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isEmpty(String cardInfo)
    {
        if(!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    private void addFamilyMember(String params) {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleAddFamilyInfoSuccessResponse(response);
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

        AddFamilyMemberInfoService service = new AddFamilyMemberInfoService(getActivity(), null);
        service.addFamilyMemberInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddFamilyInfoSuccessResponse(JSONObject response) {
        try {

            pDialog.dismiss();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}