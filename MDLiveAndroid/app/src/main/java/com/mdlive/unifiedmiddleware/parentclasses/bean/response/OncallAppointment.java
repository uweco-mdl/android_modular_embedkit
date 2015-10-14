package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 8/16/2015.
 */
public class OncallAppointment implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customer_call_in_number")
    @Expose
    private String customerCallInNumber;
    @SerializedName("appt_type")
    @Expose
    private String apptType;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The customerCallInNumber
     */
    public String getCustomerCallInNumber() {
        return customerCallInNumber;
    }

    /**
     *
     * @param customerCallInNumber
     * The customer_call_in_number
     */
    public void setCustomerCallInNumber(String customerCallInNumber) {
        this.customerCallInNumber = customerCallInNumber;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.customerCallInNumber);
        dest.writeString(this.apptType);
    }

    public OncallAppointment() {
    }

    protected OncallAppointment(Parcel in) {
        this.id = in.readString();
        this.customerCallInNumber = in.readString();
        this.apptType = in.readString();
    }

    public static final Parcelable.Creator<OncallAppointment> CREATOR = new Parcelable.Creator<OncallAppointment>() {
        public OncallAppointment createFromParcel(Parcel source) {
            return new OncallAppointment(source);
        }

        public OncallAppointment[] newArray(int size) {
            return new OncallAppointment[size];
        }
    };
}
