package com.gopetting.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.adapters.ViewPagerAdapter;
import com.gopetting.android.bus.ActivityToFragment;
import com.gopetting.android.fragments.ServiceFragment;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.ServiceCategory;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.models.ServicePackageDetail;
import com.gopetting.android.models.ServiceSubCategory;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.mikepenz.fastadapter.IItem;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    private static final int SERVICE_CATEGORY_ID = 11;  //Pet Salon
    private Credential mCredential;
    private ViewPagerAdapter mViewPagerAdapter;
    private ServiceCategory mServiceCategoryData;
    //interface through which communication is made to fragment
    public Communicator.FragmentCommunicator mFragmentCommunicator;

//    private List<ServicePackage> mServicePackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);

        getServerData();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //To fetch ServicePackage data from ServiceFragment
//    public List<ServicePackage> getServicePackages(int serviceSubCategoryIndex){
//        return mServiceCategoryData.mServiceSubCategories.get(serviceSubCategoryIndex).getServicePackages();
//    }

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
                    Toast.makeText(ServiceActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        getServiceCategoryData();

                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            getServiceCategoryData();

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

    private void initServiceData() {

//      Linking ServicePackageDetail with ServicePackage based on service package id
        for (int i = 0; i <mServiceCategoryData.mServicePackageDetails.size(); i++) {


            for (int j = 0; j < mServiceCategoryData.mServiceSubCategories.size(); j++) {


                for (int k = 0; k < mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().size(); k++) {


                    if (mServiceCategoryData.mServicePackageDetails.get(i).getServicePackageId()
                            == mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().get(k).getServicePackageId()) {

//                        List<String> subItems = new LinkedList<>();
//                        subItems.add(mServiceCategoryData.mServicePackageDetails.get(i));

                        mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().get(k)
                                .setServicePackageDetails(mServiceCategoryData.mServicePackageDetails.get(i).getDetails());
//
//                        //Adding subitem (there's only 1 subitem for each service package) to corresponding service package
//                        mServiceCategoryData.mServiceSubCategories.get(j).getServicePackages().get(k).withSubItems(subItems);

                    }
                }
            }
        }




        //Set ServiceCategory to Storage class, for communicating to Fragment.
        ServiceCategoryData.setServiceCategoryData(mServiceCategoryData);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int l = 0; l <mServiceCategoryData.mServiceSubCategories.size() ; l++) {

            //Send ServicePackages to ServiceFragment
//            mFragmentCommunicator.passDataToFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServicePackages());
//            EventBus.getDefault().post(new ActivityToFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServicePackages(),l));

            mViewPagerAdapter.addFragment(mServiceCategoryData.mServiceSubCategories.get(l).getServiceSubcategoryName()
                                            ,ServiceFragment.newInstance(mServiceCategoryData.mServiceSubCategories.get(l).getServicePackages()));


        }

        mViewPagerService.setAdapter(mViewPagerAdapter);
        mViewPagerService.setOffscreenPageLimit(mViewPagerService.getAdapter().getCount());

        mTabLayoutService.setupWithViewPager(mViewPagerService);
        mTabLayoutService.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayoutService.setTabGravity(TabLayout.GRAVITY_CENTER);

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mViewPagerAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onServiceFragmentClick(int servicePackage){
        Log.i("logs", "onServiceFragmentClick: ");
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
    public void onStop() {
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);

    }

}
