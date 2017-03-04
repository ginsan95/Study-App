package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AveryChoke on 30/1/2017.
 */

public class StudentStudyMaterialFragment extends SWDBaseFragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener {

    private static final String CLASS_NAME = "class_name";
    private static final String STUDY_MATERIALS_KEY = "study_materials_key";

    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mClassMaterialAdapter;
    private String mClassName;
    private List<StudyMaterial> mStudyMaterials;

    private FragmentClassMaterialBinding mBinding;

    public static final StudentStudyMaterialFragment newInstance(String className, List<StudyMaterial> studyMaterials) {
        StudentStudyMaterialFragment studentStudyMaterialFragment = new StudentStudyMaterialFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STUDY_MATERIALS_KEY, (ArrayList<StudyMaterial>) studyMaterials);
        args.putString(CLASS_NAME, className);

        studentStudyMaterialFragment.setArguments(args);
        return studentStudyMaterialFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassName = getArguments().getString(CLASS_NAME);
        mStudyMaterials = getArguments().getParcelableArrayList(STUDY_MATERIALS_KEY);

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
        mBinding.materialsSwipeRefreshLayout.setEnabled(false);

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mClassMaterialAdapter);
        mClassMaterialAdapter.setClassMaterials(mStudyMaterials);

        mBinding.addButton.setVisibility(View.GONE);
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
                        mDatabase.deleteStudyMaterial(studyMaterial);
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
