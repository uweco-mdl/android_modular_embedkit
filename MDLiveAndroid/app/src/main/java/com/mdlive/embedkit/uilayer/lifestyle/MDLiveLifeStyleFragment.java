package com.mdlive.embedkit.uilayer.lifestyle;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
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
public class MDLiveLifeStyleFragment extends Fragment {

    private View view;
    private EditText mHeightFtEditText;
    private EditText mHeightInEditText;
    private EditText mWeightLbsEditText;
    private TextView mBmiText;
    private ListView mListView;
    private TextView life_style_question_text;
    private RadioGroup radioGroup;
    private RadioButton yesRadioButton;
    private RadioButton noRadioButton;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;

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

        findWidgetId();
        getLifeStyleServiceData();

        return view;
    }

    private void findWidgetId() {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mHeightFtEditText = (EditText) view.findViewById(R.id.life_style_heighteditTextone);
        mHeightInEditText = (EditText) view.findViewById(R.id.life_style_heighteditTexttwo);
        mWeightLbsEditText = (EditText) view.findViewById(R.id.life_style_weight_editTextone);
        mBmiText = (TextView) view.findViewById(R.id.life_style_bmi_value_text);
        mListView = (ListView) view.findViewById(R.id.lifestyle_listview);

    }

    @Override
    public void onResume() {
        super.onResume();

        getBmiTextEvent();
    }

    private void getLifeStyleServiceData() {

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
            }
        };

        LifeStyleServices lifeStyleServices = new LifeStyleServices(getActivity(), pDialog);
        lifeStyleServices.getLifeStyleServices(responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("LifeStyle Response", response.toString());
            Log.d("LifeStyle Response", response.getInt("height_feet") + "");

            mHeightFtEditText.setText(response.getInt("height_feet") + "ft");
            mHeightInEditText.setText(response.getInt("height_inches") + "in");
            mWeightLbsEditText.setText(response.getInt("weight") + "lbs");
            Log.d("LifeStyle Response", response.toString());
            JSONArray lifestyleConditionArray = response.getJSONArray("life_style_conditions");
            JSONObject jsonObject;

            List<Model> lifeStyleModels = new ArrayList<Model>();
            for (int i = 0; i < lifestyleConditionArray.length(); i++) {
                jsonObject = lifestyleConditionArray.getJSONObject(i);
                lifeStyleModels.add(new Model(jsonObject.getInt("id"), jsonObject.getString("condition"), jsonObject.getString("active")));
                Log.d("Adapter --->", "Here111");

            }
            Log.d("Adapter --->", lifeStyleModels.toString() + "");
            adapter = new LifeStyleBaseAdapter(getActivity(), lifeStyleModels);
            mListView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("Error  --->", e.getMessage());
            e.printStackTrace();
        }

    }

    /*
         * set visible for the progress bar
         */
    public void setProgressBarVisibility() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /*
     * set visible for the details view layout
     */
    public void setInfoVisibilty() {
        progressBar.setVisibility(View.GONE);
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

                //initializeJsonElements();
                getLifeStyleUpdateServiceData(requestJSON);

    }

    private void getLifeStyleUpdateServiceData(final JSONObject requestJSON) {

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
            }
        };

        Log.d("Hello", "Request:  " + requestJSON.toString());

        LifeStyleUpdateServices lifeStyleUpdateServices = new LifeStyleUpdateServices(getActivity(), pDialog);
        lifeStyleUpdateServices.postLifeStyleServices(requestJSON, responseListener, errorListener);

    }

    private void handleUpdateSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Log.d("LifeStyleUpdateResponse", response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBmiTextEvent() {
        mBmiText.setText("4");
    }
}
