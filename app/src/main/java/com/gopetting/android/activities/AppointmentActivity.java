package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Appointment;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.CartItem;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.DateTimeSlot;
import com.gopetting.android.models.Dateslot;
import com.gopetting.android.models.Timeslot;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.gopetting.android.utils.HorizontalDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import org.parceler.Parcels;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {

    //region 'VIEW BINDINGS'

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
    @BindView(R.id.tv_in_house_delivery_type)
    TextView mTextViewInHouseDeliveryType;
    @BindView(R.id.tv_psd_delivery_type)
    TextView mTextViewPsdDeliveryType;
    @BindView(R.id.rl_address_date_time_container)
    RelativeLayout mRelativeLayoutAddressDateTimeContainer;
    @BindView(R.id.ll_default_address_container)
    LinearLayout mLinearLayoutDefaultAddressContainer;
    @BindView(R.id.ll_delivery_type_container)
    LinearLayout mLinearLayoutDeliveryTypeContainer;
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
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButton;
    @BindView(R.id.radio_button_in_house)
    public RadioButton mRadioButtonInHouse;
    @BindView(R.id.radio_button_psd)
    public RadioButton mRadioButtonPsd;
    @BindView(R.id.ll_in_house_container)
    public LinearLayout mLinearLayoutInHouseContainer;
    @BindView(R.id.ll_psd_container)
    public LinearLayout mLinearLayoutPsdContainer;
    @BindView(R.id.ll_no_slots_container)
    public LinearLayout mLinearLayoutNoSlotsContainer;
    @BindView(R.id.tv_no_slots)
    TextView mTextViewNoSlots;
    //endregion


    private static final int APPOINTMENT_INTENT_IDENTIFIER_1 = 301 ; //INTENT IDENTIFIER; Start AddAddressActivity and Get New Address
    private static final int APPOINTMENT_INTENT_IDENTIFIER_2 = 302 ; //INTENT IDENTIFIER; Start AddressListActivity and Get Address
    private static final int APPOINTMENT_INTENT_IDENTIFIER_3 = 303 ; //INTENT IDENTIFIER; Start OrderSummaryActivity; Mainly will be used for
                                                                     //moving 'mCart' object.
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
    private int mSelectedTimeslotId;
    private String mSelectedTimeslot;
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
    private String mSelectedFullName;
    private String mSelectedFullAddress;
    private String mSelectedPhone;
    private int mSelectedAddressId;
    private String mSelectedPincode;
    private int mCurrentDeliveryType;
    private int mInHouseCharges;
    private int mPsdCharges;
    private int mNoRecordsFlag=0;       //To be used for 'getAppointmentData' method, when no date/time slots received from server and Default address is to set up
    private int mDeliveryCharges;
    private String mSelectedDeliveryType;

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

            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_not_logged_in, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_empty_cart, Snackbar.LENGTH_SHORT).show();
                mRelativeLayoutAppointmentContainer.setVisibility(View.GONE);
            }

            sUserId =mSessionManager.getUserId();       //Extract unique UserId

            if (sUserId != null) {
                getServerData(1); //Sending DATA_REQUEST_ID=1; //Get Appointment Data (Address Date Time)
            }else {
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_userid_empty, Snackbar.LENGTH_LONG).show();
            }
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            initDeliveryTypeListeners();     //Initialize delivery type listeners

        }
    }

    /**
     * Initialize delivery type listeners
     */
    private void initDeliveryTypeListeners() {

        //In House Radio Button Listener
        mLinearLayoutInHouseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mRadioButtonInHouse.isChecked()) {

                    mProgressBar.setVisibility(View.VISIBLE);
                    mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                    mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);

                    //To disable user interaction with background views
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mRadioButtonInHouse.setChecked(true);
                    mRadioButtonPsd.setChecked(false);

                    mCurrentDeliveryType=1;         //In-House=1; PSD=2

                    getServerData(2);



                }
            }
        });


        //PSD Radio Button Listener
        mLinearLayoutPsdContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mRadioButtonPsd.isChecked()) {

                    mProgressBar.setVisibility(View.VISIBLE);
                    mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                    mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);

                    //To disable user interaction with background views
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    mRadioButtonInHouse.setChecked(false);
                    mRadioButtonPsd.setChecked(true);

                    mCurrentDeliveryType=2;         //In-House=1; PSD=2

                    getServerData(2);


                }
            }
        });


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
//                    Toast.makeText(AppointmentActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
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
//                Log.i("AppointmentActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void getAppointmentData(int dataRequestId) {

        Controller.GetAppointmentDetails retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetAppointmentDetails.class);
        Call<Appointment> call = retrofitSingleton.getAppointmentDetails("Bearer " + mCredential.getAccess_token(),sUserId,concatenate(),mCart.getCartServicePackages().size());
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful()) {

                    mAppointment = response.body();

                    mInHouseCharges = mAppointment.getInHouseCharges();
                    mPsdCharges = mAppointment.getPsdCharges();

                    mCurrentDeliveryType=1;     //In-House=1; PSD=2         //Set Default delivery type 'In-House' during app start


                    //server status = 101; No address available for user
                    if (mAppointment.getStatus() == 101) {

                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        addAddressClickListener();

                    //server status = 12; Address available,Set Address, Default In-House delivery type and Date & Time
                    } else if (mAppointment.getStatus() == 12) {

                        //Show Default Address, Delivery Type and  Date, Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDeliveryTypeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);



                        addAddressClickListener();

                        mNoRecordsFlag=0;       //Default is 0; so that initAppointment() could execute setupDateTimeLayouts() method

                        //initialize Appointment with data
                        initAppointment();

                        mProgressBar.setVisibility(View.GONE);


                    } else if (mAppointment.getStatus() == 10) {

                        setNoSlotsMessage();

                        //Show Default Address, Delivery Type and  Date, Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDeliveryTypeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                        mLinearLayoutNoSlotsContainer.setVisibility(View.VISIBLE);

                        mNoRecordsFlag=1;       //1; to not execute setupDateTimeLayouts() method

                        addAddressClickListener();

                        //initialize Appointment with data
                        initAppointment();

                        mNoRecordsFlag=0;       //0; Reset flag; so that initAppointment() could function normally for rest of the calls

                        mProgressBar.setVisibility(View.GONE);

                        //To enable user interaction with background views; This was disabled earlier for ProgressBar
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }

                else {
//                    Log.d("onResponse", "getAppointmentData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable throwable) {
//                Toast.makeText(AppointmentActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void setNoSlotsMessage() {

        Resources res = getResources();

        if (mCurrentDeliveryType == 1) {
            String mNoSlotsText = String.format(res.getString(R.string.text_no_slots)
                    ,"In-House" );
            mTextViewNoSlots.setText(mNoSlotsText);
        }else if (mCurrentDeliveryType == 2){
            String mNoSlotsText = String.format(res.getString(R.string.text_no_slots)
                    ,"Pickup-Service-Drop" );
            mTextViewNoSlots.setText(mNoSlotsText);
        }

    }


    private void getDateTimeData(int dataRequestId) {

        Controller.GetDateTimeItems retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetDateTimeItems.class);
        Call<DateTimeSlot> call = retrofitSingleton.getDateTimeData("Bearer " + mCredential.getAccess_token(),sUserId
                                ,mSelectedPincode,concatenate(),mCart.getCartServicePackages().size(),mCurrentDeliveryType);
        call.enqueue(new Callback<DateTimeSlot>() {
            @Override
            public void onResponse(Call<DateTimeSlot> call, Response<DateTimeSlot> response) {
                if (response.isSuccessful()) {

                    mDateTimeslotStatus = response.body().getStatus();

                    //Check DateTimeSlot status
                    if (response.body().getStatus() == 12) {

                        //Update mDateslots object with new items
                        mDateslots = response.body().getDateslots();

                        mInHouseCharges = response.body().getInHouseCharges();  //Fetching These charges may not be required again as it's already been done in getAppointmentData
                        mPsdCharges = response.body().getPsdCharges();          //Still keeping it for now.

                        //Show Default Address, Date and Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDeliveryTypeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);

                        //To enable user interaction with background views; This was disabled earlier for ProgressBar
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        setupDeliveryType();
                        setupDateTimeLayouts();

                        //When no date/time slots received
                    } else if (response.body().getStatus() == 10) {

                        setupDeliveryType();        //Setup Delivery types
                        setNoSlotsMessage();

                        //Show Default Address, Delivery Type and  Date, Time Layouts
                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDeliveryTypeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                        mLinearLayoutNoSlotsContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        //To enable user interaction with background views; This was disabled earlier for ProgressBar
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }


                else {
//                    Log.d("onResponse", "getDateTimeData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<DateTimeSlot> call, Throwable throwable) {
//                Toast.makeText(AppointmentActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void setupDeliveryType() {

        Resources res = getResources();

        if(mInHouseCharges == 0){
            String mInHouseText = String.format(res.getString(R.string.text_in_house_delivery_type)
                    ,"","free");
            mTextViewInHouseDeliveryType.setText(mInHouseText);
        }else {
            String mInHouseText = String.format(res.getString(R.string.text_in_house_delivery_type)
                    ,"Rs.",mInHouseCharges);
            mTextViewInHouseDeliveryType.setText(mInHouseText);
        }

        String mPsdText = String.format(res.getString(R.string.text_psd_delivery_type)
               ,"Rs.",mPsdCharges);
        mTextViewPsdDeliveryType.setText(mPsdText);


        if (mCurrentDeliveryType == 1){
            mRadioButtonInHouse.setChecked(true);
            mRadioButtonPsd.setChecked(false);
        }else if (mCurrentDeliveryType == 2) {
            mRadioButtonInHouse.setChecked(false);
            mRadioButtonPsd.setChecked(true);
        }



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

                if (ConnectivityReceiver.isConnected()) {

                    //1st logic: Fresh Start with No address and haven't pressed 'Add Address' button for adding new address
                    // (double check this statement, I believe, correct is "Fresh start and 'Add Address' PRESSED")
                    if (mAppointment.getStatus() == 101 && mDateTimeslotStatus == 0) {

                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        //Start AddAddressActivity and Get New Address
                        Intent intent = new Intent(AppointmentActivity.this, AddAddressActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("current_delivery_type",mCurrentDeliveryType);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, APPOINTMENT_INTENT_IDENTIFIER_1);


                        //AddAddress Listener will be executed when 'Add Address' button clicked
                        //1st logic : Fresh Start with No address and then added address
                        //2nd logic : Fresh Start with more than 1 address
                        //3rd logic : Fresh Start with more than 1 address; Then selected address from AddressListActivity

                        //5th logic : Fresh start with address but with no Date Time
                        //6th logic : Fresh start with address and with Date Time records and then clicked
                        //7th logic : Fresh start with no slots in In-House; Clicked Add Address, selected address, submitted; Again Pressed Add new address
                    } else if ((mAppointment.getStatus() == 101 && mDateTimeslotStatus == 12)
                            || (mAppointment.getStatus() == 12 && mDateTimeslotStatus == 0)
                            || (mAppointment.getStatus() == 12 && mDateTimeslotStatus == 12)
                            || (mAppointment.getStatus() == 12 && mDateTimeslotStatus == 10)
                            || (mAppointment.getStatus() == 10 && mDateTimeslotStatus == 0)
                            || (mAppointment.getStatus() == 10 && mDateTimeslotStatus == 12)
                            ||(mAppointment.getStatus() == 10 && mDateTimeslotStatus == 10)) {


                        mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDefaultAddressContainer.setVisibility(View.VISIBLE);
                        mLinearLayoutDeliveryTypeContainer.setVisibility(View.VISIBLE);
//                        mLinearLayoutDateTimeContainer.setVisibility(View.VISIBLE);  //Not changing these 2 layout visibility as of now; As not sure, what's the stage at this point
//                        mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);

                        //Start AddressListActivity and     Get Address
                        Intent intent = new Intent(AppointmentActivity.this, AddressListActivity.class);
                        Bundle bundle = new Bundle();

                        if (mAppointment != null) {
                            if (mAppointment.getAddresses().size() != 0)
                                mDefaultAddressId = mAppointment.getAddresses().get(0).getAddressId();

                        } else {
                            mDefaultAddressId = mAddressId;
                        }

                        bundle.putInt("default_address_id", mDefaultAddressId);
                        bundle.putInt("current_delivery_type",mCurrentDeliveryType);        //
                        intent.putExtras(bundle);
                        startActivityForResult(intent, APPOINTMENT_INTENT_IDENTIFIER_2);

                    }

                }else {
                    showSnack();
                }

            }
        });


        mRelativeLayoutFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ConnectivityReceiver.isConnected()) {

                    if ((View.VISIBLE == mLinearLayoutDefaultAddressContainer.getVisibility()) && (View.VISIBLE == mLinearLayoutDateTimeContainer.getVisibility())) {
                        Intent intent = new Intent(AppointmentActivity.this, OrderSummaryActivity.class);
                        Bundle bundle = new Bundle();

                        if (mCurrentDeliveryType == 1){
                            mDeliveryCharges=mInHouseCharges;   //In House charges
                            mSelectedDeliveryType="In-House Service";
                        }else if (mCurrentDeliveryType == 2){
                            mDeliveryCharges=mPsdCharges;       //PSD Charges
                            mSelectedDeliveryType="Pickup-Service-Drop";
                        }


                        bundle.putParcelable("cart", Parcels.wrap(mCart)); //Send cart data;

                        //Send Date, Time and Address Details
                        bundle.putString("selected_date", mSelectedDateslot);
                        bundle.putInt("selected_timeslot_id", mSelectedTimeslotId);
                        bundle.putString("selected_timeslot", mSelectedTimeslot);
                        bundle.putString("selected_full_name", mSelectedFullName);
                        bundle.putInt("selected_address_id", mSelectedAddressId);
                        bundle.putString("selected_full_address", mSelectedFullAddress);
                        bundle.putString("selected_pincode", mSelectedPincode);
                        bundle.putString("selected_phone", mSelectedPhone);
                        bundle.putString("selected_delivery_type", mSelectedDeliveryType);
                        bundle.putInt("delivery_charges", mDeliveryCharges);
                        bundle.putInt("selected_delivery_type_id", mCurrentDeliveryType);

                        intent.putExtras(bundle);
                        startActivityForResult(intent, APPOINTMENT_INTENT_IDENTIFIER_3); //Mainly for transporting 'mCart' object

                    }else if (!(View.VISIBLE == mLinearLayoutDefaultAddressContainer.getVisibility())){
                        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_appointment, Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        //Leaving this blank as of now
                    }
                }else {
                    showSnack();
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


        //Save selected address details for SummaryActivity
        mSelectedFullName = mAppointment.getAddresses().get(0).getFullName();
        mSelectedAddressId = mAppointment.getAddresses().get(0).getAddressId();
        mSelectedFullAddress = fullAddress;
        mSelectedPincode = mAppointment.getAddresses().get(0).getPincode();
        mSelectedPhone = mAppointment.getAddresses().get(0).getPhone();


        //Initialize mDateslots object for DateTimeLayout setup;
        mDateslots = mAppointment.getDateslots();

        setupDeliveryType();

        //mNoRecordsFlag=1; to not execute setupDateTimeLayouts() method
        if (mNoRecordsFlag == 0) {
            setupDateTimeLayouts();
        }


    }

    private void setupDateTimeLayouts() {


        //Setup Date and Time layouts
        mFastItemAdapterDateslot = new FastItemAdapter();
        mFastItemAdapterDateslot.withSelectable(true);

        mFastItemAdapterDateslot.notifyDataSetChanged(); //Added this newly


        try {
            mFastItemAdapterDateslot.add(mDateslots);
        } catch (Exception e) {
//            Log.e("AppointmentActivity", e.toString());
            Crashlytics.logException(e);
        }


        mRecyclerViewDateslot.setAdapter(mFastItemAdapterDateslot);

        mFastItemAdapterDateslot.notifyDataSetChanged();                //Added this newly

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

        mFastItemAdapterTimeslot.notifyDataSetChanged();

        mLayoutManagerTimeslot = new GridLayoutManager(this,3);

        mRecyclerViewTimeslot.setLayoutManager(mLayoutManagerTimeslot);


        //Default Date/Time selection at start of screen; First date and corresponding first timeslot will be selected by default
        mFastItemAdapterDateslot.select(0);
        mFastItemAdapterTimeslot.add(mDateslots.get(0).mTimeslots);
        mFastItemAdapterTimeslot.select(0);

        //Save currently selected Dateslot and Timeslot
        mSelectedDateslot = mDateslots.get(0).getDateslot();
        mSelectedTimeslotId = mDateslots.get(0).mTimeslots.get(0).getTimeslotId();
        mSelectedTimeslot = mDateslots.get(0).mTimeslots.get(0).getTimeslot();


        //Dateslot Fastadapter Click listener
        mFastItemAdapterDateslot.withOnClickListener(new FastAdapter.OnClickListener<Dateslot>() {
            @Override
            public boolean onClick(View v, IAdapter<Dateslot> adapter, Dateslot item, int position) {

                //Set new Timeslots for selected dateslot
                mFastItemAdapterTimeslot.setNewList(item.mTimeslots);

                //Select/highlight currently pressed item and Deselect rest (This is implemented for item correct state color)
                    Set<Integer> selectionsDateslot = mFastItemAdapterDateslot.getSelections();
                    if (!selectionsDateslot.isEmpty()) {
                        int selectedPosition = selectionsDateslot.iterator().next();
                        mFastItemAdapterDateslot.deselect();
                        mFastItemAdapterDateslot.notifyItemChanged(selectedPosition);
                    }
                    mFastItemAdapterDateslot.select(position);


                //Deselect all timeslots (so that only one item can be selected)
                    Set<Integer> selectionsTimeslot = mFastItemAdapterTimeslot.getSelections();
                    if (!selectionsTimeslot.isEmpty()) {
                        int selectedPosition = selectionsTimeslot.iterator().next();
                        mFastItemAdapterTimeslot.deselect();
                        mFastItemAdapterTimeslot.notifyItemChanged(selectedPosition);
                    }

                //Default first timeslot selection on click of a date
                mFastItemAdapterTimeslot.select(0);

                //Save currently selected Dateslot/Timeslot
                mSelectedDateslot = item.getDateslot();
                mSelectedTimeslotId = item.mTimeslots.get(0).getTimeslotId();
                mSelectedTimeslot = item.mTimeslots.get(0).getTimeslot();

                return false;
            }
        });


        //Timeslot Fastadapter Click listener
        mFastItemAdapterTimeslot.withOnClickListener(new FastAdapter.OnClickListener<Timeslot>() {
            @Override
            public boolean onClick(View v, IAdapter<Timeslot> adapter, Timeslot item, int position) {


                //Select/highlight currently pressed item and Deselect rest (This is implemented for item correct state color: Teal or Transparent)
                Set<Integer> selectionsDateslot = mFastItemAdapterTimeslot.getSelections();
                if (!selectionsDateslot.isEmpty()) {
                    int selectedPosition = selectionsDateslot.iterator().next();
                    mFastItemAdapterTimeslot.deselect();
                    mFastItemAdapterTimeslot.notifyItemChanged(selectedPosition);
                }
                mFastItemAdapterTimeslot.select(position);


                //Save currently selected Timeslot
                mSelectedTimeslotId = item.getTimeslotId();
                mSelectedTimeslot = item.getTimeslot();

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

                mAddressId = data.getExtras().getInt("address_id");
                mFullName = data.getExtras().getString("full_name");
                mAddress = data.getExtras().getString("address");
                mArea = data.getExtras().getString("area");
                mLandmark = data.getExtras().getString("landmark");
                mCity = data.getExtras().getString("city");
                mState = data.getExtras().getString("state");
                mPincode = data.getExtras().getString("pincode");
                mPhone = data.getExtras().getString("phone");

                mCurrentDeliveryType = data.getExtras().getInt("current_delivery_type");

                getServerData(2);

                //Set Newly Added Address to text views
                setDefaultAddressLayout();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("Info", "RESULT_CANCELED code was not expected");
            }

        }



        //Intent Identifier; Start AddressListActivity and Get selected Address
        if (requestCode == APPOINTMENT_INTENT_IDENTIFIER_2) {
            if (resultCode == Activity.RESULT_OK) {


                int appointmentDisableFlag = data.getExtras().getInt("appointment_disable_flag");
                if (appointmentDisableFlag == 1){

                    //Hide Default Address, Date and Time Layouts
                    mLinearLayoutDefaultAddressContainer.setVisibility(View.GONE);
                    mLinearLayoutDeliveryTypeContainer.setVisibility(View.GONE);
                    mLinearLayoutDateTimeContainer.setVisibility(View.GONE);
                    mLinearLayoutNoSlotsContainer.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);

//                Resetting flags to act as a start of Appointment Activity with no address for user. As all address were deleted in AddressListActivity
                    mAppointment.setStatus(101);
                    mDateTimeslotStatus = 0;
                    mCurrentDeliveryType=1; //Reset to default delivery type 'In-House'


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


                    mCurrentDeliveryType = data.getExtras().getInt("current_delivery_type");

                    getServerData(2);

                    //Set Newly Added Address to text views
                    setDefaultAddressLayout();
                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {

//                Toast.makeText(AppointmentActivity.this, "Inside Result_canceled",Toast.LENGTH_SHORT).show();
            }

        }


        //Intent Identifier; for Starting OrderSummaryActivity
        //For saving latest cart when moving between activities like service,cart,appointment and ordersummary.
        if (requestCode == APPOINTMENT_INTENT_IDENTIFIER_3) {
            if (resultCode == Activity.RESULT_OK) {
                mCart = (Cart) Parcels.unwrap(data.getParcelableExtra("cart")); //Update mCart object

            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("Info", "RESULT_CANCELED code was not expected");
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

        //Save selected address details for SummaryActivity
        mSelectedFullName = mFullName;
        mSelectedAddressId = mAddressId;
        mSelectedFullAddress = fullAddress;
        mSelectedPincode = mPincode;
        mSelectedPhone = mPhone;

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
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("cart", Parcels.wrap(mCart));
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();   //Finishing Activity as user press back button and want to update ServiceActivity Cart icon count if there's any change in that using onActivityResult

        super.onBackPressed();
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

    private void showSnack() {

        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();


    }




    /**
     *
     * @return Concatenated service package ids with , comma
     */
    private String concatenate() {

        String concatenatedString = "";
        int i = 0;

        for (CartItem cartItem : mCart.getCartServicePackages())
        {
            if (i==0){
                concatenatedString += Integer.toString(cartItem.getServicePackageId());
                i=1;
            }else {
                concatenatedString += "," + Integer.toString(cartItem.getServicePackageId());
            }
        }

        return concatenatedString;

    }

}
