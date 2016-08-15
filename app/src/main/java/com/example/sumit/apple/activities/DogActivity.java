package com.example.sumit.apple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.Dog;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.DogService;
import com.example.sumit.apple.network.OAuthTokenService;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Sumit on 7/26/2016.
 */

public class DogActivity extends AppCompatActivity {

    public static FastItemAdapter fastAdapterDogs;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

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
        fastAdapterDogs.withSelectable(true);

        fastAdapterDogs.withOnClickListener(new FastAdapter.OnClickListener<Dog>() {
            @Override
            public boolean onClick(View v, IAdapter<Dog> adapter, Dog item, int position) {
//                Toast.makeText(v.getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DogActivity.this,DogDetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", item.getItemId());
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

}
