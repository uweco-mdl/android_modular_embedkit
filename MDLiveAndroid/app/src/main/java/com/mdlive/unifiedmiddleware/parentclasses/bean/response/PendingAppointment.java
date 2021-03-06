package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhiman_da on 8/16/2015.
 */
public class PendingAppointment implements Parcelable {
    private List<OncallAppointment> oncallAppointments = new ArrayList<OncallAppointment>();
    @Expose
    private List<Appointment> appointments = new ArrayList<Appointment>();

    /**
     *
     * @return
     * The oncallAppointments
     */
    public List<OncallAppointment> getOncallAppointments() {
        return oncallAppointments;
    }

    /**
     *
     * @param oncallAppointments
     * The oncall_appointments
     */
    public void setOncallAppointments(List<OncallAppointment> oncallAppointments) {
        this.oncallAppointments = oncallAppointments;
    }

    /**
     *
     * @return
     * The appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     *
     * @param appointments
     * The appointments
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(oncallAppointments);
        dest.writeTypedList(appointments);
    }

    public PendingAppointment() {
    }

    protected PendingAppointment(Parcel in) {
        this.oncallAppointments = in.createTypedArrayList(OncallAppointment.CREATOR);
        this.appointments = in.createTypedArrayList(Appointment.CREATOR);
    }

    public static final Parcelable.Creator<PendingAppointment> CREATOR = new Parcelable.Creator<PendingAppointment>() {
        public PendingAppointment createFromParcel(Parcel source) {
            return new PendingAppointment(source);
        }

        public PendingAppointment[] newArray(int size) {
            return new PendingAppointment[size];
        }
    };

    public String toJsonString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PendingAppointment fromJsonString(final String jsonString) {
        //final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        //return gson.fromJson(jsonString, PendingAppointment.class);
        final PendingAppointment pendingAppointment = new PendingAppointment();

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            final JSONArray appointmentArray = jsonObject.optJSONArray("appointments");
            final JSONArray onCallAppointmentArray = jsonObject.optJSONArray("oncall_appointments");



            if (appointmentArray != null && appointmentArray.length() > 0) {
                for (int i = 0; i < appointmentArray.length(); i++) {
                    try {
                        Class clazz = Class.forName("com.mdlive.sav.MDLiveChooseProvider");
                        Field isDoctorOnCall = clazz.getDeclaredField("isDoctorOnCall");
                        Field isDoctorOnVideo = clazz.getDeclaredField("isDoctorOnVideo");
                        isDoctorOnCall.set(null, false);
                        isDoctorOnVideo.set(null, false);
                    } catch (ClassNotFoundException e){
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    final  JSONObject appointment = appointmentArray.optJSONObject(i);
                    final Appointment appo = new Appointment();

                    appo.setApptType(appointment.optString("appt_type"));
                    appo.setTimeZone(appointment.optString("time_zone"));
                    try {
                        appo.setId(appointment.getInt("id"));
                    } catch (JSONException e) {
                        appo.setStringID(appointment.optString("id"));
                    }

                    appo.setRole(appointment.optString("role"));
                    appo.setProviderId(appointment.optInt("provider_id"));
                    appo.setPhysicianName(appointment.optString("physician_name"));
                    appo.setPhysicianImageUrl(appointment.optString("physician_image_url"));
                    appo.setChiefComplaint(appointment.optString("chief_complaint"));
                    appo.setInMilliseconds(appointment.optInt("in_milliseconds"));
                    appo.setStartTime(appointment.optString("start_time"));

                    pendingAppointment.appointments.add(appo);
                }
            }
            if (onCallAppointmentArray != null && onCallAppointmentArray.length() > 0) {
                    for (int i = 0; i < onCallAppointmentArray.length(); i++) {
                        Log.v("Cominmg","Coming here");
                        try {
                            Class clazz = Class.forName("com.mdlive.sav.MDLiveChooseProvider");
                            Field isDoctorOnCall = clazz.getDeclaredField("isDoctorOnCall");
                            Field isDoctorOnVideo = clazz.getDeclaredField("isDoctorOnVideo");
                            isDoctorOnCall.set(null, false);
                            isDoctorOnVideo.set(null, true);
                        } catch (ClassNotFoundException e){
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        final  JSONObject onCallAppointment = onCallAppointmentArray.optJSONObject(i);
                        final OncallAppointment appo = new OncallAppointment();
                        appo.setApptType(onCallAppointment.optString("appt_type"));
                        appo.setCustomerCallInNumber(onCallAppointment.optString("customer_call_in_number"));
                        try {
                            appo.setId(onCallAppointment.getString("id") + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pendingAppointment.oncallAppointments.add(appo);
                    }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pendingAppointment;
    }

    public void saveToSharedPreference(final Context context , final String jsonString) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);

        if (weakReference != null && weakReference.get() != null) {
            final SharedPreferences preferences = weakReference.get().getSharedPreferences(PreferenceConstants.PENDING_APPOINMENT, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();

            editor.putString(PreferenceConstants.APPOINMENT, jsonString);
            editor.commit();
        }
    }

    public static PendingAppointment readFromSharedPreference(final Context context) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);

        if (weakReference != null && weakReference.get() != null) {
            final SharedPreferences preferences = weakReference.get().getSharedPreferences(PreferenceConstants.PENDING_APPOINMENT, Context.MODE_PRIVATE);

            final String savedString = preferences.getString(PreferenceConstants.APPOINMENT, null);

            if (savedString != null) {
                return PendingAppointment.fromJsonString(savedString);
            } else {
                return null;
            }
        }

        return null;
    }
}
