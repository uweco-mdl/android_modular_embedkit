package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class PharmacyDetails implements Parcelable {
    private int pharmacyId;
    private String phone;
    private String fax;
    private String zipcode;
    private String state;
    private boolean active;
    private String address1;
    private String address2;
    private boolean twentyFourHours;
    private String storeName;
    private Coordinates coordinates;
    private String city;

    /**
     *
     * @return
     * The pharmacyId
     */
    public int getPharmacyId() {
        return pharmacyId;
    }

    /**
     *
     * @param pharmacyId
     * The pharmacy_id
     */
    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
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
    public String getFax() {
        return fax;
    }

    /**
     *
     * @param fax
     * The fax
     */
    public void setFax(String fax) {
        this.fax = fax;
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
     * The active
     */
    public boolean getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * The twentyFourHours
     */
    public boolean getTwentyFourHours() {
        return twentyFourHours;
    }

    /**
     *
     * @param twentyFourHours
     * The twenty_four_hours
     */
    public void setTwentyFourHours(boolean twentyFourHours) {
        this.twentyFourHours = twentyFourHours;
    }

    /**
     *
     * @return
     * The storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     *
     * @param storeName
     * The store_name
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     *
     * @return
     * The coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     *
     * @param coordinates
     * The coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pharmacyId);
        dest.writeString(this.phone);
        dest.writeString(this.fax);
        dest.writeString(this.zipcode);
        dest.writeString(this.state);
        dest.writeByte(active ? (byte) 1 : (byte) 0);
        dest.writeString(this.address1);
        dest.writeString(this.address2);
        dest.writeByte(twentyFourHours ? (byte) 1 : (byte) 0);
        dest.writeString(this.storeName);
        dest.writeParcelable(this.coordinates, 0);
        dest.writeString(this.city);
    }

    public PharmacyDetails() {
    }

    protected PharmacyDetails(Parcel in) {
        this.pharmacyId = in.readInt();
        this.phone = in.readString();
        this.fax = in.readString();
        this.zipcode = in.readString();
        this.state = in.readString();
        this.active = in.readByte() != 0;
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.twentyFourHours = in.readByte() != 0;
        this.storeName = in.readString();
        this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
        this.city = in.readString();
    }

    public static final Parcelable.Creator<PharmacyDetails> CREATOR = new Parcelable.Creator<PharmacyDetails>() {
        public PharmacyDetails createFromParcel(Parcel source) {
            return new PharmacyDetails(source);
        }

        public PharmacyDetails[] newArray(int size) {
            return new PharmacyDetails[size];
        }
    };

    @Override
    public String toString() {
        return "PharmacyDetails{" +
                "pharmacyId=" + pharmacyId +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", state='" + state + '\'' +
                ", active=" + active +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", twentyFourHours=" + twentyFourHours +
                ", storeName='" + storeName + '\'' +
                ", coordinates=" + coordinates +
                ", city='" + city + '\'' +
                '}';
    }

    public static PharmacyDetails fromJSON(final String responseString) {
        try {
            final PharmacyDetails pharmacyDetails = new PharmacyDetails();

            final JSONObject fullJSONObject = new JSONObject(responseString);
            final JSONObject notificationsJSONObject = fullJSONObject.getJSONObject("notifications");
            final JSONObject pharmacyDetailsJSONObject = notificationsJSONObject.getJSONObject("pharmacy_details");

            pharmacyDetails.setZipcode(pharmacyDetailsJSONObject.getString("zipcode"));
            pharmacyDetails.setActive(pharmacyDetailsJSONObject.getBoolean("active"));

            final Coordinates coordinates = new Coordinates();

            final JSONObject coordinatesJSONObject = pharmacyDetailsJSONObject.getJSONObject("coordinates");
            coordinates.setLatitude(coordinatesJSONObject.getDouble("latitude"));
            coordinates.setLongitude(coordinatesJSONObject.getDouble("longitude"));

            pharmacyDetails.setCoordinates(coordinates);

            pharmacyDetails.setStoreName(pharmacyDetailsJSONObject.getString("store_name"));
            pharmacyDetails.setTwentyFourHours(pharmacyDetailsJSONObject.getBoolean("twenty_four_hours"));
            pharmacyDetails.setFax(pharmacyDetailsJSONObject.getString("fax"));
            pharmacyDetails.setCity(pharmacyDetailsJSONObject.getString("city"));
            pharmacyDetails.setAddress1(pharmacyDetailsJSONObject.getString("address1"));
            pharmacyDetails.setPhone(pharmacyDetailsJSONObject.getString("phone"));
            pharmacyDetails.setAddress2(pharmacyDetailsJSONObject.getString("address2"));
            pharmacyDetails.setPharmacyId(pharmacyDetailsJSONObject.getInt("pharmacy_id"));
            pharmacyDetails.setState(pharmacyDetailsJSONObject.getString("state"));


            return pharmacyDetails;
        } catch (JSONException jsonException) {

        }

        return null;
    }
}
