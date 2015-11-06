package com.mdlive.unifiedmiddleware.commonclasses.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SSOUser;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Unnikrishnan B on 03/04/15.
 */
public class MdliveUtils {
    private static final AtomicInteger S_NEXT_GENERATED_ID = new AtomicInteger(1);
    public static SSOUser ssoInstance;
    private MdliveUtils(){
        // this class cannot be directly instantiated externally
    }

    /**
     * Validation for Email
     *
     * @param value
     * @return
     */
    public final static boolean isValidEmail(String value) {
        return !TextUtils.isEmpty(value) && android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    /**
     *
     * Validation for User Name
     * @param value
     * @return
     */
    public final static boolean isValidUserName(String value) {
        return (value.length()>2)?true:false;
    }

    /**
     *
     * Validation for password
     * @param value
     * @return
     */
    public final static boolean isValidPassword(String value) {
        return (value.length()>2)?true:false;
    }

    /**
     *
     * Validation for Name
     * @param value
     * @return
     */
    public final static boolean isValidName(String value) {
        return Pattern.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", value);
    }

    public static void connectionTimeoutError(ProgressDialog pDialog, final Context context) {
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(context);
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                final String number = userBasicInfo == null ? context.getString(R.string.mdl_help_number_if_assist_not_present) : userBasicInfo.getAssistPhoneNumber();
                showDialog(context, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString(), context.getString(R.string.mdl_connection_timeout_error_message, number), context.getString(R.string.mdl_ok_upper), null, null, null);
            }
        });
    }


    public static void showDialog(final Context context, String title, String message, String positiveBtn, final String negativeBtn,
                                  DialogInterface.OnClickListener positiveOnclickListener, final DialogInterface.OnClickListener negativeOnclickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        if(positiveOnclickListener == null){
            positiveOnclickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        alertDialogBuilder
                .setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveBtn,positiveOnclickListener);
        if (!TextUtils.isEmpty(title)) {
            alertDialogBuilder
                    .setTitle(title);
        }

        if(negativeBtn!=null && negativeOnclickListener != null) {
            alertDialogBuilder.setNegativeButton(negativeBtn,negativeOnclickListener);
        }
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
                if (negativeBtn!=null && negativeOnclickListener !=null){
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
                }
            }
        });
        alertDialog.show();
    }

    public static void showDialog(final Context context, String message,
                                  DialogInterface.OnClickListener positiveOnclickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",positiveOnclickListener);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alertDialog.show();
    }


    public static void showDialog(final Context context, String title, String message, EditText promoCode, DialogInterface.OnClickListener positiveOnclickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setTitle("")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", positiveOnclickListener);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alertDialog.setView(promoCode);
        alertDialog.show();
    }


    public static void showDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        DialogInterface.OnClickListener positiveOnclickListener =
                positiveOnclickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
        alertDialogBuilder
                .setTitle(title != null && title.length() > 0 ? title : "")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", positiveOnclickListener);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });

        alertDialog.show();
    }

    public static void alert(ProgressDialog pDialog, final Context context, final String message) {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                showDialog(context, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString(), message, "OK", null, null, null);
            }
        });
    }

    public static void alert(ProgressDialog pDialog, final Context context, final String message, final DialogInterface.OnClickListener onClickListener) {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                showDialog(context, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString(), message, "OK", null, onClickListener, null);
            }
        });
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Activity context) {
        if(context.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(final Activity activity, final EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        editText.requestFocus();
    }

    /**
     * This method validate validZip-Code
     *@param zipCode -User enterd Zipcode has to be passed as parameter
     *
     */
    public static boolean  validateZipCode(String zipCode){
        String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
        Pattern zipCodePattern = Pattern.compile(regex);
        Matcher matcher = zipCodePattern.matcher(zipCode);
        if(matcher.matches()){
            return true;
        }else{
            return false;

        }
    }

    public static ProgressDialog getProgressDialog(String message, Activity context) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        return pDialog;
    }

    public static ProgressDialog getFullScreenProgressDialog(Activity context) {
        ProgressDialog pDialog = new ProgressDialog(context, R.style.MDLive_FullScrDialogTheme);
        pDialog.setIndeterminate(false);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        return pDialog;
    }

    /**
     *
     *
     * This class checks weather the network is available or not. Returns true if available and false if not available.
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNotEmpty(String str)
    {
        return(str!=null && !str.isEmpty());
    }

    /**
     * Generate a value suitable for use in view's setId() method.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = S_NEXT_GENERATED_ID.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (S_NEXT_GENERATED_ID.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * get Formatted String for Date
     *
     * @param - dateString : date String is going to be parsed as required formatted string.
     */

    public static String getFormattedDate(String dateString){
        //2015-05-29 07:15:00
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showProgressDialog(ProgressDialog dialog){
        dialog.show();
    }
    public static void hideProgressDialog(ProgressDialog dialog){
        if(dialog.isShowing()){
            dialog.dismiss();
        }

    }
/*
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }*/

/*
    public int calculateAge(int year, int month, int day){
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH, day);
        thatDay.set(Calendar.MONTH,month); // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, year);

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
        today.setTimeInMillis(diff);
        return today.get(Calendar.YEAR);
    }*/

    /**
     *  This method is used to calcualte age by getting the date of birth
     *
     *  @param birthDate - date of birth of user
     *
     *
     */


    public static int calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object

        return years;
    }

    public static int calculateMonth(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object

        return months;
    }

    public static int calculateDays(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object

        return days;
    }
    //Convertion for the Timestamp to corresponding timezone
//    Step1
    public static String getTimeFromTimestamp(String timestamp) {

        Log.v("Check timestamp",timestamp);
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        Log.d("Time - ", cal.getTime().toString() + " - ");
        final Date date = cal.getTime();
        final Format format = new SimpleDateFormat("h:mm a");
        String convertedTime =  format.format(date);
        Log.v("convertedtime", convertedTime);
        return convertedTime;
    }

    public static int getSampleSizeInFileSize(File file){
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        if(megabytes <= 2){
            return 1;
        }else{
            return (int)Math.round(megabytes);
        }
    }


    /**
     * This method is user rotate the image after picking from camera or gallery and uploaded as exactly taken from camera.
     * @param imagePath-- Corresponding image path where image has been stored.
     * @return-- Image rotation angle
     */

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static String encodeFileToBase64Binary(File file, String extension)
            throws IOException {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = getSampleSizeInFileSize(file);

        Matrix matrix = new Matrix();
        matrix.postRotate(getImageOrientation(file.getAbsolutePath()));
        Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // convert from bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if(extension.contains("jpg") || extension.contains("jpeg"))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        else if(extension.contains("png"))
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        else
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        // get the base 64 string
        String imgString = Base64.encodeToString(stream.toByteArray(),
                Base64.DEFAULT);
        bitmap.recycle();
        return imgString;
    }

    public static String getFileExtention(File file){
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
        if (i > p) {
            extension = file.getName().substring(i + 1);
        }
        return extension;
    }

    public Matrix getImageOrientation(File file){
        try {
            ExifInterface exif = new ExifInterface(
                    file.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public static void showGPSSettingsAlert(final Activity activity, final RelativeLayout loaderLayout){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
//        alertDialog.setTitle("GPS Settings");

        // Setting Dialog Message
        alertDialog.setMessage("We need access to your GPS to determine your location.");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                loaderLayout.setVisibility(View.GONE);
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);

                MdliveUtils.hideSoftKeyboard(activity);
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loaderLayout.setVisibility(View.GONE);
                dialog.cancel();
            }
        });
        final AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.mdlivePrimaryBlueColor));

            }
        });

        // Showing Alert Message
        alert.show();
    }

    public static int daysFromPrefs(Context cxt){
        try {
            SharedPreferences sharedpreferences = cxt.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String age=sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH,"");
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date date = format.parse(age);
            return MdliveUtils.calculateDays(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public static int calculteMonthFromPrefs(Context cxt){
        try {
            SharedPreferences sharedpreferences = cxt.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String age=sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH,"");
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date date = format.parse(age);
            return MdliveUtils.calculateMonth(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public static int calculteAgeFromPrefs(Context cxt){
        try {
            SharedPreferences sharedpreferences = cxt.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            UserBasicInfo userbasicinfo = UserBasicInfo.readFromSharedPreference(cxt);
            String age=sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH,"");
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Log.v("Date final",age);
            Date date = format.parse(userbasicinfo.getPersonalInfo().getBirthdate());
            return MdliveUtils.calculateAge(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //Hiding Keyboard
    public static void hideKeyboard(Context cxt,EditText edText) {
        InputMethodManager imm = (InputMethodManager)cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideKeyboard(Activity context,View view)
    {
        try{
            InputMethodManager in = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){

        }

    }


    /**
     *  This function is for check a json object has valid and not null json string.
     *
     *  This is for validation purpose.
     *
     * @param jsonId : json id to be validate
     * @param jsonObject : object that holds json id
     */

    public static boolean checkJSONResponseHasString(Object jsonObject, String jsonId){
        try {
            if(jsonObject instanceof JsonObject){
                if(!((JsonObject)jsonObject).isJsonNull()){
                    if(((JsonObject)jsonObject).has(jsonId)){
                        if(!((JsonObject)jsonObject).get(jsonId).isJsonNull())
                            return true;
                    }
                }
            }else if(jsonObject instanceof JSONObject){
                if(!((JSONObject)jsonObject).has(jsonId)){
                        if(!((JSONObject)jsonObject).isNull(jsonId))
                            return true;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clearSharedPrefValues(Activity activity)
    {
        SharedPreferences sharedpreferences = activity.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();

        sharedpreferences = activity.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * This method will converts the user entered phone nummber into standar format.
     *
     * @param phoneNumber- User typed phone number to received as params
     * @return-Formatted mobile to the scrren
     */

    public static String phoneNumberFormat(Long phoneNumber) {
        try {
            DecimalFormat phoneDecimalFormat = new DecimalFormat("0000000000");
            String phoneString = phoneDecimalFormat.format(phoneNumber);
            MessageFormat phoneNumberFormat = new MessageFormat("{0}-{1}-{2}");
            String[] phoneNumberArray = {phoneString.substring(0, 3), phoneString.substring(3, 6), phoneString.substring(6)};
            return phoneNumberFormat.format(phoneNumberArray);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * This method will converts the user entered zipcode into standar format.
     *
     * @param zipcode- User typed zipcode to received as params
     * @return-Formatted mobile to the scrren
     */

    public static String zipCodeFormat(Long zipcode) {
        try {
            DecimalFormat zipcodeDecimalFormat = new DecimalFormat("0000000000");
            String zipString = zipcodeDecimalFormat.format(zipcode);
            MessageFormat zipCodeFormat = new MessageFormat("{0}-{1}");
            String[] zipCodeArray = {zipString.substring(1, 6), zipString.substring(6)};
            return zipCodeFormat.format(zipCodeArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String zipCodeFormat(String zipcode) {
        try {
            Log.v("zipcode", zipcode);
            if(zipcode != null && !zipcode.contains("-") && zipcode.length() >= 6){
                zipcode = zipcode.substring(0, 5)+"-"+zipcode.substring(5, zipcode.length()-1);
                return zipcode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zipcode;
    }


    /**
     *This method handles all the error scenarios and displays the corresponding alert.
     * @param cxt---params referring which class it is ivoked from
     * @param error--Received error object in corresponding activities
     * @param pDialog--Progress Dialog obj to show tho dialog
     */

    public static void handelVolleyErrorResponse(Activity cxt, VolleyError error,ProgressDialog pDialog) {
        try {
            final WeakReference<Activity> reference = new WeakReference<Activity>(cxt);

            if (error.networkResponse != null) {
                NetworkResponse errorResponse = error.networkResponse;
                Log.e("Status Code", "" + error.networkResponse.statusCode);
                if (error.getClass().equals(TimeoutError.class)) {
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    };
                    // Show timeout error message
                    connectionTimeoutError(pDialog, reference.get());


                } else if (errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY || errorResponse.statusCode == HttpStatus.SC_NOT_FOUND || errorResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    Log.e("Status Code", "" + error.networkResponse.statusCode);
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    Log.e("responseBody",responseBody);
                    JSONObject errorObj = new JSONObject(responseBody);
                    if (errorObj.has("message")) {
                       alert(pDialog, reference.get(), errorObj.getString("message"));
                    }  else if (errorObj.has("mobile_login_disabled") && errorObj.getBoolean("mobile_login_disabled")) {
                        showVisitCloseAlert(reference, errorObj.getString("error"));
                    } else if (errorObj.has("error")) {
                       alert(pDialog, reference.get(), errorObj.getString("error"));
                    } else{
                        connectionTimeoutError(pDialog, reference.get());

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showVisitCloseAlert(final WeakReference<Activity> reference, final String message) {
        if (reference != null && reference.get() != null) {
            showDialog(reference.get(),
                    reference.get().getString(R.string.mdl_app_name),
                    message,
                    reference.get().getString(R.string.mdl_visit),
                    reference.get().getString(R.string.mdl_close),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                final Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(AppSpecificConfig.WEB_URL));
                                reference.get().startActivity(intent);
                            } catch (Exception e) {
                                MdliveUtils.showDialog(reference.get(), reference.get().getString(R.string.mdl_app_name), reference.get().getString(R.string.mdl_no_compitable_app));
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    /**
     *This method handles all the error scenarios and displays the corresponding alert.
     * @param cxt---params referring which class it is ivoked from
     * @param error--Received error object in corresponding activities
     * @param pDialog--Progress Dialog obj to show tho dialog
     */

    public static void handelVolleyErrorResponseForDependentChild(Activity cxt, VolleyError error,ProgressDialog pDialog, final DialogInterface.OnClickListener onClickListener) {
        final WeakReference<Activity> reference = new WeakReference<Activity>(cxt);

        try {
            if (reference.get() == null) {
                return;
            }

            final SharedPreferences prefs = reference.get().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PreferenceConstants.DEPENDENT_USER_ID, null);
            editor.commit();

            if (error.networkResponse != null) {
                NetworkResponse errorResponse = error.networkResponse;
                Log.e("Status Code", "" + error.networkResponse.statusCode);
                if (error.getClass().equals(TimeoutError.class)) {
                    // Show timeout error message
                    connectionTimeoutError(pDialog, cxt);


                } else if (errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY || errorResponse.statusCode == HttpStatus.SC_NOT_FOUND || errorResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    Log.e("Status Code", "" + error.networkResponse.statusCode);
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    Log.e("responseBody",responseBody);
                    JSONObject errorObj = new JSONObject(responseBody);
                    if (errorObj.has("message")) {
                        alert(pDialog, cxt, errorObj.getString("message"));
                    } else if (errorObj.has("error")) {
                        alert(pDialog, cxt, errorObj.getString("error"), onClickListener);
                    }else{
                        connectionTimeoutError(pDialog, cxt);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkGpsLocation(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                locationCallback(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Do what you need if enabled...
        }else{
            //MdliveUtils.showGPSSettingsAlert(activity);
        }
    }

    public static void showDialog(final Context context, String title, String message, final DialogInterface.OnClickListener positiveClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder
                .setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", positiveClickListener);
       final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alertDialog.show();
    }

    public static void showDialog(final Context context, String message, final DialogInterface.OnClickListener positiveClickListener,
                                  final DialogInterface.OnClickListener negativeClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", positiveClickListener)
                .setNegativeButton("Cancel", negativeClickListener);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alertDialog.show();
    }


    /*
    * Return the long mili seconds for a date which is n years back
     */
    public static long getDateBeforeNumberOfYears(final int numberOfYears) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.YEAR, -numberOfYears);

        return calendar.getTime().getTime();
    }

    /**
     * Starting activity with animation
     */
    public static void startActivityAnimation(Activity context){
        //context.overridePendingTransition(R.anim.mdlive_trans_left_in, R.anim.mdlive_trans_left_out);
    }

    /**
     * closing activity with animation
     */
    public static void closingActivityAnimation(Activity context){
        //context.overridePendingTransition(R.anim.mdlive_trans_right_in, R.anim.mdlive_trans_right_out);
    }

    /**
     * calculate the number of days from a given long time
     *
     * shows today/ yesterday/ number of days ago.
     */
    public static String getDaysAgo(final Context context, final long startDay) {
        final Calendar calendarToday = Calendar.getInstance();

        long end = calendarToday.getTimeInMillis();
        long start = startDay;

        final int difference = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));

        if (difference == 0) {
            return context.getString(R.string.mdl_personal_record_updated_today);
        } else if (difference == 1) {
            return context.getString(R.string.mdl_personal_record_updated_yesterday);
        } else {
            return context.getString(R.string.mdl_personal_record_updated_days_ago, difference);
        }
    }

    public static void showMDLiveHelpAndSupportDialog(final Activity activity) {
        try {
            final WeakReference<Activity> reference = new WeakReference<Activity>(activity);

            if (reference.get() == null) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(reference.get());
            LayoutInflater layoutInflater = LayoutInflater.from(reference.get());
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            TextView alertText = (TextView)view.findViewById(R.id.alertdialogtextview);
            alertText.setText(reference.get().getText(R.string.mdl_call_text));

            builder.setView(view);
            builder.setPositiveButton(reference.get().getText(R.string.mdl_call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + reference.get().getText(R.string.mdl_callnumber)));
                                reference.get().startActivity(intent);

                            } catch (Exception e) {

                            }
                        }
                    });
            builder.setNegativeButton(reference.get().getText(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();

                    } catch (Exception e) {

                    }

                }
            });

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows Add child limit exceded dialog
     */
    public static  void showAddChildExcededDialog(final Activity activity,final String assistPhonenum) {
        try {

            final WeakReference<Activity> reference = new WeakReference<Activity>(activity);

            if (reference.get() == null) {
                return;
            }

            DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + assistPhonenum));
                    reference.get().startActivity(intent);
                    MdliveUtils.startActivityAnimation(reference.get());

                }
            };

            DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            };

            MdliveUtils.showDialog(reference.get(), reference.get().getResources().getString(R.string.mdl_app_name), reference.get().getString(R.string.mdl_plscalAlert_txt, assistPhonenum), StringConstants.ALERT_DISMISS, StringConstants.ALERT_CALLNOW,
                    negativeOnClickListener, positiveOnClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertMiliSeconedsToStringWithTimeZone(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();
        final Format format = new SimpleDateFormat("E. yyyy MM dd HH:mm a");
        return format.format(date) + ", " + "EST";
    }

    /*
    * Get the last visited String
    * */
    public static String getLastVisit(final long milis) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();
        final Format format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(date);
    }
    //Choose Provider Logic Date

    public static String getReceivedTimeForProvider(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));

        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        long diff =   date.getTime()-now.getTime();

        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        Log.d("Set Date", "Diff : " + diffDays);

        if (diffDays ==0) {
            Log.v("Day","Today");
            final Format format = new SimpleDateFormat("H:mm a");
            return "Today "+format.format(date);
        } else if (diffDays == 1) {
            Log.v("Day","Tomorrow");
            final Format format = new SimpleDateFormat("H:mm a");
            return "Tommorrow "+ format.format(date);
        } else if (diffDays >1) {
            Log.v("Day","future");
            final Format format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm a");
            return format.format(date);
        } else {
            Log.v("Day","future");
            final Format format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm a");
            return format.format(date);
        }
    }

    public static String getReceivedSentTime(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));

        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        long diff = now.getTime() - date.getTime();

        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        Log.d("Set Date", "Diff : " + diffDays);

        if (diffDays < 1) {
            final Format format = new SimpleDateFormat("H:mm a");
            return format.format(date).toLowerCase();
        } else if (diffDays == 1) {
            return "Yesterday";
        } else {
            final Format format = new SimpleDateFormat("MM/dd/yyyy");
            return format.format(date);
        }
    }

    public static String getReceivedSentTimeInDetails(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));

        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        final Format format = new SimpleDateFormat("MM/dd/yyyy H:mm a");
        return format.format(date);
    }

    /*
    * Will return 0 if less than 10 minutes
    * Will return 1 if less than 24 hours
    * Will return 2 in other cases.
    * */
    public static int getRemainigTimeToAppointment(final long milis, final String timeZone) {
        final long now = System.currentTimeMillis();

        final Calendar myTime = Calendar.getInstance();
        myTime.setTimeInMillis(now);
        myTime.setTimeZone(TimeZone.getTimeZone("EDT"));


        final Calendar start = Calendar.getInstance();
        start.setTimeInMillis(milis * 1000);

        long difference = start.getTimeInMillis() - myTime.getTimeInMillis();
        long minute = 60 * 1000;
        long tenMinutes = 10 * minute;

        final long oneDay = 24 * 60 * 60 * 1000;

        if (difference < tenMinutes) {
            return 0;
        } else if (difference < oneDay){
            return 1;
        } else {
            return 2;
        }
    }

    public static String getRemainigTimeToAppointmentString(final long milis, final String timeZone) {
        final long now = System.currentTimeMillis();

        final Calendar myTime = Calendar.getInstance();
        myTime.setTimeInMillis(now);
        myTime.setTimeZone(TimeZone.getTimeZone("EST"));


        final Calendar start = Calendar.getInstance();
        start.setTimeInMillis(milis * 1000);

        long difference = start.getTimeInMillis() - myTime.getTimeInMillis();
        long minute = 60 * 1000;

        return "" + (int) difference/minute;
    }

    public static String getFutureAppointmentTime(final long milis, final String timeZone) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));

        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        final Format format1 = new SimpleDateFormat("EEE, dd ");
        final Format format2 = new SimpleDateFormat("HH:mm a z");
        return format1.format(date) + " at " + format2.format(date);
    }

    public static void clearNecessarySharedPrefernces(final Context context) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() == null) {
            return;
        }

        SharedPreferences prefs = reference.get().getSharedPreferences("PREFERENCE", 0);
        prefs.edit().clear().commit();

        prefs = reference.get().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

        prefs = reference.get().getSharedPreferences(PreferenceConstants.USER_BASIC_INFO, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

//      Do not delete PIN/ PASSWORD preference
//        prefs = reference.get().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
//        prefs.edit().clear().commit();
        setFirstTime(context, true);

        prefs = reference.get().getSharedPreferences(PreferenceConstants.SELECTED_USER, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    public static String getLockType(final Context context) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() == null) {
            return "Password";
        }

        final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
        String type = preferences.getString(PreferenceConstants.SIGN_IN + getRemoteUserId(context), context.getString(R.string.mdl_password));
        Log.d("Hello", "Get Type :" + type + ".");
        return type;
    }

    public static void setLockType(final Context context, final String type) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() == null) {
            return;
        }

        final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PreferenceConstants.SIGN_IN + getRemoteUserId(context), type);
        editor.commit();

        Log.d("Hello", "Set Type :" + type + ".");
    }

    public static boolean getFirstTime(final Context context) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() == null) {
            return false;
        }

        final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(PreferenceConstants.FIRST_TIME, true);
        Log.d("Hello", "First Time :" + isFirstTime + ".");
        return isFirstTime;
    }

    public static void setFirstTime(final Context context, final boolean firstTime) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() == null) {
            return;
        }

        final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.PREFFERED_SIGNIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PreferenceConstants.FIRST_TIME, firstTime);
        editor.commit();

        Log.d("Hello", "First Time :" + firstTime + ".");
    }

    public static String getRemoteUserId(final Context context)
    {
        String remoteUserId = null;

        if(MDLiveConfig.IS_SSO){
            remoteUserId = MDLiveConfig.USR_UNIQ_ID==null? AppSpecificConfig.DEFAULT_USER_ID : MDLiveConfig.USR_UNIQ_ID;
            //Log.d("Hello", "RemoteUserId :" +  remoteUserId + ".");
        }
        else {
            final WeakReference<Context> reference = new WeakReference<>(context);

            if (reference.get() == null) {
                return "";
            }

            final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            remoteUserId = preferences.getString(PreferenceConstants.USER_UNIQUE_ID, "");
            //Log.d("Hello", "RemoteUserId :" +  remoteUserId + ".");
        }

        return(remoteUserId);
    }

    public static void clearRemoteUserId(final Context context) {
        final WeakReference<Context> reference = new WeakReference<>(context);

        if (reference.get() != null) {
            final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            preferences.edit().clear().commit();
        }
    }


    public static String getExtention(final String link) {
        try {
            String extension = link.substring(link.lastIndexOf(".") + 1, link.length());
            return extension;
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
    * Check the current build version with Play store version
    * */
    public static boolean isHigherVersionPresent(String storeVersionNo, String bundleVersionNo) {
        String convertedStoreVersion = storeVersionNo.replaceAll("\\.", "");
        String convertedBundleVersion = bundleVersionNo.replaceAll("\\.", "");
        if (convertedStoreVersion.length() > convertedBundleVersion.length()) {
            int diff = convertedStoreVersion.length() - convertedBundleVersion.length();
            for(int i=0; i<diff; i++)
            {
                convertedBundleVersion = convertedBundleVersion + "0";
            }
        } else if (convertedStoreVersion.length() < convertedBundleVersion.length()) {

            int diff = convertedBundleVersion.length() - convertedStoreVersion.length();
            for(int i=0; i<diff; i++)
            {
                convertedStoreVersion = convertedStoreVersion + "0";
            }
        }
        return Integer.parseInt(convertedStoreVersion) >  Integer.parseInt(convertedBundleVersion);
    }

    public static String getSpecialCaseRemovedNumber(String phonumberText){
        phonumberText = phonumberText.replace("(", "");
        phonumberText = phonumberText.replace(")", "");
        phonumberText = phonumberText.replace(" ", "");
        return phonumberText;
    }

    public static String formatDualString(String formatText) {
        boolean hasParenthesis = false;
        Log.v("Raw Format Text",formatText);
        if(formatText.indexOf(")") > 0){
            hasParenthesis = true;
        }
        formatText= formatText.replace("(", "");
        formatText= formatText.replace(")", "");
        formatText= formatText.replace(" ", "");
        formatText= formatText.replace("-", "");
        if(formatText.length() >= 7){
            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, 6)+"-"+formatText.substring(6, formatText.length());
            Log.v("Print format txt",formatText);
        }else if(formatText.length() >= 4){
            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, formatText.length());
            Log.v("Print format txt",">4");
        }else if(formatText.length() == 3 && hasParenthesis){
            Log.v("Print format txt",">3");
            formatText = "("+formatText.substring(0, formatText.length())+")";
        }
        return formatText;
    }
    public static boolean checkIsEmpty(String text){

        if ((text.equalsIgnoreCase("null")) || (text == null)  || (TextUtils.isEmpty(text)) ||(text.trim().length()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveDeviceToken(final Context context, final String deviceToken) {
        final WeakReference<Context> reference = new WeakReference<Context>(context);

        if (reference != null && reference.get() != null) {
            final SharedPreferences sharedPreferences = reference.get().getSharedPreferences(PreferenceConstants.DEVICE_TOKEN, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PreferenceConstants.DEVICE_TOKEN_KEY, deviceToken);
            editor.commit();

            Log.d("Device_TOKEN", deviceToken);
        }
    }

    public static String getDeviceToken(final Context context) {
        final WeakReference<Context> reference = new WeakReference<Context>(context);

        if (reference != null && reference.get() != null) {
            final SharedPreferences sharedPreferences = reference.get().getSharedPreferences(PreferenceConstants.DEVICE_TOKEN, Context.MODE_PRIVATE);
            final String deviceToken = sharedPreferences.getString(PreferenceConstants.DEVICE_TOKEN_KEY, "");
            Log.d("Device_TOKEN", deviceToken);
            return deviceToken;
        } else {
            return "";
        }
    }
}