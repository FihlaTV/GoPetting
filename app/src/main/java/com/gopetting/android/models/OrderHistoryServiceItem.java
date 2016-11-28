package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 11/26/2016.
 */
public class OrderHistoryServiceItem  extends AbstractItem<OrderHistoryServiceItem, OrderHistoryServiceItem.ViewHolder> {

    @SerializedName("name")
    private String serviceName;
    @SerializedName("price")
    private String price;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int getType() {
        return R.id.fastadapter_order_history_service_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_order_histroy_service;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        viewHolder.mItemServicePackageName.setText(serviceName);
        viewHolder.mItemPrice.setText(("Rs." + price));

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.service_package_name_txt)
        protected TextView mItemServicePackageName;
        @BindView(R.id.price_txt)
        protected TextView mItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }
    }

}
