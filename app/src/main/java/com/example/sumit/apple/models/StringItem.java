package com.example.sumit.apple.models;

import android.view.View;

import com.example.sumit.apple.R;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

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
