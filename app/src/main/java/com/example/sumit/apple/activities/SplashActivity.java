package com.example.sumit.apple.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Credential;
import com.example.sumit.apple.models.StringItem;
import com.example.sumit.apple.network.Controller;
import com.example.sumit.apple.network.OAuthTokenService;
import com.example.sumit.apple.network.RetrofitSingleton;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.support.v7.app.AppCompatActivity;


/**
 * Created by Smit on 6/1/2016.
 */
public class SplashActivity extends Activity {

    String now_playing, earned;
    private Credential mCredential;
    private List<StringItem> mPromotionalScreens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_activity);


        getServerData();
//
//        try {
//            Thread.currentThread();
//            Thread.sleep(3000);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }


		/*
         * Showing splashscreen while making network calls to download necessary
		 * data before launching the app Will use AsyncTask to make http call
		 */
//        new PrefetchData().execute();


    }

    private void getServerData() {

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        mCredential = oAuthTokenService.getAccessTokenWithID("default");

        if(mCredential == null || mCredential.getAccess_token()==null || oAuthTokenService.isExpired("default"))
        {
            oAuthTokenService.authenticateUsingOAuth( new Controller.MethodsCallback<Credential>()
            {
                @Override public void failure(Throwable throwable)
                {
                    Toast.makeText(SplashActivity.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        getPromotionalGalleryData();

                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            getPromotionalGalleryData();

        }
    }


    private void getPromotionalGalleryData(){

//Temp Arrangement: Added these below 2 extra lines to handle Token storage issue during first start
        final OAuthTokenService oAuth = OAuthTokenService.getInstance(this);

        Credential credOAuth = oAuth.getAccessTokenWithID("default");

        Controller.GetPromotionalScreens retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetPromotionalScreens.class);
        Call<List<StringItem>> call = retrofitSingleton.getPromotionalScreens("Bearer " + credOAuth.getAccess_token());
        call.enqueue(new Callback<List<StringItem>>() {
            @Override
            public void onResponse(Call<List<StringItem>> call, Response<List<StringItem>> response) {
                if (response.isSuccessful()) {

                    mPromotionalScreens =response.body();

                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("promo_image1", mPromotionalScreens.get(0).getName());
                    bundle.putString("promo_image2", mPromotionalScreens.get(1).getName());
                    bundle.putString("promo_image3", mPromotionalScreens.get(2).getName());
                    intent.putExtras(bundle);

//                    mProgressBar.setVisibility(View.GONE);

                    startActivity(intent);
                    SplashActivity.this.finish();


                } else {
                    Log.d("Error Response", "SplashActivity.getPromotionalScreens :Error Response");
                }
            }

            @Override
            public void onFailure(Call<List<StringItem>> call, Throwable t) {
                Log.d("onFailure", "SplashActivity.getPromotionalScreens :Error Response");
            }
        });
    }
    

    /*
     * Async Task to make http call
     */
//    private class PrefetchData extends AsyncTask<Void, Void, Void> {
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // before making http calls
//            Log.e("JSON", "Pre execute");
//
//
//            try {
//                Thread.currentThread();
//                Thread.sleep(1000);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//
//
//                /*Thread timerThread = new Thread(){
//                    public void run(){
//                        try{
//                            sleep(3000);
//                        }catch(InterruptedException e){
//                            e.printStackTrace();
//                        }finally{
//                            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//                };
//                timerThread.start();
//            }*/
//        }
//
//
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//
//            /*RetrofitServiceGenerator.RetrofitService retrofitService = RetrofitServiceGenerator.createService(RetrofitServiceGenerator.RetrofitService.class);
//            Call<List<RetrofitServiceGenerator.Dog>> call = retrofitService.getJsonData();
//
//            call.enqueue(new Callback<List<RetrofitServiceGenerator.Dog>>()
//
//            {
//                @Override
//                public void onResponse(Call<List<RetrofitServiceGenerator.Dog>> call, Response<List<RetrofitServiceGenerator.Dog>> response) {
//                    if (response.isSuccessful()) {
////                    int statusCode = response.code();
//                        dogs = response.body();
//
//                        //fastAdapterDogs.add(dogs);
//                        int statusCode = response.code();
//
//
//                    } else {
//                        // error response, no access to resource?
//                        Log.d("Error Response", "Error Response");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<RetrofitServiceGenerator.Dog>> call, Throwable t) {
//                    // something went completely south (like no internet connection)
//                    Log.d("Error", t.getMessage());
//                }
//            });*/
//
////            Log.e("Response: ", "> " + dogs);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            // After completing http call
//            // will close this activity and lauch main activity
//            // Intent i = new Intent(SplashActivty.this, LoginActivity.class);
//            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // i.putStringArrayListExtra("now_playing", (ArrayList<String>)dogs);
//            // i.putExtra("earned", (ArrayList<RetrofitServiceGenerator.Dog>)dogs);
////            startActivity(new Intent(SplashActivity.this, MainActivity.class));//immy
//
//
//            //context.startActivity(new Intent(context, ResultsQueryActivity.class));
//            // EventBus.getDefault().post(new MoveToFragmentEvent(new AboutUsFragment()));
//            // close this activity
//            finish();
//        }
//
//    }

}

