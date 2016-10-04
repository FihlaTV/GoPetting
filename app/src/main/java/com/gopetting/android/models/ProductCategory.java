package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Sumit on 9/30/2016.
 */
public class ProductCategory extends AbstractItem<ProductCategory, ProductCategory.ViewHolder> {

        private int mCategoryImage;

        public int getCategoryImage() {
            return mCategoryImage;
        }

        public ProductCategory setCategoryImage(int categoryImage) {
            this.mCategoryImage = categoryImage;
            return this;
        }

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.fastadapter_product_category_item_id;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.item_product_category;
        }

        //The logic to bind your data to the view
        @Override
        public void bindView(ViewHolder viewHolder) {
            //call super so the selection is already handled for you
            super.bindView(viewHolder);
            viewHolder.mItemCategoryImage.setImageResource(mCategoryImage);
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected static class ViewHolder extends RecyclerView.ViewHolder {

            protected ImageView mItemCategoryImage;

            public ViewHolder(View view) {
                super(view);
                this.mItemCategoryImage = (ImageView) view.findViewById(R.id.iv_product_category);
            }
        }


    }


