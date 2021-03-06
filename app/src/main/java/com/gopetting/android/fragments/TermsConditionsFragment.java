package com.gopetting.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gopetting.android.R;
import com.gopetting.android.bus.UpdateActionBarTitleEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Sumit on 1/24/2016.
 */
public class TermsConditionsFragment extends BaseFragment {
//    public static final String ARG_QUOTE_NUMBER = "quote_number";

    /**
     * Create a new instance of Fragment that will be initialized
     * with the given arguments.
     */
    static TermsConditionsFragment newInstance() {
        TermsConditionsFragment fragment = new TermsConditionsFragment();
//        Bundle args = new Bundle();
//        args.putInt(fragment.ARG_QUOTE_NUMBER, itemID);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /* Update fragment's title.*/
        EventBus.getDefault().post(new UpdateActionBarTitleEvent(getString(R.string.drawer_terms_conditions)));
        //Inflate menu
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
        text.setText(R.string.terms_conditon_text);
        return rootView;
    }
}

