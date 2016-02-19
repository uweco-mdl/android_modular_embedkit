package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MyProvider implements Parcelable {
    @Override
    public String toString() {
        return "MyProvider{" +
                "nextAppointment='" + nextAppointment + '\'' +
                ", lastAppointment=" + lastAppointment +
                ", speciality='" + speciality + '\'' +
                ", name='" + name + '\'' +
                ", providerImageUrl='" + providerImageUrl + '\'' +
                ", providerId=" + providerId +
                ", providerSince='" + providerSince + '\'' +
                '}';
    }

    @SerializedName("next_appointment")
    @Expose
    public String nextAppointment;
    @SerializedName("last_appointment")
    @Expose
    public long lastAppointment;
    @Expose
    public String speciality;
    @Expose
    public String name;
    @SerializedName("provider_image_url")
    @Expose
    public String providerImageUrl;
    @SerializedName("provider_id")
    @Expose
    public int providerId;
    @SerializedName("provider_since")
    @Expose
    public String providerSince;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nextAppointment);
        dest.writeLong(this.lastAppointment);
        dest.writeString(this.speciality);
        dest.writeString(this.name);
        dest.writeString(this.providerImageUrl);
        dest.writeInt(this.providerId);
        dest.writeString(this.providerSince);
    }

    public MyProvider() {
    }

    protected MyProvider(Parcel in) {
        this.nextAppointment = in.readString();
        this.lastAppointment = in.readLong();
        this.speciality = in.readString();
        this.name = in.readString();
        this.providerImageUrl = in.readString();
        this.providerId = in.readInt();
        this.providerSince = in.readString();
    }

    public static final Creator<MyProvider> CREATOR = new Creator<MyProvider>() {
        public MyProvider createFromParcel(Parcel source) {
            return new MyProvider(source);
        }

        public MyProvider[] newArray(int size) {
            return new MyProvider[size];
        }
    };
}
