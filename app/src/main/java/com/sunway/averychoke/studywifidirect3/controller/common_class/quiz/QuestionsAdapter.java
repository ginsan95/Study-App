package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 3/4/2017.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Question> mQuestions;
    private int mLastShortQuestionIndex = -1;

    public QuestionsAdapter() {
        mQuestions = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_question, parent, false);
        QuestionViewHolder viewHolder = new QuestionViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HasQuestion) {
            HasQuestion questionVH = ((HasQuestion) holder);
            questionVH.setQuestion(mQuestions.get(position), mLastShortQuestionIndex);
        }
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public void setQuestions(List<Question> questions) {
        mQuestions.clear();
        mQuestions.addAll(questions);
        findLastShortQuestionIndex();
        notifyDataSetChanged();
    }

    private void findLastShortQuestionIndex() {
        for (int i = 0; i < mQuestions.size(); i++) {
            if (!(mQuestions.get(i) instanceof ChoiceQuestion)) {
                mLastShortQuestionIndex = i;
            }
        }
    }

    // region get set
    protected List<Question> getQuestions() {
        return mQuestions;
    }
    // endregion
}
