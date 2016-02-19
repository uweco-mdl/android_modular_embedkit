package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class DeepLink implements Parcelable {
    @Expose
    private String platform;
    @Expose
    private String page;
    @SerializedName("registration_url")
    @Expose
    private String registrationUrl;
    @SerializedName("affiliation_id")
    @Expose
    private int affiliationId;
    @Expose
    private String affiliate;
    @SerializedName("deeplink_url")
    @Expose
    private String deeplinkUrl;
    @Expose
    private String username;
    @SerializedName("affiliation_logo_url")
    @Expose
    private String affiliationLogoUrl;

    /**
     *
     * @return
     * The platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     *
     * @param platform
     * The platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     *
     * @return
     * The page
     */
    public String getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The registrationUrl
     */
    public String getRegistrationUrl() {
        return registrationUrl;
    }

    /**
     *
     * @param registrationUrl
     * The registration_url
     */
    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    /**
     *
     * @return
     * The affiliationId
     */
    public int getAffiliationId() {
        return affiliationId;
    }

    /**
     *
     * @param affiliationId
     * The affiliation_id
     */
    public void setAffiliationId(int affiliationId) {
        this.affiliationId = affiliationId;
    }

    /**
     *
     * @return
     * The affiliate
     */
    public String getAffiliate() {
        return affiliate;
    }

    /**
     *
     * @param affiliate
     * The affiliate
     */
    public void setAffiliate(String affiliate) {
        this.affiliate = affiliate;
    }

    /**
     *
     * @return
     * The deeplinkUrl
     */
    public String getDeeplinkUrl() {
        return deeplinkUrl;
    }

    /**
     *
     * @param deeplinkUrl
     * The deeplink_url
     */
    public void setDeeplinkUrl(String deeplinkUrl) {
        this.deeplinkUrl = deeplinkUrl;
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
     * The affiliationLogoUrl
     */
    public String getAffiliationLogoUrl() {
        return affiliationLogoUrl;
    }

    /**
     *
     * @param affiliationLogoUrl
     * The affiliation_logo_url
     */
    public void setAffiliationLogoUrl(String affiliationLogoUrl) {
        this.affiliationLogoUrl = affiliationLogoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.platform);
        dest.writeString(this.page);
        dest.writeString(this.registrationUrl);
        dest.writeInt(this.affiliationId);
        dest.writeString(this.affiliate);
        dest.writeString(this.deeplinkUrl);
        dest.writeString(this.username);
        dest.writeString(this.affiliationLogoUrl);
    }

    public DeepLink() {
    }

    protected DeepLink(Parcel in) {
        this.platform = in.readString();
        this.page = in.readString();
        this.registrationUrl = in.readString();
        this.affiliationId = in.readInt();
        this.affiliate = in.readString();
        this.deeplinkUrl = in.readString();
        this.username = in.readString();
        this.affiliationLogoUrl = in.readString();
    }

    public static final Parcelable.Creator<DeepLink> CREATOR = new Parcelable.Creator<DeepLink>() {
        public DeepLink createFromParcel(Parcel source) {
            return new DeepLink(source);
        }

        public DeepLink[] newArray(int size) {
            return new DeepLink[size];
        }
    };

    @Override
    public String toString() {
        return "DeepLink{" +
                "platform='" + platform + '\'' +
                ", page='" + page + '\'' +
                ", registrationUrl='" + registrationUrl + '\'' +
                ", affiliationId=" + affiliationId +
                ", affiliate='" + affiliate + '\'' +
                ", deeplinkUrl='" + deeplinkUrl + '\'' +
                ", username='" + username + '\'' +
                ", affiliationLogoUrl='" + affiliationLogoUrl + '\'' +
                '}';
    }
}
