package com.gopetting.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gopetting.android.R;
import com.gopetting.android.activities.ServiceActivity;
import com.gopetting.android.bus.ActivityToFragment;
import com.gopetting.android.models.FilterSubCategoryData;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.models.ServicePackageData;
import com.gopetting.android.models.ServicePackageDetailData;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

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

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBarMedium;

    private static final String SERVICE_PACKAGES = "service_packages";
//    private static final String SERVICE_SUBCATEGORY_INDEX = "service_subcategory_index";
    private ServiceFragmentListener mListener;
    private Unbinder unbinder;
    private int serviceSubCategoryIndex;
    private FastItemAdapter fastAdapterService;
    private LinearLayoutManager mLayoutManagerService;
    private List<ServicePackage> mServicePackages;
    private List<ServicePackageData> mServicePackageData = new ArrayList<>();

    public ServiceFragment() {

    }

    public static Fragment newInstance(List<ServicePackage> servicePackages) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
//        args.putInt(SERVICE_SUBCATEGORY_INDEX,l);
        args.putParcelable(SERVICE_PACKAGES, Parcels.wrap(servicePackages));
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
//            serviceSubCategoryIndex = getArguments().getInt(SERVICE_SUBCATEGORY_INDEX);
            mServicePackages = Parcels.unwrap(getArguments().getParcelable(SERVICE_PACKAGES));
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
        initFragmentData();
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
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(SERVICE_PACKAGES, Parcels.wrap(mServicePackages));
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

    private void initFragmentData() {

//        mServicePackages = ((ServiceActivity)getActivity()).getServicePackages(serviceSubCategoryIndex);

//        mServicePackages = ServiceCategoryData.getServicePackages(serviceSubCategoryIndex);

        for (ServicePackage servicePackage : mServicePackages) {
//        for(int i=0;i<=mServicePackages.size();i++){

            ServicePackageData servicePackageData = new ServicePackageData();
            servicePackageData.setServicePackageId(servicePackage.getServicePackageId());
            servicePackageData.setServicePackageName(servicePackage.getServicePackageName());
            servicePackageData.setDescription(servicePackage.getDescription());
            servicePackageData.setPrice(servicePackage.getPrice());

            ServicePackageDetailData servicePackageDetailData = new ServicePackageDetailData();
            servicePackageDetailData.setServicePackageId(servicePackage.getServicePackageId());
            servicePackageDetailData.setDetails(servicePackage.getServicePackageDetails());

            List<IItem> subItems = new LinkedList<>();
            subItems.add(servicePackageDetailData);
            servicePackageData.setSubItems(subItems);

            mServicePackageData.add(servicePackageData);
        }


        fastAdapterService = new FastItemAdapter();
        fastAdapterService.withSelectable(true);
        fastAdapterService.add(mServicePackageData); //Fetch ServicePackages from ServiceActivity
//fastAdapterService.add(FilterSubCategoryData.getBreedTypeData());
        mRecyclerViewService.setAdapter(fastAdapterService);

        mLayoutManagerService = new LinearLayoutManager(getContext());
        mLayoutManagerService.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewService.setLayoutManager(mLayoutManagerService);
        mRecyclerViewService.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewService.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));//Adding item divider

        mProgressBarMedium.setVisibility(View.GONE);

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void PackageSubscriber(ActivityToFragment activityToFragment){
//       mServicePackages = activityToFragment.getServicePackages();
//
//    }

    //FragmentCommunicator interface implementation
    @Override
    public void passDataToFragment(List<ServicePackage> servicePackages){
//        mServicePackages = servicePackages;

    }


    public interface ServiceFragmentListener {
        void onServiceFragmentClick(int servicePackage);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int servicePackage) {
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
