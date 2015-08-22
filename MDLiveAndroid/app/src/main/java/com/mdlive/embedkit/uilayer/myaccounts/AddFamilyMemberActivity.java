package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by venkataraman_r on 8/22/2015.
 */
public class AddFamilyMemberActivity extends AppCompatActivity{

    private EditText mUsername = null;
    private EditText mEmail = null;

    private EditText mFirstName = null;

    private EditText mLastName = null;
    private EditText mAddress1 = null;
    private EditText mCity = null;
    private EditText mState = null;

    private EditText mPhone = null;
    private EditText mDOB = null;
    private EditText mGender = null;

    private String Username = null;
    private String Email = null;
    private String FirstName = null;

    private String LastName = null;
    private String Address1 = null;

    private String City = null;
    private String State = null;
    private String Phone = null;

    private String DOB = null;
    private String Gender = null;


    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_familymember);

        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView back = (ImageView) toolbar.findViewById(R.id.backImg);
        TextView title = (TextView) toolbar.findViewById(R.id.headerTxt);
        ImageView apply = (ImageView) toolbar.findViewById(R.id.txtApply);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFamilyMemberInfo();
            }
        });

        mDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR) + 4;
                int m = c.get(Calendar.MONTH) - 2;
                int d = c.get(Calendar.DAY_OF_MONTH);
                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(getBaseContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String erg = "";
                                erg += dayOfMonth;
                                erg += "/" + String.valueOf(monthOfYear + 1);
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
    }

    public void init()
    {
        mUsername = (EditText)findViewById(R.id.userName);
        mEmail = (EditText)findViewById(R.id.email);

        mFirstName = (EditText)findViewById(R.id.firstName);

        mLastName = (EditText)findViewById(R.id.lastName);
        mAddress1 = (EditText)findViewById(R.id.streetAddress);

        mCity = (EditText)findViewById(R.id.city);
        mState = (EditText)findViewById(R.id.state);
        mPhone = (EditText)findViewById(R.id.phone);

        mDOB = (EditText)findViewById(R.id.DOB);
        mGender = (EditText)findViewById(R.id.gender);


        pDialog = MdliveUtils.getProgressDialog("Please wait...", AddFamilyMemberActivity.this);
    }

    public void addFamilyMemberInfo()
    {
        Username = mUsername.getText().toString();
        Email = mEmail.getText().toString();

        FirstName = mFirstName.getText().toString();

        LastName = mLastName.getText().toString();
        Address1 = mAddress1.getText().toString();

        City = mCity.getText().toString();
        State = mState.getText().toString();

        Phone = mPhone.getText().toString();

        DOB = mDOB.getText().toString();
        Gender = mGender.getText().toString();

        if(isEmpty(Username)&& isEmpty(Email)&&  isEmpty(FirstName)&&  isEmpty(LastName)&& isEmpty(Address1)&&  isEmpty(City)
                && isEmpty(State) &&  isEmpty(Phone) &&  isEmpty(DOB) && isEmpty(Gender))
        {
            try {
                JSONObject parent = new JSONObject();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("computer", "MAC");

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("username", Username);
                jsonObject1.put("first_name", FirstName);
//                jsonObject1.put("middle_name", MiddleName);
                jsonObject1.put("last_name", LastName);
                jsonObject1.put("gender", Gender);
                jsonObject1.put("email", Email);
                jsonObject1.put("phone", Phone);
//                jsonObject1.put("cell", Cell);
                jsonObject1.put("address1", Address1);
//                jsonObject1.put("address2", Address2);
                jsonObject1.put("city", City);
                jsonObject1.put("state_id", State);
//                jsonObject1.put("zip", Zip);
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
            Toast.makeText(getBaseContext(), "All fields are required", Toast.LENGTH_SHORT).show();
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
                    MdliveUtils.handelVolleyErrorResponse(AddFamilyMemberActivity.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getBaseContext());
                }
            }
        };

        AddFamilyMemberInfoService service = new AddFamilyMemberInfoService(getBaseContext(), null);
        service.addFamilyMemberInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddFamilyInfoSuccessResponse(JSONObject response) {
        try {

            pDialog.dismiss();

            Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

