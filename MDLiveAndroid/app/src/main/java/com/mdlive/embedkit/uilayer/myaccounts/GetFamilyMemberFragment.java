package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myaccounts.GetFamilyMemberInfoService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by venkataraman_r on 7/27/2015.
 */
public class GetFamilyMemberFragment extends MDLiveBaseFragment {
    private OnChildAdded mOnChildAdded;

    private ListView lv;
    private HashMap<String, ArrayList<String>> values;
    private ArrayList<String> nameList;
    private ArrayList<String> urlList;
    View header,footer;
    public static GetFamilyMemberFragment newInstance() {
        final GetFamilyMemberFragment fragment = new GetFamilyMemberFragment();
        return fragment;
    }

    public GetFamilyMemberFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnChildAdded = (OnChildAdded) activity;
        } catch (ClassCastException cce) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_familymember, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        values = new HashMap<String, ArrayList<String>>();

        lv = (ListView) view.findViewById(R.id.listView);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        footer = inflater.inflate(R.layout.add_family_footer, null);
        header = inflater.inflate(R.layout.add_family_header, null);
        CardView addFamilyMember1 = (CardView) footer.findViewById(R.id.addFamilyMember);

        TextView addFamilyMember = (TextView) view.findViewById(R.id.txt_add_FamilyMember);

        lv.addFooterView(footer);
        lv.addHeaderView(header);

        addFamilyMember1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(view.getContext());

                if (userBasicInfo.getRemainingFamilyMembersLimit() < 1) {
                    MdliveUtils.showAddChildExcededDialog(getActivity(), userBasicInfo.getAssistPhoneNumber());
                } else {
                    Intent changePhone = new Intent(getActivity(), MyAccountsHome.class);
                    changePhone.putExtra("Fragment_Name", "Add FAMILY MEMBER");
                    startActivityForResult(changePhone, 3);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getFamilyMemberInfoService();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnChildAdded = null;
    }

    public void getFamilyMemberInfoService() {
        showProgressDialog();

        nameList = new ArrayList<String>();
        urlList = new ArrayList<String>();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleFamilyMemberAddedSucessResponse(response);
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
        GetFamilyMemberInfoService service = new GetFamilyMemberInfoService(getActivity(), null);
        service.getFamilyMemberInfo(successCallBackListener, errorListener, null);
    }

    public void handleFamilyMemberAddedSucessResponse(JSONObject response) {
        hideProgressDialog();
        try {
            if((response.get("primary_user").toString())== "false")
            {
                lv.removeFooterView(footer);
            }

            JSONArray jsonarray = (JSONArray) response.get("dependant_users");

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject obj = jsonarray.getJSONObject(i);
                String url = obj.getString("image_url");
                String name = obj.getString("name");
                nameList.add(name);
                urlList.add(url);
            }

            lv.setAdapter(new GetFamilyMemberAdapter(getActivity(), nameList,urlList));

            if (mOnChildAdded != null) {
                mOnChildAdded.reloadNavigartion();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        switch (requestCode) {

            case 3:

                getFamilyMemberInfoService();
                break;
        }
    }

    public interface OnChildAdded {
        void reloadNavigartion();
    }
}
