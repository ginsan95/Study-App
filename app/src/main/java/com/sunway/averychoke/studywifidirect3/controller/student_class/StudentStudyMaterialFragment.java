package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

/**
 * Created by AveryChoke on 30/1/2017.
 */

public class StudentStudyMaterialFragment extends SWDBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ClassMaterialViewHolder.OnClassMaterialSelectListener {

    private StudentManager sManager;
    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mClassMaterialAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mDatabase = new DatabaseHelper(getContext());
        mClassMaterialAdapter = new ClassMaterialAdapter(true, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_material, container, false);
        mBinding = DataBindingUtil.bind(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mClassMaterialAdapter);
        mClassMaterialAdapter.setClassMaterials(sManager.getStudyMaterials());

        mBinding.addButton.setVisibility(View.GONE);

        if (!sManager.isOffline()) {
            mBinding.materialsSwipeRefreshLayout.setOnRefreshListener(this);
            onRefresh();
        } else {
            mBinding.materialsSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onRefresh() {
        // // TODO: 7/4/2017 Read data from teacher
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {

    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_study_material_dialog_title)
                .setMessage(R.string.delete_study_material_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StudyMaterial studyMaterial = (StudyMaterial) classMaterial;
                        mClassMaterialAdapter.removeClassMaterial(index);
                        sManager.deleteStudyMaterial(studyMaterial);

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    @Override
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {
    }
    // endregion class material view holder
}
