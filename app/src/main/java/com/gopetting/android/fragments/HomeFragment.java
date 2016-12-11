package com.gopetting.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gopetting.android.R;
import com.gopetting.android.bus.CheckMenuItemEvent;
import com.gopetting.android.bus.UpdateActionBarTitleEvent;
import com.gopetting.android.models.ProductData;
import com.gopetting.android.models.ProductItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;



/**
 * Created by Sumit on 1/24/2016.
 */
public class HomeFragment extends BaseFragment {


    private static final String ARG_QUOTE_NUMBER = "quote_number";
    private ViewPager mHomePager;
    private TextView mHomeText;
    private List<ProductItem> mProducts;


    /**
     * Create a new instance of Fragment that will be initialized
     * with the given arguments.
     */
   public static HomeFragment newInstance() {                       //TODO: This logic is creating new fragment every time. Change the logic like 'if(fragment==NULL)'
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUOTE_NUMBER, "Home Argument Text");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
/*        EventBus.getDefault().post(new CheckMenuItemEvent(true));
        *//* Update fragment's title.*//*
        EventBus.getDefault().post(new UpdateActionBarTitleEvent(getString(R.string.app_name)));
        //Inflate menu
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
        String temp= getArguments().getString(ARG_QUOTE_NUMBER);
        text.setText(temp);
        return rootView;*/

        EventBus.getDefault().post(new CheckMenuItemEvent(true));
        /* Update fragment's title.*/
        EventBus.getDefault().post(new UpdateActionBarTitleEvent(getString(R.string.app_name)));
        //Inflate menu
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
/*        //Inflate Home Text View
        mHomeText=(TextView) rootView.findViewById(R.id.home_text);
        mHomeText.setText(R.string.home_text);*/


        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) rootView.findViewById(R.id.products_recycler_view);

        //create our FastAdapter which will manage everything
        FastItemAdapter fastAdapter = new FastItemAdapter();

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvProducts.setAdapter(fastAdapter);

        //set the items to your ItemAdapter

        // Initialize ProductItems
        mProducts= ProductData.getProductItems();
        fastAdapter.add(mProducts);

        // Set layout manager to position the items
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));     //TODO: Cards Recycler View: changed 'this' to 'getContext()'
        // That's all!




        return rootView;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        insertNestedFragment();
        mHomePager=(ViewPager) view.findViewById(R.id.layout_fragment_home_pager);
        mHomePager.setAdapter(new HomePagerAdapter(getChildFragmentManager()));


    }

/*    // Embeds the child fragment dynamically
    private void insertNestedFragment() {
//        Fragment offersScreenFragment = new OffersScreenFragment();
//        getChildFragmentManager().beginTransaction().replace(R.id.layout_fragment_home_pager,offersScreenFragment).commit();

    }*/

    public static class HomePagerAdapter extends FragmentPagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            return OffersScreenFragment.newInstance(position);
        }
    }





}

