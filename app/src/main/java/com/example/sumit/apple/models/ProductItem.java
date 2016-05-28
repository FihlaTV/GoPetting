package com.example.sumit.apple.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sumit.apple.R;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Sumit on 3/16/2016.
 */
public class ProductItem extends AbstractItem<ProductItem, ProductItem.ViewHolder> {
    public String imageName;
    public int imageResource;


    public ProductItem withName(String name) {
        this.imageName = name;
        return this;
    }

    public ProductItem withImage(int resource) {
        this.imageResource = resource;
        return this;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_product_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_product;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        //bind our data
        //set the text for the name
        viewHolder.mImageName.setText(imageName);
        //set the text for the description or hide
        viewHolder.mImageView.setImageResource(imageResource);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mImageName;
        protected ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            this.mImageName = (TextView) view.findViewById(R.id.id_product_name);
            this.mImageView = (ImageView) view.findViewById(R.id.id_product_icon);
        }
    }
}