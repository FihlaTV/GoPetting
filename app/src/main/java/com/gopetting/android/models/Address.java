package com.gopetting.android.models;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 11/2/2016.
 */
public class Address extends AbstractItem<Address, Address.ViewHolder> {


    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    public static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @SerializedName("address_id")
    private int mAddressId;

    @SerializedName("full_name")
    private String mFullName;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("area")
    private String mArea;

    @SerializedName("landmark")
    private String mLandmark;

    @SerializedName("city")
    private String mCity;

    @SerializedName("state")
    private String mState;

    @SerializedName("pincode")
    private String mPincode;

    @SerializedName("phone")
    private String mPhone;

    public int getAddressId() {
        return mAddressId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getArea() {
        return mArea;
    }

    public String getLandmark() {
        return mLandmark;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getPincode() {
        return mPincode;
    }

    public String getPhone() {
        return mPhone;
    }



    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_address_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_address;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        viewHolder.mRadioButton.setChecked(isSelected());

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        viewHolder.mTextViewFullName.setText(mFullName);

        Resources res = ctx.getResources();
        String fullAddress = String.format(res.getString(R.string.full_address)
                ,mAddress
                ,mArea
                ,mLandmark
                ,mCity
                ,mState
                ,mPincode
        );
        viewHolder.mTextViewFullAddress.setText(fullAddress);

        viewHolder.mTextViewPhone.setText(mPhone);

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
        @BindView(R.id.tv_full_name)
        TextView mTextViewFullName;
        @BindView(R.id.tv_full_address)
        TextView mTextViewFullAddress;
        @BindView(R.id.tv_phone)
        TextView mTextViewPhone;
        @BindView(R.id.ll_delete_container)
        public LinearLayout mLinearLayoutDeleteContainer;
        @BindView(R.id.radio_button)
        public RadioButton mRadioButton;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }

    }



}
