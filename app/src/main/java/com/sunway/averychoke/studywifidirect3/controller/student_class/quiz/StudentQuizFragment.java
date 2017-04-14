package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.common_class.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsUpdaterListener;
import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.controller.connection.DownloadException;
import com.sunway.averychoke.studywifidirect3.controller.connection.TeacherThread;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class StudentQuizFragment extends SWDBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        ClassMaterialsUpdaterListener {
    public static final int ANSWER_QUIZ_CODE = 101;

    private StudentManager sManager;
    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mDatabase = new DatabaseHelper(getContext());
        mAdapter = new ClassMaterialAdapter(false, this);
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
        mBinding.materialsRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassMaterials(sManager.getQuizzes());

        mBinding.addButton.setVisibility(View.GONE);

        if (!sManager.isOffline()) {
            mBinding.materialsSwipeRefreshLayout.setOnRefreshListener(this);
            onRefresh();
        } else {
            mBinding.materialsSwipeRefreshLayout.setEnabled(false);
        }
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

    @Override
    public void onRefresh() {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(true);
        ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this);
        task.execute(TeacherThread.Request.QUIZZES);
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull final ClassMaterial classMaterial) {
        switch (classMaterial.getStatus()) {
            case DOWNLOADING:
                // // TODO: 10/4/2017 view download progress
                return;
            case ERROR:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_conflict_title)
                        .setMessage(R.string.dialog_conflict_message)
                        .setPositiveButton(R.string.dialog_download_from_teacher, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadQuiz((Quiz) classMaterial);
                            }
                        })
                        .setNeutralButton(R.string.dialog_ignore_and_continue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                answerQuiz(classMaterial);
                            }
                        })
                        .setNegativeButton(R.string.dialog_use_my_version, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sManager.updateQuizStatus((Quiz) classMaterial, ClassMaterial.Status.NORMAL);
                                mAdapter.replaceClassMaterial(classMaterial);
                            }
                        })
                        .show();
                break;
            default:
                answerQuiz(classMaterial);
                break;
        }
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        if (classMaterial.getStatus() == ClassMaterial.Status.DOWNLOADING) {
            return;
        }

        // region setup options
        final Quiz quiz = (Quiz) classMaterial;
        final List<CharSequence> options = new ArrayList<>();
        if (!sManager.isOffline()) {
            options.add(getString(R.string.option_download));
        }
        if (quiz.isAnswered()) {
            options.add(getString(R.string.option_reset_answer));
        }
        options.add(getString(R.string.option_delete_quiz));
        // endregion

        new AlertDialog.Builder(getContext())
                .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String option = options.get(which).toString();
                        if (option.equals(getString(R.string.option_download))) {
                            downloadQuiz(quiz);
                        } else if (option.equals(getString(R.string.option_reset_answer))) {
                            resetAnswer(quiz, index);
                        } else if (option.equals(getString(R.string.option_delete_quiz))) {
                            deleteQuiz(quiz, index);
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
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);

        mAdapter.replaceClassMaterial(classMaterial);

        if (getContext() != null) {
            Toast.makeText(getContext(), "Downloaded " + classMaterial.getName() + "successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassMaterialsUpdated() {
        mAdapter.setClassMaterials(sManager.getQuizzes());
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(Exception e) {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);

        if (e instanceof DownloadException) {
            ClassMaterial classMaterial = ((DownloadException) e).getClassMaterial();
            if (classMaterial instanceof Quiz) {
                Quiz quiz = (Quiz) classMaterial;
                sManager.updateQuizStatus(quiz, ClassMaterial.Status.NORMAL);
                mAdapter.replaceClassMaterial(quiz);
            }
        }

        if (getContext() != null) {
            Toast.makeText(getContext(), e != null ? e.toString() : "Error", Toast.LENGTH_SHORT).show();
        }
    }
    // endregion

    private void answerQuiz(ClassMaterial classMaterial) {
        Quiz quiz = (Quiz) classMaterial;
        Intent intent = new Intent(getActivity(), AnswerQuizActivity.class);
        intent.putExtra(AnswerQuizActivity.ARGS_QUIZ_KEY, (Parcelable) quiz);
        startActivityForResult(intent, ANSWER_QUIZ_CODE);
    }

    private void downloadQuiz(Quiz quiz) {
        if (!sManager.isOffline()) {
            sManager.updateQuizStatus(quiz, ClassMaterial.Status.DOWNLOADING);
            mAdapter.replaceClassMaterial(quiz);
            ClassMaterialsRequestTask task = new ClassMaterialsRequestTask(sManager.getTeacherAddress(), this, quiz);
            task.execute(TeacherThread.Request.QUIZ, quiz.getName());
        }
    }

    private void resetAnswer(final Quiz quiz, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.option_reset_answer)
                .setMessage(R.string.dialog_reset_answer_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

    private void deleteQuiz(final Quiz quiz, final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.option_delete_quiz)
                .setMessage(R.string.delete_quiz_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeClassMaterial(index);
                        sManager.deleteQuiz(quiz);
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
