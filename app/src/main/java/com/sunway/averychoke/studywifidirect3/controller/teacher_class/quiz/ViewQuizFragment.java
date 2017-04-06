package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.common_class.QuestionsAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentViewQuizBinding;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

/**
 * Created by AveryChoke on 6/4/2017.
 */

public class ViewQuizFragment extends SWDBaseFragment {
    public static final String ARGS_QUIZ_KEY = "args_quiz_key";

    private FragmentViewQuizBinding mBinding;

    private Quiz mQuiz;
    private QuestionsAdapter mAdapter;

    public static ViewQuizFragment newInstance(Quiz quiz) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_QUIZ_KEY, quiz);
        
        ViewQuizFragment fragment = new ViewQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuiz = getArguments().getParcelable(ARGS_QUIZ_KEY);
        // reset answers
        for (Question question : mQuiz.getQuestions()) {
            question.setUserAnswer("");
        }
        mAdapter = new QuestionsAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_quiz, container, false);
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
}
