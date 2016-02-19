package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class Record implements Parcelable {
    @SerializedName("uploaded_by")
    @Expose
    public String uploadedBy;
    @Expose
    public int id;
    @SerializedName("download_link")
    @Expose
    public String downloadLink;
    @SerializedName("doc_type")
    @Expose
    public String docType;
    @SerializedName("uploaded_at")
    @Expose
    public String uploadedAt;
    @SerializedName("doc_name")
    @Expose
    public String docName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uploadedBy);
        dest.writeInt(this.id);
        dest.writeString(this.downloadLink);
        dest.writeString(this.docType);
        dest.writeString(this.uploadedAt);
        dest.writeString(this.docName);
    }

    public Record() {
    }

    protected Record(Parcel in) {
        this.uploadedBy = in.readString();
        this.id = in.readInt();
        this.downloadLink = in.readString();
        this.docType = in.readString();
        this.uploadedAt = in.readString();
        this.docName = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
