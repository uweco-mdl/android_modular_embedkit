package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by sanjibkumar_p on 7/24/2015.
 */
public class ConditionAndActive implements Parcelable {
    public static final String YES = "Yes";
    public static final String NO = "No";

    public ConditionAndActive(String condition, String active) {
        this.condition = condition;
        this.active = active;
    }

    @Expose
    public String condition;
    @Expose
    public String active;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.condition);
        dest.writeString(this.active);
    }

    public ConditionAndActive() {
    }

    protected ConditionAndActive(Parcel in) {
        this.condition = in.readString();
        this.active = in.readString();
    }

    public static final Creator<ConditionAndActive> CREATOR = new Creator<ConditionAndActive>() {
        public ConditionAndActive createFromParcel(Parcel source) {
            return new ConditionAndActive(source);
        }

        public ConditionAndActive[] newArray(int size) {
            return new ConditionAndActive[size];
        }
    };

    @Override
    public String toString() {
        return "ConditionAndActive{" +
                "condition='" + condition + '\'' +
                ", active='" + active + '\'' +
                '}';
    }
}
