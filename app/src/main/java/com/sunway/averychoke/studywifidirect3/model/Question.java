package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Question implements Parcelable, Serializable {

    public static long mCounter = 0;

    private long mId;
    private String mQuestion;
    private String mCorrectAnswer;
    private int mTotalMarks;
    private String mUserAnswer;

    public Question(String question, String correctAnswer, int totalMarks) {
        this(++mCounter, question, correctAnswer, totalMarks, "");
    }

    //for database
    public Question(long id, String question, String correctAnswer, int totalMarks, String userAnswer) {
        mId = id;
        mQuestion = question;
        mCorrectAnswer = correctAnswer;
        mTotalMarks = totalMarks;
        mUserAnswer = userAnswer;
    }

    // for create question
    public Question() {
        this("", "", 1);
    }

    // convert to question
    public static Question cloneFrom(Question question) {
        Question cloneQuestion = new Question(
                question.getId(),
                question.getQuestion(),
                question.getCorrectAnswer(),
                question.getTotalMarks(),
                "");
        return cloneQuestion;
    }

    public boolean checkAnswer() {
        return checkAnswer(mUserAnswer);
    }

    public boolean checkAnswer(String userAnswer) {
        return mCorrectAnswer.equalsIgnoreCase(userAnswer);
    }

    public void resetId() {
        mId = ++mCounter;
        mUserAnswer = "";
    }

    // region get set
    public long getId()
    {
        return mId;
    }

    public String getQuestion()
    {
        return mQuestion;
    }

    public void setQuestion(String question)
    {
        mQuestion = question;
    }

    public String getCorrectAnswer()
    {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer)
    {
        mCorrectAnswer = correctAnswer;
    }

    public int getTotalMarks()
    {
        return mTotalMarks;
    }

    public void setTotalMarks(int totalMarks)
    {
        mTotalMarks = totalMarks;
    }

    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mQuestion);
        out.writeString(mCorrectAnswer);
        out.writeInt(mTotalMarks);
        out.writeString(mUserAnswer);
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
        mId = in.readLong();
        mQuestion = in.readString();
        mCorrectAnswer = in.readString();
        mTotalMarks = in.readInt();
        mUserAnswer = in.readString();
    }
    // endregion Parcelable
}
