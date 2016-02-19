package com.mdlive.embedkit.uilayer.behaviouralhealth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjibkumar_p on 7/24/2015.
 */
public class BehavioralHistory implements Parcelable {
    public BehavioralHistory() {
    }

    @SerializedName("family_hospitalized")
    @Expose
    public String familyHospitalized;
    @SerializedName("hospitalized_duration")
    @Expose
    public String hospitalizedDuration;
    @SerializedName("behavioral_health_reasons")
    @Expose
    public List<ConditionAndActive> behavioralHealthReasons = new ArrayList<ConditionAndActive>();
    @SerializedName("behavioral_family_history")
    @Expose
    public List<ConditionAndActive> behavioralFamilyHistory = new ArrayList<ConditionAndActive>();
    @SerializedName("counseling_preference")
    @Expose
    public String counselingPreference;
    @Expose
    public String hospitalized;
    @SerializedName("hospitalized_date")
    @Expose
    public String hospitalizedDate;
    @SerializedName("behavioral_health_description")
    @Expose
    public String behavioralHealthDescription;
    @SerializedName("behavioral_mconditions")
    @Expose
    public List<ConditionAndActive> behavioralMconditions = new ArrayList<ConditionAndActive>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.familyHospitalized);
        dest.writeString(this.hospitalizedDuration);
        dest.writeTypedList(behavioralHealthReasons);
        dest.writeTypedList(behavioralFamilyHistory);
        dest.writeString(this.counselingPreference);
        dest.writeString(this.hospitalized);
        dest.writeString(this.hospitalizedDate);
        dest.writeString(this.behavioralHealthDescription);
        dest.writeTypedList(behavioralMconditions);
    }

    protected BehavioralHistory(Parcel in) {
        this.familyHospitalized = in.readString();
        this.hospitalizedDuration = in.readString();
        this.behavioralHealthReasons = in.createTypedArrayList(ConditionAndActive.CREATOR);
        this.behavioralFamilyHistory = in.createTypedArrayList(ConditionAndActive.CREATOR);
        this.counselingPreference = in.readString();
        this.hospitalized = in.readString();
        this.hospitalizedDate = in.readString();
        this.behavioralHealthDescription = in.readString();
        this.behavioralMconditions = in.createTypedArrayList(ConditionAndActive.CREATOR);
    }

    public static final Creator<BehavioralHistory> CREATOR = new Creator<BehavioralHistory>() {
        public BehavioralHistory createFromParcel(Parcel source) {
            return new BehavioralHistory(source);
        }

        public BehavioralHistory[] newArray(int size) {
            return new BehavioralHistory[size];
        }
    };

    @Override
    public String toString() {
        return "BehavioralHistory{" +
                "familyHospitalized='" + familyHospitalized + '\'' +
                ", hospitalizedDuration='" + hospitalizedDuration + '\'' +
                ", behavioralHealthReasons=" + behavioralHealthReasons +
                ", behavioralFamilyHistory=" + behavioralFamilyHistory +
                ", counselingPreference='" + counselingPreference + '\'' +
                ", hospitalized='" + hospitalized + '\'' +
                ", hospitalizedDate='" + hospitalizedDate + '\'' +
                ", behavioralHealthDescription='" + behavioralHealthDescription + '\'' +
                ", behavioralMconditions=" + behavioralMconditions +
                '}';
    }
}
