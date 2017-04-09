package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Quiz extends ClassMaterial implements Parcelable, Serializable {
    private List<Question> mQuestions;
    private boolean mAnswered;
    private int mVersion;

    public Quiz(String name) {
        super(name, false);
        mQuestions = new ArrayList<>();
        mAnswered = false;
        mVersion = 1;
    }

    //for database
    public Quiz(long quizId, String name, List<Question> questions, boolean answered, int version, boolean visible) {
        super(quizId, name, visible);
        mAnswered = answered;
        mQuestions = questions;
        mVersion = version;
    }

    @Override
    public void updateId() {
        super.updateId();
        updateQuestionsId();
    }

    public void updateQuestionsId() {
        for (Question question : mQuestions) {
            question.updateId();
        }
    }

    // region get set
    public List<Question> getQuestions()
    {
        return mQuestions;
    }

    public void setQuestions(List<Question> questions) {
        mQuestions = questions;
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
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
        out.writeInt(isVisible() ? 1 : 0); // cannot write as boolean so change to int instead
        out.writeSerializable(getStatus());

        out.writeList(mQuestions);
        out.writeInt(mAnswered ? 1 : 0);
        out.writeInt(mVersion);
    }

    public static final Parcelable.Creator<Quiz> CREATOR = new Parcelable.Creator<Quiz>() {
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    private Quiz(Parcel in) {
        super(in.readLong(), in.readString(), in.readInt() != 0, (Status)in.readSerializable());
        mQuestions = in.readArrayList(Question.class.getClassLoader());
        mAnswered = in.readInt() != 0;
        mVersion = in.readInt();
    }
    // endregion Parcelable
}
