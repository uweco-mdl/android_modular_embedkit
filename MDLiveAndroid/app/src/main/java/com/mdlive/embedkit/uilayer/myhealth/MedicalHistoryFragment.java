package com.mdlive.embedkit.uilayer.myhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.GoogleFitUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.lifestyle.LifeStyleUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.HealthKitServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryFragment.OnGoogleFitSyncResponse} interface
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
            MedicationsGroup, AllergiesGroup;
    public View mHealthSyncContainer, mHealthSyncCv;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isFirstTime = true;
    private static boolean DisplayGoogleFitInvitePage = true;

    private OnGoogleFitSyncResponse mListener;

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
        getActivity().setTitle(getString(R.string.mdl_medical_history));
        SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        String dependentId = sharedPref.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        mHealthSyncContainer = view.findViewById(R.id.HealthSyncContainer);
        mHealthSyncCv = view.findViewById(R.id.HealthSyncCv);
        if(dependentId == null && checkFitInstalled()){
            if (userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_SYNC_BTN_CLICKED, false)) {
                if (userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, false)) {
                    editor.putBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, false);
                }
                getHealthKitSyncStatus();
            }
            else {
                if (DisplayGoogleFitInvitePage) {
                    mHealthSyncContainer.setVisibility(View.VISIBLE);
                    DisplayGoogleFitInvitePage = false;
                }
                else {
                    mHealthSyncContainer.setVisibility(View.GONE);
                }
            }
        } else {
            mHealthSyncCv.setVisibility(View.GONE);
            mHealthSyncContainer.setVisibility(View.GONE);
        }

        view.findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
        PediatricAgeCheckGroup_1 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) view.findViewById(R.id.conditionsGroup));
        MedicationsGroup = ((RadioGroup) view.findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) view.findViewById(R.id.allergiesGroup));
        if (view != null) {
            checkMedicalDateHistory(view);
        }

    }

    private void getHealthKitSyncStatus() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d("HealthKit Response", response.toString());
                    if(response.optString("message").contains("never synced") || response.optString("message").contains("synced with this")){
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
                        String dependentId = sharedPref.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
                        if (!userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, true)) {
                            Log.d("HealthKit Response", "Health Kit Called---");
                            GoogleFitUtils.getInstance().buildFitnessClient(true, null, getActivity());
                            mHealthSyncContainer.setVisibility(View.GONE);
                        } else if(dependentId != null){
                            mHealthSyncContainer.setVisibility(View.GONE);
                        } else {
                            mHealthSyncContainer.setVisibility(View.VISIBLE);
                        }

                        if (userPrefs.getBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, false) || dependentId != null) {
                            mHealthSyncCv.setVisibility(View.GONE);
                        } else {
                            mHealthSyncCv.setVisibility(View.VISIBLE);
                        }
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
                mHealthSyncCv.setVisibility(View.GONE);
                mHealthSyncContainer.setVisibility(View.GONE);
            }
        };
        HealthKitServices services = new HealthKitServices(getActivity(), getProgressDialog());
        services.registerHealthKitSync(successCallBackListener, errorListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGoogleFitSyncResponse) activity;

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
     */
    public interface OnGoogleFitSyncResponse {
        void setHealthStatus(String data);
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
                Log.d("Success response:Health", response.toString());
                hideProgressDialog();
                try {
                    if (response.get("health_last_update") instanceof Number) {
                        long num = response.getLong("health_last_update");
                    } else if (response.get("health_last_update") instanceof CharSequence) {
                        if (response.getString("health_last_update").equals("")) {
                        }
                    }
                    if (response.getString("health_last_update").length() == 0) {
                        isNewUser = true;
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
            }
        };
        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(getActivity(), getProgressDialog());
        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion details.
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
                hideProgressDialog();
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(getActivity(), getProgressDialog());
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
        if (view.findViewById(R.id.MyHealthConditionChoiceLl).getVisibility() == View.VISIBLE &&
                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (view.findViewById(R.id.MyHealthMedicationsLl).getVisibility() == View.VISIBLE &&
                MedicationsGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (view.findViewById(R.id.MyHealthAllergiesLl).getVisibility() == View.VISIBLE &&
                AllergiesGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
    }

    /**
     * Checks user medical history completion details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion details.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalCompletion(final View view) {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                medicalCompletionHandleSuccessResponse(view, response);
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
            String message = getActivity().getString(R.string.mdl_no_internet_connection);
            if (networkResponse.statusCode == MDLiveConfig.HTTP_INTERNAL_SERVER_ERROR) {
                message = getActivity().getString(R.string.mdl_internal_server_error);
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_UNPROCESSABLE_ENTITY) {
                message = getActivity().getString(R.string.mdl_unprocessable_entity_error);
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_NOT_FOUND) {
                message = getActivity().getString(R.string.mdl_page_not_found);
            }
            MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_error),
                    "Server Response : " + message);
        }
    }

    /**
     * Successful Response Handler for Medical History Completion.
     */
    private void medicalCompletionHandleSuccessResponse(View view, JSONObject response) {
        try {
            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
            checkMyHealthHistory(view, historyPercentageArray);
            checkProcedure(view, historyPercentageArray);
            checkMyMedications(view, historyPercentageArray);
            checkAllergies(view, historyPercentageArray);
            checkPediatricCompletion(view, historyPercentageArray);
            checkMyHealthBehaviouralHistory(view, historyPercentageArray);
            checkMyHealthLifestyleAndFamilyHistory(view, historyPercentageArray);
            ValidateModuleFields(view);
            checkAgeAndFemale(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This will check weather the user has completed the the family history and lifestyle section and will hide and
     * display the layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     * @param view                   - The layout view
     */
    private void checkMyHealthLifestyleAndFamilyHistory(View view, JSONArray historyPercentageArray) {
        try {
            for (int i = 0; i < historyPercentageArray.length(); i++) {
                if (historyPercentageArray.getJSONObject(i).has("life_style")) {
                    if (historyPercentageArray.getJSONObject(i).optInt("life_style", 0) > 10) {
                        ((TextView) view.findViewById(R.id.LifestyleTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }else{
                        ((TextView) view.findViewById(R.id.LifestyleTv)).setText(getResources().getString(R.string.mdl_pediatric_notcompleted_txt));
                    }
                }
            }

            if (medicalAggregationJsonObject.has("family_history")) {
                if (medicalAggregationJsonObject.getJSONArray("family_history").length() > 0) {
                    ((TextView) view.findViewById(R.id.FamilyHistoryTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                }else{
                    ((TextView) view.findViewById(R.id.FamilyHistoryTv)).setText(getResources().getString(R.string.mdl_pediatric_notcompleted_txt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton) {
            ((RadioButton) view.findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnGoogleFitGetData {
        void getGoogleFitData(String data);
    }


    /**
     * This will check weather the user has completed the behavioural health history section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthBehaviouralHistory(View view, JSONArray historyPercentageArray) {
        try {
            for (int i = 0; i < historyPercentageArray.length(); i++) {
                if (historyPercentageArray.getJSONObject(i).has("behavioral")) {
                    view.findViewById(R.id.BehaviouralHealthCardView).setVisibility(View.VISIBLE);
                    if (historyPercentageArray.getJSONObject(i).getInt("behavioral") != 0) {
                        ((TextView) view.findViewById(R.id.BehaviouralHealthTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton) {
            ((RadioButton) view.findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    }

    /**
     * This will check weather the user has completed the allergy section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkPediatricCompletion(View view, JSONArray historyPercentageArray) {
        try {
            int pediatricPercentage = 0;
            for (int i = 0; i < historyPercentageArray.length(); i++) {
                if (historyPercentageArray.getJSONObject(i).has("pediatric")) {
                    view.findViewById(R.id.pediatric_cardview).setVisibility(View.VISIBLE);
                    pediatricPercentage = historyPercentageArray.getJSONObject(i).getInt("pediatric");
                }
            }
            if (pediatricPercentage != 0) {
                ((TextView) view.findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_completed_txt));
            } else {
                ((TextView) view.findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_notcompleted_txt));
            }
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

            if (TimeZoneUtils.calculteAgeFromPrefs(getActivity()) >= 10) {
                if (gender.equalsIgnoreCase("Female")) {
                    hasFemaleAttribute = true;
                }
            } else {
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
                    ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.mdl_no_allergies_reported));
                else
                    ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.mdl_no_allergies_reported));
            }
            view.findViewById(R.id.ContainerScrollView).setVisibility(View.VISIBLE);
            hideProgressDialog();
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
                    ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.mdl_no_medications_reported));
                else
                    ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.mdl_no_medications_reported));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (MedicationsGroup.getCheckedRadioButtonId() > 0 &&
                MedicationsGroup.getCheckedRadioButtonId() == R.id.medicationsYesButton) {
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
                    ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.mdl_no_condition_reported));
                else
                    ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.mdl_no_condition_reported));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton) {
            ((RadioButton) view.findViewById(R.id.conditionYesButton)).setChecked(false);
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
            if (myHealthPercentage != null && !"0".equals(myHealthPercentage) && !(healthHistory.getJSONArray("surgeries").length() == 0)) {
                view.findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("surgeries");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                    if (i != conditonsArray.length() - 1) {
                        conditonsNames += ", ";
                    }
                }

                if (conditonsNames.trim().length() == 0)
                    ((TextView) view.findViewById(R.id.ProceduresNameTv)).setText(getString(R.string.mdl_no_procedures_reported));
                else
                    ((TextView) view.findViewById(R.id.ProceduresNameTv)).setText(conditonsNames);
            } else {
                ((TextView) view.findViewById(R.id.ProceduresNameTv)).setText(getString(R.string.mdl_no_procedures_reported));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the screen on returning back to the screen.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null && !isFirstTime) {
            checkMedicalDateHistory(getView());
        }
        isFirstTime = false;
    }

    public void setFitStatus(String data) {
        mHealthSyncContainer.setVisibility(View.GONE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getActivity().getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(PreferenceConstants.GOOGLE_FIT_FIRST_TIME, false);
        editor.commit();
        if (data.equalsIgnoreCase("success")) {
            mHealthSyncCv.setVisibility(View.GONE);
            editor.putBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, true);
            editor.commit();
            GoogleFitUtils.getInstance().buildFitnessClient(true, null, getActivity());
            updateHealthSyncStatus();
        }
    }

    private void updateLifeStyleWithHealthKitData(int weight, int heightFt, int heightIn) {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
            }
        };
        final JSONObject requestJSON = new JSONObject();

        try {
            final JSONObject personalInfoJSONObject = new JSONObject();
            if(heightFt != 0){
                personalInfoJSONObject.put("height_feet", heightFt + "");
            }
            if(heightIn != 0){
                personalInfoJSONObject.put("height_inches", heightIn + "");
            }
            if(weight != 0){
                personalInfoJSONObject.put("weight", weight + "");
            }
            requestJSON.put("personal_info", personalInfoJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LifeStyleUpdateServices lifeStyleUpdateServices = new LifeStyleUpdateServices(getActivity(), getProgressDialog());
        lifeStyleUpdateServices.postLifeStyleServices(requestJSON, responseListener, errorListener);
    }

    private void updateHealthSyncStatus() {
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
        services.addHealthKitSync(successCallBackListener, errorListener);
    }
    
    private boolean checkFitInstalled(){
        try {
            getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.fitness", 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void setFitDataEvent(final String data){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    int weight = 0, heightFt = 0, heightIn = 0;
                    hideProgressDialog();
                    JSONObject obj = new JSONObject(data);
                    if(obj.has("weight") && !obj.getString("weight").equals("0")){
                        weight = (int) Math.floor(Double.parseDouble(obj.getString("weight")));
                    }

                    if(obj.has("height") && !obj.getString("height").equals("0")){
                        double[] heightValue = GoogleFitUtils.convertMetersToFeet(Double.parseDouble(obj.getString("height")));
                        if((int) heightValue[0]>0) {
                            heightFt = (int) heightValue[0];
                        }
                        heightIn = (int) heightValue[1];
                    }
                    updateLifeStyleWithHealthKitData(weight, heightFt, heightIn);
                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                }

            }
        });

    }
}
