package com.gopetting.android.activities;

import android.content.DialogInterface;
import android.os.Bundle;
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
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.network.SessionManager;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.mikepenz.fastadapter.IItem;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

//    private List<ServicePackage> mServicePackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);

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
                getServiceCategoryData();
                getCartItemsData();
                break;
            case 2: //Get only ServiceCategoryData
                getServiceCategoryData();
                break;
            case 3: //Get only Cart Data
                getCartItemsData();
                break;
            default:
                Log.i("ServiceActivity", "getServerData datarequestid: Out of range value ");
        }


    }


    private void getServiceCategoryData(){

        Controller.GetServiceCategory retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetServiceCategory.class);
        Call<ServiceCategory> call = retrofitSingleton.getServiceCategory("Bearer " + mCredential.getAccess_token(),SERVICE_CATEGORY_ID);
        call.enqueue(new Callback<ServiceCategory>() {
            @Override
            public void onResponse(Call<ServiceCategory> call, Response<ServiceCategory> response) {
                if (response.isSuccessful()) {

                    mServiceCategoryData = response.body();
                    initServiceData();

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

    private void getCartItemsData() {

        Controller.GetCartItems retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.GetCartItems.class);
        Call<Cart> call = retrofitSingleton.getCartItems("Bearer " + mCredential.getAccess_token(),sUserId);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {

                    mCart = response.body();

                    //set Shopping Cart Item Count
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText(Integer.toString(mCart.mCartItems.size()));

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
    public void onServiceFragmentClick(ServicePackage servicePackage){
        Log.i("logs", "onServiceFragmentClick: ");

        if (!mSessionManager.isLoggedIn()){

//            setupLogin();

        }else {
//            sUserId =mSessionManager.getUserId();       //Extract unique UserId
//            getServerData(1);   //Sending DATA_REQUEST_ID=1; Whole Data includes ServiceSategoryData & CartItems
        }

    }

//    private void setupLogin() {
//
//        //Request user to login; Cart services will be provided only for logged in users
//        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
//        builder.setMessage(R.string.dialog_logout_question)
//                .setTitle(R.string.dialog_title_logout)
//                .setPositiveButton(R.string.dialog_button_logout, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        allAccountLogOut(); //Logout all accounts
//                    }
//                })
//                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        MenuItem item = menu.findItem(R.id.shopping_cart);
        MenuItemCompat.setActionView(item, R.layout.menu_cart_layout);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        mTextView = (TextView) relativeLayout.findViewById(R.id.tv_cart_count);
        mTextView.setVisibility(View.INVISIBLE);

//        mTextView.setText("6");

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
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
