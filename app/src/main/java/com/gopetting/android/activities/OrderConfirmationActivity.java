package com.gopetting.android.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.StringItem;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar_container)
    FrameLayout mProgressBarContainer;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButtonContainer;
    @BindView(R.id.btn_footer_text)
    Button mFooterButton;
    @BindView(R.id.iv_order_status)
    ImageView mImageViewOrderStatus;
    @BindView(R.id.tv_order_status)
    TextView mTextViewOrderStatus;
    @BindView(R.id.tv_order_status_id_name)
    TextView mTextViewOrderStatusIdName;
    @BindView(R.id.tv_order_status_id)
    TextView mTextViewOrderStatusId;
    @BindView(R.id.tv_order_status_message)
    TextView mTextViewOrderStatusMessage;
    @BindView(R.id.tv_scheduled_text)
    TextView mTextViewScheduledText;
    @BindView(R.id.tv_scheduled_slot)
    TextView mTextViewScheduledSlot;
    @BindView(R.id.ll_status_id)
    LinearLayout mLinearLayoutStatusId;
    @BindView(R.id.ll_failed_id)
    LinearLayout mLinearLayoutFailedId;
    @BindView(R.id.tv_order_failed_id_name)
    TextView mTextViewOrderFailedIdName;
    @BindView(R.id.tv_order_failed_id)
    TextView mTextViewOrderFailedId;


    private String mSelectedTimeslot;
    private String mSelectedDateslot;
    private String mOrderId;
    private String mTransactionId;
    private Credential mCredential;
    private List<StringItem> mPromotionalScreens;
    private ArrayList<String> mPromoImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mProgressBarContainer.setVisibility(View.VISIBLE);

        mPromoImages = new ArrayList<>();   //Initializing

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.getString("status").equalsIgnoreCase("success")) {

                mLinearLayoutFailedId.setVisibility(View.GONE);
                mLinearLayoutStatusId.setVisibility(View.VISIBLE);

                mOrderId = bundle.getString("order_id");
                mSelectedDateslot = bundle.getString("selected_dateslot");
                mSelectedTimeslot = bundle.getString("selected_timeslot");

                getSupportActionBar().setTitle("Order Confirmation");

                mImageViewOrderStatus.setImageResource(R.drawable.ic_order_confirmation);
                mTextViewOrderStatus.setText(R.string.order_confirmed);
                mTextViewOrderStatusIdName.setText(R.string.order_id);
                mTextViewOrderStatusId.setText(mOrderId);
                mTextViewOrderStatusMessage.setText(R.string.order_confirmation_message);
                mTextViewScheduledText.setText(R.string.scheduled_on);

                //Set Date & Time
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    date = format.parse(mSelectedDateslot);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("OrderSummaryActivity", e.toString());
                    Crashlytics.logException(e);
                }

                String formattedDate = (String) android.text.format.DateFormat.format("dd-MMM-yy", date);

                Resources res = getResources();
                String scheduledSlot = String.format(res.getString(R.string.scheduled_slot)
                        , formattedDate, mSelectedTimeslot);
                mTextViewScheduledSlot.setText(scheduledSlot);

                mFooterButton.setText(R.string.done_text);


                mProgressBarContainer.setVisibility(View.GONE);



            } else {

                mLinearLayoutStatusId.setVisibility(View.GONE);
                mLinearLayoutFailedId.setVisibility(View.VISIBLE);

                mTransactionId = bundle.getString("transaction_id");

                getSupportActionBar().setTitle("Transaction Failed");

                mImageViewOrderStatus.setImageResource(R.drawable.ic_order_failed);
                mTextViewOrderStatus.setText(R.string.transaction_failed);
                mTextViewOrderFailedIdName.setText(R.string.txn_id);
                mTextViewOrderFailedId.setText(mTransactionId);
                mTextViewOrderStatusMessage.setText(R.string.order_failed_message);
                mTextViewScheduledText.setVisibility(View.GONE);
                mTextViewScheduledSlot.setVisibility(View.GONE);
                mFooterButton.setText(R.string.done_text);

                mProgressBarContainer.setVisibility(View.GONE);
            }

        }


        mRelativeLayoutFooterButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressBarContainer.setVisibility(View.VISIBLE);
                getServerData();

            }


        });

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
                    Toast.makeText(OrderConfirmationActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        getPromotionalGalleryData();
                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            getPromotionalGalleryData();
        }
    }


    private void getPromotionalGalleryData() {

        Controller.GetPromotionalScreens retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetPromotionalScreens.class);
        Call<List<StringItem>> call = retrofitSingleton.getPromotionalScreens("Bearer " + mCredential.getAccess_token());
        call.enqueue(new Callback<List<StringItem>>() {
            @Override
            public void onResponse(Call<List<StringItem>> call, Response<List<StringItem>> response) {
                if (response.isSuccessful()) {

                    mPromotionalScreens =response.body();

                    mPromoImages.add(mPromotionalScreens.get(0).getName());
                    mPromoImages.add(mPromotionalScreens.get(1).getName());
                    mPromoImages.add(mPromotionalScreens.get(2).getName());

                    Intent intent = new Intent(OrderConfirmationActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putStringArrayList("promo_images", mPromoImages);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //Clear stack; for exiting activity

                    mProgressBarContainer.setVisibility(View.GONE);

                    startActivity(intent);
                    OrderConfirmationActivity.this.finish();


                }

                else {
                    Log.d("onResponse", "getPromotionalGalleryData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<List<StringItem>> call, Throwable throwable) {
                Toast.makeText(OrderConfirmationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });



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
        //Back button disabled
    }


}
