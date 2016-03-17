package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.login.adapter.DashBoardSpinnerAdapter;
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
import com.mdlive.unifiedmiddleware.services.myaccounts.GetProfileInfoService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by dhiman_da on 8/6/2015.
 */
public class MDLiveDashBoardFragment extends MDLiveBaseFragment {
    public static OnUserSelectionChanged mOnUserSelectionChanged;
    public static OnNotificationClicked mOnNotificationClicked;

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
                // Preventing  onItemSelection to get called
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

                if (mOnUserSelectionChanged != null) {
                    mOnUserSelectionChanged.onDependentSelected(selectedUser);
                    if(NotificationFragment.getInstance() != null) {
                        NotificationFragment.getInstance().reloadPendingAppointment();
                }
            }
            }
            // The Parent User Selected
            else {
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
            mOnNotificationClicked = (OnNotificationClicked) activity;
        } catch (ClassCastException cce) {
            logE("MDLiveDashBoardFragment", activity.getClass().getSimpleName() + ", should implement OnUserSelectionChanged");
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

        // Add the list of modules here
        HashMap<String, Integer> moduleMap = new HashMap<>();
        String[] modules = getActivity().getResources().getStringArray(R.array.left_navigation_modules);
        moduleMap.put(modules[0], R.id.mdliveAssist);
        moduleMap.put(modules[1], R.id.messageCenter);
        moduleMap.put(modules[2], R.id.mdliveSAV);
        moduleMap.put(modules[3], R.id.mdliveMyHealth);
        moduleMap.put(modules[4], R.id.symptomChecker);
        moduleMap.put(modules[5], R.id.myAccounts);

        for(int i=0; i<modules.length;i++){
            try {
               Class.forName(modules[i]);
            } catch (ClassNotFoundException e) {
                // Disable the module if the class is not found
                LinearLayout layout = (LinearLayout) view.findViewById(moduleMap.get(modules[i]));
                layout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getProfileInfoService();
    }

    /**
     * This method fetches the user basic info
     * @author  Jitendra Singh
     */
    private void getProfileInfoService() {

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleGetProfileInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                //We set FL in case of an error
                setDefaultState(getActivity().getString(R.string.mdl_fl));
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        GetProfileInfoService service = new GetProfileInfoService(getActivity(), null);
        service.getProfileInfo(successCallBackListener, errorListener, null);
    }

    public void handleGetProfileInfoSuccessResponse(JSONObject response) {
        try
        {
            JSONObject myProfile = response.getJSONObject("personal_info");
            String state;
            if (MdliveUtils.checkIsEmpty(myProfile.getString("state"))) {
                //We set FL in case of blank
                state = getActivity().getString(R.string.mdl_fl);
            } else {
                state = myProfile.getString("state");
            }
            setDefaultState(state);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void setDefaultState(String state)
    {
        if (getActivity() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("ADDRESS_CHANGE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getActivity().getString(R.string.mdl_user_address_state), state);
            editor.commit();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnUserSelectionChanged = null;
        mOnNotificationClicked = null;
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
                        mMessageCountTextView.setContentDescription(String.valueOf(userBasicInfo.getNotifications().getMessages()) + "unread Messages");
                        mMessageCountTextView.setVisibility(View.VISIBLE);
                    }
                }

                SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPref.edit();
                editor1.putString(PreferenceConstants.PREFFERED_LANGUAGE, userBasicInfo.getPersonalInfo().getLanguagePreference());
                editor1.commit();
                final JSONObject obj = new JSONObject(sharedPref.getString(PreferenceConstants.HEALTH_SYSTEM_PREFERENCES, "{}"));
                if (obj.length() > 0 && obj.optBoolean("additional_screen_applicable", false) && mUserBasicInfo.getPersonalInfo().getEmailConfirmed()){
                    mDashBoardEmailLl.setBackgroundColor(getResources().getColor(R.color.parentView_color));
                    mEmailConfirmationView.setVisibility(View.VISIBLE);
                    mEmailConfirmationIv.setVisibility(View.VISIBLE);
                    mEmailConfirmationTv.setClickable(false);
                    mEmailConfirmationTv.setText(obj.optString("footer_text"));
                    mEmailConfirmationTv.setTextColor(getResources().getColor(R.color.myTextPrimaryColor));
                    mEmailConfirmationTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mDashBoardEmailLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(),HealthSystemsActivity.class);
                            i.putExtra("URL", obj.optString("iframe_url"));
                            startActivity(i);
                        }
                    });
                }
                mSpinner.setOnItemSelectedListener(null);
                mSpinner.setAdapter(mAdapter);
                // Preventing  onItemSelection to get callied
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
        final TextView firstTextView = (TextView) mNotificationView.findViewById(R.id.notification_first_text_view);
        final TextView secondTextView = (TextView) mNotificationView.findViewById(R.id.notification_second_text_view);


        mNotificationView.setTag(appointment);
        mNotificationView.setVisibility(View.VISIBLE);
        Log.e("Appointment Dash",appointment.getApptType());

        if(appointment.getApptType().equalsIgnoreCase("video")){
            firstTextView.setText(getActivity().getString(R.string.mdl_your_appointmant_has_started));
            secondTextView.setText(getActivity().getString(R.string.mdl_tap_here_to_enter));
            mNotificationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (v.getTag() != null) {
                            try {
                                Class clazz = Class.forName(getString(R.string.mdl_mdlive_sav_module));
                                Intent intent = new Intent(getActivity(), clazz);
                                startActivity(intent);
                            } catch (ClassNotFoundException e){
                                /*Toast.makeText(getActivity(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        getString(R.string.mdl_mdlive_module_not_found),
                                        Snackbar.LENGTH_LONG).show();
                            }
                            MdliveUtils.startActivityAnimation(getActivity());
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }else{


            if(mUserBasicInfo!=null){
                mCustomerDefaultNumber = MdliveUtils.formatDualString(mUserBasicInfo.getPersonalInfo().getPhone());
            }else{
                mCustomerDefaultNumber="";
            }
            if (PendingAppointment.readFromSharedPreference(getActivity()) != null && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments() != null && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().size() > 0
                    && PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().get(0).getCustomerCallInNumber() != null) {
                mCustomerProvidedPhoneNumber = MdliveUtils.formatDualString(PendingAppointment.readFromSharedPreference(getActivity()).getOncallAppointments().get(0).getCustomerCallInNumber());

            }else{
                mCustomerProvidedPhoneNumber=mCustomerDefaultNumber;
            }

                firstTextView.setText(getActivity().getString(R.string.mdl_oncall_dashboard_phone_text, mCustomerProvidedPhoneNumber));
            secondTextView.setVisibility(View.GONE);
        }


    }






    public void showNotification(final Appointment appointment) {
        if (mNotificationView != null) {
            logD("Appointment", appointment.toString());

            final TextView firstTextView = (TextView) mNotificationView.findViewById(R.id.notification_first_text_view);
            final TextView secondTextView = (TextView) mNotificationView.findViewById(R.id.notification_second_text_view);

            try {
                    final int type = TimeZoneUtils.getRemainigTimeToAppointment(appointment.getInMilliseconds(), "", getActivity());

                 /*
                * Will return 0 if less than 10 minutes
                * Will return 1 if less than 24 hours
                * Will return 2 in other cases.
                * */
                    switch (type) {
                        // Ten minutes case
                        case 0:
                            final long now = System.currentTimeMillis();
                            final Calendar myTime = TimeZoneUtils.getCalendarWithOffset(getActivity());
                            myTime.setTimeInMillis(now);
                            Log.v("now 2", myTime.getTimeInMillis() + "");
                            final long difference = (appointment.getInMilliseconds() * 1000) - Calendar.getInstance().getTimeInMillis();
                            if(difference > 0){
                                String remainingMinute = Long.toString(TimeUnit.MILLISECONDS.toMinutes(difference));
                                firstTextView.setText(getString(R.string.mdl_appt_notification, remainingMinute));
                            }else{
                                firstTextView.setText(getActivity().getString(R.string.mdl_your_appointmant_has_started));
                                secondTextView.setText(getActivity().getString(R.string.mdl_tap_here_to_enter));
                            }

                            secondTextView.setText(getString(R.string.mdl_click_to_start));
                            break;

                        default:
                            firstTextView.setText(getString(R.string.mdl_next_appt) + " " + TimeZoneUtils.convertMiliSeconedsToDayYearTimeString(appointment.getInMilliseconds(), getActivity()));
                            secondTextView.setText(getString(R.string.mdl_click_to_detail));
                            break;
                    }
            }catch (Exception e){
                e.printStackTrace();
            }

            mNotificationView.setTag(appointment);
            mNotificationView.setVisibility(View.VISIBLE);
            mNotificationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (v.getTag() != null) {
                            final Appointment appo = (Appointment) v.getTag();
                            if (appo != null && mOnNotificationClicked != null) {
                                mOnNotificationClicked.onNotificationClicked(appo);
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

    public interface OnNotificationClicked {
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
