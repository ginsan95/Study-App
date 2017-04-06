package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.adapter.AnswerQuestionsAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentAnswerQuizBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class AnswerQuizFragment extends SWDBaseFragment implements AnswerQuestionsAdapter.SubmitViewHolder.OnSubmitListener {
    public static final String ARGS_QUIZ_KEY = "args_quiz_key";

    private FragmentAnswerQuizBinding mBinding;

    private StudentManager sManager;
    private Quiz mQuiz;
    private AnswerQuestionsAdapter mAdapter;

    public static AnswerQuizFragment newInstance(Quiz quiz) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_QUIZ_KEY, quiz);

        AnswerQuizFragment fragment = new AnswerQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuiz = getArguments().getParcelable(ARGS_QUIZ_KEY);
        mAdapter = new AnswerQuestionsAdapter(this);
        sManager = StudentManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answer_quiz, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.questionsRecyclerView.setAdapter(mAdapter);
        mAdapter.setQuestions(mQuiz.getQuestions());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    // region View Holder Listener
    @Override
    public void onSubmit() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_quiz_submisson_title)
                .setMessage(R.string.dialog_quiz_submission_message)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mQuiz.setAnswered(true);
                        sManager.updateQuizAnswer(mQuiz);

                        // return data to activity
                        Intent intent = new Intent();
                        intent.putExtra(ARGS_QUIZ_KEY, (Parcelable) mQuiz);
                        getActivity().setResult(Activity.RESULT_OK, intent);

                        getBaseActivity().changeFragment(QuizResultFragment.newInstance(mQuiz));
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    // endregion
}
