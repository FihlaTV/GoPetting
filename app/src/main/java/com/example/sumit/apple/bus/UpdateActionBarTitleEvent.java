package com.example.sumit.apple.bus;

/**
 * Created by Sumit on 2/2/2016.
 */

public final class UpdateActionBarTitleEvent {

    private CharSequence mTitle;

    public UpdateActionBarTitleEvent(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }
}