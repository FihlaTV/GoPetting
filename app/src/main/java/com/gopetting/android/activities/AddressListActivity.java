package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Address;
import com.gopetting.android.models.AddressList;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.rl_main_container)
    RelativeLayout mRelativeLayoutMainContainer;
    @BindView(R.id.rl_inner_container)
    RelativeLayout mRelativeLayoutInnerContainer;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerViewAddressList;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButton;
    @BindView(R.id.progress_bar_container)
    FrameLayout mProgressBarContainer;
    @BindView(R.id.ll_add_address)
    LinearLayout mLinearLayoutAddAddress;

    private static final int ADDRESS_LIST_INTENT_IDENTIFIER_1 = 401;
    private static String sUserId;

    private SessionManager mSessionManager;
    private Bundle mSavedInstanceState;
    private Credential mCredential;
    private AddressList mAddressList;
    private FastItemAdapter mFastItemAdapterAddressList;
    private ClickListenerHelper mClickListenerHelper;
    private LinearLayoutManager mLayoutManagerAddressList;
    private int mSelectedAddressId;
    private int mSelectedAddressPosition;
    private String mDeletedAddressIdList = "";
    private int mIndicator;
    private Status mStatus;
    private Address mNextAddressItem;       //To get next/previous item, when currently selected item is going to get deleted in Fastadapter Listener
    private int mAppointmentDisableFlag;    //To disable default displayed layout as there're no address in address list.
    private int mDefaultAddressId;          //To update AppointmentActivity Default address when pressed 'Back Button' if that address is deleted here
    private int mDefaultAddressUpdateFlag;  //To determine whether default address is needed to be updated when pressed 'Back Button'


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Disable footer button click until data is loaded
        mRelativeLayoutFooterButton.setEnabled(true);

        mSavedInstanceState = savedInstanceState;
//        mRelativeLayoutInnerContainer.setVisibility(View.GONE);

        mSessionManager = new SessionManager(getApplicationContext());

        //Show a snackbar if user is not logged in; However, at this screen this case is not possible.
        if (!mSessionManager.isLoggedIn()) {

            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_not_logged_in, Snackbar.LENGTH_LONG).show();
//            mRelativeLayoutMainContainer.setVisibility(View.GONE);

        } else {

            sUserId = mSessionManager.getUserId();       //Extract unique UserId

            addAddressClickListener();

            //Get Currently Selected Default AddressId in AppointmentActivity
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mDefaultAddressId = bundle.getInt("default_address_id"); //To update AppointmentActivity Default address if that address is deleted here
            }

            mRelativeLayoutFooterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    footerButtonClick();

//                    //Send back selected address only when there's at least one address available(When user will delete address,all address might be deleted
//                    if (mAddressList.getAddresses().size()>0) {
//
//                        for (int i=0; i<mAddressList.getAddresses().size(); i++){
//
//                            if (mSelectedAddressId == mAddressList.getAddresses().get(i).getAddressId()){
//                                mSelectedAddressPosition = i;
//                                break;
//                            }
//                        }
//
//                        mProgressBar.setVisibility(View.VISIBLE);
//
//                        //Set Background to Black with Opacity 50%
//                        mProgressBarContainer.setBackgroundResource(R.color.black);
//                        mProgressBarContainer.getBackground().setAlpha(50);
//
//                        //To disable user interaction with background views
//                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//
//                        //Get AddressSecondStatus; Only AddressId status
//                        // Then Send Back data to AppointmentActivity using method 'sendBackSelectedAddress'
//                        getServerData(3);
//
//                    }else {
//                        if (mAppointmentDisableFlag == 1) {  //There're no more items in address list
//
//                            //Return with no data and disable All layouts except 'Add Address' button in appointmentActivity
//                            Intent returnIntent = new Intent();
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("appointment_disable_flag", 1);
//
//                            returnIntent.putExtras(bundle);
//                            setResult(Activity.RESULT_OK, returnIntent);
//                            finish();   //Finishing Activity
//
//                        }
//                    }
                }
            });


            getServerData(1); //Sending DATA_REQUEST_ID=1; //Get Address List

        }


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
                    Toast.makeText(AddressListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
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
            case 1: //Get Address List
                getAddressListData(dataRequestId);
                break;
            case 2: //Get AddressSecondStatus; Only AddressId List
                getAddressSecondStatus(dataRequestId);
                break;
            case 3:  //Get AddressSecondStatus; AddressId status
                getAddressSecondStatus(dataRequestId);
                break;
            case 4:  //Get AddressSecondStatus; AddressId status for BackButton pressed
                getAddressSecondStatus(dataRequestId);
                break;
            default:
                Log.i("AddressListActivity", "getServerData datarequestid: Out of range value ");
        }


    }


    private void footerButtonClick() {

        //Send back selected address only when there's at least one address available(When user will delete address,all address might be deleted
        if (mAddressList.getAddresses().size() > 0) {

            for (int i = 0; i < mAddressList.getAddresses().size(); i++) {

                if (mSelectedAddressId == mAddressList.getAddresses().get(i).getAddressId()) {
                    mSelectedAddressPosition = i;
                    break;
                }
            }

            mProgressBar.setVisibility(View.VISIBLE);

            //Set Background to Black with Opacity 50%
            mProgressBarContainer.setBackgroundResource(R.color.black);
            mProgressBarContainer.getBackground().setAlpha(50);

            //To disable user interaction with background views
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            //Get AddressSecondStatus; Only AddressId status
            // Then Send Back data to AppointmentActivity using method 'sendBackSelectedAddress'
            getServerData(3);

        } else {
            if (mAppointmentDisableFlag == 1) {  //There're no more items in address list

                //Return with no data and disable All layouts except 'Add Address' button in appointmentActivity
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("appointment_disable_flag", 1);

                returnIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();   //Finishing Activity

            }
        }

    }


    private void backButtonClick() {

        //Send back selected address only when there's at least one address available(When user will delete address,all address might be deleted
        if (mAddressList.getAddresses().size() > 0) {

            for (int i = 0; i < mAddressList.getAddresses().size(); i++) {

                if (mSelectedAddressId == mAddressList.getAddresses().get(i).getAddressId()) {
                    mSelectedAddressPosition = i;
                    break;
                }
            }

            mProgressBar.setVisibility(View.VISIBLE);

            //Set Background to Black with Opacity 50%
            mProgressBarContainer.setBackgroundResource(R.color.black);
            mProgressBarContainer.getBackground().setAlpha(50);

            //To disable user interaction with background views
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            //Get AddressSecondStatus; Only AddressId status
            // This is for Back Button Pressed when deleted address is the one displayed in Appointment
            getServerData(4);

        } else {
            if (mAppointmentDisableFlag == 1) {  //There're no more items in address list

                //Return with no data and disable All layouts except 'Add Address' button in appointmentActivity
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("appointment_disable_flag", 1);

                returnIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();   //Finishing Activity

            }
        }

    }

    private void getAddressSecondStatus(final int dataRequestId) {

        switch (dataRequestId) {

            case 2: //Get AddressSecondStatus; Only AddressId List
                mIndicator = 2;
                break;
            case 3: //Get AddressSecondStatus; AddressId status
                mIndicator = 3;
                break;
            case 4: //Get AddressSecondStatus; AddressId status for BackButton pressed
                mIndicator = 3;
                break;
            default:
                Log.i("AddressListActivity", "datarequestid: Out of range value ");
        }


        Controller.GetAddressSecondStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetAddressSecondStatus.class);

        Call<Status> call = retrofitSingleton.getAddressSecondStatus(
                "Bearer " + mCredential.getAccess_token()
                , sUserId
                , mSelectedAddressId
                , mDeletedAddressIdList
                , mIndicator
        );

        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if (response.isSuccessful()) {

                    mStatus = response.body();


                    if (mStatus.getStatus() == 12) {

                        switch (dataRequestId) {

                            case 2: //Get AddressSecondStatus; Only AddressId List
                                finish();
                                break;
                            case 3: //Get AddressSecondStatus; AddressId status
                                sendBackSelectedAddress(); //Status is successful; Send back selected address data
                                break;
                            case 4: //Get AddressSecondStatus; AddressId status; When Bacak Button pressed
                                finish(); //Status is successful; Finish the activity
                                break;
                            default:
                                Log.i("AddressListActivity", "datarequestid: Out of range value ");
                        }


                    }


                } else {
                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });

    }

    private void getAddressListData(int dataRequestId) {


        Controller.GetAddressListItems retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetAddressListItems.class);
        Call<AddressList> call = retrofitSingleton.getAddressListItems("Bearer " + mCredential.getAccess_token(), sUserId);
        call.enqueue(new Callback<AddressList>() {
            @Override
            public void onResponse(Call<AddressList> call, Response<AddressList> response) {
                if (response.isSuccessful()) {

                    mAddressList = response.body();

                    //status=12; success
                    if (mAddressList.getStatus() == 12) {

//                        mRelativeLayoutInnerContainer.setVisibility(View.VISIBLE);

                        initAddressList();

                    } else if (mAddressList.getStatus() == 10) {
                        mProgressBar.setVisibility(View.GONE);
                    }

                } else {
                    Log.d("onResponse", "getAddressListData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<AddressList> call, Throwable throwable) {
                Toast.makeText(AddressListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });


    }

    private void initAddressList() {


        mFastItemAdapterAddressList = new FastItemAdapter();
        mFastItemAdapterAddressList.withSelectable(true);

        //init the ClickListenerHelper which simplifies custom click listeners on views of the Adapter
        mClickListenerHelper = new ClickListenerHelper<>(mFastItemAdapterAddressList);


        try {
            mFastItemAdapterAddressList.add(mAddressList.getAddresses());
        } catch (Exception e) {
            Log.e("AddressListActivity", e.toString());
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        mRecyclerViewAddressList.setAdapter(mFastItemAdapterAddressList);

        mLayoutManagerAddressList = new LinearLayoutManager(this);
        mLayoutManagerAddressList.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewAddressList.setLayoutManager(mLayoutManagerAddressList);
        mRecyclerViewAddressList.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewAddressList.addItemDecoration(new SimpleDividerItemDecoration(this)); //Adding item divider

        mProgressBar.setVisibility(View.GONE);

        //Set default address at start of screen
        mFastItemAdapterAddressList.select(0);

        //Save default address at start of screen
        mSelectedAddressId = mAddressList.getAddresses().get(0).getAddressId();

        //Enable footer button click as data is properly loaded
        mRelativeLayoutFooterButton.setEnabled(true);

        //restore selections (this has to be done after the items were added
        mFastItemAdapterAddressList.withSavedInstanceState(mSavedInstanceState);


        mFastItemAdapterAddressList.withOnPreClickListener(new FastAdapter.OnClickListener<Address>() {
            @Override
            public boolean onClick(View v, IAdapter<Address> adapter, Address item, int position) {

                //Select current Radio button and Deselect rest
                if (!item.isSelected()) {
                    Set<Integer> selections = mFastItemAdapterAddressList.getSelections();
                    if (!selections.isEmpty()) {
                        int selectedPosition = selections.iterator().next();
                        mFastItemAdapterAddressList.deselect();
                        mFastItemAdapterAddressList.notifyItemChanged(selectedPosition);
                    }
                    mFastItemAdapterAddressList.select(position);

                    //Save currently selected address
                    mSelectedAddressId = item.getAddressId();
                }

                return true;
            }
        });


        //a custom OnCreateViewHolder listener class which is used to create the viewHolders
        //we define the listener for the deleteContainer  here for better performance
        //you can also define the listener within the items bindView method but performance is better if you do it like this
        mFastItemAdapterAddressList.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return mFastItemAdapterAddressList.getTypeInstance(viewType).getViewHolder(parent);
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder) {

                if (viewHolder instanceof Address.ViewHolder) {
                    //if we click on the (mLinearLayoutDeleteContainer)
                    mClickListenerHelper.listen(viewHolder, ((Address.ViewHolder) viewHolder).mLinearLayoutDeleteContainer, new ClickListenerHelper.OnClickListener<Address>() {
                        @Override
                        public void onClick(View v, int position, Address item) {

                            Toast.makeText(AddressListActivity.this, "Delete button clicked", Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'

//                            if (position != mAddressList.getAddresses().size()-1) {
                            if (mDefaultAddressId == item.getAddressId()) {
                                mDefaultAddressUpdateFlag = 1;  //Update Appointment Default address and change DateTimeslot accordingly
                            }
//                            }


                            //Select previous/next address if this is currently selected address as this is going to get deleted.
                            if (item.isSelected()) {
                                if (position == mAddressList.getAddresses().size() - 1) {

                                    if (position == 0) {
                                        mAppointmentDisableFlag = 1;  //To disable default displayed Address layout in appointmentactivity
                                        // as there're no address in address list
                                    } else {

                                        mNextAddressItem = (Address) mFastItemAdapterAddressList.getItem(position - 1); //Save previous item

                                        mFastItemAdapterAddressList.select(position - 1); //select previous address as this is last item
                                    }

                                } else {
                                    mNextAddressItem = (Address) mFastItemAdapterAddressList.getItem(position + 1); //Save next item

                                    mFastItemAdapterAddressList.select(position + 1); //Select next address as this is not last item
                                }
                            }

                            //Get next/previous addressId when there're more than 1 item in AddressList
                            if (mAppointmentDisableFlag != 1 && mNextAddressItem != null) {
                                //Save newly selected address after delete
                                mSelectedAddressId = mNextAddressItem.getAddressId();
                            }

                            //Remove currently selected item from mAddressList
                            for (int i = 0; i < mAddressList.getAddresses().size(); i++) {

                                if (mAddressList.getAddresses().get(i).getAddressId() == item.getAddressId()) {
                                    mAddressList.getAddresses().remove(i);

                                }
                            }

                            //Remove currently selected item from FastItemAdapter
                            mFastItemAdapterAddressList.remove(position);

                            //Creating Deleted AddressId List
                            if (mDeletedAddressIdList.isEmpty()) {
                                mDeletedAddressIdList = Integer.toString(item.getAddressId());
                            } else {
                                mDeletedAddressIdList = mDeletedAddressIdList + "_" + Integer.toString(item.getAddressId());
                            }

                        }
                    });
                }

                return viewHolder;
            }
        });


    }


    private void addAddressClickListener() {

        mLinearLayoutAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mRelativeLayoutAddressDateTimeContainer.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                //Start AddAddressActivity and Get New Address
                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
                startActivityForResult(intent, ADDRESS_LIST_INTENT_IDENTIFIER_1);
            }
        });


    }

    @Override
    public void onBackPressed() {

        //Send back new default address data to Appointment activity when default address displayed in Appointment activity deleted here
        // And 'Back Button' pressed
        if (mDefaultAddressUpdateFlag == 1) {
            backButtonClick();
            if (mAddressList.getAddresses().size() > 0) {
                sendBackSelectedAddress();
            }
            mDefaultAddressUpdateFlag = 0;

        }


        super.onBackPressed();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        try {
            if (!mDeletedAddressIdList.isEmpty()) {
                getServerData(2);   //If DeletedAddressIdList is not empty. Then get server status and close the activity;
                // This call will redirect to onBackPress after successful completion.
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);

        }
        super.onStop();
    }

    private void sendBackSelectedAddress() {

        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("full_name", mAddressList.getAddresses().get(mSelectedAddressPosition).getFullName());
        bundle.putString("address", mAddressList.getAddresses().get(mSelectedAddressPosition).getAddress());
        bundle.putString("area", mAddressList.getAddresses().get(mSelectedAddressPosition).getArea());
        bundle.putString("landmark", mAddressList.getAddresses().get(mSelectedAddressPosition).getLandmark());
        bundle.putString("city", mAddressList.getAddresses().get(mSelectedAddressPosition).getCity());
        bundle.putString("state", mAddressList.getAddresses().get(mSelectedAddressPosition).getState());
        bundle.putString("pincode", mAddressList.getAddresses().get(mSelectedAddressPosition).getPincode());
        bundle.putString("phone", mAddressList.getAddresses().get(mSelectedAddressPosition).getPhone());

        mProgressBar.setVisibility(View.GONE);

        //Remove Background
        mProgressBarContainer.setBackgroundResource(0);

        //To enable user interaction with background views; This was disable earlier for ProgressBar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();   //Finishing Activity as user presses 'Done' button. Send back Address Data.
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Intent Identifier; For starting AddAddressActivity and Get New Address
        if (requestCode == ADDRESS_LIST_INTENT_IDENTIFIER_1) {
            if (resultCode == Activity.RESULT_OK) {

                //Return with AddAddressActivityData with same return Intent
                setResult(Activity.RESULT_OK, data);
                finish();   //Finishing Activity

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Info", "RESULT_CANCELED code was not expected");
            }

        }


    }

}