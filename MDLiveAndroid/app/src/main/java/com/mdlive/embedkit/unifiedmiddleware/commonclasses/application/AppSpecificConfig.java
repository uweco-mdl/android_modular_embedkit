package com.mdlive.embedkit.unifiedmiddleware.commonclasses.application;

/**
 * Created by unnikrishnan_b on 4/7/2015.
 */

/**
 *
 * Singleton class providing app-specific configurations. This includes the API keys, Base URLs, etc.
 *
 */
public class AppSpecificConfig {

    private static AppSpecificConfig instance = null;
    private static Object syncObj = new Object();

    public static String BASE_URL;
    public static String LOGIN_SERVICES;
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String DEFAULT_USER_ID;
    public static String URL_USER_INFORMATION;
    public static String URL_FAMILY_MEMBER;
    public static String URL_FILTER_SEARCH;
    public static String URL_SEARCH_PROVIDER;
    public static String URL_PROVIDER_DETAILS;
    public static String URL_LIFE_STYLE;
    public static String URL_PEDIATRIC_PROFILE;
    public static String URL_ZIPCODE;
    public static String URL_PROVIDER_TYPE;
    public static String URL_CHOOSE_PROVIDER;
    public static String URL_REASON_FOR_VISIT;
    public static String URL_VISIT_CHECK_PROVIDER_STATUS = "/services/consultations/provider_status/";
    public static String URL_VISIT_PROVIDER_CONSULTATION = "/services/consultations/get_vsee_login/";

    public static String URL_PHARMACIES_CURRENT ;
     public static String URL_PHARMACIES_SEARCH_LOCATION ;
     public static String URL_PHARMACIES_UPDATE;
     public static String URL_PHARMACY_BY_NAME_SEARCH;
     public static String URL_MEDICATION_LIST ;
     public static String URL_MEDICATION_CREATE ;
     public static String URL_MEDICATION_UPDATE;
     public static String URL_MEDICATION_DELETE ;
     public static String URL_MEDICATION_SEARCH;
    public static String URL_MEDICAL_HISTORY_COMPLETION;
    public static String URL_MEDICAL_HISTORY_AGGREGATION;
    public static String URL_MEDICAL_CONDITIONS_LIST;
    public static String URL_MEDICAL_CONDITIONS_AUTOSUGGESTION;
    public static String URL_ALLERGY_LIST;
    public static String URL_ALLERGY_AUTOSUGGESTION;
    public static String URL_WAITING_ROOM;
    public static String URL_WAITING_ROOM_VSEE;
    public static String URL_UPDATE_FEMALE_ATTRIBUTES;
    public static String URL_CONFIRM_APPT;

    
    private AppSpecificConfig(){
        // this class cannot be directly instantiated externally
    }

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
