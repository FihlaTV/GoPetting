package com.example.sumit.apple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sumit.apple.adapters.CardsAdapter;
import com.example.sumit.apple.bus.CheckMenuItemEvent;
import com.example.sumit.apple.bus.UpdateActionBarTitleEvent;
import com.example.sumit.apple.fragments.BaseFragment;
import com.example.sumit.apple.fragments.OffersScreenFragment;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Sumit on 1/24/2016.
 */
public class HomeFragment extends BaseFragment {


    private static final String ARG_QUOTE_NUMBER = "quote_number";
    private ViewPager mHomePager;
    private TextView mHomeText;
    private List<Card> cards;     //TODO: Cards Recycler View: Check whether it should be ArrayList<Card> contacts;


    /**
     * Create a new instance of Fragment that will be initialized
     * with the given arguments.
     */
    static HomeFragment newInstance() {
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

        //TODO: RecyclerView Cards; Need to update this for 3 Cards.

        // Lookup the recyclerview in activity layout
        RecyclerView rvCards = (RecyclerView) rootView.findViewById(R.id.cards_recycler_view);

        // Initialize contacts
        cards = Card.createCardsList();
        // Create adapter passing in the sample user data
        CardsAdapter adapter = new CardsAdapter(cards);
        // Attach the adapter to the recyclerview to populate items
        rvCards.setAdapter(adapter);
        // Set layout manager to position the items
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));     //TODO: Cards Recycler View: changed 'this' to 'getContext()'
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

