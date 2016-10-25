package com.gopetting.android.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.MenuItem;

import com.gopetting.android.activities.LoginActivity;
import com.gopetting.android.models.User;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Amit on 6/12/2016.
 */
public class SessionManager {

    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_BIRTHDATE = "birthday";

    public static final String KEY_PICTURE = "picture";

    public static final String KEY_GENDER = "gender";

    public static final String KEY_LOCATION ="location";

    public static final String IS_FIRST ="is_First";

    private static final String KEY_USER_ID = "user_id";         //Logic by ssahu
    //public static final String Key_SetLogOutBtn="setLogout";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(JSONObject object){          //Looks session for Facebook : ssahu

        try {
            // Storing login value as TRUE
            editor.putBoolean(IS_LOGIN, true);
            // Storing User info in pref
            editor.putString(KEY_NAME, object.optString("name").toString());
//            editor.putString(KEY_BIRTHDATE, object.optString("birthday").toString());
            editor.putString(KEY_EMAIL, object.optString("email").toString());
//            editor.putString(KEY_LOCATION, object.getJSONObject("location").getString("name").toString());
//            editor.putString(KEY_GENDER, object.optString("gender").toString());
            editor.putString(KEY_PICTURE, object.getJSONObject("picture").getJSONObject("data").optString("url"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        // commit changes
        editor.commit();
    }

    public void createGplusLogin(Person UserInfo, String Email, User user) {
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_NAME, UserInfo.getDisplayName());
//        editor.putString(KEY_BIRTHDATE, UserInfo.getBirthday());
        editor.putString(KEY_EMAIL, Email);
//        editor.putString(KEY_GENDER, UserInfo.getGender()+"");
//        editor.putString(KEY_LOCATION, UserInfo.getPlacesLived().toString());//change it
        editor.putString(KEY_PICTURE, UserInfo.getImage().getUrl());

        // commit changes
        editor.commit();
    }
    public void IsNotFirstOccurenceCommit(boolean val) {
        editor.putBoolean(IS_FIRST, val);
               // commit changes
        editor.commit();
    }
    public boolean IsNotFirstOccurenceCheck() {
        return pref.getBoolean(IS_FIRST, false);
    }

    //Indicator to check if specific functions were called.

    public void checkForLoginTab(MenuItem Item)
    {
        //editor.put(Key_SetLogOutBtn,Item);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user Info
        //where pref.getString(KEY_NAME, null) shows the null value which is default value, gets returned if there is no value.
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
//        user.put(KEY_BIRTHDATE, pref.getString(KEY_BIRTHDATE, null));
        user.put(KEY_PICTURE, pref.getString(KEY_PICTURE, null));
//        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
//        user.put(KEY_LOCATION,pref.getString(KEY_LOCATION,null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
       // Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
       // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
       // _context.startActivity(i);
    }

    public void setLogin(boolean isSet)
    {
        editor.putBoolean(IS_LOGIN,isSet);
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        //AccessToken acesstoken= AccessToken.getCurrentAccessToken();
        return pref.getBoolean(IS_LOGIN, false);
        //return acesstoken !=null;
    }

    public void  setUserId(User user){      //By ssahu

        editor.putString(KEY_USER_ID,user.getUserId());
        editor.commit();
        Log.d("Toast", "Not an error: Toast");
    }

    public String getUserId(){              //By ssahu

        String userId = pref.getString(KEY_USER_ID, null);
        return  userId;

    }
}


