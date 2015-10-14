package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.sav.adapters.PickImagePlugin;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.GoogleFitUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.LoadTimeZoneByState;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangeProfilePicService;
import com.mdlive.unifiedmiddleware.services.myaccounts.EditMyProfileService;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetProfileInfoService;
import com.mdlive.unifiedmiddleware.services.myhealth.HealthKitServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by venkataraman_r on 6/18/2015.
 */
public class MyProfileFragment extends MDLiveBaseFragment  implements PickImagePlugin.UploadRecordInterface{

    private CircularNetworkImageView mProfileImage = null;
    private TextView mProfileName = null;
    private LinearLayout mAddressClickListener = null;
    private TextView mUserDOB = null;
    private TextView mGender = null;
    private TextView mUserName = null;
    private TextView mPreferredSignIn = null, changeLangTv;
    private TextView mEmail = null;
    private TextView mAddress = null;
    private TextView mMobile = null, emergencyNumber = null;
    private TextView mTimeZone = null;
    private CardView mChangePassword = null;
    private CardView mChangePin = null;
    private CardView mChangeSecurityQuestions = null;
    private String profileImageURL = null,profileName = null,userDOB = null,gender = null,username = null,address = null,prefferedPhone = null,mobile = null,emergencyContactPhone = null,
    timeZone = null,email = null;
    private JSONObject myProfile;
    SharedPreferences sharedPref;
    String[] timeZoneAbbr = {"CST","EST","MST","PST","AKST","HST","AMS","MIT","GST","PAT"};
    public static PickImagePlugin cameraPlugIn;
    public static String timeZoneByStateValue;
    private View SyncHealthSwitchContainer;

    private boolean mFromResult = false;
    private SwitchCompat mSwitchCompat;
    public static MyProfileFragment newInstance() {
        final MyProfileFragment myProfileFragment = new MyProfileFragment();
        return myProfileFragment;
    }

    public MyProfileFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_myprofile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProfileImage = (CircularNetworkImageView)view.findViewById(R.id.imgProfilePic);
        mProfileName = (TextView)view.findViewById(R.id.txtProfileName);
        mUserDOB = (TextView)view.findViewById(R.id.txtUserDOB);
        mGender = (TextView)view.findViewById(R.id.txtGender);
        mUserName = (TextView)view.findViewById(R.id.txtUserName);
        mPreferredSignIn = (TextView)view.findViewById(R.id.preferredSignIn);
        changeLangTv = (TextView)view.findViewById(R.id.changeLangTv);
        mEmail = (TextView)view.findViewById(R.id.email);
        mAddress = (TextView)view.findViewById(R.id.address);
        mMobile = (TextView)view.findViewById(R.id.phoneNumber);
        emergencyNumber = (TextView) view.findViewById(R.id.emergencyContactNumber);
        mTimeZone = (TextView)view.findViewById(R.id.timeZone);
        mChangePassword = (CardView)view.findViewById(R.id.changePassword);
        mChangePin = (CardView)view.findViewById(R.id.changePin);
        mChangeSecurityQuestions = (CardView)view.findViewById(R.id.changeSecurityQuestion);
        mAddressClickListener = (LinearLayout)view.findViewById(R.id.addressView);
        mSwitchCompat = (SwitchCompat)view.findViewById(R.id.SyncHealthSwitch);
        mAddressClickListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeAddress = new Intent(getActivity(),MyAccountsHome.class);
                changeAddress.putExtra("Fragment_Name","CHANGE ADDRESS");
                changeAddress.putExtra("Address_Response",myProfile.toString());
                startActivityForResult(changeAddress, 3);
            }
        });


        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPrefs.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        if(userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, false)){
            mSwitchCompat.setChecked(true);
            mSwitchCompat.setEnabled(true);
        } else {
            mSwitchCompat.setChecked(false);
            mSwitchCompat.setEnabled(false);
        }

        view.findViewById(R.id.phoneNumberView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent changePhone = new Intent(getActivity(), MyAccountsHome.class);
                changePhone.putExtra("Fragment_Name", "CHANGE PHONE NUMBER");
                changePhone.putExtra("Address_Response", myProfile.toString());
                startActivityForResult(changePhone, 3);

            }
        });
        view.findViewById(R.id.emergencyNumberView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent changePhone = new Intent(getActivity(), MyAccountsHome.class);
                changePhone.putExtra("Fragment_Name", "CHANGE PHONE NUMBER");
                changePhone.putExtra("Address_Response", myProfile.toString());
                startActivityForResult(changePhone, 3);

            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePassword = new Intent(getActivity(),MyAccountsHome.class);
                changePassword.putExtra("Fragment_Name","CHANGE PASSWORD");
                startActivity(changePassword);
            }
        });

        mChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPinCreateion();
            }
        });

        mChangeSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeSecurityQuestions = new Intent(getActivity(),MyAccountsHome.class);
                changeSecurityQuestions.putExtra("Fragment_Name","SECURITY QUESTION");
                changeSecurityQuestions.putExtra("Security_Response",myProfile.toString());
                startActivity(changeSecurityQuestions);
            }
        });

        mPreferredSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final CharSequence[] items = view.getContext().getResources().getStringArray(R.array.mdl_lock_types);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Make your selection");
                builder.setCancelable(false);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mPreferredSignIn.setText(items[item]);
                        // If Selected Password, then simply save type as Password
                        if (view.getResources().getString(R.string.mdl_password).equalsIgnoreCase(String.valueOf(items[item]))) {
                            MdliveUtils.setLockType(getActivity(), String.valueOf(items[item]));
                            mChangePin.setVisibility(View.GONE);
                        } else { // Shows the Create PIN
                            mChangePin.setVisibility(View.VISIBLE);
                            gotoPinCreationSecond();
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        mTimeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = view.getContext().getResources().getStringArray(R.array.mdl_timezone);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Make your selection");
                builder.setCancelable(false);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mTimeZone.setText(items[item]);
                        // If Selected Password, then simply save type as Password
                        changePhoneNumberInfo(true);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if(view.getResources().getString(R.string.mdl_password).equalsIgnoreCase(MdliveUtils.getLockType(getActivity()))){
            mChangePin.setVisibility(View.GONE);
        } else {
            mChangePin.setVisibility(View.VISIBLE);
        }

        mPreferredSignIn.setText(MdliveUtils.getLockType(getActivity()));
        Log.d("UserBasicinfo", UserBasicInfo.readFromSharedPreference(getActivity()).toString());
        changeLangTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {
                        changeLangTv.getResources().getString(R.string.mdl_language_english)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        changeLangTv.setText(items[item]);
                        sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPref.edit();
                        editor1.putString(PreferenceConstants.PREFFERED_LANGUAGE, changeLangTv.getText().toString().toLowerCase());
                        editor1.commit();
                        LocalizationSingleton.getInstance().setLanguage(getActivity(), changeLangTv.getText().toString().toLowerCase());
                        changePhoneNumberInfo(false);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPrefs = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPrefs.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPrefs.edit();
                if(!compoundButton.isChecked()){
                    editor.putBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, true);
                    editor.putBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, false);
                    editor.commit();
                    deleteHealthKitData();
                }
            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        String dependentId = sharedPrefs.getString(PreferenceConstants.DEPENDENT_USER_ID, null);

        SyncHealthSwitchContainer = view.findViewById(R.id.SyncHealthSwitchContainer);
        SyncHealthSwitchContainer.setVisibility(View.GONE);
        if(dependentId == null){
            getHealthKitSyncStatus();
        } else {
            SyncHealthSwitchContainer.setVisibility(View.GONE);
        }
    }

    private void deleteHealthKitData() {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
        HealthKitServices services = new HealthKitServices(getActivity(), getProgressDialog());
        services.deleteHealthKitSync(successCallBackListener, errorListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cameraPlugIn = new PickImagePlugin(getActivity(), this);
        if(timeZoneByStateValue == null){
            getTimezonebyStateService();
        }
        getProfileInfoService();
    }

    private void getProfileInfoService() {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlegetProfileInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        GetProfileInfoService service = new GetProfileInfoService(getActivity(), null);
        service.getProfileInfo(successCallBackListener, errorListener, null);
    }

    private void getTimezonebyStateService() {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                timeZoneByStateValue = response.toString();
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        LoadTimeZoneByState service = new LoadTimeZoneByState(getActivity(), getProgressDialog());
        service.getTimeZoneByState(successCallBackListener, errorListener);
    }

    public void handlegetProfileInfoSuccessResponse(JSONObject response) {
        if(!mFromResult){
            hideProgressDialog();
        }
        Log.i("response", response.toString());
        try {
            myProfile = response.getJSONObject("personal_info");
            profileImageURL = myProfile.getString("image_url");
            profileName = myProfile.getString("first_name")+" "+ myProfile.getString("last_name");
            email = myProfile.getString("email");
            userDOB = myProfile.getString("birthdate");
            gender = myProfile.getString("gender");
            username = myProfile.getString("username");
            String address2;
            String address1;
            String city;
            String state;
            String country;
            String zip;

            if (MdliveUtils.checkIsEmpty(myProfile.getString("address2"))) {
                address2 = "";
            } else {
                address2 = myProfile.getString("address2");
            }

            if (MdliveUtils.checkIsEmpty(myProfile.getString("address1"))) {
                address1 = "";
            } else {
                address1 = myProfile.getString("address1");
            }

            if (MdliveUtils.checkIsEmpty(myProfile.getString("state"))) {
                state = "";
            } else {
                state = myProfile.getString("state") + " ";
            }

            if (MdliveUtils.checkIsEmpty(myProfile.getString("city"))) {
                city = "";
            } else {
                city = myProfile.getString("city");
            }

            if (MdliveUtils.checkIsEmpty(myProfile.getString("country"))) {
                country = "";
            } else {
                country = myProfile.getString("country");
            }

            if (MdliveUtils.checkIsEmpty(myProfile.getString("zipcode"))) {
                zip = "";
            } else {
                zip = myProfile.getString("zipcode");
            }

            address = address1 + " " + address2 + "\n" + city + "\n" + state + country + "\n" + zip;
            mobile = myProfile.getString("phone");
            timeZone = myProfile.getString("timezone");

            if (getActivity() != null) {
                sharedPref = getActivity().getSharedPreferences("ADDRESS_CHANGE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Profile_Address", myProfile.toString());
                editor.commit();
            }

            JSONObject securityQuestion = myProfile.optJSONObject("security");
            mProfileName.setText(profileName);
            mUserDOB.setText(userDOB);
            mGender.setText(gender);
            mUserName.setText(username);
            mAddress.setText(address);

            String formattedString = MdliveUtils.formatDualString(mobile);
            mMobile.setText(formattedString);
            if (!myProfile.getString("emergency_contact_number").equals("") && !myProfile.getString("emergency_contact_number").equals("null")){
                emergencyNumber.setText(MdliveUtils.formatDualString(myProfile.getString("emergency_contact_number")));
            }else {
                emergencyNumber.setText("");
            }
            List<String> tmpTimezoneAbbr = Arrays.asList(timeZoneAbbr);
            final CharSequence[] items = getActivity().getResources().getStringArray(R.array.mdl_timezone);
            CharSequence tmpTimeZone = items[tmpTimezoneAbbr.indexOf(timeZone) + 1];
            SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            if(sharedPref.getBoolean(PreferenceConstants.TIMEZONE_SET_AUTOMATIC, false)){
                mTimeZone.setText(items[0]);
            }else {
                mTimeZone.setText(tmpTimeZone);
            }
            mEmail.setText(email);

            if (getActivity() != null) {
                sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                String language = sharedPref.getString(PreferenceConstants.PREFFERED_LANGUAGE,getActivity().getString(R.string.mdl_language_english));
                changeLangTv.setText(language.toUpperCase());
                mProfileImage.setImageUrl(profileImageURL, ApplicationController.getInstance().getImageLoader(getActivity()));

                sharedPref = getActivity().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPref.edit();
                editor1.putString(PreferenceConstants.SIGN_IN, mPreferredSignIn.getText().toString());
                editor1.commit();

                changeLangTv.setText(getActivity().getString(R.string.mdl_language_english));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }


    public Boolean isEmpty(String cardInfo)
    {
        if(!TextUtils.isEmpty(cardInfo))
            return true;
        return false;
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraPlugIn.fileUri = cameraPlugIn.getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPlugIn.fileUri);
                    startActivityForResult(intent, IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    /*Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 2);*/
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            IntegerConstants.PICK_IMAGE_REQUEST_CODE);
                    /*Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);*/
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        // if the result is capturing Image
        if (requestCode == IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // successfully captured the image
                cameraPlugIn.handleCapturedImageRequest();
            }
        }
        if (requestCode == IntegerConstants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cameraPlugIn.handlePickedImageRequest(imageReturnedIntent);
            }
        }
        if(requestCode == 3){
            getProfileInfoService();
        }

    }

    private void getHealthKitSyncStatus() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d("HealthKit Response", response.toString());
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
                    String dependentId = sharedPref.getString(PreferenceConstants.DEPENDENT_USER_ID, null);

                    if(response.optString("message").contains("never synced") || response.optString("message").contains("synced with this") && dependentId == null){
                        if (userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, false) || dependentId != null) {
                            SyncHealthSwitchContainer.setVisibility(View.VISIBLE);
                        } else {
                            SyncHealthSwitchContainer.setVisibility(View.GONE);
                        }
                    } else {
                        SyncHealthSwitchContainer.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HealthKit Response", error.networkResponse.toString() + " -- ");
                SyncHealthSwitchContainer.setVisibility(View.GONE);
            }
        };
        HealthKitServices services = new HealthKitServices(getActivity(), getProgressDialog());
        services.registerHealthKitSync(successCallBackListener, errorListener);
    }
    private void loadChangeProfilePicService(String params) {
     showProgressDialog();
        mFromResult = true;
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleChangeProfilePicSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressDialog();
                mFromResult = false;
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                }
            }
        };

        ChangeProfilePicService service = new ChangeProfilePicService(getActivity(), null);
        service.changeProfilePic(successCallBackListener, errorListener, params);
    }

    private void handleChangeProfilePicSuccessResponse(JSONObject response) {
        try {
            mFromResult = false;
            hideProgressDialog();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            getProfileInfoService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mPreferredSignIn.setText(MdliveUtils.getLockType(getActivity()));
        getProfileInfoService();
    }
    public void changePhoneNumberInfo(boolean fromTimezone) {

            try {
                final CharSequence[] items = getActivity().getResources().getStringArray(R.array.mdl_timezone);
                final List<CharSequence> fullTimezone = Arrays.asList(items);
                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", myProfile.getString("email"));
                    jsonObject.put("phone", myProfile.getString("phone"));
                    jsonObject.put("birthdate", myProfile.getString("birthdate"));
                    jsonObject.put("state_id", myProfile.getString("state"));
                    jsonObject.put("city", myProfile.getString("country"));
                    jsonObject.put("zipcode", myProfile.getString("zipcode"));
                    jsonObject.put("first_name", myProfile.getString("first_name"));
                    jsonObject.put("address1", myProfile.getString("address1"));
                    jsonObject.put("address2", myProfile.getString("address2"));
                    jsonObject.put("gender", myProfile.getString("gender"));
                    jsonObject.put("last_name", myProfile.getString("last_name"));
                    jsonObject.put("language_preference", changeLangTv.getText().toString());
                    String timeZone = fullTimezone.indexOf(mTimeZone.getText()) == 0 ? TimeZoneUtils.getDeviceTimeZone() : timeZoneAbbr[fullTimezone.indexOf(mTimeZone.getText()) - 1];
                    jsonObject.put("timezone", timeZone);
                    // Set Automatic timezone fix
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    sharedPref.edit().putBoolean(PreferenceConstants.TIMEZONE_SET_AUTOMATIC, fullTimezone.indexOf(mTimeZone.getText()) == 0).commit();


                    parent.put("member", jsonObject);
                    loadProfileInfo(parent.toString(),fromTimezone);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    public void loadProfileInfo(String params, boolean fromTimezone) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response - ", response.toString());
                if(!mFromResult){
                    hideProgressDialog();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                }
            }
        };

        EditMyProfileService service = new EditMyProfileService(getActivity(), null);
        service.editMyProfile(successCallBackListener, errorListener, params);
    }

    private void gotoPinCreateion() {
        Intent changePin = new Intent(getActivity(),MyAccountsHome.class);
        changePin.putExtra("Fragment_Name","Old Pin");
        startActivity(changePin);
    }

    private void gotoPinCreationSecond() {
        Intent changePin = new Intent(getActivity(),MyAccountsHome.class);
        changePin.putExtra("Fragment_Name","Old Pin Second");
        startActivity(changePin);
    }

    @Override
    public void uploadMedicalRecordService(String filePath, boolean capturedInCamera) {
        final File file = new File(filePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        dateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(getActivity()));
        String  currentTimeStamp = dateFormat.format(TimeZoneUtils.getCalendarWithOffset(getActivity()).getTime());
        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_name", currentTimeStamp+".jpg");
            jsonObject.put("photo",  MdliveUtils.encodeFileToBase64Binary(file, MdliveUtils.getFileExtention(file)));
            parent.put("personal_information", jsonObject);
            loadChangeProfilePicService(parent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}