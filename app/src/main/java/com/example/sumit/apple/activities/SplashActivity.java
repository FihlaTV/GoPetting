package com.example.sumit.apple.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.sumit.apple.R;
import com.example.sumit.apple.models.Dog;


import java.util.List;

//import android.support.v7.app.AppCompatActivity;


/**
 * Created by Smit on 6/1/2016.
 */
public class SplashActivity extends Activity {

    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

		/*
         * Showing splashscreen while making network calls to download necessary
		 * data before launching the app Will use AsyncTask to make http call
		 */
        new PrefetchData().execute();

    }

    /*
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {
        List<Dog> dogs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
            Log.e("JSON", "Pre execute");


            try {
                Thread.currentThread();
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }


                /*Thread timerThread = new Thread(){
                    public void run(){
                        try{
                            sleep(3000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                };
                timerThread.start();
            }*/
        }



        @Override
        protected Void doInBackground(Void... arg0) {

            /*RetrofitServiceGenerator.RetrofitService retrofitService = RetrofitServiceGenerator.createService(RetrofitServiceGenerator.RetrofitService.class);
            Call<List<RetrofitServiceGenerator.Dog>> call = retrofitService.getJsonData();

            call.enqueue(new Callback<List<RetrofitServiceGenerator.Dog>>()

            {
                @Override
                public void onResponse(Call<List<RetrofitServiceGenerator.Dog>> call, Response<List<RetrofitServiceGenerator.Dog>> response) {
                    if (response.isSuccessful()) {
//                    int statusCode = response.code();
                        dogs = response.body();

                        //fastAdapterDogs.add(dogs);
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
            });*/

            Log.e("Response: ", "> " + dogs);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            // Intent i = new Intent(SplashActivty.this, LoginActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // i.putStringArrayListExtra("now_playing", (ArrayList<String>)dogs);
            // i.putExtra("earned", (ArrayList<RetrofitServiceGenerator.Dog>)dogs);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));//immy


            //context.startActivity(new Intent(context, ResultsQueryActivity.class));
            // EventBus.getDefault().post(new MoveToFragmentEvent(new AboutUsFragment()));
            // close this activity
            finish();
        }

    }

}

