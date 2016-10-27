package com.gopetting.android.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.gopetting.android.R;
import com.gopetting.android.bus.UpdateActionBarTitleEvent;
import com.gopetting.android.fragments.GalleryFragment;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.ProductCategory;
import com.gopetting.android.models.ProductCategoryData;
import com.gopetting.android.models.StringItem;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult> {

    private static final int NUM_PAGES = 3; //Promotional Screens

    private static final String PET_SALON = "pet_salon";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;

//------------------------------LoginActivity - Start-----------------------------//

    private boolean isLog = true;
    private SessionManager session;
    public JSONObject json_object;
    private static final String TAG = "MyActivity";
    private LinearLayout mLinearLayout;
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
    private ViewPager mGalleryViewPager;
    private CircleIndicator mCircleIndicator;


    private GalleryPagerAdapter mGalleryPagerAdapter;
    private RecyclerView mRecyclerViewCategory;
    private FastItemAdapter fastAdapterProductCategory;
    private LinearLayoutManager mLayoutManagerCategory;
    private Credential mCredential;
    private List<StringItem> mPromotionalScreens;
    private ProgressBar mProgressBar;
    private int mCurrentPage;
    private ArrayList<String> mPromoImages;


// ------------------------------LoginActivity - End-----------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPromoImages = new ArrayList<>();

        //Get Promotional Images
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

//      If MainActivity is returning/starting from activity other than splashactivity; Since Images are coming from splashactivity
        if(bundle != null) {
            mPromoImages = bundle.getStringArrayList("promo_images");
        }

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

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addApi(AppIndex.API).build();
        mGoogleApiClient.connect();


// ------------------------------LoginActivity -End-----------------------------//


        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_headerbar);
        setSupportActionBar(mToolbar);

        // Find our drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);

        // Initializing these views so that it could be setup later
        mGalleryViewPager = (ViewPager) findViewById(R.id.view_pager_promotional);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        mRecyclerViewCategory = (RecyclerView) findViewById(R.id.recycler_category);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_regular);

        setupHomeScreen();


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
        //Adding Navigation HeaderItem to NavigationView
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_header, null, false);

        // TODO : This Logic has to be Removed . Already Implemented.

        TextView Login_SignUp = (TextView) view.findViewById(R.id.profile_name);
        Login_SignUp.setOnClickListener(this);

//        linearLayout = (RelativeLayout) view.findViewById(R.id.navLayout);
//        RelativeLayout Rlout = (RelativeLayout) view.findViewById(R.id.navLayout);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.navLayout);
        mLinearLayout.setOnClickListener(this);


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
            proPic.setImageResource(R.drawable.ic_profile);
            ProfText.setText("Login/SignUp");

        }


        mNavDrawer.addHeaderView(view);

// ------------------------------LoginActivity - End-----------------------------//

        /*      --Commenting as fragment containers are removed from activity_main layout
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flContent, HomeFragment.newInstance()) //TODO: Update this
                    .commit();
        }
*/

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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //add the values which need to be saved from the adapter to the bundel
//        outState = fastAdapterProductCategory.saveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }

    private void setupHomeScreen() {

        //Setup Promotional Screen
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), mPromoImages);
        mGalleryViewPager.setAdapter(mGalleryPagerAdapter);

        //Automatic Viewpager Slide Setup
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {

                if (mCurrentPage == NUM_PAGES) {
                    mCurrentPage = 0;
                }
                mGalleryViewPager.setCurrentItem(mCurrentPage++, true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 2500);      //Delay, Period


        //Setup Product Category
        mCircleIndicator.setViewPager(mGalleryViewPager);

        //Product Category RecyclerView adapter setup
        fastAdapterProductCategory = new FastItemAdapter();
        fastAdapterProductCategory.withSelectable(true);
        fastAdapterProductCategory.add(ProductCategoryData.getItems());

        fastAdapterProductCategory.withOnClickListener(new FastAdapter.OnClickListener<ProductCategory>() {
            @Override
            public boolean onClick(View v, IAdapter<ProductCategory> adapter, ProductCategory item, int position) {

                switch (position) {
                    case 0:


                        Intent intent = new Intent(MainActivity.this,ServiceActivity.class);
                        Bundle b = new Bundle();
                        b.putString("service_category",PET_SALON);
                        intent.putExtras(b);
                        startActivity(intent);
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:
                        Log.i(TAG, "onClick: Out of bound of adapter index ");
                }

                return  false;
            }
        });


        mRecyclerViewCategory.setAdapter(fastAdapterProductCategory);

        //Product Category RecyclerView LayoutManager setup
        mLayoutManagerCategory = new LinearLayoutManager(this);
        mLayoutManagerCategory.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewCategory.setLayoutManager(mLayoutManagerCategory);

//        Set Default Item Animator
        mRecyclerViewCategory.setItemAnimator(new DefaultItemAnimator());


//        fastAdapterProductCategory.withSavedInstanceState(savedInstanceState);
    }

    public static class GalleryPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        private static List<String> mPromoImages;

        public GalleryPagerAdapter(FragmentManager fragmentManager, List<String> promoImages) {
            super(fragmentManager);
            mPromoImages = promoImages;

        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return GalleryFragment.newInstance(mPromoImages.get(0));
                case 1:
                      return GalleryFragment.newInstance(mPromoImages.get(1));
                case 2:
                    return GalleryFragment.newInstance(mPromoImages.get(2));
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

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
        switch (menuItem.getItemId()) {
//            case R.id.home:
//                EventBus.getDefault().post(new MoveToFragmentEvent(HomeFragment.newInstance()));
//                break;
//            case R.id.location:
//                EventBus.getDefault().post(new MoveToFragmentEvent(new LocationFragment()));
//                break;
//            case R.id.orders:
//                EventBus.getDefault().post(new MoveToFragmentEvent(new OrdersFragment()));
//                break;
            case R.id.share_app:
                shareApp();
                break;

            case R.id.contact_us:
                composeEmail();
                break;
//            case R.id.terms_conditions:
//                EventBus.getDefault().post(new MoveToFragmentEvent(new TermsConditionsFragment()));
//                break;
//            case R.id.about_us:
//                EventBus.getDefault().post(new MoveToFragmentEvent(new AboutUsFragment()));
//                break;
            case R.id.LogBtn:
                setLoginLogout();
                break;
            default:
//                Toast.makeText(this, menuItem.getItemId(), Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        menuItem.setChecked(false); //TODO: This is not working; Need to fix this.
//        setTitle(menuItem.getTitle());
//        Toast.makeText(this, menuItem.getItemId(), Toast.LENGTH_SHORT).show();  //temp toast
        mDrawerLayout.closeDrawer(mNavDrawer);


    }

    private void shareApp() {

        Log.i("Share app", "");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"GoPetting");
        intent.putExtra(Intent.EXTRA_TEXT,"Hey,\nCheck out this new app called GoPetting! It's a 1 stop for all your Pet care need. Try their Android app from https://goo.gl/1cs4ov");

        try {
            startActivity(Intent.createChooser(intent,"Share GoPetting Via"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no sharing client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setLoginLogout() {
        if (!session.isLoggedIn()){

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("promo_images", mPromoImages);
            intent.putExtras(bundle);
            startActivity(intent);

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.dialog_logout_question)
                    .setTitle(R.string.dialog_title_logout)
                    .setPositiveButton(R.string.dialog_button_logout, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            allAccountLogOut(); //Logout all accounts
                        }
                    })
                    .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void composeEmail() {

            Log.i("Send email", "");
            String[] TO = {"gopettingtech@gmail.com"};
//            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);  //for sending mail through default client like GMAIL.

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//            emailIntent.putExtra(Intent.EXTRA_CC, CC);

            try {
                startActivity(Intent.createChooser(emailIntent,"Send mail..."));
                Log.i("Finished sending email", "");
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
    }

//    private void getBackupServerId() {
//
//
//        Controller.GetAddress retrofitSingletonBackup = RetrofitSingletonBackup.getInstance().create(Controller.GetAddress.class);
//        Call<List<StringItem>> call = retrofitSingletonBackup.getAddress();
//        call.enqueue(new Callback<List<StringItem>>() {
//            @Override
//            public void onResponse(Call<List<StringItem>> call, Response<List<StringItem>> response) {
//                if (response.isSuccessful()) {
//
//                    List<StringItem> address = response.body();
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setMessage(address.get(0).getName())
//                            .setTitle(R.string.dialog_title_service_location);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//
//                } else {
//                    Log.d("Error Response", "Error Response");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<StringItem>> call, Throwable t) {
//                Log.d("onFailure", "Failure");
//            }
//        });
//
//    }

//    public void onEvent(MoveToFragmentEvent e) {
//        if (e.getFragment() instanceof HomeFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        } else if (e.getFragment() instanceof LocationFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        } else if (e.getFragment() instanceof OrdersFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        }  else if (e.getFragment() instanceof ContactUsFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        }  else if (e.getFragment() instanceof TermsConditionsFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        }  else if (e.getFragment() instanceof AboutUsFragment) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();
//
//        }
//
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        MenuItem LogText = mNavDrawer.getMenu().findItem(R.id.LogBtn);
        if (session.isLoggedIn()) {
            mNavDrawer = (NavigationView) findViewById(R.id.nvView);
            LogText.setTitle("Logout");
        } else {
            isLog = true;
            proPic.setImageResource(R.drawable.ic_profile);
            ProfText.setText("Login/SignUp");
            LogText.setTitle("Login");
        }

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //setup toolbar service location item
        MenuItem item = menu.findItem(R.id.menu_service_location);
        MenuItemCompat.setActionView(item, R.layout.menu_service_location_layout);
        TextView textView = (TextView) MenuItemCompat.getActionView(item);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage(R.string.dialog_message_service_location)
                        .setTitle(R.string.dialog_title_service_location);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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

//        if (item.getItemId() == R.id.menu_service_location) {
//            return true;
//        }

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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//
//        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getSupportFragmentManager().popBackStack();
//        }

    }

    public void onEvent(UpdateActionBarTitleEvent e) {
        getSupportActionBar().setTitle(e.getTitle());

    }

    @Override
    public void onStart() {
        if (session.isLoggedIn()) {
            mGoogleApiClient.connect();

            //Set Profile Picture and Text when MainActicity comes from other activities and was not logged in when app started
                HashMap<String, String> user = session.getUserDetails();

                Glide.with(getApplicationContext())
                        .load(user.get(SessionManager.KEY_PICTURE))
                        .into(proPic);
                ProfText.setText(user.get(SessionManager.KEY_NAME));

            }

        super.onStart();
//        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
//        EventBus.getDefault().unregister(this);
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
//                    Toast.makeText(this, "Profile page is under developement", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                    break;
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("promo_images", mPromoImages);
                    intent.putExtras(bundle);
                    startActivity(intent);
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

