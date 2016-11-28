package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 11/26/2016.
 */
public class OrderHistoryItem extends AbstractItem<OrderHistoryItem, OrderHistoryItem.ViewHolder> {

        @SerializedName("status")
        private String mStatus;

        @SerializedName("dateslot")
        private String mDateslot;

        @SerializedName("order_id")
        private String mOrderId;

        @SerializedName("service_category_name")
        private String mServiceName;

        @SerializedName("timeslot")
        private String mTimeSlot;


        private Date date;


        public String getStatus() {
        return mStatus;
    }

        public String getDateslot() {
            return mDateslot;
        }

        public String getOrderId() {
            return mOrderId;
        }

        public String getServiceName() {
            return mServiceName;
        }

        public String getTimeSlot() {
            return mTimeSlot;
        }



        @Override
        public int getType() {
            return R.id.fastadapter_order_history_item_id;
        }

        @Override
        public int getLayoutRes() {
            return R.layout.item_order_history;
        }

        @Override
        public void bindView(ViewHolder holder) {
            super.bindView(holder);
            try {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(mDateslot);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                StringBuilder dateString = new StringBuilder()
                        .append("Scheduled On : ").append(cal.get(Calendar.DAY_OF_MONTH))
                        .append(" ")
                        .append(new SimpleDateFormat("MMM").format(cal.getTime()))
                        .append(" ")
                        .append(cal.get(Calendar.YEAR));


                holder.mDateslot.setText(dateString);
                // holder.mOrderIdNumber.setText(mOrderId);
                holder.mOrderServiceName.setText(mServiceName);
                holder.mTimeSlot.setText(mTimeSlot);
                holder.mStatus.setText(mStatus);

                holder.mTextViewOrderId.setText(mOrderId);

            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }



        protected static class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            @BindView(R.id.order_summary_date_txt)
            protected TextView mDateslot;
            @BindView(R.id.order_summary_name_txt)
            protected TextView mOrderServiceName;
            @BindView(R.id.order_summary_time_txt)
            protected TextView mTimeSlot;
            @BindView(R.id.order_summary_status_txt)
            protected TextView mStatus;
            @BindView(R.id.order_id_lbl)
            protected TextView mTextViewOrderIdLebel;
            @BindView(R.id.order_id_txt)
            protected TextView mTextViewOrderId;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                this.view = view;
            }
        }



}




