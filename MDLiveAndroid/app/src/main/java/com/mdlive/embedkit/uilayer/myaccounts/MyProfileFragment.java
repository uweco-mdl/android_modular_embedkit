package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.ChangeProfilePicService;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetProfileInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.squareup.picasso.Picasso;

/**
 * Created by venkataraman_r on 6/18/2015.
 */
public class MyProfileFragment extends MDLiveBaseFragment {

    private CircularNetworkImageView mProfileImage = null;
    private CircularNetworkImageView mCircularNetworkImageView;
    private TextView mProfileName = null;
    private TextView mUserDOB = null;
    private TextView mGender = null;
    private TextView mUserName = null;
    private TextView mPreferredSignIn = null;
    private TextView mEmail = null;
    private TextView mAddress = null;
    private TextView mMobile = null;
    private TextView mTimeZone = null;
    private TextView mChangePassword = null;
    private TextView mChangePin = null;
    private TextView mChangeSecurityQuestions = null;
    private Button mSave = null;
    private String profileImageURL = null,profileName = null,userDOB = null,gender = null,username = null,address = null,prefferedPhone = null,mobile = null,emergencyContactPhone = null,
    timeZone = null,securityQuestion1 = null,securityQuestion2 = null,answer1 = null,answer2 = null,email = null;

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
        mEmail = (TextView)view.findViewById(R.id.email);
        mAddress = (TextView)view.findViewById(R.id.address);
        mMobile = (TextView)view.findViewById(R.id.phoneNumber);
        mTimeZone = (TextView)view.findViewById(R.id.timeZone);
        mChangePassword = (TextView)view.findViewById(R.id.changePassword);
        mChangePin = (TextView)view.findViewById(R.id.changePin);
        mChangeSecurityQuestions = (TextView)view.findViewById(R.id.changeSecurityQuestion);


        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
//                    ((MyAccountActivity)getActivity()).onChangePasswordClicked();
//                }
            }
        });

        mChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
//                    ((MyAccountActivity)getActivity()).onChangePinClicked();
//                }

            }
        });

        mChangeSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (getActivity() != null && getActivity() instanceof MyAccountActivity) {
//                    ((MyAccountActivity)getActivity()).onSecurityQuestionClicked();
//                }
            }
        });

        mPreferredSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {
                        "Pin", "Password"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        mPreferredSignIn.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

//        mProfileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        GetProfileInfoService service = new GetProfileInfoService(getActivity(), null);
        service.getProfileInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetProfileInfoSuccessResponse(JSONObject response) {
        hideProgressDialog();

        try {
            JSONObject myProfile = response.getJSONObject("personal_info");
            profileImageURL = myProfile.getString("image_url");
            profileName = myProfile.getString("first_name"); /*+" "+ myProfile.getString("last_name")*/;
            email = myProfile.getString("email");
            userDOB = myProfile.getString("birthdate");
            gender = myProfile.getString("gender");
            username = myProfile.getString("username");
            address = myProfile.getString("address1")+"\n"+myProfile.getString("address2")+"\n"+myProfile.getString("state")+"\n"+myProfile.getString("country")+"\n"+myProfile.getString("zipcode");
            mobile = myProfile.getString("cell");
            timeZone = myProfile.getString("timezone");

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
            mMobile.setText(mobile);
            mTimeZone.setText(timeZone);
            mEmail.setText(email);
            mPreferredSignIn.setText("Pin");
            mProfileImage.setImageUrl(profileImageURL, ApplicationController.getInstance().getImageLoader(getActivity()));
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
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 2);
                } else if (items[item].equals("Choose from Library")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        switch (requestCode) {

            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageReturnedIntent.getData());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mProfileImage.setImageBitmap(bitmap);
                    convertToBase64(bitmap);
                }
                break;

            case 2:
                if(resultCode == Activity.RESULT_OK) {
                    bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    mProfileImage.setImageBitmap(bitmap);
                    convertToBase64(bitmap);
                }

                break;
        }
    }

    public void convertToBase64(Bitmap selectedImage){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte b[] = baos.toByteArray();
        String base64String = Base64.encodeToString(b, Base64.DEFAULT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());

        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_name", currentTimeStamp+".jpg");
            jsonObject.put("photo", base64String);
            parent.put("personal_information", jsonObject);
            loadChangeProfilePicService(parent.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadChangeProfilePicService(String params) {
     showProgressDialog();

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
                        // Show timeout error message
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
            hideProgressDialog();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
