package com.mdlive.unifiedmiddleware.commonclasses.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.mdlive.unifiedmiddleware.parentclasses.bean.response.DeepLink;

/**
 * Created by sampath_k on 08/09/15.
 */
public class DeepLinkUtils {
    private static final String DELIMITER = "_";
    public static enum PageCode {HOME, ASSIST, SAV, HEALTH, MESG, SC, ACCNT};
    public static final String DEEP_LINK_BAYLOR = "is_baylor_deep_link";
    public static final String BAYLOR_PACKAGE_NAME = "com.baylorscottandwhite.healthsource";
    public static DeepLink DEEPLINK_DATA;

    /**
     * Concatenates various device IDs together to form the final unique ID.
     * Such a strategy is required on Android platform because there is no single, reliable, global mechanism
     * for generating a unique device ID across *ALL* device types/manufacturers/OS versions.
     * “ANDROID_ID” is the preferred way to generate unique IDs but is only guaranteed for devices running
     * OS 4.2+ (Jellybean+)
     *
     * @param ctx caller’s context
     * @return
     */
    public static String getDeviceId(Context ctx) {
        StringBuilder sbuff = new StringBuilder();
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

     /*
     * getDeviceId() function Returns the unique device ID.
     * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
     */
        String imeistring = telephonyManager.getDeviceId();
        sbuff.append(imeistring + DELIMITER);

     /*
     * getSubscriberId() function Returns the unique subscriber ID,
     * for example, the IMSI for a GSM phone.
     */
        String imsistring = telephonyManager.getSubscriberId();
        sbuff.append(imsistring + DELIMITER);

     /*
     * Settings.Secure.ANDROID_ID returns the unique DeviceID
     * Works for Android 2.2 and above
     */
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        sbuff.append(androidId);
        //Log.d("deep link device id", sbuff.toString()+"");
        return (sbuff.toString());
    }

    /**
     * Indicates whether or not a user is affiliated to Baylor
     *
     * @param ctx context obj for accessing other objects
     * @return True if user is affiliated, False otherwise
     */
    public static boolean isBaylorAffiliate(Activity ctx) {
        boolean status = false;
        if (ctx != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            status = sharedPref.getBoolean(DeepLinkUtils.DEEP_LINK_BAYLOR, false);
        }
        return (status);
    }

    /**
     * Generic single-button dialog generator.
     *
     * @param context    the calling Activity
     * @param title      dialog title
     * @param message    dialog message
     * @param okMessage  OK button message
     * @param cancelable user can cancel dialog ?
     * @param goBaylor   open Baylor app after dialog ?
     */
    public static void alertDialogShow(final Activity context, String title, String message, String okMessage, boolean cancelable, final boolean goBaylor) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(cancelable);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, okMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                if (goBaylor) {
                    openBaylorApp(context);
                    /*
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                    */
                }
            }
        });
        alertDialog.show();
    }

    /**
     * Opens the Baylor app if the app is installed.
     * If Baylor app is in background, it will be resumed from the point from which it was previously exited.
     *
     * @param ctx the caller's context
     */
    public static void openBaylorApp(Context ctx) {
        if (ctx == null)
            return;
        try {
            Intent baylorIntent = ctx.getPackageManager().getLaunchIntentForPackage(DeepLinkUtils.BAYLOR_PACKAGE_NAME);
            baylorIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ctx.startActivity(baylorIntent);
        } catch (Exception e) {
//            BugSenseHandler.sendException(new Exception(SplashScreen.CRASH_PREPENDAGE + e.getMessage(), e));
        }
    }

    public static int getPageCode() {
        if (DEEPLINK_DATA.getPage() != null && !DEEPLINK_DATA.getPage().isEmpty()) {
            return DeepLinkUtils.PageCode.valueOf(DeepLinkUtils.DEEPLINK_DATA.getPage()).ordinal();
        } else {
            return -1;
        }
    }
}
