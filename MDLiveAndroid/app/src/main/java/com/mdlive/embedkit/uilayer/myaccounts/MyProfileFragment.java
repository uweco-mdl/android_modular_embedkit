package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.RoundedImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.EditMyProfileService;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetProfileInfoService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/18/2015.
 */
public class MyProfileFragment extends Fragment{

    private RoundedImageView mProfileImage = null;
    private CircularNetworkImageView mCircularNetworkImageView;
    private TextView mProfileName = null;
    private TextView mUserDOB = null;
    private TextView mGender = null;
    private TextView mUserName = null;
    private TextView mPreferredSignIn = null;
    private TextView mEmail = null;
    private TextView mAddress = null;
    private TextView mPrefferdPhone = null;
    private TextView mMobile = null;
    private TextView mEmergencyContactPhone = null;
    private TextView mTimeZone = null;
    private TextView mChangePassword = null;
    private TextView mChangePin = null;
    private TextView mChangeSecurityQuestions = null;
    private Button mSave = null;
    private ProgressDialog pDialog;
    private String profileImageURL = null,profileName = null,userDOB = null,gender = null,username = null,address = null,prefferedPhone = null,mobile = null,emergencyContactPhone = null,
    timeZone = null,securityQuestion1 = null,securityQuestion2 = null,answer1 = null,answer2 = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myProfileView = inflater.inflate(R.layout.fragments_myprofile,null);

        mProfileImage = (RoundedImageView)myProfileView.findViewById(R.id.profileImg);
        mProfileName = (TextView)myProfileView.findViewById(R.id.profileName);
        mUserDOB = (TextView)myProfileView.findViewById(R.id.userDOB);
        mGender = (TextView)myProfileView.findViewById(R.id.gender);
        mUserName = (TextView)myProfileView.findViewById(R.id.txt_userName);
        mPreferredSignIn = (TextView)myProfileView.findViewById(R.id.txt_preferred_signin);
        mEmail = (TextView)myProfileView.findViewById(R.id.txt_language);
        mAddress = (TextView)myProfileView.findViewById(R.id.txt_address);
        mPrefferdPhone = (TextView)myProfileView.findViewById(R.id.txt_preferred_phone);
        mMobile = (TextView)myProfileView.findViewById(R.id.txt_mobile);
        mEmergencyContactPhone = (TextView)myProfileView.findViewById(R.id.txt_emergencyContanctPhone);
        mTimeZone = (TextView)myProfileView.findViewById(R.id.txt_timeZone);
        mChangePassword = (TextView)myProfileView.findViewById(R.id.btn_changePassword);
        mChangePin = (TextView)myProfileView.findViewById(R.id.btn_changePin);
        mChangeSecurityQuestions = (TextView)myProfileView.findViewById(R.id.btn_changeSecurityQuestion);
        mSave = (Button)myProfileView.findViewById(R.id.btn_save);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.profile_info));

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        getProfileInfoService();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSave.getText().toString().equalsIgnoreCase("Edit")) {
                    setProfileInfo();
                } else {
                    editProfileInfo();
                }
            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
                    ((MyAccountActivity)getActivity()).onChangePasswordClicked();
                }
                //getChildFragmentManager().beginTransaction().replace(R.id.tabcontent, new ChangePasswordFragment()).commit();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.replace(R.id.tabcontent, new ChangePasswordFragment()).commit();

            }
        });

        mChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
                    ((MyAccountActivity)getActivity()).onChangePinClicked();
                }
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.replace(R.id.tabcontent, OldPinFragment.newInstance()).commit();

            }
        });

        mChangeSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
                    ((MyAccountActivity)getActivity()).onSecurityQuestionClicked();
                }
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.replace(R.id.tabcontent, new SecurityQuestionsFragment()).commit();

            }
        });

        return myProfileView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setProfileInfo()
    {
        mUserName.setEnabled(true);
        mPreferredSignIn.setEnabled(true);
        mEmail.setEnabled(true);
        mAddress.setEnabled(true);
        mPrefferdPhone.setEnabled(true);
        mMobile.setEnabled(true);
        mEmergencyContactPhone.setEnabled(true);
        mTimeZone.setEnabled(true);
        mUserName.setEnabled(true);
        mSave.setText("Save");
    }

    private void getProfileInfoService() {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlegetProfileInfoSuccessResponse(response);
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

        GetProfileInfoService service = new GetProfileInfoService(getActivity(), null);
        service.getProfileInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetProfileInfoSuccessResponse(JSONObject response) {

        pDialog.dismiss();

        try {
            JSONObject myProfile = response.getJSONObject("personal_info");
            profileImageURL = myProfile.getString("image_url");
            profileName = myProfile.getString("first_name") +" "+ myProfile.getString("last_name");
            userDOB = myProfile.getString("birthdate");
            gender = myProfile.getString("gender");
            username = myProfile.getString("username");
            address = myProfile.getString("address1")+"\n"+myProfile.getString("address2")+"\n"+myProfile.getString("state")+"\n"+myProfile.getString("country")+"\n"+myProfile.getString("zipcode");
            prefferedPhone = myProfile.getString("phone");
            mobile = myProfile.getString("cell");
            emergencyContactPhone = myProfile.getString("emergency_contact_number");
            timeZone = myProfile.getString("timezone");

            Picasso.with(getActivity()).load(profileImageURL).placeholder(R.drawable.profilepic).error(R.drawable.profilepic).into(mProfileImage);

//            final ImageRequest imageRequest = new ImageRequest(profileImageURL, new Response.Listener<Bitmap>() {
//                @Override
//                public void onResponse(Bitmap response) {
//                    mCircularNetworkImageView.setImageBitmap(response);
//                }
//            }, 0, 0, null, null);
//            ApplicationController.getInstance().addToRequestQueue(imageRequest);
//
//
//            final ImageLoader imageLoader = ApplicationController.getInstance().getImageLoader(getActivity());
//            imageLoader.get(profileImageURL, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    mCircularNetworkImageView.setImageBitmap(response.getBitmap());
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });

            JSONObject securityQuestion = myProfile.getJSONObject("security");
            securityQuestion1 = securityQuestion.getString("question1");
            securityQuestion2 = securityQuestion.getString("question2");
            answer1 = securityQuestion.getString("answer1");
            answer2 = securityQuestion.getString("answer2");

            mProfileName.setText(profileName);
            mUserDOB.setText(userDOB);
            mGender.setText(gender);
            mUserName.setText(username);
            mAddress.setText(address);
            mPrefferdPhone.setText(prefferedPhone);
            mMobile.setText(mobile);
            mEmergencyContactPhone.setText(emergencyContactPhone);
            mTimeZone.setText(timeZone);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void loadProfileInfo(String params)
    {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleEditProfileInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                    }
                }
            }
        };

        EditMyProfileService service = new EditMyProfileService(getActivity(), null);
        service.editMyProfile(successCallBackListener, errorListener, params);
    }

    private void handleEditProfileInfoSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();

            handlegetProfileInfoSuccessResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editProfileInfo()
    {
        String UserName = mUserName.getText().toString();
        String Address = mAddress.getText().toString();
        String PrefferdPhone = mPrefferdPhone.getText().toString();
        String Mobile = mMobile.getText().toString();
        String TimeZone = mTimeZone.getText().toString();
        String EmergencyContactPhone = mEmergencyContactPhone.getText().toString();
        String UserDOB = mUserDOB.getText().toString();
        String Gender = mGender.getText().toString();

        if(isEmpty(UserName)&& isEmpty(Address)&& isEmpty(PrefferdPhone)&& isEmpty(Mobile)&& isEmpty(TimeZone)&& isEmpty(EmergencyContactPhone)&& isEmpty(UserDOB)&& isEmpty(Gender))
        {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", "vada@gmail.com");
                jsonObject.put("phone", PrefferdPhone);
                jsonObject.put("birthdate", UserDOB);
                jsonObject.put("state_id", "FL");
                jsonObject.put("first_name", UserName);
                jsonObject.put("address1", Address);
                jsonObject.put("address2", Address);
                jsonObject.put("gender", Gender);
                jsonObject.put("last_name", UserName);
                jsonObject.put("emergency_contact_number", EmergencyContactPhone);
                jsonObject.put("language_preference", "ko");

                parent.put("member", jsonObject);
                loadProfileInfo(parent.toString());
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
}
