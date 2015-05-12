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
    public static final String URL_USER_INFORMATION = "/customer/:id";
    public static final String URL_PROVIDER_TYPE = "/providers/provider_type_list";
public static final String URL_FAMILY_MEMBER = "/customer/family_members";

    private static final String MDLIVE_API_KEY = "c9e63d9a77f17039c470";
    private static final String MDLIVE_SECRET_KEY = "b302e84f866a8730eb2";
    private static final String DEFAULT_USER_ID = "MobileUser";

    public static void setData(){

        AppSpecificConfig.BASE_URL = BASE_URL;
        AppSpecificConfig.LOGIN_SERVICES = LOGIN_SERVICES;
        AppSpecificConfig.API_KEY = MDLIVE_API_KEY;
        AppSpecificConfig.SECRET_KEY = MDLIVE_SECRET_KEY;
        AppSpecificConfig.DEFAULT_USER_ID = DEFAULT_USER_ID;
        AppSpecificConfig.URL_USER_INFORMATION = URL_USER_INFORMATION;
        AppSpecificConfig.URL_FAMILY_MEMBER = URL_FAMILY_MEMBER;
        AppSpecificConfig.URL_PROVIDER_TYPE = URL_PROVIDER_TYPE;
    }
}
