package com.gopetting.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sumit on 11/7/2016.
 */
public class BreedType {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
      private String name;

        public BreedType(String name) {
            this.name = name;
        }

        //Below method overrides super class, so method name must be 'toString'
        @Override
        public String toString() {
            return name;
        }

    }


