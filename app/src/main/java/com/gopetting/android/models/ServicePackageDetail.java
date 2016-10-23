package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 10/21/2016.
 */
public class ServicePackageDetail {

    @SerializedName("service_package_id")
    private int mServicePackageId;

    @SerializedName("details")
    private List<String> mDetails = new ArrayList<String>();

    public int getServicePackageId() {
        return mServicePackageId;
    }

    public List<String> getDetails() {
        return mDetails;
    }
}
