package com.mdlive.unifiedmiddleware.commonclasses.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneUtils {


    /**
     *
     * Conversion for the Timestamp to corresponding timezone
     *
     */
    public static String getTimeFromTimestamp(String timestamp, Context context) {

        Log.e("Check timestamp", timestamp);
        final Calendar cal = getCalendarWithOffset(context);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        Log.d("Time - ", cal.getTime().toString() + " - ");
        final Date date = cal.getTime();
        final SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        String convertedTime =  format.format(date);
        Log.e("convertedtime", convertedTime);
        return convertedTime;
    }



    /**
     *
     * This will set and return the user specific timezone offset to Calendar object.
     *
     * @param context - Activity Context
     * @return Calendar object with Offset
     */
    public static Calendar getCalendarWithOffset(Context context){
        return Calendar.getInstance(getOffsetTimezone(context));
    }

    /**
     *
     * This will set and return the user specific timezone offset to TimeZone object.
     *
     * @param context - Activity Context
     * @return TimeZone object with offset
     */
    public static TimeZone getOffsetTimezone(Context context){
        if(UserBasicInfo.readFromSharedPreference(context)!=null && UserBasicInfo.readFromSharedPreference(context).getTimezoneOffset()!=null){
            return TimeZone.getTimeZone("GMT" + UserBasicInfo.readFromSharedPreference(context).getTimezoneOffset());
        }
        return TimeZone.getDefault();
    }

    /**
     *
     * Returns the remaining time to appointment minutes
     *
     * @param milis - Time in milliseconds
     * @param timeZone - Timezone
     * @param context - Activity context
     * @return
     */
    public static String getRemainigTimeToAppointmentString(final long milis, final String timeZone, Context context) {
        final long now = System.currentTimeMillis();

        final Calendar myTime = getCalendarWithOffset(context);
        myTime.setTimeInMillis(now);
        final Calendar start = getCalendarWithOffset(context);
        start.setTimeInMillis(milis * 1000);

        long difference = start.getTimeInMillis() - myTime.getTimeInMillis();
        long minute = 60 * 1000;

        return "" + (int) difference/minute;
    }

    public static int daysFromPrefs(Context cxt){
        try {
            SharedPreferences sharedpreferences = cxt.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String age=sharedpreferences.getString(PreferenceConstants.DATE_OF_BIRTH,"");
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            format.setTimeZone(getOffsetTimezone(cxt));
            Date date = format.parse(age);
            return MdliveUtils.calculateDays(date, cxt);
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
            format.setTimeZone(getOffsetTimezone(cxt));
            Date date = format.parse(age);
            return MdliveUtils.calculateMonth(date,cxt);
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
            format.setTimeZone(getOffsetTimezone(cxt));
            Log.e("Date final",age);
            Date date = format.parse(userbasicinfo.getPersonalInfo().getBirthdate());
            return MdliveUtils.calculateAge(date,cxt);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /*
        * Return the long mili seconds for a date which is n years back
         */
    public static long getDateBeforeNumberOfYears(final int numberOfYears, Context context) {
        final Calendar calendar = getCalendarWithOffset(context);
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.YEAR, -numberOfYears);

        return calendar.getTime().getTime();
    }

    public static String convertMiliSeconedsToStringWithTimeZone(final long milis, final String timeZone, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        cal.setTimeInMillis(milis * 1000);
        final Date date = cal.getTime();
        final SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd '\n'HH:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        String timeZoneValue = UserBasicInfo.readFromSharedPreference(context).getPersonalInfo().getTimezone();
        return format.format(date) + " " + timeZoneValue;
    }

    public static String convertMiliSeconedsToStringMonthWithTimeZone(final long milis, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        cal.setTimeInMillis(milis * 1000);
        final Date date = cal.getTime();
        final SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd '\n'HH:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        String timeZoneValue = UserBasicInfo.readFromSharedPreference(context).getPersonalInfo().getTimezone();
        return format.format(date) + " " + timeZoneValue;
    }


    public static String convertMiliSeconedsToDayYearTimeString(final long milis, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        cal.setTimeInMillis(milis * 1000);
        final Date date = cal.getTime();
        final SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd 'at' HH:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        return format.format(date);
    }


    public static String getReceivedTimeForProvider(final long milis, final String timeZone, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        long diff =   date.getTime()-now.getTime();

        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        Log.d("Set Date", "Diff : " + diffDays);

        if (diffDays ==0) {
            Log.e("Day","Today");
            final SimpleDateFormat format = new SimpleDateFormat("H:mm a");
            format.setTimeZone(getOffsetTimezone(context));
            return "Today "+format.format(date);
        } else if (diffDays == 1) {
            Log.e("Day","Tomorrow");
            final SimpleDateFormat format = new SimpleDateFormat("H:mm a");
            format.setTimeZone(getOffsetTimezone(context));
            return "Tommorrow "+ format.format(date);
        } else if (diffDays >1) {
            Log.e("Day","future");
            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm a");
            format.setTimeZone(getOffsetTimezone(context));
            return format.format(date);
        } else {
            Log.e("Day","future");
            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm a");
            format.setTimeZone(getOffsetTimezone(context));
            return format.format(date);
        }
    }

    public static String getReceivedSentTime(final long milis, final String timeZone, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);
        final Date date = cal.getTime();
        long diff = now.getTime() - date.getTime();
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        Log.d("Set Date", "Diff : " + diffDays);

        if (diffDays < 1) {
            final SimpleDateFormat format = new SimpleDateFormat("H:mm a");
            format.setTimeZone(getOffsetTimezone(context));
            return format.format(date).toLowerCase();
        } else if (diffDays == 1) {
            return "Yesterday";
        } else {
            final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            format.setTimeZone(getOffsetTimezone(context));
            return format.format(date);
        }
    }

    public static String getReceivedSentTimeInDetails(final long milis, final String timeZone, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy H:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        return format.format(date);
    }
    public static String ReceivedSentTimeInDetails(final long milis, final String timeZone, Context context) {
        final Calendar cal = getCalendarWithOffset(context);
        final Date now = cal.getTime();
        cal.setTimeInMillis(milis * 1000);

        final Date date = cal.getTime();

        final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy  HH:mm a");
        format.setTimeZone(getOffsetTimezone(context));
        return format.format(date);
    }


    /*
        * Will return 0 if less than 10 minutes
        * Will return 1 if less than 24 hours
        * Will return 2 in other cases.
        * */
    public static int getRemainigTimeToAppointment(final long milis, final String timeZone, Context context) {
        final long now = System.currentTimeMillis();

        final Calendar myTime = getCalendarWithOffset(context);
        myTime.setTimeInMillis(now);

        final Calendar start = getCalendarWithOffset(context);
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

    /**
     *
     * This function will check weather the device tiezone matches teh list of timezones supported
     * by the application. If it is supported, the corresponding timezone abbreviation is returned.
     * If no, then the default timezone is passed(EST).
     *
     * @return - Device timezone
     */
    public static String getDeviceTimeZone(){
        int offset = TimeZone.getDefault().getRawOffset()/(60*1000);
        Log.d("Timezone Offset - ", offset + " ");

        Log.d("Timezone Offsets - ", TimeZone.getDefault().getDSTSavings() + "  :: " + TimeZone.getDefault().getDisplayName() + " :: " +
                TimeZone.getDefault().getRawOffset());

        String timezone;
        switch(offset){
            case -300 : timezone = "EST";break;
            case -360 : timezone = "CST";break;
            case -420 : timezone = "MST";break;
            case -480 : timezone = "PST";break;
            case -540 : timezone = "AKST";break;
            case -600 : timezone = "HST";break;
            case -660 : timezone = "AMS";break;
            case  720 : timezone = "MIT";break;
            case  600 : timezone = "GST";break;
            case  540 : timezone = "PAT";break;
            default : timezone = "EST";break;
        }
        Log.d("Timezone Offset - ", timezone + " ");
        return timezone;
    }

}