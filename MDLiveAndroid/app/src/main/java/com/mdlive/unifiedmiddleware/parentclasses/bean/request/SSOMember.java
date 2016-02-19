package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOMember implements Parcelable {
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @Expose
    private String gender;
    @Expose
    private String email;
    @Expose
    private String phone;
    @Expose
    private String address1;
    @Expose
    private String address2;
    @Expose
    private String city;
    @Expose
    private String zip;
    @Expose
    private String birthdate;
    @SerializedName("state_id")
    @Expose
    private String stateId;

    public SSOMember() {
        super();
    }

    private SSOMember(Parcel in) {
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.gender = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.zip = in.readString();
        this.birthdate = in.readString();
        this.stateId = in.readString();
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     *
     * @param middleName
     * The middle_name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     *
     * @param address1
     * The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     *
     * @return
     * The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     *
     * @param address2
     * The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The zip
     */
    public String getZip() {
        return zip;
    }

    /**
     *
     * @param zip
     * The zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     *
     * @return
     * The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     *
     * @param birthdate
     * The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     *
     * @return
     * The stateId
     */
    public String getStateId() {
        return stateId;
    }

    /**
     *
     * @param stateId
     * The state_id
     */
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.middleName);
        dest.writeString(this.lastName);
        dest.writeString(this.gender);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.address1);
        dest.writeString(this.address2);
        dest.writeString(this.city);
        dest.writeString(this.zip);
        dest.writeString(this.birthdate);
        dest.writeString(this.stateId);
    }

    public static final Creator<SSOMember> CREATOR = new Creator<SSOMember>() {
        public SSOMember createFromParcel(Parcel source) {
            return new SSOMember(source);
        }

        public SSOMember[] newArray(int size) {
            return new SSOMember[size];
        }
    };
}
