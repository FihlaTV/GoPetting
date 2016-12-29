package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.StartupItem;
import com.gopetting.android.models.StringItem;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.gopetting.android.utils.Version;

import java.util.ArrayList;
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
    private ArrayList<String> mPromoImages;
    private StartupItem mStartupItem;
    private String mVersion;
    private int mUpdate = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        mPromoImages = new ArrayList<>();   //Initializing


        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this project the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
//        if (getIntent().getExtras() != null) {
//
//            for (String key : getIntent().getExtras().keySet()) {
//                String value = getIntent().getExtras().getString(key);
//
//                if (key.equals("AnotherActivity") && value.equals("True")) {
////                    Intent intent = new Intent(this, DogActivity.class);
////                    intent.putExtra("value", value);
////                    startActivity(intent);
////                    finish();
//                    Toast.makeText(SplashActivity.this, "Inside getIntent", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }

        subscribeToPushService();




        if (ConnectivityReceiver.isConnected()) {

            getServerData();
        }else {
            showSnack();
        }


    }


    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("updates");

//        Toast.makeText(SplashActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

//        Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(SplashActivity.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();
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

        Controller.GetStartupItem retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetStartupItem.class);
        Call<StartupItem> call = retrofitSingleton.getStartupItem("Bearer " + credOAuth.getAccess_token());
        call.enqueue(new Callback<StartupItem>() {
            @Override
            public void onResponse(Call<StartupItem> call, Response<StartupItem> response) {
                if (response.isSuccessful()) {

                    mStartupItem = response.body();

                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        mVersion = packageInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    //Reference: http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java

                    Version a = new Version(mVersion);
                    Version b = new Version(mStartupItem.getVersion());

                    if (a.compareTo(b) == -1){
                        mUpdate = 1;
                    }else {
                        mUpdate = 0;
                    }

                    mPromoImages.add(mStartupItem.getStringItems().get(0).getName());
                    mPromoImages.add(mStartupItem.getStringItems().get(1).getName());
                    mPromoImages.add(mStartupItem.getStringItems().get(2).getName());

                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putStringArrayList("promo_images", mPromoImages);
                    bundle.putInt("update_required",mUpdate);
                    bundle.putString("new_version",mStartupItem.getVersion());

                    intent.putExtras(bundle);
//                    intent.putStringArrayListExtra("promo_images", mPromoImages);

                    startActivity(intent);
                    SplashActivity.this.finish();


                } else {
                    Log.d("Error Response", "SplashActivity.getPromotionalScreens :Error Response");
                }
            }

            @Override
            public void onFailure(Call<StartupItem> call, Throwable t) {
                Log.d("onFailure", "GoPetting server is currently unavailable, please try again after some time");
            }
        });
    }

    private void showSnack() {

        Snackbar.make(findViewById(R.id.rl_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();

    }

}

