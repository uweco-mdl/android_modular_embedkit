package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.DashBoardSpinnerAdapter;
import com.mdlive.embedkit.uilayer.payment.MDLiveStartVisit;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.OncallAppointment;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PendingAppointment;
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
    public static OnUserSelectionChanged mOnUserSelectionChanged;
    public static OnNotificationCliked mOnNotificationCliked;

    private String mCustomerDefaultNumber;
    private String mCustomerProvidedPhoneNumber;

    private Spinner mSpinner;
    private DashBoardSpinnerAdapter mAdapter;

    private View mEmailConfirmationView,mDashBoardEmailLl;

    private View mNotificationView, mEmailConfirmationIv;

    private TextView mMessageCountTextView,mEmailConfirmationTv;
    public WebView mWebView;
    public boolean isWebView;

    private UserBasicInfo mUserBasicInfo;

    public static OnUserSelectionChanged getUserSelectionInstance(){
        return mOnUserSelectionChanged;
    }

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
                if(NotificationFragment.getInstance() != null) {
                    NotificationFragment.getInstance().reloadPendingAppointment();
                }
            }
            // Dependent User selected
            else if (User.MODE_DEPENDENT == selectedUser.mMode) {
                logE("User Type", "" + selectedUser.mMode);
                logE("User Type", "Expected Dependent");
                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onDependentSelected(selectedUser);
                    if(NotificationFragment.getInstance() != null) {
                        NotificationFragment.getInstance().reloadPendingAppointment();
                }
            }
            }
            // The Parent User Selected
            else {
                logE("User Type", "" + selectedUser.mMode);
                logE("User Type", "Expected Primary");
                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onPrimarySelected(selectedUser);
                    if(NotificationFragment.getInstance() != null){

                        NotificationFragment.getInstance().reloadPendingAppointment();}
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
        mEmailConfirmationTv = (TextView) view.findViewById(R.id.email_confirmation_tv);
        mEmailConfirmationIv = view.findViewById(R.id.email_confirmation_iv);
        mDashBoardEmailLl = view.findViewById(R.id.dash_board_email_ll);
        mWebView = (WebView)view.findViewById(R.id.webView);
        mEmailConfirmationView.setVisibility(View.GONE);

        mNotificationView = view.findViewById(R.id.dash_board_notification_layout);
        if (mNotificationView != null) {
            mNotificationView.setVisibility(View.GONE);
        }

        mMessageCountTextView = (TextView) view.findViewById(R.id.message_count);
        if (mMessageCountTextView != null) {
            mMessageCountTextView.setVisibility(View.INVISIBLE);
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
        try {
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
                if (mMessageCountTextView != null) {
                    if (userBasicInfo.getNotifications().getMessages() > 0) {
                        mMessageCountTextView.setText(String.valueOf(userBasicInfo.getNotifications().getMessages()));
                        mMessageCountTextView.setVisibility(View.VISIBLE);
                    }
                }

                SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPref.edit();
                editor1.putString(PreferenceConstants.PREFFERED_LANGUAGE, userBasicInfo.getPersonalInfo().getLanguagePreference());
                editor1.commit();
                final JSONObject obj = new JSONObject(sharedPref.getString(PreferenceConstants.HEALTH_SYSTEM_PREFERENCES, "{}"));
                if (obj.length() > 0 && obj.optBoolean("additional_screen_applicable", false)){
                    mDashBoardEmailLl.setBackgroundColor(getResources().getColor(R.color.parentView_color));
                    mEmailConfirmationView.setVisibility(View.VISIBLE);
                    mEmailConfirmationIv.setVisibility(View.VISIBLE);
                    mEmailConfirmationTv.setClickable(false);
                    mEmailConfirmationTv.setText(obj.optString("footer_text"));
                    mEmailConfirmationTv.setTextColor(getResources().getColor(R.color.darkgreyTextColor));
                    mEmailConfirmationTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mDashBoardEmailLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWebView.setVisibility(View.VISIBLE);
                            mWebView.loadUrl(obj.optString("iframe_url"));
                            mWebView.getSettings().setLoadWithOverviewMode(true);
                            mWebView.getSettings().setUseWideViewPort(true);
                            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                            mWebView.getSettings().setBuiltInZoomControls(true);
                            mWebView.setOnKeyListener(new View.OnKeyListener() {

                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                                        mWebView.setVisibility(View.GONE);
                                    }
                                    return false;
                                }

                            });
                            isWebView = true;
                        }
                    });
                }
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
        } catch(Exception e){
            e.printStackTrace();
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


    /***
     * This method will be invoked to show the notification in dashboard only for oncall Pending visits.
     * @param appointment--Object carries appointment details.
     */

    public void showOnCallNotification(final OncallAppointment appointment){
        logD("Appointment", appointment.toString());
        final TextView firstTextView = (TextView) mNotificationView.findViewById(R.id.notification_first_text_view);
        final TextView secondTextView = (TextView) mNotificationView.findViewById(R.id.notification_second_text_view);
        firstTextView.setText("Your appointment has started.");
        secondTextView.setText("Tap here to enter");

        mNotificationView.setTag(appointment);
        mNotificationView.setVisibility(View.VISIBLE);
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (v.getTag() != null) {
                        Intent intent = new Intent(getActivity(), MDLiveStartVisit.class);
                        startActivity(intent);
                        MdliveUtils.startActivityAnimation(getActivity());
                    }
                } catch (Exception e) {

                }
            }
        });
    }






    public void showNotification(final Appointment appointment) {
        if (mNotificationView != null) {
            logD("Appointment", appointment.toString());

            final TextView firstTextView = (TextView) mNotificationView.findViewById(R.id.notification_first_text_view);
            final TextView secondTextView = (TextView) mNotificationView.findViewById(R.id.notification_second_text_view);

            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");


            /**
             * This is for instant appointment
             * */
            if ("Now".equalsIgnoreCase(time)) {
//                firstTextView.setText("Your appointment has started.");
//                secondTextView.setText("Tap here to enter");

                if(appointment.getApptType()!= null && appointment.getApptType().equalsIgnoreCase("phone"))
                {
                    if (mUserBasicInfo == null) {
                        mUserBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
                    }
                    mCustomerDefaultNumber = mUserBasicInfo.getPersonalInfo().getPhone();
                    if(PendingAppointment.readFromSharedPreference(getActivity())!=null && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments()!=null && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().size()>0
                            && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().get(0).getCustomerCallInNumber()!=null) {
                        mCustomerProvidedPhoneNumber = PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().get(0).getCustomerCallInNumber();

                    } else {
                        mCustomerProvidedPhoneNumber = mCustomerDefaultNumber;
                    }
                    mCustomerProvidedPhoneNumber = formatDualString(mCustomerProvidedPhoneNumber);

                    firstTextView.setText("The provider will call you shortly at \n"+ mCustomerProvidedPhoneNumber);

                    secondTextView.setVisibility(View.GONE);
                }else
                {
                    firstTextView.setText("Your appointment has started.");
                    secondTextView.setText("Tap here to enter");
                }

            } else {
                final int type = TimeZoneUtils.getRemainigTimeToAppointment(appointment.getInMilliseconds(), "", getActivity());

                 /*
                * Will return 0 if less than 10 minutes
                * Will return 1 if less than 24 hours
                * Will return 2 in other cases.
                * */
                    switch (type) {
                        // Ten minutes case
                        case 0 :
                            final SharedPreferences preferences = firstTextView.getContext().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                            final String timestampString = preferences.getString((MdliveUtils.getRemoteUserId(firstTextView.getContext()) + PreferenceConstants.SELECTED_TIMESTAMP), null);
                            if (timestampString != null) {
                                final long timestamp = Long.parseLong(timestampString);
                                firstTextView.setText("Your next appointment less than  " + TimeZoneUtils.getRemainigTimeToAppointmentString(timestamp, "", getActivity()) + " minute(s)");
                                secondTextView.setText("Click here to start Appointment.");
                            }
                            break;

                        default:
                            firstTextView.setText("Your next appointment is  " + TimeZoneUtils.convertMiliSeconedsToDayYearTimeString(appointment.getInMilliseconds(), getActivity()));
                            secondTextView.setText("Click here for details.");
                            break;
                    }
            }

            mNotificationView.setTag(appointment);
            mNotificationView.setVisibility(View.VISIBLE);
            mNotificationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (v.getTag() != null) {
                            final Appointment appo = (Appointment) v.getTag();
                            if (appo != null && mOnNotificationCliked != null) {
                                mOnNotificationCliked.onNotificationClicked(appo);
                            }
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

    public String formatDualString(String formatText) {
        boolean hasParenthesis = false;
        if (formatText.indexOf(")") > 0) {
            hasParenthesis = true;
        }
        formatText = formatText.replace("(", "");
        formatText = formatText.replace(")", "");
        formatText = formatText.replace(" ", "");
        if (formatText.length() > 10) {
            formatText = formatText.substring(0, formatText.length());
        }
        if (formatText.length() >= 7) {
            formatText = "(" + formatText.substring(0, 3) + ") " + formatText.substring(3, 6) + "-" + formatText.substring(6, formatText.length());
        } else if (formatText.length() >= 4) {
            formatText = "(" + formatText.substring(0, 3) + ") " + formatText.substring(3, formatText.length());
        } else if (formatText.length() == 3 && hasParenthesis) {
            formatText = "(" + formatText.substring(0, formatText.length()) + ")";
        }

        return formatText;
        //((TextView) findViewById(R.id.phoneNumber)).setText(MdliveUtils.formatDualString(formatText));

    }
}
