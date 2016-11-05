package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 11/2/2016.
 */
public class Dateslot extends AbstractItem<Dateslot, Dateslot.ViewHolder> {

    @SerializedName("dateslot")
    private String mDateslot;

    @SerializedName("timeslots")
    public List<Timeslot> mTimeslots = new ArrayList<Timeslot>();


    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    public static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();


    public String getDateslot() {
        return mDateslot;
    }


    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_dateslot_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_dateslot;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format.parse(mDateslot);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(date);

        String monthInYear = (String) android.text.format.DateFormat.format("MMM", date); //MAY
        String dayInMonth = (String) android.text.format.DateFormat.format("dd", date); //20
        String dayNameInWeek = (String) android.text.format.DateFormat.format("EEE", date); //TUE

        viewHolder.mItemMonth.setText(monthInYear);
        viewHolder.mItemDate.setText(dayInMonth);
        viewHolder.mItemDay.setText(dayNameInWeek);

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
        @BindView(R.id.tv_month)
        protected TextView mItemMonth;
        @BindView(R.id.tv_date)
        protected TextView mItemDate;
        @BindView(R.id.tv_day)
        protected TextView mItemDay;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }
    }



}
