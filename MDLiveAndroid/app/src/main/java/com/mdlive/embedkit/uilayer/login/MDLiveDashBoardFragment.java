package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.DashBoardSpinnerAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.EmailConfirmationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDLiveDashBoardFragment extends MDLiveBaseFragment {
    private OnUserSelectionChanged mOnUserSelectionChanged;
    private OnNotificationCliked mOnNotificationCliked;

    private Spinner mSpinner;
    private DashBoardSpinnerAdapter mAdapter;

    private View mEmailConfirmationView;

    private View mNotificationView;

    private UserBasicInfo mUserBasicInfo;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListenerUserInfo = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final User selectedUser = mAdapter.getItem(position);

            // Add child selected
            if (User.MODE_ADD_CHILD == selectedUser.mMode || StringConstants.ADD_CHILD.equalsIgnoreCase(selectedUser.mName)) {
                // Setting selection to 0, as do not want Add child to Show
                mSpinner.setOnItemSelectedListener(null);
                mSpinner.setSelection(0);
                // Preventing  onItemSeleection to get callied
                mSpinner.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSpinner.setOnItemSelectedListener(mOnItemSelectedListenerUserInfo);
                    }
                }, 100);

                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onAddChildSelectedFromDashboard(selectedUser,
                            mUserBasicInfo.getDependantUsers() == null ? 0 : mUserBasicInfo.getDependantUsers().size());
                }
            }
            // Dependent User selected
            else if (User.MODE_DEPENDENT == selectedUser.mMode) {
                logE("User Type", "" + selectedUser.mMode);
                logE("User Type", "Expected Dependent");
                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onDependentSelected(selectedUser);
                }
            }
            // The Parent User Selected
            else {
                logE("User Type", "" + selectedUser.mMode);
                logE("User Type", "Expected Primary");
                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onPrimarySelected(selectedUser);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
            mOnUserSelectionChanged = (OnUserSelectionChanged) activity;
            mOnNotificationCliked = (OnNotificationCliked) activity;
        } catch (ClassCastException cce) {
            logE("MDLiveDashBoardFRagment", activity.getClass().getSimpleName() + ", should implement OnUserSelectionChanged");
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

        mSpinner = (Spinner) view.findViewById(R.id.dash_board_spinner);
        mEmailConfirmationView = view.findViewById(R.id.dash_board_email_text_view);
        mEmailConfirmationView.setVisibility(View.GONE);

        mNotificationView = view.findViewById(R.id.dash_board_notification_layout);
        if (mNotificationView != null) {
            mNotificationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnUserSelectionChanged = null;
        mOnNotificationCliked = null;
    }

    public void setPrimaryUserSelected() {
        mSpinner.setSelection(0);
    }

    public void onUserInformationLoaded(final UserBasicInfo userBasicInfo) {
        if (mSpinner != null) {
            mUserBasicInfo = userBasicInfo;
            List<User> users = null;

            if (mUserBasicInfo.getPrimaryUser()) {
                users = UserBasicInfo.getUsersAsPrimaryUser(getActivity());
            } else {
                users = UserBasicInfo.getUsersAsDependentUser(getActivity());
            }

            if (mAdapter != null) {
                mAdapter.clear();
                mAdapter.addAll(users);
            } else {
                mAdapter = new DashBoardSpinnerAdapter(getActivity(), android.R.layout.simple_list_item_1, users);
            }

            if (mUserBasicInfo.getPersonalInfo().getEmailConfirmed()) {
                mEmailConfirmationView.setVisibility(View.GONE);
            } else {
                mEmailConfirmationView.setVisibility(View.VISIBLE);
            }
            SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPref.edit();
            editor1.putString(PreferenceConstants.PREFFERED_LANGUAGE, userBasicInfo.getPersonalInfo().getLanguagePreference());
            editor1.commit();
            mSpinner.setOnItemSelectedListener(null);
            mSpinner.setAdapter(mAdapter);
            // Preventing  onItemSeleection to get callied
            mSpinner.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSpinner.setOnItemSelectedListener(mOnItemSelectedListenerUserInfo);
                }
            }, 100);
        }
    }

    public void loadEmailConfirmationService() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), response.getString("message"));
                } catch (JSONException e) {
                    logE("Email Confirmation", "Email Confirmation : " + response.toString());
                    logE("Email Confirmation", "Email Confirmation : " + e.getMessage());
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        EmailConfirmationService service = new EmailConfirmationService(getActivity(), null);
        service.emailConfirmation(successCallBackListener, errorListener, null);
    }

    public void showNotification(final Appointment appointment) {
        if (mNotificationView != null) {
            mNotificationView.setTag(appointment);
            mNotificationView.setVisibility(View.VISIBLE);
            mNotificationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Appointment appo = (Appointment) v.getTag();
                        if (appo != null && mOnNotificationCliked != null) {
                            mOnNotificationCliked.onNotificationClicked(appo);
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    public void hideNotification() {
        if (mNotificationView != null) {
            mNotificationView.setTag(null);
            mNotificationView.setVisibility(View.GONE);
        }
    }

    public interface OnUserSelectionChanged {
        void onDependentSelected(final User user);
        void onPrimarySelected(final User user);
        void onAddChildSelectedFromDashboard(final User user, final int dependentUserSize);
    }

    public interface OnNotificationCliked {
        void onNotificationClicked(final Appointment appointment);
    }
}
