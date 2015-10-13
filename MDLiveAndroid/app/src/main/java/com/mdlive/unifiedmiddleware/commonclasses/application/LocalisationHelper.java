package com.mdlive.unifiedmiddleware.commonclasses.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by unnikrishnan_b on 4/26/2015.
 *
 *
 * This class has been superceded by
 * @see com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton
 */
@Deprecated
public class LocalisationHelper {

    private static LocalisationHelper localisationHelper; // The LocalisationHelper singleton instance
    private static final String PREFS_FILE_PREFIX = "prefs_file_"; // The shared preference file prefix
    private static String PREFS_LANG = "en";      // set default language to English
    private static SharedPreferences sharedPrefs; // Shared Preference Instance

    /**
     *
     * Private constructor to make singleton class
     */
    private LocalisationHelper(){
    }

    /**
     *
     * Returns instance of the class.
     *
     * @return LocalisationHelper :: The LocalisationHelper instance.
     */
    public static LocalisationHelper getInstance(){
        if(localisationHelper==null){
            localisationHelper=new LocalisationHelper();
        }
        return localisationHelper;
    }



    /**
     * Fetch localized string from shared prefs.
     *
     * @param context :: context object
     * @param key :: key of Key-Value pair
     * @return String :: Localised string
     */
    public static String getLocalizedStringFromPrefs(Context context, String key){
        try {
            sharedPrefs = context.getSharedPreferences(PREFS_FILE_PREFIX + PREFS_LANG, Context.MODE_PRIVATE);
            JSONObject langJsonObject = new JSONObject(sharedPrefs.getString("strings", "{}"));
            String localizedString = langJsonObject.getString(key);
            return localizedString;
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Fetch localized string from local language files, or from updated shared prefs
     * if it is redefined there instead.
     *
     * @param key :: key of Key-Value pair
     * @param langJsonObject :: language JSON object
     * @return String :: Localised string
     */
    public static String getLocalizedStringFromJson(String key, JSONObject langJsonObject){
        try {
            String localizedString = langJsonObject.getString(key);
            return localizedString;
        } catch (Exception e){
            return "";
        }
    }
    /**
     *
     * Sets the specified language for the application.
     *
     * @param language :: two-character language to be set e.g. "es", "en", "ko"
     */
    public static void setLanguage(String language)
    {
        PREFS_LANG = language;
    }

    /**
     *
     * Sets the language json file to the corresponding preference.
     *
     *
     * @param context :: The context
     * @param languageJson :: The language json
     */
    public static void setLanguagePreference(Context context, String languageJson){
        sharedPrefs = context.getSharedPreferences(PREFS_FILE_PREFIX + PREFS_LANG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.putString("strings",languageJson);
        editor.commit();
    }

    /**
     * If this first time app is being run right after an update (or installation),
     * then purge the language shared preferences key-value pairs.
     * First-time invocation is detected by comparing the current app version
     * with the previous app version.
     *
     * @param ctx   context obj
     */
    public static void purgeLangPrefsIfInstallUpdated(Context ctx)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(ctx);
        int previousVersionCode = p.getInt("APP_VERSION", -1);

        String pkgName = ctx.getPackageName();
        int versionCode=0;
        try {
            versionCode = ctx.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
        }catch(PackageManager.NameNotFoundException nex) {
            // @ToDo - handle the issue
        }

        if(previousVersionCode != versionCode){
            // purge all language preference caches
            ctx.getSharedPreferences(PREFS_FILE_PREFIX + "en", Context.MODE_PRIVATE).edit().clear().commit();
            ctx.getSharedPreferences(PREFS_FILE_PREFIX + "es", Context.MODE_PRIVATE).edit().clear().commit();
            ctx.getSharedPreferences(PREFS_FILE_PREFIX + "ko", Context.MODE_PRIVATE).edit().clear().commit();

            // update the app version cache
            p.edit().putInt("APP_VERSION", versionCode).commit();
        }
    }



    /**
     *
     * This method will read all the child views of ParentLayout and will apply the localisations to
     * all the necessary fields.
     *
     *
     * @param parentLayout :: The Layout to which the localisation to be applied.
     * @param context :: The context of the parent layout
     */
    public static void localiseLayout(Context context, ViewGroup parentLayout){
        try {
            int count = parentLayout.getChildCount();
            sharedPrefs = context.getSharedPreferences(PREFS_FILE_PREFIX + PREFS_LANG, Context.MODE_PRIVATE);
            JSONObject langJsonObject = new JSONObject(sharedPrefs.getString("strings", "{}"));
            for (int i = 0; i < count; i++) {
                View childView = parentLayout.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    localiseLayout(context,(ViewGroup) childView);
                } else {
                    applyLocalisationOnView(childView,langJsonObject);
                }
            }
        }catch(Exception e){
            e.getStackTrace();
        }
    }

    /**
     *
     * This method will fetch the tag from the view and will get the localised string from the shared preference.
     * Then, the data is added to the respective views.
     *
     * @param childView :: The view to which the localisation need to be applied
     * @param langJsonObject :: language JSON object
     */
    private static void applyLocalisationOnView(View childView, JSONObject langJsonObject){
        try {
            String localisationKey = (childView.getTag() instanceof String) ? (String) childView.getTag() : null;
            if(localisationKey!= null) {
                String localisedString = getLocalizedStringFromJson(localisationKey, langJsonObject);
                if(!localisedString.equals("")) {
                    if (childView instanceof EditText) {
                        ((EditText) childView).setHint(localisedString);
                    } else if (childView instanceof TextView) {
                        ((TextView) childView).setText(localisedString);
                    } else if (childView instanceof Button) {
                        ((Button) childView).setText(localisedString);
                    } else if (childView instanceof ImageView) {
                        childView.setContentDescription(localisedString);
                    }
                }
            }
        } catch(Exception e){
            e.getStackTrace();
        }
    }

    /**
     *
     * Method to convert the timestamp to Date with time.
     *
     * @param timeStamp :: Timestamp to be converted
     * @param context :: The context
     * @return String :: The date with time string.
     */
    public String convertTimeStampToDate(Context context, String timeStamp){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String timeZone = sharedpreferences.getString(PreferenceConstants.TIMEZONE_PREF,"");
        String convertedTime = "";
        if(!timeZone.equals("")) {
            TimeZone zone = TimeZone.getTimeZone(timeZone);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.getLong(timeStamp));
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
            formatter.setCalendar(calendar);
            formatter.setTimeZone(zone);
            convertedTime = formatter.format(calendar.getTime()) + timeZone;
        }
        return convertedTime;
    }

    /**
     *
     * Method to convert the timestamp to time.
     *
     * @param timeStamp :: Timestamp to be converted
     * @param context :: The context
     * @return String :: The time string.
     */
    public String convertTimeStampToTime(Context context, String timeStamp){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String timeZone = sharedpreferences.getString(PreferenceConstants.TIMEZONE_PREF,"");
        String convertedTime = "";
        if(!timeZone.equals("")) {
            TimeZone zone = TimeZone.getTimeZone(timeZone);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.getLong(timeStamp));
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setCalendar(calendar);
            formatter.setTimeZone(zone);
            convertedTime = formatter.format(calendar.getTime()) + timeZone;
        }
        return convertedTime;
    }





}