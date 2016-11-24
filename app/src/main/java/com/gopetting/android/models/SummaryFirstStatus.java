package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 10/27/2016.
 */
public class SummaryFirstStatus {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("id")
    private String mId;

    @SerializedName("at")
    private String mAt;

    @SerializedName("email")
    private String mEmailId;

    public int getStatus() {
        return mStatus;
    }

    public String getId() {
        return mId;
    }

    public String getAt() {
        return mAt;
    }

    public String getEmailId() {
        return mEmailId;
    }


}


