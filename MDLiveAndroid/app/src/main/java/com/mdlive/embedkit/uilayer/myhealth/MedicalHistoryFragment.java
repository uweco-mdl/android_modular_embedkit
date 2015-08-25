package com.mdlive.embedkit.uilayer.myhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateFemaleAttributeServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MedicalHistoryFragment.OnMedicalHistoryResponse} interface
 * to handle interaction events.
 * Use the {@link MedicalHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalHistoryFragment extends MDLiveBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static ProgressDialog pDialog;
    private JSONObject medicalAggregationJsonObject;
    private boolean isPregnant, isBreastfeeding, hasFemaleAttribute = false;
    private boolean isFemaleQuestionsDone = false, isConditionsDone = false,
            isAllergiesDone = false, isMedicationDone = false, isPediatricDone = false, isNewUser = false;
    private Button btnSaveContinue;
    private RadioGroup PediatricAgeCheckGroup_1, PediatricAgeCheckGroup_2, PreExisitingGroup,
            MedicationsGroup, AllergiesGroup, ProceduresGroup;
    private ArrayList<HashMap<String, Object>> myPhotosList;
//    private ImageAdapter imageAdapter;
    private GridView gridview;
    public static Uri fileUri;
    public JSONArray recordsArray;
    private AlertDialog imagePickerDialog;
//    private loadDownloadedImages loadImageService;
//    private LocationCooridnates locationService;
    private IntentFilter intentFilter;
    private static List<BroadcastReceiver> registeredReceivers = new ArrayList<BroadcastReceiver>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnMedicalHistoryResponse mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicalHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicalHistoryFragment newInstance(String param1, String param2) {
        MedicalHistoryFragment fragment = new MedicalHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MedicalHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_history, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSaveContinue = (Button) view.findViewById(R.id.SavContinueBtn);
        btnSaveContinue.setClickable(false);
        view.findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
//        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
//        ((TextView) view.findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        PediatricAgeCheckGroup_1 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) view.findViewById(R.id.conditionsGroup));
//        setProgressBar(view.findViewById(R.id.progressDialog));
        MedicationsGroup = ((RadioGroup) view.findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) view.findViewById(R.id.allergiesGroup));
        ProceduresGroup = ((RadioGroup) view.findViewById(R.id.proceduresGroup));
        myPhotosList = new ArrayList<HashMap<String, Object>>();
//        locationService = new LocationCooridnates(getApplicationContext());
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(getClass().getSimpleName());

//        initializeViews();
        initializeYesNoButtonActions();
//        initializeCameraDialog();
        if(view!=null)
            checkMedicalDateHistory(view);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMedicalHistoryResponse) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMedicalHistoryResponse {
        // void onLoginSucess(); TODO : Write necessary listeners
    }


    /**
     * This function is used to update Medical history data in service
     * MedicalHistoryUpdateServices :: This class is used to update medical history. This class holds data ot update service
     *
     */
    private void updateMedicalHistory(View view){
//        ((ProgressBar) view.findViewById(R.id.thumpProgressBar)).setVisibility(View.GONE);
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                // hideProgress();
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
            MedicalHistoryUpdateServices services = new MedicalHistoryUpdateServices(getActivity(), null);
            services.updateMedicalHistoryRequest(medhistoryMap, successCallBackListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * This function is used to initialize Yes/No Button actions used in layout.
     */
    private void initializeYesNoButtonActions() {

        //TODO: Implement Yes/No buttons in UI and then work
        /*PediatricAgeCheckGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        // TODO : Move to activity level
        PreExisitingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.conditionYesButton) {
                    PreExisitingGroup.clearCheck();
                    Intent i = new Intent(getActivity(), MDLiveAddConditions.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(getActivity());
                }else{
                    ValidateModuleFields();
                }
            }
        });

        // TODO : Move to activity level
        MedicationsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.medicationsYesButton) {
                    MedicationsGroup.clearCheck();
                    Intent i = new Intent(getActivity(), MDLiveAddMedications.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(getActivity());
                }else{
                    ValidateModuleFields();
                }
            }
        });

        // TODO : Move to activity level
        AllergiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.allergiesYesButton) {
                    AllergiesGroup.clearCheck();
                    Intent i = new Intent(getActivity(), MDLiveAddAllergies.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(getActivity());
                }else{
                    ValidateModuleFields();
                }
            }
        });

        // TODO : Move to activity level
        ProceduresGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.proceduresYesButton) {
                    Intent i = new Intent(getActivity(), MDLiveAddAllergies.class);
                    startActivity(i);
                    MdliveUtils.startActivityAnimation(getActivity());
                }
                ValidateModuleFields();
            }
        });*/
    }



    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void checkMedicalDateHistory(final View view) {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    if(response.get("health_last_update") instanceof Number){
                        long num=response.getLong("health_last_update");
                        int length = (int) Math.log10(num) + 1;
                        System.out.println(length);
                    }else if(response.get("health_last_update") instanceof CharSequence){
                        if(response.getString("health_last_update").equals("")){
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
                                ((LinearLayout)view.findViewById(R.id.UpdateInfoWindow)).setVisibility(View.VISIBLE);
                                if(getActivity()!=null)
                                    ((TextView)view.findViewById(R.id.updateInfoText)).setText(
                                        getActivity().getResources().getString(R.string.last_update_txt)+
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
                checkMedicalAggregation(view);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
                hideProgressDialog();
            }
        };
        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(getActivity(), null);
        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void checkMedicalAggregation(final View view) {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                showProgressDialog();
                medicalAggregationHandleSuccessResponse(view, response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error", error.getMessage());
                hideProgressDialog();
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(getActivity(), null);
        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
    }

    /**
     * Handling the response of medical Aggregation webservice response.
     */

    private void medicalAggregationHandleSuccessResponse(View view, JSONObject response) {
        try {
            medicalAggregationJsonObject = response;
            checkMedicalCompletion(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applying validation on form and enable/disable continue button for further steps over.
     */

    public void ValidateModuleFields(View view) {
        boolean isAllFieldsfilled = true;
        if (hasFemaleAttribute) {
            if (PediatricAgeCheckGroup_1.getCheckedRadioButtonId() < 0
                    || PediatricAgeCheckGroup_2.getCheckedRadioButtonId() < 0) {
                isAllFieldsfilled = false;
            }
        }
        if (((LinearLayout) view.findViewById(R.id.MyHealthConditionChoiceLl)).getVisibility() == View.VISIBLE &&
                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (((LinearLayout) view.findViewById(R.id.MyHealthMedicationsLl)).getVisibility() == View.VISIBLE &&
                MedicationsGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (((LinearLayout) view.findViewById(R.id.MyHealthAllergiesLl)).getVisibility() == View.VISIBLE &&
                AllergiesGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (isAllFieldsfilled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnSaveContinue.setBackground(getResources().getDrawable(R.drawable.btn_rounded_bg));
            } else {
                btnSaveContinue.setBackgroundResource(R.drawable.btn_rounded_bg);
            }
            btnSaveContinue.setClickable(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnSaveContinue.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
            } else {
                btnSaveContinue.setBackgroundResource(R.drawable.btn_rounded_grey);
            }
            btnSaveContinue.setClickable(false);
        }
    }

    /**
     * Checks user medical history completion details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalCompletion(final View view) {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                medicalCompletionHandleSuccessResponse(view,response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryCompletionServices services = new MedicalHistoryCompletionServices(getActivity(), null);
        services.getMedicalHistoryCompletionRequest(successCallBackListener, errorListener);
    }

    /**
     * Error Response Handler for Medical History Completion.
     */
    private void medicalCommonErrorResponseHandler(VolleyError error) {
        hideProgressDialog();
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null) {
            String message = "No Internet Connection";
            if (networkResponse.statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                message = "Internal Server Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                message = "Unprocessable Entity Error";
            } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                message = "Page Not Found";
            }
            MdliveUtils.showDialog(getActivity(), "Error",
                    "Server Response : " + message);
        }
    }
    /**
     * Successful Response Handler for Medical History Completion.
     */
    private void medicalCompletionHandleSuccessResponse(View view, JSONObject response) {
        try {
            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
            //checkIsFirstTimeUser(historyPercentageArray);
            checkMyHealthHistory(view, historyPercentageArray);
            //checkPediatricProfile(historyPercentageArray);
            checkProcedure(view, historyPercentageArray);
            checkMyMedications(view, historyPercentageArray);
            checkAllergies(view, historyPercentageArray);
            ValidateModuleFields(view);
            checkAgeAndFemale(view);
//            downloadMedicalRecordService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the age of user and sex whether male or female to enable
     * Pediatric questions.
     */
    public void checkAgeAndFemale(View view) {
        try {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String gender = sharedpreferences.getString(PreferenceConstants.GENDER, "");

            if(MdliveUtils.calculteAgeFromPrefs(getActivity())>=10) {
                if(gender.equalsIgnoreCase("Female")){
                    ((LinearLayout) view.findViewById(R.id.PediatricAgeCheck1)).setVisibility(View.VISIBLE);
                    ((LinearLayout) view.findViewById(R.id.PediatricAgeCheck2)).setVisibility(View.VISIBLE);
                    hasFemaleAttribute = true;
                }
            }else{
                hasFemaleAttribute = false;
            }

            ValidateModuleFields(view);

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
    private void checkAllergies(View view, JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("allergies").length() == 0)) {
                view.findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                view.findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
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
                    ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.no_allergies_reported));
                else
                    ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.no_allergies_reported));
            }
            view.findViewById(R.id.ContainerScrollView).setVisibility(View.VISIBLE);
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isNewUser){
            view.findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.VISIBLE);
            view.findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
            view.findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
        }

    }

    /**
     * This will check weather the user has completed the medications section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyMedications(View view, JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("medications").length() == 0)) {
                view.findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                view.findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
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
                    ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.no_medications_reported));
                else
                    ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.no_medications_reported));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isNewUser){
            view.findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.VISIBLE);
            view.findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
            view.findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
        }


        if(MedicationsGroup.getCheckedRadioButtonId() > 0 &&
                MedicationsGroup.getCheckedRadioButtonId() == R.id.medicationsYesButton){
            ((RadioButton) view.findViewById(R.id.medicationsYesButton)).setChecked(false);
        }

    }

    /**
     * This will check weather the user has completed the my health condition section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthHistory(View view, JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("conditions").length() == 0)) {
                view.findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                view.findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
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
                    ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.no_conditions_reported));
                else
                    ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.no_conditions_reported));
            }

            if(isNewUser){
                view.findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.VISIBLE);
                view.findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
            }else{
                view.findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                view.findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
            ((RadioButton) view.findViewById(R.id.conditionYesButton)).setChecked(false);
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
//                ((TextView) view.findViewById(R.id.PediatricNameTv)).setText("Completed");
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
//                    ((TextView) view.findViewById(R.id.PediatricNameTv)).setText(pediotricNames);
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
    private void checkProcedure(View view, JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            String myHealthPercentage = historyPercentageArray.getJSONObject(0).getString("health");
            if (myHealthPercentage!=null && !"0".equals(myHealthPercentage) && !(healthHistory.getJSONArray("conditions").length() == 0)){
                view.findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
//                view.findViewById(R.id.ProceduresLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
                for(int i = 0;i<conditonsArray.length();i++){
                    conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
                    if(i!=conditonsArray.length() - 1){
                        conditonsNames += ", ";
                    }
                }
                ((TextView)view.findViewById(R.id.ProceduresNameTv)).setText(conditonsNames);
            }
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

        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                getUserPharmacyDetails();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                medicalCommonErrorResponseHandler(error);
            }
        };
        UpdateFemaleAttributeServices services = new UpdateFemaleAttributeServices(getActivity(), null);
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
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                handleSuccessResponse(response);
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
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                    }
                }
            }
        };
        callPharmacyService(responseListener, errorListener);
    }



    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener - Pharmacy error response listener
     *  @param responseListener - Pharmacy detail Success response listener
     */
    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
        /*if(locationService.checkLocationServiceSettingsEnabled(getApplicationContext())){
            showProgress();
            registerReceiver(locationReceiver, intentFilter);
            registeredReceivers.add(locationReceiver);
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(getApplicationContext());
        }else*/{
            PharmacyService services = new PharmacyService(getActivity(), null);
            services.doMyPharmacyRequest("","",responseListener, errorListener);
        }
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            unregisterReceiver(locationReceiver);
//            registeredReceivers.remove(locationReceiver);
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleSuccessResponse(response);
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
                            // Show timeout error message
                            MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                        }
                    }
                }
            };
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
                double lat=intent.getDoubleExtra("Latitude",0d);
                double lon=intent.getDoubleExtra("Longitude",0d);
                if(lat!=0 && lon!=0){
                    PharmacyService services = new PharmacyService(getActivity(), null);
                    services.doMyPharmacyRequest(lat+"", +lon+"",
                            responseListener, errorListener);
                }else{
                    PharmacyService services = new PharmacyService(getActivity(), null);
                    services.doMyPharmacyRequest("","",responseListener, errorListener);
                }
            }else{
                PharmacyService services = new PharmacyService(getActivity(), null);
                services.doMyPharmacyRequest("","",responseListener, errorListener);
            }
        }
    };



    /* This function handles webservice response and parsing the contents.
    *  Once parsing operation done, then it will update UI
    *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
    */
    Bundle bundletoSend = new Bundle();
    String jsonResponse;

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            jsonResponse = response.toString();
            if(response.has("message")){
                if(response.getString("message").equals("No pharmacy selected")){
//                    getLocationBtnOnClickAction();
                    Toast.makeText(getActivity(),"No Pharmacy Selected",Toast.LENGTH_LONG).show();
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
                /*Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.putExtra("Response",res);
                startActivity(i);
                MdliveUtils.startActivityAnimation(getActivity());*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
