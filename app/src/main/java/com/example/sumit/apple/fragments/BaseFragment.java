package com.example.sumit.apple.fragments;

import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * Created by Sumit on 2/2/2016.
 */
public abstract class BaseFragment extends Fragment {

    public void onEvent(Object e) {

    }
    //TODO: Check if Eventbus registration should be done in onStart/onStop like in MainActivity.
    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

}