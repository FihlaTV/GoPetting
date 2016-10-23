package com.gopetting.android.bus;

import com.gopetting.android.models.ServicePackage;

import java.util.List;

/**
 * Created by Sumit on 10/23/2016.
 */
public class ActivityToFragment {

        List<ServicePackage> mServicePackages;
        int mSubCategoryIndex;

        public ActivityToFragment(List<ServicePackage> servicePackages, int subCategoryIndex) {
            mServicePackages = servicePackages;
            mSubCategoryIndex = subCategoryIndex;
        }

        public List<ServicePackage> getServicePackages() {
            return mServicePackages;
        }

        public int getSubCategoryIndex() {
            return mSubCategoryIndex;
        }


}
