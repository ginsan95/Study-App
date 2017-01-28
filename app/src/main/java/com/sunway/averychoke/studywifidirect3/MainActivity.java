package com.sunway.averychoke.studywifidirect3;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;

import com.sunway.averychoke.studywifidirect3.controller.class_navigation.ClassFragment;
import com.sunway.averychoke.studywifidirect3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // display Class Fragment
        getSupportFragmentManager().beginTransaction()
                .add(mBinding.containerLayout.getId(), new ClassFragment(), ClassFragment.class.getSimpleName())
                .commit();
    }

    public void changeFragment(Fragment fragment, String simpleName) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.containerLayout.getId(), fragment, simpleName)
                .addToBackStack(simpleName)
                .commit();
    }
}
