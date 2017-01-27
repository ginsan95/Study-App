package com.sunway.averychoke.studywifidirect3.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 27/1/2017.
 */

public class Class {

    private final String mName;
    private List<Quiz> mQuizzes;
    private List<StudyMaterial> mStudyMaterials;

    public Class(String name) {
        mName = name;
        mQuizzes = new ArrayList<>();
        mStudyMaterials = new ArrayList<>();
    }

    // region get set
    public String getName() {
        return mName;
    }

    public List<Quiz> getQuizzes() {
        return mQuizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        mQuizzes = quizzes;
    }

    public List<StudyMaterial> getStudyMaterials() {
        return mStudyMaterials;
    }

    public void setStudyMaterials(List<StudyMaterial> studyMaterials) {
        mStudyMaterials = studyMaterials;
    }
    // endregion get set
}
