package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.CreateQuizActivity;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.CreateQuizFragment;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherQuizFragment extends SWDBaseFragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener {

    private static final String CLASS_NAME_KEY = "class_name_key";
    private static final String QUIZZES_KEY = "quizzes_key";
    private static final int CREATE_QUIZ_CODE = 1;

    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mClassMaterialAdapter;
    private String mClassName;
    private List<Quiz> mQuizzes;

    private FragmentClassMaterialBinding mBinding;

    public static TeacherQuizFragment newInstance(String className, List<Quiz> quizzes) {
        TeacherQuizFragment teacherQuizFragment = new TeacherQuizFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(QUIZZES_KEY, (ArrayList<Quiz>) quizzes);
        args.putString(CLASS_NAME_KEY, className);

        teacherQuizFragment.setArguments(args);
        return teacherQuizFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassName = getArguments().getString(CLASS_NAME_KEY);
        mQuizzes = getArguments().getParcelableArrayList(QUIZZES_KEY);

        mDatabase = new DatabaseHelper(getContext());
        mClassMaterialAdapter = new ClassMaterialAdapter(true, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_material, container, false);
        mBinding = DataBindingUtil.bind(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.materialsSwipeRefreshLayout.setEnabled(false);

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mClassMaterialAdapter);
        mClassMaterialAdapter.setClassMaterials(mQuizzes);

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateQuizActivity.class);
                intent.putExtra(CreateQuizActivity.CLASS_NAME_KEY, mClassName);
                startActivityForResult(intent, CREATE_QUIZ_CODE);


//                String[] strings = {"babi", "cina", "sohai"};
//                Random rand = new Random();
//                Quiz quiz = new Quiz(strings[rand.nextInt(3)]);
//
//                long errorCode = mDatabase.addQuiz(quiz, mClassName);
//                if (errorCode != -1) {
//                    mClassMaterialAdapter.addClassMaterial(quiz);
//                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CREATE_QUIZ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // update the adapter
                    Quiz quiz = data.getParcelableExtra(CreateQuizFragment.NEW_QUIZ_KEY);
                    if (quiz != null) {
                        mClassMaterialAdapter.addClassMaterial(quiz);
                    }
                }
                break;
        }
    }

    // region class material view holder
    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        Quiz quiz = (Quiz) classMaterial;
        Toast.makeText(getContext(), quiz.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull final ClassMaterial classMaterial, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_quiz_dialog_title)
                .setMessage(R.string.delete_quiz_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Quiz quiz = (Quiz) classMaterial;
                        mClassMaterialAdapter.removeClassMaterial(index);
                        mDatabase.deleteQuiz(quiz);
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
    public void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked) {
        Quiz quiz = (Quiz) classMaterial;
        quiz.setVisible(isChecked);
        mDatabase.updateQuizVisible(quiz);
    }
    // endregion class material view holder
}
