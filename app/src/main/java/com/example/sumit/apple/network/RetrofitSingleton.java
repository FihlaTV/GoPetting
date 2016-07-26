package com.example.sumit.apple.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sumit on 7/26/2016.
 */

    public class RetrofitSingleton {

        public static final String API_BASE_URL = "http://ec2-54-169-7-179.ap-southeast-1.compute.amazonaws.com";
        private static Retrofit restAdapter = null;
        private static OkHttpClient client = new OkHttpClient.Builder().build();

        private RetrofitSingleton() {

        }

        public static Retrofit getInstance() {
            if (restAdapter == null) {
                restAdapter = new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();


            }
            return restAdapter;
        }

    }

