package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class TeacherManager {

    private static final TeacherManager sInstance = new TeacherManager();

    private DatabaseHelper mDatabase;
    private StudyClass mStudyClass;
    private Map<String, Quiz> mQuizMap;
    private Map<String, StudyMaterial> mStudyMaterialMap;

    private TeacherManager() {
        mQuizMap = new HashMap<>();
        mStudyMaterialMap = new HashMap<>();
    }

    public static TeacherManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context) {
        mDatabase = new DatabaseHelper(context);

        StudyClass studyClass = mDatabase.getClass(className);
        mStudyClass = studyClass != null ? studyClass : new StudyClass(className);

        // change the list into map
        for (Quiz quiz : mStudyClass.getQuizzes()) {
            mQuizMap.put(quiz.getName(), quiz);
        }
        for (StudyMaterial studyMaterial : mStudyClass.getStudyMaterials()) {
            mStudyMaterialMap.put(studyMaterial.getName(), studyMaterial);
        }
    }

    public String getClassName() {
        return mStudyClass != null ? mStudyClass.getName() : "";
    }

    // region Quiz
    public List<Quiz> getQuizzes() {
        return mStudyClass != null ?  mStudyClass.getQuizzes() : new ArrayList<Quiz>();
    }

    public Quiz findQuiz(String name) {
        return mQuizMap.get(name);
    }

    public boolean addQuiz(Quiz quiz) {
        if (mDatabase == null || mStudyClass == null
                // save the quiz to database
                || mDatabase.addQuiz(quiz, mStudyClass.getName()) == -1) {
            return false;
        }

        mStudyClass.getQuizzes().add(quiz);
        mQuizMap.put(quiz.getName(), quiz);

        return true;
    }
    // endregion

    // region Study Material
    public List<StudyMaterial> getStudyMaterials() {
        return mStudyClass != null ? mStudyClass.getStudyMaterials() : new ArrayList<StudyMaterial>();
    }

    public StudyMaterial findStudyMaterial(String name) {
        return mStudyMaterialMap.get(name);
    }
    // endregion

    // region Get Set

    // endregion
}
