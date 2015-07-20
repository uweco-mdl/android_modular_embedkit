package com.mdlive.embedkit.global;


import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

/**
 * All the configuration details are stored here. These information will be passed to the middleware \
 * layer by invoking the AppSpecificConfig class.
 *
 */
public class MDLiveConfig {

    public static final String SSO_SERVICE = "/customer_logins/embed_kit";
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
    private static final String DOWNLOAD_MEDICALREPORT = "/customer/records?images_only=true";
    private static final String DELETE_MEDICALREPORT = "/customer/delete_document";
    private static final String DOWNLOAD_MEDICAL_IMAGE = "/customer/download_document";

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
    private static final String URL_USER_INFO= "/customer/user_information";
    private static final String URL_PENDING_APPOINTMENT= "/appointments";
    private static final String URL_BILLING_UPDATE= "/billing_informations/1";

    private static final String URL_GET_LASTDATE_MEDICAL_HISTORY = "/medical_histories/medical_history_completion";
    private static final String URL_UPDATE_MEDICAL_HISTORY= "/medical_histories/update_medical_history";
    private static final String URL_RATINGS = "/ratings";
    private static final String URL_ZERO_INSURANCE = "/customer/check_eligibility";

    public static void setData(int currentEnvironment){
        switch (currentEnvironment){
            case 1:
                // dev environment
                AppSpecificConfig.BASE_URL = "https://dev-members.mdlive.com/services";
                AppSpecificConfig.API_KEY = "843f117b0bf7368ed5d";
                AppSpecificConfig.SECRET_KEY = "a775f7e2ed1ce6cb313b";
                break;
            case 2:
                // QA environment
                AppSpecificConfig.BASE_URL = "https://pluto-members.mdtestsite.net/services";
                AppSpecificConfig.API_KEY = "b74d0fb9a114904c009b";
                AppSpecificConfig.SECRET_KEY = "89c8d3ea88501e8e62a";
                break;
            case 3:
                // stage environment
                AppSpecificConfig.BASE_URL = "https://stage-rtl.mdlive.com/services";
                AppSpecificConfig.API_KEY = "c9e63d9a77f17039c470";
                AppSpecificConfig.SECRET_KEY = "b302e84f866a8730eb2";
//                AppSpecificConfig.BASE_URL = "https://pluto-members.mdtestsite.net/services";
//                AppSpecificConfig.API_KEY = "b74d0fb9a114904c009b";
//                AppSpecificConfig.SECRET_KEY = "89c8d3ea88501e8e62a";
                break;
            case 4:
                // Production environment
                AppSpecificConfig.BASE_URL = "https://rtl.mdlive.com/services";        //  https://rtl.mdlive.com";
                AppSpecificConfig.API_KEY = "";
                AppSpecificConfig.SECRET_KEY = "";
                break;
            default:
                AppSpecificConfig.BASE_URL = "https://stage-rtl.mdlive.com/services";
                AppSpecificConfig.API_KEY = "c9e63d9a77f17039c470";
                AppSpecificConfig.SECRET_KEY = "b302e84f866a8730eb2";
        }

        AppSpecificConfig.LOGIN_SERVICES = LOGIN_SERVICES;
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
        AppSpecificConfig.URL_GET_LASTDATE_MEDICAL_HISTORY = URL_GET_LASTDATE_MEDICAL_HISTORY;
        AppSpecificConfig.URL_USER_INFO = URL_USER_INFO;
        AppSpecificConfig.URL_PENDING_APPOINTMENT = URL_PENDING_APPOINTMENT;
        AppSpecificConfig.URL_BILLING_UPDATE = URL_BILLING_UPDATE;
        AppSpecificConfig.URL_UPDATE_MEDICAL_HISTORY = URL_UPDATE_MEDICAL_HISTORY;
        AppSpecificConfig.URL_RATINGS = URL_RATINGS;
        AppSpecificConfig.URL_ZERO_INSURANCE = URL_ZERO_INSURANCE;
        AppSpecificConfig.SSO_SERVICE = SSO_SERVICE;
    }
}
