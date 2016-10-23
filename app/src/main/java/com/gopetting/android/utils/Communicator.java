package com.gopetting.android.utils;

import com.gopetting.android.models.ServicePackage;

import java.util.List;

/**
 * Created by Sumit on 10/22/2016.
 */
public class Communicator {

    public interface FragmentCommunicator{
        public void passDataToFragment(List<ServicePackage> servicePackages);
    }


}
