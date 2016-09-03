package com.example.sumit.apple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.Dog;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.DogService;
import com.example.sumit.apple.network.OAuthTokenService;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Sumit on 7/26/2016.
 */

public class DogActivity extends AppCompatActivity {

    private static final int GET_FILTER_PARAMETERS = 1;
    public static FastItemAdapter fastAdapterDogs;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    protected LinearLayout mLLFilterSort;
    private LinearLayout mSort;
    private LinearLayout mFilter;



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

        RecyclerView rvDogs = (RecyclerView) findViewById(R.id.dogs_recycler_view);

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
                fastAdapterDogs.filter("Labrador:Female");
//                fastAdapterDogs.filter("Beagle");
//                fastAdapterDogs.notifyAdapterDataSetChanged();

//                String abc = "Labrador:Female";
//                String[] constraints = abc.split(":");
//                String first = constraints[0];
//                String second = constraints[1];
//                Toast.makeText(DogActivity.this,constraints[0],Toast.LENGTH_SHORT).show();




                fastAdapterDogs.withFilterPredicate(new IItemAdapter.Predicate<Dog>() {
                    @Override
                    public boolean filter(Dog item, CharSequence constraint) {
                        //return true if we should filter it out
                        //return false to keep it
//                        boolean bool = !item.getName().toLowerCase().contains(constraint.toString().toLowerCase());

                        String[] constraints = constraint.toString().split(":");
//                        Toast.makeText(DogActivity.this,constraints[0],Toast.LENGTH_SHORT).show();
                        String breedName = constraints[0];
                        String gender = constraints[1];
String breedName2="Maltese";
                        boolean breedNameCheck = item.getName().toLowerCase().contains(breedName.toLowerCase());
                        boolean breedNameCheck2 = item.getName().toLowerCase().contains(breedName2.toLowerCase());
                        boolean genderCheck = item.getGenderString().toLowerCase().contains(gender.toLowerCase());

                        return !((breedNameCheck || breedNameCheck2) && genderCheck);

//                        return !item.getName().toLowerCase().contains(constraint.toString().toLowerCase());
//                        return bool;
                    }
                });

            }
        });

        mFilter = (LinearLayout) findViewById (R.id.ll_product_list_filter);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(DogActivity.this, FilterActivity.class);
                Bundle b = new Bundle();
                b.putInt("FILTER_PARAMETER_STATUS", 10);    // 10 = Filter is empty, 11= Filled
                intent.putExtras(b);

                startActivityForResult(intent,GET_FILTER_PARAMETERS);
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

                }

                @Override
                public void responseBody(Call<List<Dog>> call)    //Check if this method can be used for any meaningful purpose.
                {

                }
            });

        }
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



}
