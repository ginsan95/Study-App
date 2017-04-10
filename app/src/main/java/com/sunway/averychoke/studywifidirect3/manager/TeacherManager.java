package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
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

public class TeacherManager extends BaseManager {

    private static final TeacherManager sInstance = new TeacherManager();

    private Map<String, Quiz> mQuizMap;
    private Map<String, StudyMaterial> mStudyMaterialMap;

    private TeacherManager() {
        super();
        mQuizMap = new HashMap<>();
        mStudyMaterialMap = new HashMap<>();
    }

    public static TeacherManager getInstance() {
        return sInstance;
    }

    @Override
    public void initialize(String className, Context context) {
        super.initialize(className, context);

        // change the list into map
        for (Quiz quiz : getStudyClass().getQuizzes()) {
            mQuizMap.put(quiz.getName().toLowerCase(), quiz);
        }
        for (StudyMaterial studyMaterial : getStudyClass().getStudyMaterials()) {
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
        }
    }

    // region Quiz
    public List<Quiz> getVisibleQuizzes() {
        List<Quiz> visibleQuizzes = new ArrayList<>();
        for (Quiz quiz : getQuizzes()) {
            if (quiz.isVisible()) {
                visibleQuizzes.add(quiz);
            }
        }
        return visibleQuizzes;
    }

    public Quiz findQuiz(String name) {
        Quiz quiz = mQuizMap.get(name.toLowerCase());
        if (quiz != null && quiz.isVisible()) {
            return quiz;
        } else {
            return null;
        }
    }

    public boolean addQuiz(Quiz quiz) {
        if (getDatabase() == null || getStudyClass() == null
                // save the quiz to database
                || getDatabase().addQuiz(quiz, getStudyClass().getName()) == -1) {
            return false;
        }

        getStudyClass().getQuizzes().add(quiz);
        mQuizMap.put(quiz.getName().toLowerCase(), quiz);

        return true;
    }

    public boolean updateQuiz(Quiz quiz, String oldName) {
        quiz.setVersion(quiz.getVersion() + 1);
        if (getDatabase() == null || getStudyClass() == null
                // update the quiz in database
                || getDatabase().updateQuiz(quiz) == -1) {
            return false;
        }

        int index = getStudyClass().getQuizzes().indexOf(quiz);
        getStudyClass().getQuizzes().set(index, quiz);

        mQuizMap.remove(oldName);
        mQuizMap.put(quiz.getName().toLowerCase(), quiz);

        return true;
    }

    public void updateQuizVisible(Quiz quiz) {
        if (getStudyClass() != null && getDatabase() != null) {
            int index = getStudyClass().getQuizzes().indexOf(quiz);
            getStudyClass().getQuizzes().set(index, quiz);
            mQuizMap.remove(quiz.getName());
            mQuizMap.put(quiz.getName().toLowerCase(), quiz);
            getDatabase().updateClassMaterialVisible(quiz);
        }
    }

    @Override
    public void deleteQuiz(Quiz quiz) {
        super.deleteQuiz(quiz);
        mQuizMap.remove(quiz.getName().toLowerCase());
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
    public StudyMaterial findStudyMaterial(String name) {
        return mStudyMaterialMap.get(name);
    }

    public void updateStudyMaterialVisible(StudyMaterial studyMaterial) {
        if (getStudyClass() != null && getDatabase() != null) {
            int index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
            getStudyClass().getStudyMaterials().set(index, studyMaterial);
            mStudyMaterialMap.remove(studyMaterial.getName());
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
            getDatabase().updateClassMaterialVisible(studyMaterial);
        }
    }

    @Override
    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        super.deleteStudyMaterial(studyMaterial);
        mStudyMaterialMap.remove(studyMaterial.getName().toLowerCase());
    }
    // endregion

    // region Get Set

    // endregion
}
