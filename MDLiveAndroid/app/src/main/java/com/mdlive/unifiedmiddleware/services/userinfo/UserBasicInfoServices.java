package com.mdlive.unifiedmiddleware.services.userinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class UserBasicInfoServices extends BaseServicesPlugin {
    private Context context;
    public UserBasicInfoServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);
        this.context = context;
    }

    public void getUserBasicInfoRequest(String memberId, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Log.d("Hello", "Selected User : " + memberId + ".");

            String url = AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_USER_INFO;
            if(!memberId.isEmpty()){
                //url.replace(":id","get_image_url");
                SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.DEPENDENT_USER_ID,memberId);
                editor.commit();

                Log.d("Hello", "Selected User : " + "From Pref : " + sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, "") + ".");
                Log.d("Hello", "Selected User : "  + "For Dependent.");
            }else{
                SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.DEPENDENT_USER_ID,null);
                editor.commit();

                Log.d("Hello", "Selected User : " + "For Parent.");
            }
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_USER_INFO,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}