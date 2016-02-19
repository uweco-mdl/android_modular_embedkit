package com.mdlive.embedkit.uilayer.lifestyle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanjibkumar_p on 7/20/2015.
 */
public class Model implements Parcelable {
    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        public Model createFromParcel(Parcel source) {
            return new Model(source);
        }

        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public int id;
    public String condition;
    public String active;

    public Model() {
    }

    public Model(int mId, String mMessage, String mActive) {
        this.id = mId;
        this.condition = mMessage;
        this.active = mActive;
    }

    protected Model(Parcel in) {
        this.id = in.readInt();
        this.condition = in.readString();
        this.active = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.condition);
        dest.writeString(this.active);
    }

    @Override
    public String toString() {
//        return '{' +
//                "id:" + id +
//                ", condition:'" + condition + '\'' +
//                ", active:'" + active + '\'' +
//                '}';

        return "{id:" + id
                + ",condition:" + condition
                + ",active:" + active + "}";
    }
}
