package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyMaterial extends ClassMaterial implements Parcelable {

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

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // write for superclass first
        out.writeString(super.getName());
        out.writeInt(super.getVisible()? 1: 0); // cannot write as boolean so change to int instead

        out.writeLong(mStudyMaterialId);
        out.writeString(mFile.getPath());
    }

    public static final Parcelable.Creator<StudyMaterial> CREATOR = new Parcelable.Creator<StudyMaterial>() {
        public StudyMaterial createFromParcel(Parcel in) {
            return new StudyMaterial(in);
        }

        public StudyMaterial[] newArray(int size) {
            return new StudyMaterial[size];
        }
    };

    private StudyMaterial(Parcel in) {
        super(in.readString(), in.readInt()==1);
        mStudyMaterialId = in.readLong();
        mFile = new File(in.readString());
    }
    // endregion Parcelable
}
