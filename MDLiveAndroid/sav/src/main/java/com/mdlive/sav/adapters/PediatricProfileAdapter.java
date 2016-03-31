package com.mdlive.sav.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CustomEt;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sudha_s on 5/18/2015.
 */
public class PediatricProfileAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> array = new  ArrayList<HashMap<String, String>>();
    Activity context;
    private int lastFocussedPosition = -1;
    private Handler handler = new Handler();
    public HashMap<String, String> temp = new HashMap<>();
    public CustomEt immunizationEt, birthCompleteEt, newBornEt,currentWeightEt;

    public PediatricProfileAdapter(Activity applicationContext,
                                   ArrayList<HashMap<String, String>> arraylist) {
              this.context = applicationContext;
        array = arraylist;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int arg0) {
     return array.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
       return 0;
    }

    /**
     *     The getView Method displays the provider list items.
     *     The datas are fetched from the Arraylist based on the position the dats will be placed
     *     in the listview.
     */
    @Override
    public View getView(final int pos, View convertview, ViewGroup parent) {
        if(pos == 0){
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflate.inflate(R.layout.mdlive_pediatric_header, parent,false);
            currentWeightEt = (CustomEt)convertview.findViewById(R.id.currentWeightEt);
        }else{
            TextView LifeStyleTxt, current_diet;
            final CustomEt pediatricEtxt;
            final LinearLayout editLl;
            String str_condition,str_value;
            final Button selectedBtn, unselectedBtn;

            LayoutInflater inflate = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflate.inflate(R.layout.mdlive_pediatric_baseadapter, parent, false);
            LifeStyleTxt = (TextView) convertview.findViewById(R.id.lifestyle_txt);
            current_diet = (TextView) convertview.findViewById(R.id.current_diet);
            selectedBtn = (Button)convertview.findViewById(R.id.exercise_YesBtn);
            unselectedBtn = (Button) convertview.findViewById(R.id.exercise_NoBtn);
            editLl = (LinearLayout)convertview.findViewById(R.id.LinarLay);
            pediatricEtxt = (CustomEt)convertview.findViewById(R.id.pediatricEtxt);
            LifeStyleTxt.setText(array.get(pos).get("condition"));
            str_condition = array.get(pos).get("condition");
            str_value = array.get(pos).get("value");
            if(array.get(pos).get("value") != null && array.get(pos).get("value").equalsIgnoreCase("yes")){
                if(str_condition != null && str_condition.equals("Immunization up to date?")){
                    immunizationEt = (CustomEt)convertview.findViewById(R.id.pediatricEtxt);
                    editLl.setVisibility(View.VISIBLE);
                    pediatricEtxt.setText(getPediatricEditValues(PreferenceConstants.PEDIATRIC_IMMUNISATION_PREF));
                    setFocusListener(immunizationEt, pos, PreferenceConstants.PEDIATRIC_IMMUNISATION_PREF);
                    if(temp.get(PreferenceConstants.PEDIATRIC_IMMUNISATION_PREF) != null)
                         pediatricEtxt.setText(temp.get(PreferenceConstants.PEDIATRIC_IMMUNISATION_PREF));
                }
                if (str_condition != null && str_condition.equals("Birth complications")){
                    birthCompleteEt = (CustomEt)convertview.findViewById(R.id.pediatricEtxt);
                    editLl.setVisibility(View.VISIBLE);
                    pediatricEtxt.setText(getPediatricEditValues(PreferenceConstants.PEDIATRIC_BIRTH_PREF));
                    setFocusListener(birthCompleteEt, pos, PreferenceConstants.PEDIATRIC_BIRTH_PREF);
                    if(temp.get(PreferenceConstants.PEDIATRIC_BIRTH_PREF) != null)
                        pediatricEtxt.setText(temp.get(PreferenceConstants.PEDIATRIC_BIRTH_PREF));
                }
                if (str_condition != null && str_condition.equals("Newborn complications")){
                    editLl.setVisibility(View.VISIBLE);
                    newBornEt = (CustomEt)convertview.findViewById(R.id.pediatricEtxt);
                    pediatricEtxt.setText(getPediatricEditValues(PreferenceConstants.PEDIATRIC_CHILD_PREF));
                    setFocusListener(newBornEt, pos, PreferenceConstants.PEDIATRIC_CHILD_PREF);
                    if(temp.get(PreferenceConstants.PEDIATRIC_CHILD_PREF) != null)
                        pediatricEtxt.setText(temp.get(PreferenceConstants.PEDIATRIC_CHILD_PREF));
                }
            }else{
                editLl.setVisibility(View.GONE);
            }

            if(array.get(pos).get("condition") != null && array.get(pos).get("condition").trim().equalsIgnoreCase("Delivery type")){
                current_diet.setVisibility(View.VISIBLE);
                selectedBtn.setVisibility(View.GONE);
                unselectedBtn.setVisibility(View.GONE);
                showAlertView(current_diet);
            }else if(array.get(pos).get("condition") != null && array.get(pos).get("condition").trim().equalsIgnoreCase("Current Diet")){
                current_diet.setVisibility(View.VISIBLE);
                selectedBtn.setVisibility(View.GONE);
                unselectedBtn.setVisibility(View.GONE);
                showAlertView(current_diet);
            }

            selectedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    array.get(pos).put("name", array.get(pos).get("condition"));
                    array.get(pos).put("value","yes");

                    if("Immunization up to date?".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.VISIBLE);
                    }
                    if("Birth complications".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.VISIBLE);

                    }
                    if("Newborn complications".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.VISIBLE);
                    }
                    changeButtonColor(selectedBtn, unselectedBtn);
                }
            });
            unselectedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    array.get(pos).put("name",array.get(pos).get("condition"));
                    array.get(pos).put("value","no");
                    if("Immunization up to date?".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.GONE);
                    }
                    if("Birth complications".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.GONE);
                    }
                    if("Newborn complications".equalsIgnoreCase(array.get(pos).get("condition")))
                    {
                        editLl.setVisibility(View.GONE);
                    }
                    changeButtonColor(unselectedBtn, selectedBtn);
                }
            });
            if(str_value != null && "yes".equalsIgnoreCase(str_value)){
                changeButtonColor(selectedBtn, unselectedBtn);
            }else if(str_value != null && "no".equalsIgnoreCase(str_value)){
                changeButtonColor(unselectedBtn, selectedBtn);
            }else{
                changeButtonColor(unselectedBtn, selectedBtn);
            }
        }
        return convertview;
    }

    public void setFocusListener(final EditText editTextInstance, final int pos, final String pref_key){
        editTextInstance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == pos) {
                                lastFocussedPosition = pos;
                                editTextInstance.requestFocus();
                            }
                        }
                    }, 200);

                } else {
                    lastFocussedPosition = -1;
                }
            }
        });
        editTextInstance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                temp.put(pref_key, s.toString());
            }
        });
    }

    public void saveAllQuestionAnswersForPerdiotric(){
        if(immunizationEt != null){
            SavePediatricEditValues(PreferenceConstants.PEDIATRIC_IMMUNISATION_PREF, immunizationEt.getText().toString());
            Log.v("immunizationEt", immunizationEt.getText().toString());
        }

        if(birthCompleteEt != null){
            SavePediatricEditValues(PreferenceConstants.PEDIATRIC_BIRTH_PREF, birthCompleteEt.getText().toString());
            Log.v("birthCompleteEt", birthCompleteEt.getText().toString());
        }

        if(newBornEt != null){
            SavePediatricEditValues(PreferenceConstants.PEDIATRIC_CHILD_PREF, newBornEt.getText().toString());
            Log.v("newBornEt", newBornEt.getText().toString());
        }

    }


    /**
     * The Corresponding Zip Code and the Short name of the city should be saved in the Preferences and will be
     * triggerred in the Requird places.
     */
    public void SavePediatricEditValues(String prefText, String answerText) {
        SharedPreferences settings = context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefText, answerText);
        editor.commit();
    }

    public String getPediatricEditValues(String prefKeyValue) {
        SharedPreferences settings = context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        return settings.getString(prefKeyValue, "");
    }

    public void showAlertView(final TextView modifyText){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(context.getString(R.string.mdl_pediatric_select_hint));
        arrayAdapter.add(context.getString(R.string.mdl_vaginal_delivery));
        arrayAdapter.add(context.getString(R.string.mdl_C_section));
        arrayAdapter.add(context.getString(R.string.mdl_bottle_feed));
        arrayAdapter.add(context.getString(R.string.mdl_breast_feed));
        arrayAdapter.add(context.getString(R.string.mdl_full_diet));
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modifyText.setText(arrayAdapter.getItem(which));
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builderSingle.create();
        modifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    /**
     *    This is to Check the availability of the Doctor.if the next availabilty of doctor
     *    is available then the time stamp should be  visible else it should be hidden.
     */
    private void changeButtonColor(Button selectedBtn,Button unselectedBtn){
        selectedBtn.setBackgroundResource(R.drawable.btn_selected);
        unselectedBtn.setBackgroundResource(R.drawable.btn_unselected);
        unselectedBtn.setTextColor(Color.parseColor("#A4A4A4"));
        selectedBtn.setTextColor(Color.parseColor("#ffffff"));
    }

    public ArrayList<HashMap<String, String>> getResult(){
       return array;
    }

}