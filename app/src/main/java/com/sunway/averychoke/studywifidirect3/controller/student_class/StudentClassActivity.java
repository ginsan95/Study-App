package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.TeacherReceiver;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainContainerBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;

/**
 * Created by AveryChoke on 1/4/2017.
 */

public class StudentClassActivity extends SWDBaseActivity {
    private ActivityMainContainerBinding mBinding;

    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;

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
        if (mWifiManager != null && mChannel != null) {
            mWifiManager.removeGroup(mChannel, null);
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
