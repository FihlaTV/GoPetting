package com.example.sumit.apple.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sumit.apple.R;
import com.example.sumit.apple.bus.MoveToFragmentEvent;
import com.example.sumit.apple.bus.UpdateActionBarTitleEvent;
import com.example.sumit.apple.fragments.AboutUsFragment;
import com.example.sumit.apple.fragments.ContactUsFragment;
import com.example.sumit.apple.fragments.HomeFragment;
import com.example.sumit.apple.fragments.LocationFragment;
import com.example.sumit.apple.fragments.OrdersFragment;
import com.example.sumit.apple.fragments.TermsConditionsFragment;
import com.example.sumit.apple.models.CircleImageView;
import com.example.sumit.apple.network.SessionManager;
import com.example.sumit.apple.utils.Constants;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;

import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                ResultCallback<People.LoadPeopleResult> {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragmentManager;

//------------------------------LoginActivity - Start-----------------------------//

    private boolean isLog = true;
    private SessionManager session;
    public JSONObject json_object;
    private static final String TAG = "MyActivity";
    private RelativeLayout Relativelayout;
    // public Menu menu;
    CircleImageView proPic;
    TextView ProfText;
    ProgressDialog progress_dialog;

    GoogleApiClient mGoogleApiClient;

    boolean mSignInClicked;
    ProgressDialog ringProgressDialog;


    private static final int RC_SIGN_IN = 0;
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;




// ------------------------------LoginActivity - End-----------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//------------------------------LoginActivity -Start-----------------------------//

        //Get User Profile Info
        session = new SessionManager(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");


       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        mGoogleApiClient.connect();



// ------------------------------LoginActivity -End-----------------------------//


        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Find our drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);

// ------------------------------LoginActivity - Start-----------------------------//

        mNavDrawer.post(new Runnable() {
            @Override
            public void run() {
                Resources resources = getResources();
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 265, resources.getDisplayMetrics());
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mNavDrawer.getLayoutParams();
                params.width = (int) (width);
                mNavDrawer.setLayoutParams(params);
            }
        });

        //mNavDrawer.getMenu().findItem(R.id.Login).setTitle("Test");
        //Adding Navigation Header to NavigationView
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_header, null, false);

        // TODO : This Logic has to be Removed . Already Implemented.

        TextView Login_SignUp = (TextView) view.findViewById(R.id.profile_name);
        Login_SignUp.setOnClickListener(this);

        Relativelayout = (RelativeLayout) view.findViewById(R.id.navLayout);
        RelativeLayout Rlout = (RelativeLayout) view.findViewById(R.id.navLayout);

        Relativelayout.setOnClickListener(this);


        proPic = (CircleImageView) view.findViewById(R.id.profile_pic);
        ProfText = (TextView) view.findViewById(R.id.profile_name);

        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();

            Glide.with(getApplicationContext())
                    .load(user.get(SessionManager.KEY_PICTURE))
                    //.placeholder(R.mipmap.default_photo)
                    //.override(350, 448)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(proPic);
            ProfText.setText(user.get(SessionManager.KEY_NAME));

        } else {
            proPic.setImageResource(R.drawable.default_profile_pic);
            ProfText.setText("Login/SignUp");

        }


        mNavDrawer.addHeaderView(view);

// ------------------------------LoginActivity - End-----------------------------//

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flContent, HomeFragment.newInstance()) //TODO: Update this
                    .commit();
        }


        // Setup drawer view ; Set Navigation Item Selection Listener
        mNavDrawer.setNavigationItemSelectedListener(new NavigationItemSelectedListener());

//TODO: main logic is till here! Delete once done

        //Setting listener for Drawer open/close events

        final CharSequence mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                 R.string.drawer_open, R.string.drawer_close) {

//           Called when a drawer has settled in a completely closed state.

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                EventBus.getDefault().post(new UpdateActionBarTitleEvent(mTitle));
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

//             Called when a drawer has settled in a completely open state.

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                EventBus.getDefault().post(new UpdateActionBarTitleEvent(mDrawerTitle));
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }



    private class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            selectDrawerItem(menuItem);
            return true;
        }

    }

    public void selectDrawerItem(MenuItem menuItem) {

        //TODO: Update all fragments to extend from Basefragment: Done
        //TODO: Change cases like Homefragment - HomeFragment.newInstance()
        switch(menuItem.getItemId()) {
            case R.id.home:
                EventBus.getDefault().post(new MoveToFragmentEvent(HomeFragment.newInstance()));
                break;
            case R.id.location:
                EventBus.getDefault().post(new MoveToFragmentEvent(new LocationFragment()));
                break;
            case R.id.orders:
                EventBus.getDefault().post(new MoveToFragmentEvent(new OrdersFragment()));
                break;
            case R.id.ContactUs:
                EventBus.getDefault().post(new MoveToFragmentEvent(new ContactUsFragment()));
                break;
            case R.id.TC:
                EventBus.getDefault().post(new MoveToFragmentEvent(new TermsConditionsFragment()));
                break;
            case R.id.AboutUs:
                EventBus.getDefault().post(new MoveToFragmentEvent(new AboutUsFragment()));
                break;
            case R.id.LogBtn:

                if (allAccountLogOut()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            default:
                EventBus.getDefault().post(new MoveToFragmentEvent(new HomeFragment()));
        }
/*        try {
            fragment = (Fragment) FragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        mFragmentManager = getSupportFragmentManager();

//        Log.e("Count in Fragment", "" + mFragmentManager.getBackStackEntryCount());
        mFragmentManager.beginTransaction().replace(R.id.flContent,fragment).addToBackStack(null).commit();*/
//
//        if (mFragmentManager.getBackStackEntryCount()>0){
//            mFragmentManager.popBackStack();
//        }
//        else {
//            mFragmentManager.beginTransaction().replace(R.id.flContent,fragment).addToBackStack(null).commit();
//
//        }


        // Highlight the selected item, update the toolbar title, and close the drawer
        menuItem.setChecked(true);
//        setTitle(menuItem.getTitle());
//        Toast.makeText(this, menuItem.getItemId(), Toast.LENGTH_SHORT).show();  //temp toast
        mDrawerLayout.closeDrawer(mNavDrawer);


    }

    public void onEvent(MoveToFragmentEvent e) {
        if (e.getFragment() instanceof HomeFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        } else if (e.getFragment() instanceof LocationFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        } else if (e.getFragment() instanceof OrdersFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof ContactUsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof TermsConditionsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof AboutUsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        MenuItem LogText = mNavDrawer.getMenu().findItem(R.id.LogBtn);
        if (session.isLoggedIn()) {
            mNavDrawer = (NavigationView) findViewById(R.id.nvView);
            LogText.setTitle("Logout");
        } else {
            isLog = true;
            proPic.setImageResource(R.drawable.default_profile_pic);
            ProfText.setText("Login/SignUp");
            LogText.setTitle("LogIn");

        }


        return true;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //TODO : Add/Handle Action bar options item
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed(){
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

/*
    public static class PlaceholderFragment extends BaseFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			*//* Update fragment's title.*//*
            EventBus.getDefault().post(new UpdateActionBarTitleEvent(getActivity().getTitle()));
            View rootView = inflater.inflate(R.layout.fragment_home, container, false); //TODO: Change/Rename fragment_home layout name to something meaningful like fragment_placeholder
            TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
            text.setText(R.string.home_text);

*//*
			*//*
*//* Move to fragment that can demonstrate sticky-event. *//**//*

            rootView.findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new MoveToFragmentEvent(new SecondFragment()));
                }
            });
			*//*
*//* Move to fragment that can not accept sticky-event. *//**//*

            rootView.findViewById(R.id.btn_no_sticky).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new MoveToFragmentEvent(new NoStickyFragment()));
                }
            });
*//*

            return rootView;
        }
    }*/

    public void onEvent(UpdateActionBarTitleEvent e) {
        getSupportActionBar().setTitle(e.getTitle());

    }



    @Override
    public void onStart() {
        if (session.isLoggedIn()) {
            mGoogleApiClient.connect();
        }
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }


    public boolean allAccountLogOut() {
        if (session.isLoggedIn()) {

            if (mGoogleApiClient.isConnected()) {

                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect();

                    }

                });
                mGoogleApiClient.connect();
                session.setLogin(false);
                isLog = false;
            }

            if (isLog != false) {
                LoginManager.getInstance().logOut();
                session.setLogin(false);
                isLog = false;
            }
            session.logoutUser();


        }


        return isLog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navLayout:
                // Toast.makeText(this, "Login Clicked", Toast.LENGTH_LONG).show();
                if (session.isLoggedIn()) {
                    Toast.makeText(this, "Profile page is under developement", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                    break;
                }
                else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(Constants.TAG_LOGIN, "onConnectionFailed:" + connectionResult);
        //ringProgressDialog.dismiss();

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(Constants.TAG_LOGIN, "Could not resolve ConnectionResult.", e);
                    Toast.makeText(MainActivity.this, "Could not resolve ConnectionResult", Toast.LENGTH_LONG).show();
                    mIsResolving = false;
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(MainActivity.this, "Error on Login, check your google + login method", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show the signed-out UI
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mShouldResolve = false;

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(
                this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.Login).setTitle("Logout");
        return super.onPrepareOptionsMenu(menu);
    }
}

