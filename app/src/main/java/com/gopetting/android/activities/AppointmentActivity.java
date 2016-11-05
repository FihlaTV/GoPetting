package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Appointment;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.DateTimeSlot;
import com.gopetting.android.models.Dateslot;
import com.gopetting.android.models.Timeslot;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.HorizontalDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_appointment_container)
    RelativeLayout mRelativeLayoutAppointmentContainer;
    @BindView(R.id.tv_full_name)
    TextView mTextViewFullName;
    @BindView(R.id.tv_full_address)
    TextView mTextViewFullAddress;
    @BindView(R.id.tv_phone)
    TextView mTextViewPhone;
    @BindView(R.id.rl_address_date_time_container)
    RelativeLayout mRelativeLayoutAddressDateTimeContainer;
    @BindView(R.id.ll_default_address_container)
    LinearLayout mLinearLayoutDefaultAddressContainer;
    @BindView(R.id.ll_date_time_container)
    LinearLayout mLinearLayoutDateTimeContainer;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.recycler_view_date)
    RecyclerView mRecyclerViewDateslot;
    @BindView(R.id.recycler_view_time)
    RecyclerView mRecyclerViewTimeslot;
    @BindView(R.id.ll_add_address)
    LinearLayout mLinearLayoutAddAddress;


    private static final int APPOINTMENT_INTENT_IDENTIFIER_1 = 301 ; //INTENT IDENTIFIER; Start AddAddressActivity and Get New Address
    private static final int APPOINTMENT_INTENT_IDENTIFIER_2 = 302 ; //INTENT IDENTIFIER; Start AddressListActivity and Get Address
    private static String sUserId;


    private Cart mCart;
    private SessionManager mSessionManager;
    private Credential mCredential;
    private Appointment mAppointment;
    private FastItemAdapter mFastItemAdapterDateslot;
    private LinearLayoutManager mLayoutManagerDateslot;
    private Bundle mSavedInstanceState;
    private FastItemAdapter mFastItemAdapterTimeslot;
    private LinearLayoutManager mLayoutManagerTimeslot;
    private String mSelectedDateslot;
    private int mSelectedTimeslot;
    private String mFullName;
    private String mAddress;
    private String mArea;
    private String mLandmark;
    private String mCity;
    private String mState;
    private String mPincode;
    private String mPhone;
    private int mAddressId;
    private List<Dateslot> mDateslots;
    private int mDateTimeslotStatus;        //To be used as a Status flag for correctly starting activity when 'Add Address' button clicked
    private int mDefaultAddressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        ButterKnife.bind(this);

        mSavedInstanceState = savedInstanceState;
        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.GONE);

        mSessionManager = new SessionManager(getApplicationContext());

        //Show a snackbar if user is not logged in; However, at this screen this case is not possible.
        if (!mSessionManager.isLoggedIn()){

            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_not_logged_in, Snackbar.LENGTH_LONG).show();
            mRelativeLayoutAppointmentContainer.setVisibility(View.GONE);

        }else {

            //Get Cart Data
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                mCart = Parcels.unwrap(intent.getParcelableExtra("cart"));
            }

            //Show a snackbar if cart is empty; However, at this screen this case is not possible.
            if (mCart.mCartItems.size()<=0){
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_empty_cart, Snackbar.LENGTH_LONG).show();
                mRelativeLayoutAppointmentContainer.setVisibility(View.GONE);
            }

            sUserId =mSessionManager.getUserId();       //Extract unique UserId

            getServerData(1); //Sending DATA_REQUEST_ID=1; //Get Appointment Data (Address Date Time)

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void getServerData(final int dataRequestId) {

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(this);

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        mCredential = oAuthTokenService.getAccessTokenWithID("default");

        if(mCredential == null || mCredential.getAccess_token()==null || oAuthTokenService.isExpired("default"))
        {
            oAuthTokenService.authenticateUsingOAuth( new Controller.MethodsCallback<Credential>()
            {
                @Override public void failure(Throwable t)
                {
                    Toast.makeText(AppointmentActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        chooseDataRequest(dataRequestId);
                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            chooseDataRequest(dataRequestId);
        }
    }

    private void chooseDataRequest(int dataRequestId) {

        switch (dataRequestId) {
            case 1: //Get Appointment Data (Address Date Time)
                getAppointmentData(dataRequestId);
                break;
            case 2: //Get Data Time Data
                getDateTimeData(dataRequestId);
                break;
            default:
                Log.i("AppointmentActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void getAppointmentData(int dataRequestId) {

        Controller.GetAppointmentDetails retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetAppointmentDetails.class);
        Call<Appointment> call = retrofitSingleton.getAppointmentDetails("Bearer " + mCredential.getAccess_token(),sUserId);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful()) {

                    mAppointment = response.body();


                    //server status = 101; No address available for user
                    if (mAppointment.getStatus() == 101) {

                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        addAddressClickListener();

                    //server status = 12; Address available,Set Address, Date & Time
                    } else if (mAppointment.getStatus() == 12) {

                        //Show Default Address, Date and Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);

                        addAddressClickListener();

                        //initialize Appointment with data
                        initAppointment();



                    }

                }

                else {
                    Log.d("onResponse", "getAppointmentData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable throwable) {
                Toast.makeText(AppointmentActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });



    }

    private void getDateTimeData(int dataRequestId) {

        Controller.GetDateTimeItems retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetDateTimeItems.class);
        Call<DateTimeSlot> call = retrofitSingleton.getDateTimeData("Bearer " + mCredential.getAccess_token(),sUserId,mPincode);
        call.enqueue(new Callback<DateTimeSlot>() {
            @Override
            public void onResponse(Call<DateTimeSlot> call, Response<DateTimeSlot> response) {
                if (response.isSuccessful()) {

                    mDateTimeslotStatus = response.body().getStatus();

                    //Check DateTimeSlot status
                    if (response.body().getStatus() == 12) {

                        //Update mDateslots object with new items
                        mDateslots = response.body().getDateslots();

                        //Show Default Address, Date and Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);

                        setupDateTimeLayouts();
                    }

                }


                else {
                    Log.d("onResponse", "getDateTimeData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<DateTimeSlot> call, Throwable throwable) {
                Toast.makeText(AppointmentActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });



    }


/**********************************************************
 *                  Screen Flow Status Scenarios
 *
 *                  1. Fresh Start: No address
                    appointment status: 101
                    mDatetimeslot status: 0

                    Start AddAddressActivity

                    2. Now Press 'Add Address'

                    appointment status: 101
                    mDatetimeslot status: 12


                    3. Now again Press 'Add Address'

                    appointment status: 101
                    mDatetimeslot status: 12

                    Start AddressListActivity


                    4. Fresh Start: 3 Addresses

                    appointment status: 12
                    mDatetimeslot status: 0

                    5. Now press 'Add address'

                    Start AddressListActivity

 ****One more scenario need to add; Fresh start with more than 1 address then clicked 'Add Address';
 ****Selected address; This scenario however have been covered in logic
 *
 *************************************************************/



    private void addAddressClickListener() {

        mLinearLayoutAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    //1st logic: Fresh Start with No address and haven't pressed 'Add Address' button for adding new address
                    if (mAppointment.getStatus() == 101 && mDateTimeslotStatus == 0 ) {

                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        //Start AddAddressActivity and Get New Address
                        Intent intent = new Intent(AppointmentActivity.this, AddAddressActivity.class);
                        startActivityForResult(intent, APPOINTMENT_INTENT_IDENTIFIER_1);


                    //1st logic : Fresh Start with No address and then added address
                    //2nd logic : Fresh Start with more than 1 address
                    //3rd logic : Fresh Start with more than 1 address; Then selected address from AddressListActivity
                    } else if ((mAppointment.getStatus() == 101 && mDateTimeslotStatus == 12)
                                || (mAppointment.getStatus() == 12 && mDateTimeslotStatus == 0)
                                || (mAppointment.getStatus() == 12 && mDateTimeslotStatus == 12)) {


                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);

                        //Start AddressListActivity and Get Address
                        Intent intent = new Intent(AppointmentActivity.this, AddressListActivity.class);
                        Bundle bundle = new Bundle();

                        if (mAppointment != null) {
                            if (mAppointment.getAddresses().size() != 0)
                            mDefaultAddressId = mAppointment.getAddresses().get(0).getAddressId();

                        }else{
                            mDefaultAddressId = mAddressId;
                        }

                        bundle.putInt("default_address_id",mDefaultAddressId);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, APPOINTMENT_INTENT_IDENTIFIER_2);

                    }


            }
        });


    }

    private void initAppointment() {

        mTextViewFullName.setText(mAppointment.getAddresses().get(0).getFullName());
        Resources res = getResources();
        String fullAddress = String.format(res.getString(R.string.full_address)
                                            ,mAppointment.getAddresses().get(0).getAddress()
                                            ,mAppointment.getAddresses().get(0).getArea()
                                            ,mAppointment.getAddresses().get(0).getLandmark()
                                            ,mAppointment.getAddresses().get(0).getCity()
                                            ,mAppointment.getAddresses().get(0).getState()
                                            ,mAppointment.getAddresses().get(0).getPincode()
                                            );


        mTextViewFullAddress.setText(fullAddress);
        mTextViewPhone.setText(mAppointment.getAddresses().get(0).getPhone());

        //Initialize mDateslots object for DateTimeLayout setup;
        mDateslots = mAppointment.getDateslots();

        setupDateTimeLayouts();


    }

    private void setupDateTimeLayouts() {


        //Setup Date and Time layouts
        mFastItemAdapterDateslot = new FastItemAdapter();
        mFastItemAdapterDateslot.withSelectable(true);

        try {
            mFastItemAdapterDateslot.add(mDateslots);
        } catch (Exception e) {
            Log.e("AppointmentActivity", e.toString());
            Crashlytics.logException(e);
        }

        mRecyclerViewDateslot.setAdapter(mFastItemAdapterDateslot);

        mLayoutManagerDateslot = new LinearLayoutManager(this);
        mLayoutManagerDateslot.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewDateslot.setLayoutManager(mLayoutManagerDateslot);
        mRecyclerViewDateslot.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewDateslot.addItemDecoration(new HorizontalDividerItemDecoration(this)); //Adding item divider

        mProgressBar.setVisibility(View.GONE);

        //restore selections (this has to be done after the items were added
        mFastItemAdapterDateslot.withSavedInstanceState(mSavedInstanceState);


        //Setup Timeslot RecyclerView
        mFastItemAdapterTimeslot = new FastItemAdapter();
        mFastItemAdapterTimeslot.withSelectable(true);

        mRecyclerViewTimeslot.setAdapter(mFastItemAdapterTimeslot);

        mLayoutManagerTimeslot = new GridLayoutManager(this,3);

        mRecyclerViewTimeslot.setLayoutManager(mLayoutManagerTimeslot);


        //Default Date/Time selection at start of screen; First date and corresponding first timeslot will be selected by default
        mFastItemAdapterDateslot.select(0);
        mFastItemAdapterTimeslot.add(mDateslots.get(0).mTimeslots);
        mFastItemAdapterTimeslot.select(0);

        //Save currently selected Dateslot and Timeslot
        mSelectedDateslot = mDateslots.get(0).getDateslot();
        mSelectedTimeslot = mDateslots.get(0).mTimeslots.get(0).getTimeslotId();


        //Dateslot Fastadapter Click listener
        mFastItemAdapterDateslot.withOnClickListener(new FastAdapter.OnClickListener<Dateslot>() {
            @Override
            public boolean onClick(View v, IAdapter<Dateslot> adapter, Dateslot item, int position) {

                //Set new Timeslots for selected dateslot
                mFastItemAdapterTimeslot.setNewList(item.mTimeslots);

                //Save currently selected Dateslot
                mSelectedDateslot = item.getDateslot();

                return false;
            }
        });


        //Timeslot Fastadapter Click listener
        mFastItemAdapterTimeslot.withOnClickListener(new FastAdapter.OnClickListener<Timeslot>() {
            @Override
            public boolean onClick(View v, IAdapter<Timeslot> adapter, Timeslot item, int position) {

                //Save currently selected Timeslot
                mSelectedTimeslot = item.getTimeslotId();

//                Toast.makeText(AppointmentActivity.this, mSelectedDateslot+mSelectedTimeslot, Toast.LENGTH_SHORT).show();

                return false;
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Intent Identifier; For starting AddAddressActivity and Get New Address
        if (requestCode == APPOINTMENT_INTENT_IDENTIFIER_1) {
            if (resultCode == Activity.RESULT_OK) {

                //Enable Progressbar as need to get DateTime data from server
                mProgressBar.setVisibility(View.VISIBLE);

                mFullName = data.getExtras().getString("full_name");
                mAddress = data.getExtras().getString("address");
                mArea = data.getExtras().getString("area");
                mLandmark = data.getExtras().getString("landmark");
                mCity = data.getExtras().getString("city");
                mState = data.getExtras().getString("state");
                mPincode = data.getExtras().getString("pincode");
                mPhone = data.getExtras().getString("phone");

                getServerData(2);

                //Set Newly Added Address to text views
                setDefaultAddressLayout();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Info", "RESULT_CANCELED code was not expected");
            }

        }



        //Intent Identifier; Start AddressListActivity and Get selected Address
        if (requestCode == APPOINTMENT_INTENT_IDENTIFIER_2) {
            if (resultCode == Activity.RESULT_OK) {


                int appointmentDisableFlag = data.getExtras().getInt("appointment_disable_flag");
                if (appointmentDisableFlag == 1){

                    //Hide Default Address, Date and Time Layouts
                    mLinearLayoutDefaultAddressContainer.setVisibility(View.GONE);
                    mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);

//                Resetting flags to act as a start of Appointment Activity with no address for user. As all address were deleted in AddressListActivity
                    mAppointment.setStatus(101);
                    mDateTimeslotStatus = 0;


                }else {

                    //Enable Progressbar as need to get DateTime data from server
                    mProgressBar.setVisibility(View.VISIBLE);

                    mAddressId = data.getExtras().getInt("address_id");

                    mFullName = data.getExtras().getString("full_name");
                    mAddress = data.getExtras().getString("address");
                    mArea = data.getExtras().getString("area");
                    mLandmark = data.getExtras().getString("landmark");
                    mCity = data.getExtras().getString("city");
                    mState = data.getExtras().getString("state");
                    mPincode = data.getExtras().getString("pincode");
                    mPhone = data.getExtras().getString("phone");

                    getServerData(2);

                    //Set Newly Added Address to text views
                    setDefaultAddressLayout();
                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {

//                Toast.makeText(AppointmentActivity.this, "Inside Result_canceled",Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
            }

        }

}

    private void setDefaultAddressLayout() {

        mTextViewFullName.setText(mFullName);
        Resources res = getResources();
        String fullAddress = String.format(res.getString(R.string.full_address)
                ,mAddress
                ,mArea
                ,mLandmark
                ,mCity
                ,mState
                ,mPincode
        );

        mTextViewFullAddress.setText(fullAddress);
        mTextViewPhone.setText(mPhone);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
