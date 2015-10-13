package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import java.lang.ref.WeakReference;

/**
 * Created by dhiman_da on 8/13/2015.
 */
public class User implements Parcelable {
    public static final String USER_TAG = "user";

    public static final int MODE_PRIMARY = 0;
    public static final int MODE_DEPENDENT = 1;
    public static final int MODE_ADD_CHILD = 2;

    public String mName;
    public String mImageUrl;
    public String mId;
    public int mMode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mImageUrl);
        dest.writeString(this.mId);
        dest.writeInt(this.mMode);
    }

    public User() {
    }

    public User(final String name, final String imageUrl, final String id, final int mode) {
        mName = name;
        mImageUrl = imageUrl;
        mId = id;
        mMode = mode;
    }

    protected User(Parcel in) {
        this.mName = in.readString();
        this.mImageUrl = in.readString();
        this.mId = in.readString();
        this.mMode = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "mName='" + mName + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mId='" + mId + '\'' +
                ", mMode=" + mMode +
                '}';
    }

    public void saveSelectedUser(final Context context) {
        final WeakReference<Context> reference = new WeakReference<Context>(context);

        if (reference.get() != null) {
            final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.SELECTED_USER, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();

            editor.putString("user_name", mName);
            editor.putString("user_image", mImageUrl);
            editor.putString("user_id", mId);
            editor.putInt("user_mode", mMode);

            editor.commit();
        }
    }

    public static User getSelectedUser(final Context context) {
        final WeakReference<Context> reference = new WeakReference<Context>(context);

        if (reference.get() != null) {
            final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.SELECTED_USER, Context.MODE_PRIVATE);

            if (preferences.getString("user_name", null) == null
                    || preferences.getString("user_image", null) == null) {
                return null;
            } else {
                final User user = new User();
                user.mName = preferences.getString("user_name", null);
                user.mImageUrl = preferences.getString("user_image", null);
                user.mId = preferences.getString("user_id", null);
                user.mMode = preferences.getInt("user_mode", MODE_PRIMARY);

                Log.d("Hello", "Selected User get : " + user.toString());

                return user;
            }
        }

        return null;
    }

    public static void clearUser(final Context context) {
        final WeakReference<Context> reference = new WeakReference<Context>(context);

        if (reference.get() != null) {
            final SharedPreferences preferences = reference.get().getSharedPreferences(PreferenceConstants.SELECTED_USER, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }
    }
}
