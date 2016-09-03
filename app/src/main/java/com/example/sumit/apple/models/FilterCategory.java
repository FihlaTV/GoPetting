package com.example.sumit.apple.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sumit.apple.R;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Sumit on 8/28/2016.
 */
public class FilterCategory extends AbstractItem<FilterCategory, FilterCategory.ViewHolder> {

    private String CategoryName;

    public String getCategoryName() {
        return CategoryName;
    }

    public FilterCategory setCategoryName(String categoryName) {
        this.CategoryName = categoryName;
        return this;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_filter_category_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_filter_category;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);
        viewHolder.mItemFilterCategory.setText(CategoryName);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView mItemFilterCategory;

        public ViewHolder(View view) {
            super(view);
            this.mItemFilterCategory = (TextView) view.findViewById(R.id.tv_filter_category);
        }
    }


}




