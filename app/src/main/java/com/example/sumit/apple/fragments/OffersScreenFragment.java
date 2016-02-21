package com.example.sumit.apple.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sumit.apple.R;


/**
 * Created by Sumit on 2/11/2016.
 */
public class OffersScreenFragment extends BaseFragment {

    private static final String ARG_POSITION = "position";

    /**
     * Create a new instance of OffersScreenFragment, setting arguments;
     */
    public static OffersScreenFragment newInstance(int position) {
        OffersScreenFragment f = new OffersScreenFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_offers_screen, container, false);


        TextView text = (TextView) rootView.findViewById(R.id.pager_text);
        text.setText(Integer.toString((getArguments().getInt(ARG_POSITION))));
        return rootView;
    }
}
