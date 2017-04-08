package com.sunway.averychoke.studywifidirect3.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by AveryChoke on 8/4/2017.
 */

// A container class for recycler view
public class DeviceClass implements Comparable<DeviceClass> {

    public String mClassName;
    public WifiP2pDevice mDevice;

    public DeviceClass(String className) {
        this(className, null);
    }

    public DeviceClass(String className, @Nullable WifiP2pDevice device) {
        mClassName = className;
        mDevice = device;
    }

    public boolean isTeacherHosting() {
        return mDevice != null;
    }

    @Override
    public String toString() {
        if (mDevice != null) {
            return mClassName + " (" + mDevice.deviceName + ")";
        } else {
            return mClassName;
        }
    }

    // region Get Set
    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public WifiP2pDevice getDevice() {
        return mDevice;
    }

    public void setDevice(WifiP2pDevice device) {
        mDevice = device;
    }

    @Override
    public int compareTo(@NonNull DeviceClass deviceClass) {
        return toString().compareTo(deviceClass.toString());
    }
    // endregion
}
