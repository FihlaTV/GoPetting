package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 9/30/2016.
 */
public class StringItem  {

    @SerializedName("image_id")
    private int mId;

    @SerializedName("image_url")
    private String mName;

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }
}
