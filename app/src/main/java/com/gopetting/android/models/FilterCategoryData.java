package com.gopetting.android.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumit on 8/28/2016.
 */
public class FilterCategoryData {

    public static List<FilterCategory> getItems() {
        return toList(
                new FilterCategory().setCategoryName("Breed Name"),
                new FilterCategory().setCategoryName("Gender"),
                new FilterCategory().setCategoryName("Size"),
                new FilterCategory().setCategoryName("Breed Type")
        );
}

    private static List<FilterCategory> toList(FilterCategory... filterCategories) {
        return Arrays.asList(filterCategories);
    }

}