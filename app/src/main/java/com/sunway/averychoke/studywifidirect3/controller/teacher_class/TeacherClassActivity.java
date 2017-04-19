package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.connection.TeacherThread;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainContainerBinding;
import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.util.WifiDirectUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AveryChoke on 1/4/2017.
 */

public class TeacherClassActivity extends SWDBaseActivity {

    private ActivityMainContainerBinding mBinding;

    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private TeacherReceiver mTeacherReceiver;
    private IntentFilter mReceiverIntentFilter;
    private TeacherThread mTeacherThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_container);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // wifi direct initialization
        mWifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiManager.initialize(this, getMainLooper(), null);

        // region broadcast receiver
        mTeacherReceiver = new TeacherReceiver(mWifiManager, mChannel);
        mReceiverIntentFilter = new IntentFilter();
        mReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        registerReceiver(mTeacherReceiver, mReceiverIntentFilter);
        // endregion

        // region setup wifi direct service
        if (mWifiManager != null && mChannel != null) {
            WifiDirectUtil.deletePersistentGroup(mWifiManager, mChannel);

            Map<String, String> record = new HashMap<>();
            record.put("class_name", TeacherManager.getInstance().getClassName());
            final WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(BaseManager.APP_ID, "_presence._tcp", record);

            // stop previous service
            mWifiManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // add new service
                    mWifiManager.addLocalService(mChannel, serviceInfo, null);
                    // discover peers to make you searchable
                    mWifiManager.discoverPeers(mChannel, null);
                }

                @Override
                public void onFailure(int reason) {

                }
            });
        }
        // endregion

        // start teacher thread
        try {
            mTeacherThread = new TeacherThread();
            new Thread(mTeacherThread).start();
        } catch (IOException e) {
            // todo pop up error message
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new TeacherClassFragment(), TeacherClassFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // kills the thread
        if (mTeacherThread != null) {
            mTeacherThread.disconnect();
        }
        // kills wifi direct
        if (mWifiManager != null && mChannel != null) {
            mWifiManager.clearLocalServices(mChannel, null);
            mWifiManager.stopPeerDiscovery(mChannel, null);
            mWifiManager.removeGroup(mChannel, null);
        }
        unregisterReceiver(mTeacherReceiver);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_class_exit_title)
                .setMessage(R.string.dialog_class_exit_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TeacherClassActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.containerLayout.getId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}