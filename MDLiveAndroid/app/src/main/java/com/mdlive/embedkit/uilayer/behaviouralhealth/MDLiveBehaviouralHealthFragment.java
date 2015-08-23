package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.behavioural.BehaviouralService;
import com.mdlive.unifiedmiddleware.services.behavioural.BehaviouralUpdateService;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sanjibkumar_p on 7/24/2015.
 */
public class MDLiveBehaviouralHealthFragment extends MDLiveBaseFragment {
    private BehavioralHistory mBehavioralHistory;

    private ProgressBar mProgressBar;

    private LinearLayout mConditionLinearLayout;

    private RadioGroup mHospitalizedRadioGroup;
    private RadioButton mHospitalizedYesRadioButton;
    private RadioButton mHospitalizedNoRadioButton;
    private TextView mWhenTextView;
    private TextView mHowLongTextView;

    private LinearLayout mFamilyHistoryLinearLayout;

    private RadioGroup mFamilyRadioGroup;
    private RadioButton mFamilyYesRadioButton;
    private RadioButton mFamilyNoRadioButton;
    private Spinner mCounsellingSpinner;

//    private Button mSaveButton;

    List<String> relationShpList;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;

    public static MDLiveBehaviouralHealthFragment newInstance() {
        final MDLiveBehaviouralHealthFragment fragment = new MDLiveBehaviouralHealthFragment();
        return fragment;
    }

    public MDLiveBehaviouralHealthFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mdlive_behavioural_histroy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        mConditionLinearLayout = (LinearLayout) view.findViewById(R.id.mdlive_behavioural_histroy_conditions);
        mHospitalizedRadioGroup = (RadioGroup) view.findViewById(R.id.behavioural_health_hospitalized_radio_group);
        if (mHospitalizedRadioGroup != null) {
            mHospitalizedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.behavioural_health_hospitalized_yes_radio_button) {

                    } else if (checkedId == R.id.behavioural_health_hospitalized_no_radio_button) {

                    }
                }
            });
        }

        mHospitalizedYesRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_hospitalized_yes_radio_button);
        mHospitalizedNoRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_hospitalized_no_radio_button);

        mWhenTextView = (TextView) view.findViewById(R.id.mdlive_behavioural_histroy_when_text_view);
        mHowLongTextView = (TextView) view.findViewById(R.id.mdlive_behavioural_histroy_how_long_text_view);

        mFamilyHistoryLinearLayout = (LinearLayout) view.findViewById(R.id.mdlive_behavioural_family_histroy);

        mFamilyRadioGroup = (RadioGroup) view.findViewById(R.id.behavioural_health_question_family_radio_group);
        if (mFamilyRadioGroup != null) {
            mFamilyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.behavioural_health_question_family_yes_radio_button) {

                    } else if (checkedId == R.id.behavioural_health_question_family_no_radio_button) {

                    }
                }
            });
        }

        mFamilyYesRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_question_family_yes_radio_button);
        mFamilyNoRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_question_family_no_radio_button);

        mCounsellingSpinner = (Spinner) view.findViewById(R.id.behavioural_health_counselling_spinner);
//        final List<String> relationShpList = Arrays.asList(getResources().getStringArray(R.array.counseling_preference));
        relationShpList = Arrays.asList(getResources().getStringArray(R.array.counseling_preference));
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (view.getContext(), android.R.layout.simple_spinner_item, relationShpList);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mCounsellingSpinner.setAdapter(dataAdapter);

//        mSaveButton = (Button)view.findViewById(R.id.family_history_save_button);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Gson gson = new Gson();
        mBehavioralHistory = gson.fromJson(DummyJSON.getBehaviouralHistoryResponseString(), BehavioralHistory.class);
        if (mBehavioralHistory != null) {
            handleInitialResponse();
        }
    }

    private void handleInitialResponse() {

        mWhenTextView.setText(mBehavioralHistory.hospitalizedDate);
        mHowLongTextView.setText(mBehavioralHistory.hospitalizedDuration);

        int selectedPosition = 0;
        for (int j = 0; j < relationShpList.size(); j++) {
            if (mBehavioralHistory.counselingPreference.toLowerCase().trim().equalsIgnoreCase(relationShpList.get(j).toString().trim())) {
                selectedPosition = j;
                break;
            }
        }
        mCounsellingSpinner.setSelection(selectedPosition);

        if (mBehavioralHistory.behavioralMconditions != null && mBehavioralHistory.behavioralMconditions.size() > 0 && mConditionLinearLayout != null) {
            for (int i = 0; i < mBehavioralHistory.behavioralMconditions.size(); i++) {
                final int position = i;
                final CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setText(mBehavioralHistory.behavioralMconditions.get(position).condition);
                if ("Yes".equalsIgnoreCase(mBehavioralHistory.behavioralMconditions.get(position).active)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mBehavioralHistory.behavioralMconditions.get(position).active = ConditionAndActive.YES;
                        } else {
                            mBehavioralHistory.behavioralMconditions.get(position).active = ConditionAndActive.NO;
                        }
                    }
                });

                mConditionLinearLayout.addView(checkBox);
            }
        }


        if (mBehavioralHistory.behavioralFamilyHistory != null && mBehavioralHistory.behavioralFamilyHistory.size() > 0 && mFamilyHistoryLinearLayout != null) {
            for (int i = 0; i < mBehavioralHistory.behavioralFamilyHistory.size(); i++) {
                final int position = i;
                final CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setText(mBehavioralHistory.behavioralFamilyHistory.get(position).condition);
                if ("Yes".equalsIgnoreCase(mBehavioralHistory.behavioralFamilyHistory.get(position).active)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mBehavioralHistory.behavioralFamilyHistory.get(position).active = ConditionAndActive.YES;
                        } else {
                            mBehavioralHistory.behavioralFamilyHistory.get(position).active = ConditionAndActive.NO;
                        }
                    }
                });

                mFamilyHistoryLinearLayout.addView(checkBox);
            }
        }

        if("Yes".equalsIgnoreCase(mBehavioralHistory.familyHospitalized)) {
            mHospitalizedYesRadioButton.setChecked(true);
        }
        if("Yes".equalsIgnoreCase(mBehavioralHistory.hospitalized)) {
            mFamilyYesRadioButton.setChecked(true);
        }
    }

    private void getBehaviouralHealthServiceData() {

        setProgressBarVisibility();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    handleSuccessResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                            pDialog.dismiss();
                setInfoVisibilty();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        BehaviouralService services = new BehaviouralService(getActivity(), pDialog);
        services.doGetBehavioralHealthService(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("BehaviouralHealth Res", response.toString());

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /*
     * set visible for the progress bar
     */
    public void setProgressBarVisibility()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    /*
     * set visible for the details view layout
     */
    public void setInfoVisibilty()
    {
        progressBar.setVisibility(View.GONE);
    }

    private void getBehaviouralHealthUpdateServiceData(final JSONObject requestJSON) {

        setProgressBarVisibility();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    handleUpdateSuccessResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setInfoVisibilty();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        BehaviouralUpdateService behaviouralUpdateServices = new BehaviouralUpdateService(getActivity(), pDialog);
        behaviouralUpdateServices.postBehaviouralUpdateService(requestJSON, responseListener, errorListener);

    }

    private void handleUpdateSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("BehaviouralUpdateRes", response.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
