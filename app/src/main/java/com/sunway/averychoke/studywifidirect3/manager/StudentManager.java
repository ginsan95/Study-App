package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class StudentManager {

    private static final StudentManager sInstance = new StudentManager();

    private DatabaseHelper mDatabase;
    private StudyClass mStudyClass;

    private StudentManager() {}

    public static StudentManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context) {
        mDatabase = new DatabaseHelper(context);

        StudyClass studyClass = mDatabase.getClass(className);
        mStudyClass = studyClass != null ? studyClass : new StudyClass(className);
    }

    public String getClassName() {
        return mStudyClass != null ? mStudyClass.getName() : "";
    }

    // region Quiz
    public List<Quiz> getQuizzes() {
        return mStudyClass != null ?  mStudyClass.getQuizzes() : new ArrayList<Quiz>();
    }

    public void updateQuizAnswer(Quiz quiz) {
        // update manager data
        if (mStudyClass != null) {
            int index = mStudyClass.getQuizzes().indexOf(quiz);
            mStudyClass.getQuizzes().set(index, quiz);
        }

        // update local database
        if (mDatabase != null) {
            mDatabase.updateQuizAnswers(quiz);
        }
    }
    // endregion

    // region Study Material
    public List<StudyMaterial> getStudyMaterials() {
        return mStudyClass != null ? mStudyClass.getStudyMaterials() : new ArrayList<StudyMaterial>();
    }
    // endregion

    // region Get Set

    // endregion
}
