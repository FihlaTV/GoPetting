package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 10/25/2016.
 */
public class CartScreen {

        @SerializedName("status")
        private int mStatus;

        @SerializedName("service_category_name")
        private String mServiceCategoryName;

        @SerializedName("cart_items")
        private List<CartScreenItem> mCartScreenItems = new ArrayList<>();

        public int getStatus() {
            return mStatus;
        }

        public String getServiceCategoryName() {
            return mServiceCategoryName;
        }

        public List<CartScreenItem> getCartScreenItems() {
            return mCartScreenItems;
        }


    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.mServiceCategoryName = serviceCategoryName;
    }

    public void setCartScreenItems(List<CartScreenItem> cartScreenItems) {
        this.mCartScreenItems = cartScreenItems;
    }
}



