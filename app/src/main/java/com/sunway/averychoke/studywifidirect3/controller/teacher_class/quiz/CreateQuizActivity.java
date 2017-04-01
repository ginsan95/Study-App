package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityCreateQuizBinding;

/**
 * Created by AveryChoke on 5/2/2017.
 */

public class CreateQuizActivity extends SWDBaseActivity {

    private ActivityCreateQuizBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_quiz);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // display Create Quiz Fragment
        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new CreateQuizFragment(), CreateQuizFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.containerLayout.getId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.unsaved_changes_message)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateQuizActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }
}
