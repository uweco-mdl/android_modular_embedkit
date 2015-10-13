package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class Notifications implements Parcelable {
    private PharmacyDetails pharmacyDetails;
    @SerializedName("upcoming_appointments")
    @Expose
    private int upcomingAppointments;
    @Expose
    private int messages;

    /**
     * @return The pharmacyDetails
     */
    public PharmacyDetails getPharmacyDetails() {
        return pharmacyDetails;
    }

    /**
     * @param pharmacyDetails The pharmacy_details
     */
    public void setPharmacyDetails(PharmacyDetails pharmacyDetails) {
        this.pharmacyDetails = pharmacyDetails;
    }

    /**
     * @return The upcomingAppointments
     */
    public int getUpcomingAppointments() {
        return upcomingAppointments;
    }

    /**
     * @param upcomingAppointments The upcoming_appointments
     */
    public void setUpcomingAppointments(int upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    /**
     * @return The messages
     */
    public int getMessages() {
        return messages;
    }

    /**
     * @param messages The messages
     */
    public void setMessages(int messages) {
        this.messages = messages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pharmacyDetails, flags);
        dest.writeInt(this.upcomingAppointments);
        dest.writeInt(this.messages);
    }

    public Notifications() {
    }

    protected Notifications(Parcel in) {
        this.pharmacyDetails = in.readParcelable(PharmacyDetails.class.getClassLoader());
        this.upcomingAppointments = in.readInt();
        this.messages = in.readInt();
    }

    public static final Parcelable.Creator<Notifications> CREATOR = new Parcelable.Creator<Notifications>() {
        public Notifications createFromParcel(Parcel source) {
            return new Notifications(source);
        }

        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    @Override
    public String toString() {
        return "Notifications{" +
                "pharmacyDetails=" + pharmacyDetails +
                ", upcomingAppointments=" + upcomingAppointments +
                ", messages=" + messages +
                '}';
    }

    public static Notifications fromJSON(final String responseString) {
        try {
            final Notifications notifications = new Notifications();

            final JSONObject fullJSONObject = new JSONObject(responseString);
            final JSONObject notificationsJSONObject = fullJSONObject.getJSONObject("notifications");

            notifications.setPharmacyDetails(PharmacyDetails.fromJSON(responseString));
            notifications.setMessages(notificationsJSONObject.optInt("messages"));
            notifications.setUpcomingAppointments(notificationsJSONObject.optInt("upcoming_appointments"));
        } catch (JSONException jsonException) {

        }

        return null;
    }
}
