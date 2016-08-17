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

    @SerializedName("image_Url2")
    private String imageUrl2;

    @SerializedName("image_Url3")
    private String imageUrl3;

    @SerializedName("image_Url4")
    private String imageUrl4;

    @SerializedName("image_Url5")
    private String imageUrl5;

}