package com.example.sumit.apple.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sumit.apple.R;

import com.example.sumit.apple.network.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Smit on 7/4/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    CircleImageView proPic;
    TextView mName;
    TextView mEmail;
    TextView mAddress;
    TextView mMobile;
   // TextView mLocation;
    Toolbar mToolbar;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        //Setting the Home Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();

        mMobile = (TextView) findViewById(R.id.User_Mobile);
       // mLocation = (TextView) findViewById(R.id.User_Location);
        mAddress = (TextView) findViewById(R.id.User_Address);
        mEmail = (TextView) findViewById(R.id.User_Email);
        proPic = (CircleImageView) findViewById(R.id.user_profile_photo);
        mName = (TextView) findViewById(R.id.User_Name);

                // proPic.setImageResource(user.get(SessionManager.KEY_PICTURE));
        Glide.with(getApplicationContext())
                .load(user.get(SessionManager.KEY_PICTURE))


                        //.override(350, 448)
                        //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(proPic);
        mName.setText(user.get(SessionManager.KEY_NAME));

        mEmail.setText(user.get(SessionManager.KEY_EMAIL));
//        mAddress.setText(user.get(SessionManager.KEY_LOCATION));
       // mLocation.setText("Chennai,India");

       // mBirthday.setText(user.get(SessionManager.KEY_BIRTHDATE));

//        mMobile.setText("8056015741");



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
