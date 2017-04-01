package com.sunway.averychoke.studywifidirect3.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.view.Menu;
import android.view.MenuItem;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.class_navigation.ClassFragment;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainBinding;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

public class MainActivity extends SWDBaseActivity implements FragmentManager.OnBackStackChangedListener {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        // display Class Fragment
        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new ClassFragment(), ClassFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
