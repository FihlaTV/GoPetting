package com.example.sumit.apple.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.adapters.CustomExpandableListAdapter;
import com.example.sumit.apple.fragments.DeliveryOptionsFragment;
import com.example.sumit.apple.fragments.DogDetailsFragment;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.DogDetails;
import com.example.sumit.apple.models.ExpandableListDataPump;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.OAuthTokenService;
import com.example.sumit.apple.network.RetrofitSingleton;
import com.example.sumit.apple.views.TypefaceTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogDetailsActivity extends AppCompatActivity {

    private static Toolbar mToolbar;
    private static ViewPager mProductGalleryPager;
    private static ProductGalleryPagerAdapter mProductGalleryPagerAdapter;
    private static Intent intent;
    private static Bundle extras;
    private static int itemId;
    private static String name;
    private static int unitPrice;
    private static int mrp;
    private static int discount;
    private static int likes;
    private static String imageUrl;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<List<String>>> expandableListDetail;
    CircleIndicator circleIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_details);

    // To make sure the keyboard only pops up when a user clicks into an EditText
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

         intent = getIntent();
         extras = intent.getExtras();
         itemId = extras.getInt("itemId");
         name = extras.getString("name");
         unitPrice = extras.getInt("unitPrice");
         mrp = extras.getInt("mrp");
         discount = extras.getInt("discount");
         likes = extras.getInt("likes");
         imageUrl = extras.getString("imageUrl");

        getDogDetailsData();

        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //Hide Actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initializing these 3 views so that it could be setup later
        mProductGalleryPager = (ViewPager) findViewById(R.id.product_gallery_pager);
        circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);


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



    // Setting up Delivery Options Fragment

        if (findViewById(R.id.delivery_options_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.delivery_options_fragment_container, DeliveryOptionsFragment.newInstance(itemId))
                    .commit();




        }


    }


    private void getDogDetailsData() {

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        Credential credential = oAuthTokenService.getAccessTokenWithID("default");

        if(credential == null || credential.getAccess_token()==null || oAuthTokenService.isExpired("default"))
        {
            oAuthTokenService.authenticateUsingOAuth( new Controller.MethodsCallback<Credential>()
            {
                @Override public void failure(Throwable throwable)
                {
                    Toast.makeText(DogDetailsActivity.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        Controller.GetDogDetails retrofitGetDogData = RetrofitSingleton.getInstance().create(Controller.GetDogDetails.class);
                        Call<DogDetails> call = retrofitGetDogData.getDogDetailsData("Bearer " + credential.getAccess_token(),itemId);
                        call.enqueue(new Callback<DogDetails>() {
                            @Override
                            public void onResponse(Call<DogDetails> call, Response<DogDetails> response) {
                                if (response.isSuccessful()) {

                                    initializeViews(response.body());


                                } else {
                                    Log.d("Error Response", "DogDetailsActivity.getDogDetailsData :Error Response");
                                }
                            }

                            @Override
                            public void onFailure(Call<DogDetails> call, Throwable t) {
                                Toast.makeText(DogDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                            }
                        });


                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            Controller.GetDogDetails retrofitGetDogData = RetrofitSingleton.getInstance().create(Controller.GetDogDetails.class);
            Call<DogDetails> call = retrofitGetDogData.getDogDetailsData("Bearer " + credential.getAccess_token(),itemId);
            call.enqueue(new Callback<DogDetails>() {
                @Override
                public void onResponse(Call<DogDetails> call, Response<DogDetails> response) {
                    if (response.isSuccessful()) {

                        initializeViews(response.body());


                    } else {
                        Log.d("Error Response", "DogDetailsActivity.getDogDetailsData :Error Response");
                    }
                }

                @Override
                public void onFailure(Call<DogDetails> call, Throwable t) {
                    Toast.makeText(DogDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                }
            });


        }
    }


    private void initializeViews(DogDetails dogDetails){


        // Set Product Gallery Pager
//        mProductGalleryPager = (ViewPager) findViewById(R.id.product_gallery_pager);
        mProductGalleryPagerAdapter = new ProductGalleryPagerAdapter(getSupportFragmentManager(),dogDetails);
        mProductGalleryPager.setAdapter(mProductGalleryPagerAdapter);

        //Adding Circle Indicator with ViewPager
//        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        circleIndicator.setViewPager(mProductGalleryPager);


//        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData(dogDetails);
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        //temp code
        setListViewHeight(expandableListView);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {                                 //TODO: Remove all unwanted Toast text and their listener once not required.
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }


        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();
                return false;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);                        // This specific listener and below setListViewHeight methods are referenced from
                                                                                // http://stackoverflow.com/questions/17696039/expandablelistview-inside-a-scrollview?noredirect=1&lq=1
                return false;
            }
        });



    }


    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }               // http://stackoverflow.com/questions/17696039/expandablelistview-inside-a-scrollview?noredirect=1&lq=1


    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }



    public static class ProductGalleryPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        private static DogDetails mDogDetails;


        public ProductGalleryPagerAdapter(FragmentManager fragmentManager,DogDetails mDetails) {
            super(fragmentManager);
            mDogDetails = mDetails;

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
                    return DogDetailsFragment.newInstance(DogDetailsActivity.imageUrl, "Page # 1");
                case 1:
                    return DogDetailsFragment.newInstance(mDogDetails.getImageUrl2(), "Page # 2");
                case 2:
                    return DogDetailsFragment.newInstance(mDogDetails.getImageUrl3(), "Page # 3");
                case 3:
                    return DogDetailsFragment.newInstance(mDogDetails.getImageUrl4(), "Page # 4");
                case 4:
                    return DogDetailsFragment.newInstance(mDogDetails.getImageUrl5(), "Page # 5");
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
