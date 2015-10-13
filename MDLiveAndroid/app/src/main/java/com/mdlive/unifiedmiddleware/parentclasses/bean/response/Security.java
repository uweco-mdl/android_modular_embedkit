package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/10/2015.
 */
public class Security implements Parcelable {
    private static final String SECURITY = "security";

    @Expose
    private String question2;
    @Expose
    private String answer2;
    @Expose
    private String answer1;
    @Expose
    private String question1;

    /**
     *
     * @return
     * The question2
     */
    public String getQuestion2() {
        return question2;
    }

    /**
     *
     * @param question2
     * The question2
     */
    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    /**
     *
     * @return
     * The answer2
     */
    public String getAnswer2() {
        return answer2;
    }

    /**
     *
     * @param answer2
     * The answer2
     */
    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    /**
     *
     * @return
     * The answer1
     */
    public String getAnswer1() {
        return answer1;
    }

    /**
     *
     * @param answer1
     * The answer1
     */
    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    /**
     *
     * @return
     * The question1
     */
    public String getQuestion1() {
        return question1;
    }

    /**
     *
     * @param question1
     * The question1
     */
    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question2);
        dest.writeString(this.answer2);
        dest.writeString(this.answer1);
        dest.writeString(this.question1);
    }

    public Security() {
    }

    protected Security(Parcel in) {
        this.question2 = in.readString();
        this.answer2 = in.readString();
        this.answer1 = in.readString();
        this.question1 = in.readString();
    }

    public static final Parcelable.Creator<Security> CREATOR = new Parcelable.Creator<Security>() {
        public Security createFromParcel(Parcel source) {
            return new Security(source);
        }

        public Security[] newArray(int size) {
            return new Security[size];
        }
    };

    @Override
    public String toString() {
        return "Security{" +
                "question2='" + question2 + '\'' +
                ", answer2='" + answer2 + '\'' +
                ", answer1='" + answer1 + '\'' +
                ", question1='" + question1 + '\'' +
                '}';
    }

    public static Security fromJSON(final String responseString) {
        final Security security = new Security();

        try {
            final JSONObject fullJSONObject = new JSONObject(responseString);

            try {
                final JSONObject personalInfoJSONObject = fullJSONObject.getJSONObject("personal_info");
                final JSONObject securityJSONObject = personalInfoJSONObject.getJSONObject(SECURITY);
                security.setQuestion1(securityJSONObject.getString("question1"));
                security.setAnswer1(securityJSONObject.getString("answer1"));
                security.setQuestion2(securityJSONObject.getString("question2"));
                security.setAnswer2(securityJSONObject.getString("answer2"));
            } catch (JSONException e) {

            }
        } catch (JSONException jsonException) {

        }

        return security;
    }
}
