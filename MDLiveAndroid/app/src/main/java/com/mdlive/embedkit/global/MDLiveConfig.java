package com.mdlive.embedkit.global;


import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

/**
 * All the configuration details are stored here. These information will be passed to the middleware \
 * layer by invoking the AppSpecificConfig class.
 *
 */
public class MDLiveConfig {

//    private static final String BASE_URL ="https://stage-rtl.mdlive.com/services";
    private static final String BASE_URL ="https://dev-members.mdlive.com/services";
    public static final String LOGIN_SERVICES = "/customer_logins";
    public static final String URL_USER_INFORMATION = "/customer/:id";
    public static final String URL_PROVIDER_TYPE = "/providers/provider_type_list";
    public static final String URL_FAMILY_MEMBER = "/customer/family_members";
    public static final String URL_CHOOSE_PROVIDER = "/providers/search_providers";
    public static final String URL_SEARCH_PROVIDER = "/support/provider_search_options";
    public static final String URL_FILTER_SEARCH = "/providers/filter_providers";
    public static final String URL_REASON_FOR_VISIT = "/support/chief_complaint_reasons";
    public static final String URL_ZIPCODE = "/providers/search_providers";
    public static final String URL_PROVIDER_DETAILS ="/providers/4?appointment_date=2015/1/20&appointment_type=3&located_in=FL";
    public static final String URL_LIFE_STYLE = "/life_style_conditions ";
    public static final String URL_PEDIATRIC_PROFILE = "/medical_histories/pediatric_profile";

    private static final String UPLOAD_MEDICALREPORT = "/customer/upload_document";
    private static final String DOWNLOAD_MEDICALREPORT = "/customer/records";
    private static final String DELETE_MEDICALREPORT = "/customer/delete_document";
    private static final String DOWNLOAD_MEDICAL_IMAGE = "/customer/download_document";



//    private static final String MDLIVE_API_KEY = "c9e63d9a77f17039c470";
// private static final String MDLIVE_SECRET_KEY = "b302e84f866a8730eb2";


    //dev Secret Keys..
    private static final String MDLIVE_SECRET_KEY = "843f117b0bf7368ed5d";
    private static final String MDLIVE_API_KEY = "a775f7e2ed1ce6cb313b";

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
    private static final String URL_MEDICAL_HISTORY_COMPLETION = "/medical_histories/history_completion";


    private static final String URL_MEDICAL_HISTORY_AGGREGATION = "/medical_histories/history_aggregation";
    private static final String URL_MEDICAL_CONDITIONS_LIST = "/medical_conditions";
    private static final String URL_MEDICAL_CONDITIONS_AUTOSUGGESTION = "/medical_conditions/search";
    private static final String URL_ALLERGY_LIST = "/allergies";
    private static final String URL_ALLERGY_AUTOSUGGESTION = "/allergies/search";

    private static final String URL_WAITING_ROOM = "/consultations/provider_status/";
    private static final String URL_WAITING_ROOM_VSEE = "/consultations/get_vsee_login/";
    private static final String URL_UPDATE_FEMALE_ATTRIBUTES = "/medical_histories/update_female_attributes";
    private static final String URL_CONFIRM_APPT = "/appointments/confirm_appointment";
    private static final String URL_UPDATE_PEDIATRIC = "/medical_histories/update_pediatric_profile";
    private static final String URL_GET_PEDIATRIC = "/medical_histories/pediatric_profile";

    private static final String USER_INFORMATION = "/customer/user_information";

    private static final String URL_GET_PROMOCODE= "/consultation_charges/get_promocode_details?promocode=";







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
        AppSpecificConfig.URL_PROVIDER_DETAILS = URL_PROVIDER_DETAILS;
        AppSpecificConfig.URL_SEARCH_PROVIDER = URL_SEARCH_PROVIDER;
        AppSpecificConfig.URL_FILTER_SEARCH = URL_FILTER_SEARCH;
        AppSpecificConfig.URL_REASON_FOR_VISIT = URL_REASON_FOR_VISIT;
        AppSpecificConfig.URL_PEDIATRIC_PROFILE = URL_PEDIATRIC_PROFILE;
        AppSpecificConfig.URL_ZIPCODE = URL_ZIPCODE;
        AppSpecificConfig.URL_LIFE_STYLE = URL_LIFE_STYLE;
        AppSpecificConfig.URL_PHARMACIES_CURRENT = URL_PHARMACIES_CURRENT;
        AppSpecificConfig.URL_PHARMACIES_CURRENT = URL_PHARMACIES_CURRENT;
        AppSpecificConfig.URL_PHARMACIES_SEARCH_LOCATION = URL_PHARMACIES_SEARCH_LOCATION;
        AppSpecificConfig.URL_PHARMACIES_UPDATE = URL_PHARMACIES_UPDATE;
        AppSpecificConfig.URL_PHARMACY_BY_NAME_SEARCH = URL_PHARMACY_BY_NAME_SEARCH;
        AppSpecificConfig.URL_MEDICATION_LIST = URL_MEDICATION_LIST;
        AppSpecificConfig.URL_ALLERGY_LIST = URL_ALLERGY_LIST;
        AppSpecificConfig.URL_ALLERGY_AUTOSUGGESTION = URL_ALLERGY_AUTOSUGGESTION;
        AppSpecificConfig.URL_MEDICATION_CREATE = URL_MEDICATION_CREATE;
        AppSpecificConfig.URL_MEDICATION_UPDATE = URL_MEDICATION_UPDATE;
        AppSpecificConfig.URL_MEDICATION_DELETE = URL_MEDICATION_DELETE;
        AppSpecificConfig.URL_MEDICATION_SEARCH = URL_MEDICATION_SEARCH;
        AppSpecificConfig.URL_MEDICAL_HISTORY_COMPLETION = URL_MEDICAL_HISTORY_COMPLETION;
        AppSpecificConfig.URL_MEDICAL_HISTORY_AGGREGATION = URL_MEDICAL_HISTORY_AGGREGATION;
        AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST = URL_MEDICAL_CONDITIONS_LIST;
        AppSpecificConfig.URL_MEDICAL_CONDITIONS_AUTOSUGGESTION = URL_MEDICAL_CONDITIONS_AUTOSUGGESTION;
        AppSpecificConfig.URL_WAITING_ROOM = URL_WAITING_ROOM;
        AppSpecificConfig.URL_WAITING_ROOM_VSEE = URL_WAITING_ROOM_VSEE;
        AppSpecificConfig.URL_UPDATE_FEMALE_ATTRIBUTES = URL_UPDATE_FEMALE_ATTRIBUTES;
        AppSpecificConfig.URL_CONFIRM_APPT = URL_CONFIRM_APPT;
        AppSpecificConfig.URL_UPDATE_PEDIATRIC = URL_UPDATE_PEDIATRIC;
        AppSpecificConfig.URL_GET_PEDIATRIC = URL_GET_PEDIATRIC;
        AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE = DOWNLOAD_MEDICAL_IMAGE;
        AppSpecificConfig.UPLOAD_MEDICALREPORT = UPLOAD_MEDICALREPORT;
        AppSpecificConfig.DOWNLOAD_MEDICALREPORT = DOWNLOAD_MEDICALREPORT;
        AppSpecificConfig.DELETE_MEDICALREPORT = DELETE_MEDICALREPORT;
        AppSpecificConfig.USER_INFORMATION = USER_INFORMATION;
        AppSpecificConfig.URL_GET_PROMOCODE = URL_GET_PROMOCODE;

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
