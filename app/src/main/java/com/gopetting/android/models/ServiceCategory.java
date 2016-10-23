package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 10/21/2016.
 */
public class ServiceCategory {

    @SerializedName("service_subcategories")
    public List<ServiceSubCategory> mServiceSubCategories = new ArrayList<ServiceSubCategory>();

    @SerializedName("service_packages")
    public List<ServicePackageDetail> mServicePackageDetails = new ArrayList<ServicePackageDetail>();
}
