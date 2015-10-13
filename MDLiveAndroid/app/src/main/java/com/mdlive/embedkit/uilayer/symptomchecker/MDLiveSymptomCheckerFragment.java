package com.mdlive.embedkit.uilayer.symptomchecker;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

/**
 * A simple {@link Fragment} subclass.
 * This class provides the symptom checker display
 */
public class MDLiveSymptomCheckerFragment extends MDLiveBaseFragment {
    private WebView mWebView;

    public static MDLiveSymptomCheckerFragment newInstance() {
        final MDLiveSymptomCheckerFragment fragment = new MDLiveSymptomCheckerFragment();
        return fragment;
    }

    public MDLiveSymptomCheckerFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mdlive_symptom_checker_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView)view.findViewById(R.id.iframewebview);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideProgressDialog();
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgressDialog();
                super.onPageStarted(view, url, favicon);
            }
        });
        mWebView.loadUrl(AppSpecificConfig.SYMPTOM_CHECKER_URL);

    }
}
