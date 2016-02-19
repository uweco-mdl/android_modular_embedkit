package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class UserBasicInfo implements Parcelable {
    @SerializedName("affiliation_logo")
    @Expose
    private String affiliationLogo;
    @SerializedName("remaining_family_members_limit")
    @Expose
    private int remainingFamilyMembersLimit;
    @Expose
    private Notifications notifications;
    private long healthLastUpdate;
    @SerializedName("health_message")
    @Expose
    private String healthMessage;
    @SerializedName("personal_info")
    @Expose
    private PersonalInfo personalInfo;
    @SerializedName("allow_membership_card")
    @Expose
    private boolean allowMembershipCard;
    @Expose
    private String affiliation;
    @SerializedName("dependant_users")
    @Expose
    private List<DependantUser> dependantUsers = new ArrayList<DependantUser>();
    @SerializedName("assist_phone_number")
    @Expose
    private String assistPhoneNumber;
    @SerializedName("primary_user")
    @Expose
    private boolean primaryUser;
    @SerializedName("verify_eligibility")
    @Expose
    private boolean verifyEligibility;

    @SerializedName("timezone_offset")
    @Expose
    private String timezoneOffset;

    /**
     *
     * @return
     * The affiliationLogo
     */
    public String getAffiliationLogo() {
        return affiliationLogo;
    }

    /**
     *
     * @param affiliationLogo
     * The affiliation_logo
     */
    public void setAffiliationLogo(String affiliationLogo) {
        this.affiliationLogo = affiliationLogo;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(String timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    /**
     *
     * @return
     * The remainingFamilyMembersLimit
     */
    public int getRemainingFamilyMembersLimit() {
        return remainingFamilyMembersLimit;
    }

    /**
     *
     * @param remainingFamilyMembersLimit
     * The remaining_family_members_limit
     */
    public void setRemainingFamilyMembersLimit(int remainingFamilyMembersLimit) {
        this.remainingFamilyMembersLimit = remainingFamilyMembersLimit;
    }

    /**
     *
     * @return
     * The notifications
     */
    public Notifications getNotifications() {
        return notifications;
    }

    /**
     *
     * @param notifications
     * The notifications
     */
    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    /**
     *
     * @return
     * The healthLastUpdate
     */
    public long getHealthLastUpdate() {
        return healthLastUpdate;
    }

    /**
     *
     * @param healthLastUpdate
     * The health_last_update
     */
    public void setHealthLastUpdate(long healthLastUpdate) {
        this.healthLastUpdate = healthLastUpdate;
    }

    /**
     *
     * @return
     * The healthMessage
     */
    public String getHealthMessage() {
        return healthMessage;
    }

    /**
     *
     * @param healthMessage
     * The health_message
     */
    public void setHealthMessage(String healthMessage) {
        this.healthMessage = healthMessage;
    }

    /**
     *
     * @return
     * The personalInfo
     */
    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    /**
     *
     * @param personalInfo
     * The personal_info
     */
    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    /**
     *
     * @return
     * The allowMembershipCard
     */
    public boolean getAllowMembershipCard() {
        return allowMembershipCard;
    }

    /**
     *
     * @param allowMembershipCard
     * The allow_membership_card
     */
    public void setAllowMembershipCard(boolean allowMembershipCard) {
        this.allowMembershipCard = allowMembershipCard;
    }

    /**
     *
     * @return
     * The affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     *
     * @param affiliation
     * The affiliation
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     *
     * @return
     * The dependantUsers
     */
    public List<DependantUser> getDependantUsers() {
        return dependantUsers;
    }

    /**
     *
     * @param dependantUsers
     * The dependant_users
     */
    public void setDependantUsers(List<DependantUser> dependantUsers) {
        this.dependantUsers = dependantUsers;
    }

    /**
     *
     * @return
     * The assistPhoneNumber
     */
    public String getAssistPhoneNumber() {
        return assistPhoneNumber;
    }

    /**
     *
     * @param assistPhoneNumber
     * The assist_phone_number
     */
    public void setAssistPhoneNumber(String assistPhoneNumber) {
        this.assistPhoneNumber = assistPhoneNumber;
    }

    /**
     *
     * @return
     * The primaryUser
     */
    public boolean getPrimaryUser() {
        return primaryUser;
    }

    /**
     *
     * @param primaryUser
     * The primary_user
     */
    public void setPrimaryUser(boolean primaryUser) {
        this.primaryUser = primaryUser;
    }

    /**
     *
     * @return
     * The verifyEligibility
     */
    public boolean getVerifyEligibility() {
        return verifyEligibility;
    }

    /**
     *
     * @param verifyEligibility
     * The verify_eligibility
     */
    public void setVerifyEligibility(boolean verifyEligibility) {
        this.verifyEligibility = verifyEligibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.affiliationLogo);
        dest.writeInt(this.remainingFamilyMembersLimit);
        dest.writeParcelable(this.notifications, flags);
        dest.writeLong(this.healthLastUpdate);
        dest.writeString(this.healthMessage);
        dest.writeParcelable(this.personalInfo, flags);
        dest.writeByte(allowMembershipCard ? (byte) 1 : (byte) 0);
        dest.writeString(this.affiliation);
        dest.writeTypedList(dependantUsers);
        dest.writeString(this.assistPhoneNumber);
        dest.writeByte(primaryUser ? (byte) 1 : (byte) 0);
        dest.writeByte(verifyEligibility ? (byte) 1 : (byte) 0);
        dest.writeString(timezoneOffset);
    }

    public UserBasicInfo() {
    }

    protected UserBasicInfo(Parcel in) {
        this.affiliationLogo = in.readString();
        this.remainingFamilyMembersLimit = in.readInt();
        this.notifications = in.readParcelable(Notifications.class.getClassLoader());
        this.healthLastUpdate = in.readLong();
        this.healthMessage = in.readString();
        this.personalInfo = in.readParcelable(PersonalInfo.class.getClassLoader());
        this.allowMembershipCard = in.readByte() != 0;
        this.affiliation = in.readString();
        this.dependantUsers = in.createTypedArrayList(DependantUser.CREATOR);
        this.assistPhoneNumber = in.readString();
        this.primaryUser = in.readByte() != 0;
        this.verifyEligibility = in.readByte() != 0;
        this.timezoneOffset = in.readString();
    }

    public static final Parcelable.Creator<UserBasicInfo> CREATOR = new Parcelable.Creator<UserBasicInfo>() {
        public UserBasicInfo createFromParcel(Parcel source) {
            return new UserBasicInfo(source);
        }

        public UserBasicInfo[] newArray(int size) {
            return new UserBasicInfo[size];
        }
    };

    @Override
    public String toString() {
        return "UserBasicInfo{" +
                "affiliationLogo='" + affiliationLogo + '\'' +
                ", remainingFamilyMembersLimit=" + remainingFamilyMembersLimit +
                ", notifications=" + notifications +
                ", healthLastUpdate=" + healthLastUpdate +
                ", healthMessage='" + healthMessage + '\'' +
                ", personalInfo=" + personalInfo.toString() +
                ", allowMembershipCard=" + allowMembershipCard +
                ", affiliation='" + affiliation + '\'' +
                ", dependantUsers=" + dependantUsers +
                ", assistPhoneNumber='" + assistPhoneNumber + '\'' +
                ", primaryUser=" + primaryUser +
                ", verifyEligibility=" + verifyEligibility +
                ", timezoneOffset=" + timezoneOffset +
                '}';
    }

    public String toJsonString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static UserBasicInfo fromJsonString(String jsonString) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserBasicInfo mUserBasicInfo = gson.fromJson(jsonString.toString().trim(), UserBasicInfo.class);

        mUserBasicInfo.getPersonalInfo().setSecurity(Security.fromJSON(jsonString.toString().trim()));
        mUserBasicInfo.getNotifications().setPharmacyDetails(PharmacyDetails.fromJSON(jsonString.toString().trim()));

        return mUserBasicInfo;
    }

    public void saveToSharedPreference(final Context context, final String jsonString) {
        try {
            final WeakReference<Context> weakReference = new WeakReference<Context>(context);

            final SharedPreferences preferences = weakReference.get().getSharedPreferences(PreferenceConstants.USER_BASIC_INFO, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();

            editor.putString(PreferenceConstants.USER, jsonString.trim());
            editor.commit();
        } catch (Exception e) {

        }
    }

    public static UserBasicInfo readFromSharedPreference(final Context context) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);

        if (weakReference != null && weakReference.get() != null) {
            final SharedPreferences preferences = weakReference.get().getSharedPreferences(PreferenceConstants.USER_BASIC_INFO, Context.MODE_PRIVATE);

            final String savedString = preferences.getString(PreferenceConstants.USER, null);


            if (savedString != null) {
                return UserBasicInfo.fromJsonString(savedString);
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getJSONString(final Context context) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);
        final SharedPreferences preferences = weakReference.get().getSharedPreferences(PreferenceConstants.USER_BASIC_INFO, Context.MODE_PRIVATE);
        final String savedString =  preferences.getString(PreferenceConstants.USER, null);
        return savedString;
    }

    public static List<User> getUsersAsPrimaryUser(final Context context) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);

        final UserBasicInfo userBasicInfo = readFromSharedPreference(weakReference.get());

        final List<User> users = new ArrayList<User>();

        /* Adding the primary user, id is "" for primary user */
        users.add(new User(userBasicInfo.getPersonalInfo().getFirstName() + " " + userBasicInfo.getPersonalInfo().getLastName(),
                userBasicInfo.getPersonalInfo().getImageUrl(),
                "", User.MODE_PRIMARY));

        /* Adding child users */
        if (userBasicInfo.getDependantUsers() != null && userBasicInfo.getDependantUsers().size() > 0) {
            for (int i = 0; i < userBasicInfo.getDependantUsers().size(); i++) {
                final DependantUser dependantUser = userBasicInfo.getDependantUsers().get(i);

                users.add(new User(dependantUser.getName(), dependantUser.getImageUrl(), String.valueOf(dependantUser.getId()), User.MODE_DEPENDENT));
            }
        }

        /* Adding Add Child option, id is "" & imageUrl is "" for Add Child option */
        users.add(new User(weakReference.get().getString(R.string.mdl_add_new_family_member), "", "", User.MODE_ADD_CHILD));

        return  users;
    }

    public static List<User> getUsersAsDependentUser(final Context context) {
        final WeakReference<Context> weakReference = new WeakReference<Context>(context);

        final UserBasicInfo userBasicInfo = readFromSharedPreference(weakReference.get());

        final List<User> users = new ArrayList<User>();

        /* Adding Dependent User as Selected User */
        users.add(new User(userBasicInfo.getPersonalInfo().getFirstName() + " " + userBasicInfo.getPersonalInfo().getLastName(),
                userBasicInfo.getPersonalInfo().getImageUrl(),
                "", User.MODE_DEPENDENT));

        /* Adding Parent users */
        if (userBasicInfo.getDependantUsers() != null && userBasicInfo.getDependantUsers().size() > 0) {
            for (int i = 0; i < userBasicInfo.getDependantUsers().size(); i++) {
                final DependantUser dependantUser = userBasicInfo.getDependantUsers().get(i);

                users.add(new User(dependantUser.getName(), dependantUser.getImageUrl(), String.valueOf(dependantUser.getId()), User.MODE_PRIMARY));
            }
        }

        return  users;
    }
}
