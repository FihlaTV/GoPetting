package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 10/25/2016.
 */
public class Cart {

        @SerializedName("status")
        public int mStatus;

        @SerializedName("cart_items")
        public List<CartItem> mCartItems = new ArrayList<>();

        public List<CartItem> getCartServicePackages() {
            return mCartItems;
        }

        public int getStatus() {
            return mStatus;
        }
    }



