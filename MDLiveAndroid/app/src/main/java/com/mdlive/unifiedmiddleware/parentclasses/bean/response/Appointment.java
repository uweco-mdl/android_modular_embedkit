package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 8/16/2015.
 */
public class Appointment implements Parcelable {
    @SerializedName("in_milliseconds")
    @Expose
    private long inMilliseconds;
    @SerializedName("physician_name")
    @Expose
    private String physicianName;
    @Expose
    private int id;
    @SerializedName("appt_type")
    @Expose
    private String apptType;
    @SerializedName("time_zone")
    @Expose
    private String timeZone;
    @SerializedName("physician_image_url")
    @Expose
    private String physicianImageUrl;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("chief_complaint")
    @Expose
    private String chiefComplaint;
    @Expose
    private String role;
    @SerializedName("provider_id")
    @Expose
    private int providerId;

    private String stringID;

    /**
     *
     * @return
     * The inMilliseconds
     */
    public long getInMilliseconds() {
        return inMilliseconds;
    }

    /**
     *
     * @param inMilliseconds
     * The in_milliseconds
     */
    public void setInMilliseconds(int inMilliseconds) {
        this.inMilliseconds = inMilliseconds;
    }

    /**
     *
     * @return
     * The physicianName
     */
    public String getPhysicianName() {
        return physicianName;
    }

    /**
     *
     * @param physicianName
     * The physician_name
     */
    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The apptType
     */
    public String getApptType() {
        return apptType;
    }

    /**
     *
     * @param apptType
     * The appt_type
     */
    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    /**
     *
     * @return
     * The timeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     *
     * @param timeZone
     * The time_zone
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     *
     * @return
     * The physicianImageUrl
     */
    public String getPhysicianImageUrl() {
        return physicianImageUrl;
    }

    /**
     *
     * @param physicianImageUrl
     * The physician_image_url
     */
    public void setPhysicianImageUrl(String physicianImageUrl) {
        this.physicianImageUrl = physicianImageUrl;
    }

    /**
     *
     * @return
     * The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return
     * The chiefComplaint
     */
    public String getChiefComplaint() {
        return chiefComplaint;
    }

    /**
     *
     * @param chiefComplaint
     * The chief_complaint
     */
    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    /**
     *
     * @return
     * The role
     */
    public String getRole() {
        return role;
    }

    /**
     *
     * @param role
     * The role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     *
     * @return
     * The providerId
     */
    public int getProviderId() {
        return providerId;
    }

    /**
     *
     * @param providerId
     * The provider_id
     */
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.inMilliseconds);
        dest.writeString(this.physicianName);
        dest.writeInt(this.id);
        dest.writeString(this.apptType);
        dest.writeString(this.timeZone);
        dest.writeString(this.physicianImageUrl);
        dest.writeString(this.startTime);
        dest.writeString(this.chiefComplaint);
        dest.writeString(this.role);
        dest.writeInt(this.providerId);
        dest.writeString(this.stringID);
    }

    public Appointment() {
    }

    protected Appointment(Parcel in) {
        this.inMilliseconds = in.readLong();
        this.physicianName = in.readString();
        this.id = in.readInt();
        this.apptType = in.readString();
        this.timeZone = in.readString();
        this.physicianImageUrl = in.readString();
        this.startTime = in.readString();
        this.chiefComplaint = in.readString();
        this.role = in.readString();
        this.providerId = in.readInt();
        this.stringID = in.readString();
    }

    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getStringID() {
        return stringID;
    }

    public void setStringID(String stringID) {
        this.stringID = stringID;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "inMilliseconds=" + inMilliseconds +
                ", physicianName='" + physicianName + '\'' +
                ", id=" + id +
                ", apptType='" + apptType + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", physicianImageUrl='" + physicianImageUrl + '\'' +
                ", startTime='" + startTime + '\'' +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", role='" + role + '\'' +
                ", providerId=" + providerId +
                '}';
    }
}
