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
    private boolean mAnswered;

    public Quiz(String name) {
        super(name, false);
        mQuizId = ++mCounter;
        mQuestions = new ArrayList<>();
        mAnswered = false;
    }

    //for database
    public Quiz(long quizId, String name, List<Question> questions, boolean answered, boolean visible) {
        super(name, visible);
        mQuizId = quizId;
        mAnswered = answered;
        mQuestions = questions;
    }

    public void addQuestion(Question question)
    {
        mQuestions.add(question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quiz)) {
            return false;
        }
        Quiz quiz = (Quiz) o;
        return quiz.mQuizId == mQuizId;
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

    public void setQuestions(List<Question> questions) {
        mQuestions = questions;
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // write for superclass first
        out.writeString(getName());
        out.writeInt(getVisible() ? 1 : 0); // cannot write as boolean so change to int instead

        out.writeLong(mQuizId);
        out.writeList(mQuestions);
        out.writeInt(mAnswered ? 1 : 0);
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
        super(in.readString(), in.readInt() != 0);
        mQuizId = in.readLong();
        mQuestions = in.readArrayList(Question.class.getClassLoader());
        mAnswered = in.readInt() != 0;
    }
    // endregion Parcelable
}
