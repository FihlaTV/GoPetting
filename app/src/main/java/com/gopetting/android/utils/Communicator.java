package com.gopetting.android.utils;

import com.gopetting.android.models.CartItem;

import java.util.List;

/**
 * Created by Sumit on 10/22/2016.
 */
public class Communicator {

    public interface FragmentCommunicator{
        public void passDataToFragment(List<CartItem> cartItems, int id);

        public void passSecondDataToFragment(int id);

    }


}
