package com.gopetting.android.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sumit on 7/26/2016.
 */

    public class RetrofitSingletonBackup {

        public static final String API_BASE_URL = "https://s3-ap-southeast-1.amazonaws.com/";
        private static Retrofit restAdapter = null;
        private static OkHttpClient client = new OkHttpClient.Builder().build();

        private RetrofitSingletonBackup() {

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

