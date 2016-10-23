package com.gopetting.android.models;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Sumit on 10/20/2016.
 */

@Parcel
public class ServicePackage {

    //Keep all fields public for Parceler

    @SerializedName("service_package_id")
    public int mServicePackageId;

    @SerializedName("service_package_name")
    public String mServicePackageName;

    @SerializedName("description")
    public String mDescription;

    @SerializedName("price")
    public int mPrice;

    public List<String> mServicePackageDetails;


    // empty constructor needed by the Parceler library
    public ServicePackage() {
    }

    public int getServicePackageId() {
        return mServicePackageId;
    }

    public String getServicePackageName() {
        return mServicePackageName;
    }

    public int getPrice() {
        return mPrice;
    }

    public String getDescription() {
        return mDescription;
    }

    public List<String> getServicePackageDetails() {
        return mServicePackageDetails;
    }

    //  Required for Expandable Functionality
    public ServicePackage setServicePackageDetails(List<String> servicePackageDetails) {
        this.mServicePackageDetails = servicePackageDetails;
        return this;
    }

}
