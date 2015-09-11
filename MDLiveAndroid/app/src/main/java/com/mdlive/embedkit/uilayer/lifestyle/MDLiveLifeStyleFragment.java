package com.mdlive.embedkit.uilayer.lifestyle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.lifestyle.LifeStyleUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.LifeStyleServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MDLiveLifeStyleFragment extends MDLiveBaseFragment {

    private View view;
    private EditText mHeightFtEditText;
    private EditText mHeightInEditText;
    private EditText mWeightLbsEditText;
    private TextView mBmiText;
    private ListView mListView;

    LifeStyleBaseAdapter adapter;
    List<Model> models;

    /**
     * An interface for defining the callback method
     */
    public interface ListFragmentItemClickListener {
        /**
         * This method will be invoked when an item in the ListFragment is clicked
         */
        void onListFragmentItemClick(int position);
    }


    public static MDLiveLifeStyleFragment newInstance() {
        final MDLiveLifeStyleFragment fragment = new MDLiveLifeStyleFragment();
        return fragment;
    }

    public MDLiveLifeStyleFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.mdlive_life_style_fragment, container, false);

        setWidgetId();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLifeStyleServiceData();
    }

    private void setWidgetId() {
        mHeightFtEditText = (EditText) view.findViewById(R.id.life_style_heighteditTextone);
        mHeightInEditText = (EditText) view.findViewById(R.id.life_style_heighteditTexttwo);
        mWeightLbsEditText = (EditText) view.findViewById(R.id.life_style_weight_editTextone);
        mHeightInEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "11")});
        mBmiText = (TextView) view.findViewById(R.id.life_style_bmi_value_text);
        mHeightFtEditText.addTextChangedListener(bmiTextWatcher);
        mHeightInEditText.addTextChangedListener(bmiTextWatcher);
        mWeightLbsEditText.addTextChangedListener(bmiTextWatcher);
        mListView = (ListView) view.findViewById(R.id.lifestyle_listview);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This function will get the My Lifestyle details from the service.
     */
    private void getLifeStyleServiceData() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response - ", response.toString());
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
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        LifeStyleServices lifeStyleServices = new LifeStyleServices(getActivity(), getProgressDialog());
        lifeStyleServices.getLifeStyleServices(responseListener, errorListener);

    }

    /**
     * This function will populate the lifestyle data to corresponding views. The body-mass index
     * value is also set by calling setBMIText() function.
     *
     * @param response - The Response JsonObject
     */
    private void handleSuccessResponse(JSONObject response) {

        try {
            hideProgressDialog();
            mHeightFtEditText.setText(response.optInt("height_feet", 0) + "");
            mHeightInEditText.setText(response.optInt("height_inches", 0) + "");
            mWeightLbsEditText.setText(response.optInt("weight", 0) + "");
            setBMIText();
            JSONArray lifestyleConditionArray = response.getJSONArray("life_style_conditions");
            JSONObject jsonObject;

            List<Model> lifeStyleModels = new ArrayList<Model>();
            for (int i = 0; i < lifestyleConditionArray.length(); i++) {
                jsonObject = lifestyleConditionArray.getJSONObject(i);
                lifeStyleModels.add(new Model(jsonObject.getInt("id"), jsonObject.getString("condition"), jsonObject.getString("active")));

            }
            adapter = new LifeStyleBaseAdapter(getActivity(), lifeStyleModels);
            mListView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void lifeStyleSaveButtonEvent() {
        models = adapter.getItems();

        final JSONObject requestJSON = new JSONObject();

        try {
            final JSONObject personalInfoJSONObject = new JSONObject();

            personalInfoJSONObject.put("height_feet", mHeightFtEditText.getText().toString().trim());
            personalInfoJSONObject.put("height_inches", mHeightInEditText.getText().toString().trim());
            personalInfoJSONObject.put("weight", mWeightLbsEditText.getText().toString().trim());

            requestJSON.put("personal_info", personalInfoJSONObject);

            final JSONArray lifeStyleConditionJSONArray = new JSONArray();

            for (Model modelitem : models) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("condition_id", modelitem.id);
                jsonObject.put("condition", modelitem.condition);
                jsonObject.put("active", modelitem.active);
                lifeStyleConditionJSONArray.put(jsonObject);
            }

            requestJSON.put("life_style_conditions", lifeStyleConditionJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateLifeStyleService(requestJSON);

    }

    /**
     *
     * This function will save the updated lifestyle data to service.
     *
     * @param requestJSON
     */
    private void updateLifeStyleService(final JSONObject requestJSON) {

        showProgressDialog();

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    getActivity().finish();
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
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };
        LifeStyleUpdateServices lifeStyleUpdateServices = new LifeStyleUpdateServices(getActivity(), getProgressDialog());
        lifeStyleUpdateServices.postLifeStyleServices(requestJSON, responseListener, errorListener);

    }

    /**
     *
     * This textwatcher will call the setBMIText() function whenever the text is changed for
     * weight and height edittexts.
     *
     */
    TextWatcher bmiTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setBMIText();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * This function will set the BMI value to the mBmiText textview by getting the height and
     * weight values from the corresponding textviews.
     *
     */
    private void setBMIText() {
        try {
            String heightFtValue = mHeightFtEditText.getText().toString();
            String heightInValue = mHeightInEditText.getText().toString();
            String weightValue = mWeightLbsEditText.getText().toString();
            double bmiValue = 0;
            if (!heightFtValue.isEmpty() && !weightValue.isEmpty()) {
                float heightInches = 0.0f;
                if (!heightInValue.isEmpty()) {
                    heightInches = Float.parseFloat(heightInValue);
                }
                float feetValue = Float.parseFloat(heightFtValue) + (heightInches / 12);
                bmiValue = (Float.parseFloat(weightValue) * 4.88) / (feetValue * feetValue);
                mBmiText.setText(Math.round(bmiValue) + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * This filter class will set the minimum and maximum values to an Edit text.
     *
     *
     */
    private class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
