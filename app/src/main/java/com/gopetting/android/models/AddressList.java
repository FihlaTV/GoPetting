package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/4/2016.
 */
public class AddressList {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("address_list")
    private List<Address> mAddresses = new ArrayList<>();

    public int getStatus() {
        return mStatus;
    }

    public List<Address> getAddresses() {
        return mAddresses;
    }


}
