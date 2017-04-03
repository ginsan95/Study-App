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
        if (holder instanceof QuestionViewHolder) {
            ((QuestionViewHolder) holder).setQuestion(mQuestions.get(position));
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

    // region view holder class
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        private CellQuestionBinding mBinding;
        private Question mQuestion;

        public QuestionViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);

            setupShortQuestionView();
            setupMCQView();
        }

        private void setupShortQuestionView() {
            mBinding.shortQuestion.correctAnswerTextView.setVisibility(View.GONE);
            mBinding.shortQuestion.answerEditText.setText("");
            mBinding.shortQuestion.answerEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mQuestion.setUserAnswer(s.toString());
                }
            });
        }

        private void setupMCQView() {
            mBinding.mcq.choicesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (mQuestion instanceof ChoiceQuestion) {
                        int index = mBinding.mcq.choicesRadioGroup.indexOfChild(itemView.findViewById(checkedId));
                        if (index > 0) {
                            RadioButton radioButton = (RadioButton) mBinding.mcq.choicesRadioGroup.getChildAt(index);
                            mQuestion.setUserAnswer(radioButton.getText().toString());
                        }
                    }
                }
            });
        }

        private void setQuestion(Question question) {
            mQuestion = question;
            mBinding.numberTextView.setText((getAdapterPosition()+1) + ") ");

            if (question != null) {
                mBinding.questionTextView.setText(question.getQuestion());
                mBinding.marksTextView.setText(String.format("0/%d", question.getTotalMarks()));
                mBinding.shortQuestion.containerLayout.setVisibility(View.GONE);
                mBinding.mcq.containerLayout.setVisibility(View.GONE);

                // check if is short question or mcq
                if (question instanceof ChoiceQuestion) {
                    mBinding.mcq.containerLayout.setVisibility(View.VISIBLE);

                    mBinding.mcq.choicesRadioGroup.removeAllViews();
                    ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
                    for (int i=0; i<choiceQuestion.getChoices().size(); i++) {
                        mBinding.mcq.choicesRadioGroup.addView(choicesRadioButton(choiceQuestion, i));
                    }
                } else {
                    mBinding.shortQuestion.containerLayout.setVisibility(View.VISIBLE);

                    mBinding.shortQuestion.answerEditText.setText(question.getUserAnswer());
                }
            }
        }

        private RadioButton choicesRadioButton(final ChoiceQuestion choiceQuestion, final int index) {
            final RadioButton radioButton = new RadioButton(itemView.getContext());
            // example - question no.1, 2nd choice - 101
            radioButton.setId((getAdapterPosition() + 1) * 100 + index);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));

            String choice = choiceQuestion.getChoices().get(index);
            radioButton.setText(choice);
            if (choiceQuestion.getUserAnswer().equals(choice)) {
                radioButton.setChecked(true);
            }

            return radioButton;
        }
    }
    // endregion
}
