package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.student_class.StudentClassActivity;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.TeacherClassActivity;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassBinding;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.util.WifiDirectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AveryChoke on 22/1/2017.
 */

public class ClassFragment extends SWDBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        DeviceClassesAdapter.DeviceClassViewHolder.OnDeviceClassSelectListener,
        WifiP2pManager.DnsSdServiceResponseListener,
        WifiP2pManager.DnsSdTxtRecordListener,
        SearchReceiver.WifiDirectListener {

    private FragmentClassBinding mBinding;
    private ProgressDialog mProgressDialog;

    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private SearchReceiver mSearchReceiver;
    private IntentFilter mReceiverIntentFilter;
    private Map<String, String> mClassesName;
    private Handler mConnectionHandler;
    private Runnable mEndConnectionThread;

    private DatabaseHelper mDatabase;
    private DeviceClassesAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // set the id counter for the model objects
        ClassMaterial.mCounter = mDatabase.getClassMaterialMaxId();
        Question.mCounter = mDatabase.getQuestionMaxId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Classes");

        mBinding.classesSwipeRefreshLayout.setOnRefreshListener(this);

        mBinding.classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.classesRecyclerView.setAdapter(mAdapter);

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);

        // set initial data
        mAdapter.setDeviceClasses(getDatabaseClasses());
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
        mAdapter.setDeviceClasses(getDatabaseClasses());
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

        mBinding.classesSwipeRefreshLayout.setRefreshing(false);
    }

    // region wifi direct service
    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        // check if the device is running our app
        if (instanceName.equalsIgnoreCase(SWDBaseActivity.APP_ID)) {
            String className = mClassesName.get(srcDevice.deviceAddress);
            if (className != null) {
                mAdapter.addOrReplaceDeviceClass(new DeviceClass(className, srcDevice));
            }
        }
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        // get the class of the teacher
        String className = txtRecordMap.get("class_name");
        if (className != null) {
            mClassesName.put(srcDevice.deviceAddress, className);
        }
    }
    // endregion

    // region class view holder
    @Override
    public void onDeviceClassSelected(final DeviceClass deviceClass) {
        final CharSequence[] choices = new CharSequence[] {
                getString(R.string.option_host_class),
                getString(R.string.option_participate_class),
                getString(R.string.option_view_class)
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Host class
                                TeacherManager.getInstance().initialize(deviceClass.getClassName(), getContext());
                                Intent teacherIntent = new Intent(getActivity(), TeacherClassActivity.class);
                                startActivity(teacherIntent);
                                break;
                            case 1: // Participate class
                                StudentManager.getInstance().initialize(deviceClass.getClassName(), getContext());
                                Intent studentIntent = new Intent(getActivity(), StudentClassActivity.class);
                                startActivity(studentIntent);
                                break;
                            case 2: // View edit class

                                break;
                        }
                    }
                });
        dialog.show();
    }

    @Override
    public void onDeviceClassLongClicked(@NonNull final DeviceClass deviceClass, @NonNull final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_class_dialog_title)
                .setMessage(R.string.delete_class_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeClassName(index);
                        mDatabase.deleteClass(deviceClass.getClassName());
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    // endregion class view holder

    // region host room wifi direct listener
    @Override
    public void onConnectedToHost(String address) {
        // start the necessary thread

    }
    // endregion

    private void createClass() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.create_class_dialog_title)
                .setMessage(R.string.create_class_dialog_message)
                .setView(editText)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String className = editText.getText().toString();
                        if (!TextUtils.isEmpty(className.trim())) {
                            StudyClass studyClass = new StudyClass(className);
                            long errorCode = mDatabase.addClass(studyClass);
                            if (errorCode != -1) {
                                DeviceClass deviceClass = new DeviceClass(className);
                                mAdapter.addOrReplaceDeviceClass(deviceClass);
                                return; // successfully exited the method
                            }
                        }
                        // return not called, means got error
                        Toast.makeText(getContext(), R.string.create_class_failure_message, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void participateClass(WifiP2pDevice device) {
        mProgressDialog.show();

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
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

    // handler to end the connection after 15 seconds
    private void startConnectionHandler() {
        if (mConnectionHandler != null) {
            mEndConnectionThread = new Runnable() {
                @Override
                public void run() {
                    if (mWifiManager != null && mChannel != null) {
                        mWifiManager.cancelConnect(mChannel, null);
//                        if (sRepository.getGameDataUpdater() != null) {
//                            sRepository.getGameDataUpdater().disconnect();
//                        }
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

    private List<DeviceClass> getDatabaseClasses() {
        List<DeviceClass> deviceClasses = new ArrayList<>();
        List<String> classesName = mDatabase.getAllClassesName();
        for (String className : classesName) {
            deviceClasses.add(new DeviceClass(className));
        }
        return  deviceClasses;
    }

    private void showWifiDirectError(int reason) {
        endConnectionHandler();
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
