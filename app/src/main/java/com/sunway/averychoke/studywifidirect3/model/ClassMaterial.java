package com.sunway.averychoke.studywifidirect3.model;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public abstract class ClassMaterial {
    private String mName;
    private boolean mVisible;

    public ClassMaterial(String name, boolean visible) {
        mName = name;
        mVisible = visible;
    }

    // region get set
    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public boolean getVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }
    // endregion get set
}
