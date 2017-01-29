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

import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassBinding;
import com.sunway.averychoke.studywifidirect3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AveryChoke on 22/1/2017.
 */

public class ClassFragment extends Fragment implements
        ClassAdapter.ClassViewHolder.OnClassSelectListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ClassAdapter mClassAdapter;

    private FragmentClassBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassAdapter = new ClassAdapter(this);
        List<String> strings = new ArrayList<>();
        strings.add("ali");
        strings.add("abu");
        strings.add("cacing");
        strings.add("dada");
        mClassAdapter.setClassesName(strings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        mBinding.classesSwipeRefreshLayout.setOnRefreshListener(this);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.classesRecyclerView.setAdapter(mClassAdapter);

        mBinding.plusButton.setOnClickListener(new View.OnClickListener() {
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
        List<String> classesName = new ArrayList<>();
        Random rand = new Random();
        for (int i=0; i<50; i++) {
            classesName.add(""+rand.nextInt(100));
        }
        mClassAdapter.setClassesName(classesName);

        mBinding.classesSwipeRefreshLayout.setRefreshing(false);
    }
    // endregion swipe refresh layout

    // region class view holder
    @Override
    public void onClassSelected(String className) {
        final CharSequence[] choices = new CharSequence[] {"Host Class", "Participate Class", "View/Edit Class"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), choices[which], Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }

    @Override
    public void onClassLongClicked(@NonNull String className, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_class_dialog_title)
                .setMessage(R.string.delete_class_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClassAdapter.removeClassName(index);
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
                            
                            mClassAdapter.addClassName(editText.getText().toString());
                        } else {
                            Toast.makeText(getContext(), R.string.create_class_failure_message, Toast.LENGTH_SHORT).show();
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
}
