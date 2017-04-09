package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class StudyMaterial extends ClassMaterial implements Parcelable {
    private final File mFile;

    public StudyMaterial(String name, String path) {
        super(name, true);
        mFile = new File(path);
    }

    // for database
    public StudyMaterial(long studyMaterialId, String name, String path, boolean visible) {
        super(studyMaterialId, name, visible);
        mFile = new File(path);
    }

    // region get set
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
