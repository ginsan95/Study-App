package com.sunway.averychoke.studywifidirect3.controller.class_navigation.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class SearchReceiver extends BroadcastReceiver {
    public interface WifiDirectListener {
        void onConnectedToHost(String address);
    }

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectListener mListener;

    public SearchReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiDirectListener listener) {
        mManager = manager;
        mChannel = channel;
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (mManager == null || mChannel == null) {
            return;
        }

        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if (info.groupFormed && !info.isGroupOwner) {
                            mListener.onConnectedToHost(info.groupOwnerAddress.getHostAddress());
                        }
                    }
                });
            }
        }
    }
}
