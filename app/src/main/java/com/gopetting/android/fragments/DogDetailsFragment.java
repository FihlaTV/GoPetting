package com.gopetting.android.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gopetting.android.R;

public class DogDetailsFragment extends Fragment {
    // Store instance variables
    private String title;
    private String imageUrl;

    // newInstance constructor for creating fragment with arguments
    public static DogDetailsFragment newInstance(String image, String title) {
        DogDetailsFragment fragmentFirst = new DogDetailsFragment();
        Bundle args = new Bundle();
        args.putString("someImage", image);
        args.putString("someTitle", title);     //Remove this title field, not required
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments().getString("someImage");
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_details, container, false);
        ImageView ivProductDetails = (ImageView) view.findViewById(R.id.iv_product_details);
//        ivProductDetails.setImageResource(page);

        Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_promo).into(ivProductDetails);
//        Toast.makeText(getContext(),imageUrl, Toast.LENGTH_SHORT).show();
        return view;

    }
}

