package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOPharmacy implements Parcelable {
    @SerializedName("store_number")
    @Expose
    private String storeNumber;
    @Expose
    private String intersection;
    @Expose
    private String address1;
    @Expose
    private String city;
    @Expose
    private String state;
    @Expose
    private String zipcode;
    @Expose
    private double longitude;
    @Expose
    private double latitude;

    public SSOPharmacy() {
        super();
    }

    /**
     * @return The storeNumber
     */
    public String getStoreNumber() {
        return storeNumber;
    }

    /**
     * @param storeNumber The store_number
     */
    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    /**
     * @return The intersection
     */
    public String getIntersection() {
        return intersection;
    }

    /**
     * @param intersection The intersection
     */
    public void setIntersection(String intersection) {
        this.intersection = intersection;
    }

    /**
     * @return The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * @param zipcode The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * @return The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storeNumber);
        dest.writeString(this.intersection);
        dest.writeString(this.address1);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.zipcode);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    private SSOPharmacy(Parcel in) {
        this.storeNumber = in.readString();
        this.intersection = in.readString();
        this.address1 = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.zipcode = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Creator<SSOPharmacy> CREATOR = new Creator<SSOPharmacy>() {
        public SSOPharmacy createFromParcel(Parcel source) {
            return new SSOPharmacy(source);
        }

        public SSOPharmacy[] newArray(int size) {
            return new SSOPharmacy[size];
        }
    };
}
