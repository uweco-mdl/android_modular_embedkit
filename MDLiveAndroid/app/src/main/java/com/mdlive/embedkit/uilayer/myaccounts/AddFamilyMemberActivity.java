package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.AddFamilyMemberInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
    private TextView mState = null;

    private EditText mPhone = null;
    private TextView mDOB = null;
    private TextView mGender = null;

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
    private List<String> stateIds = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private RelativeLayout mStateLayout,mDOBLayout,mGenderLayout;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_familymember);
        clearMinimizedTime();

        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView back = (ImageView) toolbar.findViewById(R.id.backImg);
        TextView title = (TextView) toolbar.findViewById(R.id.headerTxt);
        title.setText(getString(R.string.add_family_member).toUpperCase());
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

        mDOBLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR) + 4;
                int m = c.get(Calendar.MONTH) - 2;
                int d = c.get(Calendar.DAY_OF_MONTH);
                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(AddFamilyMemberActivity.this,
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

        mStateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeStateDialog();
            }
        });

        mGenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {
                        "Male", "Female"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddFamilyMemberActivity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mGender.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent upIntent = new Intent(this, MyAccountActivity.class);
        upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(upIntent);
        finish();
    }

    public void leftBtnOnClick(View view) {
        Intent upIntent = new Intent(this, MyAccountActivity.class);
        upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(upIntent);
        finish();
    }

    public void rightBtnOnClick(View view) {
        addFamilyMemberInfo();
    }

    public void init()
    {
        mUsername = (EditText)findViewById(R.id.userName);
        mEmail = (EditText)findViewById(R.id.email);

        mFirstName = (EditText)findViewById(R.id.firstName);

        mLastName = (EditText)findViewById(R.id.lastName);
        mAddress1 = (EditText)findViewById(R.id.streetAddress);

        mCity = (EditText)findViewById(R.id.city);
        mState = (TextView)findViewById(R.id.state);
        mPhone = (EditText)findViewById(R.id.phone);

        mDOB = (TextView)findViewById(R.id.DOB);
        mGender = (TextView)findViewById(R.id.gender);
        mDOBLayout = (RelativeLayout)findViewById(R.id.DOBLayout);
        mStateLayout = (RelativeLayout)findViewById(R.id.stateLayout);
        mGenderLayout = (RelativeLayout)findViewById(R.id.genderLayout);

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
            Toast.makeText(AddFamilyMemberActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
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
                    MdliveUtils.connectionTimeoutError(pDialog, AddFamilyMemberActivity.this);
                }
            }
        };

        AddFamilyMemberInfoService service = new AddFamilyMemberInfoService(AddFamilyMemberActivity.this, null);
        service.addFamilyMemberInfo(successCallBackListener, errorListener, params);
    }

    private void handleAddFamilyInfoSuccessResponse(JSONObject response) {
        try {

            pDialog.dismiss();

            Toast.makeText(AddFamilyMemberActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
            Intent upIntent = new Intent(this, MyAccountActivity.class);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStateDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddFamilyMemberActivity.this);

        stateList = Arrays.asList(getResources().getStringArray(R.array.stateName));
        stateIds = Arrays.asList(getResources().getStringArray(R.array.stateCode));

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

    private void clearMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}