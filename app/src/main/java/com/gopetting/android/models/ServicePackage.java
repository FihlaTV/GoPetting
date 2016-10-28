package com.gopetting.android.models;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Sumit on 10/20/2016.
 */

//@Parcel
public class ServicePackage extends AbstractItem<ServicePackage, ServicePackage.ViewHolder> implements IExpandable<ServicePackage, IItem> {

    //Keep all fields public for Parceler

    @SerializedName("service_package_id")
    public int mServicePackageId;

    @SerializedName("service_package_name")
    public String mServicePackageName;

    @SerializedName("description")
    public String mDescription;

    @SerializedName("price")
    public int mPrice;

    public boolean mItemSelected = false;

    public List<IItem> mSubItems;
    public boolean mExpanded = false;
    public FastAdapter.OnClickListener<ServicePackage> mOnClickListener;

    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    public static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

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

    public void setItemSelected(boolean itemSelected) {
        this.mItemSelected = itemSelected;
    }


    public boolean isAutoExpanding() {
        return true;
    }

    //  Required for Expandable Functionality
    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    //  Required for Expandable Functionality
    @Override
    public ServicePackage withIsExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    //  Required for Expandable Functionality
    @Override
    public List<IItem> getSubItems() {
        return mSubItems;
    }

    //  Required for Expandable Functionality
    public ServicePackage withSubItems(List<IItem> subItems) {
        this.mSubItems = subItems;
        return this;
    }


    public FastAdapter.OnClickListener<ServicePackage> getOnClickListener() {
        return mOnClickListener;
    }

    public ServicePackage withOnClickListener(FastAdapter.OnClickListener<ServicePackage> mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }

    //we define a clickListener in here so we can directly animate
    final private FastAdapter.OnClickListener<ServicePackage> onClickListener = new FastAdapter.OnClickListener<ServicePackage>() {
        @Override
        public boolean onClick(View v, IAdapter adapter, ServicePackage item, int position) {
            if (item.getSubItems() != null) {
                if (!item.isExpanded()) {
//                    ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(180).start();
                } else {
//                    ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(0).start();
                }
                return mOnClickListener != null ? mOnClickListener.onClick(v, adapter, item, position) : true;
            }
            return mOnClickListener != null ? mOnClickListener.onClick(v, adapter, item, position) : false;
        }
    };

    /**
     * we overwrite the item specific click listener so we can automatically animate within the item
     *
     * @return
     */
    @Override
    public FastAdapter.OnClickListener<ServicePackage> getOnItemClickListener() {
        return onClickListener;
    }

//    @Override
//    public boolean isSelectable() {
//        //this might not be true for your application
//        return getSubItems() == null;
//    }
//

    /**
     * helper method to style the image view(Basket Image)
     *
     * @param view
     * @param value
     */
    private void style(View view, int value) {
        view.setScaleX(value);
        view.setScaleY(value);
        view.setAlpha(value);
    }

    /**
     * helper method to animate the image view(Basket Image)
     *
     * @param imageSelectedYes
     * @param imageSelectedNo
     * @param on
     */
    public void animateHeart(View imageSelectedYes, View imageSelectedNo, boolean on) {
        imageSelectedYes.setVisibility(View.VISIBLE);
        imageSelectedNo.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            viewPropertyStartCompat(imageSelectedNo.animate().scaleX(on ? 0 : 1).scaleY(on ? 0 : 1).alpha(on ? 0 : 1));
            viewPropertyStartCompat(imageSelectedYes.animate().scaleX(on ? 1 : 0).scaleY(on ? 1 : 0).alpha(on ? 1 : 0));
        }
    }

    /**
     * helper method for the animator on APIs < 14
     *
     * @param animator
     */
    public static void viewPropertyStartCompat(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= 14) {
            animator.start();
        }
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






    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_service_package_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_service_package;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);
        viewHolder.mItemServicePackageName.setText(mServicePackageName);
        viewHolder.mItemServicePackageDesc.setText(mDescription);
        viewHolder.mItemPrice.setText("Rs."+ mPrice);




    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView mItemServicePackageName;
        protected TextView mItemServicePackageDesc;
        protected TextView mItemPrice;
        public ImageView mItemBasketSelectedYes;
        public ImageView mItemBasketSelectedNo;
        public RelativeLayout mItemBasketContainer;


        public ViewHolder(View view) {
            super(view);
            this.mItemServicePackageName = (TextView) view.findViewById(R.id.tv_service_package_name);
            this.mItemServicePackageDesc = (TextView) view.findViewById(R.id.tv_service_package_desc);
            this.mItemPrice = (TextView) view.findViewById(R.id.tv_price);
            this.mItemBasketSelectedYes = (ImageView) view.findViewById(R.id.iv_basket_selected_yes);
            this.mItemBasketSelectedNo = (ImageView) view.findViewById(R.id.iv_basket_selected_no);
            this.mItemBasketContainer = (RelativeLayout) view.findViewById(R.id.rl_basket_container);



        }
    }



}
