package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 11/4/2016.
 */
public class AddressList {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("address_list")
    private List<Address> mAddresses = new ArrayList<>();

    public int getStatus() {
        return mStatus;
    }

    public List<Address> getAddresses() {
        return mAddresses;
    }


}
