package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class CustomerDocument implements Parcelable {
    @SerializedName("document_type_id")
    @Expose
    public int documentTypeId;
    @SerializedName("file_name")
    @Expose
    public String fileName;
    @Expose
    public String document;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.documentTypeId);
        dest.writeString(this.fileName);
        dest.writeString(this.document);
    }

    public CustomerDocument() {
    }

    protected CustomerDocument(Parcel in) {
        this.documentTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fileName = in.readString();
        this.document = in.readString();
    }

    public static final Creator<CustomerDocument> CREATOR = new Creator<CustomerDocument>() {
        public CustomerDocument createFromParcel(Parcel source) {
            return new CustomerDocument(source);
        }

        public CustomerDocument[] newArray(int size) {
            return new CustomerDocument[size];
        }
    };
}
