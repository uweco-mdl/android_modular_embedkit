package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class PersonalInfo implements Parcelable {
    @SerializedName("email_confirmed")
    @Expose
    private boolean emailConfirmed;
    @Expose
    private String phone;
    @Expose
    private Object fax;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @Expose
    private String cell;
    @Expose
    private String zipcode;
    @Expose
    private String state;
    @SerializedName("emergency_contact_number")
    @Expose
    private String emergencyContactNumber;
    @Expose
    private String address1;
    @Expose
    private String address2;

    private Security security;
    @Expose
    private String country;
    @Expose
    private String city;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @Expose
    private String username;
    @Expose
    private String timezone;
    @SerializedName("language_preference")
    @Expose
    private String languagePreference;
    @SerializedName("consult_method")
    @Expose
    private String consultMethod;
    @Expose
    private String email;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("work_phone")
    @Expose
    private String workPhone;
    @Expose
    private String birthdate;
    @Expose
    private String gender;
    @SerializedName("do_you_have_primary_care_physician")
    @Expose
    private String doYouHavePrimaryCarePhysician;

    /**
     *
     * @return
     * The emailConfirmed
     */
    public boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    /**
     *
     * @param emailConfirmed
     * The email_confirmed
     */
    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
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
     * The fax
     */
    public Object getFax() {
        return fax;
    }

    /**
     *
     * @param fax
     * The fax
     */
    public void setFax(Object fax) {
        this.fax = fax;
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
     * The cell
     */
    public String getCell() {
        return cell;
    }

    /**
     *
     * @param cell
     * The cell
     */
    public void setCell(String cell) {
        this.cell = cell;
    }

    /**
     *
     * @return
     * The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     *
     * @param zipcode
     * The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The emergencyContactNumber
     */
    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    /**
     *
     * @param emergencyContactNumber
     * The emergency_contact_number
     */
    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
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
     * The security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     *
     * @param security
     * The security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
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
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     *
     * @param timezone
     * The timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     *
     * @return
     * The languagePreference
     */
    public String getLanguagePreference() {
        return languagePreference;
    }

    /**
     *
     * @param languagePreference
     * The language_preference
     */
    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    /**
     *
     * @return
     * The consultMethod
     */
    public String getConsultMethod() {
        return consultMethod;
    }

    /**
     *
     * @param consultMethod
     * The consult_method
     */
    public void setConsultMethod(String consultMethod) {
        this.consultMethod = consultMethod;
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
     * The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     *
     * @param imageUrl
     * The image_url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
     * The workPhone
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     *
     * @param workPhone
     * The work_phone
     */
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
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
     * The doYouHavePrimaryCarePhysician
     */
    public String getDoYouHavePrimaryCarePhysician() {
        return doYouHavePrimaryCarePhysician;
    }

    /**
     *
     * @param doYouHavePrimaryCarePhysician
     * The do_you_have_primary_care_physician
     */
    public void setDoYouHavePrimaryCarePhysician(String doYouHavePrimaryCarePhysician) {
        this.doYouHavePrimaryCarePhysician = doYouHavePrimaryCarePhysician;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(emailConfirmed ? (byte) 1 : (byte) 0);
        dest.writeString(this.phone);
        //dest.writeParcelable(this.fax, flags);
        dest.writeString(this.middleName);
        dest.writeString(this.cell);
        dest.writeString(this.zipcode);
        dest.writeString(this.state);
        dest.writeString(this.emergencyContactNumber);
        dest.writeString(this.address1);
        dest.writeString(this.address2);
        dest.writeParcelable(this.security, flags);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeString(this.firstName);
        dest.writeString(this.username);
        dest.writeString(this.timezone);
        dest.writeString(this.languagePreference);
        dest.writeString(this.consultMethod);
        dest.writeString(this.email);
        dest.writeString(this.imageUrl);
        dest.writeString(this.lastName);
        dest.writeString(this.workPhone);
        dest.writeString(this.birthdate);
        dest.writeString(this.gender);
        dest.writeString(this.doYouHavePrimaryCarePhysician);
    }

    public PersonalInfo() {
    }

    protected PersonalInfo(Parcel in) {
        this.emailConfirmed = in.readByte() != 0;
        this.phone = in.readString();
        //this.fax = in.readParcelable(Object.class.getClassLoader());
        this.middleName = in.readString();
        this.cell = in.readString();
        this.zipcode = in.readString();
        this.state = in.readString();
        this.emergencyContactNumber = in.readString();
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.security = in.readParcelable(Security.class.getClassLoader());
        this.country = in.readString();
        this.city = in.readString();
        this.firstName = in.readString();
        this.username = in.readString();
        this.timezone = in.readString();
        this.languagePreference = in.readString();
        this.consultMethod = in.readString();
        this.email = in.readString();
        this.imageUrl = in.readString();
        this.lastName = in.readString();
        this.workPhone = in.readString();
        this.birthdate = in.readString();
        this.gender = in.readString();
        this.doYouHavePrimaryCarePhysician = in.readString();
    }

    public static final Parcelable.Creator<PersonalInfo> CREATOR = new Parcelable.Creator<PersonalInfo>() {
        public PersonalInfo createFromParcel(Parcel source) {
            return new PersonalInfo(source);
        }

        public PersonalInfo[] newArray(int size) {
            return new PersonalInfo[size];
        }
    };

    @Override
    public String toString() {
        return "PersonalInfo{" +
                "emailConfirmed=" + emailConfirmed +
                ", phone='" + phone + '\'' +
                ", fax=" + fax +
                ", middleName='" + middleName + '\'' +
                ", cell='" + cell + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", state='" + state + '\'' +
                ", emergencyContactNumber='" + emergencyContactNumber + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", security=" + security.toString() +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", timezone='" + timezone + '\'' +
                ", languagePreference='" + languagePreference + '\'' +
                ", consultMethod='" + consultMethod + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", lastName='" + lastName + '\'' +
                ", workPhone='" + workPhone + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", gender='" + gender + '\'' +
                ", doYouHavePrimaryCarePhysician='" + doYouHavePrimaryCarePhysician + '\'' +
                '}';
    }
}
