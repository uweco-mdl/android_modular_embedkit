package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 *  This class is used to enter maintain CRUD (Create, Read, Update, Delete) functions on
 *  1. MDLiveAddAllergies
 *  2. MDLIveAddConditions
 *  3. MDLiveAddMedications
 *
 */

public abstract class MDLiveCommonConditionsMedicationsActivity extends MDLiveBaseActivity {

    protected JSONArray conditionsListJSONArray;
    protected ArrayList<HashMap<String,String>> conditionsList;
    protected static String previousSearch = "";
    protected ArrayList<HashMap<String,String>> existingConditions;
    public enum TYPE_CONSTANT {CONDITION,ALLERGY,MEDICATION,PROCEDURE};
    protected TYPE_CONSTANT type;
    public Intent resultData = new Intent();
    public static boolean IsThisPageEdited = false;
    public ArrayList<Model> duplicateList = new ArrayList<>();
    public ConditionsAdapter adapter;
    public ListView conditionsListView;
    public LinearLayout noConditionsLayout;
    public static int INSERT_CODE = 100, UPDATE_CODE = 200;
    public int deleteCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_view_health);
        conditionsList = new ArrayList<>();
        adapter = new ConditionsAdapter();
        conditionsListView = (ListView) findViewById(R.id.conditionsListView);
        noConditionsLayout = (LinearLayout)findViewById(R.id.noConditionsLayout);
        conditionsListView.setAdapter(adapter);
        setProgressBar(findViewById(R.id.progressBar));
        getConditionsOrAllergiesData();
    }


    /**
     * This function handles onClick event of done text in layout
     * saveBtnAction - is used to add new condition/allergy/medication
     */
    public void rightBtnOnClick(View view){
        if(isEditCalled){
            ArrayList<String> deleteIdItems = adapter.getRemovedItemsIds();
            if(deleteIdItems.size() > 0){
                deleteConditions();
            }else{
                isEditCalled = false;
                adapter.notifyDataSetChanged();
                ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.options_icon);
            }
        }else{
            isEditCalled = true;
            adapter.notifyDataSetChanged();
            ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        }
    }

    protected void deleteConditions(){
    }

    public void leftBtnOnClick(View v){
        if(IsThisPageEdited)
            checkMedicalAggregation();
        else{
            finish();
            MdliveUtils.closingActivityAnimation(MDLiveCommonConditionsMedicationsActivity.this);
        }
    }

    /**
     *\
     * This function will fetch the necessary conditions or allergies information from the JSON data received from
     * the MedicalConditionListServices. A dynamic layout is inflated and the pre existing conditions
     * are loaded into the dynamic views.
     *
     */
    protected void preRenderKnownConditionData() {
        try {
            duplicateList.clear();
            for (int i = 0; i < conditionsListJSONArray.length(); i++) {
                String conditionName = null, conditionId = null;
                Model data = new Model();
                if(type == TYPE_CONSTANT.PROCEDURE){
                    conditionName = ((JSONObject) conditionsListJSONArray.get(i)).getString("name");
                    conditionId = ((JSONObject) conditionsListJSONArray.get(i)).getString("id");
                    data.setConditionId(conditionId);
                    data.setConditionName(conditionName);
                    data.setConditionSubName(((JSONObject) conditionsListJSONArray.get(i)).getString("surgery_year"));
                }else{
                    conditionName = (type == TYPE_CONSTANT.CONDITION) ? ((JSONObject) conditionsListJSONArray.get(i)).getString("condition") :
                            (type == TYPE_CONSTANT.ALLERGY) ? ((JSONObject) conditionsListJSONArray.get(i)).getString("name") : ((JSONObject) conditionsListJSONArray.get(i)).getString("name");
                    conditionId = ((JSONObject) conditionsListJSONArray.get(i)).getString("id");
                    data.setConditionId(conditionId);
                    data.setConditionName(conditionName);
                    if(type == TYPE_CONSTANT.MEDICATION){
                        data.setConditionSubName("2, once daily");
                    }
                }
                duplicateList.add(data);
            }
            adapter.notifyDataSetChanged();
            hideProgress();
            if(duplicateList.size() > 0){
                conditionsListView.setVisibility(View.VISIBLE);
                noConditionsLayout.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.VISIBLE);
            }else{
                noConditionsLayout.setVisibility(View.VISIBLE);
                conditionsListView.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();
            hideProgress();
        }
    }

    /**
     *
     * This function will retrieve the known conditions or allergies from the server.
     *
     */
    protected abstract void getConditionsOrAllergiesData();


    public boolean isEditCalled = false;

    public class ConditionsAdapter extends BaseAdapter {

        ArrayList<String> removedItemsIds = new ArrayList<>();

        public ArrayList<String> getRemovedItemsIds(){
            return removedItemsIds;
        }

        @Override
        public int getCount() {
            return duplicateList.size();
        }

        @Override
        public Model getItem(int position) {
            return duplicateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            /*convertView = getLayoutInflater().inflate(R.layout.aabdmodule, null);
            holder = new ViewHolder();
            holder.deleteIcon = (ImageView) findViewById(R.id.deleteIcon);
            holder.conditionName = (TextView) findViewById(R.id.conditionName);
            holder.conditionSubName = (TextView) findViewById(R.id.conditionSubName);*/

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.mdlive_custom_addhealth, null);
                holder = new ViewHolder();
                holder.deleteIcon = (ImageView) convertView.findViewById(R.id.deleteIcon);
                holder.conditionName = (TextView) convertView.findViewById(R.id.conditionName);
                holder.conditionSubName = (TextView) convertView.findViewById(R.id.conditionSubName);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removedItemsIds.add(duplicateList.get(position).getConditionId());
                    duplicateList.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.conditionName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MDLiveHealthModule.class);
                    i.putExtra("Id", getItem(position).getConditionId());
                    if(type == TYPE_CONSTANT.PROCEDURE){
                        i.putExtra("type", "procedure");
                        i.putExtra("Name", getItem(position).getConditionName());
                        i.putExtra("Year", getItem(position).getConditionSubName());
                    }else{
                        i.putExtra("Content", getItem(position).getConditionName());
                        if(type == TYPE_CONSTANT.CONDITION){
                            i.putExtra("type", "condition");
                        }else if(type == TYPE_CONSTANT.ALLERGY){
                            i.putExtra("type", "allergy");
                        }else if(type == TYPE_CONSTANT.MEDICATION){
                            i.putExtra("type", "medication");
                        }
                    }

                    startActivityForResult(i, UPDATE_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveCommonConditionsMedicationsActivity.this);
                }
            });

            if(isEditCalled){
                holder.deleteIcon.setVisibility(View.VISIBLE);
            }else{
                holder.deleteIcon.setVisibility(View.GONE);
            }
            if(getItem(position).getConditionName() != null)
                holder.conditionName.setText(getItem(position).getConditionName());

            if(getItem(position).getConditionSubName() != null &&
                    getItem(position).getConditionSubName().length() != 0){
                holder.conditionSubName.setVisibility(View.VISIBLE);
                if(type == TYPE_CONSTANT.PROCEDURE){
                    holder.conditionSubName.setText("Year of procedure : "+getItem(position).getConditionSubName());
                }else{
                    holder.conditionSubName.setText(getItem(position).getConditionSubName());
                }
            }else{
                if(type.equals(TYPE_CONSTANT.MEDICATION) && getItem(position).getConditionSubName() != null){
                    holder.conditionSubName.setVisibility(View.VISIBLE);
                    holder.conditionSubName.setText(getItem(position).getConditionSubName());
                }else{
                    holder.conditionSubName.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    class ViewHolder{
        ImageView deleteIcon;
        TextView conditionName,conditionSubName;
    }

    class Model {
        public String conditionName;
        public String conditionId;

        public String getConditionSubName() {
            return conditionSubName;
        }

        public void setConditionSubName(String conditionSubName) {
            this.conditionSubName = conditionSubName;
        }

        public String conditionSubName;

        public String getConditionName() {
            return conditionName;
        }

        public void setConditionName(String conditionName) {
            this.conditionName = conditionName;
        }

        public String getConditionId() {
            return conditionId;
        }

        public void setConditionId(String conditionId) {
            this.conditionId = conditionId;
        }
    }


    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    public void checkMedicalAggregation() {
        showProgress();
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
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(MDLiveCommonConditionsMedicationsActivity.this, null);
        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
    }

    public void medicalAggregationHandleSuccessResponse(JSONObject response){
        try {
            hideProgress();
            JSONObject healthHistory = response.getJSONObject("health_history");
            String conditonsNames = "";
            Log.e("HISTORY REPONSE", response.toString());
            if(type == TYPE_CONSTANT.CONDITION){
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
                    conditonsNames = getString(R.string.no_conditions_reported);
                Log.e("conditonsNames", conditonsNames);
                resultData.putExtra("conditionsData", conditonsNames);
            }else if(type == TYPE_CONSTANT.MEDICATION){
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
                    conditonsNames = getString(R.string.no_medications_reported);
                resultData.putExtra("medicationData", conditonsNames);
            }else if(type == TYPE_CONSTANT.ALLERGY){
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
                    conditonsNames = getString(R.string.no_allergies_reported);
                resultData.putExtra("allegiesData", conditonsNames);
            }else if(type == TYPE_CONSTANT.PROCEDURE){
                JSONArray conditonsArray = healthHistory.getJSONArray("surgeries");
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
                    conditonsNames = getString(R.string.no_procedures_reported);
                resultData.putExtra("proceduresData", conditonsNames);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK, resultData);
        finish();
        MdliveUtils.closingActivityAnimation(MDLiveCommonConditionsMedicationsActivity.this);
    }

    /**
     *
     *  Error Response Handler for Medical Conditions and allergies
     *
     */
    protected void medicalCommonErrorResponseHandler(VolleyError error) {
        previousSearch = StringConstants.EMPTY_STRING;
        hideProgress();
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
            MdliveUtils.showDialog(MDLiveCommonConditionsMedicationsActivity.this, "Error",
                    "Status Code : " + error.networkResponse.statusCode + "\n" +
                            "Server Response : " + message);
        }

    }

}
