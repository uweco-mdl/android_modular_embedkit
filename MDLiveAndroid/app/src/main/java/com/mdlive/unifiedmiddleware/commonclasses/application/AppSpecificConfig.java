package com.mdlive.unifiedmiddleware.commonclasses.application;

/**
 *
 * Singleton class providing app-specific configurations. This includes the API keys, Base URLs, etc.
 *
 */
public class AppSpecificConfig {

    public static String WEB_URL;
    public static String BASE_URL;
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String URL_CREATE_PIN;
    public static String SSO_SERVICE = "/customer_logins/embed_kit";
    public static String LOGIN_SERVICES = "/customer_logins";
    public static String UPGRADE_SERVICES = "/support/get_latest_app_version?";
    public static String PUSH_REGISTRATION_SERVICE = "/mobile_device_registrations";
    public static String DEEPLINK_SERVICE = "/support/deeplink_affiliation_settings?device_id=";
    public static String EMAIL_CONFIRMATION;
    public static String URL_USER_INFORMATION = "/customer/:id";
    public static String URL_PROVIDER_TYPE = "/providers/provider_type_list";
    public static String URL_FAMILY_MEMBER = "/customer/family_members";
    public static String URL_CHOOSE_PROVIDER = "/providers/search_providers";
    public static String URL_SEARCH_PROVIDER = "/support/provider_search_options";
    public static String URL_FILTER_SEARCH = "/providers/filter_providers";
    public static String URL_MAKE_APPOINTMENT = "/appointments/cs_schedule_appointment";
    public static String URL_REASON_FOR_VISIT = "/support/chief_complaint_reasons";
    public static String URL_TIMEZONE_BY_STATE = "/support/state_timezones";
    public static String URL_ZIPCODE = "/providers/search_providers";
    public static String URL_PROVIDER_DETAILS ="/providers/";
    public static String URL_PEDIATRIC_PROFILE = "/medical_histories/pediatric_profile";
    public static String UPLOAD_MEDICALREPORT = "/customer/upload_document";
    public static String DOWNLOAD_MEDICALREPORT = "/customer/records?images_only=true";
    public static String DELETE_MEDICALREPORT = "/customer/delete_document";
    public static String DOWNLOAD_MEDICAL_IMAGE = "/customer/download_document";
    public static String DEFAULT_USER_ID = "MobileUser";
    public static String DEFAULT_SESSION_ID = "";
    public static String URL_PHARMACIES_CURRENT = "/pharmacies/current"; // Need additional header
    public static String URL_PHARMACIES_SEARCH_LOCATION = "/pharmacies/search";
    public static String URL_PHARMACIES_UPDATE = "/pharmacies/update"; // Need post body
    public static String URL_PHARMACY_BY_NAME_SEARCH = "/pharmacies/suggest_pharmacy";
    public static String URL_MEDICATION_LIST = "/medications";
    public static String URL_MEDICATION_CREATE = "/medications"; // need post body
    public static String URL_MEDICATION_UPDATE = "/medications"; // need additional header
    public static String URL_MEDICATION_DELETE = "/medications";
    public static String URL_MEDICATION_SEARCH = "/medications/search"; // need post body
    public static String URL_MEDICAL_HISTORY_COMPLETION = "/medical_histories/history_completion";
    public static String URL_MEDICAL_HISTORY_AGGREGATION = "/medical_histories/history_aggregation";
    public static String URL_MEDICAL_CONDITIONS_LIST = "/medical_conditions";
    public static String URL_MEDICAL_CONDITIONS_AUTOSUGGESTION = "/medical_conditions/search";
    public static String URL_ALLERGY_LIST = "/allergies";
    public static String URL_PROCEDURE_SEARCH_LIST = "/surgeries/surgeries_list";
    public static String URL_PROCEDURE_LIST = "/surgeries";
    public static String URL_PROCEDURE_AUTOSUGGESTION = "/surgeries/search";
    public static String URL_ALLERGY_AUTOSUGGESTION = "/allergies/search";
    public static String URL_WAITING_ROOM = "/consultations/provider_status/";
    public static String URL_WAITING_ROOM_VSEE = "/consultations/get_vsee_login/";
    public static String URL_UPDATE_FEMALE_ATTRIBUTES = "/medical_histories/update_female_attributes";
    public static String URL_CONFIRM_APPT = "/appointments/confirm_appointment";
    public static String URL_UPDATE_PEDIATRIC = "/medical_histories/update_pediatric_profile";
    public static String URL_GET_PEDIATRIC = "/medical_histories/pediatric_profile";
    public static String USER_INFORMATION = "/customer/user_information";
    public static String URL_GET_PROMOCODE= "/consultation_charges/get_promocode_details?promocode=";
    public static String URL_USER_INFO= "/customer/user_information";
    public static String URL_PENDING_APPOINTMENT= "/appointments";
    public static String URL_BILLING_UPDATE= "/billing_informations/1";
    public static String URL_GET_LASTDATE_MEDICAL_HISTORY = "/medical_histories/medical_history_completion";
    public static String URL_UPDATE_MEDICAL_HISTORY= "/medical_histories/update_medical_history";
    public static String URL_RATINGS = "/ratings";
    public static String URL_ZERO_INSURANCE = "/customer/check_eligibility";
    public static String LOCATION_SERVICE = "/geolocations/find_location_by_coordinates?";
    public static String ADD_CHILD = "/customer/1/add_family_member";
    public static String URL_VISIT_CHECK_PROVIDER_STATUS = "/services/consultations/provider_status/";
    public static String URL_VISIT_PROVIDER_CONSULTATION = "/services/consultations/get_vsee_login/";
    public static String URL_MESSAGES_READ = "/messages/";
    public static String URL_MESSAGES_READ_TYPE = "?msg_type=";
    public static String URL_ONCALL_APPT = "/appointments/oncall_consultation";
    public static String URL_WAITING_ONCALL_STATUS = "/waiting_room/:id/status";
    public static final String URL_AUTHENTICATE_LOGIN_BAYLOR = "/customer_logins/authenticate";

    public static String URL_ENVIRONMENT = "";
    public static String URL_CHANGE_PASSWORD;
    public static String URL_CHANGE_PIN;
    public static String URL_SECURITY_QUESTION;
    public static String URL_UPDATE_SECURITY_QUESTIONS;
    public static String URL_PROFILE_INFO;
    public static String URL_ADD_CREDIT_CARD_INFO;
    public static String URL_GET_CREDIT_CARD_INFO;
    public static String URL_ADD_FAMILY_INFO;
    public static String URL_ADD_PCP;
    public static String URL_GET_FAMILY_MEMBER_INFO;
    public static String URL_EDIT_PROFILE_INFO;
    public static String URL_CHANGE_PROFILE_PIC;

    // URLS for MESSAGE CENTER section
    public static String URL_GET_RECEIVED_MESSAGES;
    public static String URL_GET_SENT_MESSAGES;
    public static String URL_PROVIDER;
    public static String URL_COMPOSE_MESSAGE;
    public static String URL_MY_RECORDS;
    public static String URL_UPLOAD_DOCUMENT;

    // URLS for BEHAVIOURAL section
    public static String URL_BEHAVIOURAL_HISTORY;
    public static String URL_UPDATE_BEHAVIOURAL_HISTORY;

    // URLS for LIFE STYLE section
    public static String URL_LIFE_STYLE = "/life_style_conditions ";
    public static String URL_UPDATE_LIFE_STYLE;

    // URLS for HELP SUPPORT section
    public static String URL_HELP_AND_SUPPORT;
    public static String URL_ASK_A_QUESTION;

    // URLS for FAMILY HISTORY section
    public static String URL_FAMILY_HISTORY;
    public static String URL_UPDATE_FAMILY_HISTORY;

    // URLS for NOTIFICATION section
    public static String URL_NOTIFICATIONS;

    public static String URL_SIGN_UP;
    public static String URL_FORGOT_USERNAME;
    public static String URL_FORGOT_PASSWORD;
    public static String URL_CONSULTATION_HISTORY;

    public static String SYMPTOM_CHECKER_URL;

    // API used to get zip code
    public static String GEOCODE_API_ENDPOINT;

    public static String PIN_AUTHENTICATION;
    public static String GEO_TARGET_DETAILS;
    public static String URL_HEALTH_KIT_SYNC;

    private AppSpecificConfig(){
        // this class cannot be directly instantiated externally
    }

    private static final Object syncObj = new Object();
    private static AppSpecificConfig instance;
    public static AppSpecificConfig getInstance()
    {
        // restrict access to a single thread only
        // [ START CRITICAL SECTION ]
        synchronized(syncObj)
        {
            // create instance of this class if necessary...
            if (instance == null) {
                instance = new AppSpecificConfig();
            }
        }
        // [ END CRITICAL SECTION ]

        return instance;
    }
}
