package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/4/2016.
 */
public class DateTimeSlot {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("dateslots")
    public List<Dateslot> mDateslots = new ArrayList<Dateslot>();


    public int getStatus() {
        return mStatus;
    }

    public List<Dateslot> getDateslots() {
        return mDateslots;
    }


}
