package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Sumit on 10/26/2016.
 */

@Parcel
public class CartItem {

    @SerializedName("service_package_id")
    public int mServicePackageId;

    @SerializedName("service_subcategory_id")
    public int mServiceSubCategoryId;


    @SerializedName("service_package_name")
    public String mServicePackageName;

    @SerializedName("price")
    public int mPrice;


    public CartItem() {

    }

    public int getServicePackageId() {
        return mServicePackageId;
    }

    public int getServiceSubCategoryId() {
        return mServiceSubCategoryId;
    }

    public CartItem setServicePackageId(int mServicePackageId) {
        this.mServicePackageId = mServicePackageId;
        return this;
    }

    public CartItem setServiceSubCategoryId(int mServiceSubCategoryId) {
        this.mServiceSubCategoryId = mServiceSubCategoryId;
        return this;
    }


    public String getServicePackageName() {
        return mServicePackageName;
    }

    public CartItem setServicePackageName(String servicePackageName) {
        this.mServicePackageName = servicePackageName;
        return this;
    }

    public int getPrice() {
        return mPrice;
    }

    public CartItem setPrice(int price) {
        this.mPrice = price;
        return this;
    }
}
