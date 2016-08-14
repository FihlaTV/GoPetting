package com.example.sumit.apple.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import com.example.sumit.apple.R;
import com.example.sumit.apple.fragments.DogDetailsFragment;
import com.example.sumit.apple.views.TypefaceTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import me.relex.circleindicator.CircleIndicator;

public class DogDetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mProductGalleryPager;
    private ProductGalleryPagerAdapter mProductGalleryPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_details);

        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //Hide Actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Product Gallery Pager
        mProductGalleryPager = (ViewPager) findViewById(R.id.product_gallery_pager);
        mProductGalleryPagerAdapter = new ProductGalleryPagerAdapter(getSupportFragmentManager());
        mProductGalleryPager.setAdapter(mProductGalleryPagerAdapter);

        //Adding Circle Indicator with ViewPager
        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        circleIndicator.setViewPager(mProductGalleryPager);

        TypefaceTextView mBrandName = (TypefaceTextView) findViewById(R.id.tv_brand_name);
        mBrandName.setText("Bulldog");
        TypefaceTextView mUnitPrice = (TypefaceTextView) findViewById(R.id.tv_item_unit_price);
        mUnitPrice.setText("Rs."+ 40050);
        TypefaceTextView mItemMrp = (TypefaceTextView) findViewById(R.id.tv_item_mrp);
        mItemMrp.setText("Rs."+ 45000);
        TypefaceTextView mItemDiscount = (TypefaceTextView) findViewById(R.id.tv_item_discount);
        mItemDiscount.setText(11 + "% Off");

        Button mAddToCart = (Button) findViewById(R.id.add_to_cart);
        Button mBuyNow = (Button) findViewById(R.id.buy_now);

//        ExpandableTextView mDeliveryOptions = (ExpandableTextView) findViewById(R.id.expand_text_view);
//        mDeliveryOptions.setText(getString(R.string.dummy_text));

        ExpandableTextView mDeliveryOptions = (ExpandableTextView) findViewById(R.id.delivery_options).findViewById(R.id.expand_text_view);
        mDeliveryOptions.setText(getString(R.string.dummy_text));

        ExpandableTextView mCharacter = (ExpandableTextView) findViewById(R.id.character).findViewById(R.id.expand_text_view);
        mCharacter.setText(getString(R.string.dummy_text));

        ExpandableTextView mLifeSize = (ExpandableTextView) findViewById(R.id.life_size).findViewById(R.id.expand_text_view);
        mLifeSize.setText(getString(R.string.dummy_text));







    }

    public static class ProductGalleryPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        public ProductGalleryPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DogDetailsFragment.newInstance(R.drawable.dog1, "Page # 1");
                case 1:
                    return DogDetailsFragment.newInstance(R.drawable.dog2, "Page # 2");
                case 2:
                    return DogDetailsFragment.newInstance(R.drawable.dog3, "Page # 3");
                case 3:
                    return DogDetailsFragment.newInstance(R.drawable.dog4, "Page # 4");
                case 4:
                    return DogDetailsFragment.newInstance(R.drawable.dog5, "Page # 5");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dog_details, menu);
        return true;
    }
}
