package com.gopetting.android.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.OrderHistoryDetails;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sumit on 10/9/2016.
 */
public class OrderHistoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.order_toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.order_date_txt)
    protected TextView mTextViewDateslot;
    @BindView(R.id.order_time_txt)
    protected TextView mTextViewTimeslot;
    @BindView(R.id.order_id_txt)
    protected TextView mTextViewOrderId;
    @BindView(R.id.order_service_name_txt)
    protected TextView mTextViewServicePackageName;
    @BindView(R.id.total_amount_txt)
    protected TextView mTextViewTotalAmount;
    @BindView(R.id.order_id_lbl)
    protected TextView mTextViewOrderLbl;
    @BindView(R.id.order_status_txt)
    protected TextView mTextViewOrderStatus;
    @BindView(R.id.order_customer_address_line1)
    protected TextView mTextViewAddressLine1;
    @BindView(R.id.order_customer_address_line2)
    protected TextView mTextViewAddressLine2;
    @BindView(R.id.order_customer_address_line3)
    protected TextView mTextViewAddressLine3;
    @BindView(R.id.order_cancel_button)
    protected TextView mTextViewCancelButton;
    @BindView(R.id.order_detail_list)
    protected RecyclerView mRecyclerViewOrderDetailList;
    @BindView(R.id.tv_order_date)
    protected TextView mTextViewPlacedOnDate;
    @BindView(R.id.tv_pickup_drop)
    protected TextView mTextViewPSDFacility;


    @BindView(R.id.progress_bar_container)
    FrameLayout mFrameLayoutProgressBarContainer;
    @BindView(R.id.sv_outer)
    ScrollView mScrollViewOuter;


    public static String ORDER_ID = "order_id";
    public static String DATESLOT = "dateslot";
    public static String SERVICE_NAME = "service_name";
    public static String TIME_SLOT = "time_slot";
    public static String SERVICE_COUNT = "service_count";
    public static String STATUS = "status";


    protected FastItemAdapter fastItemAdapter;
    protected static String sUserId;
    protected SessionManager mSessionManager;

    protected String mId;
    protected String mDateslot;
    protected String mServiceCount;
    protected String mServiceName;
    protected String mTimeSlot;
    protected String mStatus;
    FrameLayout mProgressBarContainer;
    Date date;
    private Date mPlacedOnDate;
    private Credential mCredential;
    private OrderHistoryDetails mOrderHistoryDetails;
    private String mSelectedReason;
    private Status mButtonOrderStatus;
    private int mOrderStatusChanged = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ConnectivityReceiver.isConnected()) {

            showProgressBar(true);  //Show Progress Bar

            mSessionManager = new SessionManager(this);
            sUserId = mSessionManager.getUserId();

            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle = getIntent().getExtras();
                mId = bundle.getString(ORDER_ID);
                mDateslot = bundle.getString(DATESLOT);
                mServiceCount = bundle.getString(SERVICE_COUNT);
                mServiceName = bundle.getString(SERVICE_NAME);
                mTimeSlot = bundle.getString(TIME_SLOT);
                mStatus = bundle.getString(STATUS);
            }


            if (sUserId != null) {

                getServerData(1);   //Get OrderHistoryDetails

            }else {
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_userid_empty, Snackbar.LENGTH_SHORT).show();
            }

        }else {
            showSnack();
        }


        mTextViewCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ConnectivityReceiver.isConnected()) {
                    onButtonClick();
                }else {
                    showSnack();
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
                    Toast.makeText(OrderHistoryDetailActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
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
            case 1: //Get OrderHistoryDetails
                getOrderHistoryDetailsData();
                break;
            case 2:
                getButtonOrderStatus();
                break;

            default:
                Log.i("default case", "choose datarequestid: Out of range value ");
        }


    }

    private void getOrderHistoryDetailsData() {

        Controller.GetOrderHistoryDetails retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetOrderHistoryDetails.class);
        Call<OrderHistoryDetails> call = retrofitSingleton.getOrderHistoryDetails("Bearer " + mCredential.getAccess_token(), sUserId, mId);
        call.enqueue(new Callback<OrderHistoryDetails>() {
            @Override
            public void onResponse(Call<OrderHistoryDetails> call, Response<OrderHistoryDetails> response) {
                if (response.isSuccessful()) {

                    mOrderHistoryDetails = response.body();

                    if (mOrderHistoryDetails.getStatus() == 12){

                        initializeOrderHistoryDetail();

                    }


                }

                else {
                    Log.d("onResponse", "onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryDetails> call, Throwable throwable) {
                Toast.makeText(OrderHistoryDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });



    }

    protected void initializeOrderHistoryDetail() {

//        setViewVisibility();

        //Comment :: Extracting the day,month and year from recieved date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(mDateslot);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        StringBuilder dateString = new StringBuilder()
                .append("Scheduled On : ").append(cal.get(Calendar.DAY_OF_MONTH))
                .append(" ")
                .append(new SimpleDateFormat("MMM").format(cal.getTime()))
                .append(" ")
                .append(cal.get(Calendar.YEAR));

        mTextViewDateslot.setText(dateString);

        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            mPlacedOnDate = timestampFormat.parse(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getOrderDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarTimestamp = Calendar.getInstance();
        calendarTimestamp.setTime(mPlacedOnDate);
        StringBuilder string = new StringBuilder()
                .append("Placed On : ").append(calendarTimestamp.get(Calendar.DAY_OF_MONTH))
                .append(" ")
                .append(new SimpleDateFormat("MMM").format(calendarTimestamp.getTime()))
                .append(" ")
                .append(calendarTimestamp.get(Calendar.YEAR));

        mTextViewPlacedOnDate.setText(string);


        mTextViewTimeslot.setText(mTimeSlot);
        mTextViewOrderId.setText(mId);
        mTextViewServicePackageName.setText(mServiceName);
//        mTextViewItemsCount.setText("");
        mTextViewTotalAmount.setText("Rs." + mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).amountPaid);
        mTextViewOrderStatus.setText(mStatus);

        if (mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getPsdFacility() != 1) {
            mTextViewPSDFacility.setVisibility(View.GONE);
        }


        StringBuilder addressDetails = new StringBuilder();
        addressDetails = formatAddressString(addressDetails, mOrderHistoryDetails);


        mTextViewAddressLine1.setText(" " + mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getCustomerName());
        mTextViewAddressLine2.setText(addressDetails);
        mTextViewAddressLine3.setText(" " + mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getMobileNumber());

        if ((mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getAvailabilityFlag() == 1) || (!mStatus.equalsIgnoreCase("Scheduled")) ) {
            mTextViewCancelButton.setVisibility(View.GONE);
        }

        fastItemAdapter = new FastItemAdapter();
        fastItemAdapter.withSelectable(true);
//        mProgressBarContainer.setVisibility(View.VISIBLE);
        mRecyclerViewOrderDetailList.setAdapter(fastItemAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerViewOrderDetailList.setLayoutManager(gridLayoutManager);

        //Adding the Service Package name and price list
        fastItemAdapter.add(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getServicePackages());

        showProgressBar(false);
    }

    private StringBuilder formatAddressString(StringBuilder addressDetails, OrderHistoryDetails mOrderHistoryDetails) {
        return addressDetails
                .append(" ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getOrderAddress() + "\n ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getArea())
                .append(", ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getLandmark() + "\n ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getCity())
                .append(", ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getState())
                .append(", ")
                .append(mOrderHistoryDetails.getOrderHistoryDetailsItem().get(0).getPincode());
    }

    private void onButtonClick() {


        AlertDialog.Builder cancelDialogBuilder = new AlertDialog.Builder(this);
        cancelDialogBuilder.setMessage(R.string.dialog_question_cancel)
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        AlertDialog.Builder cancelReasonBuilder = new AlertDialog.Builder(OrderHistoryDetailActivity.this);
                        cancelReasonBuilder.setView(R.layout.dialog_order_cancel)
                                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Spinner spinner = (Spinner) ((AlertDialog) dialog).findViewById(R.id.cancel_dialog_spinner);
                                        mSelectedReason = spinner.getSelectedItem().toString();

                                        if(!mSelectedReason.equalsIgnoreCase("Choose")) {

                                            if (ConnectivityReceiver.isConnected()) {
                                                showProgressBar(true);
                                                getServerData(2);
                                            } else {
                                                showSnack();
                                            }
                                        }else {
                                            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_choose_reason, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog

                                    }
                                });
                        AlertDialog reasonDialog = cancelReasonBuilder.create();
                        reasonDialog.show();

                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        AlertDialog dialog = cancelDialogBuilder.create();
        dialog.show();
    }

    private void getButtonOrderStatus() {

        Controller.GetButtonOrderStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetButtonOrderStatus.class);
        Call<Status> call = retrofitSingleton.getButtonOrderStatus("Bearer " + mCredential.getAccess_token(), sUserId, mId, mSelectedReason);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if (response.isSuccessful()) {

                    mButtonOrderStatus = response.body();

                    if (mButtonOrderStatus.getStatus() == 12){

                        mTextViewOrderStatus.setText(R.string.text_cancelled);
                        mTextViewCancelButton.setVisibility(View.GONE);
                        mOrderStatusChanged = 1;
                        showProgressBar(false);
                        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_order_cancelled, Snackbar.LENGTH_SHORT).show();

                    }else if (mButtonOrderStatus.getStatus() == 103){
                        showProgressBar(false);
                        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_pg_refund_case, Snackbar.LENGTH_LONG).setDuration(5000).show();
                    }else {
                        showProgressBar(false);
                        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT).show();
                    }



                }

                else {
                    showProgressBar(false);
                    Log.d("onResponse", "onResponse:notSuccessful");
                    Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable throwable) {
                showProgressBar(false);
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_something_went_wrong, Snackbar.LENGTH_SHORT).show();
            }
        });



    }


    private void showProgressBar(Boolean flag){

        if(flag){

            //Show Progress Bar
            mScrollViewOuter.setVisibility(View.GONE);
            mFrameLayoutProgressBarContainer.setVisibility(View.VISIBLE);

        }else {

            //Hide Progress Bar
            mScrollViewOuter.setVisibility(View.VISIBLE);
            mFrameLayoutProgressBarContainer.setVisibility(View.GONE);


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


        if (mOrderStatusChanged == 1) {
            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();   //Finishing Activity as user press back button and need to refresh OrderHistoryActivity
        }else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);  //Finishing Activity as no changes done
            finish();
        }

        super.onBackPressed();

    }


    private void showSnack() {

        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();

    }

}


