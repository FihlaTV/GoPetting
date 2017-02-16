package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sumit on 11/26/2016.
 */
public class OrderHistoryDetailsItem {

    @SerializedName("order_id")
    protected int orderId;
    @SerializedName("order_dt")
    protected String orderDate;
    @SerializedName("total_amount_paid")
    public String amountPaid;
    @SerializedName("full_name")
    protected String customerName;
    @SerializedName("address")
    protected String orderAddress;
    @SerializedName("area")
    protected String area;
    @SerializedName("landmark")
    protected String landmark;
    @SerializedName("city")
    protected String city;
    @SerializedName("state")
    protected String state;
    @SerializedName("pincode")
    protected String pincode;
    @SerializedName("phone")
    protected String mobileNumber;
    @SerializedName("cancelled_dt")
    protected String cancelled_date;
    @SerializedName("delivery_type")
    protected int deliveryType;
    @SerializedName("avail_flag")
    protected int availabilityFlag;
    @SerializedName("service_packages")
    protected List<OrderHistoryServiceItem> servicePackages;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCancelled_date() {
        return cancelled_date;
    }

    public void setCancelled_date(String cancelled_date) {
        this.cancelled_date = cancelled_date;
    }

    public List<OrderHistoryServiceItem> getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(List<OrderHistoryServiceItem> servicePackages) {
        this.servicePackages = servicePackages;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    public int getDeliveryType() {
        return deliveryType;
    }

    public int getAvailabilityFlag() {
        return availabilityFlag;
    }
}
