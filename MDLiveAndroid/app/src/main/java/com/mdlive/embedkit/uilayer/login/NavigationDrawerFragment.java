package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by venkataraman_r on 7/16/2015.
 */
public class NavigationDrawerFragment extends MDLiveBaseFragment {

    private NavigationDrawerCallbacks mCallbacks;

    private ListView mDrawerListView;

    private int mCurrentSelectedPosition = 0;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance() {
        final NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDrawerListView = (ListView) view.findViewById(R.id.navigation_drawer_list_view);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ArrayList<String> drawerItems = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.left_navigation_items)));
        TypedArray imgs = getResources().obtainTypedArray(R.array.left_navigation_items_image);

        mDrawerListView.setAdapter(new DrawerAdapter(this.getActivity(), drawerItems, imgs));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void selectItem(int position) {

        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}