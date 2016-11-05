package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 10/27/2016.
 */
public class Status {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("id")
    private int mId;

    public int getStatus() {
        return mStatus;
    }

    public int getId() {
        return mId;
    }
}

