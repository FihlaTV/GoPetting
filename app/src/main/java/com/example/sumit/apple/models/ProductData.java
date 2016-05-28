package com.example.sumit.apple.models;

import com.example.sumit.apple.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumit on 3/20/2016.
 */
public class ProductData {

    public static List<ProductItem> getProductItems() {
        return toList(
                new ProductItem().withName("Dog").withImage(R.drawable.ic_pets_black_48dp),
                new ProductItem().withName("CAT").withImage(R.drawable.ic_casino_black_48dp),
                new ProductItem().withName("Pet Food").withImage(R.drawable.ic_local_dining_black_48dp),
                new ProductItem().withName("Pet Boarding").withImage(R.drawable.ic_location_city_black_48dp),
                new ProductItem().withName("Pet Transportation").withImage(R.drawable.ic_local_shipping_black_48dp),
                new ProductItem().withName("Pet Doctor").withImage(R.drawable.ic_local_hospital_black_48dp)
                );
    }

    private static List<ProductItem> toList(ProductItem... productItems) {
        return Arrays.asList(productItems);
    }
}
