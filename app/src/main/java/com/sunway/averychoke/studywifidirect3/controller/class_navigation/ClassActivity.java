package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.databinding.DataBindingUtil;

import com.sunway.averychoke.studywifidirect3.databinding.ActivityClassBinding;
import com.sunway.averychoke.studywifidirect3.R;

/**
 * Created by AveryChoke on 22/1/2017.
 */

public class ClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_class);
        binding.createClassButton.setText("Fuck your mom");
        //setContentView(R.layout.activity_class);
        //Button createClassBtn =
    }
}
