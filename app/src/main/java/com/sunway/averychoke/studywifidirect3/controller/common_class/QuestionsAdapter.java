package com.sunway.averychoke.studywifidirect3.controller.common_class;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.databinding.CellQuestionBinding;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 3/4/2017.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Question> mQuestions;

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
            ((HasQuestion) holder).setQuestion(mQuestions.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public void setQuestions(List<Question> questions) {
        mQuestions.clear();
        mQuestions.addAll(questions);
        notifyDataSetChanged();
    }

    // region get set
    protected List<Question> getQuestions() {
        return mQuestions;
    }
    // endregion
}
