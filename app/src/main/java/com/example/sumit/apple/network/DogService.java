package com.example.sumit.apple.network;

import android.content.Context;
import android.util.Log;

import com.example.sumit.apple.activities.DogActivity;
import com.example.sumit.apple.models.Dog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sumit on 7/26/2016.
 */
public class DogService {

    public static void getData( String token, final Controller.MethodsCallback callback) {
        Controller.GetDogData retrofitGetDogData = RetrofitSingleton.getInstance().create(Controller.GetDogData.class);
        Call<List<Dog>> call = retrofitGetDogData.getDogData("Bearer " + token);
        call.enqueue(new Callback<List<Dog>>() {
            @Override
            public void onResponse(Call<List<Dog>> call, Response<List<Dog>> response) {
                if (response.isSuccessful()) {           //(response.body() != null)
                    callback.success(response.body());
                    callback.responseBody(call);


                } else {
                    Log.d("Error Response", "DogService.getData :Error Response");
                }
            }

            @Override
            public void onFailure(Call<List<Dog>> call, Throwable t) {
                callback.failure(t);
                callback.responseBody(call);
            }
        });

    }

}
