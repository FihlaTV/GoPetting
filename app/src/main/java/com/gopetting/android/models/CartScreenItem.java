package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 10/26/2016.
 */
public class CartScreenItem extends AbstractItem<CartScreenItem, CartScreenItem.ViewHolder> {

    @SerializedName("service_package_id")
    private int mServicePackageId;

    @SerializedName("service_package_name")
    private String mServicePackageName;

    @SerializedName("price")
    private int mPrice;

    @SerializedName("service_subcategory_id")
    private int mServiceSubCategoryId;

    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    public static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();



    public int getServicePackageId() {
        return mServicePackageId;
    }

    public String getServicePackageName() {
        return mServicePackageName;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getServiceSubCategoryId() {
        return mServiceSubCategoryId;
    }



    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_cart_screen_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_cart_screen;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        viewHolder.mItemServicePackageName.setText(mServicePackageName);
        viewHolder.mItemPrice.setText(("Rs."+ mPrice));

    }


    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }



    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected View view;
        @BindView(R.id.tv_service_package_name)
        protected TextView mItemServicePackageName;
        @BindView(R.id.tv_price)
        protected TextView mItemPrice;
        @BindView(R.id.iv_delete)
        protected ImageView mItemDelete;
        @BindView(R.id.rl_delete_container)
        public RelativeLayout mRelativeLayoutDeleteContainer;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }
    }

}


