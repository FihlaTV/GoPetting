package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/8/2016.
 */
public class OrderSummary {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("pickup_drop")
    private int mPickupDropCharges;

    @SerializedName("breed_types")
    private List<BreedType> mBreedTypes = new ArrayList<>();

    public int getStatus() {
        return mStatus;
    }

    public int getPickupDropCharges() {
        return mPickupDropCharges;
    }

    public List<BreedType> getBreedTypes() {
        return mBreedTypes;
    }
}
