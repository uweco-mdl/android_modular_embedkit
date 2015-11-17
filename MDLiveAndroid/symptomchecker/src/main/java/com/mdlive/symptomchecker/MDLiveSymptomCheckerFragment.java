package com.mdlive.symptomchecker;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.symptomchecker.R;
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
                if (url.contains("location=SAV")) {
                    try {
                        Class clazz = Class.forName(getString(R.string.mdl_mdlive_sav_module));
                        final Intent intent = new Intent(getActivity(), clazz);
                        startActivity(intent);
                    }catch (ClassNotFoundException e){
                        Toast.makeText(getActivity(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
                    }
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
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
