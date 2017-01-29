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

    public static long mCounter = 0;

    private final long mQuizId;
    private List<Question> mQuestions;
    private double mMarks;

    public Quiz(String name)
    {
        super(name, false);
        mQuizId = ++mCounter;
        mQuestions = new ArrayList<>();
        mMarks = 0.0;
    }

    //for database
    public Quiz(long quizId, String name, List<Question> questions, double marks, boolean visible)
    {
        super(name, visible);
        mQuizId = quizId;
        mQuestions = questions;
        mMarks = marks;
    }

    public void addQuestion(Question question)
    {
        mQuestions.add(question);
    }

    // region get set
    public long getQuizId()
    {
        return mQuizId;
    }

    public List<Question> getQuestions()
    {
        return mQuestions;
    }

    public void setQuestions(List<Question> questions)
    {
        mQuestions = questions;
    }

    public double getMarks()
    {
        return mMarks;
    }

    public void setMarks(double marks)
    {
        mMarks = marks;
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

        out.writeLong(mQuizId);
        out.writeList(mQuestions);
        out.writeDouble(mMarks);
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
        super(in.readString(), in.readInt()==1);
        mQuizId = in.readLong();
        mQuestions = in.readArrayList(Question.class.getClassLoader());
        mMarks = in.readDouble();
    }
    // endregion Parcelable
}
