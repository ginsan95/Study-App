package com.sunway.averychoke.studywifidirect3.model;

import java.io.Serializable;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class ClassMaterial implements Serializable {
    public enum Status {
        NORMAL, DOWNLOADING, CONFLICT;
    }

    public static long mCounter = 0;

    private long mId;
    private String mName;
    private boolean mVisible;
    private Status mStatus;

    public ClassMaterial(String name, boolean visible) {
        this(++mCounter, name, visible, Status.NORMAL);
    }

    public ClassMaterial(long id, String name, boolean visible) {
        this(id, name, visible, Status.NORMAL);
    }

    public ClassMaterial(long id, String name, boolean visible, Status status) {
        mId = id;
        mName = name;
        mVisible = visible;
        mStatus = status;
    }

    public void updateId() {
        mId = ++mCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassMaterial)) {
            return false;
        }
        ClassMaterial classMaterial = (ClassMaterial) o;
        return classMaterial.mId == mId;
    }

    // region get set
    public long getId() {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }
    // endregion get set
}
