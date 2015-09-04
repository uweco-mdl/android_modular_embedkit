package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sanjibkumar_p on 7/24/2015.
 */
public class MDLiveBehaviouralHealthFragment extends MDLiveBaseFragment {
    private BehavioralHistory mBehavioralHistory;

    private LinearLayout mConditionLinearLayout;

    private RadioGroup mHospitalizedRadioGroup;
    private RadioButton mHospitalizedYesRadioButton;
    private RadioButton mHospitalizedNoRadioButton;
    private TextView mWhenTextView;
    private EditText mHowLongTextView;

    private LinearLayout mFamilyHistoryLinearLayout, mWhenLl, mhowLongLl;

    private RadioGroup mFamilyRadioGroup;
    private RadioButton mFamilyYesRadioButton;
    private RadioButton mFamilyNoRadioButton;
    private Spinner mCounsellingSpinner;

    List<String> relationShpList;


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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConditionLinearLayout = (LinearLayout) view.findViewById(R.id.mdlive_behavioural_histroy_conditions);
        mHospitalizedRadioGroup = (RadioGroup) view.findViewById(R.id.behavioural_health_hospitalized_radio_group);
        if (mHospitalizedRadioGroup != null) {
            mHospitalizedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.behavioural_health_hospitalized_yes_radio_button) {
                        mBehavioralHistory.hospitalized = "Yes";
                        mWhenLl.setVisibility(View.VISIBLE);
                        mhowLongLl.setVisibility(View.VISIBLE);
                    } else if (checkedId == R.id.behavioural_health_hospitalized_no_radio_button) {
                        mBehavioralHistory.hospitalized = "No";
                        mBehavioralHistory.hospitalizedDate = "";
                        mBehavioralHistory.hospitalizedDuration = "";
                        mWhenLl.setVisibility(View.GONE);
                        mhowLongLl.setVisibility(View.GONE);
                    }
                }
            });
        }

        mHospitalizedYesRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_hospitalized_yes_radio_button);
        mHospitalizedNoRadioButton = (RadioButton)view.findViewById(R.id.behavioural_health_hospitalized_no_radio_button);
        mWhenLl = (LinearLayout)view.findViewById(R.id.behavoural_when_ll);
        mhowLongLl = (LinearLayout)view.findViewById(R.id.behavoural_how_long_ll);

        mWhenTextView = (TextView) view.findViewById(R.id.mdlive_behavioural_histroy_when_text_view);
        mHowLongTextView = (EditText) view.findViewById(R.id.mdlive_behavioural_histroy_how_long_text_view);

        mFamilyHistoryLinearLayout = (LinearLayout) view.findViewById(R.id.mdlive_behavioural_family_histroy);

        mFamilyRadioGroup = (RadioGroup) view.findViewById(R.id.behavioural_health_question_family_radio_group);
        if (mFamilyRadioGroup != null) {
            mFamilyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.behavioural_health_question_family_yes_radio_button) {
                        mBehavioralHistory.familyHospitalized = "Yes";
                    } else if (checkedId == R.id.behavioural_health_question_family_no_radio_button) {
                        mBehavioralHistory.familyHospitalized = "No";
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
        getBehaviouralHealthServiceData();
//        final Gson gson = new Gson();
//        mBehavioralHistory = gson.fromJson(DummyJSON.getBehaviouralHistoryResponseString(), BehavioralHistory.class);
        if (mBehavioralHistory != null) {
            handleInitialResponse();
        }
    }

    private void handleInitialResponse() {
        if(!mBehavioralHistory.hospitalizedDate.isEmpty()) {
            mWhenTextView.setText(mBehavioralHistory.hospitalizedDate);
        }
        if(!mBehavioralHistory.hospitalizedDuration.isEmpty()) {
            mHowLongTextView.setText(mBehavioralHistory.hospitalizedDuration);
        }
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
                final View child = getActivity().getLayoutInflater().inflate(R.layout.mdlive_behavioural_checkbox_layout, null);
                final CheckBox checkBox = (CheckBox)child.findViewById(R.id.behavioral_history_checkBox);
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

                mConditionLinearLayout.addView(child);
            }
        }


        if (mBehavioralHistory.behavioralFamilyHistory != null && mBehavioralHistory.behavioralFamilyHistory.size() > 0 && mFamilyHistoryLinearLayout != null) {
            for (int i = 0; i < mBehavioralHistory.behavioralFamilyHistory.size(); i++) {
                final int position = i;
                final View child = getActivity().getLayoutInflater().inflate(R.layout.mdlive_behavioural_checkbox_layout, null);
                final CheckBox checkBox = (CheckBox)child.findViewById(R.id.behavioral_history_checkBox);
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

                mFamilyHistoryLinearLayout.addView(child);
            }
        }

        if("Yes".equalsIgnoreCase(mBehavioralHistory.hospitalized)) {
            mHospitalizedYesRadioButton.setChecked(true);
            mHospitalizedNoRadioButton.setChecked(false);
            mWhenLl.setVisibility(View.VISIBLE);
            mhowLongLl.setVisibility(View.VISIBLE);
        } else{
            mHospitalizedYesRadioButton.setChecked(false);
            mHospitalizedNoRadioButton.setChecked(true);
            mWhenLl.setVisibility(View.GONE);
            mhowLongLl.setVisibility(View.GONE);
        }

        if("Yes".equalsIgnoreCase(mBehavioralHistory.familyHospitalized)) {
            mFamilyYesRadioButton.setChecked(true);
            mFamilyNoRadioButton.setChecked(false);
        } else {
            mFamilyNoRadioButton.setChecked(true);
            mFamilyYesRadioButton.setChecked(false);
        }
    }

    private void getBehaviouralHealthServiceData() {
        showProgressDialog();

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
                hideProgressDialog();

                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }};

        BehaviouralService services = new BehaviouralService(getActivity(), getProgressDialog());
        services.doGetBehavioralHealthService(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            final Gson gson = new Gson();
            mBehavioralHistory = gson.fromJson(response.toString(), BehavioralHistory.class);
            if (mBehavioralHistory != null) {
                handleInitialResponse();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void updateBehaviourHealthService() {
        showProgressDialog();
        mBehavioralHistory.hospitalizedDuration = mHowLongTextView.getText().toString().trim();
        mBehavioralHistory.counselingPreference = mCounsellingSpinner.getSelectedItem().toString();
        final Gson gson = new Gson();
        String request = gson.toJson(mBehavioralHistory);

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
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }};

        BehaviouralUpdateService behaviouralUpdateServices = new BehaviouralUpdateService(getActivity(), getProgressDialog());
        behaviouralUpdateServices.postBehaviouralUpdateService(request, responseListener, errorListener);

    }

    private void handleUpdateSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            getActivity().finish();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            mWhenTextView.setText(sdf.format(calendar.getTime()));
            mBehavioralHistory.hospitalizedDate = sdf.format(calendar.getTime());
        }
    };
}
