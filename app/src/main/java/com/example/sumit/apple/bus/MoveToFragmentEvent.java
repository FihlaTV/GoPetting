package com.example.sumit.apple.bus;


import android.support.v4.app.Fragment;

/**
 * Created by Sumit on 2/4/2016.
 */
public final class MoveToFragmentEvent {

    private Fragment mFragment;


    public MoveToFragmentEvent(Fragment fragment) {
        mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
    }
}