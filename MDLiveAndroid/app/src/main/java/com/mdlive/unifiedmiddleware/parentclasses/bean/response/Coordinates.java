package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class Coordinates implements Parcelable {
    @Expose
    private double longitude;
    @Expose
    private double latitude;

    /**
     *
     * @return
     * The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
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
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public Coordinates() {
    }

    protected Coordinates(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Parcelable.Creator<Coordinates> CREATOR = new Parcelable.Creator<Coordinates>() {
        public Coordinates createFromParcel(Parcel source) {
            return new Coordinates(source);
        }

        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
}
