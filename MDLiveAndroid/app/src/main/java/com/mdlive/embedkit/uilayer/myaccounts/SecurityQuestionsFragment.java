package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetSecurityQuestionService;
import com.mdlive.unifiedmiddleware.services.myaccounts.UpdateSecurityQuestionsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by venkataraman_r on 6/17/2015.
 */
public class SecurityQuestionsFragment extends MDLiveBaseFragment {

    public static SecurityQuestionsFragment newInstance(String response) {

        final SecurityQuestionsFragment securityQuestionsFragment = new SecurityQuestionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Response", response);
        securityQuestionsFragment.setArguments(bundle);
        return securityQuestionsFragment;
    }

    private RelativeLayout mSecurityQuestion1Layout = null;
    private RelativeLayout mSecurityQuestion2Layout = null;
    private android.support.v7.widget.CardView mSecurityAnswer1Layout = null;
    private android.support.v7.widget.CardView mSecurityAnswer2Layout = null;
    private TextView mSecurityQuestion1 = null;
    private TextView mSecurityQuestion2 = null;
    private EditText mSecurityAnswer1 = null;
    private EditText mSecurityAnswer2 = null;
    private ArrayList<String> mQuestions = null;
    private String response;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View securityQuestions = inflater.inflate(R.layout.fragments_security_questions, null);
        getActivity().setTitle(getString(R.string.mdl_change_security_questions));

        mSecurityQuestion1Layout = (RelativeLayout)securityQuestions.findViewById(R.id.changeQuestion1Layout);
        mSecurityQuestion2Layout = (RelativeLayout)securityQuestions.findViewById(R.id.changeQuestion2Layout);
        mSecurityQuestion1 = (TextView)securityQuestions.findViewById(R.id.changeQuestion1);
        mSecurityQuestion2 = (TextView)securityQuestions.findViewById(R.id.changeQuestion2);
        mSecurityAnswer1 = (EditText)securityQuestions.findViewById(R.id.answer1);
        mSecurityAnswer2 = (EditText)securityQuestions.findViewById(R.id.answer2);
        mSecurityAnswer1Layout = (android.support.v7.widget.CardView)securityQuestions.findViewById(R.id.answer1Layout);
        mSecurityAnswer2Layout = (android.support.v7.widget.CardView)securityQuestions.findViewById(R.id.answer2Layout);

        mQuestions = new ArrayList<String>();

        response = getArguments().getString("Response");

        if (response != null) {

            try {
                JSONObject responseDetail = new JSONObject(response);
                JSONObject securityQuestion = responseDetail.getJSONObject("security");
                if(!TextUtils.isEmpty(securityQuestion.toString())) {
                    mSecurityQuestion1.setText(securityQuestion.getString("question1"));
                    mSecurityQuestion2.setText(securityQuestion.getString("question2"));
                    mSecurityAnswer1.setText(securityQuestion.getString("answer1"));
                    mSecurityAnswer2.setText(securityQuestion.getString("answer2"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        final LayoutInflater inflater1 = getLayoutInflater(savedInstanceState);

        if (TextUtils.isEmpty(mSecurityAnswer1.getText().toString()) && TextUtils.isEmpty(mSecurityAnswer2.getText().toString())) {
            if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                ((MyAccountsHome) getActivity()).hideTick();
            }
        }

        mSecurityAnswer2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(mSecurityAnswer1.getText().toString()) && !TextUtils.isEmpty(mSecurityAnswer2.getText().toString())) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).showTick();
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                }
            }
        });


        mSecurityAnswer1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(mSecurityAnswer1.getText().toString()) && !TextUtils.isEmpty(mSecurityAnswer2.getText().toString())) {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).showTick();
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MyAccountsHome) {
                        ((MyAccountsHome) getActivity()).hideTick();
                    }
                }
            }
        });

        mSecurityQuestion1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MdliveUtils.hideKeyboard(getActivity(),view);

                final SecurityQuestionsAdapter adapter = new SecurityQuestionsAdapter(getActivity(), mQuestions);

//                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

//                final Dialog dialog = new Dialog(getActivity());
//                dialog.setContentView(R.layout.custom_dialog);
//
//                dialog.setTitle("List");
//                ListView lv = (ListView) dialog.findViewById(R.id.lv);
//                lv.setAdapter(adapter);
//                lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                dialog.show();

                showSecurityQuestionsDialog(mSecurityQuestion1,mSecurityAnswer1Layout);
                mSecurityAnswer1.setText("");

//                lv.setOnItemClickListener(new ListView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> listView, View itemView, int position, long itemId) {
//
//                        Toast.makeText(getActivity(), mQuestions.get(position), Toast.LENGTH_SHORT).show();
//                        mSecurityQuestion1.setText(mQuestions.get(position));
//                        dialog.dismiss();
//
//                    }
//                });
//                lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        Toast.makeText(getActivity(), mQuestions.get(i), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });

//                ListView listView = new ListView(getActivity());
//                listView.setAdapter(adapter);
//                listView.setDivider(null);
//
//                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//                builder.setTitle("Select security questions");
//                builder.setView(listView);
//
//                final android.app.AlertDialog alert = builder.create();
//                alert.show();
//
//                listView.setOnItemClickListener(new ListView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> listView, View itemView, int position, long itemId) {
//
//                        Toast.makeText(getActivity(), mQuestions.get(position), Toast.LENGTH_SHORT).show();
//                        mSecurityQuestion1.setText(mQuestions.get(position));
//                        alert.dismiss();
//                    }
//                });


            }
        });

        mSecurityQuestion2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MdliveUtils.hideKeyboard(getActivity(),view);
                showSecurityQuestionsDialog(mSecurityQuestion2,mSecurityAnswer2Layout);
                mSecurityAnswer2.setText("");
            }
        });

        return securityQuestions;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSecurityQuestionsService();
    }

    private void getSecurityQuestionsService() {

        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSecurityQuestionsSuccessResponse(response);
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
            }
        };

        GetSecurityQuestionService service = new GetSecurityQuestionService(getActivity(), null);
        service.getSecurityQuestions(successCallBackListener, errorListener, null);
    }

    public void handleSecurityQuestionsSuccessResponse(JSONObject response)
    {
        hideProgressDialog();
        mQuestions = getListofValues(response);
    }

    public  ArrayList<String> getListofValues(final JSONObject response) {
        final ArrayList<String> list = new ArrayList<String>();

        try {
            final JSONObject questions = response.getJSONObject("questions");
            Iterator<String> a = (Iterator<String>) questions.keys();

            while (a.hasNext()) {
                String key = a.next();
                list.add(key);
            }

        } catch (Exception e) {

        }

        if((mSecurityAnswer1.getText().length()) == 0 && (mSecurityAnswer2.getText().length() == 0)) {
            mSecurityQuestion1.setText("Select Question");
            mSecurityQuestion2.setText("Select Question");
            mSecurityAnswer1Layout.setVisibility(View.GONE);
            mSecurityAnswer2Layout.setVisibility(View.GONE);
        }
        return list;
    }

    private void showSecurityQuestionsDialog(final TextView textView,final android.support.v7.widget.CardView answerLayout) {
        if (getActivity() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select security question");
//        final String[] stringArray = mQuestions.toArray(new String[mQuestions.size()]);
       final ArrayList<String> stringArray =new ArrayList<String>();
//        final String[] stringArray =new String[mQuestions.size()];
        int j=0;
        for(int i=0;i<mQuestions.size();i++)
        {
            if(textView == mSecurityQuestion1) {
                if (!mSecurityQuestion2.getText().toString().equals(mQuestions.get(i))) {
                    stringArray.add(mQuestions.get(i));
                    j++;
                }
            }

            if(textView == mSecurityQuestion2) {
                if (!mSecurityQuestion1.getText().toString().equals(mQuestions.get(i))) {
                    stringArray.add(mQuestions.get(i));
                    j++;
                }
            }
        }

        builder.setItems(stringArray.toArray(new String[stringArray.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                textView.setText(stringArray.get(i));
                answerLayout.setVisibility(View.VISIBLE);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void uploadSecurityQuestions()
    {
        String answer1 = mSecurityAnswer1.getText().toString();
        String answer2 = mSecurityAnswer2.getText().toString();

        if( !TextUtils.isEmpty(answer1) && !TextUtils.isEmpty(answer2))
        {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question1", mSecurityQuestion1.getText().toString());
                jsonObject.put("answer1", mSecurityAnswer1.getText().toString());
                jsonObject.put("question2",  mSecurityQuestion2.getText().toString());
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
    private void updateSecurityQuestionsService(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleUpdateSecurityQuestionsSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.tabcontent, new MyProfileFragment()).commit();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                }
            }
        };

        UpdateSecurityQuestionsService service = new UpdateSecurityQuestionsService(getActivity(), null);
        service.updateSecurityQuestions(successCallBackListener, errorListener, params);
    }

    private void handleUpdateSecurityQuestionsSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
