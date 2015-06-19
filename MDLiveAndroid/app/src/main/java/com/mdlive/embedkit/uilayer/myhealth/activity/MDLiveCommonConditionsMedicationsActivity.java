package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;

import org.json.JSONArray;
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

public abstract class MDLiveCommonConditionsMedicationsActivity extends Activity {

    protected ProgressDialog pDialog;
    protected JSONArray conditionsListJSONArray;
    protected ArrayList<HashMap<String,String>> conditionsList;
    protected static String previousSearch = "";
    protected ArrayList<String> newConditions;
    protected ArrayList<HashMap<String,String>> existingConditions;
    protected int addConditionsCount = 0;
    protected int existingConditionsCount = 0;
    public enum TYPE_CONSTANT {CONDITION,ALLERGY,MEDICATION};
    protected TYPE_CONSTANT type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_add_medical_condition);
        conditionsList = new ArrayList<HashMap<String,String>>();
        newConditions = new ArrayList<String>();
        existingConditions = new ArrayList<HashMap<String, String>>();

        pDialog = Utils.getProgressDialog("Loading...", this);
        getConditionsOrAllergiesData();
        findViewById(R.id.SaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBtnAction();
            }
        });
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveCommonConditionsMedicationsActivity.this);
                finish();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });
    }

    /**
     *
     * This function handles the saving of data when the user presses the save button. Only the newly
     * added conditions are saved.
     *
     */
    private void saveBtnAction() {
        LinearLayout addConditionsLl = (LinearLayout) findViewById(R.id.AddConditionsLl);
        existingConditions.clear();
        newConditions.clear();
        for(int i = 0;i<addConditionsLl.getChildCount();i++){

            RelativeLayout conditionRl = (RelativeLayout) addConditionsLl.getChildAt(i);
            Log.d("Add addConditionsLl", "conditionRl.getTag() - " + conditionRl.getTag() + "--" + ((EditText)conditionRl.getChildAt(0)).getText().toString());
            if(conditionRl.getTag()==null && (((EditText)conditionRl.getChildAt(0)).getText() != null) &&
                    !(((EditText)conditionRl.getChildAt(0)).getText().toString().equalsIgnoreCase(""))){
                Log.d("Add addConditionsLl", "conditionRl.getTag() - " + conditionRl.getTag() + "--" + ((EditText)conditionRl.getChildAt(0)).getText().toString());
                newConditions.add(((EditText)conditionRl.getChildAt(0)).getText().toString());
            } else if(conditionRl.getTag()!=null) {
                HashMap<String, String> items = new HashMap<String, String>();
                items.put("id", conditionRl.getTag().toString());
                items.put("name",((EditText)conditionRl.getChildAt(0)).getText().toString());
                existingConditions.add(items);
            }
        }
        saveNewConditionsOrAllergies();
    }

    /**
     *
     * This abstract method handles the saving of new conditions or allergies by making a network call to server.
     *
     */
    protected abstract void saveNewConditionsOrAllergies();


    /**
     *
     * This abstract method handles the updating of existing conditions or allergies by making a network call to server.
     *
     */
    protected abstract void updateConditionsOrAllergies();

    /**
     *
     * This function will retrieve the known conditions or allergies from the server.
     *
     */
    protected abstract void getConditionsOrAllergiesData();

    /**
     *\
     * This function will fetch the necessary conditions or allergies information from the JSON data received from
     * the MedicalConditionListServices. A dynamic layout is inflated and the pre existing conditions
     * are loaded into the dynamic views.
     *
     */
    private void preRenderKnownConditionData() {
        try {
            LinearLayout addConditionsLl = (LinearLayout) findViewById(R.id.AddConditionsLl);
            LayoutInflater viewInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            conditionsList.clear();
            addConditionsLl.removeAllViews();

            for (int i = 0; i < conditionsListJSONArray.length(); i++) {
                View singleConditionView = viewInflater.inflate(R.layout.mdlive_add_condition, null);
                String conditionName = (type == TYPE_CONSTANT.CONDITION) ? ((JSONObject) conditionsListJSONArray.get(i)).getString("condition") :
                        (type == TYPE_CONSTANT.ALLERGY) ? ((JSONObject) conditionsListJSONArray.get(i)).getString("name") : ((JSONObject) conditionsListJSONArray.get(i)).getString("name");
                String conditionId = ((JSONObject) conditionsListJSONArray.get(i)).getString("id");
                HashMap<String,String> tmpCondition = new HashMap<String, String>();
                tmpCondition.put("id",conditionId);
                tmpCondition.put((type == TYPE_CONSTANT.CONDITION) ? "condition": (type == TYPE_CONSTANT.ALLERGY)?"allergy":"medication",conditionName);
                conditionsList.add(tmpCondition);
                initialiseSingleConditionView(singleConditionView, addConditionsLl, i, tmpCondition);
                addConditionsLl.addView(singleConditionView);
            }
            if(addConditionsLl.getChildCount() < 3){
                while(addConditionsLl.getChildCount()<3){
                    addBlankConditionOrAllergy();
                }
            }else{
                addBlankConditionOrAllergy();
            }
            pDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
            pDialog.dismiss();
        }
    }

    /**
     *
     * This function will a blank condition or allergy field without any pre-populated conditions or allergies. The Dynamic
     * view is inflated and the same is initialised by calling initialiseSingleConditionView() function.
     * The initialised view is added into the conditions or allergies layout.
     *
     */
    protected void addBlankConditionOrAllergy(){
        LinearLayout addConditionsLl = (LinearLayout) findViewById(R.id.AddConditionsLl);
        LayoutInflater viewInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View singleConditionView = viewInflater.inflate(R.layout.mdlive_add_condition, null);
        initialiseSingleConditionView(singleConditionView, addConditionsLl, addConditionsLl.getChildCount(), null);
        addConditionsLl.addView(singleConditionView,0);
    }

    /**
     *
     * This function will initialise an add condition view. A dynamic view is added to the content-view. All
     * the necessary actions for the edit-text and the delete buttons are also set here.
     *
     *
     *  @param singleConditionView
     * @param addConditionsLl
     * @param position
     * @param conditionDetails
     */
    private void initialiseSingleConditionView(final View singleConditionView, final LinearLayout addConditionsLl, int position, HashMap<String, String> conditionDetails) {
        final EditText conditonEt = (EditText) singleConditionView.findViewById(R.id.ConditionEt);
        final ImageView deleteView = (ImageView) singleConditionView.findViewById(R.id.DeleteConditionBtn);
        createSingleConditionAllergiesViews(position, conditonEt, deleteView, conditionDetails);

        conditonEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return conditonEditTextEditorActon(actionId, addConditionsLl, conditonEt);
            }
        });
        conditonEt.addTextChangedListener(getEditTextWatcher(conditonEt, deleteView));

        conditonEt.setOnFocusChangeListener(getEditTextFocusChangedListener(conditonEt, deleteView));
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((ViewGroup)(deleteView.getParent())).getTag()!=null) {
                    deleteMedicalConditionsOrAllergyAction(deleteView, addConditionsLl);
                }
                addConditionsLl.removeView(singleConditionView);
            }
        });
    }

    /**
     *
     * This abstract function handles the deletion of a condition or allergy by making a service call.
     *
     * @param deleteView - The delete view
     */
    protected abstract void deleteMedicalConditionsOrAllergyAction(ImageView deleteView, LinearLayout addConditionsLl);

    /**
     *
     * This function will handle the focus changes for the add condition edit text. The delete
     * button visibility is managed by this function.
     *
     * @param conditonEt :: The dynamic condition EditText
     * @param deleteView :: The Delete Button View
     * @return The Focus Changed Listener
     */
    private View.OnFocusChangeListener getEditTextFocusChangedListener(final EditText conditonEt, final ImageView deleteView) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    deleteView.setVisibility(View.GONE);
                } else {
                    if (conditonEt.getText().toString().trim().equals("")) {
                        deleteView.setVisibility(View.GONE);
                    } else {
                        deleteView.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
    }

    /**
     *
     * This function handles the text changes the add condition edit text. This function manages
     * the auto-completion feature of the edit-text.
     *
     * @param conditonEt :: The dynamic condition EditText
     * @param deleteView :: The Delete Button View
     * @return The TextWatcher object
     */
    private TextWatcher getEditTextWatcher(final EditText conditonEt, final ImageView deleteView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = conditonEt.getText().toString().trim();
                if(text.equals("")){
                    deleteView.setVisibility(View.GONE);
                } else {
                    deleteView.setVisibility(View.VISIBLE);
                }

                if(text.length()>2){
                    getAutoCompleteData((AutoCompleteTextView)conditonEt,text);
                }
            }
        };
    }

    /**
     *
     *
     * This function sets the mandatory view attributes for the dynamically generated condition view.
     * The EditText's text is also set, if the data is available.
     *
     * @param position :: The position of the dynamic layout
     * @param conditonEt :: The dynamic condition EditText
     * @param deleteView :: The Delete Button View
     * @param conditionDetails :: The optional condition Details(Name and Id)
     */
    protected void createSingleConditionAllergiesViews(int position, EditText conditonEt, ImageView deleteView, HashMap<String, String> conditionDetails) {
        conditonEt.setId(Utils.generateViewId());
        deleteView.setId(Utils.generateViewId());
        String hint = (type == TYPE_CONSTANT.CONDITION)?((position == 0)?getResources().getString(R.string.add_condition_hint) : getResources().getString(R.string.add_condition_hint)) : (type == TYPE_CONSTANT.ALLERGY)?((position == 0)?getResources().getString(R.string.add_allergies_hint) : getResources().getString(R.string.add_allergies_hint)) : ((position == 0)?getResources().getString(R.string.add_medications_hint) : getResources().getString(R.string.add_medications_hint));
//        String hint = (type == TYPE_CONSTANT.CONDITION)?((position == 0)?getResources().getString(R.string.add_condition_with_eg_hint) : getResources().getString(R.string.add_condition_hint)) : (type == TYPE_CONSTANT.ALLERGY)?((position == 0)?getResources().getString(R.string.add_allergies_with_eg_hint) : getResources().getString(R.string.add_allergies_hint)) : ((position == 0)?getResources().getString(R.string.add_meidations_with_eg_hint) : getResources().getString(R.string.add_medications_hint));
        conditonEt.setHint(hint);
        conditonEt.setHintTextColor(getResources().getColor(R.color.grey_txt));
        conditonEt.setTextColor(Color.BLACK);
        conditonEt.setSingleLine(true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)deleteView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_TOP,conditonEt.getId());
        params.addRule(RelativeLayout.ALIGN_BOTTOM, conditonEt.getId());
        params.addRule(RelativeLayout.ALIGN_RIGHT, conditonEt.getId());
        deleteView.setLayoutParams(params);
        deleteView.setVisibility(View.GONE);
        conditonEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if(type == TYPE_CONSTANT.CONDITION) {
            if (conditionDetails != null && !conditionDetails.get("condition").equals("")) {
                conditonEt.setText(conditionDetails.get("condition"));
                ((ViewGroup) (conditonEt.getParent())).setTag(conditionDetails.get("id"));
            }
        } else if(type == TYPE_CONSTANT.ALLERGY){
            if(conditionDetails!=null && !conditionDetails.get("allergy").equals("")) {
                conditonEt.setText(conditionDetails.get("allergy"));
                ((ViewGroup)(conditonEt.getParent())).setTag(conditionDetails.get("id"));
            }
        } else {
            if(conditionDetails!=null && !conditionDetails.get("medication").equals("")) {
                conditonEt.setText(conditionDetails.get("medication"));
                ((ViewGroup)(conditonEt.getParent())).setTag(conditionDetails.get("id"));
            }
        }
    }

    /**
     *
     *
     * This function handles the action of adding new condition view when the user enters the data
     * in the last condition field.
     *
     * @param actionId :: The action Id
     * @param addConditionsLl :: THe addCondition Linear Layout
     * @param conditonEt :: The dynamic condition edit text
     * @return the boolean action response.
     */
    private boolean conditonEditTextEditorActon(int actionId, LinearLayout addConditionsLl, EditText conditonEt) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {

            int childCount = addConditionsLl.getChildCount();
            if (childCount >= 3 && (((ViewGroup)(addConditionsLl.getChildAt(childCount-1))).getChildAt(0)).getId() == conditonEt.getId()) {
                addBlankConditionOrAllergy();
                childCount = addConditionsLl.getChildCount();
                EditText newEt = (EditText) ((ViewGroup)(addConditionsLl.getChildAt(childCount-1))).getChildAt(0);
                conditonEt.setNextFocusDownId(newEt.getId());
                newEt.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(newEt,InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            return true;
        }
        return false;
    }


    /**
     *
     *  Error Response Handler for Medical Conditions and allergies
     *
     */
    protected void medicalCommonErrorResponseHandler(VolleyError error) {
        previousSearch = "";
        pDialog.dismiss();
        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
            }
        }
    }


    /**
     *
     *  Successful Response Handler for Medical History Completion. Once the data is successfully received,
     *  the conditions are fetched from the JSONObject response and this data is pre-rendered in the
     *  layout by calling the preRenderKnownConditionData() function.
     *
     */

    protected void medicalConditionOrAllergyListHandleSuccessResponse(JSONObject response) {
        try {
            conditionsListJSONArray = response.getJSONArray((type == TYPE_CONSTANT.CONDITION)?"conditions":type == (TYPE_CONSTANT.ALLERGY)?"allergies":"medications");
            preRenderKnownConditionData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *
     * This function will make the service call to get the auto-completion conditions or allergy list based up
     * on the data entered in the edit text.
     *
     * @param atv :: The auto completion text view
     * @param constraint :: The text entered by the user.
     */
    protected abstract void getAutoCompleteData(final AutoCompleteTextView atv, String constraint);

    /**
     *
     * The success Response Handler for Auto completion. The auto completion data is fetched from the
     * JSON response and the same is set on the array adapter. This adapter is assigned to the auto-completion-textview
     * and the same is displayed as dropdown.
     *
     * @param atv :: The AutoCompletionTextView
     * @param response :: JSON response from the service
     */
    protected void autoCompletionHandleSuccessResponse(final AutoCompleteTextView atv, JSONObject response) {
        try {
            JSONArray conditionArray = response.getJSONArray((type == TYPE_CONSTANT.CONDITION)?"conditions":type == (TYPE_CONSTANT.ALLERGY)?"allergies":"medications");
            ArrayList<String> conditionList = new ArrayList<String>();
            for(int i = 0;i<conditionArray.length();i++){
                conditionList.add(conditionArray.getJSONObject(i).getString("name"));
            }
            if(conditionList.size()>0) {
                ArrayAdapter<String> adapter = getAutoCompletionArrayAdapter(atv, conditionList);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                atv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                atv.showDropDown();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     *This function creates an array adapter for the AutoCompletionTextView. Once the list item is
     * clicked, the text is set to the edit text and the autocompletion list is dismissed.
     *
     *
     * @param atv :: The AutoCompletionTextView
     * @param conditionList :: The conditions array list
     * @return The array adapter
     */
    private ArrayAdapter<String> getAutoCompletionArrayAdapter(final AutoCompleteTextView atv, final ArrayList<String> conditionList) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionList) {
            @Override
            public View getView(int position,View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView, parent);
                parent.setBackgroundColor(Color.WHITE);
                final TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previousSearch = text.getText().toString();
                        atv.setText(text.getText().toString());
                        atv.dismissDropDown();
                        atv.setAdapter(null);
                    }
                });
                return view;
            }
        };
    }
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLiveCommonConditionsMedicationsActivity.this, MDLiveLogin.class);
    }
}
