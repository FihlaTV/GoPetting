package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 10/27/2016.
 */
public class SummarySecondStatus {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("id")
    private String mId;

    public int getStatus() {
        return mStatus;
    }

    public String getId() {
        return mId;
    }




}


