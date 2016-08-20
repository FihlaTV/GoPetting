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

    @SerializedName("image_url2")
    private String imageUrl2;

    @SerializedName("image_url3")
    private String imageUrl3;

    @SerializedName("image_url4")
    private String imageUrl4;

    @SerializedName("image_url5")
    private String imageUrl5;

    private String temperament;

    private String barking;

    private String life;

    private String maleHeight;

    private String femaleHeight;


    public int getItemId() {
        return itemId;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public String getImageUrl4() {
        return imageUrl4;
    }

    public String getImageUrl5() {
        return imageUrl5;
    }

    public String getTemperament() {
        return temperament;
    }

    public String getBarking() {
        return barking;
    }

    public String getLife() {
        return life;
    }

    public String getMaleHeight() {
        return maleHeight;
    }

    public String getFemaleHeight() {
        return femaleHeight;
    }
}