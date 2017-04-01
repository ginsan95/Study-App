package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.controller.student_class.StudentClassFragment;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityClassBinding;

/**
 * Created by AveryChoke on 1/4/2017.
 */

public class StudentClassActivity extends SWDBaseActivity {

    public static final String CLASS_NAME_KEY = "class_name_key";

    private ActivityClassBinding mBinding;

    private String mClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_class);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mClassName = getIntent().getStringExtra(CLASS_NAME_KEY);

        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), StudentClassFragment.newInstance(mClassName), StudentClassFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.containerLayout.getId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
