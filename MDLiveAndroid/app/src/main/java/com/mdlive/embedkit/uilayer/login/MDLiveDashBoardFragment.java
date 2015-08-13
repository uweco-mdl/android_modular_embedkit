package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.DashBoardSpinnerAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDLiveDashBoardFragment extends MDLiveBaseFragment {
    private SendNotification mSendNotification;

    //private CircularNetworkImageView mCircularNetworkImageView;
    private Spinner mSpinner;
    private DashBoardSpinnerAdapter mAdapter;

    private UserBasicInfo mUserBasicInfo;

    public static MDLiveDashBoardFragment newInstance() {
        final MDLiveDashBoardFragment fragment = new MDLiveDashBoardFragment();
        return fragment;
    }

    public MDLiveDashBoardFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSendNotification = (SendNotification) activity;
        } catch (ClassCastException cce) {
            logE("MDLiveDashBoradFragment", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mdlive_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mCircularNetworkImageView = (CircularNetworkImageView) view.findViewById(R.id.dash_board_circular_image_view);
        mSpinner = (Spinner) view.findViewById(R.id.dash_board_spinner);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadUserInformationDetails();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mSendNotification = null;
    }

    /**
     * makes the customer/user_information call to get the User information.
     *
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     *
     * After getting the uniqueid save it to shared preference.
     */
    private void loadUserInformationDetails() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                final Gson gson = new Gson();
                mUserBasicInfo = gson.fromJson(response.toString().trim(), UserBasicInfo.class);
                mUserBasicInfo.saveToSharedPreference(getActivity());

                if (mSendNotification != null) {
                    mSendNotification.sendNotification(mUserBasicInfo);
                }

//                if (mCircularNetworkImageView != null) {
//                    mCircularNetworkImageView.setImageUrl(mUserBasicInfo.getPersonalInfo().getImageUrl(), ApplicationController.getInstance().getImageLoader(getActivity()));
//                }

                if (mSpinner != null) {
                    mAdapter = new DashBoardSpinnerAdapter(getActivity(), android.R.layout.simple_list_item_1, UserBasicInfo.getAllUsers(getActivity()));
                    mSpinner.setAdapter(mAdapter);
                }
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }};

        final UserBasicInfoServices services = new UserBasicInfoServices(getActivity(), null);
        services.getUserBasicInfoRequest("", successCallBackListener, errorListener);
    }

    public interface SendNotification {
        void sendNotification(UserBasicInfo userBasicInfo);
    }
}
