package com.gopetting.android.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gopetting.android.models.Credential;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sumit on 7/26/2016.
 */
public class OAuthTokenService {

    //TODO: Remove unwanted fields,methods etc.

    Context context;
    private SharedPreferences sharedpreferences;
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private final String TOKEN_TYPE = "TOKEN_TYPE";
    private final String EXPIRATION_DATE = "EXPIRATION_DATE";
    private final String OAUTH_PREFERENCE = "OAUTH_PREFERENCE";

    public SharedPreferences.Editor editor;


    private static final String GRANT_TYPE = "client_credentials";
    private static final String CLIENT_ID = "f3d259ddd3ed8ff3843839b";
    private static final String SECRET = "4c7f6f8fa93d59c45502c0ae8c4a95b";
    private static OAuthTokenService oAuthTokenService = null;

    public OAuthTokenService(Context context) {
        this.context = context;
    }

    public static OAuthTokenService getInstance(Context context){
            if (oAuthTokenService == null) {
                oAuthTokenService = new OAuthTokenService(context);
            }
        return oAuthTokenService;
    }

    public void authenticateUsingOAuth(final Controller.MethodsCallback callback) {
        Controller.GetAccessToken retrofitGetAccessToken = RetrofitSingleton.getInstance().create(Controller.GetAccessToken.class);
        Call<Credential> call = retrofitGetAccessToken.getAccessToken(GRANT_TYPE,CLIENT_ID,SECRET);
        call.enqueue(new Callback<Credential>() {
            @Override
            public void onResponse(Call<Credential> call, Response<Credential> response) {
                if (response.isSuccessful()) {           //(response.body() != null)
                    callback.success(response.body());
                    callback.responseBody(call);


                } else {
                    Log.d("Error Response", "OAuthTokenService.authenticateUsingOAuth : Error Response");
                }
            }

            @Override
            public void onFailure(Call<Credential> call, Throwable t) {
                callback.failure(t);
                callback.responseBody(call);
            }
        });



    }

    public Credential getAccessTokenWithID(String id) {
        if (id.equals("default") || id == null) {
            sharedpreferences = context.getSharedPreferences(OAUTH_PREFERENCE, Context.MODE_PRIVATE);
            Credential credential = new Credential();
            credential.setAccess_token(sharedpreferences.getString(ACCESS_TOKEN, null));
            credential.setExpires_in(sharedpreferences.getLong(EXPIRATION_DATE, -1));
            credential.setToken_type(sharedpreferences.getString(TOKEN_TYPE, null));

            return credential;
        } else {
            sharedpreferences = context.getSharedPreferences(id, Context.MODE_PRIVATE);
            Credential credential = new Credential();
            credential.setAccess_token(sharedpreferences.getString(ACCESS_TOKEN, null));
            credential.setExpires_in(sharedpreferences.getLong(EXPIRATION_DATE, -1));
            credential.setToken_type(sharedpreferences.getString(TOKEN_TYPE, null));

            return credential;
        }

    }

    public void saveTokenWithID(Credential credential, String id) {
        if (id.equals("default") || id == null) {
            sharedpreferences = context.getSharedPreferences(OAUTH_PREFERENCE, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putString(ACCESS_TOKEN, credential.getAccess_token());
            editor.putString(TOKEN_TYPE, credential.getToken_type());
            editor.commit();
            saveExpiryDate(credential.getExpires_in(), "default");
        } else {
            sharedpreferences = context.getSharedPreferences(id, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putString(ACCESS_TOKEN, credential.getAccess_token());
            editor.putString(TOKEN_TYPE, credential.getToken_type());
            editor.commit();
            saveExpiryDate(credential.getExpires_in(), id);
        }


    }

    public void saveExpiryDate(long expire, String id) {
        Calendar c = Calendar.getInstance();
        Date currentLocalTime = c.getTime();
        //Log.d("currentDate", currentLocalTime.getTime() + "");
        long newDate = currentLocalTime.getTime() + (expire * 1000);
        Date expireDate = new Date(newDate);
        //Log.d("expireDate", expireDate + "");
        if (id.equals("default")) {
            sharedpreferences = context.getSharedPreferences(OAUTH_PREFERENCE, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putLong(EXPIRATION_DATE, expireDate.getTime());//plus current time
            editor.commit();
        } else {
            sharedpreferences = context.getSharedPreferences(id, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putLong(EXPIRATION_DATE, expireDate.getTime());//plus current time
            editor.commit();
        }

    }

    public boolean isExpired(String id) {
        if (id.equals("default")) {
            sharedpreferences = context.getSharedPreferences(OAUTH_PREFERENCE, Context.MODE_PRIVATE);
        } else {
            sharedpreferences = context.getSharedPreferences(id, Context.MODE_PRIVATE);
        }
        //expiry time
        long expiredate = sharedpreferences.getLong(EXPIRATION_DATE, -1);
        Date expireDate = new Date(expiredate);
        //current time
        Calendar c = Calendar.getInstance();
        Date currentLocalTime = c.getTime();
        //return true if current time greater than expiry date
        return currentLocalTime.after(expireDate);
    }

    public void deleteTokenWithId(String id) {
        if (id.equals("default")) {
            sharedpreferences = context.getSharedPreferences(OAUTH_PREFERENCE, 0);
            sharedpreferences.edit().remove(OAUTH_PREFERENCE).commit();
            String a = "sum";
        } else {
            sharedpreferences = context.getSharedPreferences(id, 0);
            sharedpreferences.edit().remove(id).commit();
        }
    }

    public void deleteAllToken() {
        editor.clear().commit();
    }


}
