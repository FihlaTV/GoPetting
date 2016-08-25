package com.example.sumit.apple.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 8/24/2016.
 */
public class DeliveryDetails {

    @SerializedName("query_status")
    private String queryStatus;

    @SerializedName("delivery_days")
    private int deliveryDays;

    @SerializedName("description")
    private String otherDetails;


    public String getQueryStatus() {
        return queryStatus;
    }

    public int getDeliveryDays() {
        return deliveryDays;
    }

    public String getOtherDetails() {
        return otherDetails;
    }



}
