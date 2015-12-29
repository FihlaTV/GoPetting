package com.example.sumit.apple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sumit on 12/28/2015.
 */
public class ContactFragment extends Fragment  {
    public static final String ARG_QUOTE_NUMBER = "quote_number";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        //Inflate Contact Us menu
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.contact);
        text.setText(R.string.contact_text);
        return rootView;

//        int itemId = getArguments().getInt(ARG_QUOTE_NUMBER);
//        int arrayIndex = getArguments().getInt(ARG_QUOTE_NUMBER);
////        get the text view to display the text
//        TextView textViewQuote = (TextView) rootView.findViewById(R.id.text);
////        get the quote
//        String quote = getResources()
//                .getStringArray(R.array.quotes_array)[arrayIndex];
////        get the quoter
//        String quoter = getResources()
//                .getStringArray(R.array.quotes_array_list)[arrayIndex];
//        textViewQuote.setText(quote);
////        set the title in the action bar to the selected quoter
//        getActivity().setTitle(quoter);
//        return rootView;
    }
}
