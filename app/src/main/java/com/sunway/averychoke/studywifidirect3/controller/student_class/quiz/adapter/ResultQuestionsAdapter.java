package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.adapter;

import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.quiz.QuestionViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.common_class.quiz.QuestionsAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.CellResultBinding;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;

import java.util.List;

/**
 * Created by AveryChoke on 4/4/2017.
 */

public class ResultQuestionsAdapter extends QuestionsAdapter {
    private static final int QUESTION_VH = 101;
    private static final int RESULT_VH = 102;

    @Override
    public int getItemViewType(int position) {
        if (super.getItemCount() > 0 && position == super.getItemCount()) {
            return RESULT_VH;
        } else {
            return QUESTION_VH;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == QUESTION_VH) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_question, parent, false);
            return new ResultQuestionViewHolder(rootView);
        } else {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_result, parent, false);
            return new ResultViewHolder(rootView, getQuestions());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == QUESTION_VH) {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        // +1 include the result section
        return super.getItemCount() > 0 ? super.getItemCount() + 1 : 0;
    }

    // region View Holder
    static class ResultQuestionViewHolder extends QuestionViewHolder {

        public ResultQuestionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setupShortQuestionView() {
            getBinding().shortQuestion.answerEditText.setText("");
            getBinding().shortQuestion.answerEditText.setEnabled(false);
        }

        @Override
        protected void setupMCQView() {

        }

        @Override
        public void setQuestion(Question question) {
            super.setQuestion(question);

            if (question != null) {
                getBinding().marksTextView.setText(String.format("%d/%d",
                        question.checkAnswer() ? question.getTotalMarks() : 0,
                        question.getTotalMarks()));

                if (!(question instanceof ChoiceQuestion)) {
                    getBinding().shortQuestion.correctAnswerTextView.setText(
                            itemView.getResources().getString(R.string.short_question_correct_answer, question.getCorrectAnswer()));
                    getBinding().shortQuestion.correctAnswerTextView.setTextColor(
                            question.checkAnswer() ? Color.GREEN : Color.RED
                    );
                }
            }
        }

        @Override
        protected AppCompatRadioButton choicesRadioButton(ChoiceQuestion choiceQuestion, int index) {
            AppCompatRadioButton radioButton = super.choicesRadioButton(choiceQuestion, index);
            radioButton.setEnabled(false);

            String choice = choiceQuestion.getChoices().get(index);
            if (choiceQuestion.checkAnswer(choice)) {
                changeColor(radioButton, choiceQuestion.checkAnswer() ? Color.GREEN : Color.RED);
            }
            return radioButton;
        }

        private void changeColor(AppCompatRadioButton radioButton, int color) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{color, color}
            );
            CompoundButtonCompat.setButtonTintList(radioButton, colorStateList);
            radioButton.setTextColor(color);
        }
    }
    // endregion

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private CellResultBinding mBinding;

        public ResultViewHolder(View itemView, List<Question> questions) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            // calculate marks
            int totalMarks = 0;
            int userMarks = 0;
            for (Question question : questions) {
                if (question.checkAnswer()) {
                    userMarks += question.getTotalMarks();
                }
                totalMarks += question.getTotalMarks();
            }
            double marksPercentage = (double) userMarks / totalMarks * 100;

            mBinding.marksTextView.setText(String.format("%d / %d", userMarks, totalMarks));
            mBinding.marksPercentageTextView.setText(String.format("(%.2f%%)", marksPercentage));
        }
    }
    // endregion
}
