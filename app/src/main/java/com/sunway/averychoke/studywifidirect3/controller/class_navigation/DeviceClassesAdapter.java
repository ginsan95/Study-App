package com.sunway.averychoke.studywifidirect3.controller.class_navigation;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.databinding.CellClassBinding;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class DeviceClassesAdapter extends RecyclerView.Adapter<DeviceClassesAdapter.DeviceClassViewHolder> {

    private List<DeviceClass> mDeviceClasses;
    private DeviceClassViewHolder.OnDeviceClassSelectListener mListener;

    public DeviceClassesAdapter(DeviceClassViewHolder.OnDeviceClassSelectListener listener) {
        mDeviceClasses = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public DeviceClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_class, parent, false);
        DeviceClassViewHolder viewHolder = new DeviceClassViewHolder(rootView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceClassViewHolder holder, int position) {
        DeviceClass deviceClass = null;
        deviceClass = mDeviceClasses.get(position);
        if (deviceClass != null) {
            holder.setDeviceClass(deviceClass);
        }
    }

    @Override
    public int getItemCount() {
        return mDeviceClasses.size();
    }

    public void setDeviceClasses(List<DeviceClass> deviceClasses) {
        mDeviceClasses.clear();
        mDeviceClasses.addAll(deviceClasses);
        Collections.sort(mDeviceClasses);
        notifyDataSetChanged();
    }

    public void addOrReplaceDeviceClass(DeviceClass deviceClass) {
        DeviceClass sameDeviceClass = null;
        if (deviceClass.getDevice() != null) {
            sameDeviceClass = getSameDeviceClass(deviceClass.getClassName());
        }

        if (sameDeviceClass != null) {
            sameDeviceClass.setDevice(deviceClass.getDevice());
            notifyItemChanged(mDeviceClasses.indexOf(sameDeviceClass));
        } else {
            mDeviceClasses.add(deviceClass);
            Collections.sort(mDeviceClasses);
            notifyItemInserted(mDeviceClasses.indexOf(deviceClass));
        }
    }

    public void removeClassName(int index) {
        mDeviceClasses.remove(index);
        notifyItemRemoved(index);
    }

    private DeviceClass getSameDeviceClass(String className) {
        for (DeviceClass deviceClass : mDeviceClasses) {
            if (deviceClass.getClassName().equalsIgnoreCase(className) && !deviceClass.isTeacherHosting()) {
                return deviceClass;
            }
        }
        return null;
    }

    // region View Holder
    static class DeviceClassViewHolder extends RecyclerView.ViewHolder {

        public interface OnDeviceClassSelectListener {
            void onDeviceClassSelected(@NonNull DeviceClass deviceClass);
            void onDeviceClassLongClicked(@NonNull DeviceClass deviceClass, @NonNull int index);
        }

        private CellClassBinding mBinding;
        private DeviceClass mDeviceClass;

        public DeviceClassViewHolder(View itemView, final OnDeviceClassSelectListener listener) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeviceClass != null) {
                        listener.onDeviceClassSelected(mDeviceClass);
                    }
                }
            });
            mBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mDeviceClass != null) {
                        listener.onDeviceClassLongClicked(mDeviceClass, getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        private void setDeviceClass(DeviceClass deviceClass) {
            mDeviceClass = deviceClass;

            mBinding.classNameTextView.setText(deviceClass.toString());
            mBinding.classNameTextView.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    deviceClass.isTeacherHosting() ? R.color.text_enable_color : R.color.text_color));
        }
    }
    // endregion
}
