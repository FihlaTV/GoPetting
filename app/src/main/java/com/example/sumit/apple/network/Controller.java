package com.example.sumit.apple.network;

import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.Dog;
import com.example.sumit.apple.models.DogDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Sumit on 7/26/2016.
 */
public class Controller {

    public interface MethodsCallback<T> {
        public void failure(Throwable arg0);

        public void success(T arg0);

        public void responseBody(Call<T> call);         //TODO:Check the usage/significance of this.
    }

    public interface GetAccessToken {
        @POST("/oauth/access_token")
        @FormUrlEncoded
//        @Headers({"content-type: application/x-www-form-urlencoded"})
//        @Headers({"Accept: application/json"})
        Call<Credential> getAccessToken(@Field("grant_type") String grant_type,
                                        @Field("client_id") String client_id,
                                        @Field("client_secret") String client_secret);
    }

    public interface GetDogData {
        @GET("/api/v1")
        Call<List<Dog>> getDogData(@Header("Authorization") String authorization);
    }

    public interface GetDogDetails {
        @GET("/api/v1/dog_details/{item_id}")
        Call<DogDetails> getDogDetailsData(@Header("Authorization") String authorization,
                                          @Path("item_id") int item_id);
    }


}
