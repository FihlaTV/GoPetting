package com.gopetting.android.network;

import com.gopetting.android.models.Address;
import com.gopetting.android.models.AddressList;
import com.gopetting.android.models.Appointment;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.CartScreen;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.DateTimeSlot;
import com.gopetting.android.models.Dateslot;
import com.gopetting.android.models.DeliveryDetails;
import com.gopetting.android.models.Dog;
import com.gopetting.android.models.DogDetails;
import com.gopetting.android.models.FilterSubCategory;
import com.gopetting.android.models.OrderSummary;
import com.gopetting.android.models.Promo;
import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.Status;
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


    //Get cart items version v2
    public interface GetCartItemsV2 {
        @GET("/api/v1/service/cart/screen")
        Call<Cart> getCartItemsV2(@Header("Authorization") String authorization,
                                  @Query("user_id") String userId);
    }

    public interface GetCartStatus {
        @GET("/api/v1/service/cart/u")
        Call<Status> getCartStatus(@Header("Authorization") String authorization,
                                           @Query("user_id") String userId,
                                           @Query("service_package_id_1") int servicePackageId1,
                                           @Query("service_package_id_2") int servicePackageId2,
                                           @Query("service_package_id_3") int servicePackageId3,
                                           @Query("service_package_id_4") int servicePackageId4,
                                           @Query("service_package_id_5") int servicePackageId5,
                                           @Query("server_request_id") int requestId);
    }


    //Get cart screen items
    public interface GetCartScreenItems {
        @GET("/api/v1/service/cart/screen")
        Call<CartScreen> getCartScreenItems(@Header("Authorization") String authorization,
                                            @Query("user_id") String userId);
    }



    public interface GetAppointmentDetails {

        @GET("/api/v1/appointment/address_date_time")
        Call<Appointment> getAppointmentDetails(@Header("Authorization") String authorization,
                                                @Query("user_id") String userId);

    }


    public interface GetAddressFirstStatus {
        @GET("/api/v1/appointment/address/u1")
        Call<Status> getAddressFirstStatus(@Header("Authorization") String authorization,
                                   @Query("user_id") String userId,
                                   @Query("full_name") String  fullName,
                                   @Query("address") String address,
                                   @Query("area") String area,
                                   @Query("landmark") String landmark,
                                   @Query("city") String city,
                                   @Query("state") String state,
                                   @Query("pincode") int pincode,
                                   @Query("phone") String phone);
    }


    public interface GetDateTimeItems {

        @GET("/api/v1/appointment/date_time")
        Call<DateTimeSlot> getDateTimeData(@Header("Authorization") String authorization,
                                           @Query("user_id") String userId,
                                           @Query("pincode") String pincode);

    }


    public interface GetAddressListItems {

        @GET("/api/v1/appointment/address_list")
        Call<AddressList> getAddressListItems(@Header("Authorization") String authorization,
                                          @Query("user_id") String userId);

    }



    public interface GetAddressSecondStatus {
        @GET("/api/v1/appointment/address/u2")
        Call<Status> getAddressSecondStatus(@Header("Authorization") String authorization,
                                           @Query("user_id") String userId,
                                           @Query("address_id") int  addressId,
                                           @Query("address_id_list") String  addressIdList,
                                           @Query("indicator") int  indicator); //2 for AddressId List; indicator=3 for AddressId ; Now both options removed
    }



    public interface GetOrderSummary {
        @GET("/api/v1/standard/service/summary")
        Call<OrderSummary> getOrderSummary(@Header("Authorization") String authorization);
    }


    public interface GetPromoStatus {
        @GET("/api/v1/standard/service/promo")
        Call<Promo> getPromoStatus(@Header("Authorization") String authorization,
                                   @Query("promo_code") String promoCode,
                                   @Query("service_package_id_1") int servicePackageId1,
                                   @Query("service_package_id_2") int servicePackageId2,
                                   @Query("service_package_id_3") int servicePackageId3,
                                   @Query("service_package_id_4") int servicePackageId4,
                                   @Query("service_package_id_5") int servicePackageId5);

    }



}
