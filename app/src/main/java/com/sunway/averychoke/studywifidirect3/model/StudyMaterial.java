package com.sunway.averychoke.studywifidirect3.model;

import java.io.File;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyMaterial {

    public static int mCounter = 0;

    private final int mStudyMaterialId;
    private String mName;
    private boolean mVisible;

    private File mFile;

    public StudyMaterial(String name, String path) {
        mStudyMaterialId = ++mCounter;
        mName = name;
        mFile = new File(path);
        mVisible = false;
    }

    // for database
    public StudyMaterial(int studyMaterialId, String name, String path, boolean visible) {
        mStudyMaterialId = studyMaterialId;
        mName = name;
        mFile = new File(path);
        mVisible = visible;
    }

    // region get set
    public int getStudyMaterialId()
    {
        return mStudyMaterialId;
    }

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
