package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 12/11/2016.
 */
public class StartupItem {

    @SerializedName("version_id")
    private String mVersion;

    @SerializedName("promotional_screens")
    private List<StringItem> mStringItem = new ArrayList<>();

    public List<StringItem> getStringItems() {
        return mStringItem;
    }

    public String getVersion() {
        return mVersion;
    }
}
