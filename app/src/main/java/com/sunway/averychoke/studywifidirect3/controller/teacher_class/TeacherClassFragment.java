package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.SWDBaseFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.quiz.TeacherQuizFragment;
import com.sunway.averychoke.studywifidirect3.controller.teacher_class.study_material.TeacherStudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassBinding;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherClassFragment extends SWDBaseFragment {
    public interface OnTeacherRestartListener {
        void onTeacherRestart();
    }

    private TeacherManager sManager;

    private FragmentClassBinding mBinding;
    private ClassPagerFragmentAdapter mClassPagerFragmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = TeacherManager.getInstance();
        mClassPagerFragmentAdapter = new ClassPagerFragmentAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        getActivity().setTitle("(T) " + sManager.getClassName());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        ((TabLayout) getActivity().findViewById(R.id.class_tab_layout)).setupWithViewPager(mBinding.classViewPager);
        mBinding.classViewPager.setAdapter(mClassPagerFragmentAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                if (getActivity() instanceof OnTeacherRestartListener) {
                    ((OnTeacherRestartListener) getActivity()).onTeacherRestart();
                }
                return true;
            case R.id.class_info:
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                String message = "";
                if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                    message += getString(R.string.dialog_class_info_teacher_offline_message, sManager.getClassName());
                } else {
                    message = getString(R.string.dialog_class_info_teacher_online_message, sManager.getClassName());
                }
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_class_info_title)
                        .setMessage(message)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // adapter for the view pager
    class ClassPagerFragmentAdapter extends FragmentStatePagerAdapter {
        private String[] tabTitles = { getString(R.string.tab_quiz), getString(R.string.tab_study_material) };

        private ClassPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TeacherQuizFragment();
                default:
                    return new TeacherStudyMaterialFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
