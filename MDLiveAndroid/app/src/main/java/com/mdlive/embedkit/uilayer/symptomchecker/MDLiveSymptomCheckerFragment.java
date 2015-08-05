package com.mdlive.embedkit.uilayer.symptomchecker;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mdlive.embedkit.R;

/**
 * A simple {@link Fragment} subclass.
 * This class provides the symptom checker display
 */
public class MDLiveSymptomCheckerFragment extends Fragment {

    private WebView iFrameWebview;

    // initialize the class fragment object
    public static MDLiveSymptomCheckerFragment newInstance() {
        final MDLiveSymptomCheckerFragment iframeFragment = new MDLiveSymptomCheckerFragment();
        return iframeFragment;
    }

    // constructor
    public MDLiveSymptomCheckerFragment() {
        // Required empty public constructor
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mdlive_symptom_checker_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize variable
        iFrameWebview = (WebView)view.findViewById(R.id.iframewebview);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set the javscript enabled true
        iFrameWebview.getSettings().setJavaScriptEnabled(true);
        // load the symptom checker url
        iFrameWebview.loadUrl(getString(R.string.symptomchecker_url));

    }
}
