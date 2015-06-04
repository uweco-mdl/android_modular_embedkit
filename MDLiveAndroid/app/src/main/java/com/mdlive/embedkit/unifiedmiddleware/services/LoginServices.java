package com.mdlive.embedkit.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.mdlive.embedkit.unifiedmiddleware.bean.request.Login;
import com.mdlive.embedkit.unifiedmiddleware.bean.request.LoginBean;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by Unnikrishnan B on 04/04/15.
 */
public class LoginServices extends BaseServicesPlugin {

    public LoginServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }

    /**
     *
     * @param email
     * @param password
     */
    public void doLoginRequest(String email,String password, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        LoginBean loginBean = new LoginBean();
        loginBean.setLogin(new Login(email,password));
        try {
            Gson gson = new Gson();
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.LOGIN_SERVICES, gson.toJson(loginBean), responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
