package com.example.sumit.apple.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sumit.apple.R;
import com.example.sumit.apple.activities.DogShoppingActivity;
import com.example.sumit.apple.bus.UpdateActionBarTitleEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Sumit on 1/24/2016.
 */
public class AboutUsFragment extends BaseFragment {
//    public static final String ARG_QUOTE_NUMBER = "quote_number";

    /**
     * Create a new instance of Fragment that will be initialized
     * with the given arguments.
     */
    static AboutUsFragment newInstance() {
        AboutUsFragment fragment = new AboutUsFragment();
//        Bundle args = new Bundle();
//        args.putInt(fragment.ARG_QUOTE_NUMBER, itemID);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /* Update fragment's title.*/
        EventBus.getDefault().post(new UpdateActionBarTitleEvent(getString(R.string.AboutUs)));
        //Inflate menu
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
        text.setText(R.string.about_us_text);

        //TODO: Temporary code for DogShoppingActivity
        Intent intent = new Intent(getContext(), DogShoppingActivity.class);
        startActivity(intent);

        return rootView;
    }
}

