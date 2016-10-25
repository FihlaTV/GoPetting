package com.gopetting.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.activities.ServiceActivity;
import com.gopetting.android.bus.ActivityToFragment;
import com.gopetting.android.models.FilterSubCategoryData;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.models.ServicePackageData;
import com.gopetting.android.models.ServicePackageDetailData;
import com.gopetting.android.utils.Animators.SlideDownAlphaAnimator;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class ServiceFragment extends Fragment implements Communicator.FragmentCommunicator {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerViewService;

    private static final String SERVICE_SUBCATEGORY_INDEX = "service_subcategory_index";
    private ServiceFragmentListener mListener;
    private Unbinder unbinder;
    private int serviceSubCategoryIndex;
    private FastItemAdapter fastAdapterService;
    private LinearLayoutManager mLayoutManagerService;
    private List<ServicePackage> mServicePackages;
    private List<ServicePackageData> mServicePackageData = new ArrayList<>();
    private ClickListenerHelper<ServicePackage> mClickListenerHelper;


    public ServiceFragment() {

    }

    public static Fragment newInstance(int serviceSubCategoryIndex) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putInt(SERVICE_SUBCATEGORY_INDEX,serviceSubCategoryIndex);
//        args.putParcelable(SERVICE_PACKAGES, Parcels.wrap(servicePackages));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ServiceFragmentListener) {
            mListener = (ServiceFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ServiceFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            serviceSubCategoryIndex = getArguments().getInt(SERVICE_SUBCATEGORY_INDEX);
//            mServicePackages = Parcels.unwrap(getArguments().getParcelable(SERVICE_PACKAGES));
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (savedInstanceState != null) {
//            //probably orientation change etc.
//            mServicePackages = Parcels.unwrap(savedInstanceState.getParcelable(SERVICE_PACKAGES));
////            mServicePackages = Parcels.unwrap(getArguments().getParcelable(SERVICE_PACKAGES));
//        } else {
////            if (mServicePackageData != null) {
//            //returning from backstack, data is fine, do nothing
////            } else {
//            //newly created, compute data
//                initFragmentData();
////            }
//        }
        initFragmentData(savedInstanceState);
//
//        fastAdapterService = new FastItemAdapter();
//        fastAdapterService.withSelectable(true);
////        fastAdapterService.add(mServicePackages); //Fetch ServicePackages from ServiceActivity
//        fastAdapterService.add(FilterSubCategoryData.getBreedTypeData());
//        mRecyclerViewService.setAdapter(fastAdapterService);
//
//        mLayoutManagerService = new LinearLayoutManager(getContext());
//        mLayoutManagerService.setOrientation(LinearLayoutManager.VERTICAL);
//
//        mRecyclerViewService.setLayoutManager(mLayoutManagerService);
//        mRecyclerViewService.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerViewService.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));//Adding item divider
//
//        mProgressBarMedium.setVisibility(View.GONE);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            //probably orientation change etc.
//            mServicePackages = Parcels.unwrap(savedInstanceState.getParcelable(SERVICE_PACKAGES));
////            mServicePackages = Parcels.unwrap(getArguments().getParcelable(SERVICE_PACKAGES));
//        } else {
////            if (mServicePackageData != null) {
//                //returning from backstack, data is fine, do nothing
////            } else {
//                //newly created, compute data
////                initFragmentData();
////            }
//        }

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
    public void onDestroy() {
        super.onDestroy();
    }

    private void initFragmentData(Bundle savedInstanceState) {

//        mServicePackages = ((ServiceActivity)getActivity()).getServicePackages(serviceSubCategoryIndex);

//        mServicePackages = ServiceCategoryData.getServicePackages(serviceSubCategoryIndex);

//        for (ServicePackage servicePackage : mServicePackages) {
////        for(int i=0;i<=mServicePackages.size();i++){
//
//            ServicePackageData servicePackageData = new ServicePackageData();
//            servicePackageData.setServicePackageId(servicePackage.getServicePackageId());
//            servicePackageData.setServicePackageName(servicePackage.getServicePackageName());
//            servicePackageData.setDescription(servicePackage.getDescription());
//            servicePackageData.setPrice(servicePackage.getPrice());
//
//            ServicePackageDetailData servicePackageDetailData = new ServicePackageDetailData();
//            servicePackageDetailData.setServicePackageId(servicePackage.getServicePackageId());
//            servicePackageDetailData.setDetails(servicePackage.getServicePackageDetails());
//
//            List<IItem> subItems = new LinkedList<>();
//            subItems.add(servicePackageDetailData);
//            servicePackageData.setSubItems(subItems);
//
//            mServicePackageData.add(servicePackageData);
//        }


        fastAdapterService = new FastItemAdapter();
        fastAdapterService.withSelectable(true);
        fastAdapterService.withOnlyOneExpandedItem(true);

        //init the ClickListenerHelper which simplifies custom click listeners on views of the Adapter
        mClickListenerHelper = new ClickListenerHelper<>(fastAdapterService);


        try {
            fastAdapterService.add(ServiceCategoryData.getServicePackages(serviceSubCategoryIndex));
        } catch (Exception e) {
            Log.e("App in Long Background", e.toString());      //To finish Service Activity, to avoid Crash due to app coming from background after a long time.
            ((ServiceActivity)getActivity()).onBackPressed();

        }

//        fastAdapterService.add(ServiceCategoryData.getServicePackages());
//        fastAdapterService.add(mServicePackageData); //Fetch ServicePackages from ServiceActivity
//fastAdapterService.add(FilterSubCategoryData.getBreedTypeData());
        mRecyclerViewService.setAdapter(fastAdapterService);

        mLayoutManagerService = new LinearLayoutManager(getContext());
        mLayoutManagerService.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewService.setLayoutManager(mLayoutManagerService);
        mRecyclerViewService.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewService.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));//Adding item divider

        //restore selections (this has to be done after the items were added
        fastAdapterService.withSavedInstanceState(savedInstanceState);

        //a custom OnCreateViewHolder listener class which is used to create the viewHolders
        //we define the listener for the imageLovedContainer here for better performance
        //you can also define the listener within the items bindView method but performance is better if you do it like this
        fastAdapterService.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return fastAdapterService.getTypeInstance(viewType).getViewHolder(parent);
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder) {
                //we do this for our ServicePackage.ViewHolder
                if (viewHolder instanceof ServicePackage.ViewHolder) {
                    //if we click on the iv_basket (mItemBasket)
                    mClickListenerHelper.listen(viewHolder, ((ServicePackage.ViewHolder) viewHolder).mItemBasket, new ClickListenerHelper.OnClickListener<ServicePackage>() {
                        @Override
                        public void onClick(View v, int position, ServicePackage item) {
//                            item.withStarred(!item.mStarred);
                            //we animate the heart
//                            item.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), item.mStarred);

                            //we display the info about the click
                            Toast.makeText(getContext(), item.mServicePackageName, Toast.LENGTH_SHORT).show();

//                            mListener.onServiceFragmentClick(item); //Send back clicked Service Package


                        }
                        });
                    }

                return viewHolder;
            }
        });


    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void PackageSubscriber(ActivityToFragment activityToFragment){
//       mServicePackages = activityToFragment.getServicePackages();
//
//    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapterService.saveInstanceState(outState);
        super.onSaveInstanceState(outState);

//        outState.putParcelable(SERVICE_PACKAGES, Parcels.wrap(mServicePackages));
    }


    //FragmentCommunicator interface implementation
    @Override
    public void passDataToFragment(List<ServicePackage> servicePackages){
//        mServicePackages = servicePackages;

    }


    public interface ServiceFragmentListener {
        void onServiceFragmentClick(ServicePackage servicePackage);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(ServicePackage servicePackage) {
        if (mListener != null) {
            mListener.onServiceFragmentClick(servicePackage);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
