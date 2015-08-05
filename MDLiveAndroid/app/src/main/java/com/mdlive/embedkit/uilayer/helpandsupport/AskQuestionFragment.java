package com.mdlive.embedkit.uilayer.helpandsupport;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.helpandsupport.HelpandSupportAskQuestionPostService;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AskQuestionFragment extends Fragment {

    private View view;
    public EditText questionEditText;

    private ProgressBar progressBar;
    private ProgressDialog pDialog = null;

    HashMap<String, String> hm;
    JSONObject outerJsonObject;

    public static AskQuestionFragment newInstance() {
        final AskQuestionFragment fragment = new AskQuestionFragment();
        return fragment;
    }

    public AskQuestionFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.ask_question_fragment, container, false);

        findWidgetId();

        return view;
    }

    private void findWidgetId() {

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        questionEditText = (EditText)view.findViewById(R.id.questionId);

    }

    public void cancelTextViewEvent() {
        getActivity().getFragmentManager().beginTransaction().remove(AskQuestionFragment.this).commit();
    }

    public void submitTexViewEvent() {

        if (questionEditText.getText().toString() != null && questionEditText.getText().length() != 0) {
            String questionText = questionEditText.getText().toString();
            String subject = "Request for advice";
            Log.d("questionText", questionText);

            hm = new HashMap<String,String>();

            try {
                outerJsonObject = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", questionText);
                jsonObject.put("subject", subject);
                outerJsonObject.put("message",jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("JSONObject request",outerJsonObject.toString());

            getHelpAndSupportAskQuestionServiceData();

        } else if (questionEditText.getText().length() == 0) {
            Log.d("Enter Question", "Enter Question");
        }

    }

    private void getHelpAndSupportAskQuestionServiceData() {

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

        HelpandSupportAskQuestionPostService helpandSupportAskQuestionPostService = new HelpandSupportAskQuestionPostService(getActivity(), pDialog);
        helpandSupportAskQuestionPostService.postHelpandSupportAskQuestionPostService(outerJsonObject,responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            setInfoVisibilty();
            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

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

}
