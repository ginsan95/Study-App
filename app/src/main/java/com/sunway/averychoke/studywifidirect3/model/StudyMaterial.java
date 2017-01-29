package com.sunway.averychoke.studywifidirect3.model;

import java.io.File;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyMaterial extends ClassMaterial{

    public static long mCounter = 0;

    private final long mStudyMaterialId;

    private final File mFile;

    public StudyMaterial(String name, String path) {
        super(name, true);
        mStudyMaterialId = ++mCounter;
        mFile = new File(path);
    }

    // for database
    public StudyMaterial(long studyMaterialId, String name, String path, boolean visible) {
        super(name, visible);
        mStudyMaterialId = studyMaterialId;
        mFile = new File(path);
    }

    // region get set
    public long getStudyMaterialId()
    {
        return mStudyMaterialId;
    }

    public File getFile() {
        return mFile;
    }
    // endregion get set
}
