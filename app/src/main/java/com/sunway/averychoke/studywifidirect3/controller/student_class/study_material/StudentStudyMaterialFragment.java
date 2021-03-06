package com.sunway.averychoke.studywifidirect3.controller.student_class.study_material;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.BuildConfig;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.common_class.study_material.StudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialProgressListener;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsUpdaterListener;
import com.sunway.averychoke.studywifidirect3.controller.connection.DownloadException;
import com.sunway.averychoke.studywifidirect3.controller.connection.TeacherThread;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;
import com.sunway.averychoke.studywifidirect3.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by AveryChoke on 30/1/2017.
 */

public class StudentStudyMaterialFragment extends StudyMaterialFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        ClassMaterialsUpdaterListener {

    private StudentManager sManager;
    private ClassMaterialAdapter mAdapter;
    private ExecutorService mExecutor;

    private Map<String, ProgressBar> mProgressMap;
    private enum ProgressBarState {
        INVISIBLE, VISIBLE
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mAdapter = new ClassMaterialAdapter(false, this);
        int bestThreadCount = Runtime.getRuntime().availableProcessors() + 1;
        mExecutor = Executors.newFixedThreadPool(bestThreadCount > 4 ? bestThreadCount : 4);
        mProgressMap = new HashMap<>();
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
            getBinding().classMaterial.materialsSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
            getBinding().classMaterial.materialsRecyclerView.setNestedScrollingEnabled(false);
            onRefresh();
        } else {
            getBinding().classMaterial.materialsSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
    }

    @Override
    public void onRefresh() {
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(true);
        ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this);
        task.executeOnExecutor(mExecutor, TeacherThread.Request.STUDY_MATERIALS);
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull final ClassMaterial classMaterial) {
        switch (classMaterial.getStatus()) {
            case NORMAL:
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
                break;
            case PENDING:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.option_download)
                        .setMessage(getString(R.string.dialog_download_study_material_message, classMaterial.getName()))
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadStudyMaterial((StudyMaterial) classMaterial);
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case DOWNLOADING:
                final ProgressBar progressBar = mProgressMap.get(classMaterial.getName());
                final LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.addView(progressBar);
                progressBar.setTag(ProgressBarState.VISIBLE);

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_download_progress_title)
                        .setMessage(getString(R.string.dialog_download_progress_message, classMaterial.getName()))
                        .setView(linearLayout)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                progressBar.setTag(ProgressBarState.INVISIBLE);
                                linearLayout.removeAllViews();
                            }
                        })
                        .show();
        }
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
        options.add(getString(R.string.option_delete));
        // endregion

        if (options.size() > 1) {
            new AlertDialog.Builder(getContext())
                    .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String option = options.get(which).toString();
                            if (option.equals(getString(R.string.option_download))) {
                                downloadStudyMaterial(studyMaterial);
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

    }
    // endregion class material view holder

    // region Class Materials Updater
    @Override
    public void onClassMaterialUpdated(ClassMaterial classMaterial) {
        getBinding().classMaterial.materialsSwipeRefreshLayout.setRefreshing(false);

        mAdapter.replaceClassMaterial(classMaterial);
        mProgressMap.remove(classMaterial.getName());

        if (getContext() != null) {
            Toast.makeText(getContext(), getString(R.string.download_success_message, classMaterial.getName()), Toast.LENGTH_SHORT).show();
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
        if (e != null) {
            e.printStackTrace();
        }

        if (e instanceof DownloadException) {
            DownloadException downloadException = (DownloadException) e;
            ClassMaterial classMaterial = downloadException.getClassMaterial();
            if (classMaterial instanceof StudyMaterial) {
                StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
                sManager.updateStudyMaterialStatus(studyMaterial, downloadException.getInitialStatus());
                mAdapter.replaceClassMaterial(studyMaterial);
                mProgressMap.remove(studyMaterial.getName());
            }
            if (getContext() != null) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (getContext() != null) {
            Toast.makeText(getContext(), R.string.student_class_connection_error_message, Toast.LENGTH_SHORT).show();
        }
    }
    // endregion

    private void downloadStudyMaterial(StudyMaterial studyMaterial) {
        if (!sManager.isOffline()) {
            final ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            progressBar.setTag(ProgressBarState.INVISIBLE);
            mProgressMap.put(studyMaterial.getName(), progressBar);

            ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this, studyMaterial,
                    new ClassMaterialProgressListener() {
                        @Override
                        public void onClassMaterialProgress(int progress) {
                            if (progressBar.getTag() != ProgressBarState.INVISIBLE) {
                                progressBar.setProgress(progress);
                            }
                        }
                    });
            task.executeOnExecutor(mExecutor, TeacherThread.Request.STUDY_MATERIAL, studyMaterial.getName());
            sManager.updateStudyMaterialStatus(studyMaterial, ClassMaterial.Status.DOWNLOADING);
            mAdapter.replaceClassMaterial(studyMaterial);
        }
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

                    }
                })
                .show();
    }
}
