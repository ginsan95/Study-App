package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainContainerBinding;

/**
 * Created by AveryChoke on 1/4/2017.
 */

public class TeacherClassActivity extends SWDBaseActivity {

    private ActivityMainContainerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_container);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new TeacherClassFragment(), TeacherClassFragment.class.getSimpleName())
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