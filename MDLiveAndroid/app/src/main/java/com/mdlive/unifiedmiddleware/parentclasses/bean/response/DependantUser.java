package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class DependantUser implements Parcelable {
    @Expose
    private int id;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("primary_authorized")
    @Expose
    private Boolean primaryAuthorized;
    @Expose
    private String name;

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * The primaryAuthorized
     */
    public Boolean getPrimaryAuthorized() {
        return primaryAuthorized;
    }

    /**
     *
     * @param primaryAuthorized
     * The primary_authorized
     */
    public void setPrimaryAuthorized(Boolean primaryAuthorized) {
        this.primaryAuthorized = primaryAuthorized;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.primaryAuthorized);
        dest.writeString(this.name);
    }

    public DependantUser() {
    }

    protected DependantUser(Parcel in) {
        this.id = in.readInt();
        this.imageUrl = in.readString();
        this.primaryAuthorized = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Parcelable.Creator<DependantUser> CREATOR = new Parcelable.Creator<DependantUser>() {
        public DependantUser createFromParcel(Parcel source) {
            return new DependantUser(source);
        }

        public DependantUser[] newArray(int size) {
            return new DependantUser[size];
        }
    };

    @Override
    public String toString() {
        return "DependantUser{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", primaryAuthorized=" + primaryAuthorized +
                ", name='" + name + '\'' +
                '}';
    }
}
