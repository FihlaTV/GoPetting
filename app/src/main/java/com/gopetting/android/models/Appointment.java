package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/2/2016.
 */
public class Appointment {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("in_house")
    private int mInHouseCharges;

    @SerializedName("psd")
    private int mPsdCharges;

    @SerializedName("address")
    public List<Address> mAddresses = new ArrayList<Address>();

    @SerializedName("dateslots")
    public List<Dateslot> mDateslots = new ArrayList<Dateslot>();


    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getInHouseCharges() {
        return mInHouseCharges;
    }

    public int getPsdCharges() {
        return mPsdCharges;
    }

    public List<Address> getAddresses() {
        return mAddresses;
    }

    public List<Dateslot> getDateslots() {
        return mDateslots;
    }




}
