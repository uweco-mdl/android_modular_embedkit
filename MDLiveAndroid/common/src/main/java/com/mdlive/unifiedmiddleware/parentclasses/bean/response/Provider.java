package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class Provider implements Parcelable {
    @Override
    public String toString() {
        return "Provider{" +
                "primaryCarePhysician=" + primaryCarePhysician +
                ", myProviders=" + myProviders +
                '}';
    }

    @SerializedName("primary_care_physician")
    public PrimaryCarePhysician primaryCarePhysician;

    @SerializedName("my_providers")
    @Expose
    public List<MyProvider> myProviders;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.primaryCarePhysician, 0);
        dest.writeTypedList(myProviders);
    }

    public Provider() {
    }

    protected Provider(Parcel in) {
        this.primaryCarePhysician = in.readParcelable(PrimaryCarePhysician.class.getClassLoader());
        this.myProviders = new ArrayList<MyProvider>();
        in.readTypedList(myProviders, MyProvider.CREATOR);
    }

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        public Provider createFromParcel(Parcel source) {
            return new Provider(source);
        }

        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
}
