package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;
import android.support.annotation.Nullable;

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
            mQuizMap.put(quiz.getName().toLowerCase(), quiz);
        }
        for (StudyMaterial studyMaterial : mStudyClass.getStudyMaterials()) {
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
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
        return mQuizMap.get(name.toLowerCase());
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

    public boolean updateQuiz(Quiz quiz, String oldName) {
        if (mDatabase == null || mStudyClass == null
                // update the quiz in database
                || mDatabase.updateQuiz(quiz) == -1) {
            return false;
        }

        quiz.setVersion(quiz.getVersion() + 1);
        int index = mStudyClass.getQuizzes().indexOf(quiz);
        mStudyClass.getQuizzes().set(index, quiz);

        mQuizMap.remove(oldName);
        mQuizMap.put(quiz.getName(), quiz);

        return true;
    }

    public void deleteQuiz(Quiz quiz) {
        if (mDatabase != null && mStudyClass != null) {
            mDatabase.deleteClassMaterial(quiz);
            mStudyClass.getQuizzes().remove(quiz);
            mQuizMap.remove(quiz.getName().toLowerCase());
        }
    }

    public boolean isQuizNameConflicting(String newName, @Nullable String oldName) {
        if (oldName != null) {
            return !newName.equalsIgnoreCase(oldName) && mQuizMap.containsKey(newName.toLowerCase());
        } else {
            return mQuizMap.containsKey(newName.toLowerCase());
        }
    }
    // endregion

    // region Study Material
    public List<StudyMaterial> getStudyMaterials() {
        return mStudyClass != null ? mStudyClass.getStudyMaterials() : new ArrayList<StudyMaterial>();
    }

    public StudyMaterial findStudyMaterial(String name) {
        return mStudyMaterialMap.get(name);
    }

    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        if (mDatabase != null && mStudyClass != null) {
            mDatabase.deleteClassMaterial(studyMaterial);
            mStudyClass.getStudyMaterials().remove(studyMaterial);
            mStudyMaterialMap.remove(studyMaterial.getName().toLowerCase());
        }
    }
    // endregion

    // region Get Set

    // endregion
}
