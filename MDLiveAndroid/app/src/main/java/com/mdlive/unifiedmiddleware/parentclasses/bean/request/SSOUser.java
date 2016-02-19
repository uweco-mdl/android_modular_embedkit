package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SSOMember;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SSOPharmacy;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOUser implements Parcelable {
    public static final String SSO_USER = "sso_user";

    @SerializedName("member")
    @Expose
    private SSOMember ssoMember;
    @SerializedName("unique_id")
    @Expose
    private String uniqueId;
    @SerializedName("environment")
    @Expose
    private int currentEnvironment;
    @SerializedName("pharmacy")
    @Expose
    private SSOPharmacy ssoPharmacy;

    private String parentPackagename;

    private String parentClassname;

    public SSOUser() {
        super();
    }

    private SSOUser(Parcel in) {
        this.ssoMember = in.readParcelable(SSOMember.class.getClassLoader());
        this.uniqueId = in.readString();
        this.ssoPharmacy = in.readParcelable(SSOPharmacy.class.getClassLoader());
        this.parentPackagename = in.readString();
        this.parentClassname = in.readString();
        this.currentEnvironment = in.readInt();
    }

    public void setparentPackagename(String parentPackagename){
        this.parentPackagename = parentPackagename;
    }

    public String getparentPackagename(){
        return parentPackagename;
    }

    public void setparentClassname(String parentClassname){
        this.parentClassname = parentClassname;
    }

    public String getparentClassname(){
        return parentClassname;
    }

    /**
     *
     * @param ssoMember
     * The member
     */
    public void setSsoMember(SSOMember ssoMember) {
        this.ssoMember = ssoMember;
    }

    /**
     *
     * @param uniqueId
     * The unique_id
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     *
     * @param currentEnvironment
     * Embed Kit environment setup (dev, stage, qa, prod)
     */
    public void setCurrentEnvironment(String currentEnvironment){
        if(currentEnvironment.equalsIgnoreCase("qa")){
            this.currentEnvironment = 2;
        }else if(currentEnvironment.equalsIgnoreCase("stage")){
            this.currentEnvironment = 3;
        }else if(currentEnvironment.equalsIgnoreCase("prod")){
            this.currentEnvironment = 4;
        }else{
            this.currentEnvironment = 1;
        }

    }

    /**
     *
     * @param ssoPharmacy
     * The member
     */
    public void setSsoPharmacy(SSOPharmacy ssoPharmacy) {
        this.ssoPharmacy = ssoPharmacy;
    }

    public SSOMember getSsoMember() {
        return ssoMember;
    }

    public String getUniqueId() {
        return uniqueId;
    }
    public int getCurrentEnvironment(){
        return currentEnvironment;
    }
    public SSOPharmacy getSsoPharmacy() {
        return ssoPharmacy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.ssoMember, 0);
        dest.writeString(this.uniqueId);
        dest.writeParcelable(this.ssoPharmacy, 0);
        dest.writeString(this.parentPackagename);
        dest.writeString(this.parentClassname);
        dest.writeInt(this.currentEnvironment);
    }

    public static final Creator<SSOUser> CREATOR = new Creator<SSOUser>() {
        public SSOUser createFromParcel(Parcel source) {
            return new SSOUser(source);
        }

        public SSOUser[] newArray(int size) {
            return new SSOUser[size];
        }
    };
}

