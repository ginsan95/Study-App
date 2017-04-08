package com.sunway.averychoke.studywifidirect3.controller.student_class.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialAdapter;
import com.sunway.averychoke.studywifidirect3.controller.class_details.ClassMaterialViewHolder;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

/**
 * Created by AveryChoke on 6/4/2017.
 */

public class StudentQuizzesAdapter extends ClassMaterialAdapter {

    public StudentQuizzesAdapter(StudentQuizViewHolder.OnCheckSelectListener listener) {
        super(false, listener);
    }

    @Override
    public ClassMaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getListener() instanceof StudentQuizViewHolder.OnCheckSelectListener) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_class_material, parent, false);
            return new StudentQuizViewHolder(rootView, (StudentQuizViewHolder.OnCheckSelectListener) getListener());
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    public static class StudentQuizViewHolder extends ClassMaterialViewHolder {

        public interface OnCheckSelectListener extends OnClassMaterialSelectListener  {
           void onCheckLongClicked(@NonNull Quiz quiz, @NonNull int index);
        }

        public StudentQuizViewHolder(View itemView, final OnCheckSelectListener listener) {
            super(itemView, false, listener);

            getBinding().checkButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getClassMaterial() != null && getClassMaterial() instanceof Quiz) {
                        listener.onCheckLongClicked((Quiz) getClassMaterial(), getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        @Override
        protected void setClassMaterial(ClassMaterial classMaterial) {
            super.setClassMaterial(classMaterial);

            if (classMaterial instanceof Quiz) {
                getBinding().checkButton.setVisibility(((Quiz)classMaterial).isAnswered() ? View.VISIBLE : View.GONE);
            }
        }
    }
}
