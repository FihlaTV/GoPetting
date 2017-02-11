package com.gopetting.android.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gopetting.android.R;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.CartItem;
import com.gopetting.android.models.CartScreen;
import com.gopetting.android.models.CartScreenItem;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.ConnectivityReceiver;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: Cart is getting data from ServiceActivity only, not from Server. So will have to remove unused code.

public class CartActivity extends AppCompatActivity {


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
    @BindView(R.id.ll_main_cart_container)
    LinearLayout mLinearLayoutCartContainer;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButtonContainer;


    private static final String OTHER_ACTIVITY_FLAG = "other_activity_flag";  //Used for starting login activity
    private static final int IDENTIFIER = 101; //101 value to Identify ServiceActivity
    private static final int CART_IDENTIFIER_2 = 102 ; //102 value to Identify AppointmentActivity
    private static String sUserId;

    private Credential mCredential;
    private SessionManager mSessionManager;
    private CartScreen mCartScreen;
    private FastItemAdapter mFastAdapterCart;
    private LinearLayoutManager mLayoutManagerCart;
    private Bundle mSavedInstanceState;
    private ClickListenerHelper mClickListenerHelper;
    private int mServerRequestId = 10;  //Default Value
    private Status mStatus;
    private Cart mCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        //Saving this so that it could be used in fastadapter.withSavedInstanceState
        mSavedInstanceState = savedInstanceState; //Check if this could cause any issue

        //Get Cart Data
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
//            mCartItems = (List<CartItem>) Parcels.unwrap(intent.getParcelableExtra("cart_items"));
            mCart = (Cart) Parcels.unwrap(intent.getParcelableExtra("cart"));
        }

        //Intialize cart screen model(fastadapter) using 'cart' bundle received from previous activity; This will be used to display cart items
        initCartScreenObject();
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Cart");
        
        mSessionManager = new SessionManager(getApplicationContext());

        if (!mSessionManager.isLoggedIn()){
            //Ask user to login; Cart services are only for logged in users as of now

            if (ConnectivityReceiver.isConnected()) {
                setupLogin();
            }else {
                showSnack();
            }
        }else {

            sUserId =mSessionManager.getUserId();       //Extract unique UserId
//            getServerData(1);   //Sending DATA_REQUEST_ID=1; Get Cart Screen Data

            if (sUserId != null) {
                initCartData();
            }else {
                Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_userid_empty, Snackbar.LENGTH_LONG).show();
            }
            
        }


        


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
            cartScreenItem.setServicePackageType(cartItem.getServicePackageType());

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



        mRelativeLayoutFooterButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ConnectivityReceiver.isConnected()) {

                    updateCartObject();

                    Intent intent = new Intent(CartActivity.this, AppointmentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cart", Parcels.wrap(mCart));
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //Clear stack; for exiting activity
                    startActivityForResult(intent, CART_IDENTIFIER_2); //Identifier for starting Appointment activity
                }else {
                    showSnack();
                }


            }
        });


            mTextViewCategoryName.setText(mCartScreen.getServiceCategoryName());

            mFastAdapterCart = new FastItemAdapter();
            mFastAdapterCart.withSelectable(true);

            //init the ClickListenerHelper which simplifies custom click listeners on views of the Adapter
            mClickListenerHelper = new ClickListenerHelper<>(mFastAdapterCart);


            try {
                mFastAdapterCart.add(mCartScreen.getCartScreenItems());
            } catch (Exception e) {
//                Log.e("CartActivity", e.toString());
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
                                }

                            }
                        });
                    }

                    return viewHolder;
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
//                    Toast.makeText(CartActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
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
            case 1: //Get Cart Activity Data
                getCartScreenData(dataRequestId);
                break;
            case 2: //Get Cart Status
                getCartStatus(dataRequestId);
                break;
            default:
//                Log.i("CartActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void setupLogin() {

        //Request user to login; Cart services will be provided only for logged in users
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage(R.string.dialog_question_login)
                .setTitle(R.string.dialog_title_login)
                .setPositiveButton(R.string.dialog_button_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    try {
                        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                        Bundle b = new Bundle();
                        b.putInt(OTHER_ACTIVITY_FLAG, 10);    //OTHER_ACTIVITY_FLAG = 10; This means login activity is started by activity other than MainActivity;
                        intent.putExtras(b);
                        startActivityForResult(intent, IDENTIFIER);
                    }catch (Exception exception){
                        Crashlytics.logException(exception);
                    }

                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor((ContextCompat.getColor(CartActivity.this, R.color.colorPrimary)));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor((ContextCompat.getColor(CartActivity.this, R.color.colorPrimary)));
    }

    private void getCartScreenData(final int dataRequestId) {

        Controller.GetCartScreenItems retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetCartScreenItems.class);
        Call<CartScreen> call = retrofitSingleton.getCartScreenItems("Bearer " + mCredential.getAccess_token(),sUserId);
        call.enqueue(new Callback<CartScreen>() {
            @Override
            public void onResponse(Call<CartScreen> call, Response<CartScreen> response) {
                if (response.isSuccessful()) {

//                    mCartScreen = response.body();


                    //initialize cart with data
//                    initCart();

                } else {
//                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<CartScreen> call, Throwable t) {
//                Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
//                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
//                Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initCart() {

        if(mCartScreen.getCartScreenItems().size() <= 0){
            mLinearLayoutCartContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
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
//            Log.e("CartActivity", e.toString());
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
                                mLinearLayoutEmptyCart.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }

                return viewHolder;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IDENTIFIER) {
            if(resultCode == Activity.RESULT_OK){
//                Log.i("Info","RESULT_OK code was not expected");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                if(mSessionManager.isLoggedIn()) {
                    sUserId = mSessionManager.getUserId();       //Extract unique UserId
                    getServerData(1);   //Sending DATA_REQUEST_ID=1; //Get Cart Screen Data
                }

            }
        }

        if (requestCode == CART_IDENTIFIER_2) {
            if(resultCode == Activity.RESULT_OK){

                setResult(Activity.RESULT_OK,data);
                finish();   //Finishing Activity; Go back to ServiceActivity

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }

        }



    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        updateCartObject();
        Bundle bundle = new Bundle();
        bundle.putParcelable("cart", Parcels.wrap(mCart));
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();   //Finishing Activity as user press back button and want to update ServiceActivity Cart icon count if there's any change in that using onActivityResult

        super.onBackPressed();
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
            cartItem.setServicePackageType(cartScreenItem.getServicePackageType());

            cartItems.add(cartItem);
        }
        mCart.setStatus(mCartScreen.getStatus());
        mCart.setServiceCategoryName(mCartScreen.getServiceCategoryName());
        mCart.setCartItems(cartItems);
        
    }

    @Override
    public void onStop() {

        try{
            if ( mCartScreen.getCartScreenItems().size()>0) {
                mServerRequestId = 10;
                getServerData(2);   //Cart Status
            }else {
                mServerRequestId = 11;
                getServerData(2);   //Cart Status
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showSnack() {

        Snackbar.make(findViewById(R.id.ll_activity_container), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG).show();

    }


}
