package com.stanwind.lock.feature;

import com.stanwind.lock.PlatformUtils;

/**
 * PIDFeatureParam
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2018-12-18 11:25
 **/
public class PIDFeatureParam implements LockFeatureParam {

    @Override
    public String getFeature() {
        return PlatformUtils.JVMPid() + "";
    }
}
