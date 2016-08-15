package com.example.sumit.apple.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.fragments.DogDetailsFragment;
import com.example.sumit.apple.views.TypefaceTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import me.relex.circleindicator.CircleIndicator;

public class DogDetailsActivity extends AppCompatActivity {

    private static Toolbar mToolbar;
    private static ViewPager mProductGalleryPager;
    private static ProductGalleryPagerAdapter mProductGalleryPagerAdapter;
    private static Intent intent;
    private static Bundle extras;
    private static int id;
    private static String name;
    private static int unitPrice;
    private static int mrp;
    private static int discount;
    private static int likes;
    private static String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_details);

         intent = getIntent();
         extras = intent.getExtras();
         id = extras.getInt("id");
         name = extras.getString("name");
         unitPrice = extras.getInt("unitPrice");
         mrp = extras.getInt("mrp");
         discount = extras.getInt("discount");
         likes = extras.getInt("likes");
         imageUrl = extras.getString("imageUrl");



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
        mBrandName.setText(name);
        TypefaceTextView mUnitPrice = (TypefaceTextView) findViewById(R.id.tv_item_unit_price);
        mUnitPrice.setText("Rs."+ unitPrice);


        TypefaceTextView mItemMrp = (TypefaceTextView) findViewById(R.id.tv_item_mrp);

        TypefaceTextView mItemDiscount = (TypefaceTextView) findViewById(R.id.tv_item_discount);


        if(discount > 0){
            mItemMrp.setVisibility(View.VISIBLE);
            mItemDiscount.setVisibility(View.VISIBLE);

            mItemMrp.setText("Rs." + mrp);
            mItemMrp.setPaintFlags(mItemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // To strike through text
            mItemDiscount.setText(discount + "% Off");

        }else {
            mItemMrp.setVisibility(View.GONE);
            mItemDiscount.setVisibility(View.GONE);
        }



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
                    return DogDetailsFragment.newInstance(imageUrl, "Page # 1");
                case 1:
                    return DogDetailsFragment.newInstance(imageUrl, "Page # 2");
                case 2:
                    return DogDetailsFragment.newInstance(imageUrl, "Page # 3");
                case 3:
                    return DogDetailsFragment.newInstance(imageUrl, "Page # 4");
                case 4:
                    return DogDetailsFragment.newInstance(imageUrl, "Page # 5");
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
