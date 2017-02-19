package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.controller.MainActivity;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.TeacherClassFragment;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassBinding;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AveryChoke on 22/1/2017.
 */

public class ClassFragment extends Fragment implements
        ClassAdapter.ClassViewHolder.OnClassSelectListener,
        SwipeRefreshLayout.OnRefreshListener {

    private DatabaseHelper mDatabase;
    private ClassAdapter mClassAdapter;

    private FragmentClassBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DatabaseHelper(getContext());
        mClassAdapter = new ClassAdapter(this);

        // set the id counter for the model objects
        Quiz.mCounter = mDatabase.getQuizMaxId();
        Question.mCounter = mDatabase.getQuestionMaxId();
        StudyMaterial.mCounter = mDatabase.getStudyMaterialMaxId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        mBinding.classesSwipeRefreshLayout.setOnRefreshListener(this);
        getActivity().setTitle("Classes");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onRefresh();

        mBinding.classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.classesRecyclerView.setAdapter(mClassAdapter);

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });
    }

    // region swipe refresh layout
    @Override
    public void onRefresh() {
        mBinding.classesSwipeRefreshLayout.setRefreshing(true);
        // // TODO: search for broadcasted classes

        // example
        List<String> classesName = mDatabase.getAllClassesName();
        mClassAdapter.setClassesName(classesName);

        mBinding.classesSwipeRefreshLayout.setRefreshing(false);
    }
    // endregion swipe refresh layout

    // region class view holder
    @Override
    public void onClassSelected(final String className) {
        // get class from database
        final StudyClass studyClass = mDatabase.getClass(className);

        final CharSequence[] choices = new CharSequence[] {
                getString(R.string.option_host_class),
                getString(R.string.option_participate_class),
                getString(R.string.option_view_class)
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Host class
                                TeacherClassFragment teacherClassFragment = TeacherClassFragment.newInstance(className);

                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.changeFragment(teacherClassFragment);
                                break;
                            case 1: // Participate class

                                break;
                            case 2: // View edit class

                                break;
                        }

//                        Toast.makeText(getContext(),
//                                String.format("%s for %s with %d quizzes and %d materials",
//                                        choices[which], studyClass.getName(), studyClass.getQuizzes().size(), studyClass.getStudyMaterials().size()),
//                                Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }

    @Override
    public void onClassLongClicked(@NonNull final String className, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_class_dialog_title)
                .setMessage(R.string.delete_class_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClassAdapter.removeClassName(index);
                        mDatabase.deleteClass(className);
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

    // endregion class view holder

    private void createClass() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.create_class_dialog_title)
                .setMessage(R.string.create_class_dialog_message)
                .setView(editText)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String className = editText.getText().toString();
                        if (!TextUtils.isEmpty(className.trim())) {
                            //// TODO: create Class object and save to database 
                            StudyClass studyClass = new StudyClass(className);
                            long errorCode = mDatabase.addClass(studyClass);
                            if (errorCode != -1) {
                                mClassAdapter.addClassName(editText.getText().toString());
                                return; // successfully exited the method
                            }
                        }
                        // return not called, means got error
                        Toast.makeText(getContext(), R.string.create_class_failure_message, Toast.LENGTH_SHORT).show();
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
}
