package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanjibkumar_p on 7/21/2015.
 */
public class FamilyHistoryModel implements Parcelable {

    public static final String YES = "Yes";
    public static final String NO = "No";

    public String relationship;
    public String active;
    public String condition;


    public static final Creator<FamilyHistoryModel> CREATOR = new Creator<FamilyHistoryModel>() {
        public FamilyHistoryModel createFromParcel(Parcel source) {
            return new FamilyHistoryModel(source);
        }

        public FamilyHistoryModel[] newArray(int size) {
            return new FamilyHistoryModel[size];
        }
    };

    public FamilyHistoryModel() {
    }

    public FamilyHistoryModel(String relationship, String mMessage, String mActive) {
        this.relationship = relationship;
        this.active = mActive;
        this.condition = mMessage;
    }

    protected FamilyHistoryModel(Parcel in) {
        this.relationship = in.readString();
        this.active = in.readString();
        this.condition = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.relationship);
        dest.writeString(this.active);
        dest.writeString(this.condition);
    }

    @Override
    public String toString() {

        return "{relationship:" + relationship
                + ",active:" + active
                + ",condition:" + condition + "}";
    }


    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

}
