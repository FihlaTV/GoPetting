package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gopetting.android.R;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Sumit on 8/28/2016.
 */

public class FilterSubCategory extends AbstractItem<FilterSubCategory, FilterSubCategory.ViewHolder> {


        @SerializedName("name")
        private String SubCategoryName;

        public String getSubCategoryName() {
            return SubCategoryName;
        }

        public FilterSubCategory setSubCategoryName(String subCategoryName) {
            this.SubCategoryName = subCategoryName;
            return this;
        }

    //------------

    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public FilterSubCategory setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }


    //-------------

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.fastadapter_filter_subcategory_item_id;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.item_filter_subcategory;
        }

        //The logic to bind your data to the view
        @Override
        public void bindView(ViewHolder viewHolder) {
            //call super so the selection is already handled for you
            super.bindView(viewHolder);
            viewHolder.mItemFilterSubCategory.setText(SubCategoryName);

            viewHolder.mItemCheckbox.setChecked(checked);
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected static class ViewHolder extends RecyclerView.ViewHolder {

            protected TextView mItemFilterSubCategory;
            protected CheckBox mItemCheckbox;

            public ViewHolder(View view) {
                super(view);
                this.mItemFilterSubCategory = (TextView) view.findViewById(R.id.tv_filter_subcategory);
                this.mItemCheckbox = (CheckBox) view.findViewById(R.id.cb_filter_subcategory);
            }
        }


}
