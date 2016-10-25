package com.gopetting.android.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.gopetting.android.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sumit on 10/21/2016.
 */
public class ServicePackageDetailData extends AbstractItem<ServicePackageDetailData, ServicePackageDetailData.ViewHolder> {


    @SerializedName("service_package_id")
    private int mServicePackageId;

    @SerializedName("details")
    private List<String> mDetails = new ArrayList<String>();

    public int getServicePackageId() {
        return mServicePackageId;
    }

    public List<String> getDetails() {
        return mDetails;
    }


    public void setServicePackageId(int servicePackageId) {
        this.mServicePackageId = servicePackageId;
    }

    public void setDetails(List<String> details) {
        this.mDetails = details;
    }


    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_service_package_detail_item_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_service_package_detail;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder);

        viewHolder.mPackageDetailPoint1.setText(mDetails.get(0));
        viewHolder.mPackageDetailPoint2.setText(mDetails.get(1));
        viewHolder.mPackageDetailPoint3.setText(mDetails.get(2));
        viewHolder.mPackageDetailPoint4.setText(mDetails.get(3));
        viewHolder.mPackageDetailPoint5.setText(mDetails.get(4));

    }


    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected View view;
        @BindView(R.id.tv_package_detail_title)
        TextView mPackageDetailTitle;
        @BindView(R.id.iv_bullet)
        ImageView mBulletIcon;
        @BindView(R.id.tv_package_detail_point1)
        TextView mPackageDetailPoint1;
        @BindView(R.id.tv_package_detail_point2)
        TextView mPackageDetailPoint2;
        @BindView(R.id.tv_package_detail_point3)
        TextView mPackageDetailPoint3;
        @BindView(R.id.tv_package_detail_point4)
        TextView mPackageDetailPoint4;
        @BindView(R.id.tv_package_detail_point5)
        TextView mPackageDetailPoint5;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

}
