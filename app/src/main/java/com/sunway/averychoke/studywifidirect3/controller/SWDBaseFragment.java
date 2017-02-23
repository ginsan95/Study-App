package com.sunway.averychoke.studywifidirect3.controller;

import android.support.v4.app.Fragment;

/**
 * Created by AveryChoke on 20/2/2017.
 */

public class SWDBaseFragment extends Fragment {

    public SWDBaseActivity getBaseActivity() {
        return (SWDBaseActivity) getActivity();
    }

}
