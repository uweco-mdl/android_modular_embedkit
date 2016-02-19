package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class SendMessage implements Parcelable {
    @Override
    public String toString() {
        return "SendMessage{" +
                "destinationUserId=" + destinationUserId +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @SerializedName("destination_user_id")
    @Expose
    public String destinationUserId;
    @SerializedName("replied_to_message_id")
    public int repliedToMessageId;
    @Expose
    public String subject;
    @Expose
    public String message;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.destinationUserId);
        dest.writeInt(this.repliedToMessageId);
        dest.writeString(this.subject);
        dest.writeString(this.message);
    }

    public SendMessage() {
    }

    protected SendMessage(Parcel in) {
        this.destinationUserId = in.readString();
        this.repliedToMessageId = in.readInt();
        this.subject = in.readString();
        this.message = in.readString();
    }

    public static final Creator<SendMessage> CREATOR = new Creator<SendMessage>() {
        public SendMessage createFromParcel(Parcel source) {
            return new SendMessage(source);
        }

        public SendMessage[] newArray(int size) {
            return new SendMessage[size];
        }
    };
}
