package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 8/16/2016.
 */
public class DogDetails {

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

    @SerializedName("male_height")
    private String maleHeight;

    @SerializedName("female_height")
    private String femaleHeight;

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