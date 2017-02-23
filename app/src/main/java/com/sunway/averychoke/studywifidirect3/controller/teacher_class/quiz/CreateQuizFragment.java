package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentCreateQuizBinding;
import com.sunway.averychoke.studywifidirect3.model.ChoiceQuestion;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;

/**
 * Created by AveryChoke on 11/2/2017.
 */

public class CreateQuizFragment extends SWDBaseFragment implements
        CreateQuestionAdapter.CreateQuestionViewHolder.OnQuestionChangeListener {

    public static final String NEW_QUIZ_KEY = "new_quiz_key";

    private DatabaseHelper mDatabase;
    private FragmentCreateQuizBinding mBinding;
    private CreateQuestionAdapter mCreateQuestionAdapter;

    private String mClassName;

    public static CreateQuizFragment newInstance(String className) {
        Bundle args = new Bundle();
        args.putString(CreateQuizActivity.CLASS_NAME_KEY, className);

        CreateQuizFragment fragment = new CreateQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassName = getArguments().getString(CreateQuizActivity.CLASS_NAME_KEY);

        mDatabase = new DatabaseHelper(getContext());
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
        String title = titleEditText.getText().toString();
        if (!TextUtils.isEmpty(title.trim())) {
            Quiz quiz = new Quiz(title);
            // add all the created questions into the quiz
            quiz.getQuestions().addAll(mCreateQuestionAdapter.getQuestions());

            // save the quiz to database
            mDatabase.addQuiz(quiz, mClassName);

            // send data back
            Intent intent = new Intent();
            intent.putExtra(NEW_QUIZ_KEY, (Parcelable) quiz);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
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
                                choiceQuestion.setAnswer(choice);
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
                                choiceQuestion.setAnswer(choice);
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
                            choiceQuestion.setAnswer("");
                        } else if (choiceQuestion.checkAnswer(choice)) {
                            choiceQuestion.setAnswer(choiceQuestion.getChoices().get(0));
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
