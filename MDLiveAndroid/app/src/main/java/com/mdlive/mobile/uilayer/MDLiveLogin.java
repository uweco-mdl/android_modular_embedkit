package com.mdlive.mobile.uilayer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.mdlive.unifiedmiddleware.parentclasses.activity.UMWLogin;
import com.mdlive.unifiedmiddleware.parentclasses.bean.activity.LoginActivityBean;
import com.mdlive.mobile.R;

/**
 *
 * The wrapper class for Login Activity. The Login layout is set here. The necessary logic inputs
 * are passed to UMWLogin using setData() method.
 *
 */
public class MDLiveLogin extends UMWLogin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        LoginActivityBean loginBean = new LoginActivityBean();
        loginBean.setUsernameEt((EditText) findViewById(R.id.UserNameEt));
        loginBean.setPasswordEt((EditText) findViewById(R.id.PasswordEt));
        loginBean.setLoginBtn((Button) findViewById(R.id.LoginBtn));
        loginBean.setHomeActivity(MDLiveDashboard.class);
        setData(loginBean);
        super.onCreate(savedInstanceState);
    }
}
