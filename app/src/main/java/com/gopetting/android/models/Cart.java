package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 10/25/2016.
 */

@Parcel
public class Cart {

        @SerializedName("status")
        public int mStatus;

        @SerializedName("cart_items")
        public List<CartItem> mCartItems = new ArrayList<>();


    @SerializedName("service_category_name")
    public String mServiceCategoryName;


    public Cart() {

    }

    public List<CartItem> getCartServicePackages() {
            return mCartItems;
        }

        public int getStatus() {
            return mStatus;
        }


    public String getServiceCategoryName() {
        return mServiceCategoryName;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.mCartItems = cartItems;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.mServiceCategoryName = serviceCategoryName;
    }
}



