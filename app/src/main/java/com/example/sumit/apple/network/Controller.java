package com.example.sumit.apple.network;

import com.example.sumit.apple.models.Dog;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sumit on 7/26/2016.
 */
public class Controller {

    public interface MethodsCallback<T> {
        public void failure(Throwable arg0);

        public void success(T arg0);

        public void responseBody(Call<T> call);         //TODO:Check the usage/significance of this.
    }

    public interface GetDogData {
        @GET("/withoutauth/v1")
        Call<List<Dog>> getDogData();
    }
}
