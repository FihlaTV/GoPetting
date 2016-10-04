package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 9/17/2016.
 */
public class User {

    @SerializedName("user_id")
    private String userId;

    public String getUserId() {
        return userId;
    }
}
