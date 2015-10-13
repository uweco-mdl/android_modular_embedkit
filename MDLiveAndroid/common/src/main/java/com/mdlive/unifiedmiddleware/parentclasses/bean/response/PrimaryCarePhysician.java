package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class PrimaryCarePhysician implements Parcelable {
    @Expose
    public String prefix;
    @Expose
    public String phone;
    @Expose
    public String country;
    @Expose
    public String fax;
    @Expose
    public String practice;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @Expose
    public String cell;
    @Expose
    public String stateprov;
    @Expose
    public String state;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @Expose
    public String zip;
    @Expose
    public String email;
    @SerializedName("middle_name")
    @Expose
    public String middleName;
    @Expose
    public String suffix;
    @Expose
    public String address2;
    @Expose
    public String city;
    @Expose
    public String address1;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.prefix);
        dest.writeString(this.phone);
        dest.writeString(this.country);
        dest.writeString(this.fax);
        dest.writeString(this.practice);
        dest.writeString(this.lastName);
        dest.writeString(this.cell);
        dest.writeString(this.stateprov);
        dest.writeString(this.state);
        dest.writeString(this.firstName);
        dest.writeString(this.zip);
        dest.writeString(this.email);
        dest.writeString(this.middleName);
        dest.writeString(this.suffix);
        dest.writeString(this.address2);
        dest.writeString(this.city);
        dest.writeString(this.address1);
    }

    public PrimaryCarePhysician() {
    }

    protected PrimaryCarePhysician(Parcel in) {
        this.prefix = in.readString();
        this.phone = in.readString();
        this.country = in.readString();
        this.fax = in.readString();
        this.practice = in.readString();
        this.lastName = in.readString();
        this.cell = in.readString();
        this.stateprov = in.readString();
        this.state = in.readString();
        this.firstName = in.readString();
        this.zip = in.readString();
        this.email = in.readString();
        this.middleName = in.readString();
        this.suffix = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.address1 = in.readString();
    }

    public static final Creator<PrimaryCarePhysician> CREATOR = new Creator<PrimaryCarePhysician>() {
        public PrimaryCarePhysician createFromParcel(Parcel source) {
            return new PrimaryCarePhysician(source);
        }

        public PrimaryCarePhysician[] newArray(int size) {
            return new PrimaryCarePhysician[size];
        }
    };
}
