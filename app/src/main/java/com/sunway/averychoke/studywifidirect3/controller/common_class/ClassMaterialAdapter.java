package com.sunway.averychoke.studywifidirect3.controller.common_class;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class ClassMaterialAdapter extends RecyclerView.Adapter<ClassMaterialViewHolder> {

    private ClassMaterialViewHolder.OnClassMaterialSelectListener mListener;

    private final List<ClassMaterial> mClassMaterials = new ArrayList<>();
    private final boolean mIsTeacher;

    public ClassMaterialAdapter(boolean isTeacher, ClassMaterialViewHolder.OnClassMaterialSelectListener listener) {
        mIsTeacher = isTeacher;
        mListener = listener;
    }

    @Override
    public ClassMaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_class_material, parent, false);
        ClassMaterialViewHolder classMaterialViewHolder = new ClassMaterialViewHolder(rootView, mIsTeacher, mListener);
        return classMaterialViewHolder;
    }

    @Override
    public void onBindViewHolder(ClassMaterialViewHolder holder, int position) {
        ClassMaterial classMaterial = null;
        classMaterial = mClassMaterials.get(position);
        if (classMaterial != null) {
            holder.setClassMaterial(classMaterial);
        }
    }

    @Override
    public int getItemCount() {
        return mClassMaterials.size();
    }

    public void setClassMaterials(List<? extends ClassMaterial> classMaterials) {
        mClassMaterials.clear();
        mClassMaterials.addAll(classMaterials);
        // sort

        notifyDataSetChanged();
    }

    public void addClassMaterial(ClassMaterial classMaterial) {
        mClassMaterials.add(classMaterial);
        notifyItemInserted(mClassMaterials.size()-1);
    }

    public void replaceClassMaterial(ClassMaterial classMaterial) {
        int index = mClassMaterials.indexOf(classMaterial);
        if (index >= 0) {
            mClassMaterials.set(index, classMaterial);
            notifyItemChanged(index);
        } else {
            mClassMaterials.add(classMaterial);
            notifyItemInserted(mClassMaterials.size() - 1);
        }
    }

    public void addClassMaterials(List<? extends ClassMaterial> classMaterials) {
        int startIndex = mClassMaterials.size();

        mClassMaterials.addAll(classMaterials);

        notifyItemRangeInserted(startIndex, classMaterials.size());
    }

    public void removeClassMaterial(int index) {
        mClassMaterials.remove(index);
        notifyItemRemoved(index);
    }

    // region Get Set
    protected ClassMaterialViewHolder.OnClassMaterialSelectListener getListener() {
        return mListener;
    }
    // endregion
}
