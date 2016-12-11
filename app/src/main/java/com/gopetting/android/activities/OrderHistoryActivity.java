package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.OrderHistory;
import com.gopetting.android.models.OrderHistoryItem;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sumit on 11/25/2016.
 */
public class OrderHistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerViewOrderHistoryItem;
    @BindView(R.id.progress_bar_container)
    FrameLayout mFrameLayoutProgressBarContainer;
    @BindView(R.id.rl_outer_container)
    RelativeLayout mRelativeLayoutOuterContainer;
    @BindView(R.id.ll_empty_order)
    LinearLayout mLinearLayoutEmptyOrder;



    public static String ORDER_ID = "order_id";
    public static String DATESLOT = "dateslot";
    public static String SERVICE_NAME = "service_name";
    public static String TIME_SLOT = "time_slot";
    public static String SERVICE_COUNT = "service_count";
    public static String STATUS = "status";
    private static final int IDENTIFIER = 101;
    private static String sUserId;

    private SessionManager mSessionManager;
    private FastItemAdapter mFastItemAdapterOrderHistoryItem;
    private Credential mCredential;
    private OrderHistory mOrderHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (ConnectivityReceiver.isConnected()) {

            mSessionManager = new SessionManager(getApplicationContext());

            mRelativeLayoutOuterContainer.setVisibility(View.GONE);
            mFrameLayoutProgressBarContainer.setVisibility(View.VISIBLE);   //Show Progress Bar


            sUserId = mSessionManager.getUserId();       //Extract unique UserId

            if (sUserId != null) {

                getServerData();

            }else {
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_userid_empty, Snackbar.LENGTH_SHORT).show();
            }

        }else{
            showSnack();
        }


    }


    private void getServerData() {

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
                    Toast.makeText(OrderHistoryActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");
                        getOrderData();

                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            getOrderData();

        }
    }

    private void getOrderData() {

        Controller.GetOrderHistory retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetOrderHistory.class);
        Call<OrderHistory> call = retrofitSingleton.getOrderHistory("Bearer " + mCredential.getAccess_token(), sUserId);
        call.enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(Call<OrderHistory> call, Response<OrderHistory> response) {
                if (response.isSuccessful()) {

                    mOrderHistory = response.body();

                    if (mOrderHistory.getStatus() == 12 && (mOrderHistory.getOrderHistoryItems().size() > 0)){

                        initializeAdapter();

                    }else {

                        mRelativeLayoutOuterContainer.setVisibility(View.GONE);
                        mLinearLayoutEmptyOrder.setVisibility(View.VISIBLE);
                        mFrameLayoutProgressBarContainer.setVisibility(View.GONE);   //Hide Progress Bar
                    }


                }

                else {
                    Log.d("onResponse", "onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<OrderHistory> call, Throwable throwable) {
                Toast.makeText(OrderHistoryActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });



    }

    private void initializeAdapter() {

        mFastItemAdapterOrderHistoryItem = new FastItemAdapter();
        mFastItemAdapterOrderHistoryItem.withSelectable(true);

        mFastItemAdapterOrderHistoryItem.withOnClickListener(new FastAdapter.OnClickListener<OrderHistoryItem>() {
            @Override
            public boolean onClick(View v, IAdapter<OrderHistoryItem> adapter, OrderHistoryItem item, int position) {

                if (ConnectivityReceiver.isConnected()) {

                    Intent intent = new Intent(OrderHistoryActivity.this, OrderHistoryDetailActivity.class);
                    intent.putExtra(ORDER_ID, item.getOrderId());
                    intent.putExtra(DATESLOT, item.getDateslot());
                    intent.putExtra(SERVICE_NAME, item.getServiceName());
                    intent.putExtra(TIME_SLOT, item.getTimeSlot());
                    intent.putExtra(STATUS, item.getStatus());
                    startActivityForResult(intent, IDENTIFIER);
                }else {
                    showSnack();
                }

                return false;
            }
        });



        mFastItemAdapterOrderHistoryItem.add(mOrderHistory.getOrderHistoryItems());

        mRecyclerViewOrderHistoryItem.setAdapter(mFastItemAdapterOrderHistoryItem);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerViewOrderHistoryItem.setLayoutManager(gridLayoutManager);
        mRecyclerViewOrderHistoryItem.setItemAnimator(new DefaultItemAnimator());


        mRelativeLayoutOuterContainer.setVisibility(View.VISIBLE);
        mFrameLayoutProgressBarContainer.setVisibility(View.GONE);   //Show Progress Bar

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
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IDENTIFIER) {
            if (resultCode == Activity.RESULT_OK) {

                mRelativeLayoutOuterContainer.setVisibility(View.GONE);
                mFrameLayoutProgressBarContainer.setVisibility(View.VISIBLE);   //Show Progress Bar

                sUserId = mSessionManager.getUserId();       //Extract unique UserId

                if (ConnectivityReceiver.isConnected()) {
                    if (sUserId != null) {

                        getServerData();

                    }
                }else{
                    showSnack();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {

                //Do nothing

            }
        }
    }


    private void showSnack() {

        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();

    }


}

