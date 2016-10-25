package com.gopetting.android.network;

import com.gopetting.android.models.Cart;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.DeliveryDetails;
import com.gopetting.android.models.Dog;
import com.gopetting.android.models.DogDetails;
import com.gopetting.android.models.FilterSubCategory;
import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.ServiceSubCategory;
import com.gopetting.android.models.StringItem;
import com.gopetting.android.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    public interface CheckDeliveryDetails {
        @GET("/api/v1/delivery_details/{item_id}/{pincode}")
        Call<DeliveryDetails> checkDeliveryDetails(@Header("Authorization") String authorization,
                                                   @Path("item_id") int item_id,
                                                   @Path("pincode") int pincode);
    }

    public interface GetBreedNames {
        @GET("/api/v1/dog_details/breed/{product_category_id}")
        Call<List<FilterSubCategory>> getBreedNames(@Header("Authorization") String authorization,
                                                    @Path("product_category_id") int product_category_id);
    }

//   /api/v1/user/get_user_id?id=<id>&indicator=<indicator>&email=<email>
    public interface GetUserId {
        @GET("/api/v1/user/get_user_id")
        Call<User> getUserId(@Header("Authorization") String authorization,
                           @Query("id") String id,
                           @Query("indicator") int indicator,   //indicator=1 for google_id and 2 for facebook_id
                           @Query("email") String email,
                           @Query("first_name") String first_name,
                           @Query("last_name") String last_name);
    }

    public interface GetPromotionalScreens {
        @GET("/api/v1/screens/promotional")
        Call<List<StringItem>> getPromotionalScreens(@Header("Authorization") String authorization);
    }

//    Temp interface for Backup Server DNS
    public interface GetAddress {
        @GET("/samplebucket-6-5-2016/address.json")
        Call<List<StringItem>> getAddress();
    }

//Get Service SubCategory and Service Packages
//   /api/v1/service/service_category?service_category_id=<id>
    public interface GetServiceCategory {
    @GET("/api/v1/service/service_category")
    Call<ServiceCategory> getServiceCategory(@Header("Authorization") String authorization,
                                             @Query("service_category_id") int serviceCategoryId);
    }

    //Get cart items
    public interface GetCartItems {
        @GET("/api/v1/service/cart")
        Call<Cart> getCartItems(@Header("Authorization") String authorization,
                                @Query("user_id") String userId);
    }

}
