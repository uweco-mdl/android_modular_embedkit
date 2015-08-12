package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by venkataraman_r on 8/12/2015.
 */
public class CreateAccountFragment extends MDLiveBaseFragment {
    private OnSignupSuccess mOnSignupSuccess;

    public static CreateAccountFragment newInstance() {
        final CreateAccountFragment fragment = new CreateAccountFragment();
        return fragment;
    }

    public CreateAccountFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnSignupSuccess = (OnSignupSuccess) activity;
        } catch (ClassCastException cce) {
            logE("CreateAccountFragment", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_createaccount, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WebView webview = (WebView) view.findViewById(R.id.webView);
        webview.loadUrl(AppSpecificConfig.URL_SIGN_UP);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                showProgressDialog();

                List<NameValuePair> params = null;
                try {
                    params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                for (NameValuePair param : params) {
                    if (param.getName().equals("remoteUserId")) {
                        if (getActivity() != null) {
                            SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(PreferenceConstants.USER_UNIQUE_ID, param.getValue());
                            editor.commit();

                            if (mOnSignupSuccess != null) {
                                mOnSignupSuccess.onSignUpSucess();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                hideProgressDialog();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnSignupSuccess = null;
    }

    public static interface OnSignupSuccess {
        void onSignUpSucess();
    }
}
