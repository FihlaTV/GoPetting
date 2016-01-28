package com.example.sumit.apple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sumit on 1/24/2016.
 */
public class AboutUsFragment extends Fragment {
    public static final String ARG_QUOTE_NUMBER = "quote_number";

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
        //Inflate Contact Us menu
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
        text.setText(R.string.about_us_text);
        return rootView;
    }
}

