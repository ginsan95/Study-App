package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Quiz implements Parcelable, Serializable {

    public static int mCounter = 0;

    private final int mQuizId;
    private String mTitle;
    private List<Question> mQuestions;
    private double mMarks;
    private boolean mVisible;

    public Quiz(String title)
    {
        mQuizId = ++mCounter;
        mTitle = title;
        mQuestions = new ArrayList<>();
        mMarks = 0.0;
        mVisible = false;
    }

    //for database
    public Quiz(int quizId, String title, List<Question> questions, double marks, boolean visible)
    {
        mQuizId = quizId;
        mTitle = title;
        mQuestions = new ArrayList<>();
        mMarks = marks;
        mVisible = visible;
    }

    public void addQuestion(Question question)
    {
        mQuestions.add(question);
    }

    @Override
    public String toString()
    {
        return String.format("%s (%.2f)", mTitle, mMarks);
    }

    // region get set
    public int getQuizId()
    {
        return mQuizId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
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

    public boolean getVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mQuizId);
        out.writeString(mTitle);
        out.writeList(mQuestions);
        out.writeDouble(mMarks);
        out.writeInt(mVisible? 1: 0); // cannot write as boolean so change to int instead
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
        mQuizId = in.readInt();
        mTitle = in.readString();
        mQuestions = in.readArrayList(Question.class.getClassLoader());
        mMarks = in.readDouble();
        mVisible = in.readInt() == 1;
    }
    // endregion Parcelable
}
