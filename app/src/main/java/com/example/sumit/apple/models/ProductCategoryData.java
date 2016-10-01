package com.example.sumit.apple.models;

import com.example.sumit.apple.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumit on 9/30/2016.
 */
public class ProductCategoryData {

        public static List<ProductCategory> getItems() {
            return toList(
                    new ProductCategory().setCategoryImage(R.drawable.grooming),
                    new ProductCategory().setCategoryImage(R.drawable.training),
                    new ProductCategory().setCategoryImage(R.drawable.vaccination),
                    new ProductCategory().setCategoryImage(R.drawable.boarding)
            );
        }

        private static List<ProductCategory> toList(ProductCategory... productCategories) {
            return Arrays.asList(productCategories);
        }

    }
