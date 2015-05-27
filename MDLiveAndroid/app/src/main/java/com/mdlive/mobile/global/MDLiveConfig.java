package com.mdlive.mobile.global;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

/**
 * All the configuration details are stored here. These information will be passed to the middleware \
 * layer by invoking the AppSpecificConfig class.
 *
 */
public class MDLiveConfig {

    private static final String BASE_URL ="https://stage-rtl.mdlive.com/services";
    public static final String LOGIN_SERVICES = "/customer_logins";
    public static final String URL_USER_INFORMATION = "/customer/:id";
    public static final String URL_PROVIDER_TYPE = "/providers/provider_type_list";
    public static final String URL_FAMILY_MEMBER = "/customer/family_members";
    public static final String URL_CHOOSE_PROVIDER = "/providers/search_providers";
    public static final String URL_REASON_FOR_VISIT = "/support/chief_complaint_reasons";
    public static final String URL_ZIPCODE = "/providers/search_providers";
    public static final String URL_LIFE_STYLE = "/life_style_conditions ";
    private static final String MDLIVE_API_KEY = "c9e63d9a77f17039c470";
    private static final String MDLIVE_SECRET_KEY = "b302e84f866a8730eb2";
    public static final String DEFAULT_USER_ID = "MobileUser";

    public static final String URL_PHARMACIES_CURRENT = "/pharmacies/current"; // Need additional header
    public static final String URL_PHARMACIES_SEARCH_LOCATION = "/pharmacies/search";
    public static final String URL_PHARMACIES_UPDATE = "/pharmacies/update"; // Need post body
    public static final String URL_PHARMACY_BY_NAME_SEARCH = "/pharmacies/suggest_pharmacy";

    public static final String URL_MEDICATION_LIST = "/medications";
    public static final String URL_MEDICATION_CREATE = "/medications"; // need post body
    public static final String URL_MEDICATION_UPDATE = "/medications"; // need additional header
    public static final String URL_MEDICATION_DELETE = "/medications";
    public static final String URL_MEDICATION_SEARCH = "/medications/search"; // need post body




    /*Dev Services*/
   /*
    private static final String BASE_URL ="https://dev-sso.mdlive.com";
    private static final String LOGIN_SERVICES = "/services/customer_logins";
    public static final String URL_USER_INFORMATION = "/services/customer/:id";
    public static final String URL_PROVIDER_TYPE = "/services/providers/provider_type_list";
    public static final String URL_FAMILY_MEMBER = "/services/customer/family_members";
    public static final String URL_CHOOSE_PROVIDER = "/services/providers/search_providers";
    public static final String URL_REASON_FOR_VISIT = "/services/support/chief_complaint_reasons";
    public static final String URL_LIFE_STYLE = "/services/life_style_conditions";
    public static final String URL_ZIPCODE = "/services/providers/search_providers";
    private static final String MDLIVE_API_KEY = "c9e63d9a77f17039c470";
    private static final String MDLIVE_SECRET_KEY = "b302e84f866a8730eb2";
    private static final String DEFAULT_USER_ID = "MobileUser";
    private static final String URL_PHARMACIES_CURRENT = "/services/pharmacies/current";
    public static final String URL_PHARMACIES_SEARCH_LOCATION = "/services/pharmacies/search";
    */

    public static void setData(){

        AppSpecificConfig.BASE_URL = BASE_URL;
        AppSpecificConfig.LOGIN_SERVICES = LOGIN_SERVICES;
        AppSpecificConfig.API_KEY = MDLIVE_API_KEY;
        AppSpecificConfig.SECRET_KEY = MDLIVE_SECRET_KEY;
        AppSpecificConfig.DEFAULT_USER_ID = DEFAULT_USER_ID;
        AppSpecificConfig.URL_USER_INFORMATION = URL_USER_INFORMATION;
        AppSpecificConfig.URL_FAMILY_MEMBER = URL_FAMILY_MEMBER;
        AppSpecificConfig.URL_PROVIDER_TYPE = URL_PROVIDER_TYPE;
        AppSpecificConfig.URL_CHOOSE_PROVIDER = URL_CHOOSE_PROVIDER;
        AppSpecificConfig.URL_REASON_FOR_VISIT = URL_REASON_FOR_VISIT;
        AppSpecificConfig.URL_ZIPCODE = URL_ZIPCODE;
        AppSpecificConfig.URL_LIFE_STYLE = URL_LIFE_STYLE;
        AppSpecificConfig.URL_PHARMACIES_CURRENT = URL_PHARMACIES_CURRENT;
        AppSpecificConfig.URL_PHARMACIES_CURRENT = URL_PHARMACIES_CURRENT;
        AppSpecificConfig.URL_PHARMACIES_SEARCH_LOCATION = URL_PHARMACIES_SEARCH_LOCATION;
        AppSpecificConfig.URL_PHARMACIES_UPDATE = URL_PHARMACIES_UPDATE;
        AppSpecificConfig.URL_PHARMACY_BY_NAME_SEARCH = URL_PHARMACY_BY_NAME_SEARCH;
        AppSpecificConfig.URL_MEDICATION_LIST = URL_MEDICATION_LIST;
        AppSpecificConfig.URL_MEDICATION_CREATE = URL_MEDICATION_CREATE;
        AppSpecificConfig.URL_MEDICATION_UPDATE = URL_MEDICATION_UPDATE;
        AppSpecificConfig.URL_MEDICATION_DELETE = URL_MEDICATION_DELETE;
        AppSpecificConfig.URL_MEDICATION_SEARCH = URL_MEDICATION_SEARCH;

    }
    /*
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
    }*/
}
