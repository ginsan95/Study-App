package com.sunway.averychoke.studywifidirect3.controller.connection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunway.averychoke.studywifidirect3.manager.BaseManager;

/**
 * Created by AveryChoke on 4/5/2017.
 */

public class CloseConnectionService extends Service {
    private static final String TAG = CloseConnectionService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // close the connection
        WifiP2pManager wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (wifiManager != null && BaseManager.getInstance().getChannel() != null) {
            wifiManager.clearLocalServices(BaseManager.getInstance().getChannel(), null);
            wifiManager.stopPeerDiscovery(BaseManager.getInstance().getChannel(), null);
            wifiManager.removeGroup(BaseManager.getInstance().getChannel(), null);
        }
        Log.d(TAG, "Service destroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
        Log.d(TAG, "Service removed");
    }
}
