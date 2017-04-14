package com.sunway.averychoke.studywifidirect3.controller.student_class.quiz.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.common_class.quiz.QuestionsAdapter;
import com.sunway.averychoke.studywifidirect3.databinding.CellSubmitBinding;

/**
 * Created by AveryChoke on 3/4/2017.
 */

public class AnswerQuestionsAdapter extends QuestionsAdapter {
    private static final int QUESTION_VH = 101;
    private static final int SUBMIT_VH = 102;

    private SubmitViewHolder.OnSubmitListener mListener;

    public AnswerQuestionsAdapter(SubmitViewHolder.OnSubmitListener listener) {
        super();
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (super.getItemCount() > 0 && position == super.getItemCount()) {
            return SUBMIT_VH;
        } else {
            return QUESTION_VH;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == QUESTION_VH) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_submit, parent, false);
            return new SubmitViewHolder(rootView, mListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == QUESTION_VH) {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        // +1 include the submit button
        return super.getItemCount() > 0 ? super.getItemCount() + 1 : 0;
    }

    // region Submit view holder
    public static class SubmitViewHolder extends RecyclerView.ViewHolder {
        public interface OnSubmitListener {
            void onSubmit();
        }

        private CellSubmitBinding mBinding;

        public SubmitViewHolder(View itemView, final OnSubmitListener listener) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);

            mBinding.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSubmit();
                }
            });
        }
    }
    // endregion
}
