package com.sunway.averychoke.studywifidirect3.controller.student_class.study_material;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.common_class.study_material.StudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsUpdaterListener;
import com.sunway.averychoke.studywifidirect3.controller.connection.DownloadException;
import com.sunway.averychoke.studywifidirect3.controller.connection.TeacherThread;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 30/1/2017.
 */

public class StudentStudyMaterialFragment extends StudyMaterialFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        ClassMaterialsUpdaterListener {

    private StudentManager sManager;
    private ClassMaterialAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mAdapter = new ClassMaterialAdapter(false, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().classMaterial.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().classMaterial.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getStudyMaterials());

        getBinding().classMaterial.addButton.setVisibility(View.GONE);

        if (!sManager.isOffline()) {
            getBinding().classMaterial.materialsSwipeRefreshLayout.setOnRefreshListener(this);
            onRefresh();
        } else {
            getBinding().classMaterial.materialsSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onRefresh() {
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(true);
        ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this);
        task.execute(TeacherThread.Request.STUDY_MATERIALS);
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        // // TODO: 14/4/2017 view study material
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        if (classMaterial.getStatus() == ClassMaterial.Status.DOWNLOADING) {
            return;
        }

        // region setup options
        final StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
        final List<CharSequence> options = new ArrayList<>();
        if (!sManager.isOffline()) {
            options.add(getString(R.string.option_download));
        }
        options.add(getString(R.string.option_delete_study_material));
        // endregion

        new AlertDialog.Builder(getContext())
                .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String option = options.get(which).toString();
                        if (option.equals(getString(R.string.option_download))) {
                            downloadStudyMaterial(studyMaterial);
                        } else if (option.equals(getString(R.string.option_delete_study_material))) {
                            deleteStudyMaterial(studyMaterial, index);
                        }
                    }
                })
                .show();
    }

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {

    }
    // endregion class material view holder

    // region Class Materials Updater
    @Override
    public void onClassMaterialUpdated(ClassMaterial classMaterial) {
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(false);

        mAdapter.replaceClassMaterial(classMaterial);

        if (getContext() != null) {
            Toast.makeText(getContext(), "Downloaded " + classMaterial.getName() + "successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassMaterialsUpdated() {
        mAdapter.setClassMaterials(sManager.getStudyMaterials());
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(Exception e) {
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(false);

        if (e instanceof DownloadException) {
            ClassMaterial classMaterial = ((DownloadException) e).getClassMaterial();
            if (classMaterial instanceof StudyMaterial) {
                StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
                sManager.updateStudyMaterialStatus(studyMaterial, ClassMaterial.Status.PENDING);
                mAdapter.replaceClassMaterial(studyMaterial);
            }
        }

        if (getContext() != null) {
            Toast.makeText(getContext(), e != null ? e.toString() : "Error", Toast.LENGTH_SHORT).show();
        }
    }
    // endregion

    private void downloadStudyMaterial(StudyMaterial studyMaterial) {
        if (!sManager.isOffline()) {
            sManager.updateStudyMaterialStatus(studyMaterial, ClassMaterial.Status.DOWNLOADING);
            mAdapter.replaceClassMaterial(studyMaterial);
            ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this, studyMaterial);
            task.execute(TeacherThread.Request.STUDY_MATERIAL, studyMaterial.getName());
        }
    }

    private void deleteStudyMaterial(final StudyMaterial studyMaterial, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.option_delete_study_material)
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

                    }
                })
                .show();
    }
}
