package com.sunway.averychoke.studywifidirect3.model;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class ChoiceQuestion extends Question implements Serializable {

    private List<String> mChoices;

    //for database
    public ChoiceQuestion(long questionId, String question, String correctAnswer, int totalMarks, String userAnswer, List<String> choices) {
        super(questionId, question, correctAnswer, totalMarks, userAnswer);
        mChoices = choices;
    }

    // convert to choice question
    public static ChoiceQuestion cloneFrom(Question question) {
        ChoiceQuestion cloneQuestion = new ChoiceQuestion(
                question.getId(),
                question.getQuestion(),
                "",
                question.getTotalMarks(),
                "",
                new ArrayList<String>());
        return cloneQuestion;
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
