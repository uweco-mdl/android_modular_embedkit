package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class ReceivedMessages implements Parcelable {
    @Override
    public String toString() {
        return "ReceivedMessages{" +
                "receivedMessages=" + receivedMessages +
                '}';
    }

    @SerializedName("received_messages")
    @Expose
    public List<com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage> receivedMessages;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(receivedMessages);
    }

    public ReceivedMessages() {
    }

    protected ReceivedMessages(Parcel in) {
        this.receivedMessages = in.createTypedArrayList(com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage.CREATOR);
    }

    public static final Creator<ReceivedMessages> CREATOR = new Creator<ReceivedMessages>() {
        public ReceivedMessages createFromParcel(Parcel source) {
            return new ReceivedMessages(source);
        }

        public ReceivedMessages[] newArray(int size) {
            return new ReceivedMessages[size];
        }
    };
}
