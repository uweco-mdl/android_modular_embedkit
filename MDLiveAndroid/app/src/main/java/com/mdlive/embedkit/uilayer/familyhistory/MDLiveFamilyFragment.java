package com.mdlive.embedkit.uilayer.familyhistory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.FamilyHistoryModel;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.familyhistory.FamilyHistoryServices;
import com.mdlive.unifiedmiddleware.services.familyhistory.FamilyHistoryUpdateServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MDLiveFamilyFragment extends Fragment {

    protected static EditText mFamilyHistoryOtherEditText;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;

    JSONObject innerJsonObject;
    JSONArray innerJsonArray;
    JSONObject innerJSONArrayJsonObject;
    String rootJsonObjectString;
    HashMap<String, String> familyHistory_conditionValue = new HashMap<String, String>();

    protected String mFamilyHistoryOtherEditTextValue;
    View view;

    protected List<FamilyHistoryModel> familyHistoryList;

    /**
     * An interface for defining the callback method
     */
    public interface ListFragmentItemClickListener {
        /**
         * This method will be invoked when an item in the ListFragment is clicked
         */
        void onListFragmentItemClick(int position);
    }


    public static MDLiveFamilyFragment newInstance() {
        final MDLiveFamilyFragment fragment = new MDLiveFamilyFragment();
        return fragment;
    }

    public MDLiveFamilyFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.mdlive_family_histroy, container, false);

        findWidgetId();
        getFamilyHistoryServiceData();

        return view;
    }

    private void findWidgetId() {

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        mFamilyHistoryOtherEditText = (EditText)view.findViewById(R.id.my_family_history_other_editText);

    }

    private void getFamilyHistoryServiceData() {

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
                setInfoVisibilty();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }};

        FamilyHistoryServices services = new FamilyHistoryServices(getActivity() , pDialog);
        services.getFamilyHistoryServices(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("FamilyHistory Res", response.toString());

            JSONObject resObj = new JSONObject(response.toString());
            JSONArray resArray = resObj.getJSONArray("family_histories");
            JSONObject resJsonObj;

            familyHistoryList = new ArrayList<FamilyHistoryModel>();

            for(int i = 0; i < resArray.length(); i++) {
                resJsonObj = resArray.getJSONObject(i);
                final FamilyHistoryModel model = new FamilyHistoryModel(resJsonObj.getString("relationship"), resJsonObj.getString("condition"), resJsonObj.getString("active"));

                Log.d("FamilyHistoryResVal = ", model.toString());
                familyHistoryList.add(model);
            }

            setFamilyHistory();

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



    protected void getFamilyHistoryUpdateServiceData(final JSONObject requestJSON) {

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

        FamilyHistoryUpdateServices familyHistoryUpdateServices = new FamilyHistoryUpdateServices(getActivity(), pDialog);
        familyHistoryUpdateServices.postFamilyHistoryUpdateServices(requestJSON, responseListener, errorListener);

    }

    private void handleUpdateSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            getActivity().finish();
            Log.d("FamilyHistoryUpdateRes", response.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void initializeJsonElements() {

        innerJsonObject = new JSONObject();
        innerJsonArray = new JSONArray();

        try {
            innerJsonObject.put("family_histories",innerJsonArray);
            rootJsonObjectString = innerJsonObject.toString();
            familyHistory_conditionValue.put("", rootJsonObjectString);
        }
        catch (JSONException e) {

        }

    }

    private void getFamilyHistoryOtherEditTextEvent() {

        mFamilyHistoryOtherEditText.addTextChangedListener(new TextWatcher() {

            @SuppressLint("ShowToast")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFamilyHistoryOtherEditText.setCursorVisible(true);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mFamilyHistoryOtherEditTextValue = mFamilyHistoryOtherEditText.getText().toString();
                Log.d("FamilyHistoryOtherValue", mFamilyHistoryOtherEditTextValue.toString());

            }
        });


    }

    private void setFamilyHistory() {
        if (familyHistoryList == null || familyHistoryList.size() == 0) {
            return;
        }

        final LinearLayout scrollLinearLayout = (LinearLayout) view.findViewById(R.id.mdlive_family_scroll_view);

        for (int i = 0; i < familyHistoryList.size(); i++) {
            final int position = i;

            // Inflate Row
            final LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
            final View rootLinearLayout = inflater.inflate(R.layout.mdlive_familyhistory_addrows, null, false);

            // Inflte & get reference of Views to be used
            final CheckBox checkBox = (CheckBox) rootLinearLayout.findViewById(R.id.my_family_history_checkBox);
            final Spinner spinner = (Spinner) rootLinearLayout.findViewById(R.id.my_family_history_checkBox_spinner);
            final CardView spinnerCv = (CardView) rootLinearLayout.findViewById(R.id.my_family_history_checkBox_spinnerCv);

            // Set Checkbox values & check changed listener
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        familyHistoryList.get(position).active = FamilyHistoryModel.YES;
                        spinnerCv.setVisibility(View.VISIBLE);
                    } else {
                        familyHistoryList.get(position).active = FamilyHistoryModel.NO;
                        spinnerCv.setVisibility(View.GONE);
                    }
                }
            });

            if (FamilyHistoryModel.YES.toString().equalsIgnoreCase(familyHistoryList.get(position).active)) {
                checkBox.setChecked(true);
                spinnerCv.setVisibility(View.VISIBLE);

                if ("null".equalsIgnoreCase(familyHistoryList.get(position).relationship) || familyHistoryList.get(position).relationship == null) {
                }
            } else {
                checkBox.setChecked(false);
                spinnerCv.setVisibility(View.GONE);
            }

            checkBox.setText(familyHistoryList.get(position).condition);

            // Set Spinner values & selection listener
            final List<String> relationShpList = Arrays.asList(getResources().getStringArray(R.array.Relationship));
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (getActivity().getBaseContext(), android.R.layout.simple_spinner_item, relationShpList);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int selectedIndex, long id) {
                    familyHistoryList.get(position).relationship = relationShpList.get(selectedIndex);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if ("null".equalsIgnoreCase(familyHistoryList.get(position).relationship) || familyHistoryList.get(position).relationship == null) {
            } else {
                int selectedPosition = 0;
                for (int j = 0; j < relationShpList.size(); j++) {
                    if (familyHistoryList.get(position).relationship.toLowerCase().trim().equalsIgnoreCase(relationShpList.get(j).toString().trim())) {
                        selectedPosition = j;
                        break;
                    }
                }
                spinner.setSelection(selectedPosition);
            }

            scrollLinearLayout.addView(rootLinearLayout);
        }
    }
}