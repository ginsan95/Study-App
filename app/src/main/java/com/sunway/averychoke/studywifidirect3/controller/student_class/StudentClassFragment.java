package com.sunway.averychoke.studywifidirect3.controller.student_class;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.StudentQuizFragment;
import com.sunway.averychoke.studywifidirect3.controller.student_class.study_material.StudentStudyMaterialFragment;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassDetailsBinding;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class StudentClassFragment extends SWDBaseFragment {
    private StudentManager sManager;
    private DatabaseHelper mDatabase;

    private FragmentClassDetailsBinding mBinding;
    private ClassPagerFragmentAdapter mClassPagerFragmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sManager = StudentManager.getInstance();
        mDatabase = new DatabaseHelper(getContext());

        mClassPagerFragmentAdapter = new ClassPagerFragmentAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_details, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        getActivity().setTitle("(S) " + sManager.getClassName());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mBinding.classTabLayout.setupWithViewPager(mBinding.classViewPager);
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
            case R.id.class_info:
                String message  = "";
                if (!sManager.isOffline()) {
                    message = getString(R.string.dialog_class_info_student_online_message, sManager.getClassName(), sManager.getDeviceClass().getDevice().deviceName);
                } else {
                    message = getString(R.string.dialog_class_info_student_offline_message, sManager.getClassName());
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
                    return new StudentQuizFragment();
                default:
                    return new StudentStudyMaterialFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
