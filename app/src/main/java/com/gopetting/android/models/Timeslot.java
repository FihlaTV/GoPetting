package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
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
public class Timeslot extends AbstractItem<Timeslot, Timeslot.ViewHolder> {

    @SerializedName("timeslot_id")
    private int mTimeslotId;

    @SerializedName("timeslot")
    private String mTimeslot;


    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    public static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();


    public int getTimeslotId() {
        return mTimeslotId;
    }


    public String getTimeslot() {
        return mTimeslot;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_timeslot_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_timeslot;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        viewHolder.mItemTimeslot.setText(mTimeslot);

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
        @BindView(R.id.tv_timeslot)
        protected TextView mItemTimeslot;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }
    }



}
