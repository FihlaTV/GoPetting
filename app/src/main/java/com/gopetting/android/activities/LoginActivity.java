package com.gopetting.android.activities;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.User;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */


//    private static final String[] DUMMY_CREDENTIALS = new String[]{       //         --ssahu: Disabling Custom Signup
//            "foo@example.com:hello", "bar@example.com:world"
//    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

/*         --ssahu: Disabling Custom Signup


    private UserLoginTask mAuthTask = null;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

*/

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private CallbackManager callbackManager;

    private Button mPlusSignInButton;
//    private Button mEmailSignInButton;

    //    private TextView txt_create, txt_forgot;
    private LoginButton facebookLoginButton;

    ProgressDialog ringProgressDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPromoImages = new ArrayList<>();

        //Get Promotional Images
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
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


        //Taking reference of UserName and Password Fiedls

/*      --ssahu: Disabling Custom Signup


         mUserName =(AutoCompleteTextView)findViewById(R.id.txt_email);
         mUserPassword = (AutoCompleteTextView)findViewById(R.id.txt_password);

*/
        /*Setting the Text Font*//*
        TextView txt = (TextView) findViewById(R.id.txt_forgot);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont-4.2.0");
        txt.setTypeface(font);*/


        session = new SessionManager(getApplicationContext());
        // TODO: This IsGbtnClickInd ind checks if Google login button is clicked. check for the the case when
        // TODO: G Plus button is clicked , user canceled it and want to login from FB account.
        IsGbtnClickInd = false;
        initInstances();
    }

    private void initInstances() {

/*      --ssahu: Disabling Custom Signup

        // Set up the login form.
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.txt_email);

*/

        populateAutoComplete();

/*
        mPasswordView = (EditText) findViewById(R.id.txt_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        txt_create = (TextView) findViewById(R.id.txt_create);
        txt_create.setOnClickListener(this);

        txt_forgot = (TextView) findViewById(R.id.txt_forgot);
        txt_forgot.setOnClickListener(this);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

*/


        //Google+ Login
        mPlusSignInButton = (Button) findViewById(R.id.btn_login_google);
        /*mPlusSignInButton.setSize(SignInButton.SIZE_WIDE);*/
        mPlusSignInButton.setOnClickListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        //Facebook Login

        mFbSignInButton = (Button) findViewById(R.id.btn_login_fb);
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_fb_native);

        mFbSignInButton.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(findViewById(R.id.ll_login_container), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    /**
     * Callback received when a permissions request has been completed.
     */


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

/*      --ssahu: Disabling Custom Signup

    private void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validateUserInfo = new ValidateUserInfo();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !validateUserInfo.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!validateUserInfo.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            /*RetrofitServiceGenerator.ItemDetail retrofitService = RetrofitServiceGenerator.createService(RetrofitServiceGenerator.ItemDetail.class);
            retrofitService.Login(email,password,new Callback<List<RetrofitServiceGenerator.LoginModel>>() {
                        @Override
                        public void onResponse(Call<List<RetrofitServiceGenerator.LoginModel>> call, Response<List<RetrofitServiceGenerator.LoginModel>> response) {
                            Toast.makeText(LoginActivity.this, "Could not resolve ConnectionResult", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<List<RetrofitServiceGenerator.LoginModel>> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Could not resolve ConnectionResult", Toast.LENGTH_LONG).show();
                        }}

                );


//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
        }
    }

*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (IsGbtnClickInd == true) {
            getProfileInformation();
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */

/*    --ssahu: Disabling Custom Signup


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

*/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

//        mEmailView.setAdapter(adapter);           /*   --ssahu: Disabling Custom Signup
    }

    @Override
    public void onClick(View v) {
//        String email = mEmailView.getText().toString();       /*   --ssahu: Disabling Custom Signup


        switch (v.getId()) {
            case R.id.btn_login_google:
                onSignInClicked();
                break;

            case R.id.btn_login_fb:
                facebookLoginButton.performClick();
                fbRegisterCallback();
                break;



/*      --ssahu: Disabling Custom Signup


            case R.id.email_sign_in_button:
                attemptLogin();
                break;
            case R.id.txt_create:
                Toast.makeText(LoginActivity.this, "Start RegisterActivity", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                intent.putExtra(Constants.TAG_EMAIL, email);
//                startActivity(intent);
                finish();
                break;
            case R.id.txt_forgot:
                Toast.makeText(LoginActivity.this, "Start ForgotPassActivity", Toast.LENGTH_LONG).show();
//                Intent intentForgot = new Intent(LoginActivity.this, ForgotPassActivity.class);
//                intentForgot.putExtra(Constants.TAG_EMAIL, email);
//                startActivity(intentForgot);
                finish();
                break;

*/
        }
    }

    private void fbRegisterCallback() {

        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email"));

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
                                    handleSignInResult(object);
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
//                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
//                Snackbar.make(findViewById(R.id.ll_login), R.string.login_cancelled, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Error on Login, check your facebook app_id", Toast.LENGTH_LONG).show();
            }
        });

    }

    // handles the sign in functionality
    public void handleSignInResult(JSONObject userInfo) {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        //TODO: This flow should be optimised : This Iscorrect we are using there in MainActivity to check if it returned from Login scree
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


    private void onSignInClicked() {
//        toastLoading.show();
        IsGbtnClickInd = true;
        mIsResolving = false;
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        ringProgressDialog = ProgressDialog.show(LoginActivity.this, "Connecting...", "Atempting to connect", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mShouldResolve = true;
                    mGoogleApiClient.connect();
                } catch (Exception e) {
                    ringProgressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        ringProgressDialog.dismiss();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(Constants.TAG_LOGIN, "onConnectionFailed:" + connectionResult);
        ringProgressDialog.dismiss();

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;

                } catch (IntentSender.SendIntentException e) {
                    Log.e(Constants.TAG_LOGIN, "Could not resolve ConnectionResult.", e);
                    Toast.makeText(LoginActivity.this, "Could not resolve ConnectionResult", Toast.LENGTH_LONG).show();
                    mIsResolving = false;
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(LoginActivity.this, "Error on Login, check your google + login method", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show the signed-out UI
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        // mIsResolving=true;
        ringProgressDialog.dismiss();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {


                mCurrentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);

                mFirstName = mCurrentPerson.getName().getGivenName();
                mLastName = mCurrentPerson.getName().getFamilyName();
                mId = mCurrentPerson.getId();

                mEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);

                getServerData(1);       //Indicator=1 for google_id;  By ssahu

                //Clearing the Prefs
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.clear();
                editor.commit();

                session.setLogin(true);

                session.createGplusLogin(mCurrentPerson, mEmailId, mUser);
                //setPersonalInfo(currentPerson);

                //Below Code Modified by SS
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("promo_images", mPromoImages);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //Clear stack; for exiting activity
                startActivity(intent);
                LoginActivity.this.finish();    //for exiting activity





            /*String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String birth = currentPerson.getBirthday();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);*/

                //startActivity(new Intent(LoginActivity.this, newClass.class));
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
//                personPhotoUrl = personPhotoUrl.substring(0,
//                        personPhotoUrl.length() - 2)
//                        + PROFILE_PIC_SIZE;

                //new LoadProfileImage().execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } else {
            //ResolveSignInError();
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

/*      --ssahu: Disabling custom signup


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                        String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success
        ) {
            mAuthTask = null;
//            showProgress(false);

            if (success) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);      --ssahu: Disabling custom signup
        }
    }

*/
    private void getServerData(final int indicator) {        // By ssahu

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        credential = oAuthTokenService.getAccessTokenWithID("default");

        if (credential == null || credential.getAccess_token() == null || oAuthTokenService.isExpired("default")) {
            oAuthTokenService.authenticateUsingOAuth(new Controller.MethodsCallback<Credential>() {
                @Override
                public void failure(Throwable throwable) {
                    Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
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

    private void getUserId(int indicator) {          // By ssahu

        Controller.GetUserId retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetUserId.class);
        Call<User> call = retrofitSingleton.getUserId("Bearer " + credential.getAccess_token(), mId, indicator, mEmailId, mFirstName, mLastName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    mUser = response.body();
                    session.setUserId(mUser);       //Set user_id in sessionn : ssahu


                } else {
                    Log.d("Error Response", "LoginActivity :Error Response");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("onFailure", "LoginActivity :Error Response");
            }
        });


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
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


}
