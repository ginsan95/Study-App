package com.sunway.averychoke.studywifidirect3.model;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class ClassMaterial {
    public static long mCounter = 0;

    private final long mId;
    private String mName;
    private boolean mVisible;

    public ClassMaterial(String name, boolean visible) {
        mId = ++mCounter;
        mName = name;
        mVisible = visible;
    }

    public ClassMaterial(long id, String name, boolean visible) {
        mId = id;
        mName = name;
        mVisible = visible;
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
    // endregion get set
}
