package com.mdlive.embedkit.uilayer.myhealth;

import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;

/**
 * This class is for MDLiveMedicalHistory page.
 * User have to fill up yes/no questions for
 * Allergies
 * Medications
 * Conditions
 * Pediatric Profile
 * For female Pediatric users has two more questions to fill up.
 */

public class MDLiveMedicalHistory extends MDLiveBaseActivity {

//    public static ProgressDialog pDialog;
//    private JSONObject medicalAggregationJsonObject;
//    private boolean isPregnant, isBreastfeeding, hasFemaleAttribute = false;
//    private boolean isFemaleQuestionsDone = false, isConditionsDone = false,
//            isAllergiesDone = false, isMedicationDone = false, isPediatricDone = false, isNewUser = false;
//    private Button btnSaveContinue;
//    private RadioGroup PediatricAgeCheckGroup_1, PediatricAgeCheckGroup_2, PreExisitingGroup,
//            MedicationsGroup, AllergiesGroup, ProceduresGroup;
//    private ArrayList<HashMap<String, Object>> myPhotosList;
//    private ImageAdapter imageAdapter;
//    private GridView gridview;
//    public static Uri fileUri;
//    public JSONArray recordsArray;
//    private AlertDialog imagePickerDialog;
//    private loadDownloadedImages loadImageService;
//    private LocationCooridnates locationService;
//    private IntentFilter intentFilter;
//    private static List<BroadcastReceiver> registeredReceivers = new ArrayList<BroadcastReceiver>();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mdlive_medical_history);
//        btnSaveContinue = (Button) findViewById(R.id.SavContinueBtn);
//        btnSaveContinue.setClickable(false);
//        findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
//        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
//        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
//        PediatricAgeCheckGroup_1 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup1));
//        PediatricAgeCheckGroup_2 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup2));
//        PreExisitingGroup = ((RadioGroup) findViewById(R.id.conditionsGroup));
//        setProgressBar(findViewById(R.id.progressDialog));
//        MedicationsGroup = ((RadioGroup) findViewById(R.id.medicationsGroup));
//        AllergiesGroup = ((RadioGroup) findViewById(R.id.allergiesGroup));
//        ProceduresGroup = ((RadioGroup) findViewById(R.id.proceduresGroup));
//        myPhotosList = new ArrayList<HashMap<String, Object>>();
//        locationService = new LocationCooridnates(getApplicationContext());
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(getClass().getSimpleName());
//
//        initializeViews();
//        initializeYesNoButtonActions();
//        initializeCameraDialog();
//        checkMedicalDateHistory();
//    }
//
//    /**
//     * This function is used to update Medical history data in service
//     * MedicalHistoryUpdateServices :: This class is used to update medical history. This class holds data ot update service
//     *
//     */
//    private void updateMedicalHistory(){
//        showProgress();
//        ((ProgressBar) findViewById(R.id.thumpProgressBar)).setVisibility(View.GONE);
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                hideProgress();
//                if (hasFemaleAttribute) {
//                    updateFemaleAttributes();
//                } else {
//                    getUserPharmacyDetails();
//                }
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        try {
//            boolean hasAllergies = false, hasConditions = false, hasMedications = false, hasProcedures = false;
//            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            hasAllergies = !(healthHistory.getJSONArray("allergies").length() == 0);
//            hasConditions = !(healthHistory.getJSONArray("conditions").length() == 0);
//            hasMedications = !(healthHistory.getJSONArray("medications").length() == 0);
//            HashMap<String,String> updateMap = new HashMap<String,String>();
//            updateMap.put("Do you have any health conditions?", hasConditions?"Yes":"No");
//            updateMap.put("Are you currently taking any medication?", hasMedications?"Yes":"No");
//            updateMap.put("Do you have any Allergies or Drug Sensitivities?", hasAllergies?"Yes":"No");
//            updateMap.put("Have you ever had any surgeries or medical procedures?", hasProcedures?"Yes":"No");
//            HashMap<String, HashMap<String,String>> medhistoryMap = new HashMap<String, HashMap<String,String>>();
//            medhistoryMap.put("medical_history",updateMap);
//            MedicalHistoryUpdateServices services = new MedicalHistoryUpdateServices(MDLiveMedicalHistory.this, null);
//            services.updateMedicalHistoryRequest(medhistoryMap, successCallBackListener, errorListener);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * This function is used to initialize clickListners of Buttons used in MedicalHistory page
//     */
//
//    public void MyHealthConditionsLlOnClick(View view){
//        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
//        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//    }
//
//    public void MedicationsLlOnClick(View view){
//        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
//        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//    }
//
//    public void AllergiesLlOnClick(View view){
//        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
//        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//    }
//
//    public void MyHealthAddPhotoL2OnClick(View view){
//        if(!(myPhotosList.size() >= 8)){
//            imagePickerDialog.show();
//        }
//    }
//
//    public void backImgOnClick(View view){
//        onBackPressed();
//    }
//
//    public void SavContinueBtnOnClick(View view){
//        updateMedicalHistory();
//    }
//
//    public void initializeViews() {
//        imageAdapter = new ImageAdapter(MDLiveMedicalHistory.this, myPhotosList);
//        gridview = (GridView) findViewById(R.id.gridview);
//        gridview.setAdapter(imageAdapter);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(MDLiveMedicalHistory.this, "" + position, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * This function is used to initialize Yes/No Button actions used in layout.
//     */
//    private void initializeYesNoButtonActions() {
//        PediatricAgeCheckGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.perdiatric1YesButton)
//                    isPregnant = true;
//                else
//                    isPregnant = false;
//                ValidateModuleFields();
//            }
//        });
//        PediatricAgeCheckGroup_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.perdiatric2YesButton)
//                    isBreastfeeding = true;
//                else
//                    isBreastfeeding = false;
//                ValidateModuleFields();
//            }
//        });
//        PreExisitingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.conditionYesButton) {
//                    PreExisitingGroup.clearCheck();
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
//                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                }else{
//                    ValidateModuleFields();
//                }
//            }
//        });
//        MedicationsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.medicationsYesButton) {
//                    MedicationsGroup.clearCheck();
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
//                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                }else{
//                    ValidateModuleFields();
//                }
//            }
//        });
//        AllergiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.allergiesYesButton) {
//                    AllergiesGroup.clearCheck();
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
//                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
//                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                }else{
//                    ValidateModuleFields();
//                }
//            }
//        });
//        ProceduresGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.proceduresYesButton) {
//                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
//                    startActivity(i);
//                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                }
//                ValidateModuleFields();
//            }
//        });
//    }
//
//    /**
//     *  This function is used to initialized Camera Dialog.
//     *  According to user chooses Camera/Gallery it will navigate to appropriate intents.
//     */
//    public void initializeCameraDialog() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLiveMedicalHistory.this);
//        alertDialogBuilder
//                .setMessage("Use image from")
//                .setPositiveButton("Camera", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        imagePickerDialog.dismiss();
//                        if (!isDeviceSupportCamera()){
//                            MdliveUtils.alert(null, getApplicationContext(), "Your Device doesn't support have Camera Feature!");
//                        }else{
//                                captureImage();
//                        }
//                    }
//                })
//                .setNegativeButton("Gallery", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        imagePickerDialog.dismiss();
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, IntegerConstants.PICK_IMAGE_REQUEST_CODE);
//                        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                    }
//                })
//                .setNeutralButton("Close", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        imagePickerDialog.dismiss();
//                    }
//                });
//        imagePickerDialog = alertDialogBuilder.create();
//        imagePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                imagePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
//                imagePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
//                imagePickerDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
//            }
//        });
//    }
//
//    /*
//     * Capturing Camera Image will lauch camera app requrest image capture
//     */
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        fileUri = getOutputMediaFileUri();
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//        startActivityForResult(intent, IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//    }
//    /**
//     * Creating file uri to store image/video
//     */
//    public Uri getOutputMediaFileUri() {
//        return Uri.fromFile(getOutputMediaFile());
//    }
//
//    /**
//     * Checking device has camera hardware or not
//     */
//    private boolean isDeviceSupportCamera() {
//        if (getApplicationContext().getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA)) {
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
//
//
//    // directory name to store captured images and videos
//    private static final String IMAGE_DIRECTORY_NAME = "MDLive Images";
//
//    /*
//     * returning image / video
//     */
//    private static File getOutputMediaFile() {
//        // External sdcard location
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                IMAGE_DIRECTORY_NAME);
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());
//        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");
//        return mediaFile;
//    }
//
//    /**
//     *  This function is used to Check size of Picked/Captured Image from device
//     *  If it exceeds more than 10 mb then it will make alert to user about size exceeding
//     *
//     *  @param file :: Image file captured or picked by user
//     */
//    public String checkSizeOfImageAndType(File file){
//        boolean acceptSize = true;
//        double bytes = file.length();
//        double kilobytes = (bytes / 1024);
//        double megabytes = (kilobytes / 1024);
//        double hasexceededSize = 10.0000000 - megabytes;
//        if(hasexceededSize < 0){
//                acceptSize = false;
//        }
//        if(!acceptSize)
//            return "Please add a photo with a maximum size of 10 MB";
//        else
//            return null;
//    }
//
//    /**
//     * Receiving activity result method will be called after closing the camera
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // if the result is capturing Image
//        if (requestCode == IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // successfully captured the image
//                if(fileUri != null){
//                    File file = new File(fileUri.getPath());
//                    String hasErrorText = checkSizeOfImageAndType(file);
//                    if(file.exists() && hasErrorText == null){
//                       uploadMedicalRecordService(fileUri.getPath());
//                    }else{
//                        MdliveUtils.showDialog(MDLiveMedicalHistory.this,
//                                hasErrorText,
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        captureImage();
//                                    }
//                                },
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }
//                        );
//                    }
//                }
//            }
//        }
//
//        if (requestCode == IntegerConstants.PICK_IMAGE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    fileUri = data.getData();
//
//                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                    Cursor cursor = getContentResolver().query(fileUri,filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    File file = new File(picturePath);
//                    String hasErrorText = checkSizeOfImageAndType(file);
//
//                    if(fileUri != null && hasErrorText == null){
//                        uploadMedicalRecordService(getPath(fileUri));
//                    }else{
//                        MdliveUtils.showDialog(MDLiveMedicalHistory.this,
//                                hasErrorText,
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                                        photoPickerIntent.setType("image/*");
//                                        startActivityForResult(photoPickerIntent, IntegerConstants.PICK_IMAGE_REQUEST_CODE);
//                                        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//                                    }
//                                },
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }
//                                );
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if(requestCode == IntegerConstants.RELOAD_REQUEST_CODE) {
//            if (resultCode == RESULT_OK && data != null) {
//                if(data.hasExtra("medicationData")){
//                    findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
//                    findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
//                    ((TextView)findViewById(R.id.MedicationsNameTv)).setText(data.getStringExtra("medicationData"));
//                }else if(data.hasExtra("conditionsData")){
//                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
//                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
//                    ((TextView)findViewById(R.id.MyHealthConditionsNameTv)).setText(data.getStringExtra("conditionsData"));
//                }else if(data.hasExtra("allegiesData")){
//                    findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
//                    findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
//                    ((TextView)findViewById(R.id.AlergiesNameTv)).setText(data.getStringExtra("allegiesData"));
//                }
//            }
//        }
//
//        if(requestCode == IntegerConstants.IMAGE_PREVIEW_CODE) {
//            if (resultCode == RESULT_OK) {
//                downloadMedicalRecordService();
//            }
//        }
//    }
//
//    /**
//     * helper to retrieve the path of an image URI
//     */
//    public String getPath(Uri uri) {
//        // just some safety built in
//        if( uri == null ) {
//            return null;
//        }
//        // try to retrieve the image from the media store first
//        // this will only work for images selected from gallery
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if( cursor != null ){
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//        // this is our fallback here
//        return uri.getPath();
//    }
//
//    @Override
//    public void onResume() {
//        try {
//            ValidateModuleFields();
//            locationService.setBroadCastData(StringConstants.DEFAULT);
//            if(loadImageService != null && loadImageService.getStatus().equals(AsyncTask.Status.RUNNING)){
//                ((ProgressBar) findViewById(R.id.thumpProgressBar)).setVisibility(View.VISIBLE);
//            }
//            registeredReceivers.clear();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        try {
//            if(loadImageService != null){
//                loadImageService.cancel(true);
//            }
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onPause();
//        try {
//            locationService.setBroadCastData(StringConstants.DEFAULT);
//            if(locationService != null && locationService.isTrackingLocation()){
//                locationService.stopListners();
//            }
//            if(registeredReceivers != null){
//                for(BroadcastReceiver receiver : registeredReceivers){
//                    unregisterReceiver(receiver);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * This function is used to upload new medical record to service
//     * UploadImageService - This class file is used to upload record to service.
//     * @param filePath :: path of image in Device.
//     */
//    private void uploadMedicalRecordService(String filePath) {
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                hideProgress();
//                try {
//                    if(response!= null){
//                        if(response.has("message")){
//                            if(response.getString("message").equals("Document uploaded successfully")){
//                                downloadMedicalRecordService();
//                            }
//                        }else{
//                            Toast.makeText(getApplicationContext(), "Something Went Wrong in Uploading Document!",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        UploadImageService services = new UploadImageService(MDLiveMedicalHistory.this, null);
//        services.doUploadDocumentService(new File(filePath), successCallBackListener, errorListener);
//    }
//
//
//    /**
//     * Checks user medical history aggregation details.
//     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
//     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
//     * Based on the server response the corresponding action will be triggered.
//     */
//
//    private void downloadMedicalRecordService() {
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                handleDownloadRecordService(response);
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        DownloadMedicalService services = new DownloadMedicalService(MDLiveMedicalHistory.this, null);
//        services.doDownloadImagesRequest(successCallBackListener, errorListener);
//    }
//
//
//    /**
//     * This function is used to handle downloaded records data
//     * Once data parsed, imageAdapter will be notified to display images in UI.
//     */
//    public void handleDownloadRecordService(JSONObject response) {
//        hideProgress();
//        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<>();
//        try {
//            if (response != null && response.toString().contains("No Previous Documents Found")) {
//                gridview.setVisibility(View.GONE);
//            } else if (response != null && response.has("records")) {
//                listDatas.clear();
//                recordsArray = response.getJSONArray("records");
//                for (int i = 0; i < recordsArray.length(); i++) {
//                    JSONObject jsonObject = recordsArray.getJSONObject(i);
//                    HashMap<String, Object> data = new HashMap<>();
//                    data.put("download_link", jsonObject.getString("download_link"));
//                    data.put("doc_type", jsonObject.getString("doc_type"));
//                    data.put("uploaded_by", jsonObject.getString("uploaded_by"));
//                    data.put("doc_name", jsonObject.getString("doc_name"));
//                    data.put("id", jsonObject.getInt("id"));
//                    data.put("uploaded_at", jsonObject.getString("uploaded_at"));
//                    listDatas.add(data);
//                }
//
//                if (recordsArray.length() >= 8) {
//                    ((TextView) findViewById(R.id.takephotoTxt)).setTextColor(getResources().getColor(R.color.grey_txt));
//                    ((ImageView) findViewById(R.id.MyHealthCameraBtn)).setImageResource(R.drawable.camera_gray_icon);
//                } else {
//                    ((TextView) findViewById(R.id.takephotoTxt)).setTextColor(Color.BLACK);
//                    ((ImageView) findViewById(R.id.MyHealthCameraBtn)).setImageResource(R.drawable.camera_icon);
//                }
//                if (recordsArray != null) {
//                    if (recordsArray.length() > 4) {
//                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);
//                        int width = dm.widthPixels;
//                        gridview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                (width / 2) + 100));
//                    } else if (recordsArray.length() > 0) {
//                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);
//                        int width = dm.widthPixels;
//                        gridview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                (width / 4) + 25));
//                    }
//                }
//
//                if (recordsArray != null && recordsArray.length() > 0) {
//                    gridview.setVisibility(View.VISIBLE);
//                    loadImageService = new loadDownloadedImages();
//                    loadImageService.execute();
//                } else {
//                    gridview.setVisibility(View.GONE);
//                }
//
//
//               /* boolean hasPendingDownloads = false;
//                try {
//                    for(int i =0; i<recordsArray.length(); i++){
//                        JSONObject jsonObject = recordsArray.getJSONObject(i);
//                        if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null) {
//                            hasPendingDownloads = true;
//                            i = recordsArray.length();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    hasPendingDownloads = true;
//                }
//                if(hasPendingDownloads){
//                    if(recordsArray != null){
//                        for(int i =0; i<recordsArray.length(); i++){
//                            JSONObject jsonObject = recordsArray.getJSONObject(i);
//                            try {
//                                if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null){
//                                    downloadImageService(jsonObject.getInt("id"));
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    *//*loadDownloadedImages loadImageService = new loadDownloadedImages(recordsArray);
//                    loadImageService.execute();*//*
//                }*/
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        myPhotosList = listDatas;
//        imageAdapter.notifyWithDataSet(listDatas);
//        /*loadDownloadedImages l=new loadDownloadedImages("");
//        if(l.getStatus().equals(AsyncTask.Status.RUNNING)){
//
//        }*/
//
//    }
//
//    /**
//     * This function is used to handle downloaded records data
//     * Once data parsed, imageAdapter will be notified to display images in UI.
//     */
//    class loadDownloadedImages extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected void onPreExecute() {
//            if(!(progressBarLayout.getVisibility() == View.VISIBLE)){
//                ((ProgressBar) findViewById(R.id.thumpProgressBar)).setVisibility(View.VISIBLE);
//            }
//            super.onPreExecute();
//        }
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if(imageAdapter != null)
//                imageAdapter.notifyDataSetChanged();
//            boolean hasPendingDownloads = false;
//            try {
//                if(recordsArray != null){
//                    for(int i =0; i<recordsArray.length(); i++){
//                        JSONObject jsonObject = recordsArray.getJSONObject(i);
//                        if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null) {
//                            hasPendingDownloads = true;
//                            i = recordsArray.length();
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                hasPendingDownloads = true;
//            }
//            if(hasPendingDownloads && loadImageService != null && !loadImageService.isCancelled()){
//                loadImageService = new loadDownloadedImages();
//                loadImageService.execute();
//            }else{
//                ((ProgressBar) findViewById(R.id.thumpProgressBar)).setVisibility(View.GONE);
//            }
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                if(recordsArray != null){
//                    for(int i =0; i<recordsArray.length(); i++){
//                        JSONObject jsonObject = recordsArray.getJSONObject(i);
//                        try {
//                            if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null){
//                                String response = makeImageRequestCall(AppSpecificConfig.BASE_URL + AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE + "/"+jsonObject.getInt("id"));
//                                if(response != null && response.length() != 0){
//                                    handleDownloadImageService(new JSONObject(response), jsonObject.getInt("id"));
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
//
//    /* For Testing Purpose Get Method Call*/
//    public String makeImageRequestCall(String urlString) throws Exception {
//        //Url link for choose provider details
//        URL url = new URL(urlString);
//        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//        urlConnection.setRequestProperty("Content-Type", "application/json");
//        urlConnection.setRequestMethod("GET");
//        urlConnection.setDoInput(true);
//        urlConnection.setDoOutput(true);
//        urlConnection.setUseCaches(false);
//        urlConnection.setConnectTimeout(30000);
//        urlConnection.setRequestProperty("Connection", "Keep-Alive");
//        urlConnection.setChunkedStreamingMode(0);
//
//        String creds = String.format("%s:%s", AppSpecificConfig.API_KEY,AppSpecificConfig.SECRET_KEY);
//        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
//        urlConnection.setRequestProperty("Authorization", auth);
//        urlConnection.setRequestProperty("RemoteUserId", sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
////        urlConnection.setRequestProperty("RemoteUserId", MDLiveConfig.USR_UNIQ_ID); // for SSO2 we no longer persist sensitive data in sharedprefs
//
//        String dependentId = sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
//        if(dependentId != null) {
//            urlConnection.setRequestProperty("DependantId", dependentId);
//        }
//        if (urlConnection.getResponseCode() == 200) {
//            InputStream in = urlConnection.getInputStream();
//            return convertInputStreamToString(in);
//        } else {
//            return null;
//        }
//    }
//
//    /** This function is used to convert Inputstream Datas to String type*/
//    private String convertInputStreamToString(InputStream inputStream) throws IOException {
//        int n = 0;
//        char[] buffer = new char[1024 * 4];
//        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
//        StringWriter writer = new StringWriter();
//        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
//        return writer.toString();
//    }
//
//    /**
//     * Checks user medical history aggregation details.
//     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
//     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
//     * Based on the server response the corresponding action will be triggered.
//     */
//
//    private void downloadImageService(final int photoId) {
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                //handleDownloadImageService(response, photoId);
//                //hideProgress();
//                try {
//                    if(response != null && response.length() != 0 && photoId != 0){
//                        Log.e("Response -->", response.toString());
//                        handleDownloadImageService(response, photoId);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if(imageAdapter != null)
//                    imageAdapter.notifyDataSetChanged();
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                pDialog.dismiss();
//                //progressBar.setVisibility(View.GONE);
//                //hideProgress();
//                //medicalCommonErrorResponseHandler(error);
//            }
//        };
//        DownloadMedicalImageService services = new DownloadMedicalImageService(this, null);
//        services.doDownloadImagesRequest(photoId, successCallBackListener, errorListener);
//    }
//
//    /**
//     * This method is used to handle downloaded byte data content from webservice.
//     * @param photoId :: id of image
//     * @param response :: response received from service.
//     */
//    public void handleDownloadImageService(JSONObject response, int photoId){
//        try {
//            if(response != null){
//                if(response.has("message") && response.getString("message").equals("Document found")){
//                    byte[] bytes = Base64.decode(response.getString("file_stream").getBytes("UTF-8"), Base64.DEFAULT);
//                    if(bytes == null){
//                    }else{
//                        feedDatasInVolleyCache(photoId+"", bytes);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * This function is used to feed data to volley cache
//     * @param bytes :: byte data to be inserted.
//     * @param photoId :: id of photo to be display.
//     */
//    public void feedDatasInVolleyCache(String photoId, byte[] bytes){
//        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveMedicalHistory.this).getCache();
//        Cache.Entry entry = new Cache.Entry();
//        entry.etag = photoId+"";
//        entry.data = bytes;
//        cache.put(photoId + "", entry);
//    }
//
//    /**
//     * This function is used to get Cache Entry of photo
//     * @param photoId :: id of photo to be display.
//     */
//    public Cache.Entry getDatasInVolleyCache(String photoId){
//        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveMedicalHistory.this).getCache();
//        Cache.Entry entry = new Cache.Entry();
//        entry = cache.get(photoId+"");
//        return entry;
//    }
//
//    /**
//     * Checks user medical history aggregation details.
//     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
//     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
//     * Based on the server response the corresponding action will be triggered.
//     */
//
//    private void checkMedicalDateHistory() {
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                hideProgress();
//                try {
//                    if(response.get("health_last_update") instanceof Number){
//                        long num=response.getLong("health_last_update");
//                        int length = (int) Math.log10(num) + 1;
//                        System.out.println(length);
//                    }else if(response.get("health_last_update") instanceof CharSequence){
//                        if(response.getString("health_last_update").equals("")){
//                        }
//                    }
//                        if(response.getString("health_last_update").length() == 0){
//                                isNewUser = true;
//                        }else{
//                            if(response.has("health_last_update")){
//                                long time = response.getLong("health_last_update");
//                                if(time != 0){
//                                    Calendar calendar = Calendar.getInstance();
//                                    calendar.setTimeInMillis(time * 1000);
//                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//                                    ((LinearLayout)findViewById(R.id.UpdateInfoWindow)).setVisibility(View.VISIBLE);
//                                    ((TextView)findViewById(R.id.updateInfoText)).setText(
//                                            getResources().getString(R.string.last_update_txt)+
//                                                    dateFormat.format(calendar.getTime())
//                                    );
//                                    isNewUser = false;
//                                }
//                            }
//                        }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    isNewUser = true;
//                }
//                checkMedicalAggregation();
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(MDLiveMedicalHistory.this, null);
//        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
//    }
//
//    /**
//     * Checks user medical history aggregation details.
//     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
//     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
//     * Based on the server response the corresponding action will be triggered.
//     */
//
//    private void checkMedicalAggregation() {
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                medicalAggregationHandleSuccessResponse(response);
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//               // Log.e("error", error.getMessage());
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(MDLiveMedicalHistory.this, null);
//        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
//    }
//
//    /**
//     * Handling the response of medical Aggregation webservice response.
//     */
//
//    private void medicalAggregationHandleSuccessResponse(JSONObject response) {
//        try {
//            medicalAggregationJsonObject = response;
//            checkMedicalCompletion();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Applying validation on form and enable/disable continue button for further steps over.
//     */
//
//    public void ValidateModuleFields() {
//        boolean isAllFieldsfilled = true;
//        if (hasFemaleAttribute) {
//            if (PediatricAgeCheckGroup_1.getCheckedRadioButtonId() < 0
//                    || PediatricAgeCheckGroup_2.getCheckedRadioButtonId() < 0) {
//                isAllFieldsfilled = false;
//            }
//        }
//        if (((LinearLayout) findViewById(R.id.MyHealthConditionChoiceLl)).getVisibility() == View.VISIBLE &&
//                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
//            isAllFieldsfilled = false;
//        }
//        if (((LinearLayout) findViewById(R.id.MyHealthMedicationsLl)).getVisibility() == View.VISIBLE &&
//                MedicationsGroup.getCheckedRadioButtonId() < 0) {
//            isAllFieldsfilled = false;
//        }
//        if (((LinearLayout) findViewById(R.id.MyHealthAllergiesLl)).getVisibility() == View.VISIBLE &&
//                AllergiesGroup.getCheckedRadioButtonId() < 0) {
//            isAllFieldsfilled = false;
//        }
//        if (isAllFieldsfilled) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                btnSaveContinue.setBackground(getResources().getDrawable(R.drawable.btn_rounded_bg));
//            } else {
//                btnSaveContinue.setBackgroundResource(R.drawable.btn_rounded_bg);
//            }
//            btnSaveContinue.setClickable(true);
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                btnSaveContinue.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
//            } else {
//                btnSaveContinue.setBackgroundResource(R.drawable.btn_rounded_grey);
//            }
//            btnSaveContinue.setClickable(false);
//        }
//    }
//
//    /**
//     * Checks user medical history completion details.
//     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
//     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
//     * Based on the server response the corresponding action will be triggered.
//     */
//    private void checkMedicalCompletion() {
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                medicalCompletionHandleSuccessResponse(response);
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        MedicalHistoryCompletionServices services = new MedicalHistoryCompletionServices(MDLiveMedicalHistory.this, null);
//        services.getMedicalHistoryCompletionRequest(successCallBackListener, errorListener);
//    }
//
//    /**
//     * Error Response Handler for Medical History Completion.
//     */
//    private void medicalCommonErrorResponseHandler(VolleyError error) {
//        hideProgress();
//        NetworkResponse networkResponse = error.networkResponse;
//        if (networkResponse != null) {
//            String message = "No Internet Connection";
//            if (networkResponse.statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//                message = "Internal Server Error";
//            } else if (networkResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
//                message = "Unprocessable Entity Error";
//            } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
//                message = "Page Not Found";
//            }
//            MdliveUtils.showDialog(MDLiveMedicalHistory.this, "Error",
//                    "Server Response : " + message);
//        }
//    }
//    /**
//     * Successful Response Handler for Medical History Completion.
//     */
//    private void medicalCompletionHandleSuccessResponse(JSONObject response) {
//        try {
//            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
//            //checkIsFirstTimeUser(historyPercentageArray);
//            checkMyHealthHistory(historyPercentageArray);
//            //checkPediatricProfile(historyPercentageArray);
//            checkProcedure(historyPercentageArray);
//            checkMyMedications(historyPercentageArray);
//            checkAllergies(historyPercentageArray);
//            ValidateModuleFields();
//            checkAgeAndFemale();
//            downloadMedicalRecordService();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Check the age of user and sex whether male or female to enable
//     * Pediatric questions.
//     */
//    public void checkAgeAndFemale() {
//        try {
//            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
//            String gender = sharedpreferences.getString(PreferenceConstants.GENDER, "");
//
//            if(MdliveUtils.calculteAgeFromPrefs(MDLiveMedicalHistory.this)>=10) {
//                        if(gender.equalsIgnoreCase("Female")){
//                            ((LinearLayout) findViewById(R.id.PediatricAgeCheck1)).setVisibility(View.VISIBLE);
//                            ((LinearLayout) findViewById(R.id.PediatricAgeCheck2)).setVisibility(View.VISIBLE);
//                            hasFemaleAttribute = true;
//                        }
//                    }else{
//                        hasFemaleAttribute = false;
//                    }
//
//            ValidateModuleFields();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * This will check weather the user has completed the allergy section and will hide and
//     * display teh layouts accordingly.
//     *
//     * @param historyPercentageArray - The history percentage JSONArray
//     */
//    private void checkAllergies(JSONArray historyPercentageArray) {
//        try {
//            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
//            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("allergies").length() == 0)) {
//                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
//                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
//                String conditonsNames = "";
//                JSONArray conditonsArray = healthHistory.getJSONArray("allergies");
//                for (int i = 0; i < conditonsArray.length(); i++) {
//                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
//                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
//                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
//                        if (i != conditonsArray.length() - 1) {
//                            conditonsNames += ", ";
//                        }
//                    }
//                }
//                if (conditonsNames.trim().length() == 0)
//                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.no_allergies_reported));
//                else
//                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText(conditonsNames);
//            } else {
//                ((TextView) findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.no_allergies_reported));
//            }
//            findViewById(R.id.ContainerScrollView).setVisibility(View.VISIBLE);
//            hideProgress();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//            if(isNewUser){
//                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.VISIBLE);
//                findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
//            }else{
//                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
//                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
//            }
//
//    }
//
//    /**
//     * This will check weather the user has completed the medications section and will hide and
//     * display teh layouts accordingly.
//     *
//     * @param historyPercentageArray - The history percentage JSONArray
//     */
//    private void checkMyMedications(JSONArray historyPercentageArray) {
//        try {
//            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
//            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("medications").length() == 0)) {
//                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
//                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
//                String conditonsNames = "";
//                JSONArray conditonsArray = healthHistory.getJSONArray("medications");
//                for (int i = 0; i < conditonsArray.length(); i++) {
//                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
//                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
//                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
//                        if (i != conditonsArray.length() - 1) {
//                            conditonsNames += ", ";
//                        }
//                    }
//                }
//                if (conditonsNames.trim().length() == 0)
//                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.no_medications_reported));
//                else
//                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText(conditonsNames);
//            } else {
//                ((TextView) findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.no_medications_reported));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//            if(isNewUser){
//                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.VISIBLE);
//                findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
//            }else{
//                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
//                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
//            }
//
//
//        if(MedicationsGroup.getCheckedRadioButtonId() > 0 &&
//                MedicationsGroup.getCheckedRadioButtonId() == R.id.medicationsYesButton){
//            ((RadioButton) findViewById(R.id.medicationsYesButton)).setChecked(false);
//        }
//
//    }
//
//    /**
//     * This will check weather the user has completed the my health condition section and will hide and
//     * display teh layouts accordingly.
//     *
//     * @param historyPercentageArray - The history percentage JSONArray
//     */
//    private void checkMyHealthHistory(JSONArray historyPercentageArray) {
//        try {
//            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
//            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("conditions").length() == 0)) {
//                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
//                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
//                String conditonsNames = "";
//                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
//                for (int i = 0; i < conditonsArray.length(); i++) {
//                    if (conditonsArray.getJSONObject(i).getString("condition").trim() != null &&
//                            !conditonsArray.getJSONObject(i).getString("condition").trim().equals("")) {
//                        conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
//                        if (i != conditonsArray.length() - 1) {
//                            conditonsNames += ", ";
//                        }
//                    }
//                }
//                if (conditonsNames.trim().length() == 0)
//                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.no_conditions_reported));
//                else
//                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(conditonsNames);
//            } else {
//                ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.no_conditions_reported));
//            }
//
//                if(isNewUser){
//                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.VISIBLE);
//                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
//                }else{
//                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
//                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
//                }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
//                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
//            ((RadioButton) findViewById(R.id.conditionYesButton)).setChecked(false);
//        }
//    }
//
//
//    /**
//     * This will check weather the user has completed the Pediatric Profile section and will hide and
//     * display teh layouts accordingly.
//     *
//     * @param historyPercentageArray - The history percentage JSONArray
//     */
//    private void checkPediatricProfile(JSONArray historyPercentageArray) {
//        try {
//            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            int myHealthPercentage = -1;
//            for (int j = 0; j < historyPercentageArray.length(); j++) {
//                if (historyPercentageArray.getJSONObject(j).has("pediatric"))
//                    myHealthPercentage = historyPercentageArray.getJSONObject(j).getInt("pediatric");
//            }
//            if (myHealthPercentage == 100) {
////                ((TextView) findViewById(R.id.PediatricNameTv)).setText("Completed");
//            } else if (myHealthPercentage >= 0) {
//                if (!(healthHistory.getJSONObject("pediatric") == null)) {
//                    String pediotricNames = "";
//                    JSONObject pediatricObject = healthHistory.getJSONObject("pediatric");
//                    JSONArray perdiatricQuestionArray = pediatricObject.getJSONArray("questions");
//                    for (int i = 0; i < perdiatricQuestionArray.length(); i++) {
//                        if (perdiatricQuestionArray.getJSONObject(i).getString("name").trim() != null &&
//                                !perdiatricQuestionArray.getJSONObject(i).getString("name").trim().equals("")) {
//                            pediotricNames += perdiatricQuestionArray.getJSONObject(i).getString("name");
//                            if (i != perdiatricQuestionArray.length() - 1) {
//                                pediotricNames += ", ";
//                            }
//                        }
//                    }
////                    ((TextView) findViewById(R.id.PediatricNameTv)).setText(pediotricNames);
//                }
//            } else {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * This will check weather the user has completed the Pediatric Profile section and will hide and
//     * display teh layouts accordingly.
//     *
//     * @param historyPercentageArray - The history percentage JSONArray
//     */
//    private void checkProcedure(JSONArray historyPercentageArray) {
//        try {
//            /*JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
//            String myHealthPercentage = historyPercentageArray.getJSONObject(0).getString("health");
//            if (myHealthPercentage!=null && !"0".equals(myHealthPercentage) && !(healthHistory.getJSONArray("conditions").length() == 0)){
//                findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
////                findViewById(R.id.ProceduresLl).setVisibility(View.VISIBLE);
//                String conditonsNames = "";
//                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
//                for(int i = 0;i<conditonsArray.length();i++){
//                    conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
//                    if(i!=conditonsArray.length() - 1){
//                        conditonsNames += ", ";
//                    }
//                }
//                ((TextView)findViewById(R.id.ProcedureNameTv)).setText(conditonsNames);
//            }*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Sending the answer details of Female pediatric users
//     */
//    private void updateFemaleAttributes() {
//        HashMap<String, String> femaleAttributes = new HashMap<String, String>();
//        femaleAttributes.put("is_pregnant", isPregnant + "");
//        femaleAttributes.put("is_breast_feeding", isBreastfeeding + "");
//        HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
//        postBody.put("female_questions", femaleAttributes);
//        showProgress();
//        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                hideProgress();
//                getUserPharmacyDetails();
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                hideProgress();
//                medicalCommonErrorResponseHandler(error);
//            }
//        };
//        UpdateFemaleAttributeServices services = new UpdateFemaleAttributeServices(MDLiveMedicalHistory.this, null);
//        services.updateFemaleAttributeRequest(new Gson().toJson(postBody), successCallBackListener, errorListener);
//    }
//
//    /*
//   * This function will get latest default pharmacy details of users from webservice.
//   * PharmacyService class handles webservice integration.
//   * @responseListener - Receives webservice informatoin
//   * @errorListener - Received error information (if any problem in webservice)
//   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
//   * to parse message content.
//   */
//    public void getUserPharmacyDetails() {
//        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                handleSuccessResponse(response);
//            }
//        };
//        NetworkErrorListener errorListener = new NetworkErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                hideProgress();
//                if (error.networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        };
//                        // Show timeout error message
//                        MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
//                    }
//                }
//            }
//        };
//        callPharmacyService(responseListener, errorListener);
//    }
//
//
//
//    /**
//     *  This method is used to call pharmacy service
//     *  In pharmacy service, it requires GPS location details to get distance details.
//     *
//     *  @param errorListener - Pharmacy error response listener
//     *  @param responseListener - Pharmacy detail Success response listener
//     */
//    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
//                                            final NetworkErrorListener errorListener){
//        if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
//            showProgress();
//            registerReceiver(locationReceiver, intentFilter);
//            registeredReceivers.add(locationReceiver);
//            locationService.setBroadCastData(getClass().getSimpleName());
//            locationService.startTrackingLocation(getApplicationContext());
//        }else{
//            PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
//            services.doMyPharmacyRequest("","",responseListener, errorListener);
//        }
//    }
//
//    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            unregisterReceiver(locationReceiver);
//            registeredReceivers.remove(locationReceiver);
//            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    handleSuccessResponse(response);
//                }
//            };
//            NetworkErrorListener errorListener = new NetworkErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    hideProgress();
//                    if (error.networkResponse == null) {
//                        if (error.getClass().equals(TimeoutError.class)) {
//                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            };
//                            // Show timeout error message
//                            MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
//                        }
//                    }
//                }
//            };
//            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
//                double lat=intent.getDoubleExtra("Latitude",0d);
//                double lon=intent.getDoubleExtra("Longitude",0d);
//                if(lat!=0 && lon!=0){
//                    PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
//                    services.doMyPharmacyRequest(lat+"", +lon+"",
//                            responseListener, errorListener);
//                }else{
//                    PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
//                    services.doMyPharmacyRequest("","",responseListener, errorListener);
//                }
//            }else{
//                PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
//                services.doMyPharmacyRequest("","",responseListener, errorListener);
//            }
//        }
//    };
//
//
//
//    /* This function handles webservice response and parsing the contents.
//    *  Once parsing operation done, then it will update UI
//    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
//    */
//    Bundle bundletoSend = new Bundle();
//    String jsonResponse;
//
//    private void handleSuccessResponse(JSONObject response) {
//        try {
//            hideProgress();
//            jsonResponse = response.toString();
//            if(response.has("message")){
//                if(response.getString("message").equals("No pharmacy selected")){
//                    getLocationBtnOnClickAction();
//                }
//            }else{
//                JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
//                bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
//                JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
//                bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
//                bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
//                bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
//                bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
//                bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
//                bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
//                bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
//                bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
//                bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
//                bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
//                bundletoSend.putString("city", pharmacyDatas.getString("city"));
//                bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
//                bundletoSend.putString("state", pharmacyDatas.getString("state"));
//                String res = response.toString();
//                Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
//                i.putExtra("Response",res);
//                startActivity(i);
//                MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * OnClickAction for Location Button Action. This will get the current location. If the current
//     * location is received, starts teh MDLivePharmacyResult activity.
//     *
//     *
//     */
//    private void getLocationBtnOnClickAction() {
//        if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
//            showProgress();
//            registerReceiver(newUserReceiver, intentFilter);
//            registeredReceivers.add(newUserReceiver);
//            locationService.setBroadCastData(getClass().getSimpleName());
//            locationService.startTrackingLocation(getApplicationContext());
//        }else{
//            Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
//            i.putExtra("Response",jsonResponse);
//            startActivity(i);
//            MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//        }
//    }
//
//    public BroadcastReceiver newUserReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            hideProgress();
//            unregisterReceiver(newUserReceiver);
//            registeredReceivers.remove(newUserReceiver);
//            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
//                double lat=intent.getDoubleExtra("Latitude",0d);
//                double lon=intent.getDoubleExtra("Longitude",0d);
//                Intent i = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
//                i.putExtra("longitude", lat+"");
//                i.putExtra("latitude", lon+"");
//                i.putExtra("errorMesssage", "No Pharmacies listed in your location");
//                startActivity(i);
//                MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//            }else{
//                Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
//                i.putExtra("Response",jsonResponse);
//                startActivity(i);
//                MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
//            }
//        }
//    };
//
//    /**
//     * This method will close the activity with transition effect.
//     */
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        MdliveUtils.closingActivityAnimation(MDLiveMedicalHistory.this);
//    }
//    /**
//     * This method will stop the service call if activity is closed during service call.
//     */

}
