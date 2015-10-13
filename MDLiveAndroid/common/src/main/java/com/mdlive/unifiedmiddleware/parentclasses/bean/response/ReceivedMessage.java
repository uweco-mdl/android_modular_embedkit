package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class ReceivedMessage implements Parcelable {
    @Override
    public String toString() {
        return "ReceivedMessage{" +
                "time='" + time + '\'' +
                ", readStatus=" + readStatus +
                ", timeZone='" + timeZone + '\'' +
                ", messageId=" + messageId +
                ", providerImageUrl='" + providerImageUrl + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", inMilliseconds=" + inMilliseconds +
                ", providerId=" + providerId +
                ", date='" + date + '\'' +
                '}';
    }

    @Expose
    public String time;
    @SerializedName("read_status")
    @Expose
    public boolean readStatus;
    @SerializedName("time_zone")
    @Expose
    public String timeZone;
    @SerializedName("message_id")
    @Expose
    public int messageId;
    @SerializedName("provider_image_url")
    @Expose
    public String providerImageUrl;
    @Expose
    public String to;
    @Expose
    public String message;
    @Expose
    public String from;
    @Expose
    public String subject;
    @SerializedName("in_milliseconds")
    @Expose
    public long inMilliseconds;
    @SerializedName("provider_id")
    @Expose
    public int providerId;
    @Expose
    public String date;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeByte(readStatus ? (byte) 1 : (byte) 0);
        dest.writeString(this.timeZone);
        dest.writeInt(this.messageId);
        dest.writeString(this.providerImageUrl);
        dest.writeString(this.to);
        dest.writeString(this.message);
        dest.writeString(this.from);
        dest.writeString(this.subject);
        dest.writeLong(this.inMilliseconds);
        dest.writeInt(this.providerId);
        dest.writeString(this.date);
    }

    public ReceivedMessage() {
    }

    protected ReceivedMessage(Parcel in) {
        this.time = in.readString();
        this.readStatus = in.readByte() != 0;
        this.timeZone = in.readString();
        this.messageId = in.readInt();
        this.providerImageUrl = in.readString();
        this.to = in.readString();
        this.message = in.readString();
        this.from = in.readString();
        this.subject = in.readString();
        this.inMilliseconds = in.readLong();
        this.providerId = in.readInt();
        this.date = in.readString();
    }

    public static final Creator<ReceivedMessage> CREATOR = new Creator<ReceivedMessage>() {
        public ReceivedMessage createFromParcel(Parcel source) {
            return new ReceivedMessage(source);
        }

        public ReceivedMessage[] newArray(int size) {
            return new ReceivedMessage[size];
        }
    };
}
