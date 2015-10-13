package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class Records implements Parcelable {
    @SerializedName("records")
    @Expose
    public List<com.mdlive.unifiedmiddleware.parentclasses.bean.response.Record> records;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(records);
    }

    public Records() {
    }

    protected Records(Parcel in) {
        this.records = in.createTypedArrayList(com.mdlive.unifiedmiddleware.parentclasses.bean.response.Record.CREATOR);
    }

    public static final Creator<Records> CREATOR = new Creator<Records>() {
        public Records createFromParcel(Parcel source) {
            return new Records(source);
        }

        public Records[] newArray(int size) {
            return new Records[size];
        }
    };
}
