package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.AnswerQuizActivity;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import static android.app.Activity.RESULT_OK;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class StudentQuizFragment extends SWDBaseFragment implements
        StudentQuizzesAdapter.StudentQuizViewHolder.OnCheckSelectListener {
    public static final int ANSWER_QUIZ_CODE = 101;

    private StudentManager sManager;
    private DatabaseHelper mDatabase;
    private StudentQuizzesAdapter mAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mDatabase = new DatabaseHelper(getContext());
        mAdapter = new StudentQuizzesAdapter(this);
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
        mBinding.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getQuizzes());

        mBinding.addButton.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ANSWER_QUIZ_CODE:
                if (resultCode == RESULT_OK) {
                    Quiz quiz = data.getParcelableExtra(AnswerQuizActivity.ARGS_QUIZ_KEY);
                    if (quiz != null) {
                        mAdapter.replaceClassMaterial(quiz);
                    }
                }
        }
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        Quiz quiz = (Quiz) classMaterial;
        Intent intent = new Intent(getActivity(), AnswerQuizActivity.class);
        intent.putExtra(AnswerQuizActivity.ARGS_QUIZ_KEY, (Parcelable) quiz);
        startActivityForResult(intent, ANSWER_QUIZ_CODE);
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
                        mAdapter.removeClassMaterial(index);
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

    }

    @Override
    public void onCheckLongClicked(@NonNull final Quiz quiz, @NonNull final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_reset_answer_title)
                .setMessage(R.string.dialog_reset_answer_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // reset quiz answer data
                        quiz.setAnswered(false);
                        for (Question question : quiz.getQuestions()) {
                            question.setUserAnswer("");
                        }

                        mAdapter.notifyItemChanged(index);
                        mDatabase.updateQuizAnswers(quiz);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    // endregion class material view holder
}
