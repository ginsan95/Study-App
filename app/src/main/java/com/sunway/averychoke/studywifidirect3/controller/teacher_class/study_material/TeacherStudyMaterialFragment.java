package com.sunway.averychoke.studywifidirect3.controller.teacher_class.study_material;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.common_class.study_material.StudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;
import com.sunway.averychoke.studywifidirect3.util.FileUtil;
import com.sunway.averychoke.studywifidirect3.util.PermissionUtil;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by AveryChoke on 30/1/2017.
 */

public class TeacherStudyMaterialFragment extends StudyMaterialFragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        UploadStudyMaterialTask.UploadListener {

    private static final int UPLOAD_STUDY_MATERIAL_CODE = 101;

    private ProgressDialog mProgressDialog;

    private TeacherManager sManager;
    private ClassMaterialAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = TeacherManager.getInstance();
        mAdapter = new ClassMaterialAdapter(true, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().classMaterial.materialsSwipeRefreshLayout.setEnabled(false);

        getBinding().classMaterial.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().classMaterial.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getStudyMaterials());

        getBinding().classMaterial.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudyMaterial();
            }
        });

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.uploading));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPLOAD_STUDY_MATERIAL_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    mProgressDialog.show();
                    Uri uri = data.getData();
                    UploadStudyMaterialTask task = new UploadStudyMaterialTask(getContext(), this);
                    task.execute(uri);
                }
                break;
        }
    }

    private void addStudyMaterial() {
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)) {
            // ask for permission
            PermissionUtil.requestPermission(this, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_study_material_message)), UPLOAD_STUDY_MATERIAL_CODE);
        }
    }

    // region Upload listener
    @Override
    public void onUploadCompleted(@NonNull List<StudyMaterial> studyMaterials) {
        mAdapter.addClassMaterials(studyMaterials);

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    // endregion

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(((StudyMaterial)classMaterial).getFile());
        String mimeType = FileUtil.getMimeType(getContext(), uri);
        intent.setDataAndType(uri, mimeType);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.option_delete_study_material)
                .setMessage(R.string.delete_study_material_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
                        mAdapter.removeClassMaterial(index);
                        sManager.deleteStudyMaterial(studyMaterial);
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

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {
        StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
        studyMaterial.setVisible(isChecked);
        sManager.updateStudyMaterialVisible(studyMaterial);
    }
    // endregion class material view holder
}
