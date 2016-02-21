package com.example.sumit.apple.bus;

/**
 * Created by Sumit on 2/4/2016.
 */
public final class CheckMenuItemEvent {

    private boolean mCheck;

    public CheckMenuItemEvent(boolean check) {
        mCheck = check;
    }

    public boolean getCheck() {
        return mCheck;
    }
}
