package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.databinding.CellCreateQuestionBinding;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 11/2/2017.
 */

public class CreateQuestionAdapter extends RecyclerView.Adapter<CreateQuestionAdapter.CreateQuestionViewHolder> {

    private CreateQuestionViewHolder.OnQuestionChangeListener mListener;

    private final List<Question> mQuestions = new ArrayList<>();

    public CreateQuestionAdapter(CreateQuestionViewHolder.OnQuestionChangeListener listener) {
        mListener = listener;
    }

    @Override
    public CreateQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_create_question, parent, false);
        CreateQuestionViewHolder viewHolder = new CreateQuestionViewHolder(rootView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CreateQuestionViewHolder holder, int position) {
        Question question = null;
        question = mQuestions.get(position);
        if (question != null) {
            holder.setQuestion(question);
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

    public void addQuestion(Question question) {
        mQuestions.add(question);
        notifyItemInserted(mQuestions.indexOf(question));
    }

    public void changeQuestion(int index, Question question) {
        mQuestions.set(index, question);
        notifyItemChanged(index);
    }

    public void removeQuestion(int index) {
        mQuestions.remove(index);
        notifyItemRemoved(index);
    }

    // region get set
    protected List<Question> getQuestions() {
        return mQuestions;
    }
    // endregion get set


    // region view holder class
    static class CreateQuestionViewHolder extends RecyclerView.ViewHolder {

        public interface OnQuestionChangeListener {
            void onQuestionTypeSelected(QuestionType questionType, @NonNull Question question, @NonNull int index);

            // MCQ
            void onAddChoicesClicked(ChoiceQuestion choiceQuestion, int index);

            void onEditChoiceClicked(ChoiceQuestion choiceQuestion, int index, int choiceIndex);

            void onChoiceLongClicked(ChoiceQuestion choiceQuestion, int index, int choiceIndex);
        }

        private Question mQuestion;

        private CellCreateQuestionBinding mBinding;
        private OnQuestionChangeListener mListener;

        public CreateQuestionViewHolder(View itemView, final OnQuestionChangeListener listener) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
            mListener = listener;

            List<String> questionTypesString = new ArrayList<>();
            for (QuestionType type : QuestionType.values()) {
                questionTypesString.add(type.simpleName());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, questionTypesString);
            mBinding.typeSpinner.setAdapter(dataAdapter);

            mBinding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mQuestion != null) {
                        listener.onQuestionTypeSelected(QuestionType.getByOrdinal(position), mQuestion, getAdapterPosition());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            mBinding.questionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mQuestion.setQuestion(s.toString());
                }
            });

            mBinding.marksEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mQuestion.setTotalMarks(Double.parseDouble(s.toString()));
                }
            });

            setupShortQuestionView();
            setupMCQView();
        }

        private void setupShortQuestionView() {
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
                    mQuestion.setAnswer(s.toString());
                }
            });
        }

        private void setupMCQView() {
            mBinding.mcq.addChoicesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mQuestion instanceof ChoiceQuestion) {
                        mListener.onAddChoicesClicked((ChoiceQuestion) mQuestion, getAdapterPosition());
                    }
                }
            });

            mBinding.mcq.choicesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (mQuestion instanceof ChoiceQuestion) {
                        int index = mBinding.mcq.choicesRadioGroup.indexOfChild(itemView.findViewById(checkedId));
                        if (index > 0) {
                            RadioButton radioButton = (RadioButton) mBinding.mcq.choicesRadioGroup.getChildAt(index);
                            mQuestion.setAnswer(radioButton.getText().toString());
                        }
                    }
                }
            });
        }

        private void setQuestion(Question question) {
            mQuestion = question;

            mBinding.questionEditText.setText(question.getQuestion());
            mBinding.marksEditText.setText(String.valueOf(question.getTotalMarks()));

            mBinding.shortQuestion.containerLayout.setVisibility(View.GONE);
            mBinding.mcq.containerLayout.setVisibility(View.GONE);

            if (question instanceof ChoiceQuestion) {
                mBinding.typeSpinner.setSelection(QuestionType.MCQ.ordinal());
                mBinding.mcq.containerLayout.setVisibility(View.VISIBLE);

                //mBinding.mcq.choicesRadioGroup.clearCheck();
                mBinding.mcq.choicesRadioGroup.removeAllViews();
                ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
                for (int i=0; i<choiceQuestion.getChoices().size(); i++) {
                    mBinding.mcq.choicesRadioGroup.addView(choicesRadioButton(choiceQuestion, i));
                }
            } else {
                mBinding.typeSpinner.setSelection(QuestionType.SHORT_QUESTION.ordinal());
                mBinding.shortQuestion.containerLayout.setVisibility(View.VISIBLE);

                mBinding.shortQuestion.answerEditText.setText(mQuestion.getAnswer());
            }
        }

        private RadioButton choicesRadioButton(final ChoiceQuestion choiceQuestion, final int index) {
            final RadioButton radioButton =  new RadioButton(itemView.getContext());
            // example - question no.1, 2nd choice - 101
            radioButton.setId((getAdapterPosition()+1)*100 + index);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));

            String choice = choiceQuestion.getChoices().get(index);
            radioButton.setText(choice);
            if (choiceQuestion.getAnswer().equals(choice)) {
                radioButton.setChecked(true);
            } else if (choiceQuestion.getAnswer().equals("") && index == 0) {
                radioButton.setChecked(true);
            }

            // edit click
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
            } else {
                radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
            }
            radioButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (radioButton.getRight() - radioButton.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            mListener.onEditChoiceClicked(choiceQuestion, getAdapterPosition(), index);
                            return true;
                        }
                    }
                    return false;
                }
            });

            // long click - delete
            radioButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onChoiceLongClicked(choiceQuestion, getAdapterPosition(), index);
                    return true;
                }
            });

            return radioButton;
        }
    }
    // endregion view holder class


    // region question type enum
    public enum QuestionType {
        SHORT_QUESTION, MCQ, REMOVE;

        public String simpleName() {
            switch (this) {
                case SHORT_QUESTION:
                    return "Short Question";
                case MCQ:
                    return name();
                case REMOVE:
                    return "Remove";
                default:
                    return "";
            }
        }

        public static QuestionType getByOrdinal(int ordinal) {
            for (QuestionType type : QuestionType.values()) {
                if (type.ordinal() == ordinal) {
                    return type;
                }
            }
            return null;
        }
    }
    // endregion question type enum
}