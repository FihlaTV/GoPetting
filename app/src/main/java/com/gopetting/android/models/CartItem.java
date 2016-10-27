package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 10/26/2016.
 */
public class CartItem {

    @SerializedName("service_package_id")
    public int mServicePackageId;

    @SerializedName("service_subcategory_id")
    public int mServiceSubCategoryId;


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
}
