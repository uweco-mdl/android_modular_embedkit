package com.mdlive.embedkit.uilayer.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
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
import java.util.HashMap;
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

    private OnUserChangedInGetStarted mOnUserChangedInGetStarted;
    private ArrayList<String> mDrawerArray;

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

        try {
            mOnUserChangedInGetStarted = (OnUserChangedInGetStarted) activity;
        } catch (ClassCastException cce) {

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
        String[] navStrings;
        if(MDLiveConfig.IS_SSO)
            navStrings = getActivity().getResources().getStringArray(R.array.left_navigation_items_sso);
        else
            navStrings = getActivity().getResources().getStringArray(R.array.left_navigation_items);

        TypedArray imgs = getResources().obtainTypedArray(R.array.left_navigation_items_image);
        ArrayList<Drawable> navImages = new ArrayList<>();

        HashMap<String, Boolean> stringMap = new HashMap<>();
        boolean SHOW_THE_ICON = true,
                HIDE_THE_ICON = false;
        for (int i = 0; i<navStrings.length; i++){
            stringMap.put(navStrings[i], SHOW_THE_ICON);
        }
        // Add the list of modules here
        HashMap<String, String> moduleMap = new HashMap<>();
        String[] modules = getActivity().getResources().getStringArray(R.array.left_navigation_modules);
        moduleMap.put(getString(R.string.mdl_mdlive_assist), modules[0]);
        moduleMap.put(getString(R.string.mdl_message_center), modules[1]);
        moduleMap.put(getString(R.string.mdl_see_a_doctor_now), modules[2]);
        moduleMap.put(getString(R.string.mdl_my_health), modules[3]);
        moduleMap.put(getString(R.string.mdl_symptom_checker), modules[4]);
        moduleMap.put(getString(R.string.mdl_my_accounts), modules[5]);

        if(!MDLiveConfig.IS_SSO){
            for (int i = 0; i < navStrings.length; i++) {
                try {
                    String module = moduleMap.get(navStrings[i]);
                    if (module != null) {
                        Class.forName(module);
                    }
                } catch (ClassNotFoundException e) {
                    // Feature is remove. Set the flag to remove the associated icon later
                    drawerItems.remove(i);
                    stringMap.put(navStrings[i], HIDE_THE_ICON);
                }
            }
        }

        // Add only the drawables that have the corresponding strings in the menu
        for(int i = 0; i<navStrings.length; i++) {
            if(stringMap.get(navStrings[i]) == SHOW_THE_ICON) {
                navImages.add(imgs.getDrawable(i));
            }
        }

        mDrawerArray = drawerItems;
        mDrawerListView.setAdapter(new DrawerAdapter(this.getActivity(), drawerItems, navImages));
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null && (getActivity() instanceof MDLiveDashboardActivity)) {
            final User user = getArguments().getParcelable(USER_PASSED_FROM_ACTIVITY);
            if (user != null && user.mMode == User.MODE_DEPENDENT) {
                loadDependendUserDetails(user, true);
            } else {
                loadUserInformationDetails(true);
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
        mOnUserChangedInGetStarted = null;
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
    public void loadUserInformationDetails(final boolean showProgress) {
        /* Clears the Selected User Preference*, for safety */
        //User.clearSelectedUser(getActivity());
        if (showProgress) {
            showProgressDialog();
        }

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                User.clearUser(getActivity());

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

                mUserBasicInfo.saveToSharedPreference(getActivity(), response.toString().trim());

                updateList();

                if (showProgress) {
                    hideProgressDialog();
                }
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (showProgress) {
                    hideProgressDialog();
                }
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
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
    public void loadDependendUserDetails(final User user, final boolean showProgress) {
        if (showProgress) {
            showProgressDialog();
        }

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                user.saveSelectedUser(getActivity());

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

                mUserBasicInfo.saveToSharedPreference(getActivity(), response.toString().trim());
                updateList();

                if (showProgress) {
                    hideProgressDialog();
                }
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (showProgress) {
                    hideProgressDialog();
                }

                try {
                    //MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);

                    MdliveUtils.handelVolleyErrorResponseForDependentChild(getActivity(), error, getProgressDialog(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //loadUserInformationDetails(true);

                        }
                    });

                    if (mOnUserInformationLoaded != null) {
                        mOnUserInformationLoaded.setPrimaryUserSelected();
                    }
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

        if (getActivity() == null) {
            return;
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
                    View view = null;

                    if (i == 0) {
                        view = inflater.inflate(R.layout.drawer_user_row, mSelectedUserLinearLayout, false);
                        view.setTag(users.get(i));

                        mSelectedUserLinearLayout.addView(view);
                        mSelectedUserLinearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                animateWithLoad(user, false);
                            }
                        });
                    } else {
                        if (User.MODE_ADD_CHILD == user.mMode || StringConstants.ADD_CHILD.equalsIgnoreCase(user.mName)) {
                            view = inflater.inflate(R.layout.drawer_user_row_add_child, mSelectedUserLinearLayout, false);
                            view.setTag(users.get(i));
                        } else {
                            view = inflater.inflate(R.layout.drawer_user_row_middle, mSelectedUserLinearLayout, false);
                            view.setTag(users.get(i));
                        }

                        logD("Dependent Users", "" + user.mMode + ", " + user.mName);
                        view.findViewById(R.id.drawer_user_row_down_image_view).setVisibility(View.GONE);
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

                    setMaxWidthForLeftText(mSelectedUserLinearLayout,
                            ((ImageView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_circular_image_view)),
                            ((TextView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_text_view)),
                            mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_down_image_view));
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
                if (mIsExpanded) {
                    ((ImageView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_down_image_view)).setImageResource(R.drawable.arrow_up_black);
                } else {
                    ((ImageView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_down_image_view)).setImageResource(R.drawable.arrow_down_black);
                }
                mSelectedUserLinearLayout.setTag(user);

                if (load) {
                    if (getActivity() != null && getActivity() instanceof MDLiveDashboardActivity) {
                        if (user.mMode == User.MODE_PRIMARY) {
                            loadUserInformationDetails(true);
                        } else {
                            loadDependendUserDetails(user, true);
                        }
                    } else {
                        if (mOnUserInformationLoaded != null) {
                            mOnUserInformationLoaded.reloadApplicationForUser(user);
                        }
                    }
                } else {
                    // No need to load new data
                }

                setMaxWidthForLeftText(mSelectedUserLinearLayout,
                        ((ImageView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_circular_image_view)),
                        ((TextView) mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_text_view)),
                        mSelectedUserLinearLayout.findViewById(R.id.drawer_user_row_down_image_view));
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

    public void onUserChangedInGetStarted() {
        mUserBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
        updateList();
    }

    public void reload() {
        mUserBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());

        if (mUserBasicInfo != null && mUserBasicInfo.getPrimaryUser()) {
            loadUserInformationDetails(true);
        } else {
            final User user = User.getSelectedUser(getActivity());
            if (user != null) {
                loadDependendUserDetails(user, true);
            }
        }
    }

    /**
     *  This method is used to shrink pharmacy store name if exceeds screen display
     *
     *  While pharmacy name is shrinking, then there will not be any changes on distance text.
     */
    public void setMaxWidthForLeftText(final View parentView, final ImageView imageView, final TextView leftTextView,
                                       final View rightTextView) {
        parentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int rWidth, iWidth, aWidth, bWidth, iLeftMargin, iRightMargin;
                rWidth = parentView.getWidth();
                iWidth = imageView.getWidth();
                leftTextView.measure(0, 0);
                aWidth = leftTextView.getMeasuredWidth();
                rightTextView.measure(0, 0);
                bWidth = rightTextView.getMeasuredWidth();

                int aMarginEnd = 0, bMarginStart = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    aMarginEnd = (int) (((LinearLayout.LayoutParams)leftTextView.getLayoutParams()).getMarginEnd() * leftTextView.getResources().getDisplayMetrics().density);
                    bMarginStart = (int) (((LinearLayout.LayoutParams)rightTextView.getLayoutParams()).getMarginStart() * leftTextView.getResources().getDisplayMetrics().density);
                    iLeftMargin = (int) (((LinearLayout.LayoutParams)imageView.getLayoutParams()).getMarginStart() * leftTextView.getResources().getDisplayMetrics().density);
                    iRightMargin = (int) (((LinearLayout.LayoutParams)imageView.getLayoutParams()).getMarginEnd() * leftTextView.getResources().getDisplayMetrics().density);
                } else {
                    aMarginEnd = 10;
                    bMarginStart = 10;
                    iLeftMargin = 10;
                    iRightMargin = 10;
                }
                leftTextView.setMaxWidth(rWidth - (bWidth + aMarginEnd + bMarginStart + iWidth + iLeftMargin + iRightMargin));
                leftTextView.invalidate();
                rightTextView.invalidate();
                parentView.invalidate();
            }
        }, 50);
    }

    public ArrayList<String> getDrawerList() {
        return mDrawerArray;
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
        void setPrimaryUserSelected();
    }

    public interface OnUserChangedInGetStarted {
        void onUserChangedInGetStarted();
    }
}