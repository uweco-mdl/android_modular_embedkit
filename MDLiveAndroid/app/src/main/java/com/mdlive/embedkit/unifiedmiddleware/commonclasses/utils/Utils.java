package com.mdlive.embedkit.unifiedmiddleware.commonclasses.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Unnikrishnan B on 03/04/15.
 */
public class Utils {
    private static final AtomicInteger S_NEXT_GENERATED_ID = new AtomicInteger(1);
    private Utils(){
        // this class cannot be directly instantiated externally
    }

    /**
     *
     * Validation for Email
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
        return (value.length()>6)?true:false;
    }

    /**
     *
     * Validation for password
     * @param value
     * @return
     */
    public final static boolean isValidPassword(String value) {
        return (value.length()>6)?true:false;
    }


    public static void connectionTimeoutError(ProgressDialog pDialog, final Context context) {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                showDialog(context,context.getApplicationInfo().loadLabel(context.getPackageManager()).toString(),"Please check your internet connection.", "OK",null,null,null);
            }
        });
    }
    public static void alert(ProgressDialog pDialog, final Context context, final String message) {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                showDialog(context,context.getApplicationInfo().loadLabel(context.getPackageManager()).toString(),message, "OK",null,null,null);
            }
        });
    }


    public static void showDialog(final Context context, String title, String message, String positiveBtn, String negativeBtn,
                                  DialogInterface.OnClickListener positiveOnclickListener, DialogInterface.OnClickListener negativeOnclickListener) {
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
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(positiveBtn,positiveOnclickListener);
        if(negativeBtn!=null && negativeOnclickListener != null) {
            alertDialogBuilder.setNegativeButton(negativeBtn,negativeOnclickListener);
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    public static ProgressDialog getProgressDialog(String message, Activity context) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage(message);
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


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public static void showGPSSettingsAlert(final Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
