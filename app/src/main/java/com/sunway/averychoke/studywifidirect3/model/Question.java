package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Question implements Parcelable, Serializable {

    public static long mCounter = 0;

    private final long mQuestionId;
    private String mQuestion;
    private String mAnswer;
    private double mTotalMarks;

    public Question(String question, String answer, double totalMarks)
    {
        mQuestionId = ++mCounter;
        mQuestion = question;
        mAnswer = answer;
        mTotalMarks = totalMarks;
    }

    //for database
    public Question(long questionId, String question, String answer, double totalMarks)
    {
        mQuestionId = questionId;
        mQuestion = question;
        mAnswer = answer;
        mTotalMarks = totalMarks;
    }

    // for create question
    public Question() {
        this("", "", 1);
    }

    // convert to question
    public static Question cloneFrom(Question question) {
        Question cloneQuestion = new Question(
                question.getQuestionId(),
                question.getQuestion(),
                "",
                question.getTotalMarks());
        return cloneQuestion;
    }

    public boolean checkAnswer(String userAnswer)
    {
        return mAnswer.equalsIgnoreCase(userAnswer);
    }

    // region get set
    public long getQuestionId()
    {
        return mQuestionId;
    }

    public String getQuestion()
    {
        return mQuestion;
    }

    public void setQuestion(String question)
    {
        mQuestion = question;
    }

    public String getAnswer()
    {
        return mAnswer;
    }

    public void setAnswer(String answer)
    {
        mAnswer = answer;
    }

    public double getTotalMarks()
    {
        return mTotalMarks;
    }

    public void setTotalMarks(double totalMarks)
    {
        mTotalMarks = totalMarks;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mQuestionId);
        out.writeString(mQuestion);
        out.writeString(mAnswer);
        out.writeDouble(mTotalMarks);
    }


    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    protected Question(Parcel in) {
        mQuestionId = in.readLong();
        mQuestion = in.readString();
        mAnswer = in.readString();
        mTotalMarks = in.readDouble();
    }
    // endregion Parcelable
}
