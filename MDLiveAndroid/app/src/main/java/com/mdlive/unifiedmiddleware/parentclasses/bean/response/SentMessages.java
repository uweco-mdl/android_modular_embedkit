package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class SentMessages implements Parcelable {
    @Override
    public String toString() {
        return "SentMessages{" +
                "sentMessages=" + sentMessages +
                '}';
    }

    @SerializedName("sent_messages")
    @Expose
    public List<com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage> sentMessages;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(sentMessages);
    }

    public SentMessages() {
    }

    protected SentMessages(Parcel in) {
        this.sentMessages = in.createTypedArrayList(com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage.CREATOR);
    }

    public static final Creator<SentMessages> CREATOR = new Creator<SentMessages>() {
        public SentMessages createFromParcel(Parcel source) {
            return new SentMessages(source);
        }

        public SentMessages[] newArray(int size) {
            return new SentMessages[size];
        }
    };
}
