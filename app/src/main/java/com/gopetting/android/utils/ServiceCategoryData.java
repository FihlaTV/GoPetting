package com.gopetting.android.utils;

import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.ServicePackage;

import java.util.List;

/**
 * Created by Sumit on 10/23/2016.
 */
public class ServiceCategoryData {

    private static ServiceCategory mServiceCategoryData;

    public static ServiceCategory getServiceCategoryData() {
        return mServiceCategoryData;
    }

    public static void setServiceCategoryData(ServiceCategory mServiceCategoryData) {
        ServiceCategoryData.mServiceCategoryData = mServiceCategoryData;
    }

    //To fetch ServicePackage data from ServiceFragment
    public static List<ServicePackage> getServicePackages(int serviceSubCategoryIndex){
        return mServiceCategoryData.mServiceSubCategories.get(serviceSubCategoryIndex).getServicePackages();
    }

}
