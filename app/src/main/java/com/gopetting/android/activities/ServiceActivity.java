package com.gopetting.android.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gopetting.android.R;
import com.gopetting.android.adapters.ViewPagerAdapter;
import com.gopetting.android.fragments.ServiceFragment;
import com.gopetting.android.models.Cart;
import com.gopetting.android.models.CartItem;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.models.Status;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.mikepenz.fastadapter.IItem;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceActivity extends AppCompatActivity implements ServiceFragment.ServiceFragmentListener {

    @BindView(R.id.toolbar_headerbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager_service)
    ViewPager mViewPagerService;
    @BindView(R.id.tab_layout_service)
    TabLayout mTabLayoutService;
    @BindView(R.id.rl_footer_button_container)
    RelativeLayout mRelativeLayoutFooterButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBarMedium;

    private static final String OTHER_ACTIVITY_FLAG = "other_activity_flag";  //Used for starting login activity
    private static final int SERVICE_IDENTIFIER_1 = 201 ; ////Intent Identifier; for Starting CartActivity
    private static final int SERVICE_IDENTIFIER_2 = 202; //Intent Identifier, when Basket icon clicked and user not logged in
    private static final int SERVICE_IDENTIFIER_3 = 203; //Intent Identifier; Cart icon clicked and user not logged in
    private static final int SERVICE_IDENTIFIER_4 = 204; //Intent Identifier; for starting Appointment Activity
    private static final int SERVICE_CATEGORY_ID = 11;  //Pet Salon
    private static String sUserId;


    private Credential mCredential;
    private ViewPagerAdapter mViewPagerAdapter;
    private ServiceCategory mServiceCategoryData;
    //interface through which communication is made to fragment
    public Communicator.FragmentCommunicator mFragmentCommunicator;
    private SessionManager mSessionManager;
    private Cart mCart;
    private TextView mTextView;
    private String mNotifyCount = "";
    private int mSelectedServicePackageId;
    private String mSelectedServicePackageName;
    private int mSelectedPrice;
    private int mSelectedServiceSubCategoryId;
    private String mSelectedServiceSubCategoryName;
    private Status mStatus;
    private int mServerRequestId = 10; //Default value



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);

        //Disable footer button click until data is loaded into mCart Object
        mRelativeLayoutFooterButton.setEnabled(false);

        mSessionManager = new SessionManager(getApplicationContext());

        if (!mSessionManager.isLoggedIn()){
            getServerData(2);   //Sending DATA_REQUEST_ID=2; Only ServiceCategoryData
        }else {
            sUserId =mSessionManager.getUserId();       //Extract unique UserId
            getServerData(1);   //Sending DATA_REQUEST_ID=1; Whole Data includes ServiceSategoryData & CartItems
        }


        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  //Initializing it here, it's a good practice to do it to avoid NPE

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pet Salon");

        mRelativeLayoutFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSessionManager.isLoggedIn() && mCart.mCartItems.size()>0){

                    Intent intent = new Intent(ServiceActivity.this, AppointmentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cart", Parcels.wrap(mCart));
                    intent.putExtras(bundle);
                    startActivityForResult(intent,SERVICE_IDENTIFIER_4);

                }else {
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.snackbar_services, Snackbar.LENGTH_LONG).show();
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
                    Toast.makeText(ServiceActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
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
            case 1: //Get Whole Data
                getServiceCategoryData(dataRequestId);
                getCartItemsDataV2(dataRequestId);
                break;
            case 2: //Get only ServiceCategoryData
                getServiceCategoryData(dataRequestId);
                break;
            case 3: //Get only Cart Data
                getCartItemsDataV2(dataRequestId);
                break;
            case 4: //Cart Status
                getCartStatus(dataRequestId);
                break;
            case 5: //5=dataRequestId; Cart Icon Clicked, User was not logged in; Asked to login, now logged in; Get Cart Data; Start CartActivity
                getCartItemsDataV2(dataRequestId);
                break;
            default:
                Log.i("ServiceActivity", "getServerData datarequestid: Out of range value ");
        }


    }

    private void getCartStatus(final int dataRequestId) {

        List<Integer> cartServicePackageIds = new ArrayList<>();
        for (int i=0;i<5;i++){        //Assuming only service packages can be allowed

            if(i>=mCart.mCartItems.size()) {        //To avoid ArrayOutofBoundException
                cartServicePackageIds.add(0);
            }else{
                cartServicePackageIds.add(mCart.mCartItems.get(i).getServicePackageId());
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
                Toast.makeText(ServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });


    }

    private void getServiceCategoryData(int dataRequestId){

        Controller.GetServiceCategory retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetServiceCategory.class);
        Call<ServiceCategory> call = retrofitSingleton.getServiceCategory("Bearer " + mCredential.getAccess_token(),SERVICE_CATEGORY_ID);
        call.enqueue(new Callback<ServiceCategory>() {
            @Override
            public void onResponse(Call<ServiceCategory> call, Response<ServiceCategory> response) {
                if (response.isSuccessful()) {

                    mServiceCategoryData = response.body();
                    initServiceData();

                    //Enable footer button click, since if no items are added into cart and user tries to click button; It should work and give snackbar message "Add items to cart"
                    mRelativeLayoutFooterButton.setEnabled(true);


                } else {
                    Log.d("onResponse", "getServiceCategoryData :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<ServiceCategory> call, Throwable t) {
                Toast.makeText(ServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });
    }

    //backup
    private void getCartItemsDataV2(final int dataRequestId) {

        Controller.GetCartItemsV2 retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetCartItemsV2.class);
        Call<Cart> call = retrofitSingleton.getCartItemsV2("Bearer " + mCredential.getAccess_token(),sUserId);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {

                    mCart = response.body();

                    //Enable footer button click since data is loaded into mCart Object
                    mRelativeLayoutFooterButton.setEnabled(true);

//                    Toast.makeText(ServiceActivity.this,Integer.toString(mCart.mCartItems.size()), Toast.LENGTH_SHORT).show();


                    switch (dataRequestId) {
                        case 1: //User Already logged in; User started ServiceActivity, so Just update Cart Items count
                            mNotifyCount = Integer.toString(mCart.mCartItems.size());
                            invalidateOptionsMenu();
                            break;
                        case 3: //Add selected service package to cart
                            addItemToCart();
                            break;
                        case 5: //5=dataRequestId; Cart Icon Clicked, User was not logged in; Asked to login, now logged in; Get Cart Data; Start CartActivity
                            startCartActivity(SERVICE_IDENTIFIER_3); //Sending SERVICE_IDENTIFIER_3 just as a parameter value; As user is already logged in, CartActivity will be started.
                            break;
                        default:
                            Log.i("ServiceActivity", "datarequestid: Out of range value ");
                    }


                } else {
                    Log.d("onResponse", "getCartItems :onResponse:notSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(ServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });


    }
    
    
    private void addItemToCart() {

        int flag = 1; //Just a default value to check whether any package is already available in cart for selected service subcategory

        //Check whether cart is empty for user
        if (mCart.mCartItems.size()<=0){

                addAndRefreshCart();
                flag=2;

        }else {

            //Loop through all Cart items
            for (int i = 0; i < mCart.mCartItems.size(); i++) {
                //check if select service subcategory item are already available in cart
                if (mSelectedServiceSubCategoryId == mCart.mCartItems.get(i).getServiceSubCategoryId()) {
                    //Yes it's available, now check selected service package is already available in cart
                    if (mSelectedServicePackageId == mCart.mCartItems.get(i).getServicePackageId()) {
                        //Don't do anything as Service Package is already available in Cart and we don't allow more than 1 service package per service subcategory as of now.

                        Toast.makeText(ServiceActivity.this, "Package is already in cart",Toast.LENGTH_SHORT).show(); //Show to user, package is already in cart

                        flag = 2;
                        break;      //Break loop as service package is added/replaced

                    } else {
                        //Other service package is already available under this service sub category; Ask user to replace it with this package

                        flag =2;
//
//                        String dialogQuestion = getResources().getString(R.string.dialog_question_cart_item_change_part1 )
//                                                + mSelectedServiceSubCategoryName
//                                                + getResources().getString(R.string.dialog_question_cart_item_change_part2)
//                                                + getResources().getString(R.string.dialog_question_cart_item_change_part3);

                        mCart.mCartItems.remove(i); //Remove old service package

                        //add currently selected service package to Cart
                        mCart.mCartItems.add(new CartItem().setServicePackageId(mSelectedServicePackageId)
                                                            .setServicePackageName(mSelectedServicePackageName)
                                                            .setPrice(mSelectedPrice)
                                                            .setServiceSubCategoryId(mSelectedServiceSubCategoryId));

                        //Refresh Shopping Cart Icon Count; Logically Count is going to be same as we're removing 1 package and adding 1 package :)
                        mNotifyCount = Integer.toString(mCart.mCartItems.size());
                        invalidateOptionsMenu();

                        Toast.makeText(ServiceActivity.this, "Package is added to cart",Toast.LENGTH_SHORT).show(); //Show to user, package is added
/*
                        final int j = i;


                        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);

                        builder.setMessage(dialogQuestion)
//                            .setTitle(R.string.dialog_title_cart_item_change)
                                .setPositiveButton(R.string.dialog_button_cart_item_change, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        mCart.mCartItems.remove(j); //Remove old service package

                                        //add currently selected service package to Cart
                                        mCart.mCartItems.add(new CartItem().setServicePackageId(mSelectedServicePackageId).setServiceSubCategoryId(mSelectedServiceSubCategoryId));

                                        //Refresh Shopping Cart Icon Count; Logically Count is going to be same as we're removing 1 package and adding 1 package :)
                                        mNotifyCount = Integer.toString(mCart.mCartItems.size());
                                        invalidateOptionsMenu();

                                        Toast.makeText(ServiceActivity.this, "Package is added to cart",Toast.LENGTH_SHORT).show(); //Show to user, package is added

                                    }
                                })
                                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        //Set Dialog text size and font
                        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                        textView.setTextSize(14);
//                        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DIN-Regular.otf"));
*/
                        break; //Break loop as service package is added/replaced
                    }
                }
            }
        }

        if (flag == 1){

            addAndRefreshCart();

        }

    }

    //Add selected service package and refresh the cart
    private void addAndRefreshCart() {

        //add currently selected service package to Cart
        mCart.mCartItems.add(new CartItem().setServicePackageId(mSelectedServicePackageId)
                                            .setServicePackageName(mSelectedServicePackageName)
                                            .setPrice(mSelectedPrice)
                                            .setServiceSubCategoryId(mSelectedServiceSubCategoryId));


        //Refresh Shopping Cart Icon Count;
        mNotifyCount = Integer.toString(mCart.mCartItems.size());
        invalidateOptionsMenu();

        Toast.makeText(ServiceActivity.this, "Package is added to cart",Toast.LENGTH_SHORT).show(); //Show to user, package is added

    }

    private void initServiceData() {

//      Linking ServicePackageDetail with ServicePackage based on service package id
        for (int i = 0; i <mServiceCategoryData.mServicePackageDetails.size(); i++) {


            for (int j = 0; j < mServiceCategoryData.mServiceSubCategories.size(); j++) {


                for (int k = 0; k < mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().size(); k++) {


                    if (mServiceCategoryData.mServicePackageDetails.get(i).getServicePackageId()
                            == mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().get(k).getServicePackageId()) {

                        List<IItem> subItems = new LinkedList<>();
                        subItems.add(mServiceCategoryData.mServicePackageDetails.get(i));

                        //Adding subitem (there's only 1 subitem for each service package) to corresponding service package
                        mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().get(k).withSubItems(subItems);

                    }
                }
            }
        }




        //Set ServiceCategory to Storage class, for communicating to Fragment.
        ServiceCategoryData.setServiceCategoryData(mServiceCategoryData);


        for (int l = 0; l <mServiceCategoryData.mServiceSubCategories.size() ; l++) {

            //Send ServicePackages to ServiceFragment
//            mFragmentCommunicator.passDataToFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServicePackages());
//            EventBus.getDefault().post(new ActivityToFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServicePackages(),l));

            mViewPagerAdapter.addFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServiceSubcategoryName()
                                            ,ServiceFragment.newInstance(l));


        }

        mViewPagerService.setAdapter(mViewPagerAdapter);
        mViewPagerService.setOffscreenPageLimit(mViewPagerService.getAdapter().getCount());

        mTabLayoutService.setupWithViewPager(mViewPagerService);
        mTabLayoutService.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayoutService.setTabGravity(TabLayout.GRAVITY_CENTER);

        mProgressBarMedium.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mViewPagerAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onServiceFragmentClick(ServicePackage servicePackage, int serviceSubCategoryIndex){
        Log.i("logs", "onServiceFragmentClick: ");

        //Initializing fields for Selected Service Package
        mSelectedServicePackageId = servicePackage.getServicePackageId();
        mSelectedServicePackageName = servicePackage.getServicePackageName();
        mSelectedPrice = servicePackage.getPrice();
        mSelectedServiceSubCategoryId = mServiceCategoryData.mServiceSubCategories.get(serviceSubCategoryIndex).getServiceSubCategoryId();
        mSelectedServiceSubCategoryName = mServiceCategoryData.mServiceSubCategories.get(serviceSubCategoryIndex).getServiceSubcategoryName();

        if (!mSessionManager.isLoggedIn()){

            setupLogin(SERVICE_IDENTIFIER_2);   //Intent Identifier, when Basket icon clicked and user not logged in

        }else {

            addItemToCart();
        }

    }

    private void setupLogin(final int intentIdentifier) {

        //Request user to login; Cart services will be provided only for logged in users
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
        builder.setMessage(R.string.dialog_question_login)
                .setTitle(R.string.dialog_title_login)
                .setPositiveButton(R.string.dialog_button_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(ServiceActivity.this, LoginActivity.class);
                        Bundle b = new Bundle();
                        b.putInt(OTHER_ACTIVITY_FLAG, 10);    //OTHER_ACTIVITY_FLAG = 10; This means login activity is started by activity other than MainActivity;
                        intent.putExtras(b);
                        startActivityForResult(intent, intentIdentifier);

                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Intent Identifier; for Starting CartActivity when user logged in
        if (requestCode == SERVICE_IDENTIFIER_1) {
            if (resultCode == Activity.RESULT_OK) {
                mCart = (Cart) Parcels.unwrap(data.getParcelableExtra("cart"));

                //Refresh Shopping Cart Icon Count;
                mNotifyCount = Integer.toString(mCart.mCartItems.size());
                invalidateOptionsMenu();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Info", "RESULT_CANCELED code was not expected");
            }

        }


        //Intent Identifier, when Basket icon clicked and user not logged in
        if (requestCode == SERVICE_IDENTIFIER_2) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("Info", "RESULT_OK code was not expected");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                if (mSessionManager.isLoggedIn()) {
                    sUserId = mSessionManager.getUserId();       //Extract unique UserId
                    getServerData(3);   //Sending DATA_REQUEST_ID=3; //Get only Cart Data, when Basket icon clicked
                }

            }
        }

        //Intent Identifier; Cart icon clicked and user not logged in
        if (requestCode == SERVICE_IDENTIFIER_3) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("Info", "RESULT_OK code was not expected");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                if (mSessionManager.isLoggedIn()) {
                    sUserId = mSessionManager.getUserId();   //Extract unique UserId
                    getServerData(5);
                }

            }
        }


        //Intent Identifier; for Starting AppointmentActivity
        //For saving latest cart when moving between activities like service,cart,appointment and ordersummary.
        if (requestCode == SERVICE_IDENTIFIER_4) {
            if (resultCode == Activity.RESULT_OK) {
                mCart = (Cart) Parcels.unwrap(data.getParcelableExtra("cart"));

                //Refresh Shopping Cart Icon Count;
                mNotifyCount = Integer.toString(mCart.mCartItems.size());
                invalidateOptionsMenu();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Info", "RESULT_CANCELED code was not expected");
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        MenuItem item = menu.findItem(R.id.shopping_cart);
        MenuItemCompat.setActionView(item, R.layout.menu_cart_layout);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        mTextView = (TextView) relativeLayout.findViewById(R.id.tv_cart_count);
        mTextView.setText(mNotifyCount);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCart!=null) {
                    startCartActivity(SERVICE_IDENTIFIER_3);    //Intent Identifier; This will be passed to 'setupLogin' method; Cart icon clicked and user not logged in
                }

            }
        });

        return super.onCreateOptionsMenu(menu);
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

    private void startCartActivity(int intentIdentifier) {

        if (!mSessionManager.isLoggedIn()){
            setupLogin(intentIdentifier);   //Ask user to login

        }else {
            Intent intent = new Intent(ServiceActivity.this, CartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("cart", Parcels.wrap(mCart));
            intent.putExtras(bundle);

            startActivityForResult(intent,SERVICE_IDENTIFIER_1);

        }

    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        try{
        if ( mCart.mCartItems.size()>0) {
            getServerData(4);   //Cart Status
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
}
