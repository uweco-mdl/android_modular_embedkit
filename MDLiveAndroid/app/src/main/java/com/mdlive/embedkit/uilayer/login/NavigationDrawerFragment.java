package com.mdlive.embedkit.uilayer.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PharmacyDetails;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Security;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by venkataraman_r on 7/16/2015.
 */
public class NavigationDrawerFragment extends MDLiveBaseFragment {
    private static final String USER_PASSED_FROM_ACTIVITY = "user_passed";

    private NavigationDrawerCallbacks mCallbacks;
    private OnUserInformationLoaded mOnUserInformationLoaded;

    private LinearLayout mSelectedUserLinearLayout;
    private LinearLayout mAllUserLinearLayout;

    private ListView mDrawerListView;
    private int mCurrentSelectedPosition = 0;

    private UserBasicInfo mUserBasicInfo;

    private boolean mIsExpanded = false;
    private float mDrawerWidth;
    private float mScrollHeight;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance() {
        final NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        return fragment;
    }

    public static NavigationDrawerFragment newInstance(final User user) {
        final Bundle args = new Bundle();
        args.putParcelable(USER_PASSED_FROM_ACTIVITY, user);

        final NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
            mOnUserInformationLoaded = (OnUserInformationLoaded) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks, OnUserInformationLoaded.");
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

        mSelectedUserLinearLayout = (LinearLayout) view.findViewById(R.id.navigation_selected_user);
        mAllUserLinearLayout = (LinearLayout) view.findViewById(R.id.navigation_user_list);
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

        mDrawerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mDrawerListView.getViewTreeObserver().removeOnPreDrawListener(this);
                mDrawerWidth = mDrawerListView.getWidth();
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null && getActivity() instanceof MDLiveDashboardActivity) {
            final User user = getArguments().getParcelable(USER_PASSED_FROM_ACTIVITY);

            if (user != null && user.mMode == User.MODE_DEPENDENT) {
                loadDependendUserDetails(user);
            } else {
                loadUserInformationDetails();
            }
        } else {
            mUserBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
            updateList();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = null;
        mOnUserInformationLoaded = null;
    }

    private void selectItem(int position) {
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
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
    public void loadUserInformationDetails() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

                /* Security JSON we need to read again, because of the web service issue..
                * We are excluding the security tag to be parsed by GSON,
                * then we are manually adding the Security JSON again
                * */
                final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                mUserBasicInfo = gson.fromJson(response.toString().trim(), UserBasicInfo.class);
                mUserBasicInfo.getPersonalInfo().setSecurity(Security.fromJSON(response.toString().trim()));
                mUserBasicInfo.getNotifications().setPharmacyDetails(PharmacyDetails.fromJSON(response.toString().trim()));
                try {
                    mUserBasicInfo.setHealthLastUpdate(response.getLong("health_last_update"));
                } catch (JSONException e) {
                    mUserBasicInfo.setHealthLastUpdate(-1l);
                }

                mUserBasicInfo.saveToSharedPreference(getActivity());

                updateList();
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

    /**
     * makes the /customer/user_information?include_dependent_appointments=true call to get the User information.
     *
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     *
     * After getting the uniqueid save it to shared preference.
     */
    public void loadDependendUserDetails(final User user) {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();

                /* Security JSON we need to read again, because of the web service issue..
                * We are excluding the security tag to be parsed by GSON,
                * then we are manually adding the Security JSON again
                * */
                final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                mUserBasicInfo = gson.fromJson(response.toString().trim(), UserBasicInfo.class);
                mUserBasicInfo.getPersonalInfo().setSecurity(Security.fromJSON(response.toString().trim()));
                mUserBasicInfo.getNotifications().setPharmacyDetails(PharmacyDetails.fromJSON(response.toString().trim()));
                try {
                    mUserBasicInfo.setHealthLastUpdate(response.getLong("health_last_update"));
                } catch (JSONException e) {
                    mUserBasicInfo.setHealthLastUpdate(-1l);
                }

                mUserBasicInfo.saveToSharedPreference(getActivity());

                updateList();
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
        services.getUserBasicInfoRequest(user.mId, successCallBackListener, errorListener);
    }

    private void updateList() {
        if (mOnUserInformationLoaded != null) {
            mOnUserInformationLoaded.sendUserInformation(mUserBasicInfo);
        }

        if (mSelectedUserLinearLayout != null) {
            List<User> users = null;

            if (mUserBasicInfo.getPrimaryUser()) {
                users = UserBasicInfo.getUsersAsPrimaryUser(getActivity());
            } else {
                users = UserBasicInfo.getUsersAsDependentUser(getActivity());
            }

            if (users.size() > 0) {
                final LayoutInflater inflater = LayoutInflater.from(mSelectedUserLinearLayout.getContext());
                mSelectedUserLinearLayout.removeAllViews();
                mAllUserLinearLayout.removeAllViews();

                for (int i = 0; i < users.size(); i++) {
                    final int position = i;
                    final User user = users.get(position);
                    final View view = inflater.inflate(R.layout.drawer_user_row, mSelectedUserLinearLayout, false);
                    view.setTag(users.get(i));

                    if (i == 0) {
                        mSelectedUserLinearLayout.addView(view);
                        mSelectedUserLinearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                animateWithLoad(user, false);
                            }
                        });
                    } else {
                        logD("Dependent Users", "" + user.mMode + ", " + user.mName);
                        mAllUserLinearLayout.addView(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // If Add child clicked
                                if (User.MODE_ADD_CHILD == user.mMode || StringConstants.ADD_CHILD.equalsIgnoreCase(user.mName)) {
                                    if (mOnUserInformationLoaded != null) {
                                        mOnUserInformationLoaded.onAddChildSelectedFromDrawer(user,
                                                mUserBasicInfo.getDependantUsers() == null ? 0 : mUserBasicInfo.getDependantUsers().size());
                                        return;
                                    }
                                }

                                animateWithLoad(user, true);
                            }
                        });
                    }

                    ((TextView) view.findViewById(R.id.drawer_user_row_text_view)).setText(users.get(i).mName);
                    if (user.mMode == User.MODE_ADD_CHILD || StringConstants.ADD_CHILD.equalsIgnoreCase(user.mName)) {
                        ((CircularNetworkImageView) view.findViewById(R.id.drawer_user_row_circular_image_view)).setImageResource(R.drawable.add_child);
                    } else {
                        ((CircularNetworkImageView) view.findViewById(R.id.drawer_user_row_circular_image_view)).setImageUrl(users.get(i).mImageUrl, ApplicationController.getInstance().getImageLoader(view.getContext()));
                    }
                }

                mAllUserLinearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mAllUserLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        logD("Height", "" + mScrollHeight);
                        mScrollHeight = mAllUserLinearLayout.getHeight();
                        return false;
                    }
                });

                mAllUserLinearLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofFloat(mAllUserLinearLayout, "translationY", -mAllUserLinearLayout.getHeight()).setDuration(0).start();
                    }
                }, 100);
            }
        }
    }

    private void animateWithLoad(final User user, final boolean load) {
        float width = 0f;
        float height = 0f;

        if (mIsExpanded) {
            width = 0;
            height = -mScrollHeight;
        } else {
            width = -mDrawerWidth;
            height = 0;
        }

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mDrawerListView, "translationX", width);
        animator1.setRepeatCount(0);
        animator1.setDuration(300);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mAllUserLinearLayout, "translationY", height);
        animator2.setRepeatCount(0);
        animator2.setDuration(300);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2);
        //set.play(animator1).before(animator2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsExpanded = !mIsExpanded;

                ((TextView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_text_view)).setText(user.mName);
                ((CircularNetworkImageView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_circular_image_view)).setImageUrl(user.mImageUrl, ApplicationController.getInstance().getImageLoader(mSelectedUserLinearLayout.getContext()));
                mSelectedUserLinearLayout.setTag(user);

                if (load) {
                    if (getActivity() != null && getActivity() instanceof MDLiveDashboardActivity) {
                        if (user.mMode == User.MODE_PRIMARY) {
                            loadUserInformationDetails();
                        } else {
                            loadDependendUserDetails(user);
                        }
                    } else {
                        if (mOnUserInformationLoaded != null) {
                            mOnUserInformationLoaded.reloadApplicationForUser(user);
                        }
                    }
                } else {
                    // No need to load new data
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
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

    public interface OnUserInformationLoaded {
        void sendUserInformation(UserBasicInfo userBasicInfo);
        void onAddChildSelectedFromDrawer(final User user, final int dependentUserSize);
        void reloadApplicationForUser(final User user);
    }
}