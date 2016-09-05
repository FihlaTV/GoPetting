package com.example.sumit.apple.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.Dog;
import com.example.sumit.apple.models.FilteredItems;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.DogService;
import com.example.sumit.apple.network.OAuthTokenService;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by Sumit on 7/26/2016.
 */

public class DogActivity extends AppCompatActivity {

    private static final int IDENTIFIER = 1;
    public static FastItemAdapter fastAdapterDogs;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    protected LinearLayout mLLFilterSort;
    private LinearLayout mSort;
    private LinearLayout mFilter;
    private ArrayList<FilteredItems> mFilteredItems;
    private RecyclerView rvDogs;
    private static int FILTER_PARAMETER_STATUS = 10;    // FILTER_PARAMETER_STATUS = 10 Filter is empty, 11= Filled



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     //TODO: Check if ButterKnife should be used for whole app
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.dogActivityTitle);

        //Inflating layout_filter_sort
        mLLFilterSort = (LinearLayout) findViewById(R.id.ll_filter_sort_container);

//        Setting up filter & sort listeners
        setupListeners();

//TODO: Check this optimize it (moving to fragments)

        rvDogs = (RecyclerView) findViewById(R.id.dogs_recycler_view);

        fastAdapterDogs = new FastItemAdapter();
        fastAdapterDogs.withSelectable(true);

        fastAdapterDogs.withOnClickListener(new FastAdapter.OnClickListener<Dog>() {
            @Override
            public boolean onClick(View v, IAdapter<Dog> adapter, Dog item, int position) {
//                Toast.makeText(v.getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DogActivity.this,DogDetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("itemId", item.getItemId());
                b.putString("name",item.getName());
                b.putInt("unitPrice",item.getUnitPrice());
                b.putInt("mrp",item.getMrp());
                b.putInt("discount",item.getDiscount());
                b.putInt("likes",item.getLikes());
                b.putString("imageUrl",item.getImageUrl());
                intent.putExtras(b);
                startActivity(intent);
                return false;
            }
        });

        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvDogs.setAdapter(fastAdapterDogs);



        initAdapterData();

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        // Set layout manager to position the items
        rvDogs.setLayoutManager(gridLayoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting up recyclerview OnScrollListener
        rvDogs.addOnScrollListener(new HidingScrollListener() {

            @Override
            public void onIdle() {
                showFilterSortLayout();
//                Toast.makeText(DogActivity.this,"onShow.2 Executed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onHide() {
                hideFilterSortLayout();
//                Toast.makeText(DogActivity.this,"onHide Executed",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onShow() {
                showFilterSortLayout();
//                Toast.makeText(DogActivity.this,"onShow Executed",Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void setupListeners() {

        mSort = (LinearLayout) findViewById (R.id.ll_product_list_sort);
        mSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(DogActivity.this,"Sort Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        mFilter = (LinearLayout) findViewById (R.id.ll_product_list_filter);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(DogActivity.this, FilterActivity.class);
                Bundle b = new Bundle();
                b.putInt("filter_parameter_status", FILTER_PARAMETER_STATUS);    // FILTER_PARAMETER_STATUS = 10 Filter is empty, 11= Filled

                if (FILTER_PARAMETER_STATUS == 11) {
                   b.putParcelable("post_filtered_items", Parcels.wrap(mFilteredItems));
                }
                intent.putExtras(b);

                startActivityForResult(intent,IDENTIFIER);

            }
        });


    }

    private void showFilterSortLayout() {

//        Delaying showing up of bottom bar.
        mLLFilterSort.postDelayed(new Runnable() {
            public void run() {
                mLLFilterSort.setVisibility(View.VISIBLE);
                mLLFilterSort.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        }, 100);
    }

    private void hideFilterSortLayout() {
        mLLFilterSort.animate().translationY(mLLFilterSort.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        mLLFilterSort.setVisibility(View.GONE);
    }

    public void initAdapterData() {

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
                            Toast.makeText(DogActivity.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                        }
                        @Override public void success(Credential credential)
                        {
                            if(credential != null)
                            {
                                oAuthTokenService.saveTokenWithID(credential, "default");

                                DogService.getData(credential.getAccess_token(), new Controller.MethodsCallback<List<Dog>>() {
                                    @Override
                                    public void failure(Throwable throwable) {
                                        Toast.makeText(DogActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                                    }

                                    @Override
                                    public void success(List<Dog> dogs) {
                                        fastAdapterDogs.add(dogs);
                                        configureFilterPredicate();

                                    }

                                    @Override
                                    public void responseBody(Call<List<Dog>> call)    //Check if this method can be used for any meaningful purpose.
                                    {

                                    }
                                });

                            }
                        }
                        @Override public void responseBody(Call<Credential> call)
                        {

                        }
                    });
        }else {

            DogService.getData(credential.getAccess_token(), new Controller.MethodsCallback<List<Dog>>() {
                @Override
                public void failure(Throwable throwable) {
                    Toast.makeText(DogActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                }

                @Override
                public void success(List<Dog> dogs) {
                    fastAdapterDogs.add(dogs);
                    configureFilterPredicate();

                }

                @Override
                public void responseBody(Call<List<Dog>> call)    //Check if this method can be used for any meaningful purpose.
                {

                }
            });

        }
    }

    private void configureFilterPredicate() {

                fastAdapterDogs.withFilterPredicate(new IItemAdapter.Predicate<Dog>() {
                    @Override
                    public boolean filter(Dog item, CharSequence constraint) {
                        //return true if we should filter it out
                        //return false to keep it
//                        boolean bool = !item.getName().toLowerCase().contains(constraint.toString().toLowerCase());

                        boolean bool = splitFilterString(item, constraint);
//                String[] constraints = constraint.toString().split(":");
////                        Toast.makeText(DogActivity.this,constraints[0],Toast.LENGTH_SHORT).show();
//                String breedName = constraints[0];
//                String gender = constraints[1];
//                String breedName2="Maltese";
//                boolean breedNameCheck = item.getName().toLowerCase().contains(breedName.toLowerCase());
//                boolean breedNameCheck2 = item.getName().toLowerCase().contains(breedName2.toLowerCase());
//                boolean genderCheck = item.getGenderString().toLowerCase().contains(gender.toLowerCase());

//                return !((breedNameCheck || breedNameCheck2) && genderCheck);

//                return true;
//              ----------------------------------
//
//                        String[] categories = constraint.toString().split(":");
//
////                        Toast.makeText(DogActivity.this,constraints[0],Toast.LENGTH_SHORT).show();
//
//                        String breedName = categories[0];
//                        String gender = categories[1];
//                        String size = categories[2];
//                        String breedType = categories[3];
//
//                        String[] breedNames = categories[0].toString().split(",");
//
//                        boolean bool = false;
//
//                        for (int i = 0; i < categories[0].length(); i++) {
//
//                            bool = bool || item.getName().toLowerCase().contains(breedNames[i].toLowerCase());
//                        }
//
//---------------------------------------
//        boolean breedNameCheck = item.getName().toLowerCase().contains(breedNames[i].toLowerCase());
//        boolean breedNameCheck2 = item.getName().toLowerCase().contains(breedName2.toLowerCase());
//        boolean genderCheck = item.getGenderString().toLowerCase().contains(gender.toLowerCase());


                        return !bool;
//                        return bool;
                    }
                });



    }

    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;


        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState){
            super.onScrollStateChanged(recyclerView,newState);

            if(newState==RecyclerView.SCROLL_STATE_IDLE){
                onIdle();
                controlsVisible=true;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
        }

        public abstract void onHide();
        public abstract void onShow();
        public abstract void onIdle();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IDENTIFIER) {
            if(resultCode == Activity.RESULT_OK){

                FILTER_PARAMETER_STATUS = 11;       //Setting so that filteritems parcel can be sent back
                mFilteredItems = (ArrayList<FilteredItems>) Parcels.unwrap(data.getParcelableExtra("filtered_items"));

                String filterString = createFilterString();

                fastAdapterDogs.filter(filterString);
                fastAdapterDogs.notifyAdapterDataSetChanged();      //Refreshing the data
                rvDogs.smoothScrollToPosition(0);               //TODO: Find a better way(like using Progressbar); Scroll to top recyclerview item

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Nothing do be done as no items were filtered
            }
        }



    }

    // Create filter string for filtering item using fastadapter.filter();
    private String createFilterString() {

        //FilterCategory Separator is : colon
        //FilterSubCategory Separator is , comma
        
        String filterString = "";
        int check = 0;

        //BreedNames
            int i = 0;
                for (int j = 0; j < mFilteredItems.get(i).getSubCategoryNames().size(); j++){
                    if(check == 0){
                        check = 1;
                    }
                    filterString = filterString + mFilteredItems.get(i).getSubCategoryNames().get(j);
                    if(j != (mFilteredItems.get(i).getSubCategoryNames().size() - 1)){        //Check whether it is last item of FilterCategory; Last item should not be appended with ','
                        filterString = filterString + ",";
                    }
                }

        if(check == 0){
            filterString = "BLANK";     //Just a default value to identify no filter is added for this category
        }

        check = 0;      //Resetting it

        filterString = filterString + ":";

            
        //Gender
            i = 1;
                for (int k = 0; k < mFilteredItems.get(i).getSubCategoryNames().size(); k++){
                    if(check == 0){
                        check = 1;
                    }
                    filterString = filterString + mFilteredItems.get(i).getSubCategoryNames().get(k);
                    if(k != (mFilteredItems.get(i).getSubCategoryNames().size() - 1)){        //Check whether it is last item of FilterCategory; Last item should not be appended with ','
                        filterString = filterString + ",";
                    }
                }

         if(check == 0){
             filterString = filterString + "BLANK";     //Just a default value to identify no filter is added for this category
        }

        check = 0;      //Resetting it

        filterString = filterString + ":";

        //Size
            i = 2;
                for (int l = 0; l < mFilteredItems.get(i).getSubCategoryNames().size(); l++){
                    if(check == 0){
                        check = 1;
                    }
                    filterString = filterString + mFilteredItems.get(i).getSubCategoryNames().get(l);
                    if(l != (mFilteredItems.get(i).getSubCategoryNames().size() - 1)){        //Check whether it is last item of FilterCategory; Last item should not be appended with ','
                        filterString = filterString + ",";
                    }
                }

        if(check == 0){
            filterString = filterString + "BLANK";     //Just a default value to identify no filter is added for this category
        }

        check = 0;      //Resetting it

        filterString = filterString + ":";

        //BreedType
            i = 3;
                for (int m = 0; m < mFilteredItems.get(i).getSubCategoryNames().size(); m++){
                    if(check == 0){
                        check = 1;
                    }
                    filterString = filterString + mFilteredItems.get(i).getSubCategoryNames().get(m);
                    if(m != (mFilteredItems.get(i).getSubCategoryNames().size() - 1)){        //Check whether it is last item of FilterCategory; Last item should not be appended with ','
                        filterString = filterString + ",";
                    }
                }


        if(check == 0){
            filterString = filterString + "BLANK";     //Just a default value to identify no filter is added for this category
        }

        return filterString;

    }

    //Split FilterString for fastadapter.withFilterPredicate();
    private boolean splitFilterString(Dog item, CharSequence constraint) {

        String[] categories = constraint.toString().split(":");

        String breedName = categories[0];
        String gender = categories[1];
        String size = categories[2];
        String breedType = categories[3];



        String[] breedNames = categories[0].split(",");
        boolean boolBreedName = false;

        if(breedNames[0].equalsIgnoreCase("BLANK")){
            boolBreedName = true;
        }else {
            for (int i = 0; i < breedNames.length; i++) {

                boolBreedName = boolBreedName || item.getName().toLowerCase().contains(breedNames[i].toLowerCase());

//            boolBreedName = boolBreedName || item.getName().equalsIgnoreCase(breedNames[i]);

            }
        }

        String[] genders = categories[1].split(",");
        boolean boolGender = false;

        if(genders[0].equalsIgnoreCase("BLANK")){
            boolGender = true;
        }else {
            for (int j = 0; j < genders.length; j++) {

//            boolGender = boolGender || item.getGenderString().toLowerCase().contains(genders[j].toLowerCase());
                boolGender = boolGender || item.getGenderString().equalsIgnoreCase(genders[j]);
            }
        }

        String[] sizes = categories[2].split(",");
        boolean boolSize = false;

        if(sizes[0].equalsIgnoreCase("BLANK")){
            boolSize = true;
        }else {
            for (int k = 0; k < sizes.length; k++) {
                boolSize = boolSize || item.getSize().equalsIgnoreCase(sizes[k]);
            }
        }

        String[] breedTypes = categories[3].split(",");
        boolean boolBreedTypes = false;

        if(breedTypes[0].equalsIgnoreCase("BLANK")){
            boolBreedTypes = true;
        }else {
            for (int l = 0; l < breedTypes.length; l++) {
                boolBreedTypes = boolBreedTypes || item.getBreedType().equalsIgnoreCase(breedTypes[l]);
            }
        }

        return boolBreedName && boolGender && boolSize && boolBreedTypes;
    }

}
