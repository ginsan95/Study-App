package com.sunway.averychoke.studywifidirect3.controller.class_navigation.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.student_class.StudentClassActivity;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentSearchClassBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.util.WifiDirectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AveryChoke on 9/4/2017.
 */

public class SearchClassFragment extends SWDBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        DeviceClassesAdapter.DeviceClassViewHolder.OnDeviceClassSelectListener,
        WifiP2pManager.DnsSdServiceResponseListener,
        WifiP2pManager.DnsSdTxtRecordListener,
        SearchReceiver.WifiDirectListener {
    private static final String ARGS_CLASS_NAME = "args_class_name";

    private FragmentSearchClassBinding mBinding;
    private ProgressDialog mProgressDialog;

    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private SearchReceiver mSearchReceiver;
    private IntentFilter mReceiverIntentFilter;
    private Map<String, String> mClassesName;
    private Handler mConnectionHandler;
    private Runnable mEndConnectionThread;

    private StudentManager sManager;
    private DatabaseHelper mDatabase;
    private DeviceClassesAdapter mAdapter;
    private String mClassName;

    public static SearchClassFragment newInstance(@Nullable String className) {
        Bundle args = new Bundle();
        args.putString(ARGS_CLASS_NAME, className);

        SearchClassFragment fragment = new SearchClassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassName = getArguments().getString(ARGS_CLASS_NAME);

        // wifi direct initialization
        mWifiManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiManager.initialize(getActivity(), getActivity().getMainLooper(), null);
        if (mWifiManager != null && mChannel != null) {
            WifiDirectUtil.deletePersistentGroups(mWifiManager, mChannel);
            mWifiManager.setDnsSdResponseListeners(mChannel, this, this);
        }

        // region wifi direct receiver
        mSearchReceiver = new SearchReceiver(mWifiManager, mChannel, this);
        mReceiverIntentFilter = new IntentFilter();
        mReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // endregion

        mDatabase = new DatabaseHelper(getContext());
        mAdapter = new DeviceClassesAdapter(this);
        mClassesName = new HashMap<>();
        mConnectionHandler = new Handler();
        sManager = StudentManager.getInstance();

        // set the id counter for the model objects
        ClassMaterial.mCounter = mDatabase.getClassMaterialMaxId();
        Question.mCounter = mDatabase.getQuestionMaxId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(mClassName != null ? mClassName : getString(R.string.title_search_classes));

        mBinding.classesSwipeRefreshLayout.setOnRefreshListener(this);

        mBinding.classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.classesRecyclerView.setAdapter(mAdapter);

        if (mClassName != null) {
            mBinding.goOfflineButton.setVisibility(View.VISIBLE);
            mBinding.goOfflineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sManager.initialize(mClassName, getContext());
                    Intent studentIntent = new Intent(getActivity(), StudentClassActivity.class);
                    startActivity(studentIntent);
                }
            });
        }

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);

        onRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.classesSwipeRefreshLayout.setRefreshing(false);
        try {
            getActivity().unregisterReceiver(mSearchReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        if (mWifiManager == null || mChannel == null) {
            return;
        }

        mBinding.classesSwipeRefreshLayout.setRefreshing(true);

        // clear previous data
        mClassesName.clear();
        mAdapter.clearDeviceClasses();
        mWifiManager.clearServiceRequests(mChannel, null);

        // register broadcast receiver to identify connection
        getActivity().registerReceiver(mSearchReceiver, mReceiverIntentFilter);

        // start new search
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mWifiManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // discover services
                mWifiManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int reason) {
                        showWifiDirectError(reason);
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                showWifiDirectError(reason);
            }
        });
    }

    // region wifi direct service
    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        // check if the device is running our app
        if (instanceName.equalsIgnoreCase(SWDBaseActivity.APP_ID)) {
            String className = mClassesName.get(srcDevice.deviceAddress);
            if (className != null) {
                mAdapter.addDeviceClass(new DeviceClass(className, srcDevice));
                mBinding.classesSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        // get the class of the teacher
        String className = txtRecordMap.get("class_name");
        if (className != null) {
            if (mClassName == null) {
                mClassesName.put(srcDevice.deviceAddress, className);
            } else if (className.equalsIgnoreCase(mClassName)) {
                mClassesName.put(srcDevice.deviceAddress, className);
            }
        }
    }
    // endregion

    // region class view holder
    @Override
    public void onDeviceClassSelected(final DeviceClass deviceClass) {
        mProgressDialog.show();
        sManager.setDeviceClass(deviceClass);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceClass.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0 ;

        if (mWifiManager != null && mChannel != null) {
            mWifiManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // prepare to end the connection if it takes too long to response
                    startConnectionHandler();
                }

                @Override
                public void onFailure(int reason) {
                    showWifiDirectError(reason);
                }
            });
        } else {
            showWifiDirectError(-1);
        }
    }
    // endregion class view holder

    // region host room wifi direct listener
    @Override
    public void onConnectedToHost(String address) {
        // start the necessary thread
        sManager.setTeacherAddress(address);
        sManager.initialize(sManager.getDeviceClass().getClassName(), getContext());
        Intent studentIntent = new Intent(getActivity(), StudentClassActivity.class);
        startActivity(studentIntent);
    }
    // endregion

    // handler to end the connection after 15 seconds
    private void startConnectionHandler() {
        if (mConnectionHandler != null) {
            mEndConnectionThread = new Runnable() {
                @Override
                public void run() {
                    if (mWifiManager != null && mChannel != null) {
                        mWifiManager.cancelConnect(mChannel, null);
                    }
                    showWifiDirectError(-1);
                }
            };
            mConnectionHandler.postDelayed(mEndConnectionThread, 15000);
        }
    }

    // kill the end connection handler
    private void endConnectionHandler() {
        if (mConnectionHandler != null && mEndConnectionThread != null) {
            mConnectionHandler.removeCallbacks(mEndConnectionThread);
        }
    }

    private void showWifiDirectError(int reason) {
        endConnectionHandler();
        mBinding.classesSwipeRefreshLayout.setRefreshing(false);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.dialog_connection_error_title)
                    .setMessage(R.string.dialog_connection_error_message)
                    .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }
}
