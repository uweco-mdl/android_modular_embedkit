package com.mdlive.myhealth;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.mdlive.myhealth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MDLiveFullBgFragment extends Fragment {

    private View view;

    ListAdapter adapter;

    public static MDLiveFullBgFragment newInstance() {
        final MDLiveFullBgFragment fragment = new MDLiveFullBgFragment();
        return fragment;
    }

    public MDLiveFullBgFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.mdlive_full_bg, container, false);
        return view;
    }

}