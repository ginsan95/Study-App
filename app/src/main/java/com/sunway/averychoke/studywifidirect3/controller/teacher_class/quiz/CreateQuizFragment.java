package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.adapter.CreateQuestionAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentCreateQuizBinding;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

/**
 * Created by AveryChoke on 11/2/2017.
 */

public class CreateQuizFragment extends SWDBaseFragment implements
        CreateQuestionAdapter.CreateQuestionViewHolder.OnQuestionChangeListener {

    public static final String ARGS_QUIZ_KEY = "quiz_key";
    public static final String ARGS_TYPE_KEY = "type_key";
    public static final int TYPE_CREATE = 101;
    public static final int TYPE_EDIT = 102;

    private TeacherManager sManager;
    private Quiz mQuiz;
    private int mType;
    private String mOldName;

    private FragmentCreateQuizBinding mBinding;
    private CreateQuestionAdapter mCreateQuestionAdapter;

    public static CreateQuizFragment newInstance(int type, @Nullable Quiz quiz) {
        Bundle args = new Bundle();
        if (quiz != null) {
            args.putParcelable(ARGS_QUIZ_KEY, quiz);
        }
        args.putInt(ARGS_TYPE_KEY, type);

        CreateQuizFragment fragment = new CreateQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuiz = getArguments().getParcelable(ARGS_QUIZ_KEY);
        if (mQuiz == null) {
            mQuiz = new Quiz("");
        } else {
            mOldName = mQuiz.getName();
        }
        mType = getArguments().getInt(ARGS_TYPE_KEY);

        sManager = TeacherManager.getInstance();
        mCreateQuestionAdapter = new CreateQuestionAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_quiz, container, false);
        mBinding = DataBindingUtil.bind(rootView);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.quizzesRecyclerView.setAdapter(mCreateQuestionAdapter);
        if (mQuiz.getQuestions().size() > 0) {
            mCreateQuestionAdapter.setQuestions(mQuiz.getQuestions());
        } else {
            // add a single question
            mCreateQuestionAdapter.addQuestion(new Question());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_quiz, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quiz_add_question:
                mCreateQuestionAdapter.addQuestion(new Question());
                mBinding.quizzesRecyclerView.smoothScrollToPosition(mCreateQuestionAdapter.getItemCount()-1);
                return true;
            case R.id.quiz_save_quiz:
                saveQuiz();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveQuiz() {
        EditText titleEditText = (EditText) getActivity().findViewById(R.id.title_edit_text);
        if (handleEmptyET(titleEditText)) {
            String title = titleEditText.getText().toString();

            // error checking
            if (mCreateQuestionAdapter.getQuestions().size() <= 0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Please ensure that there is at least 1 question.")
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return;
            }
            if (sManager.isQuizNameConflicting(title, mOldName)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Invalid quiz name")
                        .setMessage("The quiz name is already used by other quizzes. Please use a different name.")
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return;
            }

            mQuiz.setName(title);
            // add all the created questions into the quiz
            mQuiz.getQuestions().clear();
            mQuiz.getQuestions().addAll(mCreateQuestionAdapter.getQuestions());

            // boolean to check if add / update successfully
            boolean success = mType == TYPE_CREATE ? sManager.addQuiz(mQuiz) : sManager.updateQuiz(mQuiz, mOldName);
            if (success) {
                // send data back
                Intent intent = new Intent();
                intent.putExtra(ARGS_QUIZ_KEY, (Parcelable) mQuiz);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Save failed")
                        .setMessage("Failed to save the quiz into the database. Please try again later.")
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        }
    }


    // region create question view holder
    @Override
    public void onQuestionTypeSelected(CreateQuestionAdapter.QuestionType questionType, @NonNull Question question, @NonNull int index) {
        switch (questionType) {
            case SHORT_QUESTION:
                if (question instanceof ChoiceQuestion) {
                    mCreateQuestionAdapter.changeQuestion(index, Question.cloneFrom(question));
                }
                break;
            case MCQ:
                if (!(question instanceof ChoiceQuestion)) {
                    mCreateQuestionAdapter.changeQuestion(index, ChoiceQuestion.cloneFrom(question));
                }
                break;
            case REMOVE:
                mCreateQuestionAdapter.removeQuestion(index);
                break;
        }
    }

    @Override
    public void onAddChoicesClicked(final ChoiceQuestion choiceQuestion, final int index) {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_choice_dialog_title)
                .setView(editText)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = editText.getText().toString();
                        if (!TextUtils.isEmpty(choice.trim())) {
                            choiceQuestion.getChoices().add(choice);
                            if (choiceQuestion.getChoices().size() == 1) {
                                choiceQuestion.setCorrectAnswer(choice);
                            }
                            mCreateQuestionAdapter.notifyItemChanged(index);
                        }
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
    public void onEditChoiceClicked(final ChoiceQuestion choiceQuestion, final int index, final int choiceIndex) {
        final EditText editText = new EditText(getContext());
        editText.setText(choiceQuestion.getChoices().get(choiceIndex));

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_choice_dialog_title)
                .setView(editText)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = editText.getText().toString();
                        if (!TextUtils.isEmpty(choice.trim())) {
                            if (choiceQuestion.checkAnswer(choiceQuestion.getChoices().get(choiceIndex))) {
                                choiceQuestion.setCorrectAnswer(choice);
                            }
                            choiceQuestion.getChoices().set(choiceIndex, choice);
                            mCreateQuestionAdapter.notifyItemChanged(index);
                        }
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
    public void onChoiceLongClicked(final ChoiceQuestion choiceQuestion, final int index, final int choiceIndex) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_choice_dialog_title)
                .setMessage(R.string.delete_choice_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = choiceQuestion.getChoices().get(choiceIndex);
                        choiceQuestion.getChoices().remove(choiceIndex);
                        if (choiceQuestion.getChoices().size() <= 0) {
                            choiceQuestion.setCorrectAnswer("");
                        } else if (choiceQuestion.checkAnswer(choice)) {
                            choiceQuestion.setCorrectAnswer(choiceQuestion.getChoices().get(0));
                        }
                        mCreateQuestionAdapter.notifyItemChanged(index);
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
    // endregion create question view holder
}
