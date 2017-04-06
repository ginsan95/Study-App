package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.CreateQuizActivity;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.CreateQuizFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.ViewQuizActivity;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherQuizFragment extends SWDBaseFragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener {

    private static final int CREATE_QUIZ_CODE = 1;

    private TeacherManager sManager;
    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mClassMaterialAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = TeacherManager.getInstance();
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
        mClassMaterialAdapter.setClassMaterials(sManager.getQuizzes());

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateQuizActivity.class);
                startActivityForResult(intent, CREATE_QUIZ_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CREATE_QUIZ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // update the adapter
                    Quiz quiz = data.getParcelableExtra(CreateQuizFragment.NEW_QUIZ_KEY);
                    if (quiz != null) {
                        mClassMaterialAdapter.addClassMaterial(quiz);
                    }
                }
                break;
        }
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        final Quiz quiz = (Quiz) classMaterial;

        final CharSequence[] choices = new CharSequence[] {
                "View Quiz",
                "Edit Quiz"
        };

        new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // View Quiz
                                Intent intent = new Intent(getActivity(), ViewQuizActivity.class);
                                intent.putExtra(ViewQuizActivity.ARGS_QUIZ_KEY, (Parcelable)quiz);
                                startActivity(intent);
                                break;
                            case 1: // Edit Quiz
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_quiz_dialog_title)
                .setMessage(R.string.delete_quiz_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Quiz quiz = (Quiz) classMaterial;
                        mClassMaterialAdapter.removeClassMaterial(index);
                        mDatabase.deleteClassMaterial(quiz);
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
        Quiz quiz = (Quiz) classMaterial;
        quiz.setVisible(isChecked);
        mDatabase.updateClassMaterialVisible(quiz);
    }
    // endregion class material view holder
}
