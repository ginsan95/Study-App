package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;

import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class ChoiceQuestion extends Question {

    private List<String> mChoices;

    public ChoiceQuestion(String question, String answer, double totalMarks, List<String> choices)
    {
        super(question, answer, totalMarks);
        mChoices = choices;
    }

    //for database
    public ChoiceQuestion(int questionId, String question, String answer, double totalMarks, List<String> choices)
    {
        super(questionId, question, answer, totalMarks);
        mChoices = choices;
    }

    // region get set
    public List<String> getChoices()
    {
        return mChoices;
    }

    public void setChoices(List<String> choices)
    {
        mChoices = choices;
    }
    // endregion get set

    // region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeStringList(mChoices);
    }

    public static final Creator<ChoiceQuestion> CREATOR = new Creator<ChoiceQuestion>() {
        public ChoiceQuestion createFromParcel(Parcel in) {
            return new ChoiceQuestion(in);
        }

        public ChoiceQuestion[] newArray(int size) {
            return new ChoiceQuestion[size];
        }
    };

    private ChoiceQuestion(Parcel in) {
        super(in);
        mChoices = in.createStringArrayList();
    }
    // endregion Parcelable
}
