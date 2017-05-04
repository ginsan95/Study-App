package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.connection.CloseConnectionService;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityClassBinding;
import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;

/**
 * Created by AveryChoke on 1/4/2017.
 */

public class StudentClassActivity extends SWDBaseActivity {
    private ActivityClassBinding mBinding;

    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private Intent mConnectionServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_class);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (!StudentManager.getInstance().isOffline()) {
            // wifi direct initialization
            mWifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mWifiManager.initialize(this, getMainLooper(), null);

            // start disconnection service
            BaseManager.getInstance().setChannel(mChannel);
            mConnectionServiceIntent = new Intent(this, CloseConnectionService.class);
            startService(mConnectionServiceIntent);
        }

        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new StudentClassFragment(), StudentClassFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // kills the tasks
        StudentManager.getInstance().killAllTasks();
        // kills wifi direct
        if (mConnectionServiceIntent != null) {
            stopService(mConnectionServiceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_class_exit_title)
                .setMessage(R.string.dialog_class_exit_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StudentClassActivity.super.onBackPressed();
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
