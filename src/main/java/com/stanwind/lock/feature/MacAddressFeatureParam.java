package com.stanwind.lock.feature;

import com.stanwind.lock.PlatformUtils;

/**
 * MacAddressFeatureParam MAC地址特征
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2018-12-18 10:39
 **/
public class MacAddressFeatureParam implements LockFeatureParam {

    @Override
    public String getFeature() {
        return PlatformUtils.getMACAddress();
    }
}
