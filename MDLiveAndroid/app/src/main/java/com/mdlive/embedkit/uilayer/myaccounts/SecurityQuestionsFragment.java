package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

    private RelativeLayout mSecurityQuestion1Layout = null;
    private RelativeLayout mSecurityQuestion2Layout = null;
    private TextView mSecurityQuestion1 = null;
    private TextView mSecurityQuestion2 = null;
    private EditText mSecurityAnswer1 = null;
    private EditText mSecurityAnswer2 = null;
    private ArrayList<String> mQuestions = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View securityQuestions = inflater.inflate(R.layout.fragments_security_questions, null);
        mSecurityQuestion1Layout = (RelativeLayout)securityQuestions.findViewById(R.id.changeQuestion1Layout);
        mSecurityQuestion2Layout = (RelativeLayout)securityQuestions.findViewById(R.id.changeQuestion2Layout);
        mSecurityQuestion1 = (TextView)securityQuestions.findViewById(R.id.changeQuestion1);
        mSecurityQuestion2 = (TextView)securityQuestions.findViewById(R.id.changeQuestion2);
        mSecurityAnswer1 = (EditText)securityQuestions.findViewById(R.id.answer1);
        mSecurityAnswer2 = (EditText)securityQuestions.findViewById(R.id.answer2);

        mQuestions = new ArrayList<String>();

        final LayoutInflater inflater1 = getLayoutInflater(savedInstanceState);

        getSecurityQuestionsService();

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

                showSecurityQuestionsDialog(mSecurityQuestion1,mSecurityAnswer1);

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
                showSecurityQuestionsDialog(mSecurityQuestion2,mSecurityAnswer2);
            }
        });

        return securityQuestions;
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
                Log.i("value", key);
            }

        } catch (Exception e) {

        }
        Log.i("list",list.get(0));

        mSecurityQuestion1.setText(list.get(0));
        mSecurityQuestion2.setText(list.get(0));
        return list;

    }

    private void showSecurityQuestionsDialog(final TextView textView,final EditText editText) {
        if (getActivity() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select security question");

        final String[] stringArray = mQuestions.toArray(new String[mQuestions.size()]);
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                textView.setText(mQuestions.get(i));
                editText.setVisibility(View.VISIBLE);
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
