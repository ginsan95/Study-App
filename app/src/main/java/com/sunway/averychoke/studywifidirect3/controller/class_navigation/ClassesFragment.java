package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.databinding.DataBindingUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.class_navigation.search.SearchClassFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.TeacherClassActivity;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassesBinding;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Question;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;

/**
 * Created by AveryChoke on 22/1/2017.
 */

public class ClassesFragment extends SWDBaseFragment implements
        ClassesNameAdapter.ClassViewHolder.OnClassSelectListener {

    private DatabaseHelper mDatabase;
    private ClassesNameAdapter mAdapter;

    private FragmentClassesBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DatabaseHelper(getContext());
        mAdapter = new ClassesNameAdapter(this);

        // set the id counter for the model objects
        ClassMaterial.mCounter = mDatabase.getClassMaterialMaxId();
        Question.mCounter = mDatabase.getQuestionMaxId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classes, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Classes");
        setHasOptionsMenu(true);

        mBinding.classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.classesRecyclerView.setAdapter(mAdapter);

        mBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });

        // initialize data
        mAdapter.setClassesName(mDatabase.getAllClassesName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_classes_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_classes:
                getBaseActivity().changeFragment(SearchClassFragment.newInstance(null));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // region class view holder
    @Override
    public void onClassSelected(final String className) {
        final CharSequence[] choices = new CharSequence[] {
                getString(R.string.option_host_class),
                getString(R.string.option_participate_class)
        };

        new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Host class
                                TeacherManager.getInstance().initialize(className, getContext());
                                Intent teacherIntent = new Intent(getActivity(), TeacherClassActivity.class);
                                startActivity(teacherIntent);
                                break;
                            case 1: // Participate class
                                getBaseActivity().changeFragment(SearchClassFragment.newInstance(className));
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onClassLongClicked(@NonNull final String className, @NonNull final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_class_dialog_title)
                .setMessage(R.string.delete_class_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeClassName(index);
                        mDatabase.deleteClass(className);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    // endregion class view holder

    private void createClass() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.create_class_dialog_title)
                .setMessage(R.string.create_class_dialog_message)
                .setView(editText)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String className = editText.getText().toString();
                        if (!TextUtils.isEmpty(className.trim())) {
                            StudyClass studyClass = new StudyClass(className);
                            long errorCode = mDatabase.addClass(studyClass);
                            if (errorCode != -1) {
                                mAdapter.addClassName(className);
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

                    }
                })
                .show();
    }
}