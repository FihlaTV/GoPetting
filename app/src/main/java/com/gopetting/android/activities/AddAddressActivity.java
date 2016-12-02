package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.progress_bar_container)
    FrameLayout mProgressBarContainer;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButton;
    @BindView(R.id.edit_text_full_name)
    EditText mEditTextFullName;
    @BindView(R.id.edit_text_address)
    EditText mEditTextAddress;
    @BindView(R.id.edit_text_area)
    EditText mEditTextArea;
    @BindView(R.id.edit_text_landmark)
    EditText mEditTextLandmark;
    @BindView(R.id.edit_text_city)
    EditText mEditTextCity;
    @BindView(R.id.edit_text_state)
    EditText mEditTextState;
    @BindView(R.id.edit_text_pincode)
    EditText mEditTextPincode;
    @BindView(R.id.edit_text_mobile)
    EditText mEditTextMobile;


    private static String sUserId;


    private SessionManager mSessionManager;
    private Credential mCredential;

    private int mPincode;
    private Status mStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar.setVisibility(View.GONE);

        //Submit Button Click Listener
        mRelativeLayoutFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Check all mandatory text boxes are filled.
                if (mEditTextFullName.getText().toString().trim().equals("")
                        || mEditTextAddress.getText().toString().trim().equals("")
                        || mEditTextArea.getText().toString().trim().equals("")
                        || mEditTextCity.getText().toString().trim().equals("")
                        || mEditTextState.getText().toString().trim().equals("")
                        || mEditTextPincode.getText().toString().trim().equals("")
                        || mEditTextMobile.getText().toString().trim().equals("")) {


                    Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_add_address, Snackbar.LENGTH_LONG).show();

                } else {
                    if (ConnectivityReceiver.isConnected()) {

                        mSessionManager = new SessionManager(getApplicationContext());

                        if (mSessionManager.isLoggedIn()) {

                            mProgressBar.setVisibility(View.VISIBLE);

                            //Set Background to Black with Opacity 50%
                            mProgressBarContainer.setBackgroundResource(R.color.black);
                            mProgressBarContainer.getBackground().setAlpha(50);

                            //To disable user interaction with background views
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                            sUserId = mSessionManager.getUserId();       //Extract unique UserId
                            getServerData(1);   //Sending DATA_REQUEST_ID=1; Get AddressFirstStatus

                        } else {
                            //user is not logged in; Ideally this will not happen
                        }

                    }else {
                        showSnack();
                    }

                }

            }
        });


    }


    private void getServerData(final int dataRequestId) {

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        mCredential = oAuthTokenService.getAccessTokenWithID("default");

        if (mCredential == null || mCredential.getAccess_token() == null || oAuthTokenService.isExpired("default")) {
            oAuthTokenService.authenticateUsingOAuth(new Controller.MethodsCallback<Credential>() {
                @Override
                public void failure(Throwable t) {
                    Toast.makeText(AddAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }

                @Override
                public void success(Credential credential) {
                    if (credential != null) {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        chooseDataRequest(dataRequestId);
                    }
                }

                @Override
                public void responseBody(Call<Credential> call) {

                }
            });
        } else {

            chooseDataRequest(dataRequestId);
        }
    }

    private void chooseDataRequest(int dataRequestId) {

        switch (dataRequestId) {
            case 1: //Get AddressFirstStatus
                getAddressFirstStatus(dataRequestId);
                break;

            default:
                Log.i("AddAddressActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void getAddressFirstStatus(int dataRequestId) {

        String landmark = mEditTextLandmark.getText().toString();
//
//        if (landmark.trim().equals("")) {
//
//        }

        //Convert EditTextPincode string to integer
        try {
            mPincode = Integer.parseInt(mEditTextPincode.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }


//        Call API only when Pincode is not zero
        if (mPincode !=0 ) {

            Controller.GetAddressFirstStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetAddressFirstStatus.class);

            Call<Status> call = retrofitSingleton.getAddressFirstStatus(
                    "Bearer " + mCredential.getAccess_token()
                    , sUserId
                    , mEditTextFullName.getText().toString()
                    , mEditTextAddress.getText().toString()
                    , mEditTextArea.getText().toString()
                    , landmark
                    , mEditTextCity.getText().toString()
                    , mEditTextState.getText().toString()
                    , mPincode                                  //Sending Pincode in 'int' as 'int' type is expected in Controller
                    , mEditTextMobile.getText().toString()
            );

            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    if (response.isSuccessful()) {

                        mStatus = response.body();

                        mProgressBar.setVisibility(View.GONE);

                        //Remove Background
                        mProgressBarContainer.setBackgroundResource(0);

                        //To enable user interaction with background views; This was disable earlier for ProgressBar
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        checkAddressStatus();


                    } else {
                        Log.d("onResponse", "getAddAddressFirstStatus :onResponse:notSuccessful");
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable t) {
                    Toast.makeText(AddAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                }
            });


        }


    }

    private void checkAddressStatus() {

        //Pincode is not under service area
        if (mStatus.getStatus() == 101){

            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_non_servicable_pincode, Snackbar.LENGTH_LONG).show();

        }else if (mStatus.getStatus() == 12){

            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();

            bundle.putInt("address_id",mStatus.getId());

            bundle.putString("full_name",mEditTextFullName.getText().toString());
            bundle.putString("address",mEditTextAddress.getText().toString());
            bundle.putString("area",mEditTextArea.getText().toString());
            bundle.putString("landmark",mEditTextLandmark.getText().toString());
            bundle.putString("city",mEditTextCity.getText().toString());
            bundle.putString("state",mEditTextState.getText().toString());
            bundle.putString("pincode",mEditTextPincode.getText().toString());  //Sending Pincode in String as it's String type in 'Address' Model also
            bundle.putString("phone",mEditTextMobile.getText().toString());

            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();   //Finishing Activity as user presses 'Done' button. Send back Address Data.


        }

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
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void showSnack() {
        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();
    }


}
