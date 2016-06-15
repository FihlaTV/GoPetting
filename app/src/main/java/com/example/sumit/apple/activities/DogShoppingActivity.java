package com.example.sumit.apple.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.sumit.apple.R;
import com.example.sumit.apple.network.RetrofitServiceGenerator;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogShoppingActivity extends AppCompatActivity {

    public static FastItemAdapter fastAdapterDogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     //TODO: Check if ButterKnife should be used for whole app
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.dogActivityTitle);



//TODO: Check this optimize it (moving to fragments)

        RecyclerView rvDogs = (RecyclerView) findViewById(R.id.dogs_recycler_view);

        fastAdapterDogs = new FastItemAdapter();



        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvDogs.setAdapter(fastAdapterDogs);

//        // Initialize ProductItems
//        RetrofitServiceGenerator.initAdapterData();

        initAdapterData();

        // Set layout manager to position the items
        rvDogs.setLayoutManager(new GridLayoutManager(this, 2));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
//
//    public static void addAdapterData(List<RetrofitServiceGenerator.Dog> dogs){
//        fastAdapterDogs.add(dogs);
//    }


    public void initAdapterData() {

        RetrofitServiceGenerator.RetrofitService retrofitService = RetrofitServiceGenerator.createService(RetrofitServiceGenerator.RetrofitService.class);
        Call<List<RetrofitServiceGenerator.Dog>> call = retrofitService.getJsonData();

        call.enqueue(new Callback<List<RetrofitServiceGenerator.Dog>>()

        {
            @Override
            public void onResponse(Call<List<RetrofitServiceGenerator.Dog>> call, Response<List<RetrofitServiceGenerator.Dog>> response) {
                if (response.isSuccessful()) {
//                    int statusCode = response.code();
                    List<RetrofitServiceGenerator.Dog> dogs = response.body();

                    fastAdapterDogs.add(dogs);
                    int statusCode = response.code();


                } else {
                    // error response, no access to resource?
                    Log.d("Error Response", "Error Response");
                }
            }

            @Override
            public void onFailure(Call<List<RetrofitServiceGenerator.Dog>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });

    }

}
