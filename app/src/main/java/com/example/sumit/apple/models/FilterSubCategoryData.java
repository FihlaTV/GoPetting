package com.example.sumit.apple.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumit on 8/28/2016.
 */
public class FilterSubCategoryData {

//        public static final ArrayList<FilterCheckBox> genderCheckbox = new ArrayList<>();
//        public static final  ArrayList<FilterCheckBox> sizeCheckbox = new ArrayList<>();
//        public static final  ArrayList<FilterCheckBox> breedCheckbox = new ArrayList<>();


        private static List<FilterSubCategory> toList(FilterSubCategory... filterSubCategories) {
            return Arrays.asList(filterSubCategories);
        }

        public static List<FilterSubCategory> getGenderData() {
            return toList(
                    new FilterSubCategory().setSubCategoryName("Male"),
                    new FilterSubCategory().setSubCategoryName("Female")
            );
        }


        public static List<FilterSubCategory> getSizeData() {
            return toList(
                    new FilterSubCategory().setSubCategoryName("Small"),
                    new FilterSubCategory().setSubCategoryName("Medium"),
                    new FilterSubCategory().setSubCategoryName("Large")
            );
        }

        public static List<FilterSubCategory> getBreedTypeData() {
            return toList(
                    new FilterSubCategory().setSubCategoryName("Pure"),
                    new FilterSubCategory().setSubCategoryName("Cross")
            );
        }

//    public static List<FilterSubCategory> getTempData() {
//
//        genderCheckbox.get(0).setName("Male").setChecked(false);
//        genderCheckbox.get(1).setName("Female").setChecked(false);
//
//        return
//        );
//    }


    }
