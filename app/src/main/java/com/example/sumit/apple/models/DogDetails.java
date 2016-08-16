package com.example.sumit.apple.models;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sumit.apple.R;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Sumit on 8/16/2016.
 */
public class DogDetails {

    @SerializedName("item_id")
    private int itemId;

    private String imageUrl[];

    @SerializedName("unit_price")
    private int unitPrice;

    private int mrp;

    private int discount;

    private int likes;

    @SerializedName("image_url")
    private String imageUrl;

    private int gender;

    private int age;

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_dog_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_dog;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getMrp() {
        return mrp;
    }

    public int getDiscount() {
        return discount;
    }

    public int getLikes() {
        return likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        Glide.with(ctx).load(imageUrl).placeholder(R.drawable.ic_shopping_placeholder).into(viewHolder.mItemImageView);

        viewHolder.mItemName.setText(name);
        viewHolder.mItemPrice.setText("Rs."+ unitPrice);


        if(discount > 0){
            viewHolder.mItemMRP.setVisibility(View.VISIBLE);
            viewHolder.mItemDiscount.setVisibility(View.VISIBLE);

            viewHolder.mItemMRP.setText("Rs." + mrp);
            viewHolder.mItemMRP.setPaintFlags(viewHolder.mItemMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // To strike through text
            viewHolder.mItemDiscount.setText(discount + "% Off");

        }else {
            viewHolder.mItemMRP.setVisibility(View.GONE);
            viewHolder.mItemDiscount.setVisibility(View.GONE);
        }

        viewHolder.mItemDescription.setText(setItemDescription(gender,age));

//                viewHolder.mItemLikes.setImageResource(R.drawable.ic_heart);


        viewHolder.mItemLikes.setColorFilter(ContextCompat.getColor(ctx, R.color.black)); //TODO: To dynamically change color of icon

//                viewHolder.mItemlikes.setText(String.valueOf(likes));


    }


    private String setItemDescription(int gender,int age) {
        if(gender==0){
            return "MALE/"+age+" MONTHS";
        }
        else {
            return "FEMALE/"+age+" MONTHS";
        }

    }


    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView mItemImageView;
        protected TextView mItemName;
        protected TextView mItemPrice;
        protected TextView mItemMRP;
        protected TextView mItemDiscount;
        protected TextView mItemDescription;
        protected ImageView mItemLikes;


        public ViewHolder(View view) {
            super(view);
            this.mItemImageView = (ImageView) view.findViewById(R.id.iv_product_list_item);
            this.mItemName = (TextView) view.findViewById(R.id.tv_item_name);
            this.mItemPrice = (TextView) view.findViewById(R.id.tv_item_unit_price);
            this.mItemMRP = (TextView) view.findViewById(R.id.tv_item_mrp);
            this.mItemDiscount = (TextView) view.findViewById(R.id.tv_item_discount);
            this.mItemDescription = (TextView) view.findViewById(R.id.tv_item_description);
            this.mItemLikes = (ImageView) view.findViewById(R.id.iv_item_likes);

        }
    }
}



