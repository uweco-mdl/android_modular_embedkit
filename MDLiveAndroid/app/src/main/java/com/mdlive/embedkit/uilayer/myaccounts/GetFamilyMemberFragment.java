package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
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
public class GetFamilyMemberFragment extends Fragment {

    Toolbar toolbar;
    private TextView toolbarTitle;
    ListView lv;
    private ProgressDialog pDialog;
    HashMap<String,ArrayList<String>> values;
    ArrayList<String> nameList;
    ArrayList<String> urlList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View addCreditCard = inflater.inflate(R.layout.fragment_get_familymember,null);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.family_member));

         values =new HashMap<String,ArrayList<String>>();
         nameList = new ArrayList<String>();
         urlList = new ArrayList<String>();

        lv=(ListView) addCreditCard.findViewById(R.id.listView);


        TextView addFamilyMember = (TextView)addCreditCard.findViewById(R.id.txt_add_FamilyMember);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        getFamilyMemberInfoService();

        addFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.tabcontent, AddFamilyMemberFragment.newInstance()).commit();

            }
        });

        return addCreditCard;

    }

    public void getFamilyMemberInfoService()
    {
        pDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handlegetCreditCardInfoSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };
        GetFamilyMemberInfoService service = new GetFamilyMemberInfoService(getActivity(), null);
        service.getFamilyMemberInfo(successCallBackListener, errorListener, null);
    }

    public void handlegetCreditCardInfoSuccessResponse(JSONObject response)
    {
        pDialog.dismiss();
        try {
            Log.i("response",response.toString());
            JSONArray jsonarray = (JSONArray) response.get("dependant_users");

            for(int i=0; i<jsonarray.length(); i++) {
                JSONObject obj = jsonarray.getJSONObject(i);
                String url = obj.getString("image_url");
                String name = obj.getString("name");
                nameList.add(name);
                urlList.add(url);
            }
            values.put("NAME",nameList);
            values.put("URL",urlList);
            lv.setAdapter(new GetFamilyMemberAdapter(getActivity(), values));
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}
