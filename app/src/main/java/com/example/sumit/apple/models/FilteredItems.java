package com.example.sumit.apple.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sumit on 9/2/2016.
 */

@Parcel
public class FilteredItems {

    public static int INDEX_BREED_NAME = 0;
    public static int INDEX_GENDER = 1;
    public static int INDEX_SIZE = 2;
    public static int INDEX_BREED_TYPE = 3;


    // empty constructor needed by the Parceler library
    public FilteredItems() {
    }

    ArrayList<String> subCategoryNames = new ArrayList<String>();

    public ArrayList<String> getSubCategoryNames() {
        return subCategoryNames;
    }

    public void setSubCategoryNames(ArrayList<String> subCategoryNames) {
        this.subCategoryNames = subCategoryNames;

    }

}