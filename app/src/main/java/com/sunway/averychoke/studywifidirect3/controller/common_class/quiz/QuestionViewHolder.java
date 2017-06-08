package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sunway.averychoke.studywifidirect3.databinding.CellQuestionBinding;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;

/**
 * Created by AveryChoke on 4/4/2017.
 */

public class QuestionViewHolder extends RecyclerView.ViewHolder implements HasQuestion {

    private CellQuestionBinding mBinding;
    private Question mQuestion;

    public QuestionViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);

        setupShortQuestionView();
        setupMCQView();
    }

    protected void setupShortQuestionView() {
        mBinding.shortQuestion.correctAnswerTextView.setVisibility(View.GONE);
        mBinding.shortQuestion.answerEditText.setText("");
        mBinding.shortQuestion.answerEditText.setMaxLines(Integer.MAX_VALUE);
        mBinding.shortQuestion.answerEditText.setHorizontallyScrolling(false);
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

    protected void setupMCQView() {
        mBinding.mcq.choicesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mQuestion instanceof ChoiceQuestion) {
                    int index = mBinding.mcq.choicesRadioGroup.indexOfChild(itemView.findViewById(checkedId));
                    if (index >= 0) {
                        RadioButton radioButton = (RadioButton) mBinding.mcq.choicesRadioGroup.getChildAt(index);
                        mQuestion.setUserAnswer(radioButton.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void setQuestion(Question question, int lastIndex) {
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
                mBinding.shortQuestion.answerEditText.setImeOptions(EditorInfo.IME_ACTION_NONE);

                mBinding.mcq.choicesRadioGroup.removeAllViews();
                ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
                for (int i=0; i<choiceQuestion.getChoices().size(); i++) {
                    mBinding.mcq.choicesRadioGroup.addView(choicesRadioButton(choiceQuestion, i));
                }
            } else {
                mBinding.shortQuestion.containerLayout.setVisibility(View.VISIBLE);
                mBinding.shortQuestion.answerEditText.setImeOptions(lastIndex == getAdapterPosition() ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_UNSPECIFIED);

                mBinding.shortQuestion.answerEditText.setText(question.getUserAnswer());
            }
        }
    }

    protected AppCompatRadioButton choicesRadioButton(final ChoiceQuestion choiceQuestion, final int index) {
        final AppCompatRadioButton radioButton = new AppCompatRadioButton(itemView.getContext());
        // example - question no.1, 2nd choice - 101
        radioButton.setId((getAdapterPosition() + 1) * 100 + index);
        radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioButton.setTextSize(14);

        String choice = choiceQuestion.getChoices().get(index);
        radioButton.setText(choice);
        if (choiceQuestion.getUserAnswer().equals(choice)) {
            radioButton.setChecked(true);
        }

        return radioButton;
    }

    // region get set
    protected CellQuestionBinding getBinding() {
        return mBinding;
    }

    protected Question getQuestion() {
        return mQuestion;
    }
    // endregion
}
