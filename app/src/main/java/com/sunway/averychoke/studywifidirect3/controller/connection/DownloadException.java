package com.sunway.averychoke.studywifidirect3.controller.connection;

import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;

/**
 * Created by AveryChoke on 10/4/2017.
 */

public class DownloadException extends Exception {

    private ClassMaterial mClassMaterial;
    private ClassMaterial.Status mInitialStatus;

    public DownloadException(ClassMaterial classMaterial, ClassMaterial.Status initialStatus) {
        super();
        mClassMaterial = classMaterial;
        mInitialStatus = initialStatus;
    }

    @Override
    public String toString() {
        return "Failed to download " + mClassMaterial.getName();
    }

    // region Get Set
    public ClassMaterial getClassMaterial() {
        return mClassMaterial;
    }

    public void setClassMaterial(ClassMaterial classMaterial) {
        mClassMaterial = classMaterial;
    }

    public ClassMaterial.Status getInitialStatus() {
        return mInitialStatus;
    }

    public void setInitialStatus(ClassMaterial.Status initialStatus) {
        mInitialStatus = initialStatus;
    }
    // endregion
}
