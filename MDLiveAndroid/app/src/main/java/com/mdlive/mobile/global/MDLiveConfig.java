package com.mdlive.mobile.global;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

/**
 * All the configuration details are stored here. These information will be passed to the middleware \
 * layer by invoking the AppSpecificConfig class.
 *
 */
public class MDLiveConfig {
    private static final String BASE_URL = "https://stage-members.mdlive.com/services";
    private static final String LOGIN_SERVICES = "/customer_logins";
    private static final String MDLIVE_API_KEY = "c9e63d9a77f17039c470";
    private static final String MDLIVE_SECRET_KEY = "b302e84f866a8730eb2";
    private static final String DEFAULT_USER_ID = "MobileUser";

    public static void setData(){
        AppSpecificConfig config = AppSpecificConfig.getInstance();
        config.BASE_URL = BASE_URL;
        config.LOGIN_SERVICES = LOGIN_SERVICES;
        config.API_KEY = MDLIVE_API_KEY;
        config.SECRET_KEY = MDLIVE_SECRET_KEY;
        config.DEFAULT_USER_ID = DEFAULT_USER_ID;
    }
}
