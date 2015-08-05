package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetSecurityQuestionService;
import com.mdlive.unifiedmiddleware.services.myaccounts.UpdateSecurityQuestionsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by venkataraman_r on 6/17/2015.
 */
public class SecurityQuestionsFragment extends Fragment {

    private Spinner mSecurityQuestion1 = null;
    private Spinner mSecurityQuestion2 = null;
    private EditText mSecurityAnswer1 = null;
    private EditText mSecurityAnswer2 = null;
    private Button mSave = null;
    private ProgressDialog pDialog;
    private ArrayList<String> mQuestions = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View securityQuestions = inflater.inflate(R.layout.fragments_security_questions, null);
        mSecurityQuestion1 = (Spinner)securityQuestions.findViewById(R.id.question1);
        mSecurityQuestion2 = (Spinner)securityQuestions.findViewById(R.id.question2);
        mSecurityAnswer1 = (EditText)securityQuestions.findViewById(R.id.answer1);
        mSecurityAnswer2 = (EditText)securityQuestions.findViewById(R.id.answer2);
        mSave = (Button)securityQuestions.findViewById(R.id.save);

        mQuestions = new ArrayList<String>();

        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        getSecurityQuestionsService();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.change_security_questions));

        mSave.setOnClickListener(saveClickListener);
        return securityQuestions;
    }

    View.OnClickListener saveClickListener = new View.OnClickListener(){
        public void onClick(View v)
        {
            String answer1 = mSecurityAnswer1.getText().toString();
            String answer2 = mSecurityAnswer2.getText().toString();

            if( !TextUtils.isEmpty(answer1) && !TextUtils.isEmpty(answer2))
            {
                try {
                    JSONObject parent = new JSONObject();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("question1", mSecurityQuestion1.getSelectedItem().toString());
                    jsonObject.put("answer1", mSecurityAnswer1.getText().toString());
                    jsonObject.put("question2",  mSecurityQuestion2.getSelectedItem().toString());
                    jsonObject.put("answer2", mSecurityAnswer2.getText().toString());
                    parent.put("security", jsonObject);
                    updateSecurityQuestionsService(parent.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(getActivity(), "Answers are mandatory", Toast.LENGTH_SHORT).show();
        }
    };


    private void getSecurityQuestionsService() {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSecurityQuestionsSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };

        GetSecurityQuestionService service = new GetSecurityQuestionService(getActivity(), null);
        service.getSecurityQuestions(successCallBackListener, errorListener, null);
    }

    public void handleSecurityQuestionsSuccessResponse(JSONObject response)
    {
        pDialog.dismiss();
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,getListofValues(response));
            mSecurityQuestion1.setAdapter(adapter);
            mSecurityQuestion2.setAdapter(adapter);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Hello", "I am in error");
        }
    }

    private void updateSecurityQuestionsService(String params) {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleUpdateSecurityQuestionsSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };

        UpdateSecurityQuestionsService service = new UpdateSecurityQuestionsService(getActivity(), null);
        service.updateSecurityQuestions(successCallBackListener, errorListener, params);
    }

    private void handleUpdateSecurityQuestionsSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services
            Toast.makeText(getActivity(),response.getString("message"),Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<String> getListofValues(final JSONObject response) {
        final List<String> list = new ArrayList<String>();

        try {
            final JSONObject questions = response.getJSONObject("questions");
            Iterator<String> a = (Iterator<String>) questions.keys();

            while (a.hasNext()) {
                String key = a.next();
                //String value = (String) questions.get(key);
                list.add(key);
                Log.i("value", key);
            }

        } catch (Exception e) {

        }
        Log.i("list",list.get(0));
        return list;

    }
}
