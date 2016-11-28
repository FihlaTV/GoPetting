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

import com.gopetting.android.R;
import com.gopetting.android.activities.ServiceActivity;
import com.gopetting.android.models.ServicePackage;
import com.gopetting.android.utils.Communicator;
import com.gopetting.android.utils.ServiceCategoryData;
import com.gopetting.android.utils.SimpleDividerItemDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import java.util.List;
import java.util.Set;

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
                    //if we click on the rl_basket_container (mItemBasketContainer)
                    mClickListenerHelper.listen(viewHolder, ((ServicePackage.ViewHolder) viewHolder).mItemBasketContainer, new ClickListenerHelper.OnClickListener<ServicePackage>() {
                        @Override
                        public void onClick(View v, int position, ServicePackage item) {

//                            for(int i=0;i<ServiceCategoryData.getServicePackages(serviceSubCategoryIndex).size();i++){
//
//                                ServiceCategoryData.getServicePackages(serviceSubCategoryIndex).get(i).setItemSelected(false);
//
//                                ServiceCategoryData.getServicePackages(serviceSubCategoryIndex).get(i).animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), ServiceCategoryData.getServicePackages(serviceSubCategoryIndex).get(i).mItemSelected);
//
//                            }
//                            fastAdapterService.notifyAdapterDataSetChanged();

//---------------------------------------------------
                            //Select/highlight currently pressed item and Deselect rest
//                            Set<Integer> selectionsBasket = fastAdapterService.getSelections();
//                            if (!selectionsBasket.isEmpty()) {
//                                int selectedPosition = selectionsBasket.iterator().next();
//                                fastAdapterService.deselect();
//                                item.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1),false);
//                                fastAdapterService.notifyItemChanged(selectedPosition);
//
//                            }
//                            fastAdapterService.select(position);
//
//                            item.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1),true);
//                            fastAdapterService.notifyItemChanged(position);

//----------------------------------------------
                            if (!item.isSelected()) {
                                Set<Integer> selections = fastAdapterService.getSelections();
                                if (!selections.isEmpty()) {
                                    int selectedPosition = selections.iterator().next();
                                    fastAdapterService.deselect();
                                    ServicePackage servicePackage = (ServicePackage) fastAdapterService.getItem(selectedPosition);
                                    servicePackage.withPressed(false);
                                    fastAdapterService.notifyItemChanged(selectedPosition);
                                }
                                fastAdapterService.select(position);
                                item.withPressed(true);

                            }


                            //we display the info about the click
//                            Toast.makeText(getContext(), item.mServicePackageName + serviceSubCategoryIndex, Toast.LENGTH_SHORT).show();

                            mListener.onServiceFragmentClick(item,serviceSubCategoryIndex); //Send back clicked Service Package


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
        void onServiceFragmentClick(ServicePackage servicePackage, int serviceSubCategoryIndex);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(ServicePackage servicePackage) {
        if (mListener != null) {
            mListener.onServiceFragmentClick(servicePackage, serviceSubCategoryIndex);
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
