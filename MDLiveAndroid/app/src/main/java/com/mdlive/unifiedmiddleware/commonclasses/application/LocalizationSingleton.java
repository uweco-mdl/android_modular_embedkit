package com.mdlive.unifiedmiddleware.commonclasses.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Locale;

/**
 * This singleton class provides APIs for manipulating localized strings.
 *
 * This class supercedes
 * @see com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper
 *
 * i18n support is implemented via the platform's native localization mechanism (XML language files)
 * However, SharedPreference files are used to provide runtime language string updates by caching the received
 * 'patch-file' language updates from the server.
 * Separate SharedPreferences files are used for each supported language, that way the *same* key names can be used across all language pref files.
 *
 */
public final class LocalizationSingleton
{
    private static final String PREFS_FILE_PREFIX = "prefs_file_";
    private static String PREFS_LANG = "en";            // set default lang to English
    private static SharedPreferences sharedPrefs = null;
    private static LocalizationSingleton localizer = null;
    private static final Object syncObj = new Object();
    public static JSONObject languageContent = null;

    private LocalizationSingleton(){
        // this class cannot be directly instantiated externally
    }

    public static LocalizationSingleton getInstance()
    {
        // [ START CRITICAL SECTION ]
        synchronized(syncObj) {
            if (localizer == null) {
                localizer = new LocalizationSingleton();
            }
        }
        // [ END CRITICAL SECTION ]

        return localizer;
    }

    /**
     *
     * Sets the specified language for the application.
     *
     * @param activity  Activity
     * @param language  two-character language to be set e.g. "es", "en", "ko"
     */
	public static void setLanguage(Activity activity, String language)
    {
        PREFS_LANG = language;

        // update Locale to ensure that correct strings.xml file is automatically selected.
        Locale myLocale = new Locale(PREFS_LANG);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());

        LocalizationSingleton.languageContent = getJsonObjectForLanguage(activity, language);
		if(LocalizationSingleton.languageContent == null){
            LocalizationSingleton.languageContent = getJsonObjectForLanguage(activity, "en");
		}
//        activity.recreate();
	}

    /**
     * Fetch localized string from local language files, or from updated shared prefs
     * if it is redefined there instead.
     *
     * @param localStringId     resource ID of local string
     * @param key               key of Key-Value pair
     * @param ctx               context obj
     * @return
     */
    public static String getLocalizedString(int localStringId, String key, Context ctx)
    {
        sharedPrefs = ctx.getSharedPreferences(PREFS_FILE_PREFIX + PREFS_LANG, Context.MODE_PRIVATE);
        String selection = sharedPrefs.getString(key, null);
        if(selection==null)
            selection = ctx.getString(localStringId);

        return(selection);
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
     *  Get a localised string for the specified key
     * @param key key for the string
     * @return Specified language sting
     */
	public static String localizedStringForKey(String key){
		try {
            if(LocalizationSingleton.languageContent != null)
			    return LocalizationSingleton.languageContent.has(key) ? LocalizationSingleton.languageContent.getString(key) : "";
            else
                return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static JSONObject getJsonObjectForLanguage(Activity mdliveMain, String language){
		String json = null;
	    try {
	        InputStream is = mdliveMain.getAssets().open("www/lang/"+language+".json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        json = new String(buffer, "UTF-8");
	        return (new JSONObject(json)).getJSONObject("native_list");
	    } catch (Exception ex) {
	    	// GATrackingExceptions.trackExceptions(ex.getLocalizedMessage() + " :: Language File missing for internationalization", mdliveMain);
	        ex.printStackTrace();
	        return null;
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
                    } else if (childView instanceof RadioButton){
                        ((RadioButton)childView).setText(localisedString);
                    }
                }
            }
        } catch(Exception e){
            e.getStackTrace();
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
}
