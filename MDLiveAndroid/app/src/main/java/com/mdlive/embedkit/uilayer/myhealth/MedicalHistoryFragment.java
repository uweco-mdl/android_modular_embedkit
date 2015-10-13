package com.mdlive.embedkit.uilayer.myhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


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
            MedicationsGroup, AllergiesGroup;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isFirstTime = true;

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
        view.findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
        PediatricAgeCheckGroup_1 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) view.findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) view.findViewById(R.id.conditionsGroup));
        MedicationsGroup = ((RadioGroup) view.findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) view.findViewById(R.id.allergiesGroup));
        if(view!=null) {
            checkMedicalDateHistory(view);
        }

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
     *
     */
    public interface OnMedicalHistoryResponse {
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
                    if (response.get("health_last_update") instanceof Number) {
                        long num = response.getLong("health_last_update");
                    } else if (response.get("health_last_update") instanceof CharSequence) {
                        if (response.getString("health_last_update").equals("")) {
                        }
                    }
                    if (response.getString("health_last_update").length() == 0) {
                        isNewUser = true;
                    } else {
                        if (response.has("health_last_update")) {
                            long time = response.getLong("health_last_update");
                            if (time != 0) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(time * 1000);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                ((LinearLayout) view.findViewById(R.id.UpdateInfoWindow)).setVisibility(View.VISIBLE);
                                ((TextView) view.findViewById(R.id.updateInfoText)).setText(
                                        getResources().getString(R.string.mdl_last_update_txt) +
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
     * @param view - The layout view
     *
     */
    private void checkMyHealthLifestyleAndFamilyHistory(View view,JSONArray historyPercentageArray) {
        try {
            for(int i =0; i<historyPercentageArray.length();i++){
                if(historyPercentageArray.getJSONObject(i).has("life_style")){
                    if(historyPercentageArray.getJSONObject(i).optInt("life_style",0) == 40){
                        ((TextView)view.findViewById(R.id.LifestyleTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }
                }
            }

            if(medicalAggregationJsonObject.has("family_history")){
                if(medicalAggregationJsonObject.getJSONArray("family_history").length()>0){
                    ((TextView)view.findViewById(R.id.FamilyHistoryTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                }
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
     * This will check weather the user has completed the behavioural health history section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthBehaviouralHistory(View view,JSONArray historyPercentageArray) {
        try {
            for(int i =0; i<historyPercentageArray.length();i++){
                if(historyPercentageArray.getJSONObject(i).has("behavioral")){
                    view.findViewById(R.id.BehaviouralHealthCardView).setVisibility(View.VISIBLE);
                    if(historyPercentageArray.getJSONObject(i).getInt("behavioral")!=0){
                        ((TextView)view.findViewById(R.id.BehaviouralHealthTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }
                    break;
                }
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
     * This will check weather the user has completed the allergy section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkPediatricCompletion(View view, JSONArray historyPercentageArray) {
        try {
            int pediatricPercentage = 0;
            for(int i = 0; i < historyPercentageArray.length(); i++){
                if(historyPercentageArray.getJSONObject(i).has("pediatric")){
                    view.findViewById(R.id.pediatric_cardview).setVisibility(View.VISIBLE);
                    pediatricPercentage = historyPercentageArray.getJSONObject(i).getInt("pediatric");
                }
            }
            if(pediatricPercentage != 0){
                ((TextView) view.findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_completed_txt));
            }else{
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

            if(MdliveUtils.calculteAgeFromPrefs(getActivity())>=10) {
                if(gender.equalsIgnoreCase("Female")){
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
    private void checkProcedure(View view, JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            String myHealthPercentage = historyPercentageArray.getJSONObject(0).getString("health");
            if (myHealthPercentage!=null && !"0".equals(myHealthPercentage) && !(healthHistory.getJSONArray("surgeries").length() == 0)){
                view.findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("surgeries");
                for(int i = 0;i<conditonsArray.length();i++){
                    conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                    if(i!=conditonsArray.length() - 1){
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
     *
     * Update the screen on returning back to the screen.
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        if(getView()!=null  && !isFirstTime) {
            checkMedicalDateHistory(getView());
        }
        isFirstTime = false;
    }
}
