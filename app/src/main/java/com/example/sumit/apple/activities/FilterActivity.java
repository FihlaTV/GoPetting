package com.example.sumit.apple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.FilterCategory;
import com.example.sumit.apple.models.FilterCategoryData;
import com.example.sumit.apple.models.FilterCheckBox;
import com.example.sumit.apple.models.FilterSubCategory;
import com.example.sumit.apple.models.FilterSubCategoryData;
import com.example.sumit.apple.models.FilteredItems;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.OAuthTokenService;
import com.example.sumit.apple.network.RetrofitSingleton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity {

    private static final int PRODUCT_CATEGORY_ID = 1;
    private RecyclerView rvFilterCategory;
    private RecyclerView rvFilterSubCategory;
    private FastItemAdapter fastAdapterFilterCategory;
    private FastItemAdapter fastAdapterFilterSubCategory;
    private LinearLayoutManager mLayoutManagerCategory;
    private Credential credential;
    private LinearLayoutManager mLayoutManagerSubCategory;

    //------------------TODO: Check and delete unncessary codes


//    private ArrayList<FilteredItems> mFilteredItems = new ArrayList<>();

    private ArrayList<FilterCheckBox> breedNameCheckbox = new ArrayList<>();
    private ArrayList<FilterCheckBox> genderCheckbox = new ArrayList<>();
    private ArrayList<FilterCheckBox> sizeCheckbox = new ArrayList<>();
    private ArrayList<FilterCheckBox> breedTypeCheckbox = new ArrayList<>();
    private Intent intent;
    private Bundle extras;
    private int filterParameterStatus;
    private List<FilterSubCategory> breedNameData;
    private List<FilterSubCategory> genderData;
    private List<FilterSubCategory> sizeData;
    private List<FilterSubCategory> breedTypeData;
    private static int filterCategorySelected = -1;     //Setting Default value as a Uninitialized sign

    private ArrayList<ArrayList<FilterCheckBox>> filterModels = new ArrayList<>();


    //-------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);

        intent = getIntent();
        extras = intent.getExtras();
        filterParameterStatus = extras.getInt("FILTER_PARAMETER_STATUS");

        if(filterParameterStatus != 10){            // 10 = Filter is empty, 11= Filled

            //Initialize filter data.
        }else {



           getServerData();
        }

        initActivity();




    }

    private void initActivity() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_headerbar);     //TODO: Check if ButterKnife should be used for whole app
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvFilterCategory = (RecyclerView) findViewById(R.id.rv_filter_category);
        rvFilterSubCategory = (RecyclerView) findViewById(R.id.rv_filter_sub_category);

//        Filter Category RecyclerView setup
        fastAdapterFilterCategory = new FastItemAdapter();
        fastAdapterFilterCategory.withSelectable(true);
        fastAdapterFilterCategory.add(FilterCategoryData.getItems());        //Adding Filter Category Data

        rvFilterCategory.setAdapter(fastAdapterFilterCategory);

        mLayoutManagerCategory = new LinearLayoutManager(this);
        mLayoutManagerCategory.setOrientation(LinearLayoutManager.VERTICAL);
        rvFilterCategory.setLayoutManager(mLayoutManagerCategory);


        // Filter SubCategory RecyclerView setup
        fastAdapterFilterSubCategory = new FastItemAdapter();
        fastAdapterFilterSubCategory.withSelectable(true);
        fastAdapterFilterSubCategory.withMultiSelect(true);


//        getServerData();

//        fastAdapterFilterSubCategory.add();        //Adding Filter SubCategory Data

        rvFilterSubCategory.setAdapter(fastAdapterFilterSubCategory);

        mLayoutManagerSubCategory = new LinearLayoutManager(this);
        mLayoutManagerSubCategory.setOrientation(LinearLayoutManager.VERTICAL);
        rvFilterSubCategory.setLayoutManager(mLayoutManagerSubCategory);

        setClickListeners();
    }

    private void setClickListeners() {

        fastAdapterFilterCategory.withOnClickListener(new FastAdapter.OnClickListener<FilterCategory>() {
            @Override
            public boolean onClick(View v, IAdapter<FilterCategory> adapter, FilterCategory item, int position) {
//                Toast.makeText(v.getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0:
                        fastAdapterFilterSubCategory.clear();
//                        FilteredItems.categorySelected = 0;
                        filterCategorySelected = 0;
//                        getServerData();
                        fastAdapterFilterSubCategory.add(breedNameData);
                        break;

                    case 1:
                        fastAdapterFilterSubCategory.clear();

//                        FilteredItems.categorySelected = 1;
                        filterCategorySelected = 1;
                        fastAdapterFilterSubCategory.add(genderData);
                        break;
                    case 2:
                        fastAdapterFilterSubCategory.clear();
//                        FilteredItems.categorySelected = 2;
                        filterCategorySelected = 2;
                        fastAdapterFilterSubCategory.add(sizeData);
                        break;
                    case 3:
                        fastAdapterFilterSubCategory.clear();
//                        FilteredItems.categorySelected = 3;
                        filterCategorySelected = 3;
                        fastAdapterFilterSubCategory.add(breedTypeData);
                        break;
                    default:
                        Toast.makeText(v.getContext(), "Exception: Inside fastAdapterFilterCategory Case Statement", Toast.LENGTH_SHORT).show();
                        break;

                    }
                return true;        //TODO: Check this value whether it should be true or false?
            }
        });

//        fastAdapterFilterSubCategory.withOnClickListener(new FastAdapter.OnClickListener<FilterSubCategory>() {
//            @Override
//            public boolean onClick(View v, IAdapter<FilterSubCategory> adapter, FilterSubCategory item, int position) {
//                Toast.makeText(v.getContext(),"withOnClickListener", Toast.LENGTH_SHORT).show();
////                filterItemListClicked(item, position);
////
////                item.setChecked(!item.isChecked());
//
////                mFilteredItems.get(FilteredItems.categorySelected).filterCheckbox.get(position).setChecked(!item.isChecked());
////                mFilteredItems.get(FilteredItems.categorySelected).filterCheckbox.get(position).setName(item.getSubCategoryName());
//
//                return false;
//            }
//        });


        fastAdapterFilterSubCategory.withOnPreClickListener(new FastAdapter.OnClickListener<FilterSubCategory>() {        //Used withOnPreClickListener for Checkbox(not sure if withOnClickListener will also work)
            @Override
            public boolean onClick(View v, IAdapter<FilterSubCategory> adapter, FilterSubCategory item, int position) {
//                Toast.makeText(v.getContext(),"withOnPreClickListener", Toast.LENGTH_SHORT).show();
                // consume otherwise radio/checkbox will be deselected
                item.setChecked(!item.isChecked());
                fastAdapterFilterSubCategory.notifyAdapterDataSetChanged();     //To notify adapter that data is changed
                return true;
            }
        });




    }

    private void filterItemListClicked(FilterSubCategory item, int position) {


        if (FilteredItems.categorySelected == 0) {
            breedNameCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());

//            filterValAdapter = new FilterValRecyclerAdapter(this, R.layout.filter_list_val_item_layout, sizeMultipleListModels, MainFilterModel.SIZE);
        } else if (FilteredItems.categorySelected == 1) {
            genderCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
        } else if (FilteredItems.categorySelected == 2) {
            sizeCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
        } else if (FilteredItems.categorySelected == 3) {
            breedTypeCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
        } else {
            Toast.makeText(FilterActivity.this, "Error: Inside FilterItemListClicked", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(FilterActivity.this, "Temp: Inside FilterItemListClicked", Toast.LENGTH_SHORT).show();
    }

    private void getServerData() {

            final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
            credential = oAuthTokenService.getAccessTokenWithID("default");

            if(credential == null || credential.getAccess_token()==null || oAuthTokenService.isExpired("default"))
            {
                oAuthTokenService.authenticateUsingOAuth( new Controller.MethodsCallback<Credential>()
                {
                    @Override public void failure(Throwable throwable)
                    {
                        Toast.makeText(FilterActivity.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                    }
                    @Override public void success(Credential credential)
                    {
                        if(credential != null)
                        {
                            oAuthTokenService.saveTokenWithID(credential, "default");

                            getBreedNamesData();

                        }
                    }
                    @Override public void responseBody(Call<Credential> call)
                    {

                    }
                });
            }else {

                getBreedNamesData();

            }
        }

    private void getBreedNamesData(){

    Controller.GetBreedNames retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetBreedNames.class);
    Call<List<FilterSubCategory>> call = retrofitSingleton.getBreedNames("Bearer " + credential.getAccess_token(),PRODUCT_CATEGORY_ID);
    call.enqueue(new Callback<List<FilterSubCategory>>() {
        @Override
        public void onResponse(Call<List<FilterSubCategory>> call, Response<List<FilterSubCategory>> response) {
            if (response.isSuccessful()) {


//                FilteredItems.categorySelected = 0;
//               fastAdapterFilterSubCategory.add(response.body());        //Adding Filter Category Data

                breedNameData =response.body();
                initFilterData();



            } else {
                Log.d("Error Response", "FilterActivity.getBreedNamesData :Error Response");
            }
        }

        @Override
        public void onFailure(Call<List<FilterSubCategory>> call, Throwable t) {
            Toast.makeText(FilterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
        }
    });
}

    private void initFilterData() {

        for (int i = 0; i < breedNameData.size(); i++) {   //Initialize breedNameCheckbox Data with disable checkbox

            FilterCheckBox filterCheckBox = new FilterCheckBox();
            filterCheckBox.setChecked(false);
            breedNameCheckbox.add(filterCheckBox);
        }

        for (int i = 0; i < 2; i++) {                   //Initialize genderCheckbox Data  with disable checkbox

            FilterCheckBox filterCheckBox = new FilterCheckBox();
            filterCheckBox.setChecked(false);
            genderCheckbox.add(filterCheckBox);
        }


        for (int i = 0; i < 3; i++) {                   //Initialize sizeCheckbox Data  with disable checkbox

            FilterCheckBox filterCheckBox = new FilterCheckBox();
            filterCheckBox.setChecked(false);
            sizeCheckbox.add(filterCheckBox);
        }


        for (int i = 0; i < 2; i++) {                   //Initialize breedTypeCheckbox Data with disable checkbox

            FilterCheckBox filterCheckBox = new FilterCheckBox();
            filterCheckBox.setChecked(false);
            breedTypeCheckbox.add(filterCheckBox);
        }

        filterModels.add(breedNameCheckbox);
        filterModels.add(genderCheckbox);
        filterModels.add(sizeCheckbox);
        filterModels.add(breedTypeCheckbox);


        //Setting first SubCategoryFilter data
        filterCategorySelected = 0;
        if(!breedNameData.isEmpty()) {
            fastAdapterFilterSubCategory.add(breedNameData);        //Adding Filter Category Data
        }else {
            Toast.makeText(FilterActivity.this,"Error: breedNameData is empty", Toast.LENGTH_SHORT).show();
        }

        genderData = FilterSubCategoryData.getGenderData();
        sizeData = FilterSubCategoryData.getSizeData();
        breedTypeData = FilterSubCategoryData.getBreedTypeData();


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
        return true;
    }
}
