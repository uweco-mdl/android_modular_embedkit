package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter.ImageAdapter;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacy;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacyResult;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.DownloadMedicalImageService;
import com.mdlive.unifiedmiddleware.services.myhealth.DownloadMedicalService;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateFemaleAttributeServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UploadImageService;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class is for MDLiveMedicalHistory page.
 * User have to fill up yes/no questions for
 * Allergies
 * Medications
 * Conditions
 * Pediatric Profile
 * For female Pediatric users has two more questions to fill up.
 */

public class MDLiveMedicalHistory extends Activity {


    public static ProgressDialog pDialog;
    private JSONObject medicalAggregationJsonObject;
    private boolean isPregnant, isBreastfeeding, hasFemaleAttribute = false;
    private boolean isFemaleQuestionsDone = false, isConditionsDone = false,
            isAllergiesDone = false, isMedicationDone = false, isPediatricDone = false, isNewUser = false;
    private Button btnSaveContinue;
    private RadioGroup PediatricAgeCheckGroup_1, PediatricAgeCheckGroup_2, PreExisitingGroup,
            MedicationsGroup, AllergiesGroup, ProceduresGroup;
    private ArrayList<HashMap<String, Object>> myPhotosList;
    private ImageAdapter imageAdapter;
    private GridView gridview;
    public static Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST_CODE = 101;
    public static final int IMAGE_PREVIEW_CODE = 200;
    private static final int RELOAD_REQUEST_CODE = 111;
    private AlertDialog imagePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_medical_history);
        btnSaveContinue = (Button) findViewById(R.id.SavContinueBtn);
        btnSaveContinue.setClickable(false);
        clearCacheInVolley();
        findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
//        findViewById(R.id.SavContinueBtn).setVisibility(View.GONE);
        pDialog = Utils.getProgressDialog("Please wait...", this);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        PediatricAgeCheckGroup_1 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) findViewById(R.id.conditionsGroup));
        MedicationsGroup = ((RadioGroup) findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) findViewById(R.id.allergiesGroup));
        ProceduresGroup = ((RadioGroup) findViewById(R.id.proceduresGroup));
        myPhotosList = new ArrayList<HashMap<String, Object>>();

//        Utils.photoList.clear();


        findViewById(R.id.editConditionsTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
                startActivityForResult(i, RELOAD_REQUEST_CODE);
            }
        });

        findViewById(R.id.editMedicationsTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
                startActivityForResult(i, RELOAD_REQUEST_CODE);
            }
        });

        findViewById(R.id.editAllergiesTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                startActivityForResult(i, RELOAD_REQUEST_CODE);
            }
        });

        btnSaveContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewUser) {
                    //First Time User
                    updateMedicalHistory();
                } else {
                    getUserPharmacyDetails();
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLivePharmacy.class);
//                    startActivity(i);
                }
            }
        });

//        saveDateOfBirth();
        initializeViews();

        checkMedicalDateHistory();
    }

    private void updateMedicalHistory(){
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (hasFemaleAttribute) {
                    updateFemaleAttributes();
                } else {
                    getUserPharmacyDetails();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        try {
            boolean hasAllergies = false, hasConditions = false, hasMedications = false, hasProcedures = false;
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            hasAllergies = !(healthHistory.getJSONArray("allergies").length() == 0);
            hasConditions = !(healthHistory.getJSONArray("conditions").length() == 0);
            hasMedications = !(healthHistory.getJSONArray("medications").length() == 0);
            HashMap<String,String> updateMap = new HashMap<String,String>();
            updateMap.put("Do you have any health conditions?", hasConditions?"Yes":"No");
            updateMap.put("Are you currently taking any medication?", hasMedications?"Yes":"No");
            updateMap.put("Do you have any Allergies or Drug Sensitivities?", hasAllergies?"Yes":"No");
            updateMap.put("Have you ever had any surgeries or medical procedures?", hasProcedures?"Yes":"No");
            HashMap<String, HashMap<String,String>> medhistoryMap = new HashMap<String, HashMap<String,String>>();
            medhistoryMap.put("medical_history",updateMap);
            MedicalHistoryUpdateServices services = new MedicalHistoryUpdateServices(MDLiveMedicalHistory.this, null);
            services.updateMedicalHistoryRequest(medhistoryMap, successCallBackListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void initializeViews() {

        PediatricAgeCheckGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.perdiatric1YesButton)
                    isPregnant = true;
                else
                    isPregnant = false;
                ValidateModuleFields();
            }
        });

        PediatricAgeCheckGroup_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.perdiatric2YesButton)
                    isBreastfeeding = true;
                else
                    isBreastfeeding = false;
                ValidateModuleFields();
            }
        });

        PreExisitingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.conditionYesButton) {
                    PreExisitingGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
                    startActivityForResult(i, RELOAD_REQUEST_CODE);
                }else{
                    ValidateModuleFields();
                }
            }
        });
        MedicationsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.medicationsYesButton) {
                    MedicationsGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
                    startActivityForResult(i, RELOAD_REQUEST_CODE);
                }else{
                    ValidateModuleFields();
                }
            }
        });
        AllergiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.allergiesYesButton) {
                    AllergiesGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                    startActivityForResult(i, RELOAD_REQUEST_CODE);
                }else{
                    ValidateModuleFields();
                }
            }
        });

        ProceduresGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.proceduresYesButton) {
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                    startActivity(i);
                }
                ValidateModuleFields();
            }
        });

        ((LinearLayout) findViewById(R.id.MyHealthAddPhotoL2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myPhotosList.size() >= 8){
                    Utils.alert(null, MDLiveMedicalHistory.this, "Maximum allowed photos is 8!");
                }else{
                    imagePickerDialog.show();
                }
            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });

        imageAdapter = new ImageAdapter(MDLiveMedicalHistory.this, myPhotosList);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MDLiveMedicalHistory.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        initializeCameraDialog();
    }


    public void initializeCameraDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLiveMedicalHistory.this);

        alertDialogBuilder
                .setMessage("Pick Image From")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imagePickerDialog.dismiss();
                        if (!isDeviceSupportCamera()){
                            Utils.alert(null, getApplicationContext(), "Your Device doesn't support have Camera Feature!");
                        }else{
                                captureImage();
                        }

                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imagePickerDialog.dismiss();
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);
                    }
                })
                .setNeutralButton("Close", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imagePickerDialog.dismiss();
                    }
                });

        imagePickerDialog = alertDialogBuilder.create();
    }

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // set the video image quality to high
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    // Activity request codes


    /* Checking device has camera hardware or not
    * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MDLive Images";

    /*
     * returning image / video
     */
    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                if(fileUri != null){
                    File file = new File(fileUri.getPath());
                    if(file.exists()){
                        //int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                        //Log.e("Size of File..", file_size+"");
                        uploadMedicalRecordService(fileUri.getPath());
                    }
                }
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to Upload Image!", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    fileUri = data.getData();
                    if(fileUri != null)
                        uploadMedicalRecordService(getPath(fileUri));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == RELOAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                if(data.hasExtra("medicationData")){
                    findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                    findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.MedicationsNameTv)).setText(data.getStringExtra("medicationData"));
                }else if(data.hasExtra("conditionsData")){
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.MyHealthConditionsNameTv)).setText(data.getStringExtra("conditionsData"));
                }else if(data.hasExtra("allegiesData")){
                    findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                    findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.AlergiesNameTv)).setText(data.getStringExtra("allegiesData"));
                }
            }
        }

        if(requestCode == IMAGE_PREVIEW_CODE) {
            if (resultCode == RESULT_OK) {
                downloadMedicalRecordService();
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    @Override
    protected void onResume() {
        ValidateModuleFields();
        /*if(imageAdapter != null)
            imageAdapter.notifyWithDataSet(myPhotosList);*/
        super.onResume();
    }


    /*private void saveEntryForOptions(String prefId, String value) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(prefId, value);
        editor.commit();
    }

    private String getEntryForOptions(String prefId) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(prefId, null);
    }*/

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void uploadMedicalRecordService(String filePath) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    if(response!= null){
                        if(response.has("message")){
                            if(response.getString("message").equals("Document uploaded successfully")){
                                downloadMedicalRecordService();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Something Went Wrong in Uploading Document!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        File file = new File(filePath);

        UploadImageService services = new UploadImageService(MDLiveMedicalHistory.this, null);
        services.doUploadDocumentService(new File(filePath), successCallBackListener, errorListener);
    }


    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void downloadMedicalRecordService() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleDownloadRecordService(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        DownloadMedicalService services = new DownloadMedicalService(MDLiveMedicalHistory.this, null);
        services.doDownloadImagesRequest(successCallBackListener, errorListener);
    }


    public void handleDownloadRecordService(JSONObject response){
        pDialog.dismiss();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<>();
        try {
            if(response != null && response.toString().contains("No Previous Documents Found")){
                gridview.setVisibility(View.GONE);
            }else if(response != null && response.has("records")){
                listDatas.clear();
                JSONArray recordsArray = response.getJSONArray("records");
                Log.e("myPhotosList", response.toString());
                for(int i = 0; i<recordsArray.length(); i++){
                    JSONObject jsonObject = recordsArray.getJSONObject(i);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("download_link", jsonObject.getString("download_link"));
                    data.put("doc_type", jsonObject.getString("doc_type"));
                    data.put("uploaded_by", jsonObject.getString("uploaded_by"));
                    data.put("doc_name", jsonObject.getString("doc_name"));
                    data.put("id", jsonObject.getInt("id"));
                    data.put("uploaded_at", jsonObject.getString("uploaded_at"));
                    listDatas.add(data);
                    /*if(Utils.photoList.get(jsonObject.getInt("id")) == null)
                        downloadImageService(jsonObject.getInt("id"));*/
                }
                if(recordsArray.length() > 0){
                    gridview.setVisibility(View.VISIBLE);
                }else{
                    gridview.setVisibility(View.GONE);
                }

                if(recordsArray.length() >= 8){
                    ((TextView) findViewById(R.id.takephotoTxt)).setTextColor(getResources().getColor(R.color.grey_txt));
                    ((ImageView) findViewById(R.id.MyHealthCameraBtn)).setImageResource(R.drawable.camera_gray_icon);
                }else{
                    ((TextView) findViewById(R.id.takephotoTxt)).setTextColor(Color.BLACK);
                    ((ImageView) findViewById(R.id.MyHealthCameraBtn)).setImageResource(R.drawable.camera_icon);
                }

                if(recordsArray != null){
                    if(recordsArray.length() > 4){
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels;
                        gridview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                (width/2)+50));
                    }else if(recordsArray.length() > 0){
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels;
                        gridview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                (width/4)+25));
                    }
                }

                boolean hasPendingDownloads = false;

                try {
                    for(int i =0; i<recordsArray.length(); i++){
                        JSONObject jsonObject = recordsArray.getJSONObject(i);
                        if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null) {
                            hasPendingDownloads = true;
                            i = recordsArray.length();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hasPendingDownloads = true;
                }

                if(hasPendingDownloads){
                    loadDownloadedImages loadImageService = new loadDownloadedImages(recordsArray);
                    loadImageService.execute();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myPhotosList = listDatas;
        imageAdapter.notifyWithDataSet(listDatas);
    }

    class loadDownloadedImages extends AsyncTask<Void, Void, Void>{

        JSONArray recordsArray;

        public loadDownloadedImages(JSONArray recordsArray){
            this.recordsArray = recordsArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            pDialog.dismiss();
            if(imageAdapter != null)
                imageAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(recordsArray != null){
                    for(int i =0; i<recordsArray.length(); i++){
                        JSONObject jsonObject = recordsArray.getJSONObject(i);
                        try {
                            if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null){
                                String response = makeImageRequestCall(AppSpecificConfig.BASE_URL + AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE + "/"+jsonObject.getInt("id"));
                                if(response != null && response.length() != 0)
                                    handleDownloadImageService(new JSONObject(response), jsonObject.getInt("id"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    /* For Testing Purpose Get Method Call*/
    public String makeImageRequestCall(String urlString) throws Exception {
        //Url link for choose provider details
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(30000);
        String creds = String.format("%s:%s", AppSpecificConfig.API_KEY,AppSpecificConfig.SECRET_KEY);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
        urlConnection.setRequestProperty("Authorization", auth);
        urlConnection.setRequestProperty("RemoteUserId", sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
        String dependentId = sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        if(dependentId != null) {
            urlConnection.setRequestProperty("DependantId", dependentId);
        }
        if (urlConnection.getResponseCode() == 200) {
            InputStream in = urlConnection.getInputStream();
            return convertInputStreamToString(in);
        } else {
            return null;
        }
    }

    /** This function is used to convert Inputstream Datas to String type*/
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
      /*  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        inputStream.close();*/
        Log.e("Data... ", writer.toString());
        return writer.toString();
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void downloadImageService(final int photoId) {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleDownloadImageService(response, photoId);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                medicalCommonErrorResponseHandler(error);
            }
        };
        DownloadMedicalImageService services = new DownloadMedicalImageService(this, null);
        services.doDownloadImagesRequest(photoId, successCallBackListener, errorListener);
    }


    public void handleDownloadImageService(JSONObject response, int photoId){
        try {
            if(response != null){
                if(response.has("message") && response.getString("message").equals("Document found")){

                    /*byte[] bytes = com.mdlive.unifiedmiddleware.commonclasses.utils.Base64.decode(response.getString("file_stream"),
                            com.mdlive.unifiedmiddleware.commonclasses.utils.Base64.DECODE);*/
                    byte[] bytes = Base64.decode(response.getString("file_stream").getBytes("UTF-8"), Base64.DEFAULT);
                    //response.getString("file_stream").getBytes("UTF-8");
                    if(bytes == null){
                    }else{
                        feedDatasInVolleyCache(photoId+"", bytes);
                    }
                    //Utils.mphotoList.put(photoId, response.getString("file_stream"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCacheInVolley(){
        ApplicationController.getInstance().getRequestQueue(MDLiveMedicalHistory.this).getCache().clear();
        ApplicationController.getInstance().getBitmapLruCache().evictAll();
    }

    public void feedDatasInVolleyCache(String photoId, byte[] bytes){
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveMedicalHistory.this).getCache();
        Cache.Entry entry = new Cache.Entry();
        entry.etag = photoId+"";
        entry.data = bytes;
        cache.put(photoId+"", entry);
    }

    public Cache.Entry getDatasInVolleyCache(String photoId){
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveMedicalHistory.this).getCache();
        Cache.Entry entry = new Cache.Entry();
        entry = cache.get(photoId+"");
        return entry;
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void checkMedicalDateHistory() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    if(response.get("health_last_update") instanceof Number){
                        Log.e("health_last_update ", "number");
                        long num=response.getLong("health_last_update");
                        int length = (int) Math.log10(num) + 1;
                        System.out.println(length);
                    }else if(response.get("health_last_update") instanceof CharSequence){
                        Log.e("health_last_update ", "String");
                        if(response.getString("health_last_update").equals("")){
                            Log.e("health_last_update ", "String is empty");
                        }
                    }

                        if(response.getString("health_last_update").length() == 0){
                                isNewUser = true;
                        }else{
                            if(response.has("health_last_update")){
                                long time = response.getLong("health_last_update");
                                if(time != 0){
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(time * 1000);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                    ((LinearLayout)findViewById(R.id.UpdateInfoWindow)).setVisibility(View.VISIBLE);
                                    ((TextView)findViewById(R.id.updateInfoText)).setText(
                                            "Last Updated : "+
                                                    dateFormat.format(calendar.getTime())
                                    );
                                    isNewUser = false;
                                }
                            }
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                    isNewUser = true;
                }
                checkMedicalAggregation();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
    }




    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void checkMedicalAggregation() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                medicalAggregationHandleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
    }

    /**
     * Handling the response of medical Aggregation webservice response.
     */

    private void medicalAggregationHandleSuccessResponse(JSONObject response) {
        try {
            medicalAggregationJsonObject = response;
            Log.e("Health History -->", medicalAggregationJsonObject.toString());
            checkMedicalCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applying validation on form and enable/disable continue button for further steps over.
     */

    public void ValidateModuleFields() {
        boolean isAllFieldsfilled = true;
        if (hasFemaleAttribute) {
            if (PediatricAgeCheckGroup_1.getCheckedRadioButtonId() < 0
                    || PediatricAgeCheckGroup_2.getCheckedRadioButtonId() < 0) {
                isAllFieldsfilled = false;
            }
        }
        if (((LinearLayout) findViewById(R.id.MyHealthConditionChoiceLl)).getVisibility() == View.VISIBLE &&
                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (((LinearLayout) findViewById(R.id.MyHealthMedicationsLl)).getVisibility() == View.VISIBLE &&
                MedicationsGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (((LinearLayout) findViewById(R.id.MyHealthAllergiesLl)).getVisibility() == View.VISIBLE &&
                AllergiesGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (isAllFieldsfilled) {
            btnSaveContinue.setBackgroundColor(Color.parseColor("#2b7db5"));
            btnSaveContinue.setClickable(true);
        } else {
            btnSaveContinue.setBackgroundColor(getResources().getColor(R.color.grey_txt));
            btnSaveContinue.setClickable(false);
        }
    }

    /**
     * Checks user medical history completion details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalCompletion() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                medicalCompletionHandleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryCompletionServices services = new MedicalHistoryCompletionServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryCompletionRequest(successCallBackListener, errorListener);
    }

    /**
     * Error Response Handler for Medical History Completion.
     */
    private void medicalCommonErrorResponseHandler(VolleyError error) {
        pDialog.dismiss();
        NetworkResponse networkResponse = error.networkResponse;
/*
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8" );
            Log.e("Error Message", responseBody)
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
*/
        if (networkResponse != null) {
            String message = "No Internet Connection";
            if (networkResponse.statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                message = "Internal Server Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                message = "Unprocessable Entity Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                message = "Page Not Found";
            }
            Utils.showDialog(MDLiveMedicalHistory.this, "Error",
                            "Server Response : " + message);

        }
    }
    /**
     * Successful Response Handler for Medical History Completion.
     */

    private void medicalCompletionHandleSuccessResponse(JSONObject response) {
        try {
            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
           /* if(getEntryForOptions(PreferenceConstants.IS_FIRST_TIME_USER) == null &&
                    getEntryForOptions(PreferenceConstants.IS_FIRST_TIME_USER).equals("false")){
                isNewUser = isUserFirstToApp(historyPercentageArray);
            }*/
            //checkIsFirstTimeUser(historyPercentageArray);
            checkMyHealthHistory(historyPercentageArray);
            //checkPediatricProfile(historyPercentageArray);
            checkProcedure(historyPercentageArray);
            checkMyMedications(historyPercentageArray);
            checkAllergies(historyPercentageArray);
//            applyValidationOnViews();
            ValidateModuleFields();
            checkAgeAndFemale();
            downloadMedicalRecordService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public boolean isUserFirstToApp(JSONArray historyPercentageArray){
        int havingHealth = -1, liftStyle = -1, pediatric = -1;
        try {
            for(int i = 0; i < historyPercentageArray.length(); i++)
            {
                JSONObject subObj = historyPercentageArray.getJSONObject(i);
                if(subObj.has("health")){
                    int myHealthPercentage = subObj.getInt("health");
                    if(myHealthPercentage == 0){
                        havingHealth = 1;
                    }
                }
                if(subObj.has("life_style")){
                    int life_styleValue = subObj.getInt("life_style");
                    if(life_styleValue == 0){
                        liftStyle = 1;
                    }
                }
                if(subObj.has("pediatric")){
                    int pediatricValue = subObj.getInt("pediatric");
                    if(pediatricValue == 0){
                        pediatric = 1;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(havingHealth == 1){
            saveEntryForOptions(PreferenceConstants.IS_FIRST_TIME_USER, "true");
        }else{
            saveEntryForOptions(PreferenceConstants.IS_FIRST_TIME_USER, "false");
        }
        if(havingHealth == 1){
            return  true;
        }else{
            return false;
        }
    }*/

    /**
     * Check the age of user and sex whether male or female to enable
     * Pediatric questions.
     */

    public void checkAgeAndFemale() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String gender = sharedpreferences.getString(PreferenceConstants.GENDER, "");

            if(Utils.calculteAgeFromPrefs(MDLiveMedicalHistory.this)>=10) {
                        if(gender.equalsIgnoreCase("Female")){
                            ((LinearLayout) findViewById(R.id.PediatricAgeCheck1)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.PediatricAgeCheck2)).setVisibility(View.VISIBLE);
                            hasFemaleAttribute = true;
                        }
                    }else{
                        hasFemaleAttribute = false;
                    }

            ValidateModuleFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will check weather the user has completed the allergy section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkAllergies(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("allergies").length() == 0)) {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("allergies");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText("No allergies reported");
                else
                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.AlergiesNameTv)).setText("No allergies reported");
            }
            findViewById(R.id.ContainerScrollView).setVisibility(View.VISIBLE);
//            findViewById(R.id.SavContinueBtn).setVisibility(View.VISIBLE);
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

            if(isNewUser){
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.VISIBLE);
                findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
            }else{
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
            }

    }

    /**
     * This will check weather the user has completed the medications section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */

    private void checkMyMedications(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("medications").length() == 0)) {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("medications");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText("No medications reported");
                else
                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.MedicationsNameTv)).setText("No medications reported");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

            if(isNewUser){
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.VISIBLE);
                findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
            }else{
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
            }


        if(MedicationsGroup.getCheckedRadioButtonId() > 0 &&
                MedicationsGroup.getCheckedRadioButtonId() == R.id.medicationsYesButton){
            ((RadioButton) findViewById(R.id.medicationsYesButton)).setChecked(false);
        }

    }

    /**
     * This will check weather the user has completed the my health condition section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */

    private void checkMyHealthHistory(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("conditions").length() == 0)) {
                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("condition").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("condition").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText("No conditions reported");
                else
                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText("No conditions reported");
            }

                if(isNewUser){
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.VISIBLE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
            ((RadioButton) findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    }


    /**
     * This will check weather the user has completed the Pediatric Profile section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */

    private void checkPediatricProfile(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = -1;
            for (int j = 0; j < historyPercentageArray.length(); j++) {
                if (historyPercentageArray.getJSONObject(j).has("pediatric"))
                    myHealthPercentage = historyPercentageArray.getJSONObject(j).getInt("pediatric");
            }
            if (myHealthPercentage == 100) {
//                ((TextView) findViewById(R.id.PediatricNameTv)).setText("Completed");
            } else if (myHealthPercentage >= 0) {
                if (!(healthHistory.getJSONObject("pediatric") == null)) {
                    String pediotricNames = "";
                    JSONObject pediatricObject = healthHistory.getJSONObject("pediatric");
                    JSONArray perdiatricQuestionArray = pediatricObject.getJSONArray("questions");
                    for (int i = 0; i < perdiatricQuestionArray.length(); i++) {
                        if (perdiatricQuestionArray.getJSONObject(i).getString("name").trim() != null &&
                                !perdiatricQuestionArray.getJSONObject(i).getString("name").trim().equals("")) {
                            pediotricNames += perdiatricQuestionArray.getJSONObject(i).getString("name");
                            if (i != perdiatricQuestionArray.length() - 1) {
                                pediotricNames += ", ";
                            }
                        }
                    }
//                    ((TextView) findViewById(R.id.PediatricNameTv)).setText(pediotricNames);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will check weather the user has completed the Pediatric Profile section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */

    private void checkProcedure(JSONArray historyPercentageArray) {
        try {
            /*JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            String myHealthPercentage = historyPercentageArray.getJSONObject(0).getString("health");
            if (myHealthPercentage!=null && !"0".equals(myHealthPercentage) && !(healthHistory.getJSONArray("conditions").length() == 0)){
                findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
//                findViewById(R.id.ProceduresLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
                for(int i = 0;i<conditonsArray.length();i++){
                    conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
                    if(i!=conditonsArray.length() - 1){
                        conditonsNames += ", ";
                    }
                }
                ((TextView)findViewById(R.id.ProcedureNameTv)).setText(conditonsNames);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sending the answer details of Female pediatric users
     */
    private void updateFemaleAttributes() {
        HashMap<String, String> femaleAttributes = new HashMap<String, String>();
        femaleAttributes.put("is_pregnant", isPregnant + "");
        femaleAttributes.put("is_breast_feeding", isBreastfeeding + "");
        HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
        postBody.put("female_questions", femaleAttributes);

        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                getUserPharmacyDetails();
//                Intent i = new Intent(MDLiveMedicalHistory.this, MDLivePharmacy.class);
//                startActivity(i);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                medicalCommonErrorResponseHandler(error);
            }
        };

    /*  Log.e("new Gson().toJson(postBody)", new Gson().toJson(postBody));*/

        UpdateFemaleAttributeServices services = new UpdateFemaleAttributeServices(MDLiveMedicalHistory.this, null);
        services.updateFemaleAttributeRequest(new Gson().toJson(postBody), successCallBackListener, errorListener);
    }

    /*
   * This function will get latest default pharmacy details of users from webservice.
   * PharmacyService class handles webservice integration.
   * @responseListener - Receives webservice informatoin
   * @errorListener - Received error information (if any problem in webservice)
   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
   * to parse message content.
   */

    public void getUserPharmacyDetails() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
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
                        Utils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
                    }
                }
            }
        };
        PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
        services.doMyPharmacyRequest(responseListener, errorListener);
    }

    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */
    Bundle bundletoSend = new Bundle();
    String jsonResponse;

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            jsonResponse = response.toString();

            if(response.has("message")){
                if(response.getString("message").equals("No pharmacy selected")){
                    getLocationBtnOnClickAction();
                }
            }else{
                JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
                bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
                JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
                bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
                bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
                bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
                bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
                bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
                bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
                bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
                bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
                bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
                bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
                bundletoSend.putString("city", pharmacyDatas.getString("city"));
                bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
                bundletoSend.putString("state", pharmacyDatas.getString("state"));
                String res = response.toString();

                Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.putExtra("Response",res);
                startActivity(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OnClickAction for Location Button Action. This will get the current location. If the current
     * location is received, starts teh MDLivePharmacyResult activity.
     *
     *
     */
    private void getLocationBtnOnClickAction() {
        LocationCooridnates locationService = new LocationCooridnates();
        if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
            pDialog.show();
            locationService.getLocation(this, new LocationCooridnates.LocationResult(){
                @Override
                public void gotLocation(final Location location) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();
                            if(location != null){
                                Intent i = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
                                i.putExtra("longitude", location.getLongitude());
                                i.putExtra("latitude", location.getLatitude());
                                startActivity(i);
                            }else{
                                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
                                i.putExtra("Response",jsonResponse);
                                startActivity(i);
                            }
                        }
                    });
                }
            });
        }else{
            Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
            i.putExtra("Response",jsonResponse);
            startActivity(i);
            //Utils.showGPSSettingsAlert(MDLiveMedicalHistory.this);
        }
    }


    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLiveMedicalHistory.this, MDLiveLogin.class);
    }

}