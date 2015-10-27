package com.mdlive.embedkit.global;


import android.app.Activity;
import android.content.Intent;

import com.mdlive.embedkit.uilayer.login.SSOActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;

import java.util.EnumSet;
import java.util.Set;

/**
 * All the configuration details are stored here. These information will be passed to the middleware \
 * layer by invoking the AppSpecificConfig class.
 *
 */
public class MDLiveConfig {

    private MDLiveConfig(){}

    public enum ENVIRON {
        PROD, STAGE, QA, DEV, QAPL, UNDEFINED
    }

    public enum SIGNALS {
        EXIT_SIGNAL
    }

    /**
     * This API exposes the capabilities of the delivered embedkit.
     * All exposed components must be declared here.
     * The affiliate's app will use this enum to view/select a specific EmbedKit component.
     */
    public enum EMBEDKITS {
        SYMPTOM_CHECKER,
        MY_MESSAGES,
        MY_HEALTH,
        MY_ACCOUNT,
        DOCTOR_CONSULT,
        CALL_ASSIST
    }

    // Define what embedkit components subset will be actually accessible to affiliate app
    public static Set<EMBEDKITS> EMBEDKIT_COMPONENTS = EnumSet.of(EMBEDKITS.CALL_ASSIST, EMBEDKITS.MY_MESSAGES);

    public static final String ZERO_PAY = "0.00";     // used to indicate a "zero payment" liability for consultations

    public static ENVIRON CURRENT_ENVIRONMENT;
    public static EMBEDKITS SELECTED_COMPONENT;

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
    public static final String URL_PROVIDER_DETAILS ="/providers/";
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
    private static final String URL_ADD_PCP = "/providers/add_primary_care_physician";


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
    private static final String LOCATION_SERVICE = "/geolocations/find_location_by_coordinates?";
    private static final String ADD_CHILD = "/customer/1/add_family_member";

    private static final String URL_CHANGE_PASSWORD = "/customer/change_password";
    private static final String URL_CHANGE_PIN = "/pass_codes/update";
    public static final String URL_CREATE_PIN = "/pass_codes";
    private static final String URL_SECURITY_QUESTION = "/support/security_questions";
    private static final String URL_UPDATE_SECURITY_QUESTIONS = "/customer/update_security_questions";
    private static final String URL_PROFILE_INFO = "/customer/:id";
    private static final String URL_ADD_CREDIT_CARD_INFO = "/billing_informations";
    private static final String URL_GET_CREDIT_CARD_INFO = "/billing_informations/1";
    private static final String URL_ADD_FAMILY_INFO = "/customer/1/add_family_member";
    private static final String URL_GET_FAMILY_MEMBER_INFO = "/customer/family_members";
    private static final String URL_EDIT_PROFILE_INFO = "/customer/3";
    private static final String URL_CHANGE_PROFILE_PIC = "/customer/upload_photo";

    // URLS for MESSAGE CENTER section
    private static final String URL_GET_RECEIVED_MESSAGES = "/messages/received_messages";
    private static final String URL_GET_SENT_MESSAGES = "/messages/sent_messages";
    private static final String URL_PROVIDER = "/providers";
    private static final String URL_COMPOSE_MESSAGE = "/messages/";
    private static final String URL_MESSAGES_READ = "/messages/";
    private static final String URL_MESSAGES_READ_TYPE = "?msg_type=";
    private static final String URL_MY_RECORDS = "/customer/records";
    private static final String URL_UPLOAD_DOCUMENT = "/customer/upload_document";
    private static final String URL_CONSULTATION_HISTORY = "/customer/consultation_history";

    // URLS for BEHAVIOURAL section
    public static final String URL_BEHAVIOURAL_HISTORY = "/behavioral_histories";
    public static final String URL_UPDATE_BEHAVIOURAL_HISTORY = "/behavioral_histories/update_behavioral_history ";

    // URLS for LIFE STYLE section
    public static final String URL_LIFE_STYLE = "/life_style_conditions ";
    public static final String URL_UPDATE_LIFE_STYLE = "/life_style_conditions/update_life_style_conditions ";

    // URLS for HELP SUPPORT section
    public static final String URL_HELP_AND_SUPPORT = "/support/faqs";
    public static final String URL_ASK_A_QUESTION = "/messages/send_support_message";

    // URLS for FAMILY HISTORY section
    public static final String URL_FAMILY_HISTORY = "/family_histories";
    public static final String URL_UPDATE_FAMILY_HISTORY = "/family_histories/update_family_histories";

    // URL for NotificationService
    public static final String URL_NOTIFICATION = "/notifications";

    public static final String PIN_AUTHENTICATION = "/pass_codes/authenticate";

    public static final String EMAIL_CONFIRMATION = "/customer/resend_email_confirmation";

    // SSO-related keys
    public static String USR_UNIQ_ID=null;
    public static String AUTH_KEY=null;
    public static final String UNIQUE_ID_STRINGNAME = "uniqueid";
    public static final String AUTHORIZATION_KEY = "api_key";

    static {
        System.loadLibrary("app");
    }


    public static void setData(ENVIRON currentEnvironment){
        CURRENT_ENVIRONMENT = currentEnvironment;

        switch (currentEnvironment){
            case DEV:
                // dev environment
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://dev-members.mdlive.com/services";
                AppSpecificConfig.API_KEY = "a775f7e2ed1ce6cb313b";
                AppSpecificConfig.SECRET_KEY = "843f117b0bf7368ed5d";
                AppSpecificConfig.URL_ENVIRONMENT = "d";
                AppSpecificConfig.URL_SIGN_UP = "https://dev-members.mdlive.com/signup/mobile";
                AppSpecificConfig.URL_FORGOT_USERNAME = "https://dev-members.mdlive.com/forgot_username";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "https://dev-members.mdlive.com/forgot_password";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
                break;
            case QA:
                // QA environment
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://pluto-members.mdtestsite.net/services";
                AppSpecificConfig.API_KEY = "b74d0fb9a114904c009b";
                AppSpecificConfig.SECRET_KEY = "89c8d3ea88501e8e62a";
                AppSpecificConfig.URL_ENVIRONMENT = "q";
                AppSpecificConfig.URL_SIGN_UP = "https://pluto-members.mdtestsite.net/signup/mobile";
                AppSpecificConfig.URL_FORGOT_USERNAME = "http://www.mdlive.com/mobile/forgotusername";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "http://www.mdlive.com/mobile/forgotpassword";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
                break;
            case STAGE:
                // stage environment
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://stage-rtl.mdlive.com/services";
                AppSpecificConfig.API_KEY = "c9e63d9a77f17039c470";
                AppSpecificConfig.SECRET_KEY = "b302e84f866a8730eb2";
                AppSpecificConfig.URL_ENVIRONMENT = "s";
                AppSpecificConfig.URL_SIGN_UP = "https://stage-members.mdlive.com/signup/mobile";
                AppSpecificConfig.URL_FORGOT_USERNAME = "https://stage-members.mdlive.com/forgot_username";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "https://stage-members.mdlive.com/forgot_password";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
                break;
            case PROD:
                // Production environment
                AppSpecificConfig.URL_ENVIRONMENT = "";
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://rtl.mdlive.com/services";
                AppSpecificConfig.API_KEY = getProdApiKeyFromNative();
                AppSpecificConfig.SECRET_KEY = getProdSecretKeyFromNative();
                AppSpecificConfig.URL_SIGN_UP = "http://www.mdlive.com/mobile/joinnow";
                AppSpecificConfig.URL_FORGOT_USERNAME = "https://members.mdlive.com/forgot_username";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "https://members.mdlive.com/forgot_password";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
                break;
            case QAPL:
                // QA Pluto URL
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://pluto-members.mdtestsite.net/services";
                AppSpecificConfig.API_KEY = "b74d0fb9a114904c009b";
                AppSpecificConfig.SECRET_KEY = "89c8d3ea88501e8e62a";
                AppSpecificConfig.URL_ENVIRONMENT = "q";
                AppSpecificConfig.URL_SIGN_UP = "https://pluto-members.mdtestsite.net/signup/mobile";
                AppSpecificConfig.URL_FORGOT_USERNAME = "https://pluto-members.mdtestsite.net/forgot_username";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "https://pluto-members.mdtestsite.net/forgot_password";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
                break;

            case UNDEFINED:
            default:
                AppSpecificConfig.WEB_URL = "http://www.mdlive.com";
                AppSpecificConfig.BASE_URL = "https://dev-members.mdlive.com/services";
                AppSpecificConfig.API_KEY = "a775f7e2ed1ce6cb313b";
                AppSpecificConfig.SECRET_KEY = "843f117b0bf7368ed5d";
                AppSpecificConfig.URL_ENVIRONMENT = "d";
                AppSpecificConfig.URL_SIGN_UP = "https://dev-members.mdlive.com/signup/mobile";
                AppSpecificConfig.URL_FORGOT_USERNAME = "https://dev-members.mdlive.com/forgot_username";
                AppSpecificConfig.URL_FORGOT_PASSWORD = "https://dev-members.mdlive.com/forgot_password";
                AppSpecificConfig.SYMPTOM_CHECKER_URL = "https://stage-symptomchecker.mdlive.com/sc/html/index.html";
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
        AppSpecificConfig.LOCATION_SERVICE = LOCATION_SERVICE;
        AppSpecificConfig.ADD_CHILD = ADD_CHILD;
        AppSpecificConfig.EMAIL_CONFIRMATION = EMAIL_CONFIRMATION;

        AppSpecificConfig.URL_CHANGE_PASSWORD=URL_CHANGE_PASSWORD;
        AppSpecificConfig.URL_CHANGE_PIN=URL_CHANGE_PIN;
        AppSpecificConfig.URL_CREATE_PIN = URL_CREATE_PIN;
        AppSpecificConfig.URL_SECURITY_QUESTION=URL_SECURITY_QUESTION;
        AppSpecificConfig.URL_UPDATE_SECURITY_QUESTIONS=URL_UPDATE_SECURITY_QUESTIONS;
        AppSpecificConfig.URL_PROFILE_INFO=URL_PROFILE_INFO;

        AppSpecificConfig.URL_GET_RECEIVED_MESSAGES = URL_GET_RECEIVED_MESSAGES;
        AppSpecificConfig.URL_GET_SENT_MESSAGES = URL_GET_SENT_MESSAGES;
        AppSpecificConfig.URL_PROVIDER = URL_PROVIDER;
        AppSpecificConfig.URL_COMPOSE_MESSAGE = URL_COMPOSE_MESSAGE;
        AppSpecificConfig.URL_MESSAGES_READ = URL_MESSAGES_READ;
        AppSpecificConfig.URL_MESSAGES_READ_TYPE = URL_MESSAGES_READ_TYPE;
        AppSpecificConfig.URL_MY_RECORDS = URL_MY_RECORDS;
        AppSpecificConfig.URL_UPLOAD_DOCUMENT = URL_UPLOAD_DOCUMENT;
        AppSpecificConfig.URL_ADD_CREDIT_CARD_INFO=URL_ADD_CREDIT_CARD_INFO;
        AppSpecificConfig.URL_GET_CREDIT_CARD_INFO=URL_GET_CREDIT_CARD_INFO;
        AppSpecificConfig.URL_ADD_FAMILY_INFO=URL_ADD_FAMILY_INFO;
        AppSpecificConfig.URL_ADD_PCP=URL_ADD_PCP;
        AppSpecificConfig.URL_GET_FAMILY_MEMBER_INFO=URL_GET_FAMILY_MEMBER_INFO;
        AppSpecificConfig.URL_EDIT_PROFILE_INFO=URL_EDIT_PROFILE_INFO;
        AppSpecificConfig.URL_CHANGE_PROFILE_PIC=URL_CHANGE_PROFILE_PIC;


        AppSpecificConfig.URL_LIFE_STYLE = URL_LIFE_STYLE;
        AppSpecificConfig.URL_UPDATE_LIFE_STYLE = URL_UPDATE_LIFE_STYLE;

        AppSpecificConfig.URL_HELP_AND_SUPPORT = URL_HELP_AND_SUPPORT;
        AppSpecificConfig.URL_ASK_A_QUESTION = URL_ASK_A_QUESTION;

        AppSpecificConfig.URL_FAMILY_HISTORY = URL_FAMILY_HISTORY;
        AppSpecificConfig.URL_UPDATE_FAMILY_HISTORY = URL_UPDATE_FAMILY_HISTORY;

        AppSpecificConfig.URL_BEHAVIOURAL_HISTORY = URL_BEHAVIOURAL_HISTORY;
        AppSpecificConfig.URL_UPDATE_BEHAVIOURAL_HISTORY = URL_UPDATE_BEHAVIOURAL_HISTORY;

        AppSpecificConfig.URL_NOTIFICATIONS = URL_NOTIFICATION;

        AppSpecificConfig.URL_CONSULTATION_HISTORY = URL_CONSULTATION_HISTORY;

        AppSpecificConfig.PIN_AUTHENTICATION = PIN_AUTHENTICATION;

    }

    static native String getProdApiKeyFromNative();
    static native String getProdSecretKeyFromNative();

    /**
     * Initiates a specific EmbedKit component
     *
     * @param component
     * @return
     */
    public static boolean activate(EMBEDKITS component, String jsonString, ENVIRON env, Activity ctx )
    {
        boolean success = true;

        SELECTED_COMPONENT = component;

        try {
            Intent embedKitIntent = new Intent(ctx, SSOActivity.class);
            embedKitIntent.putExtra("affiliate_sso_login", jsonString);
            embedKitIntent.putExtra("env", env.name());
            ctx.startActivity(embedKitIntent);
        }catch(Exception ex)
        {
            success = false;
        }

        return(success);
    }

}
