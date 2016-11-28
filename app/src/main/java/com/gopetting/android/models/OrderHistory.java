package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 11/26/2016.
 */
public class OrderHistory {

    @SerializedName("status")
    private   int mStatus ;

    @SerializedName("order_history_summary_items")
    private List<OrderHistoryItem> mOrderHistoryItems = new ArrayList<>();


    public List<OrderHistoryItem> getOrderHistoryItems() {
        return mOrderHistoryItems;
    }

    public int getStatus() {
        return mStatus;
    }


}
