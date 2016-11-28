package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/26/2016.
 */
public class OrderHistoryDetails {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("order_history_details")
    private List<OrderHistoryDetailsItem> mOrderHistoryDetailsItem =new ArrayList<>();

    public int getStatus() {
        return mStatus;
    }

    public List<OrderHistoryDetailsItem> getOrderHistoryDetailsItem() {
        return mOrderHistoryDetailsItem;
    }
}
