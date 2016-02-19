package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class SentMessage implements Parcelable {
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

    public SentMessage() {
    }

    protected SentMessage(Parcel in) {
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

    public static final Creator<SentMessage> CREATOR = new Creator<SentMessage>() {
        public SentMessage createFromParcel(Parcel source) {
            return new SentMessage(source);
        }

        public SentMessage[] newArray(int size) {
            return new SentMessage[size];
        }
    };
}
