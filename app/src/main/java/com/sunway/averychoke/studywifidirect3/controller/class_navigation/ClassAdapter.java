package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.databinding.CellClassBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private ClassViewHolder.OnClassSelectListener mListener;

    private final List<String> mClassesName = new ArrayList<>();

    public ClassAdapter(ClassViewHolder.OnClassSelectListener listener) {
        mListener = listener;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_class, parent, false);
        ClassViewHolder classViewHolder = new ClassViewHolder(rootView, mListener);
        return classViewHolder;
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        String className = null;
        className = mClassesName.get(position);
        if (className != null) {
            holder.setString(className);
        }
    }

    @Override
    public int getItemCount() {
        return mClassesName.size();
    }

    public void setClassesName(List<String> classesName) {
        mClassesName.clear();
        mClassesName.addAll(classesName);

        notifyDataSetChanged();
    }

    // region view holder class
    static class ClassViewHolder extends RecyclerView.ViewHolder {

        public interface OnClassSelectListener {
            void onClassSelected(@NonNull String className);
        }

        private String mClassName;

        private CellClassBinding mViewBinding;

        public ClassViewHolder(View itemView, final OnClassSelectListener listener) {
            super(itemView);

            mViewBinding = DataBindingUtil.bind(itemView);

            mViewBinding.classNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClassName != null) {
                        listener.onClassSelected(mClassName);
                    }
                }
            });
        }

        private void setString(String className) {
            mClassName = className;

            mViewBinding.classNameTextView.setText(mClassName);
        }
    }
    // endregion view holder class
}
