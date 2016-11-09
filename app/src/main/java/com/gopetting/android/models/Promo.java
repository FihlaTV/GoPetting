package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 11/8/2016.
 */
public class Promo {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("promo_code_discount")
    private int mAmount;

    public int getStatus() {
        return mStatus;
    }

    public int getAmount() {
        return mAmount;
    }

}
