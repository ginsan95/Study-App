package com.sunway.averychoke.studywifidirect3.controller.teacher_class.study_material;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.BuildConfig;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.common_class.study_material.StudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;
import com.sunway.averychoke.studywifidirect3.util.FileUtil;
import com.sunway.averychoke.studywifidirect3.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

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
        if (classMaterial.getStatus() == ClassMaterial.Status.NORMAL) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            try {
                Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", ((StudyMaterial) classMaterial).getFile());
                String mimeType = FileUtil.getMimeType(getContext(), uri);
                intent.setDataAndType(uri, mimeType);
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.open_study_material_failed_message, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IllegalArgumentException || e instanceof FileUriExposedException) {
                    Toast.makeText(getContext(), R.string.open_file_permission_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        // region setup options
        final StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
        final List<CharSequence> options = new ArrayList<>();
        if (classMaterial.getStatus() == ClassMaterial.Status.NORMAL) {
            options.add(getString(R.string.option_rename));
        }
        options.add(getString(R.string.option_delete));
        // endregion

        if (options.size() > 1) {
            new AlertDialog.Builder(getContext())
                    .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String option = options.get(which).toString();
                            if (option.equals(getString(R.string.option_rename))) {
                                renameStudyMaterial(studyMaterial);
                            } else if (option.equals(getString(R.string.option_delete))) {
                                deleteStudyMaterial(studyMaterial, index);
                            }
                        }
                    })
                    .show();
        } else {
            deleteStudyMaterial(studyMaterial, index);
        }
    }

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {
        StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
        studyMaterial.setVisible(isChecked);
        sManager.updateStudyMaterialVisible(studyMaterial);
    }
    // endregion class material view holder

    private void renameStudyMaterial(final StudyMaterial studyMaterial) {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setText(studyMaterial.getName());
        editText.selectAll();

        final Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_rename_study_material_title)
                .setMessage(R.string.dialog_rename_study_material_message)
                .setView(editText)
                .setPositiveButton(R.string.option_rename, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editText.getText().toString();
                        if (!TextUtils.isEmpty(newName.trim())
                                && !studyMaterial.getName().equals(newName)
                                && sManager.renameStudyMaterial(studyMaterial, newName)) {
                            mAdapter.replaceClassMaterial(studyMaterial);
                            return; // successfully exited the method
                        }
                        // return not called, means got error
                        Toast.makeText(getContext(), R.string.rename_study_material_failure_message, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void deleteStudyMaterial(final StudyMaterial studyMaterial, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_study_material_title)
                .setMessage(R.string.delete_study_material_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
}
