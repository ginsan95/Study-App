package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.class_navigation.ClassFragment;
import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.databinding.FragmentClassDetailsBinding;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class TeacherClassFragment extends Fragment {
    private static final String CLASS_NAME_KEY = "classNameKey";

    private StudyClass mStudyClass;
    private DatabaseHelper mDatabase;

    private FragmentClassDetailsBinding mBinding;
    private ClassPagerFragmentAdapter mClassPagerFragmentAdapter;

    public static final TeacherClassFragment newInstance(String className) {
        TeacherClassFragment teacherClassFragment = new TeacherClassFragment();
        Bundle args = new Bundle();
        args.putString(CLASS_NAME_KEY, className);

        teacherClassFragment.setArguments(args);
        return teacherClassFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String className = getArguments().getString(CLASS_NAME_KEY);

        mDatabase = new DatabaseHelper(getContext());
        mStudyClass = mDatabase.getClass(className);

        mClassPagerFragmentAdapter = new ClassPagerFragmentAdapter(getChildFragmentManager());

        getActivity().setTitle(mStudyClass.getName() + " (Teacher)");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_details, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.classTabLayout.setupWithViewPager(mBinding.classViewPager);
        mBinding.classViewPager.setAdapter(mClassPagerFragmentAdapter);
    }

    // adapter for the view pager
    protected static class ClassPagerFragmentAdapter extends FragmentStatePagerAdapter {
        private String[] tabTitles = { "Quiz", "Study Material" };

        private ClassPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return new TeacherQuizFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
