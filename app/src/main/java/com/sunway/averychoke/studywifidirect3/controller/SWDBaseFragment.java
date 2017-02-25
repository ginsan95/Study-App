package com.sunway.averychoke.studywifidirect3.controller;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;

import com.sunway.averychoke.studywifidirect3.R;

/**
 * Created by AveryChoke on 20/2/2017.
 */

public class SWDBaseFragment extends Fragment {

    public SWDBaseActivity getBaseActivity() {
        return (SWDBaseActivity) getActivity();
    }

    //edit text fault tolerance
    public boolean handleEmptyET(EditText et) {
        String str = et.getText().toString();
        if (TextUtils.isEmpty(str.trim())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                et.setError(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.edit_text_error_message) + "</font>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                et.setError(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.edit_text_error_message) + "</font>"));
            }
            return false;
        } else {
            return true;
        }
    }
}
