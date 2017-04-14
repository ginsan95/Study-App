package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyMaterial extends ClassMaterial implements Parcelable {
    private File mFile;

    // for reading name from teacher
    public StudyMaterial(String name) {
        super(name, false);
        mFile = new File("");
        setStatus(Status.PENDING);
    }

    public StudyMaterial(String name, String path) {
        super(name, false);
        mFile = new File(path);
        if (!mFile.exists()) {
            setStatus(Status.ERROR);
        }
    }

    // for database
    public StudyMaterial(long studyMaterialId, String name, String path, boolean visible) {
        super(studyMaterialId, name, visible);
        mFile = new File(path);
        if (!mFile.exists()) {
            setStatus(Status.ERROR);
        }
    }

    public void update(StudyMaterial studyMaterial) {
        mFile = studyMaterial.mFile;
        setStatus(Status.NORMAL);
    }

    // region get set
    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // write for superclass first
        out.writeLong(getId());
        out.writeString(getName());
        out.writeInt(isVisible()? 1: 0); // cannot write as boolean so change to int instead
        out.writeSerializable(getStatus());

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
        super(in.readLong(), in.readString(), in.readInt()==1, (Status)in.readSerializable());
        mFile = new File(in.readString());
    }
    // endregion Parcelable
}
