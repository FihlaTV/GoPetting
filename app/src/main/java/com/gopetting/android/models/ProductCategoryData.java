package com.gopetting.android.models;

import com.gopetting.android.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumit on 9/30/2016.
 */
public class ProductCategoryData {

        public static List<ProductCategory> getItems() {
            return toList(
                    new ProductCategory().setCategoryImage(R.drawable.img_pet_salon),
                    new ProductCategory().setCategoryImage(R.drawable.img_pet_school),
                    new ProductCategory().setCategoryImage(R.drawable.img_pet_doctor),
                    new ProductCategory().setCategoryImage(R.drawable.img_pet_hotel)
            );
        }

        private static List<ProductCategory> toList(ProductCategory... productCategories) {
            return Arrays.asList(productCategories);
        }

    }
