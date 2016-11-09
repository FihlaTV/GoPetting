package com.gopetting.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.BreedType;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.CartItem;
import com.gopetting.android.models.CartScreen;
import com.gopetting.android.models.CartScreenItem;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.OrderSummary;
import com.gopetting.android.models.Promo;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import org.parceler.Parcels;

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

public class OrderSummaryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_category_name)
    TextView mTextViewCategoryName;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerViewCart;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.ll_empty_cart)
    LinearLayout mLinearLayoutEmptyCart;
    @BindView(R.id.sv_content_container)
    ScrollView mLinearLayoutCartContainer;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButtonContainer;
    @BindView(R.id.auto_complete_tv_breed_type)
    AutoCompleteTextView mAutoCompleteTextViewBreedType;
    @BindView(R.id.spinner_age_group)
    Spinner mSpinnerAgeGroup;
    @BindView(R.id.tv_apply_promo)
    TextView mTextViewApply;
    @BindView(R.id.cb_pickup_drop)
    CheckBox mCheckBoxPickupDrop;
    @BindView(R.id.tv_sub_total_amount)
    TextView mTextViewSubTotalAmount;
    @BindView(R.id.tv_pickup_drop_subtotal_amount)
    TextView mTextViewPickupDropSubTotalAmount;
    @BindView(R.id.tv_promo_code_subtotal_amount)
    TextView mTextViewPromoCodeSubTotalAmount;
    @BindView(R.id.tv_grand_total_amount)
    TextView mTextViewGrandTotalAmount;
    @BindView(R.id.edit_text_apply_promo)
    EditText mEditTextApplyPromo;
    @BindView(R.id.rl_apply_promo_code)
    RelativeLayout mRelativeLayoutApplyPromo;
    @BindView(R.id.rl_promo_code_applied)
    RelativeLayout mRelativeLayoutPromoApplied;
    @BindView(R.id.rl_promo_code_subtotal)
    RelativeLayout mRelativeLayoutPromoCodeSubTotal;
    @BindView(R.id.tv_remove)
    TextView mTextViewRemove;
    @BindView(R.id.rl_pickup_drop_subtotal)
    RelativeLayout mRelativeLayoutPickupDropSubTotal;
    @BindView(R.id.tv_date)
    TextView mTextViewDate;
    @BindView(R.id.tv_time)
    TextView mTextViewTime;
    @BindView(R.id.tv_full_name)
    TextView mTextViewFullName;
    @BindView(R.id.tv_full_address)
    TextView mTextViewFullAddress;
    @BindView(R.id.tv_phone)
    TextView mTextViewPhone;
    @BindView(R.id.rl_inner_container)
    RelativeLayout mRelativeLayoutInnerContainer;
    @BindView(R.id.tv_i_accept_terms_conditions)
    TextView mTextViewTermsConditions;

    private static String sUserId;

    private SessionManager mSessionManager;
    private Bundle mSavedInstanceState;
    private String mSelectedDateslot;
    private int mSelectedTimeslotId;
    private String mSelectedTimeslot;
    private String mSelectedFullName;
    private String mSelectedFullAddress;
    private String mSelectedPhone;
    private int mSelectedAddressId;
    private Cart mCart;
    private CartScreen mCartScreen;
    private FastItemAdapter mFastAdapterCart;
    private LinearLayoutManager mLayoutManagerCart;
    private ClickListenerHelper mClickListenerHelper;
    private Credential mCredential;
    private OrderSummary mOrderSummary;
    private String mPromoCode;
    private Promo mPromo;
    private int mSubTotal = 0; //Initialize with 0
    private int mPickupDropAmount = 0; //Initialize with 0
    private int mPromoAmount = 0; //Initialize with 0
    private int mGrandTotal = 0; //Initialize with 0
    private String mSelectedAgeGroup;
    private String mSelectedBreedType;
    private int mServerRequestId = 10;  //Default Value
    private Status mStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        ButterKnife.bind(this);

        //Saving this so that it could be used in fastadapter.withSavedInstanceState
        mSavedInstanceState = savedInstanceState; //Check if this could cause any issue

        mRelativeLayoutInnerContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        //Get Cart Data, Date, Time and Address Details
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            mCart = (Cart) Parcels.unwrap(intent.getParcelableExtra("cart"));

            mSelectedDateslot = bundle.getString("selected_date");
            mSelectedTimeslotId = bundle.getInt("selected_timeslot_id");
            mSelectedTimeslot = bundle.getString("selected_timeslot");
            mSelectedFullName = bundle.getString("selected_full_name");
            mSelectedAddressId = bundle.getInt("selected_address_id");
            mSelectedFullAddress = bundle.getString("selected_full_address");
            mSelectedPhone = bundle.getString("selected_phone");

        }

        //Intialize cart screen model(fastadapter) using 'cart' bundle received from previous activity; This will be used to display cart items
        initCartScreenObject();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSessionManager = new SessionManager(getApplicationContext());

        if (!mSessionManager.isLoggedIn()){
            //Ask user to login;
            Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_not_logged_in, Snackbar.LENGTH_LONG).show();
        }else {
            sUserId =mSessionManager.getUserId();       //Extract unique UserId
            getServerData(1);   //Sending DATA_REQUEST_ID=1;

            initCartData();


            initSummaryData();

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
                    Toast.makeText(OrderSummaryActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
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
            case 1: //Get Order Summary Data (Pickup drop charges and Breed Types)
                getOrderSummaryData(dataRequestId);
                break;
            case 2: //Get Promo Data
                getPromoStatus(dataRequestId);
                break;
            case 3: //Get Cart Status
                getCartStatus(dataRequestId);
                break;
            default:
                Log.i("OrderSummaryActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void getOrderSummaryData(int dataRequestId) {

            Controller.GetOrderSummary retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetOrderSummary.class);
            Call<OrderSummary> call = retrofitSingleton.getOrderSummary("Bearer " + mCredential.getAccess_token());
            call.enqueue(new Callback<OrderSummary>() {
                @Override
                public void onResponse(Call<OrderSummary> call, Response<OrderSummary> response) {
                    if (response.isSuccessful()) {

                        mOrderSummary = response.body();

                        loadServerData();

                    }

                    else {
                        Log.d("onResponse", "getOrderSummary :onResponse:notSuccessful");
                    }
                }

                @Override
                public void onFailure(Call<OrderSummary> call, Throwable throwable) {
                    Toast.makeText(OrderSummaryActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
                }
            });



    }

    private void getPromoStatus(final int dataRequestId) {

        List<Integer> cartServicePackageIds = new ArrayList<>();
        for (int i=0;i<5;i++){        //Assuming only 5 service packages can be allowed

            if(i>=mCartScreen.getCartScreenItems().size()) {        //To avoid ArrayOutofBoundException
                cartServicePackageIds.add(0);
            }else{
                cartServicePackageIds.add(mCartScreen.getCartScreenItems().get(i).getServicePackageId());
            }
        }


        Controller.GetPromoStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetPromoStatus.class);

        Call<Promo> call = retrofitSingleton.getPromoStatus(
                "Bearer " + mCredential.getAccess_token()
                ,mPromoCode
                ,cartServicePackageIds.get(0)
                ,cartServicePackageIds.get(1)
                ,cartServicePackageIds.get(2)
                ,cartServicePackageIds.get(3)
                ,cartServicePackageIds.get(4)
        );

        call.enqueue(new Callback<Promo>() {
            @Override
            public void onResponse(Call<Promo> call, Response<Promo> response) {
                if (response.isSuccessful()) {

                    mPromo = response.body();

                    mProgressBar.setVisibility(View.GONE);
                    //Remove Background
//                    mProgressBarContainer.setBackgroundResource(0);

                    //To enable user interaction with background views; This was disable earlier for ProgressBar
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if (mPromo.getStatus() == 10){

                        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_invalid_promo, Snackbar.LENGTH_LONG).show();

                    }else if (mPromo.getStatus() == 12){

                        mRelativeLayoutApplyPromo.setVisibility(View.GONE);
                        mRelativeLayoutPromoApplied.setVisibility(View.VISIBLE);
                        mRelativeLayoutPromoCodeSubTotal.setVisibility(View.VISIBLE);

                        //Set Promo Amount
                        mPromoAmount = mPromo.getAmount();

                        //Show Promo Code Amount
                        Resources res = getResources();
                        String promoCodeAmount = String.format(res.getString(R.string.promo_code_amount)
                                ,mPromoAmount);
                        mTextViewPromoCodeSubTotalAmount.setText(promoCodeAmount);

                        //Update Grand Total
                        mGrandTotal = (mSubTotal + mPickupDropAmount) - mPromoAmount;
                        String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                                ,mGrandTotal);
                        mTextViewGrandTotalAmount.setText(grandTotalAmount);

                    }




                } else {
                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Promo> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });

    }

    private void initSummaryData() {


        //promo code 'Apply Button'
        mTextViewApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               if( !mEditTextApplyPromo.getText().toString().trim().equals("")){

                   mProgressBar.setVisibility(View.VISIBLE);

                   //To disable user interaction with background views
                   getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                           WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//
//                   //Set Background to Black with Opacity 50%
//                   mProgressBarContainer.setBackgroundResource(R.color.black);
//                   mProgressBarContainer.getBackground().setAlpha(30);


                   mPromoCode = mEditTextApplyPromo.getText().toString();
                   getServerData(2);
               }

            }
        });


        // 'Remove Button' of Promo Code Applied Layout
        mTextViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRelativeLayoutApplyPromo.setVisibility(View.VISIBLE);
                mRelativeLayoutPromoApplied.setVisibility(View.GONE);
                mRelativeLayoutPromoCodeSubTotal.setVisibility(View.GONE);

                mPromoAmount = 0; //Reset Amount

                //Update Grand Total
                Resources res = getResources();
                mGrandTotal = mSubTotal + mPickupDropAmount;
                String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                        ,mGrandTotal);
                mTextViewGrandTotalAmount.setText(grandTotalAmount);


            }
        });


        //CheckBox Pickup-Service-Drop
        mCheckBoxPickupDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked())
                {
                    //Checkbox is checked
                    mPickupDropAmount = mOrderSummary.getPickupDropCharges();

                    mRelativeLayoutPickupDropSubTotal.setVisibility(View.VISIBLE);
                    Resources res = getResources();
                    String pickupDropSubTotalAmount = String.format(res.getString(R.string.pickup_drop_charges)
                            ,mPickupDropAmount);
                    mTextViewPickupDropSubTotalAmount.setText(pickupDropSubTotalAmount);

                    //Update Grand Total
                    mGrandTotal = (mSubTotal + mPickupDropAmount) - mPromoAmount;
                    String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                            ,mGrandTotal);
                    mTextViewGrandTotalAmount.setText(grandTotalAmount);

                }else {

                    //Checkbox is unchecked
                    mRelativeLayoutPickupDropSubTotal.setVisibility(View.GONE);

                    mPickupDropAmount = 0; //Reset amount

                    //Update Grand Total
                    mGrandTotal = mSubTotal - mPromoAmount;

                    Resources res = getResources();
                    String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                            ,mGrandTotal);
                    mTextViewGrandTotalAmount.setText(grandTotalAmount);

                }

            }
        });


        //Update mSubTotal at start of screen
        for (CartScreenItem cartScreenItem :mCartScreen.getCartScreenItems()) {
            mSubTotal = mSubTotal + cartScreenItem.getPrice();
        }


        //Set SubTotal TextView
        Resources res = getResources();
        String subTotalAmount = String.format(res.getString(R.string.sub_total_amount)
                ,mSubTotal);
        mTextViewSubTotalAmount.setText(subTotalAmount);

        //Set Grand Total TextView
        mGrandTotal = mSubTotal;
        String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                ,mGrandTotal);
        mTextViewGrandTotalAmount.setText(grandTotalAmount);


        //setup age group spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.string_array_age_group, R.layout.spinner_textview);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_textview);
        mSpinnerAgeGroup.setAdapter(spinnerAdapter);

        mSpinnerAgeGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedAgeGroup = (String) adapterView.getItemAtPosition(i);
//                Toast.makeText(OrderSummaryActivity.this, mSelectedAgeGroup,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(OrderSummaryActivity.this, "Spinner:Nothing Selected",Toast.LENGTH_SHORT).show();
            }
        });

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

        String formattedDate = (String) android.text.format.DateFormat.format("dd-MMM-yy", date); //MAY

        mTextViewDate.setText(formattedDate);

        String timeslot = String.format(res.getString(R.string.timeslot)
                ,mSelectedTimeslot);
        mTextViewTime.setText(timeslot);


        //Set Address Details
        mTextViewFullName.setText(mSelectedFullName);
        mTextViewFullAddress.setText(mSelectedFullAddress);
        mTextViewPhone.setText(mSelectedPhone);



        //Terms & Conditions hyperlink click listener
//        Spanned underlinedText;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            underlinedText = Html.fromHtml("Terms & Conditions",Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            underlinedText = Html.fromHtml("Terms & Conditions");
//        }

//        String underlinedText = "Terms & Conditions";
//        SpannableString content = new SpannableString(underlinedText);
//        content.setSpan(new UnderlineSpan(), 0, underlinedText.length(), 0);
//        String termsConditionsText = String.format(res.getString(R.string.terms_conditions_underlined),content);
//        mTextViewTermsConditions.setText("content);

        mTextViewTermsConditions.setPaintFlags(mTextViewTermsConditions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //Underline text

        mTextViewTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Start Terms & Conditions activity

            }
        });


    }

    private void initCartScreenObject() {

        List<CartScreenItem> cartScreenItems = new ArrayList<>();

        for (CartItem cartItem:mCart.mCartItems
                ) {
            CartScreenItem cartScreenItem = new CartScreenItem();
            cartScreenItem.setServicePackageId(cartItem.getServicePackageId());
            cartScreenItem.setServicePackageName(cartItem.getServicePackageName());
            cartScreenItem.setPrice(cartItem.getPrice());
            cartScreenItem.setServiceSubCategoryId(cartItem.getServiceSubCategoryId());

            cartScreenItems.add(cartScreenItem);
        }

        mCartScreen = new CartScreen();

        mCartScreen.setStatus(mCart.getStatus());
        mCartScreen.setServiceCategoryName(mCart.getServiceCategoryName());
        mCartScreen.setCartScreenItems(cartScreenItems);

    }

    private void initCartData() {

        //check whether cart is empty; If yes, then show empty cart
        if(mCartScreen.getCartScreenItems().size() <= 0){
            mLinearLayoutCartContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mRelativeLayoutFooterButtonContainer.setVisibility(View.GONE);
            mLinearLayoutEmptyCart.setVisibility(View.VISIBLE);
        }

        mTextViewCategoryName.setText(mCartScreen.getServiceCategoryName());

        mFastAdapterCart = new FastItemAdapter();
        mFastAdapterCart.withSelectable(true);

        //init the ClickListenerHelper which simplifies custom click listeners on views of the Adapter
        mClickListenerHelper = new ClickListenerHelper<>(mFastAdapterCart);


        try {
            mFastAdapterCart.add(mCartScreen.getCartScreenItems());
        } catch (Exception e) {
            Log.e("CartActivity", e.toString());
        }

        mRecyclerViewCart.setAdapter(mFastAdapterCart);

        mLayoutManagerCart = new LinearLayoutManager(this);
        mLayoutManagerCart.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewCart.setLayoutManager(mLayoutManagerCart);
        mRecyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewCart.addItemDecoration(new SimpleDividerItemDecoration(this)); //Adding item divider

        mProgressBar.setVisibility(View.GONE);

        //restore selections (this has to be done after the items were added
        mFastAdapterCart.withSavedInstanceState(mSavedInstanceState);

        //a custom OnCreateViewHolder listener class which is used to create the viewHolders
        //we define the listener for the imageLovedContainer here for better performance
        //you can also define the listener within the items bindView method but performance is better if you do it like this
        mFastAdapterCart.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return mFastAdapterCart.getTypeInstance(viewType).getViewHolder(parent);
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder) {
                //we do this for our ServicePackage.ViewHolder
                if (viewHolder instanceof CartScreenItem.ViewHolder) {
                    //if we click on the  (mItemBasketContainer)
                    mClickListenerHelper.listen(viewHolder, ((CartScreenItem.ViewHolder) viewHolder).mRelativeLayoutDeleteContainer, new ClickListenerHelper.OnClickListener<CartScreenItem>() {
                        @Override
                        public void onClick(View v, int position, CartScreenItem item) {
                            for (int i=0;i<mCartScreen.getCartScreenItems().size();i++){
                                if (mCartScreen.getCartScreenItems().get(i).getServicePackageId() == item.getServicePackageId()){
                                    mCartScreen.getCartScreenItems().remove(i);
                                }
                            }

                            mFastAdapterCart.remove(position);

                            //check whether cart is empty; If yes, then show empty cart
                            if(mCartScreen.getCartScreenItems().size() <= 0){
                                mLinearLayoutCartContainer.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                                mRelativeLayoutFooterButtonContainer.setVisibility(View.GONE);
                                mLinearLayoutEmptyCart.setVisibility(View.VISIBLE);
                            }else { //


                                mSubTotal = 0; //Reset Subtotal
                                //Update Subtotal and GrandTotal views when item is deleted from cart
                                for (CartScreenItem cartScreenItem :mCartScreen.getCartScreenItems()) {
                                    mSubTotal = mSubTotal + cartScreenItem.getPrice();
                                }

                                //Set SubTotal TextView
                                Resources res = getResources();
                                String subTotalAmount = String.format(res.getString(R.string.sub_total_amount)
                                        ,mSubTotal);
                                mTextViewSubTotalAmount.setText(subTotalAmount);

                                //Set Grand Total TextView
                                mGrandTotal = (mSubTotal + mPickupDropAmount) - mPromoAmount;
                                String grandTotalAmount = String.format(res.getString(R.string.grand_total_amount)
                                        ,mGrandTotal);
                                mTextViewGrandTotalAmount.setText(grandTotalAmount);


                            }



                        }
                    });
                }

                return viewHolder;
            }
        });



    }

    private void loadServerData() {

        //Setup Breed Type AutoCompleteTextView
        ArrayAdapter<BreedType> breedTypeArrayAdapter = new ArrayAdapter<>(this, R.layout.item_autocomplete_textview,mOrderSummary.getBreedTypes());
        mAutoCompleteTextViewBreedType.setAdapter(breedTypeArrayAdapter);

        mAutoCompleteTextViewBreedType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                BreedType selectedItem = (BreedType) parent.getAdapter().getItem(position);
//                Toast.makeText(OrderSummaryActivity.this,
//                        "Clicked " + position + " name: " + selectedItem.toString(),
//                        Toast.LENGTH_SHORT).show();
                mSelectedBreedType = selectedItem.toString();
            }
        });


        mProgressBar.setVisibility(View.GONE);
        mRelativeLayoutInnerContainer.setVisibility(View.VISIBLE);


    }

    private void getCartStatus(final int dataRequestId) {

        List<Integer> cartServicePackageIds = new ArrayList<>();
        for (int i=0;i<5;i++){        //Assuming only service packages can be allowed

            if(i>=mCartScreen.getCartScreenItems().size()) {        //To avoid ArrayOutofBoundException
                cartServicePackageIds.add(0);
            }else{
                cartServicePackageIds.add(mCartScreen.getCartScreenItems().get(i).getServicePackageId());
            }
        }


        Controller.GetCartStatus retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetCartStatus.class);

        Call<Status> call = retrofitSingleton.getCartStatus(
                "Bearer " + mCredential.getAccess_token()
                ,sUserId
                ,cartServicePackageIds.get(0)
                ,cartServicePackageIds.get(1)
                ,cartServicePackageIds.get(2)
                ,cartServicePackageIds.get(3)
                ,cartServicePackageIds.get(4)
                ,mServerRequestId
        );

        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if (response.isSuccessful()) {

                    mStatus = response.body();

                } else {
                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapterCart.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
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

    private void updateCartObject() {

        List<CartItem> cartItems = new ArrayList<>();

        for (CartScreenItem cartScreenItem:mCartScreen.getCartScreenItems()
                ) {
            CartItem cartItem = new CartItem();
            cartItem.setServicePackageId(cartScreenItem.getServicePackageId());
            cartItem.setServicePackageName(cartScreenItem.getServicePackageName());
            cartItem.setPrice(cartScreenItem.getPrice());
            cartItem.setServiceSubCategoryId(cartScreenItem.getServiceSubCategoryId());

            cartItems.add(cartItem);
        }
        mCart.setStatus(mCartScreen.getStatus());
        mCart.setServiceCategoryName(mCartScreen.getServiceCategoryName());
        mCart.setCartItems(cartItems);

    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        updateCartObject();
        Bundle bundle = new Bundle();
        bundle.putParcelable("cart", Parcels.wrap(mCart));
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();   //Finishing Activity as user press back button and want to update mCart object so that Cart items remain same after delete also.

        super.onBackPressed();
    }

    @Override
    public void onStop() {

        try{
            if ( mCartScreen.getCartScreenItems().size()>0) {
                mServerRequestId = 10;
                getServerData(3);   //Cart Status
            }else {
                mServerRequestId = 11;
                getServerData(3);   //Cart Status
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
