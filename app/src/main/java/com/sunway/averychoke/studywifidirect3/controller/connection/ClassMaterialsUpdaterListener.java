package com.sunway.averychoke.studywifidirect3.controller.connection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;

import java.util.List;

/**
 * Created by AveryChoke on 9/4/2017.
 */

public interface ClassMaterialsUpdaterListener {

    void onClassMaterialUpdated(ClassMaterial classMaterial);

    void onClassMaterialsUpdated();

    void onError(@Nullable Exception e);
}
