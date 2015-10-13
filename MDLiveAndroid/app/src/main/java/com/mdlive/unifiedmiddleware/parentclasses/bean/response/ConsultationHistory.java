package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsultationHistory implements Parcelable {
    @Override
    public String toString() {
        return "ConsultationHistory{" +
                "primaryDiagnosis=" + primaryDiagnosis +
                ", consultationMethod='" + consultationMethod + '\'' +
                ", providerId=" + providerId +
                ", claimFormUrl='" + claimFormUrl + '\'' +
                ", providerImageUrl='" + providerImageUrl + '\'' +
                ", providerName='" + providerName + '\'' +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", consultationDate='" + consultationDate + '\'' +
                ", dischargeFormUrl='" + dischargeFormUrl + '\'' +
                '}';
    }

    @SerializedName("primary_diagnosis")
    @Expose
    private List<String> primaryDiagnosis = new ArrayList<String>();
    @SerializedName("consultation_method")
    @Expose
    private String consultationMethod;
    @SerializedName("provider_id")
    @Expose
    private int providerId;
    @SerializedName("claim_form_url")
    @Expose
    private String claimFormUrl;
    @SerializedName("provider_image_url")
    @Expose
    private String providerImageUrl;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
    @SerializedName("chief_complaint")
    @Expose
    private String chiefComplaint;
    @SerializedName("consultation_date")
    @Expose
    private String consultationDate;
    @SerializedName("discharge_form_url")
    @Expose
    private String dischargeFormUrl;

    /**
     *
     * @return
     * The primaryDiagnosis
     */
    public List<String> getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    /**
     *
     * @param primaryDiagnosis
     * The primary_diagnosis
     */
    public void setPrimaryDiagnosis(List<String> primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
    }

    /**
     *
     * @return
     * The consultationMethod
     */
    public String getConsultationMethod() {
        return consultationMethod;
    }

    /**
     *
     * @param consultationMethod
     * The consultation_method
     */
    public void setConsultationMethod(String consultationMethod) {
        this.consultationMethod = consultationMethod;
    }

    /**
     *
     * @return
     * The providerId
     */
    public int getProviderId() {
        return providerId;
    }

    /**
     *
     * @param providerId
     * The provider_id
     */
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    /**
     *
     * @return
     * The claimFormUrl
     */
    public String getClaimFormUrl() {
        return claimFormUrl;
    }

    /**
     *
     * @param claimFormUrl
     * The claim_form_url
     */
    public void setClaimFormUrl(String claimFormUrl) {
        this.claimFormUrl = claimFormUrl;
    }

    /**
     *
     * @return
     * The providerImageUrl
     */
    public String getProviderImageUrl() {
        return providerImageUrl;
    }

    /**
     *
     * @param providerImageUrl
     * The provider_image_url
     */
    public void setProviderImageUrl(String providerImageUrl) {
        this.providerImageUrl = providerImageUrl;
    }

    /**
     *
     * @return
     * The providerName
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     *
     * @param providerName
     * The provider_name
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     *
     * @return
     * The chiefComplaint
     */
    public String getChiefComplaint() {
        return chiefComplaint;
    }

    /**
     *
     * @param chiefComplaint
     * The chief_complaint
     */
    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    /**
     *
     * @return
     * The consultationDate
     */
    public String getConsultationDate() {
        return consultationDate;
    }

    /**
     *
     * @param consultationDate
     * The consultation_date
     */
    public void setConsultationDate(String consultationDate) {
        this.consultationDate = consultationDate;
    }

    /**
     *
     * @return
     * The dischargeFormUrl
     */
    public String getDischargeFormUrl() {
        return dischargeFormUrl;
    }

    /**
     *
     * @param dischargeFormUrl
     * The discharge_form_url
     */
    public void setDischargeFormUrl(String dischargeFormUrl) {
        this.dischargeFormUrl = dischargeFormUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.primaryDiagnosis);
        dest.writeString(this.consultationMethod);
        dest.writeInt(this.providerId);
        dest.writeString(this.claimFormUrl);
        dest.writeString(this.providerImageUrl);
        dest.writeString(this.providerName);
        dest.writeString(this.chiefComplaint);
        dest.writeString(this.consultationDate);
        dest.writeString(this.dischargeFormUrl);
    }

    public ConsultationHistory() {
    }

    protected ConsultationHistory(Parcel in) {
        this.primaryDiagnosis = in.createStringArrayList();
        this.consultationMethod = in.readString();
        this.providerId = in.readInt();
        this.claimFormUrl = in.readString();
        this.providerImageUrl = in.readString();
        this.providerName = in.readString();
        this.chiefComplaint = in.readString();
        this.consultationDate = in.readString();
        this.dischargeFormUrl = in.readString();
    }

    public static final Parcelable.Creator<ConsultationHistory> CREATOR = new Parcelable.Creator<ConsultationHistory>() {
        public ConsultationHistory createFromParcel(Parcel source) {
            return new ConsultationHistory(source);
        }

        public ConsultationHistory[] newArray(int size) {
            return new ConsultationHistory[size];
        }
    };
}
