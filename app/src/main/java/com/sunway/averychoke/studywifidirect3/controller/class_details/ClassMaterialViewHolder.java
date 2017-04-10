package com.sunway.averychoke.studywifidirect3.controller.class_details;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.databinding.CellClassMaterialBinding;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class ClassMaterialViewHolder extends RecyclerView.ViewHolder {

    public interface OnClassMaterialSelectListener {
        void onClassMaterialSelected(@NonNull ClassMaterial classMaterial);
        void onClassMaterialLongClicked(@NonNull ClassMaterial classMaterial, @NonNull int index);
        void onClassMaterialChecked(@NonNull ClassMaterial classMaterial, @NonNull boolean isChecked);
    }

    private CellClassMaterialBinding mBinding;

    private ClassMaterial mClassMaterial;

    public ClassMaterialViewHolder(View itemView, boolean isTeacher, final OnClassMaterialSelectListener listener) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);

        if (isTeacher) {
            mBinding.visibleCheckBox.setVisibility(View.VISIBLE);
        } else {
            mBinding.visibleCheckBox.setVisibility(View.GONE);
        }

        mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClassMaterial != null) {
                    listener.onClassMaterialSelected(mClassMaterial);
                }
            }
        });
        mBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mClassMaterial != null) {
                    listener.onClassMaterialLongClicked(mClassMaterial, getAdapterPosition());
                }
                return true;
            }
        });
        mBinding.visibleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mClassMaterial != null) {
                    listener.onClassMaterialChecked(mClassMaterial, isChecked);
                }
            }
        });
    }

    protected void setClassMaterial(ClassMaterial classMaterial) {
        mClassMaterial = classMaterial;

        mBinding.nameTextView.setText(classMaterial.getName());
        mBinding.visibleCheckBox.setChecked(classMaterial.isVisible());

        switch (classMaterial.getStatus()) {
            case NORMAL:
                mBinding.nameTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_color));
                break;
            case DOWNLOADING:
                mBinding.nameTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_downloading_color));
                break;
            case CONFLICT:
                mBinding.nameTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_conflict_color));
                break;
        }
    }

    // region get set\
    protected CellClassMaterialBinding getBinding() {
        return mBinding;
    }

    protected ClassMaterial getClassMaterial() {
        return mClassMaterial;
    }
    // endregion
}
