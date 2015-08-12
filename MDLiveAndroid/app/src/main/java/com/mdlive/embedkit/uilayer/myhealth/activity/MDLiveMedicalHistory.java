package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter.ImageAdapter;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacy;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyResult;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCooridnates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateFemaleAttributeServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    private ProgressDialog pDialog;
    private JSONObject medicalAggregationJsonObject;
    private boolean isPregnant, isBreastfeeding, hasFemaleAttribute = false;
    private boolean isFemaleQuestionsDone = false, isConditionsDone = false,
            isAllergiesDone = false, isMedicationDone = false, isPediatricDone = false;
    private Button btnSaveContinue;
    private RadioGroup PediatricAgeCheckGroup_1, PediatricAgeCheckGroup_2, PreExisitingGroup,
            MedicationsGroup, AllergiesGroup, ProceduresGroup;
    private ImageView MyHealthCameraBtn;
    private ArrayList<String> myPhotosList;
    private ImageAdapter imageAdapter;
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_medical_history);
        btnSaveContinue = (Button) findViewById(R.id.SavContinueBtn);
        btnSaveContinue.setClickable(false);
        findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
        findViewById(R.id.SavContinueBtn).setVisibility(View.GONE);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", this);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        PediatricAgeCheckGroup_1 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) findViewById(R.id.conditionsGroup));
        MedicationsGroup = ((RadioGroup) findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) findViewById(R.id.allergiesGroup));
        ProceduresGroup = ((RadioGroup) findViewById(R.id.proceduresGroup));
        MyHealthCameraBtn = ((ImageView) findViewById(R.id.MyHealthCameraBtn));
        myPhotosList = new ArrayList<String>();

        findViewById(R.id.editConditionsTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
                startActivity(i);
            }
        });

        findViewById(R.id.editMedicationsTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
                startActivity(i);
            }
        });

        findViewById(R.id.editAllergiesTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                startActivity(i);
            }
        });

        findViewById(R.id.SavContinueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasFemaleAttribute) {
                    updateFemaleAttributes();
                } else {
                    getUserPharmacyDetails();
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLivePharmacy.class);
//                    startActivity(i);
                }
            }
        });

        saveDateOfBirth();
        initializeViews();
    }


    private void saveDateOfBirth() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.DATE_OF_BIRTH, "09-14-2008");
        MDLiveGetStarted.isFemale = true;
        editor.commit();
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
                    saveEntryForOptions(PreferenceConstants.IS_CONDITION_CHECKED, "true");
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
                    startActivity(i);
                } else {
                    saveEntryForOptions(PreferenceConstants.IS_CONDITION_CHECKED, "false");
                }
                ValidateModuleFields();
            }
        });
        MedicationsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.medicationsYesButton) {
                    saveEntryForOptions(PreferenceConstants.IS_MEDICATION_CHECKED, "true");
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
                    startActivity(i);
                } else {
                    saveEntryForOptions(PreferenceConstants.IS_MEDICATION_CHECKED, "false");
                }
                ValidateModuleFields();
            }
        });
        AllergiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.allergiesYesButton) {
                    saveEntryForOptions(PreferenceConstants.IS_ALLERGY_CHECKED, "true");
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                    startActivity(i);
                } else {
                    saveEntryForOptions(PreferenceConstants.IS_ALLERGY_CHECKED, "false");
                }
                ValidateModuleFields();
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

        MyHealthCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDeviceSupportCamera())
                    Toast.makeText(getApplicationContext(), "Your Device doesn't support have Camera Feature!",
                            Toast.LENGTH_SHORT).show();
                else
                if(myPhotosList.size() >= 8){
                    Toast.makeText(getApplicationContext(), "Maximum allowed photos is 8!", Toast.LENGTH_SHORT).show();
                }else{
                    captureImage();
                }
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

        loadPictureFromSDCard();
    }
    /*
 * Capturing Camera Image will lauch camera app requrest image capture
 */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
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
    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int IMAGE_PREVIEW_CODE = 200;

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
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
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

    private void loadPictureFromSDCard() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        myPhotosList.clear();
        for(int i=1; i<=8; i++){
            if(sharedpreferences.getString("photo"+i, null) != null){
                myPhotosList.add(sharedpreferences.getString("photo"+i, null));
            }
        }
        imageAdapter.notifyWithDataSet(myPhotosList);
    }

    public void storeImagePathInSharedPref(String uriPath){
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        for(int i=1; i<=8; i++){
            if(sharedpreferences.getString("photo"+i, null) == null){
                editor.putString("photo"+i, uriPath);
                i = 9;
            }
        }
        editor.commit();
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
                // display it in image view
                //previewCapturedImage();
                storeImagePathInSharedPref(fileUri.getPath());
                Log.e("uriPath", fileUri.getPath());
                loadPictureFromSDCard();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }else if(resultCode == RESULT_OK && requestCode == MDLiveMedicalHistory.IMAGE_PREVIEW_CODE){
            if(data.getStringExtra("imageId") != null){
                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                Log.e("data.imageId", data.getStringExtra("imageId"));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(data.getStringExtra("imageId"),null);
                editor.commit();
                loadPictureFromSDCard();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkMedicalAggregation();


    }

    private void applyValidationOnViews(){
        if (getEntryForOptions(PreferenceConstants.IS_ALLERGY_CHECKED) != null) {
            if (getEntryForOptions(PreferenceConstants.IS_ALLERGY_CHECKED).equals("true")) {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.VISIBLE);
                findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
                ((RadioButton) findViewById(R.id.allergiesNoButton)).setChecked(true);
            }
        }
        if (getEntryForOptions(PreferenceConstants.IS_MEDICATION_CHECKED) != null) {
            if (getEntryForOptions(PreferenceConstants.IS_MEDICATION_CHECKED).equals("true")) {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.VISIBLE);
                findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
                ((RadioButton) findViewById(R.id.medicationsNoButton)).setChecked(true);
            }
        }
        if (getEntryForOptions(PreferenceConstants.IS_CONDITION_CHECKED) != null) {
            if (getEntryForOptions(PreferenceConstants.IS_CONDITION_CHECKED).equals("true")) {
                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.VISIBLE);
                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
                ((RadioButton) findViewById(R.id.conditionNoButton)).setChecked(true);
            }
        }
        ValidateModuleFields();
    }
    private void saveEntryForOptions(String prefId, String value) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(prefId, value);
        editor.commit();
    }

    private String getEntryForOptions(String prefId) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(prefId, null);
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
            Log.d("Response", response.toString());
            medicalAggregationJsonObject = response;
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
                Log.e("Failed In", "hasFemaleAttribute");
            }
        }
        if (((LinearLayout) findViewById(R.id.MyHealthConditionChoiceLl)).getVisibility() == View.VISIBLE &&
                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
            Log.e("Failed In", "PreExisitingGroup");
        }
        if (((LinearLayout) findViewById(R.id.MyHealthMedicationsLl)).getVisibility() == View.VISIBLE &&
                MedicationsGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
            Log.e("Failed In", "MedicationsGroup");
        }
        if (((LinearLayout) findViewById(R.id.MyHealthAllergiesLl)).getVisibility() == View.VISIBLE &&
                AllergiesGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
            Log.e("Failed In", "MyHealthAllergiesLl");
        }
        if (isAllFieldsfilled) {
            btnSaveContinue.setBackgroundColor(getResources().getColor(R.color.green));
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
                Log.e("Rresponse", response.toString());
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
        try {
            MdliveUtils.handelVolleyErrorResponse(MDLiveMedicalHistory.this, error, null);
        }
        catch (Exception e) {
            MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
        }
    }


    /**
     * Successful Response Handler for Medical History Completion.
     */

    private void medicalCompletionHandleSuccessResponse(JSONObject response) {
        try {
            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
            //checkIsFirstTimeUser(historyPercentageArray);
            checkMyHealthHistory(historyPercentageArray);
            //checkPediatricProfile(historyPercentageArray);
            checkProcedure(historyPercentageArray);
            checkMyMedications(historyPercentageArray);
            checkAllergies(historyPercentageArray);
            checkAgeAndFemale();
            applyValidationOnViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the age of user and sex whether male or female to enable
     * Pediatric questions.
     */

    public void checkAgeAndFemale() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String dateOfBirth = sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH, "");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            int age = MdliveUtils.calculateAge(sdf.parse(dateOfBirth));
            if ((age >= 10 && age <= 12) && MDLiveGetStarted.isFemale) {
                ((LinearLayout) findViewById(R.id.PediatricAgeCheck1)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.PediatricAgeCheck2)).setVisibility(View.VISIBLE);
                hasFemaleAttribute = true;
            } else {
                hasFemaleAttribute = false;
            }

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
            findViewById(R.id.SavContinueBtn).setVisibility(View.VISIBLE);
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.e("conditions", myHealthPercentage + "");
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
                        Log.e("name", perdiatricQuestionArray.getJSONObject(i).getString("name"));
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
                Log.e("myHealthPercentage", myHealthPercentage + "");
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
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLiveMedicalHistory.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
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
            Log.d("Response", response.toString());
            jsonResponse = response.toString();
            JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
            if(pharmacyDatas == null){
                getLocationBtnOnClickAction();
            }else{
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
                /*if (map != null) {
                    LatLng markerPoint = new LatLng(Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("latitude")),
                            Double.parseDouble(pharmacyDatas.getJSONObject("coordinates").getString("longitude")));
                    Marker marker = map.addMarker(new MarkerOptions().position(markerPoint)
                            .title("Marker"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPoint, 10));
                }*/
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
            MdliveUtils.showGPSSettingsAlert(MDLiveMedicalHistory.this, null);
        }
    }



}
