package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassMaterialBinding;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.util.Random;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherQuizFragment extends Fragment implements
        ClassMaterialViewHolder.OnClassMaterialSelectListener,
        SwipeRefreshLayout.OnRefreshListener{

    private DatabaseHelper mDatabase;
    private ClassMaterialAdapter mClassMaterialAdapter;

    private FragmentClassMaterialBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DatabaseHelper(getContext());
        mClassMaterialAdapter = new ClassMaterialAdapter(true, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_material, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        mBinding.materialsSwipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.materialsRecyclerView.setAdapter(mClassMaterialAdapter);
    }

    @Override
    public void onRefresh() {
        mBinding.materialsSwipeRefreshLayout.setRefreshing(true);

        String[] strings = {"babi", "cina", "sohai"};
        Random rand = new Random();
        Quiz quiz = new Quiz(strings[rand.nextInt(3)]);
        mClassMaterialAdapter.addClassMaterial(quiz);

        mBinding.materialsSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClassMaterialSelected(@NonNull ClassMaterial classMaterial) {
        Quiz quiz = (Quiz) classMaterial;
        Toast.makeText(getContext(), quiz.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClassMaterialLongClicked(@NonNull ClassMaterial classMaterial, @NonNull final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_class_dialog_title)
                .setMessage(R.string.delete_class_dialog_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClassMaterialAdapter.removeClassMaterial(index);
                        //mDatabase.deleteClass(className);
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
        classMaterial.setVisible(isChecked);
    }
}
