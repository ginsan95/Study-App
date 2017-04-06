package com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainContainerBinding;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

/**
 * Created by AveryChoke on 6/4/2017.
 */

public class ViewQuizActivity extends SWDBaseActivity {

    public static final String ARGS_QUIZ_KEY = "args_quiz_key";
    private ActivityMainContainerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_container);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Quiz quiz = getIntent().getParcelableExtra(ARGS_QUIZ_KEY);
        if (quiz != null) {
            setTitle(quiz.getName());
            getSupportFragmentManager().beginTransaction()
                    .add(mBinding.containerLayout.getId(), ViewQuizFragment.newInstance(quiz), ViewQuizFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.containerLayout.getId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
