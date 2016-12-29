package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;
import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.User;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.gopetting.android.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */



    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 10;
    private static final String TAG = "LoginActivity";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
//    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
//    private boolean mShouldResolve = false;

    private CallbackManager callbackManager;

    private Button mPlusSignInButton;
//    private Button mEmailSignInButton;

    //    private TextView txt_create, txt_forgot;
    private LoginButton facebookLoginButton;

//    ProgressDialog ringProgressDialog;

    private boolean IsGbtnClickInd;
    SessionManager session;
    //    Button btn_fb_login;
    Toolbar mToolbar;

    //    AutoCompleteTextView mUserName;
//    AutoCompleteTextView mUserPassword;
    private Credential credential;
    private String mFirstName;
    private String mLastName;
    private String mId;
    private String mEmailId;
    private Person mCurrentPerson;
    private User mUser;
    private Button mFbSignInButton;
    private ArrayList<String> mPromoImages;
    private int mOtherActivityFlag;
    private JSONObject mObject;     //By ssahu; workaround for delay in update of UserId
    private FrameLayout mProgressBarContainer;
    private String mPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        mPromoImages = new ArrayList<>();

        //Get Promotional Images
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            //To identify, it's launched by activity other than MainActivity; This is mainly not to interfere in already working code
            mOtherActivityFlag = bundle.getInt("other_activity_flag");          //Code by ssahu;

            mPromoImages = bundle.getStringArrayList("promo_images");
        }


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        /*Setting Toolbar*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar_transparent);      //Changed id; ssahu
        setSupportActionBar(mToolbar);
        //Setting the Home Back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //Hide Actionbar title

        mProgressBarContainer = (FrameLayout) findViewById(R.id.progress_bar_container);   //By ssahu

        session = new SessionManager(getApplicationContext());
        // TODO: This IsGbtnClickInd ind checks if Google login button is clicked. check for the the case when
        // TODO: G Plus button is clicked , user canceled it and want to login from FB account.
        IsGbtnClickInd = false;
        initInstances();
    }

    private void initInstances() {


//        populateAutoComplete();

        //Google+ Login
        mPlusSignInButton = (Button) findViewById(R.id.btn_login_google);
        /*mPlusSignInButton.setSize(SignInButton.SIZE_WIDE);*/
        mPlusSignInButton.setOnClickListener(this);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API)
//                .addScope(new Scope(Scopes.PROFILE))
//                .build();

        //Facebook Login

        mFbSignInButton = (Button) findViewById(R.id.btn_login_fb);
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_fb_native);

        //Placed this line here since it wasn't requesting email at first login
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        mFbSignInButton.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }


//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(findViewById(R.id.ll_login_container), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    }).show();
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }


//        if (IsGbtnClickInd == true) {
//            getProfileInformation();
//        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {

//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String id = acct.getId();
            String name = acct.getDisplayName();

            mFirstName = acct.getGivenName();
            mLastName = acct.getFamilyName();
            mId = acct.getId();
            mEmailId = acct.getEmail();

            if (acct.getPhotoUrl() != null){
                mPicture = acct.getPhotoUrl().toString();
            }else {
                mPicture = "empty";
            }


            getServerData(1);       //Indicator=1 for google_id;  By ssahu

            //Clearing the Prefs
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.commit();

            session.setLogin(true);

            session.createGplusLogin(acct.getDisplayName(), mEmailId, mPicture);



        } else {
//             Signed out, show unauthenticated UI.
            showProgressBarContainer(false);

        }

    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
//
//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }
//
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
////        mEmailView.setAdapter(adapter);           /*   --ssahu: Disabling Custom Signup
//    }

    @Override
    public void onClick(View v) {
//        String email = mEmailView.getText().toString();       /*   --ssahu: Disabling Custom Signup

        if (ConnectivityReceiver.isConnected()) {

            switch (v.getId()) {
                case R.id.btn_login_google:
//                    mProgressBarContainer.setVisibility(View.VISIBLE);
                    showProgressBarContainer(true);
                    onSignInClicked();
                    break;

                case R.id.btn_login_fb:
//                    mProgressBarContainer.setVisibility(View.VISIBLE);
                    showProgressBarContainer(true);
                    facebookLoginButton.performClick();
                    fbRegisterCallback();
                    break;
            }
        }else {
            showSnack();
        }



    }

    private void fbRegisterCallback() {
        

        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code

                                session.logoutUser();

                                //By ssahu
                                mId = object.optString("id");
                                mFirstName = object.optString("first_name");
                                mLastName = object.optString("last_name");
                                mEmailId = object.optString("email");

                                if (mEmailId.isEmpty()) {
                                    Snackbar.make(findViewById(R.id.ll_login), R.string.login_snackbar, Snackbar.LENGTH_LONG).show();
                                } else {
                                    getServerData(2);       //Indicator=2 for facebook_id;  By ssahu

                                    session.createLoginSession(object);

                                    mObject = object;   //By ssahu; workaround for delay in update of UserId
//                                    handleSignInResult(object); //Disabled by ssahu
                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,picture.width(150).height(150)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

//                mProgressBarContainer.setVisibility(View.GONE);
                showProgressBarContainer(false);

//                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
//                Snackbar.make(findViewById(R.id.ll_login), R.string.login_cancelled, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {

//                mProgressBarContainer.setVisibility(View.GONE);
                showProgressBarContainer(false);

//                Toast.makeText(LoginActivity.this, "Error on Login, check your facebook app_id", Toast.LENGTH_LONG).show();
                Crashlytics.logException(exception);
            }
        });

    }

    // handles the sign in functionality
    public void handleSignInResult(JSONObject userInfo) {

        //Code by ssahu
        if (mOtherActivityFlag == 10){          //mOtherActivityFlag = 10; This means login activity is started by activity other than MainActivity;

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);      //Finishing Acticity as login is done and UserId is saved in sharedpreference
            finish();

        }else {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            //TODO: This flow should be optimised : This Is correct we are using there in MainActivity to check if it returned from Login scree
            intent.putExtra("FromLoginActivity", true);
            intent.putExtra("User_profile", userInfo.toString());

            //Below Code Modified by SS
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("promo_images", mPromoImages);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //Clear stack; for exiting activity
            startActivity(intent);
            LoginActivity.this.finish();    //for exiting activity

        }

    }


    private void onSignInClicked() {

        IsGbtnClickInd = true;
//        mIsResolving = false;

//        mShouldResolve = true;
//        mGoogleApiClient.connect();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        showProgressBarContainer(false);
//    }
//
//    @Override
//    public void onConnectionSuspended(int arg0) {
////        mProgressBarContainer.setVisibility(View.GONE);
//        showProgressBarContainer(false);
////        ringProgressDialog.dismiss();
////        mGoogleApiClient.connect();
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
//        Log.d(Constants.TAG_LOGIN, "onConnectionFailed:" + connectionResult);
        showProgressBarContainer(false);

    }


    /**
     * Fetching user's information name, email, profile pic
     */
//    private void getProfileInformation() {
        // mIsResolving=true;

//        ringProgressDialog.dismiss();
//        showProgressBarContainer(false);


//        mGoogleApiClient.connect();
//        if (mGoogleApiClient.isConnected()) {
//            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//
//
//                mCurrentPerson = Plus.PeopleApi
//                        .getCurrentPerson(mGoogleApiClient);
//
//                mFirstName = mCurrentPerson.getName().getGivenName();
//                mLastName = mCurrentPerson.getName().getFamilyName();
//                mId = mCurrentPerson.getId();
//
//                mEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
//
//                getServerData(1);       //Indicator=1 for google_id;  By ssahu
//
//                //Clearing the Prefs
//                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = sharedPrefs.edit();
//                editor.clear();
//                editor.commit();
//
//                session.setLogin(true);
//
//                session.createGplusLogin(mCurrentPerson, mEmailId, mUser);
//                //setPersonalInfo(currentPerson);
//
//
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Person information is null", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            //ResolveSignInError();
//            showProgressBarContainer(false);
//        }
//    }

    private void getServerData(final int indicator) {        // By ssahu

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        credential = oAuthTokenService.getAccessTokenWithID("default");

        if (credential == null || credential.getAccess_token() == null || oAuthTokenService.isExpired("default")) {
            oAuthTokenService.authenticateUsingOAuth(new Controller.MethodsCallback<Credential>() {
                @Override
                public void failure(Throwable throwable) {
//                    Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }

                @Override
                public void success(Credential credential) {
                    if (credential != null) {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        getUserId(indicator);

                    }
                }

                @Override
                public void responseBody(Call<Credential> call) {

                }
            });
        } else {

            getUserId(indicator);

        }
    }

    private void getUserId(final int indicator) {          // By ssahu

        Controller.GetLFirstStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetLFirstStatus.class);
        Call<User> call = retrofitSingleton.getLStatus("Bearer " + credential.getAccess_token(), mId, indicator, mEmailId, mFirstName, mLastName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    mUser = response.body();

                    session.setUserId(mUser);       //Set user_id in sessinn : ssahu

                    if(indicator == 2) {                  //Indicator=2 for facebook;
                    handleSignInResult(mObject);     //By ssahu; workaround for delay in update of UserId when logging from other activities.
                    }

                    if(indicator == 1){
                        sendbackGPlusResult();      //By ssahu;
                    }

                } else {
//                    Log.d("Error Response", "LoginActivity :Error Response");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.d("onFailure", "LoginActivity :Error Response");
            }
        });


    }

//    Workaround for calling login activity from other activities.
    private void sendbackGPlusResult() {

        //Code by ssahu
        if (mOtherActivityFlag == 10){          //mOtherActivityFlag = 10; This means login activity is started by activity other than MainActivity;

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();   //Finishing Acticity as login is done and UserId is saved in sharedpreference

        }else {

            //Below Code Modified by SS
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("promo_images", mPromoImages);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //Clear stack; for exiting activity
            startActivity(intent);
            LoginActivity.this.finish();    //for exiting activity
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //Clicking 'Close' button in toolbar closes LoginActivity
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {

        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }


    private void showSnack() {
        Snackbar.make(findViewById(R.id.rl_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();
    }



    private void showProgressBarContainer(boolean bool){

        //Show Progress Bar
        if (bool){

            mProgressBarContainer.setVisibility(View.VISIBLE);

            //Set Background to Black with Opacity 50%
            mProgressBarContainer.setBackgroundResource(R.color.black);
            mProgressBarContainer.getBackground().setAlpha(50);

            //To disable user interaction with background views
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        }else {

            //Show Progress Bar and Disable User Interaction
            mProgressBarContainer.setVisibility(View.GONE);
            //Remove Background
            mProgressBarContainer.setBackgroundResource(0);
            //To enable user interaction with background views; This was disable earlier for ProgressBar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }


}
