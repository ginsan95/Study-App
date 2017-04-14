package com.sunway.averychoke.studywifidirect3.controller.common_class.study_material;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentStudyMaterialBinding;
import com.sunway.averychoke.studywifidirect3.util.PermissionUtil;

/**
 * Created by AveryChoke on 14/4/2017.
 */

public class StudentMaterialFragment extends SWDBaseFragment {

    private FragmentStudyMaterialBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_material, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // region Setup permission view
        mBinding.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStudyMaterial();
            }
        });

        mBinding.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PermissionUtil.getSettingIntent(getContext()));
            }
        });
        // endregion
    }

    @Override
    public void onStart() {
        super.onStart();
        displayStudyMaterial();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE.getRequestCode()) {
            PermissionUtil.PermissionStatus status = PermissionUtil.isRequestGranted(requestCode, permissions, grantResults, this, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE);
            switch (status) {
                case ALLOW:
                    displayStudyMaterial();
                    break;
                case DENY:
                    mBinding.classMaterial.getRoot().setVisibility(View.GONE);
                    mBinding.permissionTextView.setText(R.string.external_storage_reason);
                    break;
                case NEVER_ASK_AGAIN:
                    mBinding.classMaterial.getRoot().setVisibility(View.GONE);
                    mBinding.permissionTextView.setText(R.string.external_storage_settings);
                    break;
            }
        }
    }

    private void displayStudyMaterial() {
        if (PermissionUtil.isPermissionGranted(this, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)) {
            mBinding.permissionLayout.setVisibility(View.GONE);
            mBinding.classMaterial.getRoot().setVisibility(View.VISIBLE);
        } else {
            mBinding.classMaterial.getRoot().setVisibility(View.GONE);
            PermissionUtil.requestPermission(this, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    // region Get Set
    protected FragmentStudyMaterialBinding getBinding() {
        return mBinding;
    }
    // endregion
}
