package com.mdlive.embedkit.uilayer.helpandsupport;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Message;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.helpandsupport.HelpandSupportAskQuestionPostService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AskQuestionFragment extends MDLiveBaseFragment {
    public EditText questionEditText;

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
        return inflater.inflate(R.layout.ask_question_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionEditText = (EditText)view.findViewById(R.id.questionId);
    }

    public void onTickClicked() {
        if (questionEditText.getText().toString() != null && questionEditText.getText().length() > 0
                && !questionEditText.getText().toString().startsWith(" ")) {
            MdliveUtils.hideKeyboard(getActivity(), (View) questionEditText);
            try {
                final JSONObject outerJsonObject = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", questionEditText.getText().toString());
                jsonObject.put("subject", "Subject");
                outerJsonObject.put("message",jsonObject);

                fetchAskQuestionServiceData(outerJsonObject);
            } catch (JSONException e) {

            }
        } else if (questionEditText.getText().length() == 0) {
            if (getActivity() != null) {
                MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdli_ask_a_question_validation));
            }
        }

    }

    private void fetchAskQuestionServiceData(final JSONObject jsonObject) {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    handleSuccessResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }};

        final HelpandSupportAskQuestionPostService helpandSupportAskQuestionPostService = new HelpandSupportAskQuestionPostService(getActivity(), getProgressDialog());
        helpandSupportAskQuestionPostService.postHelpandSupportAskQuestionPostService(jsonObject, responseListener, errorListener);

    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgressDialog();

            final Gson gson = new Gson();
            final Message message =  gson.fromJson(response.toString(), Message.class);

            MdliveUtils.showDialog(getActivity(), message.message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() != null && getActivity() instanceof MDLiveHelpAndSupportActivity) {
                        ((MDLiveHelpAndSupportActivity) getActivity()).onCrossClicked(questionEditText);
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
