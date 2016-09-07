package com.example.sumit.apple.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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

import org.parceler.Parcels;

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

    private ArrayList<FilteredItems> filteredItems = new ArrayList<>();
    private ArrayList<String> breedNameSelected = new ArrayList<>();
    private ArrayList<String> genderSelected = new ArrayList<>();
    private ArrayList<String> sizeSelected = new ArrayList<>();
    private ArrayList<String> breedTypeSelected = new ArrayList<>();

    private Button buttonApply;
    private Button buttonClear;
    private static int FILTER_PARAMETER_STATUS = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);

        intent = getIntent();
        extras = intent.getExtras();
        FILTER_PARAMETER_STATUS = extras.getInt("filter_parameter_status");

        if(FILTER_PARAMETER_STATUS == 11){            // 10 = Filter is empty, 11= Filled

            filteredItems = (ArrayList<FilteredItems>) Parcels.unwrap(intent.getParcelableExtra("post_filtered_items"));
        }

        //If filteredItem is null then no parcel is sent from parent activity, this could be because user moved to parent/other screen of DogActivity
        // after filtering once and then again came back to Dogactivity and tried to filter again.
        if(filteredItems == null){
            FILTER_PARAMETER_STATUS = 10;       //Resetting since no filteredItems(Parcel) received.
            filteredItems = new ArrayList<>();
        }
        getServerData();

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

        rvFilterSubCategory.setAdapter(fastAdapterFilterSubCategory);

        mLayoutManagerSubCategory = new LinearLayoutManager(this);
        mLayoutManagerSubCategory.setOrientation(LinearLayoutManager.VERTICAL);
        rvFilterSubCategory.setLayoutManager(mLayoutManagerSubCategory);

        buttonApply = (Button) findViewById(R.id.btn_apply);
        buttonClear = (Button) findViewById(R.id.btn_clear);

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


        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < filteredItems.size(); i++) {        //Clear filteredItems so filter selections are cleared
                    filteredItems.get(i).clearSubCategoryNames();
                }

                //Clear SubCategory Data and notify SubCategoryAdapter of the changes done.
                for (FilterSubCategory model : breedNameData) {
                    model.setChecked(false);
                }

                for (FilterSubCategory model : genderData) {
                    model.setChecked(false);
                }

                for (FilterSubCategory model : sizeData) {
                    model.setChecked(false);
                }

                for (FilterSubCategory model : breedTypeData) {
                    model.setChecked(false);
                }


                fastAdapterFilterSubCategory.notifyAdapterDataSetChanged(); //Notify Adapter of the cleared items

                Intent returnIntent = new Intent();
                returnIntent.putExtra("filtered_items", Parcels.wrap(filteredItems));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FILTER_PARAMETER_STATUS == 11){      //Clear filteredItems so that fresh filter selection can be added

                    for (int i = 0; i < filteredItems.size(); i++) {
                        filteredItems.get(i).clearSubCategoryNames();
                    }
                }

                for (FilterSubCategory model : breedNameData) {
                    if (model.isChecked()) {
                        filteredItems.get(FilteredItems.INDEX_BREED_NAME).getSubCategoryNames().add(model.getSubCategoryName());
                    }
                }

                for (FilterSubCategory model : genderData) {
                    if (model.isChecked()) {
                        filteredItems.get(FilteredItems.INDEX_GENDER).getSubCategoryNames().add(model.getSubCategoryName());
                    }
                }

                for (FilterSubCategory model : sizeData) {
                    if (model.isChecked()) {
                        filteredItems.get(FilteredItems.INDEX_SIZE).getSubCategoryNames().add(model.getSubCategoryName());
                    }
                }

                for (FilterSubCategory model : breedTypeData) {
                    if (model.isChecked()) {
                        filteredItems.get(FilteredItems.INDEX_BREED_TYPE).getSubCategoryNames().add(model.getSubCategoryName());
                    }
                }


                breedNameSelected = filteredItems.get(FilteredItems.INDEX_BREED_NAME).getSubCategoryNames();
                genderSelected = filteredItems.get(FilteredItems.INDEX_GENDER).getSubCategoryNames();
                sizeSelected = filteredItems.get(FilteredItems.INDEX_SIZE).getSubCategoryNames();
                breedTypeSelected = filteredItems.get(FilteredItems.INDEX_BREED_TYPE).getSubCategoryNames();

                //Check whether any filter is selected
                if(breedNameSelected.isEmpty() && genderSelected.isEmpty() && sizeSelected.isEmpty() && breedTypeSelected.isEmpty()){
                    //No Items are filterd, so returning without data
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }else{
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("filtered_items", Parcels.wrap(filteredItems));
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }


            }
        });



    }

//    private void filterItemListClicked(FilterSubCategory item, int position) {
//
//
//        if (FilteredItems.categorySelected == 0) {
//            breedNameCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
//
////            filterValAdapter = new FilterValRecyclerAdapter(this, R.layout.filter_list_val_item_layout, sizeMultipleListModels, MainFilterModel.SIZE);
//        } else if (FilteredItems.categorySelected == 1) {
//            genderCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
//        } else if (FilteredItems.categorySelected == 2) {
//            sizeCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
//        } else if (FilteredItems.categorySelected == 3) {
//            breedTypeCheckbox.get(position).setChecked(!item.isChecked()).setName(item.getSubCategoryName());
//        } else {
//            Toast.makeText(FilterActivity.this, "Error: Inside FilterItemListClicked", Toast.LENGTH_SHORT).show();
//        }
//        Toast.makeText(FilterActivity.this, "Temp: Inside FilterItemListClicked", Toast.LENGTH_SHORT).show();
//    }

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

        if(FILTER_PARAMETER_STATUS == 11){
            //
            for (FilterSubCategory model : breedNameData) {
                for (int i = 0; i < filteredItems.get(FilteredItems.INDEX_BREED_NAME).getSubCategoryNames().size(); i++) {

                    String string = filteredItems.get(FilteredItems.INDEX_BREED_NAME).getSubCategoryNames().get(i);
                    if (model.getSubCategoryName().equalsIgnoreCase(string)) {
                        model.setChecked(true);
                    }
                }
            }

            for (FilterSubCategory model : genderData) {
                for (int i = 0; i < filteredItems.get(FilteredItems.INDEX_GENDER).getSubCategoryNames().size(); i++) {

                    String string = filteredItems.get(FilteredItems.INDEX_GENDER).getSubCategoryNames().get(i);
                    if (model.getSubCategoryName().equalsIgnoreCase(string)) {
                        model.setChecked(true);
                    }
                }
            }

            for (FilterSubCategory model : sizeData) {
                for (int i = 0; i < filteredItems.get(FilteredItems.INDEX_SIZE).getSubCategoryNames().size(); i++) {

                    String string = filteredItems.get(FilteredItems.INDEX_SIZE).getSubCategoryNames().get(i);
                    if (model.getSubCategoryName().equalsIgnoreCase(string)) {
                        model.setChecked(true);
                    }
                }
            }

            for (FilterSubCategory model : breedTypeData) {
                for (int i = 0; i < filteredItems.get(FilteredItems.INDEX_BREED_TYPE).getSubCategoryNames().size(); i++) {

                    String string = filteredItems.get(FilteredItems.INDEX_BREED_TYPE).getSubCategoryNames().get(i);
                    if (model.getSubCategoryName().equalsIgnoreCase(string)) {
                        model.setChecked(true);
                    }
                }
            }
        fastAdapterFilterSubCategory.notifyAdapterDataSetChanged();

        }else {
            for (int i = 0; i < 4; i++) {                           //Initialize filteredItems
                FilteredItems filterItem = new FilteredItems();
                filteredItems.add(filterItem);
            }
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
        return true;
    }
}
