package com.mdlive.embedkit.uilayer.myhealth.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthActivity;
import com.mdlive.embedkit.uilayer.familyhistory.MDLiveFamilyHistory;
import com.mdlive.embedkit.uilayer.lifestyle.MDLiveLifestyleActivity;
import com.mdlive.embedkit.uilayer.myhealth.activity.adapter.MyHealthListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjibkumar_p on 7/28/2015.
 */
public class MDLiveMyHealthListFragment extends MDLiveBaseFragment {
    public static MDLiveMyHealthListFragment newInstance() {
        MDLiveMyHealthListFragment fragment = new MDLiveMyHealthListFragment();
        return fragment;
    }

    public MDLiveMyHealthListFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mdlive_my_health_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = (ListView) view.findViewById(R.id.mdlive_my_health_list_fragment_list_view);
        if (listView != null) {
            final List<ListItem> items = new ArrayList<ListItem>();

            items.add(new ListItem(view.getContext().getString(R.string.mdl_family_history), R.drawable.ic_launcher ,"completed"));
            items.add(new ListItem(view.getContext().getString(R.string.mdl_behaviouralhealthhistory), R.drawable.ic_launcher,"completed"));
            items.add(new ListItem(view.getContext().getString(R.string.mdl_LifeStyle), R.drawable.ic_launcher,"completed"));
            items.add(new ListItem(view.getContext().getString(R.string.mdl_spinnerdisplay), R.drawable.ic_launcher,"completed"));

            final MyHealthListAdapter adapter = new MyHealthListAdapter(view.getContext(), R.layout.adapter_health_list, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final ListItem item = adapter.getItem(position);

                    Intent intent = null;
                    if (item.mString.equals(view.getContext().getString(R.string.mdl_family_history))) {
                        intent = new Intent(view.getContext(), MDLiveFamilyHistory.class);
                    } else if (item.mString.equals(view.getContext().getString(R.string.mdl_behaviouralhealthhistory))) {
                        intent = new Intent(view.getContext(), MDLiveBehaviouralHealthActivity.class);
                    } else if (item.mString.equals(view.getContext().getString(R.string.mdl_LifeStyle))) {
                        intent = new Intent(view.getContext(), MDLiveLifestyleActivity.class);
                    }else if (item.mString.equals(view.getContext().getString(R.string.mdl_spinnerdisplay))) {
                        //intent = new Intent(view.getContext(), SpinnerDropDownActivity.class);
                    }
                    else {
                        return;
                    }

                    startActivity(intent);
                }
            });
        }
    }
}
