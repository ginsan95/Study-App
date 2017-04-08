package com.sunway.averychoke.studywifidirect3.controller;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by AveryChoke on 5/2/2017.
 */

public abstract class SWDBaseActivity extends AppCompatActivity {

    public static final String APP_ID = "swd_study_app";
    public static final int APP_PORT_NUMBER = 6767;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract void changeFragment(Fragment fragment);
}
