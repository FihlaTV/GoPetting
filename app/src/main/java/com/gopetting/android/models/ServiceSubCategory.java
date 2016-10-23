package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 10/20/2016.
 */
public class ServiceSubCategory {

    @SerializedName("service_subcategory_id")
    private int mServiceSubCategoryId;

    @SerializedName("service_subcategory_name")
    private String mServiceSubcategoryName;

    @SerializedName("service_packages")
    private List<ServicePackage> mServicePackages = new ArrayList<ServicePackage>();

    public int getServiceSubCategoryId() {
        return mServiceSubCategoryId;
    }

    public String getServiceSubcategoryName() {
        return mServiceSubcategoryName;
    }

    public List<ServicePackage> getServicePackages() {
        return mServicePackages;
    }
}
